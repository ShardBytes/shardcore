package templates

import io.javalin.Context
import io.javalin.Handler

class IndexTemplate : Handler {
	
	override fun handle(ctx: Context) {
		
		// uses file template engine
		ctx.renderThymeleaf("static/index", mapOf(
				"randomNumber" to Math.random()
		))
		
	}
	
}