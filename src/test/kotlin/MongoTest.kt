import org.litote.kmongo.*

data class Fruit(val id: String, val price: Double, val amount: Long)

fun main(args: Array<String>) {

	val fruits = dbDemo.getCollection<Fruit>("fruit").find().toMutableList()

	fruits.forEach {
		println(it)
	}
}