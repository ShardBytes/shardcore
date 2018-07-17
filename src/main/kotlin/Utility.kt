import io.javalin.Context
import java.io.File

// removes whitespaces from string
fun noWhiteSpace(text: String)
	= text.replace("\\s".toRegex(), "")


// routes the path using thymeleaf
// works only with StringTemplateResolver
fun Context.routeStaticThymeleaf(model: Map<String, Any?>) {
	val file = File("static/${path()}")
	if (file.exists()) {
		renderThymeleaf(file.readText(), model)
	} else {
		status(404)
	}
}