package me.oyb.altorithm.sort

class SelectionSort<T : Comparable<T>> : Sort<T>() {

    override fun doInvoke(array: Array<T>) {
        val n = array.size

        for (i in 0 until n - 1) {
            var maxValueIndex = 0
            for (j in 1 until n - i) {
                if (array[maxValueIndex].lessThan(array[j])) {
                    maxValueIndex = j
                }
            }
            array.swap(n - i - 1, maxValueIndex)
        }
    }

}