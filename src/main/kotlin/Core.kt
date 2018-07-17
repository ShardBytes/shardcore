import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
	
	val app = Javalin.create()
			// setup jetty
			.embeddedServer(EmbeddedJettyFactory {
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
			.enableStaticFiles("static", Location.EXTERNAL)
			.apply {
				println("==== JAVALIN INITIALIZED ====")
			}
	
	val mongo: Mongo
	
	init {
		
		// setup mongo
		mongo = Mongo(config.mongoHost, config.mongoUserName, config.mongoPassword)
		
		// setup thymeleaf plugin with CUSTOM TEPMPLATE ENGINE
		JavalinThymeleafPlugin.configure(FileTemplateEngine())
		
		
		// redirect address to https
		app.before {
			println("@[${it.request().method}] <${it.request().remoteAddr}> <${it.port()}> ${it.url()}")
			
			if (it.port() == 80 || it.port() == config.port) { // disallow port non ssl port requests
				println("[REDIRECT] Redirecting ${it.request().remoteAddr} to https ...")
				it.redirect(it.url().replace("http://", "https://"), 301) // 301 status->moved permanently
			}
		}
		
		
		// TEMPLATES
		app.get("/index.html", IndexTemplate())
		
		
		// REST
		app.get("/random", RandomRest())
		app.get("/fruit", FruitRest(mongo))
		
		// WEBSOCKETS
		app.ws("/", RootEchoWS())
		
		// ERRORS
		app.error(404) {
			it.html(File("static/notFound.html").readText())
		}
		
		app.start()
		
		println("===== JAVALIN STARTED =====")
		
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