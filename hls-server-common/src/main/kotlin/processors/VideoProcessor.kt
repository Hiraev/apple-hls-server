package processors

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.Video
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

object VideoProcessor {

    private lateinit var bitrateList: List<Int>
    private lateinit var videosDir: File
    private lateinit var videosDirName: String

    fun init(bitrateList: List<Int>, videosDir: File, videosDirName: String) {
        this.bitrateList = bitrateList
        this.videosDir = videosDir
        this.videosDirName = videosDirName
    }

    /** This function saves the processors as mp4 file then convert it into
     *  m3u8 files with different resolutions. All generated files
     *  are represented in the result file.
     *
     * @return m3u8 master file
     */
    fun processNewVideo(
            byteArray: ByteArray,
            onLoad: (Result) -> Unit,
            name: String = UUID.randomUUID().toString()
    ): String {
        GlobalScope.launch {
            val result = try {
                val mp4 = saveMp4(byteArray, name)
                Result.Success(Video(name, createM3u8(mp4)))
            } catch (t: Throwable) {
                Result.Failure(name)
            }
            onLoad.invoke(result)
        }
        return name
    }

    fun readAllM3u8Files() = videosDir.listFiles()
            ?.filter(File::isDirectory)
            ?.map { File("$videosDirName/${it.name}/${it.name}.m3u8") }
            ?.map { Video(it.nameWithoutExtension, it) }
            ?: emptyList()

    private fun saveMp4(byteArray: ByteArray, name: String): File {
        val dirPath = videosDir.path + "/" + name
        File(dirPath).mkdirs()

        val mp4 = File("$dirPath/$name.mp4")
        mp4.createNewFile()
        mp4.writeBytes(byteArray)
        return mp4
    }

    private fun createM3u8(mp4: File): File {
        val videoInfo = getVideoInfo(mp4)
        println(videoInfo)
        require(mp4.extension == "mp4")

        val m3u8List = bitrateList
                .filter { it < videoInfo.bitrate }.plus(videoInfo.bitrate).sorted()
                .associateWith { bitrate -> convertVideoAndSave(mp4, bitrate) }

        mp4.delete()
        if (m3u8List.isEmpty()) throw Throwable("Can't generate m3u8")
        return createMasterFile(mp4.nameWithoutExtension, m3u8List)
    }

    private fun convertVideoAndSave(file: File, bitrate: Int): File {
        File("${file.parent}/$bitrate/").mkdir()
        val process = Runtime.getRuntime().exec("ffmpeg -i ${file.path} -c:a copy -b:v $bitrate -maxrate $bitrate ${file.parent}/$bitrate/${file.nameWithoutExtension}.m3u8")
        val waited = process.waitFor(15, TimeUnit.SECONDS)
        return if (waited && process.exitValue() == 0) {
            File("$bitrate/${file.nameWithoutExtension}.m3u8")
        } else {
            throw Throwable("Error when saving the processors")
        }
    }

    private fun getVideoInfo(mp4: File): VideoInfo {
        val process = Runtime.getRuntime().exec("ffprobe -v error -select_streams v:0 -show_entries stream=width,height,bit_rate -of default=noprint_wrappers=1 ${mp4.path}")
        val res = process.waitFor(5, TimeUnit.SECONDS)
        if (res && process.exitValue() == 0) {
            val bufferedReader = process.inputStream.bufferedReader()
            return VideoInfo(
                    getInt(bufferedReader.readLine()),
                    getInt(bufferedReader.readLine()),
                    getInt(bufferedReader.readLine())
            )
        } else {
            throw Throwable("Error when trying ffprobe")
        }
    }

    private fun createMasterFile(name: String, bitrateToFileMap: Map<Int, File>): File {
        val stringBuilder = StringBuilder("#EXTM3U\n")

        bitrateToFileMap.forEach { (bandwidth, file) ->
            stringBuilder.append("#EXT-X-STREAM-INF:BANDWIDTH=$bandwidth\n")
            stringBuilder.append(file.path)
            stringBuilder.append("\n")
        }
        val file = File("${videosDir.path}/$name/$name.m3u8")
        file.createNewFile()
        file.writeText(stringBuilder.toString())
        return File("$videosDirName/$name/$name.m3u8")
    }

    private fun getInt(string: String): Int = string.split("=").component2().toInt()

    private data class VideoInfo(
            val width: Int,
            val height: Int,
            val bitrate: Int
    )

    sealed class Result(val name: String) {
        class Success(val video: Video) : Result(video.name)
        class Failure(name: String) : Result(name)
    }

}
