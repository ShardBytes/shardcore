import io.javalin.Javalin
import io.javalin.embeddedserver.Location

class CoreServer {

	val app = Javalin.create()
			.port(80)
			.enableStaticFiles("static", Location.EXTERNAL)

	init {

		app.start()

	}



}

fun main(args: Array<String>) {

	val server = CoreServer()

}