import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.litote.kmongo.KMongo

class Mongo {

	val mongoClient = KMongo.createClient(
			ServerAddress(getNoSpaceFileText("mongo.host"), 27017),
			listOf<MongoCredential>(
					MongoCredential.createCredential(
							"faggot",
							"admin",
							getNoSpaceFileText("mongo.password").toCharArray()
					)
			)
	)

	val dbDemo = mongoClient.getDatabase("demo")

}
