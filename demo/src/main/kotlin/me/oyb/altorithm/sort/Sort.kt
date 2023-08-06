package me.oyb.altorithm.sort

abstract class Sort<T : Comparable<T>> {

    companion object {

        fun <X : Comparable<X>> bubbleSort(array: Array<X>) = BubbleSort<X>().invoke(array)
        fun <X : Comparable<X>> insertionSort(array: Array<X>) = InsertionSort<X>().invoke(array)
        fun <X : Comparable<X>> selectionSort(array: Array<X>) = SelectionSort<X>().invoke(array)
        fun <X : Comparable<X>> mergeSort(array: Array<X>) = MergeSort<X>().invoke(array)
        fun <X : Comparable<X>> quickSort(array: Array<X>) = QuickSort<X>().invoke(array)

    }

    private var swapCount = 0
    private var compareCount = 0

    fun invoke(array: Array<T>) {
        val kClass = this::class
        val startTime = System.currentTimeMillis()
        doInvoke(array)
        val endTime = System.currentTimeMillis()

        val sorted = sorted(array)

        val costTime = endTime - startTime
        println("[${kClass.simpleName}] sorted: $sorted, cost: ${costTime}ms, swap: $swapCount, compare: $compareCount")
    }

    protected abstract fun doInvoke(array: Array<T>)

    fun T.lessThan(other: T): Boolean {
        compareCount++
        return compareTo(other) < 0
    }

    fun T.greaterThan(other: T): Boolean {
        compareCount++
        return compareTo(other) > 0
    }

    fun Array<T>.swap(i: Int, j: Int) {
        val t = this[i]
        this[i] = this[j]
        this[j] = t
        swapCount++
    }

    private fun sorted(array: Array<T>): Boolean {
        for (i in 1 until array.size) {
            if (array[i].lessThan(array[i - 1])) return false
        }
        return true
    }
}