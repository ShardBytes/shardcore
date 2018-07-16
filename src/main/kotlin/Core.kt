import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import io.javalin.embeddedserver.jetty.EmbeddedJettyFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import rest.FruitRest
import rest.RandomRest
import java.io.File

class CoreServer {
	
	val mongo = Mongo().apply { println("==== MONGO INITIALIZED ====") }
	
	val app = Javalin.create()
			.embeddedServer(EmbeddedJettyFactory {
				Server().apply {
					
					val ports = File("ports.txt").readLines()
					
					// setup jetty
					val sslConnector = ServerConnector(this, getSslContextFactory()).apply {
						port = ports[1].toInt()
					}
					
					val httpConnector = ServerConnector(this).apply {
						port = ports[0].toInt()
					}
					
					connectors = arrayOf(sslConnector, httpConnector)
					
				}
			})
			.enableStaticFiles("static", Location.EXTERNAL)
			.apply {
				println("==== JAVALIN INITIALIZED ====")
			}
	
	init {
		
		// redirect address to https
		app.before {
			println("@[${it.request().method}] <${it.request().remoteAddr}> ${it.url()}")
			
			if (it.port() == 80) { // disallow port 80 requests
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
		sslContextFactory.keyStorePath = "keystore.jks"
		sslContextFactory.setKeyStorePassword("kysfaggot")
		return sslContextFactory
	}
	
}

fun main(args: Array<String>) {
	
	CoreServer()
	
}