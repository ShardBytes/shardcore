package logintest

import io.javalin.Context
import io.javalin.Handler

class UserRestPost(val userCore: UserCore) : Handler {
	override fun handle(ctx: Context) {
	
		val auth = ctx.basicAuthCredentials()
		val command = ctx.param("command") ?: ""
		
		if (auth == null) { // if no auth -> no username
			ctx.result("@ERROR:NO_AUTH")
			return // autocast nonnullable
		}
		
		val user = userCore.getUser(auth.username)
		
		if (user == null) {
			ctx.result("@ERROR:NO_USER")
			return // autocast nonnullable
		}
		
		if (command == "salt") {
			ctx.result(user.passwordSalt)
			return
		}
		
		if (auth.password == user.passwordHash) {
			userCore.processCommand(ctx, user, command)
		} else {
			ctx.result("@ERROR:WRONG_HASH")
			return
		}
		
	}
}