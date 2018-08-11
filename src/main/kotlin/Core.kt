/** ShardCore Core class for ShardCore server by ShardBytes
 *
 *  (c) ShardBytes
 *
 */

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.messenger4j.Messenger
import io.javalin.ApiBuilder.path
import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import io.javalin.embeddedserver.jetty.EmbeddedJettyFactory
import io.javalin.translator.template.JavalinThymeleafPlugin
import logintest.UserCore
import logintest.UserRestPost
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.thymeleaf.TemplateEngine
import rest.FruitRest
import rest.RandomRest
import shardbot.ShardBotHandler
import templates.IndexTemplate
import websocket.RootEchoWS
import java.io.File

data class CoreConfig(
		// ports
		val port: Int,
		val sslPort: Int,
		// keystore
		val keystorePath: String,
		val keystorePassword: String,
		// coreMongo
		val mongoHost: String,
		val mongoUserName: String,
		val mongoPassword: String,
		// thyme
		val cacheActive: Boolean,
		val cacheResetKey: String
)

data class MessengerConfig(
		val PAGE_ACCESS_TOKEN: String,
		val APP_SECRET: String,
		val VERIFY_TOKEN: String
)

class CoreServer(private val config: CoreConfig,
                 messengerConfig: MessengerConfig) {
	
	val coreMongo: CoreMongo
	val coreMessenger: Messenger
	val app: Javalin
	val redirectServer: Javalin
	val thymeleaf: TemplateEngine
	
	val userCore: UserCore
	
	init {
		
		thymeleaf = ThymeleafFileTemplateEngine(config.cacheActive)
		
		// setup Javalin
		app = Javalin.create().apply {
			
			// setup jetty with SSL
			embeddedServer(EmbeddedJettyFactory {
				Server().apply {
					
					// https connector
					val sslConnector = ServerConnector(this, getSslContextFactory()).apply {
						port = config.sslPort
					}
					
					connectors = arrayOf(sslConnector)
					
				}
			})
			
			// setup thymeleaf plugin
			JavalinThymeleafPlugin.configure(thymeleaf)
			
			enableStaticFiles("static", Location.EXTERNAL)
			
			start()
			println("==== Core app started ! ====")
			
		}
		
		// setup separate redirect server
		redirectServer = Javalin.create().disableStartupBanner().port(config.port).apply {
			// redirect to https
			before {
				if (it.port() == 80 || it.port() == config.port) { // disallow port non ssl port requests
					println("<REDIRECT> Redirecting ${it.request().remoteAddr} to https ...")
					it.redirect(it.url().replace("http://", "https://"), 301) // 301 status->moved permanently
				}
			}
			
			start()
			println("<REDIRECT> === Redirect server started. ===")
		}
		
		// setup coreMongo
		coreMongo = CoreMongo(config.mongoHost, config.mongoUserName, config.mongoPassword)
		
		// setup user core
		userCore = UserCore(coreMongo)
		
		// setup messenger
		coreMessenger = Messenger.create(messengerConfig.PAGE_ACCESS_TOKEN, messengerConfig.APP_SECRET, messengerConfig.VERIFY_TOKEN)
		
		// setup routes
		// last routed = first checked
		app.apply {
			
			// watch requests
			before {
				println("@[${it.request().method}] <${it.request().remoteAddr}> <${it.port()}> ${it.url()}")
			}
			
			path("/") {
				
				// core
				get("", IndexTemplate())
				
				get("log") {
					it.result(File("logs/teelog.txt").run { if (exists()) readText() else "No log file."})
				}
				
				// messenger bot
				// verify webhook
				get("shardbot/webhook") {
					if (it.queryParam("hub.verify_token") == messengerConfig.VERIFY_TOKEN) {
						it.result(it.queryParam("hub.challenge")?:"")
					}
				}
				// handle webhook
				post("shardbot/webhook", ShardBotHandler(coreMessenger))
				
				//get("resetCache/:key", CacheResetREST(thymeleaf, config.cacheResetKey))
				
				get("random", RandomRest())
				get("fruit", FruitRest(coreMongo))
				
				// login test
				post("logintest/:command", UserRestPost(userCore))
				
				get("greet/:name/:age") {
					it.html("Hello, my name is ${it.param("name")} and I got ${it.param("age")} yrs.")
				}
				
				ws("", RootEchoWS())
				
			}
			
		}
		
		// ERRORS
		app.error(404) {
			it.html(File("static/notFound.html").readText())
			println("@[ERROR] [${it.status()}] ${it.path()}")
		}
		
		println("===== CORE ROUTING COMPLETE, LET THE ACTION BEGIN ! xD =====")
		
	}
	
	private fun getSslContextFactory(): SslContextFactory = SslContextFactory().apply {
		keyStorePath = config.keystorePath
		setKeyStorePassword(config.keystorePassword)
	}
	
}

fun main(args: Array<String>) {
	
	var configPath = "config.json"
	
	if (args.size > 0) {
		configPath = args[0]
	} else {
		println("< no config file argument specified, using default config.json >")
	}
	
	val config: CoreConfig
	
	try {
		val configJson = File(configPath).readText()
		config = jacksonObjectMapper().readValue(configJson, CoreConfig::class.java)
	}
	catch (ex: Exception) {
		println("======= FAILED TO LOAD CONFIG =========")
		ex.printStackTrace()
		println("=======================================")
		println("exiting ...")
		System.exit(-1)
		return
	}
	
	var messengerConfig: MessengerConfig
	
	try {
		messengerConfig = jacksonObjectMapper().readValue(File("messenger.json"), MessengerConfig::class.java)
	} catch (ex: Exception) {
		println("FAILED TO LOAD MESSENGER CONFIG !")
		messengerConfig = MessengerConfig("", "", "")
	}
	
	CoreServer(config, messengerConfig)
	
}