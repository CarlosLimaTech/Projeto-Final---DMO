package br.edu.ifsp.dmo.syncchat.model

data class Conversation(
    val id: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis()
)
