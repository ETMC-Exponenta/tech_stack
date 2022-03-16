package com.example.exponenta

class MaxIndex() {

    fun findMaxIndex(arrayIndex: FloatArray): Int {

        var index: Int = 0
        var max: Float = 0.0f

        for (i in 0 until arrayIndex.size) {
            if (arrayIndex[i] > max) {
                max = arrayIndex[i]
                index = i
            }
        }
        return index
    }
}