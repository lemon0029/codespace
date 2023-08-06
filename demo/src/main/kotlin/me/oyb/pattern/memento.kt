package me.oyb.pattern

class Editor {
    private var content = ""

    fun type(words: String) {
        content += words
    }

    fun getContent() = content

    fun createState() = EditorState(content)

    fun restore(state: EditorState) {
        content = state.content
    }
}

class EditorState(val content: String)

class EditorStateHistory {

    private val states = mutableListOf<EditorState>()

    fun push(state: EditorState) {
        states.add(state)
    }

    fun pop(): EditorState {
        states.removeLast()
        return states.last()
    }
}

fun main() {
    val editorStateHistory = EditorStateHistory()

    val editor = Editor()

    editor.type("This is the first sentence.\n")
    editorStateHistory.push(editor.createState())

    editor.type("This is the second sentence.\n")
    editorStateHistory.push(editor.createState())

    editor.type("This is the third sentence.\n")
    editorStateHistory.push(editor.createState())

    println(editor.getContent())

    editor.restore(editorStateHistory.pop())
    println(editor.getContent())
}