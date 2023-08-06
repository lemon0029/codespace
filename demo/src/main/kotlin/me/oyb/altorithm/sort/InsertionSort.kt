package me.oyb.altorithm.sort

class InsertionSort<T : Comparable<T>> : Sort<T>() {

    override fun doInvoke(array: Array<T>) {
        val n = array.size

        for (i in 1 until n) {
            for (j in i downTo 1) {
                if (array[j - 1].greaterThan(array[j])) {
                    array.swap(j - 1, j)
                } else {
                    break
                }
            }
        }
    }

}