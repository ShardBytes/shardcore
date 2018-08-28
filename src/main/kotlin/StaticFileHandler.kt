import io.javalin.Context
import io.javalin.Handler
import org.eclipse.jetty.http.MimeTypes
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

// !! UNUSED
// browser doesnt cache or something, fucc thymeleaf, use STATTICCC NORMALLL

class StaticFileHandler(private val enableBufferedStream: Boolean) : Handler {
	
	override fun handle(ctx: Context) {
		
		val splat = ctx.splat(0) ?: "" // get route
		val reqfile = File("static/$splat") // grab file matching route
		
		if (reqfile.exists()) { // check if exists
			
			// different processing for directory and files
			if (reqfile.isDirectory()) {
				// if directory, render thymeleaf and serve index.html
				val index = File("static/$splat/index.html")
				if (index.exists()) {
					println("-> serving thymeleaf index.html of /$splat")
					ctx.render(index.path, mapOf()) // render empty thymeleaf
					// ( this wont be executed if something else was routed here before ofc )
				} else {
					ctx.status(404)
				}
			} else {
				// if file, patch header MIME type and serve file through stream
				val type = MimeTypes.getDefaultMimeByExtension(reqfile.path)
				println("serve file -> ${reqfile.path} [$type]")
				ctx.contentType(type) // ULTRA IMPORTANT, http header needs the serve mime type matching !!!!
				
				var stream: InputStream = FileInputStream(reqfile)
				if (enableBufferedStream) stream = BufferedInputStream(stream)
				ctx.result(stream)
			}
			
		} else {
			ctx.status(404)
		}
		
	}
	
}