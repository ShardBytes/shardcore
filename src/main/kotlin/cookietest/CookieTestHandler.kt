package cookietest

// TODO : actually rewrite the login system so it works on cookies

import com.mongodb.client.MongoDatabase
import io.javalin.Context
import io.javalin.Handler
import logintest.UserCore
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import java.util.*

class CookieTestHandler(private val dbDemo : MongoDatabase) : Handler {
	override fun handle(ctx: Context) {
	
		var authcookie = ctx.cookie("auth") ?: ""
		
		if (authcookie == "") {
			val login = ctx.basicAuthCredentials()
			
			if (login == null) {
				ctx.result("NO_LOGIN")
				return
			}
			
			if (login.password == "password" && login.username == "name") {
				
				// check for user in database
				val users = dbDemo.getCollection<UserCore.User>("users")
				val user = users.findOne("{name:${login.username}}")
				
				if (user == null) {
					ctx.result("NO_USER")
					return
				}
				
				authcookie = UUID.randomUUID().toString()
				ctx.cookie("auth", authcookie)
				
				dbDemo
				
				// .. continue with checking
			
			} else {
				ctx.result("BAD_LOGIN")
				return
			}
			
		}
		
		
		
	
	}
}