package me.oyb.altorithm.template

fun <T : Comparable<T>> Array<T>.binarySearch(target: T, fromIndex: Int, toIndex: Int): Int {
    var low = fromIndex
    var high = toIndex - 1

    while (low <= high) {
        val mid = low + ((high - low) shr 1)
        val midVal = this[mid]

        if (midVal == target) {
            return mid
        } else if (midVal < target) {
            low = mid + 1
        } else {
            high = mid - 1
        }
    }

    return -(low + 1)
}

fun <T : Comparable<T>> Array<T>.upperBound(target: T, fromIndex: Int, toIndex: Int): Int {
    var low = fromIndex
    var high = toIndex - 1

    while (low <= high) {
        val mid = low + ((high - low) shr 1)
        val midVal = this[mid]

        if (midVal >= target) {
            high = mid - 1
        } else {
            low = mid + 1
        }
    }

    return low
}

fun <T : Comparable<T>> Array<T>.lowerBound(target: T, fromIndex: Int, toIndex: Int): Int {
    var low = fromIndex
    var high = toIndex - 1

    while (low <= high) {
        val mid = low + ((high - low) shr 1)
        val midVal = this[mid]

        if (midVal <= target) {
            low = mid + 1
        } else {
            high = mid - 1
        }
    }

    return high
}

fun <T : Comparable<T>> Array<T>.binarySearch(target: T) =
    this.binarySearch(target, 0, size)

fun <T : Comparable<T>> Array<T>.upperBound(target: T) =
    this.upperBound(target, 0, size)

fun <T : Comparable<T>> Array<T>.lowerBound(target: T) =
    this.lowerBound(target, 0, size)