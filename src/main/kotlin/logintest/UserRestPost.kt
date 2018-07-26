package logintest

import io.javalin.Context
import io.javalin.Handler

class UserRestPost(val userCore: UserCore) : Handler {
	override fun handle(ctx: Context) {
		
		val user = userCore.getUser(ctx.param("name"))
		
		if (user == null) {
			ctx.result("@ERROR:NO_USER")
			return
		}
		
		when (ctx.param("command")) {
			
			"login" -> {
				if (ctx.body() == user.passwordHash) {
					ctx.result("OK LOGIN")
				} else {
					ctx.result("ERROR WRONG HASH")
				}
			}
			
		}
		
	}
}