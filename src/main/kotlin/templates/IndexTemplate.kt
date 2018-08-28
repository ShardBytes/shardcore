package templates

import io.javalin.Context
import io.javalin.Handler

class IndexTemplate : Handler {
	
	override fun handle(ctx: Context) {
		
		// uses file template engine
		ctx.render("static/index.html", mapOf(
				"randomNumber" to Math.random()
		))
		
	}
	
}