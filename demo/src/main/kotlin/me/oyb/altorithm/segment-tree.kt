package me.oyb.altorithm

class SegmentTree {
    private lateinit var nums: Array<Int>
    private lateinit var lazy: Array<Int>
    private lateinit var data: Array<Int>

    companion object {
        fun build(nums: Array<Int>) =
                SegmentTree().apply {
                    this.nums = nums
                    this.data = Array(4 * nums.size + 1) { 0 }
                    this.lazy = Array(4 * nums.size + 1) { 0 }

                    build(0, nums.size - 1, 1)
                }
    }

    fun getData() = data

    fun update(from: Int, to: Int, add: Int) {
        update(from, to, 0, nums.size - 1, 1, add)
    }

    private fun update(from: Int, to: Int, left: Int, right: Int, id: Int, add: Int) {
        if (left >= from && right <= to) {
            lazy[id] += add
            data[id] += (add * (right - left + 1))
            return
        }

        pushDown(id, left, right)
        val mid = left + (right - left) / 2

        if (from <= mid) {
            update(from, to, left, mid, id * 2, add)
        }

        if (to > mid) {
            update(from, to, mid + 1, right, id * 2 + 1, add)
        }

        data[id] = data[id * 2] + data[id * 2 + 1]
    }

    private fun pushDown(id: Int, left: Int, right: Int) {
        if (lazy[id] != 0) {
            val mid = left + (right - left) / 2

            lazy[id * 2] += lazy[id]
            lazy[id * 2 + 1] += lazy[id]

            data[id * 2] += (lazy[id] * (mid - left + 1))
            data[id * 2 + 1] += (lazy[id] * (right - mid))

            lazy[id] = 0
        }
    }

    fun query(from: Int, to: Int): Int {
        return query(from, to, 0, nums.size - 1, 1)
    }

    private fun query(from: Int, to: Int, left: Int, right: Int, id: Int): Int {
        if (left >= from && right <= to) {
            return data[id]
        }

        pushDown(id, left, right)
        val mid = left + (right - left) / 2

        var sum = 0
        if (from <= mid) {
            sum += query(from, to, left, mid, id * 2)
        }

        if (to > mid) {
            sum += query(from, to, mid + 1, right, id * 2 + 1)
        }

        return sum
    }

    private fun build(left: Int, right: Int, id: Int) {
        if (left == right) {
            data[id] = nums[left]
            return
        }

        val mid = left + (right - left) / 2
        build(left, mid, id * 2)
        build(mid + 1, right, id * 2 + 1)

        data[id] = data[id * 2] + data[id * 2 + 1]
    }
}

fun main() {
    val nums = arrayOf(1, 3, 5, 7, 9, 11)

    val segmentTree = SegmentTree.build(nums)

    nums.contentToString().let(::println)
    segmentTree.getData().contentToString().let(::println)

    segmentTree.query(0, 2).let(::println)
    segmentTree.query(2, 5).let(::println)
    segmentTree.query(0, 5).let(::println)

    segmentTree.update(0, 2, 1)
    segmentTree.query(0, 2).let(::println)
    segmentTree.query(0, 1).let(::println)

}