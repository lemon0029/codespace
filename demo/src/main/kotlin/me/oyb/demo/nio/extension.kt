package me.oyb.demo.nio

import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.SocketChannel

fun SocketChannel.readCommand(): ByteArray {
    val buffer = ByteBuffer.allocate(22)
    var result = ByteArray(0)

    read(buffer)
    buffer.flip()
    result += buffer.getBytes()
    buffer.clear()

    return result
}

fun SocketChannel.readLine(): ByteArray {
    val buffer = ByteBuffer.allocate(1024)
    var result = ByteArray(0)

    try {
        while (read(buffer) > 0) {
            buffer.flip()
            result += buffer.getBytes()
            buffer.clear()
            if (result.last() == '\n'.code.toByte()) {
                break
            }
        }
    } catch (e: ClosedChannelException) {
        return result
    }

    return result
}

fun ByteBuffer.getBytes(): ByteArray = ByteArray(remaining()).apply { get(this) }