package rest

import Mongo
import io.javalin.Context
import io.javalin.Handler
import org.litote.kmongo.getCollection

class FruitRest(val mongo: Mongo) : Handler {

	data class Fruit(val id: String, val price: Double, val amount: Long)

	override fun handle(ctx: Context) {

		val fruits = mongo.dbDemo.getCollection<Fruit>("fruit").find().toMutableList()
		ctx.json(fruits)

	}
}