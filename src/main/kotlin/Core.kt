import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import io.javalin.embeddedserver.jetty.EmbeddedJettyFactory
import org.eclipse.jetty.server.*
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.security.Constraint
import org.eclipse.jetty.security.ConstraintSecurityHandler
import org.eclipse.jetty.security.ConstraintMapping






class CoreServer {

	val app = Javalin.create()
			.embeddedServer(EmbeddedJettyFactory {
				Server().apply {
					val sslConnector = ServerConnector(this, getSslContextFactory()).apply {
						port = 443
					}

					val httpConnector = ServerConnector(this).apply {
						port = 80
					}

					connectors = arrayOf(sslConnector, httpConnector)

				}
			})
			.enableStaticFiles("static", Location.EXTERNAL)

	init {

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

	val server = CoreServer()

}