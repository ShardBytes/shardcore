import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.litote.kmongo.KMongo

class Mongo(val host: String,
            userName: String,
            password: String) {

	val mongoClient = KMongo.createClient(
			ServerAddress(host, 27017),
			listOf<MongoCredential>(
					MongoCredential.createCredential(
							userName,
							"admin",
							password.toCharArray()
					)
			)
	)

	val dbDemo = mongoClient.getDatabase("demo")

}
