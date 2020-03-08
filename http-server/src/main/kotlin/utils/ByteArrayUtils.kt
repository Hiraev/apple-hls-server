package utils

object ByteArrayUtils {

    fun merge(arrays: List<ByteArray>): ByteArray {
        val size = arrays.map(ByteArray::size).reduce { a, b -> a + b }
        val result = ByteArray(size)
        var index = 0
        arrays.forEach { array ->
            array.forEach {
                result[index++] = it
            }
        }
        return result
    }

}
