import java.io.File

fun getNoSpaceFileText(filename: String): String
	= File(filename)
		.readText()
		.replace("\n", "")
		.replace(" ", "")
		.replace("  ", "")