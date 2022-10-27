package com.example.learningble.models


sealed class Message(val text: String) {
    class RemoteMessage(text: String) : Message(text)
    class LocalMessage(text: String) : Message(text)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Message) return false

        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }
}