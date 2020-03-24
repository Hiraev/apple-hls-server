package extensions

import model.constants.Mime
import java.io.File

fun File.mime() = when (this.extension.toLowerCase()) {
    "m3u8" -> Mime.M3U8
    "ts" -> Mime.TS
    "html" -> Mime.HTML
    "css" -> Mime.CSS
    else -> null
}
