package com.example.tetris.models

import com.example.tetris.helpers.array2dOfByte

class Frame(private val width: Int) {
    var data: ArrayList<ByteArray> = ArrayList()

    fun addRow(byteString: String): Frame {
        val row = ByteArray(byteString.length)

        for (i: Int in byteString.indices) {
            row[i] = "${byteString[i]}".toByte()
        }
        data.add(row)
        return this
    }

    fun as2dByteArray(): Array<ByteArray> {
        val bytes = array2dOfByte(data.size, width)
        return data.toArray(bytes)
    }
}
