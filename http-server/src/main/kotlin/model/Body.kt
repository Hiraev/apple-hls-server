package model

sealed class Body {

    abstract val size: Int

    object Empty : Body() {
        override val size = 0
    }

    class ArrayBody(val byteArray: ByteArray) : Body() {
        override val size = byteArray.size
    }

}
