import io.javalin.Context
import io.javalin.Handler
import org.thymeleaf.TemplateEngine

// uses REST path parameter :key
class CacheResetREST(private val engine: TemplateEngine,
                     private val cacheResetKey: String) : Handler {
	
	override fun handle(ctx: Context) {
		
		if (cacheResetKey == ctx.param("key")) {
			engine.clearTemplateCache()
			ctx.result("ok cache cleared")
		} else {
			ctx.result("error wrong key")
		}
		
	}
}