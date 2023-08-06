package me.oyb.altorithm.sort

class MergeSort<T : Comparable<T>> : Sort<T>() {

    override fun doInvoke(array: Array<T>) {
        helper(array, 0, array.size)
    }

    private fun helper(array: Array<T>, left: Int, right: Int) {
        if (right - left <= 7) {
            for (i in left + 1 until right) {
                var j = i
                val t = array[j]
                while (j >= left + 1 && t.lessThan(array[j - 1])) {
                    array[j] = array[--j]
                }

                array[j] = t
            }
            return
        }

        val mid = (right - left) / 2 + left
        helper(array, left, mid)
        helper(array, mid, right)

        val leftArray = array.copyOfRange(left, mid)

        var i = left
        var j = 0
        var k = mid
        while (i < right) {
            if ((k >= right) || (j < leftArray.size && leftArray[j].lessThan(array[k]))) {
                array[i++] = leftArray[j++]
            } else {
                array[i++] = array[k++]
            }
        }
    }
}