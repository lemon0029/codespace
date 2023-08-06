package me.oyb.altorithm.sort

class QuickSort<T : Comparable<T>> : Sort<T>() {

    override fun doInvoke(array: Array<T>) {
        helper(array, 0, array.size - 1)
    }

    private fun helper(array: Array<T>, left: Int, right: Int) {
        if (right > left) {
            val p = partition(array, left, right)
            helper(array, left, p - 1)
            helper(array, p + 1, right)
        }
    }

    private fun partition(array: Array<T>, left: Int, right: Int): Int {
        val x = array[left]

        var i = left
        var j = right

        while (i < j) {
            while (i < j) {
                if (array[j].greaterThan(x)) {
                    j--
                } else {
                    array[i] = array[j]
                    i++
                    break
                }
            }

            while (i < j) {
                if (array[i].lessThan(x)) {
                    i++
                } else {
                    array[j] = array[i]
                    j--
                    break
                }
            }
        }


        array[i] = x
        return i
    }
}