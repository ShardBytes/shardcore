import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.litote.kmongo.KMongo
import java.io.File

val mongoClient = KMongo.createClient(
		ServerAddress(File("mongo.host").readText()),
		mutableListOf<MongoCredential>(
				MongoCredential.createCredential(
						"faggot",
						"admin",
						File("mongo.password").readText().toCharArray()
				)
		)
)

val dbDemo = mongoClient.getDatabase("demo")