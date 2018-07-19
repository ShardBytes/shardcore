import io.javalin.Context
import io.javalin.Handler
import org.thymeleaf.TemplateEngine

class CacheResetREST(private val engine: TemplateEngine) : Handler {
	
	override fun handle(ctx: Context) {
		
		engine.clearTemplateCache()
		ctx.result("ok cache reset")
		
	}
	
}