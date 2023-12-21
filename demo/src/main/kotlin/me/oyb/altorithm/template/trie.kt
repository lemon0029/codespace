package me.oyb.altorithm.template


class TrieNode {
    var isWord = false
    val children = mutableMapOf<Char, TrieNode>()
}

class Trie {

    private val root = TrieNode()

    fun insert(word: String) {
        var node = root
        for (c in word) {
            if (!node.children.containsKey(c)) {
                node.children[c] = TrieNode()
            }

            node = node.children[c]!!
        }
        node.isWord = true
    }

    fun search(word: String) = searchPrefix(word)?.isWord ?: false

    fun startWith(word: String) = searchPrefix(word) != null

    fun searchPrefix(word: String): TrieNode? {
        var node = root

        for (c in word) {
            if (!node.children.containsKey(c)) {
                return null
            }

            node = node.children[c]!!
        }

        return node
    }
}

fun main() {
    val trie = Trie()
    val words = listOf("aaa", "aa")

    words.forEach(trie::insert)

    println(trie.search("a"))
    println(trie.search("aa"))
    println(trie.search("aaa"))

    println(trie.startWith("a"))
    println(trie.startWith("aa"))
    println(trie.startWith("aaa"))
    println(trie.startWith("aaaa"))
}