import args.Args
import args.ArgsParser
import extensions.completedOrNull
import model.Video
import processors.VideoProcessor
import java.io.File

class HlsServerCommon(args: Array<String>) {

    companion object {
        private const val dirName = "uploaded/videos"
    }

    private val parsedArgs: Args = ArgsParser.parse(args)

    val port = parsedArgs.port
    val root = File(parsedArgs.root)
    private val videosDir = File(parsedArgs.root + "/" + dirName)

    init {
        require(root.isDirectory)
        VideoProcessor.init(parsedArgs.bitrateList, videosDir, dirName)
    }

    private val videos: MutableList<Video> = VideoProcessor.readAllM3u8Files().toMutableList()
    private val pagesProcessor = PagesProcessor(parsedArgs.root)

    fun getIndexPage(): String {
        return pagesProcessor.gegIndexPage(
                videos
                        .map { it.name to it.file.completedOrNull() }.toMap()
        )
    }

    fun saveVideo(byteArray: ByteArray) {
        VideoProcessor.processNewVideo(byteArray).let(videos::add)
    }

    fun removeVideo(name: String) {
        File("$root/$dirName/$name").deleteRecursively()
        videos.removeIf { it.name == name && !it.file.isActive }
    }

}
