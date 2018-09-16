/** ShardCore Core class for ShardCore server by ShardBytes
 *
 *  (c) ShardBytes
 *
 */

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.messenger4j.Messenger
import discord.Shardy
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.rendering.JavalinRenderer
import io.javalin.rendering.template.JavalinThymeleaf
import io.javalin.staticfiles.Location
import logintest.UserCore
import logintest.UserRestPost
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
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

class CoreServer(private val config: CoreConfig,
                 messengerConfig: MessengerConfig,
                 discordConfig: DiscordConfig) {
	
	val coreMongo: CoreMongo
	val coreMessenger: Messenger
	var shardy: Shardy? = null
	
	val app: Javalin
	val redirectServer: Javalin
	
	val userCore: UserCore
	
	init {
		
		// setup Javalin app
		app = Javalin.create().apply {
			
			// setup jetty with SSL
			server {
				Server().apply {
					
					// https connector
					val sslConnector = ServerConnector(this, getSslContextFactory()).apply {
						port = config.sslPort
					}
					
					connectors = arrayOf(sslConnector)
					
				}
			}
			
			// setup thymeleaf plugin
			JavalinThymeleaf.configure(ThymeleafFileTemplateEngine(config.cacheActive))
			JavalinRenderer.register(JavalinThymeleaf)
			
			enableStaticFiles("static", Location.EXTERNAL)
			
			start()
			println("==== Core app started ! ====")
			
		}
		
		// setup separate redirect server
		redirectServer = Javalin.create().disableStartupBanner().port(config.port).apply {
			// redirect to https
			before {
				if (it.port() == 80 || it.port() == config.port) { // disallow port non ssl port requests
					println("<REDIRECT> Redirecting ${it.req.remoteAddr} to https ...")
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
		println("[MESSENGER] Setting up messenger")
		coreMessenger = Messenger.create(messengerConfig.PAGE_ACCESS_TOKEN, messengerConfig.APP_SECRET, messengerConfig.VERIFY_TOKEN)
		println("[MESSENGER] DONE")
		
		// discord
		startShardy(discordConfig)
		
		// setup routes
		// last routed = first checked
		app.apply {
			
			// watch requests
			before {
				println("@[${it.req.method}] <${it.req.remoteAddr}> <${it.port()}> ${it.url()}")
			}
			
			path("/") {
				
				// only when using thymeleaf, otherwise leave to static
				// get("", IndexTemplate())
				
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
				
				// discord
				get("shardy/:cmd") { when(it.pathParam("cmd")) {
					"start" -> {
						startShardy(discordConfig)
						it.result("started Shardy")
					}
					"stop" -> {
						stopShardy()
						it.result("stopped Shardy")
					}
				}}
				
				// normal debug
				get("random", RandomRest())
				get("fruit", FruitRest(coreMongo))
				
				// login test
				post("logintest/:command", UserRestPost(userCore))
				
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
	
	
	private fun startShardy(config: DiscordConfig) {
		// setup discord
		println("[DISCORD] Starting bot Shardy")
		
		if (shardy == null) {
			try {
				shardy = Shardy(JDABuilder(AccountType.BOT).setToken(config.token).build().awaitReady())
				println("[DISCORD] Started !")
			} catch (ex: Exception) {
				println("[DISCORD] ERROR -> problems starting shardy ! -> ${ex.message}")
			}
		} else {
			println("[DISCORD] Error starting shardy, already running")
		}
		
	}
	
	private fun stopShardy() {
		println("[DISCORD] Stopping Shardy")
		shardy?.shutdown()
		shardy = null
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
		messengerConfig = jacksonObjectMapper().readValue(File("messenger.json"))
	} catch (ex: Exception) {
		println("[ ERROR ] -> FAILED TO LOAD MESSENGER CONFIG !")
		messengerConfig = MessengerConfig("", "", "")
	}
	
	var discordConfig: DiscordConfig
	try {
		discordConfig = jacksonObjectMapper().readValue(File("discord.json"))
	} catch (ex: Exception) {
		println("[ ERROR ] -> FAILED TO LOAD DISCORD CONFIG !")
		discordConfig = DiscordConfig("")
	}
	
	println("<----- STARTING CORE ----->")
	CoreServer(config, messengerConfig, discordConfig)
	
}