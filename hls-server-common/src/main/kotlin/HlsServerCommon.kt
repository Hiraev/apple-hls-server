import args.Args
import args.ArgsParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import model.Video
import processors.PagesProcessor
import processors.VideoProcessor
import java.io.File

class HlsServerCommon(args: Array<String>) {

    companion object {
        private const val dirName = "uploaded/videos"
    }

    private val parsedArgs: Args = ArgsParser.parse(args)

    val port = parsedArgs.port
    val root = File(parsedArgs.root)
    private val bitrateList: List<Int> = parsedArgs.bitrateList.sorted()

    private val videosDir = File(parsedArgs.root + "/" + dirName)

    init {
        require(root.isDirectory)
        require(bitrateList.all { it > 0 }) { "All bitrate values must be positive" }
        VideoProcessor.init(bitrateList, videosDir, dirName)
    }

    private val videosMutex = Mutex()
    private val videos: MutableList<Video> = VideoProcessor.readAllM3u8Files().toMutableList()
    private val processingVideos: MutableList<String> = mutableListOf()
    private val pagesProcessor = PagesProcessor(parsedArgs.root)

    fun getIndexPage(): String = videos
            .associate { it.name to it.file }
            .plus(processingVideos.map { it to null })
            .let(pagesProcessor::gegIndexPage)

    fun saveVideo(byteArray: ByteArray) {
        val processingVideoName = VideoProcessor.processNewVideo(byteArray, ::onVideoSaveCompleted)
        processingVideos.add(processingVideoName)
    }

    fun removeVideo(name: String) {
        videos.removeIf { it.name == name }
        GlobalScope.launch {
            File("$root/$dirName/$name").deleteRecursively()
        }
    }

    private fun onVideoSaveCompleted(result: VideoProcessor.Result) {
        processingVideos.remove(result.name)

        GlobalScope.launch {
            videosMutex.lock()
            when (result) {
                is VideoProcessor.Result.Success -> videos.add(result.video)
                is VideoProcessor.Result.Failure -> removeVideo(result.name)
            }
            videosMutex.unlock()
        }
    }

}
