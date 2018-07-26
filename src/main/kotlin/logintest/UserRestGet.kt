package logintest

import io.javalin.Context
import io.javalin.Handler

class UserRestGet(val userCore: UserCore) : Handler {
	
	override fun handle(ctx: Context) {

		val user = userCore.getUser(ctx.param("name"))
		
		if (user == null) {
			ctx.result("@ERROR:NO_USER")
			return
		}
		
		when (ctx.param("command")) {
		
			"salt" -> ctx.result(user.passwordSalt)
			
			else -> ctx.result("@ERROR:UNKNOWN_COMMAND")
		
		}

	}
}