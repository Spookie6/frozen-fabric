package dev.frozencloud.infernum.util.ui.rendering

import dev.frozencloud.infernum.util.network.WebUtils.getInputStream
import kotlinx.coroutines.runBlocking
import org.lwjgl.system.MemoryUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.Files

class Image(
    val identifier: String,
    var isSVG: Boolean = false,
    var stream: InputStream = getStream(identifier),
    private var buffer: ByteBuffer? = null
) {

    init {
        isSVG = identifier.endsWith(".svg", true)
    }

    fun buffer(): ByteBuffer {
        if (buffer == null) {
            val bytes = stream.use { it.readBytes() } // .use ensures stream closes
            val nativeBuffer = MemoryUtil.memAlloc(bytes.size)
            nativeBuffer.put(bytes).flip()
            buffer = nativeBuffer
        }
        return buffer ?: throw IllegalStateException("Image has no data")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Image) return false
        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }

    companion object {

        private fun getStream(path: String): InputStream {
            val trimmedPath = path.trim()
            return if (trimmedPath.startsWith("http")) runBlocking { getInputStream(trimmedPath).getOrThrow() }
            else {
                val file = File(trimmedPath)
                if (file.exists() && file.isFile) Files.newInputStream(file.toPath())
                else this::class.java.getResourceAsStream(trimmedPath) ?: throw FileNotFoundException(trimmedPath)
            }
        }
    }
}
