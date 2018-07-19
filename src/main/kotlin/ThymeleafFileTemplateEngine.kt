import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver

class ThymeleafFileTemplateEngine(cacheActive: Boolean) : TemplateEngine() {
	
	init {
		// by default Javalin uses ClassLoaderTemplateResolver, so I switch to File templates as its more useful
		setTemplateResolver(FileTemplateResolver().apply {
			suffix = ".html"
			prefix = ""
			templateMode = TemplateMode.HTML
			cacheTTLMs = 3600000L
			isCacheable = cacheActive
		})
	}
	
}