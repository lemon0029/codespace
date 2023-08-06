package me.oyb.altorithm.sort

class BubbleSort<T : Comparable<T>> : Sort<T>() {

    override fun doInvoke(array: Array<T>) {
        val n = array.size

        for (i in 0 until n - 1) {
            var bubbled = false
            for (j in 1 until n - i) {
                if (array[j].lessThan(array[j - 1])) {
                    array.swap(j, j - 1)
                    bubbled = true
                }
            }
            if (!bubbled) break
        }
    }
}