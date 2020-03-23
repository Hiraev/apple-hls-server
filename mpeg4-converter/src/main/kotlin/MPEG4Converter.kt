import java.io.File

object MPEG4Converter {

    fun convert(file: File) {
        Runtime.getRuntime().exec("ffmpeg")
        require(file.extension == ".mp4")

    }

}
