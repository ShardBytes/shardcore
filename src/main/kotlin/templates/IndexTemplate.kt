package templates

import io.javalin.Context
import io.javalin.Handler
import model.User
import org.thymeleaf.Thymeleaf
import routeStaticThymeleaf

class IndexTemplate : Handler {
	
	override fun handle(ctx: Context) {
		
		// uses file template engine
		ctx.renderThymeleaf("static/index", mapOf(
				"randomNumber" to Math.random(),
				"user" to User("Seb", 17)
		))
		
	}
	
}