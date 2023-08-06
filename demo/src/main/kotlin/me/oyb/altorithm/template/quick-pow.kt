package me.oyb.altorithm.template

fun test() {
    println(2.pow(39))
    println(2.quickPow(39))

    val a = 13
    val n = 1000000000
    val m = 10007

    println(modExpQuick(a, n, m))
    println(modExp(a, n, m))
}

fun Int.pow(p: Int): Long {
    var ret = 1L

    repeat(p) { ret *= this }

    return ret
}

fun Int.quickPow(p: Int): Long {
    if (p == 0) {
        return 1L
    }

    val t = this.quickPow(p shr 1)

    return if (p and 1 == 1) t * t * this
    else t * t
}

fun modExp(a: Int, n: Int, m: Int): Int {
    var r = 1
    repeat(n) { r = (r * a) % m }
    return r
}

fun modExpQuick(a: Int, n: Int, m: Int): Int {
    var r = 1
    var x = a
    var y = n

    while (y != 0) {

        if (y.takeLowestOneBit() == 1) {
            r = (r * x) % m
        }

        x = (x * x) % m

        y = y shr 1
    }

    return r
}
