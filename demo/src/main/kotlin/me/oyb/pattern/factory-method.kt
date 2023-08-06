package me.oyb.pattern

interface Dialog {
    fun render()
    fun createButton(): Button
}

class HtmlDialog : Dialog {
    override fun render() {
        val button = createButton()
        button.onClick()
        button.render()
    }

    override fun createButton() = HtmlButton()
}

class WindowsDialog : Dialog {
    override fun render() {
        val button = createButton()
        button.onClick()
        button.render()
    }

    override fun createButton() = WindowsButton()
}

interface Button {
    fun render()
    fun onClick()
}

class HtmlButton : Button {
    override fun render() {
        println("<button>Test Button</button>")
        onClick()
    }

    override fun onClick() {
        println("Click! Button says - 'Hello World!'")
    }
}

class WindowsButton : Button {
    override fun render() {
        println("<button>Test Button</button>")
        onClick()
    }

    override fun onClick() {
        println("Click! Button says - 'Hello World!'")
    }
}