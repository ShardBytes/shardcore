import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.litote.kmongo.KMongo
import java.io.File

class Mongo {

	val mongoClient = KMongo.createClient(
			ServerAddress(File("mongo.host").readText(), 27017),
			listOf<MongoCredential>(
					MongoCredential.createCredential(
							"faggot",
							"admin",
							File("mongo.password").readText().toCharArray().apply { println(this) }
					)
			)
	)

	val dbDemo = mongoClient.getDatabase("demo")

}

