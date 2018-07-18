import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.ApiBuilder.*
import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import io.javalin.embeddedserver.jetty.EmbeddedJettyFactory
import io.javalin.translator.template.JavalinThymeleafPlugin
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import rest.FruitRest
import rest.RandomRest
import templates.IndexTemplate
import websocket.RootEchoWS
import java.io.File

data class CoreConfig(
		// devmode
		val devMode: Boolean,
		// ports
		val port: Int,
		val sslPort: Int,
		// keystore
		val keystorePath: String,
		val keystorePassword: String,
		// mongo
		val mongoHost: String,
		val mongoUserName: String,
		val mongoPassword: String
)

class CoreServer(private val config: CoreConfig) {
	
	val mongo: Mongo
	val app: Javalin
	
	init {
		
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
			
			// static files
			enableStaticFiles("static", Location.EXTERNAL)
			
			// setup thymeleaf plugin with CUSTOM TEPMPLATE ENGINE
			// also if in devmode, turn off cache
			JavalinThymeleafPlugin.configure(FileTemplateEngine(!config.devMode))
			
			start()
			println("==== JAVALIN STARTED ====")
			
		}
		
		// setup mongo
		mongo = Mongo(config.mongoHost, config.mongoUserName, config.mongoPassword)
		
		
		// setup routes
		app.routes {
			
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
				get("index.html", IndexTemplate())
				get("teelog") {
					it.result(File("logs/teelog.txt").run {
						if (exists()) readText() else "No teelog.txt"
					})
				}
				
				// apps
				get("random", RandomRest())
				get("fruit", FruitRest(mongo))
				ws("", RootEchoWS())
			}
			
		}
		
		// ERRORS
		app.error(404) {
			it.html(File("static/notFound.html").readText())
			println("@[ERROR] [${it.status()}] ${it.path()}")
		}
		
		println("===== ROUTING DONE =====")
		if (config.devMode) println(">>> DEVELOPMENT MODE ACTIVE <<<")
		
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