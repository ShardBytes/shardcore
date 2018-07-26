import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.ApiBuilder.path
import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import io.javalin.embeddedserver.jetty.EmbeddedJettyFactory
import io.javalin.translator.template.JavalinThymeleafPlugin
import logintest.UserCore
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.thymeleaf.TemplateEngine
import rest.FruitRest
import rest.RandomRest
import logintest.UserRestGet
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

class CoreServer(private val config: CoreConfig) {
	
	val coreMongo: CoreMongo
	val app: Javalin
	val thymeleaf: TemplateEngine
	
	val userCore: UserCore
	
	init {
		
		thymeleaf = ThymeleafFileTemplateEngine(config.cacheActive)
		
		// setup Javalin
		app = Javalin.create().apply {
			
			// setup jetty
			embeddedServer(EmbeddedJettyFactory {
				Server().apply {
					
					// https connector
					val sslConnector = ServerConnector(this, getSslContextFactory()).apply {
						port = config.sslPort
					}
					
					// http connector
					val httpConnector = ServerConnector(this).apply {
						port = config.port
					}
					
					connectors = arrayOf(sslConnector, httpConnector)
					
				}
			})
			
			// setup thymeleaf plugin
			JavalinThymeleafPlugin.configure(thymeleaf)
			
			enableStaticFiles("static", Location.EXTERNAL)
			
			start()
			println("==== JAVALIN STARTED ====")
			
		}
		
		// setup coreMongo
		coreMongo = CoreMongo(config.mongoHost, config.mongoUserName, config.mongoPassword)
		
		// setup user core
		userCore = UserCore(coreMongo)
		
		// setup routes
		// last routed = first checked
		app.apply {
			
			// redirect to https
			before {
				println("@[${it.request().method}] <${it.request().remoteAddr}> <${it.port()}> ${it.url()}")
				
				if (it.port() == 80 || it.port() == config.port) { // disallow port non ssl port requests
					println("[REDIRECT] Redirecting ${it.request().remoteAddr} to https ...")
					it.redirect(it.url().replace("http://", "https://"), 301) // 301 status->moved permanently
				}
			}
			
			
			path("/") {
				
				// core
				get("/", IndexTemplate())
				
				get("/log") {
					it.result(File("logs/teelog.txt").run { if (exists()) readText() else "No log file."})
				}
				
				//get("resetCache/:key", CacheResetREST(thymeleaf, config.cacheResetKey))
				
				get("random", RandomRest())
				get("fruit", FruitRest(coreMongo))
				get("/user/:name/:command", UserRestGet(userCore))
				
				get("/greet/:name/:age") {
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
		
		println("===== ROUTING DONE =====")
		
	}
	
	private fun getSslContextFactory(): SslContextFactory {
		val sslContextFactory = SslContextFactory()
		sslContextFactory.keyStorePath = config.keystorePath
		sslContextFactory.setKeyStorePassword(config.keystorePassword)
		return sslContextFactory
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
		return
	}
	
	CoreServer(config)
	
}