package me.oyb.util

import java.util.*

object HexUtils {
    private val NEW_LINE = System.lineSeparator()
    private val BYTE2CHAR = CharArray(256)
    private val HEXDUMP_TABLE = CharArray(256 * 4)
    private val HEX_PADDING = arrayOfNulls<String>(16)
    private val HEXDUMP_ROW_PREFIXES = arrayOfNulls<String>(65536 ushr 4)
    private val BYTE2HEX = arrayOfNulls<String>(256)
    private val BYTE_PADDING = arrayOfNulls<String>(16)

    private val BYTE2HEX_PAD = Array(256) {
        val str = Integer.toHexString(it)
        if (it > 0xF) str else "0$str"
    }

    init {
        val digits = "0123456789abcdef".toCharArray()
        for (i in 0..255) {
            HEXDUMP_TABLE[i shl 1] = digits[i ushr 4 and 0x0F]
            HEXDUMP_TABLE[(i shl 1) + 1] = digits[i and 0x0F]
        }

        // Generate the lookup table for hex dump paddings
        var i = 0
        while (i < HEX_PADDING.size) {
            val padding: Int = HEX_PADDING.size - i
            val buf = StringBuilder(padding * 3)
            for (j in 0 until padding) {
                buf.append("   ")
            }
            HEX_PADDING[i] = buf.toString()
            i++
        }

        // Generate the lookup table for the start-offset header in each row (up to 64KiB).
        i = 0
        while (i < HEXDUMP_ROW_PREFIXES.size) {
            val buf = StringBuilder(12)
            buf.append(NEW_LINE)
            buf.append(java.lang.Long.toHexString(i.toLong() shl 4 and 0xFFFFFFFFL or 0x100000000L))
            buf.setCharAt(buf.length - 9, '|')
            buf.append('|')
            HEXDUMP_ROW_PREFIXES[i] = buf.toString()
            i++
        }

        // Generate the lookup table for byte-to-hex-dump conversion
        i = 0
        while (i < BYTE2HEX.size) {
            BYTE2HEX[i] = " " + BYTE2HEX_PAD[i and 0xFF]
            i++
        }

        // Generate the lookup table for byte dump paddings
        i = 0
        while (i < BYTE_PADDING.size) {
            val padding: Int = BYTE_PADDING.size - i
            val buf = StringBuilder(padding)
            for (j in 0 until padding) {
                buf.append(' ')
            }
            BYTE_PADDING[i] = buf.toString()
            i++
        }

        // Generate the lookup table for byte-to-char conversion
        i = 0
        while (i < BYTE2CHAR.size) {
            if (i <= 0x1f || i >= 0x7f) {
                BYTE2CHAR[i] = '.'
            } else {
                BYTE2CHAR[i] = i.toChar()
            }
            i++
        }
    }

    fun prettyHexDump(bytes: ByteArray): String {
        val origin = StringBuilder(256)
        appendPrettyHexDump(origin, bytes, 0, bytes.size)
        return origin.toString()
    }

    fun formatHex(bytes: ByteArray): String = HexFormat.of().formatHex(bytes)

    private fun appendPrettyHexDump(dump: StringBuilder, buf: ByteArray, offset: Int = 0, length: Int) {
        if (isOutOfBounds(offset, length, buf.size)) {
            throw IndexOutOfBoundsException(
                "expected: " + "0 <= offset(" + offset + ") <= offset + length(" + length
                        + ") <= " + "buf.capacity(" + buf.size + ')'
            )
        }
        if (length == 0) {
            return
        }
        dump.append("+--------+-------------------------------------------------+----------------+")
        val fullRows = length ushr 4
        val remainder = length and 0xF

        // Dump the rows which have 16 bytes.
        for (row in 0 until fullRows) {
            val rowStartIndex = (row shl 4) + offset

            // Per-row prefix.
            appendHexDumpRowPrefix(dump, row, rowStartIndex)

            // Hex dump
            val rowEndIndex = rowStartIndex + 16
            for (j in rowStartIndex until rowEndIndex) {
                dump.append(
                    BYTE2HEX[getUnsignedByte(buf[j]).toInt()]
                )
            }
            dump.append(" |")

            // ASCII dump
            for (j in rowStartIndex until rowEndIndex) {
                dump.append(BYTE2CHAR[getUnsignedByte(buf[j]).toInt()])
            }
            dump.append('|')
        }

        // Dump the last row which has less than 16 bytes.
        if (remainder != 0) {
            val rowStartIndex = (fullRows shl 4) + offset
            appendHexDumpRowPrefix(dump, fullRows, rowStartIndex)

            // Hex dump
            val rowEndIndex = rowStartIndex + remainder
            for (j in rowStartIndex until rowEndIndex) {
                dump.append(BYTE2HEX[getUnsignedByte(buf[j]).toInt()])
            }
            dump.append(HEX_PADDING[remainder])
            dump.append(" |")

            // Ascii dump
            for (j in rowStartIndex until rowEndIndex) {
                dump.append(BYTE2CHAR[getUnsignedByte(buf[j]).toInt()])
            }
            dump.append(BYTE_PADDING[remainder])
            dump.append('|')
        }
        dump.append("$NEW_LINE+--------+-------------------------------------------------+----------------+")
    }

    private fun appendHexDumpRowPrefix(dump: StringBuilder, row: Int, rowStartIndex: Int) {
        if (row < HEXDUMP_ROW_PREFIXES.size) {
            dump.append(HEXDUMP_ROW_PREFIXES[row])
        } else {
            dump.append(NEW_LINE)
            dump.append(java.lang.Long.toHexString(rowStartIndex.toLong() and 0xFFFFFFFFL or 0x100000000L))
            dump.setCharAt(dump.length - 9, '|')
            dump.append('|')
        }
    }

    private fun getUnsignedByte(byte: Byte): Short {
        return (byte.toInt() and 0xFF).toShort()
    }

    private fun isOutOfBounds(index: Int, length: Int, capacity: Int): Boolean {
        return index or length or capacity or index + length or capacity - (index + length) < 0
    }
}