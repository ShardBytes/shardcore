package logintest
import CoreMongo
import io.javalin.Context
import org.litote.kmongo.getCollection

class UserCore(val coreMongo: CoreMongo) {
	
	data class User(val name: String, val passwordHash: String, val passwordSalt: String)
	
	fun getUser(name: String?): User? {
		
		val users = coreMongo.dbDemo.getCollection<User>("users").find().toMutableList()
		
		if (name == null) return null
		
		for (u in users) {
			if (u.name == name) {
				return u
			}
		}
		
		return null
	}
	
	fun processCommand(ctx: Context, user: User, command: String) = when (command) {
		
		"login" -> ctx.result("OK_LOGIN")
		
		else -> ctx.result("@ERROR:UNKNOWN_COMMAND")
		
	}
	
	
}