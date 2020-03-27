import java.io.File
import java.util.UUID

class HlsServerCommon(args: Array<String>) {

    companion object {
        const val dirName = "uploaded/videos"
    }

    private val parsedArgs: Args = ArgsParser.parse(args)

    val port = parsedArgs.port
    val root = File(parsedArgs.root)

    init {
        require(root.isDirectory)
    }

    fun saveVideo(byteArray: ByteArray) {
        val fileName = UUID.randomUUID()
        val dirPath = parsedArgs.root + "/" + dirName + "/" + fileName
        val dir = File(dirPath)
        dir.mkdirs()

        val file = File("$dirPath/$fileName.mp4")
        file.createNewFile()
        file.writeBytes(byteArray)
        println("Video saved")
    }

}
