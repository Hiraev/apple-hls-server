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
    private val pagesProcessor = PagesProcessor(parsedArgs.root)

    fun getIndexPage(): String = videos
            .associate { it.name to it.file }
            .let(pagesProcessor::gegIndexPage)

    fun saveVideo(byteArray: ByteArray) {
        GlobalScope.launch {
            VideoProcessor.processNewVideo(byteArray, ::onVideoSaveProcess)
        }
    }

    fun removeVideo(name: String) {
        videos.removeIf { it.name == name }
        GlobalScope.launch {
            File("$root/$dirName/$name").deleteRecursively()
        }
    }

    private fun onVideoSaveProcess(result: VideoProcessor.Result) {
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
