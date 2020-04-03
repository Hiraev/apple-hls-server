package model

import kotlinx.coroutines.Deferred
import java.io.File

data class Video(
        val name: String,
        val file: Deferred<File>
)
