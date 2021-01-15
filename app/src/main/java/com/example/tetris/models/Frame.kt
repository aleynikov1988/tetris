package com.example.tetris.models

import com.example.tetris.helpers.array2dOfByte

class Frame(private val width: Int) {
    var data: ArrayList<ByteArray> = ArrayList()

    fun addRow(str: String): Frame {
        val row = ByteArray(str.length)

        for (i: Int in str.indices) {
            row[i] = "${str[i]}".toByte()
        }
        data.add(row)

        return this
    }

    fun as2dByteArray(): Array<ByteArray> {
        return data.toArray(array2dOfByte(data.size, width))
    }
}
