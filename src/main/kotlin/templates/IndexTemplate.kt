package templates

import io.javalin.Context
import io.javalin.Handler
import model.User
import routeStaticThymeleaf

class IndexTemplate : Handler {
	
	override fun handle(ctx: Context) {
		ctx.routeStaticThymeleaf(mapOf(
				"randomNumber" to Math.random(),
				"user" to User("Seb", 17)
		))
	}
	
}