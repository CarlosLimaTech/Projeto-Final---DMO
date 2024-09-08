package br.edu.ifsp.dmo.syncchat.viewmodel

import androidx.lifecycle.ViewModel
import br.edu.ifsp.dmo.syncchat.model.Message
import br.edu.ifsp.dmo.syncchat.repository.MessageRepository

class MessageViewModel : ViewModel() {

    private val messageRepository = MessageRepository()

    // Converte o objeto Message em um HashMap para passar ao repositório
    fun sendMessage(message: Message) {
        val messageData: HashMap<String, Any> = hashMapOf(
            "senderId" to message.senderId as Any,
            "receiverId" to message.receiverId as Any,
            "content" to message.content as Any,
            "timestamp" to message.timestamp as Any
        )

        messageRepository.sendMessage(messageData)
    }

    fun getMessages(conversationId: String) = messageRepository.getMessages(conversationId)
}
