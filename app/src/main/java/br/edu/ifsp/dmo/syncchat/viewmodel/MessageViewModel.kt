package br.edu.ifsp.dmo.syncchat.viewmodel

import androidx.lifecycle.ViewModel
import br.edu.ifsp.dmo.syncchat.model.Message
import br.edu.ifsp.dmo.syncchat.repository.MessageRepository

class MessageViewModel : ViewModel() {

    private val messageRepository = MessageRepository()

    fun sendMessage(message: Message) = messageRepository.sendMessage(message)

    fun getMessages(conversationId: String) = messageRepository.getMessages(conversationId)
}
