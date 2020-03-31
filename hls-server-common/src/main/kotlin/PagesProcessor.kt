import freemarker.template.Configuration
import java.io.File
import java.io.StringWriter

class PagesProcessor(
        rootPath: String
) {

    private val cfg = Configuration(Configuration.VERSION_2_3_30)

    init {
        cfg.setDirectoryForTemplateLoading(File("$rootPath/templates"))
        cfg.defaultEncoding = "UTF-8"
    }

    fun gegIndexPage(m3u8FilesPath: List<String>): String {
        val videosString = StringWriter()
        cfg.getTemplate("videos.tmp").process(mapOf("videos" to m3u8FilesPath), videosString)
        val outIndexPage = StringWriter()
        cfg.getTemplate("index.html").process(mapOf("videos" to videosString.toString()), outIndexPage)
        return outIndexPage.toString()
    }

}
