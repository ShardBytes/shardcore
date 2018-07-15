import data.RandomREST
import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import io.javalin.embeddedserver.jetty.EmbeddedJettyFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory

class CoreServer {

	val app = Javalin.create()
			.embeddedServer(EmbeddedJettyFactory {
				Server().apply {

					// setup jetty
					val sslConnector = ServerConnector(this, getSslContextFactory()).apply {
						port = 443
					}

					val httpConnector = ServerConnector(this).apply {
						port = 8080
					}

					connectors = arrayOf(sslConnector, httpConnector)

				}
			})
			.enableStaticFiles("static", Location.EXTERNAL)

	init {

		// redirect address to https
		app.before {
			if (it.port() == 80) { // disallow port 80 requests
				it.redirect(it.url().replace("http://", "https://"), 301) // 301 status->moved permanently
			}
		}

		// REST
		app.get("/random", RandomREST(app))

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