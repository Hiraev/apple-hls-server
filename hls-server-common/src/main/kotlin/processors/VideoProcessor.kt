package processors

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import model.Video
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

object VideoProcessor {

    private lateinit var bitrateList: List<Int>
    private lateinit var videosDir: File
    private lateinit var videosPath: String

    fun init(bitrateList: List<Int>, videosDir: File, videosPath: String) {
        this.bitrateList = bitrateList
        this.videosDir = videosDir
        this.videosPath = videosPath
    }

    /** This function saves the processors as mp4 file then convert it into
     *  m3u8 files with different resolutions. All generated files
     *  are represented in the result file.
     *
     * @return m3u8 master file
     */
    fun processNewVideo(byteArray: ByteArray, name: String = UUID.randomUUID().toString()): Video =
            Video(
                    name,
                    GlobalScope.async(Dispatchers.IO) {
                        val mp4 = saveMp4(byteArray, name)
                        createM3u8(mp4)
                    }
            )

    fun readAllM3u8Files() = videosDir.listFiles()
            ?.filter(File::isDirectory)
            ?.map { File("$videosPath/${it.name}/m3u8/${it.name}.m3u8") }
            ?.map { Video(it.name, CompletableDeferred(it)) }
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
        require(mp4.extension == "mp4")
        File(mp4.parent + "/m3u8").mkdir()
        return convertVideoAndSave(mp4)
    }

    private fun convertVideoAndSave(file: File): File {
        val a = Runtime.getRuntime().exec("ffmpeg -i ${file.path} -b:v 1M -g 60 -hls_time 1 -hls_list_size 0 -hls_segment_size 50000 ${file.parent}/m3u8/${file.nameWithoutExtension}.m3u8")
        val res = a.waitFor(5, TimeUnit.SECONDS)
        val result = if (res && a.exitValue() == 0) {
            File("$videosPath/${file.nameWithoutExtension}/m3u8/${file.nameWithoutExtension}.m3u8")
        } else {
            throw Throwable("Error when saving the processors")
        }
        file.delete()
        return result
    }

    private fun getVideoInfo(mp4: File) {
        val process = Runtime.getRuntime().exec("ffprobe -v error -select_streams v:0 -show_entries stream=width,height,bit_rate -of default=noprint_wrappers=1 processors.mp4")
        val res = process.waitFor(5, TimeUnit.SECONDS)
        process.inputStream.bufferedReader().lines()

    }

    private data class VideoInfo(
            val width: Int,
            val height: Int,
            val resolution: Int
    )

}
