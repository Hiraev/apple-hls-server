import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

class HlsServerCommon(args: Array<String>) {

    companion object {
        const val dirName = "uploaded/videos"
    }

    private val parsedArgs: Args = ArgsParser.parse(args)
    private val videos = mutableListOf<File>()
    private val pagesProcessor = PagesProcessor(parsedArgs.root)

    val port = parsedArgs.port
    val root = File(parsedArgs.root)

    init {
        require(root.isDirectory)
    }

    fun getIndexPage(): String {
        return pagesProcessor.gegIndexPage(listOf("sadsasd", "sadasdas"))
    }

    fun saveVideo(byteArray: ByteArray) {
        val fileName = UUID.randomUUID()
        val dirPath = parsedArgs.root + "/" + dirName + "/" + fileName
        val dir = File(dirPath)
        dir.mkdirs()

        val mp4 = File("$dirPath/$fileName.mp4")
        mp4.createNewFile()
        mp4.writeBytes(byteArray)
        createM3u8(mp4)
        println("Video saved")
    }

    fun removeVideo(name: String) {
        File("$dirName/$name").deleteRecursively()
        videos.removeIf { it.nameWithoutExtension == name }
    }

    private fun createM3u8(mp4: File) {
        require(mp4.extension == "mp4")
        File(mp4.parent + "/m3u8").mkdir()
        convertVideoAndSave(mp4)
    }

    private fun convertVideoAndSave(file: File) {
        val a = Runtime.getRuntime().exec("ffmpeg -i ${file.path} -b:v 1M -g 60 -hls_time 1 -hls_list_size 0 -hls_segment_size 50000 ${file.parent}/m3u8/${file.nameWithoutExtension}.m3u8")
        val res = a.waitFor(5, TimeUnit.SECONDS)
        if (res && a.exitValue() == 0) {
            println("Good")
            videos += File(file.parent)
        } else {
            processErrorVideoSaving()
        }
        file.delete()
    }

    private fun processErrorVideoSaving() {
        println("Error when saving the video")
    }

}
