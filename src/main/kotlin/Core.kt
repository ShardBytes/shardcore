import com.fasterxml.jackson.databind.util.JSONPObject
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import io.javalin.embeddedserver.jetty.EmbeddedJettyFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import rest.FruitRest
import rest.RandomRest
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
			.embeddedServer(EmbeddedJettyFactory {
				Server().apply {
					
					// setup jetty
					val sslConnector = ServerConnector(this, getSslContextFactory()).apply {
						port = config.sslPort
					}
					
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
		
		// redirect address to https
		app.before {
			println("@[${it.request().method}] <${it.request().remoteAddr}> ${it.url()}")
			
			if (it.port() == config.port) { // disallow port non ssl port requests
				println("[REDIRECT] Redirecting ${it.request().remoteAddr} to https ...")
				it.redirect(it.url().replace("http://", "https://"), 301) // 301 status->moved permanently
			}
		}
		
		// REST
		app.get("/random", RandomRest())
		app.get("/fruit", FruitRest(mongo))
		
		// websockets
		app.ws("/") {
			it.onConnect {
				println("${it.remoteAddress} connected")
				if (!it.isSecure) {
					println("${it.remoteAddress} unsecure !! closing ws")
					it.close()
					it.disconnect()
				} else {
					println("${it.remoteAddress} SECURE, keeping connection wss")
				}
			}
			
			it.onClose { sess, _, _ ->
				println("${sess.remoteAddress} closed")
			}
			
			it.onMessage { sess, msg ->
				sess.send(msg)
			}
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
	
	val config: CoreConfig
	
	try {
		
		val jsonMapper = jacksonObjectMapper()
		val configJson = File("config.json").readText()
		config = jsonMapper.readValue(configJson, CoreConfig::class.java)
		
	} catch (ex: Exception) {
		println("======= FAILED TO LOAD CONFIG =========")
		ex.printStackTrace()
		println("=======================================")
		println("exiting ...")
		return
	}
	
	CoreServer(config)
	
}