import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver

class FileTemplateEngine : TemplateEngine() {
	
	init {
		setTemplateResolver(FileTemplateResolver().apply {
			suffix = ".html"
			prefix = ""
			templateMode = TemplateMode.HTML
			cacheTTLMs = 3600000L
		})
	}
	
}