package br.edu.ifsp.dmo.syncchat.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.dmo.syncchat.view.MessageAdapter
import br.edu.ifsp.dmo.syncchat.databinding.ActivityConversationBinding
import br.edu.ifsp.dmo.syncchat.model.Message
import br.edu.ifsp.dmo.syncchat.repository.MessageRepository

class ConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConversationBinding
    private lateinit var messageRepository: MessageRepository
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var conversationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageRepository = MessageRepository()

        // Configurando RecyclerView
        binding.messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(emptyList())
        binding.messageRecyclerView.adapter = messageAdapter

        // Recuperando o ID da conversa a partir do Intent
        conversationId = intent.getStringExtra("conversationId") ?: "defaultConversationId"

        loadMessages()

        binding.sendMessageButton.setOnClickListener {
            val messageContent = binding.messageEditText.text.toString()
            if (messageContent.isNotEmpty()) {
                val message = Message(
                    senderId = "user1Id",  // ID do usuário logado
                    receiverId = "user2Id",  // ID do destinatário
                    content = messageContent
                )
                sendMessage(message)
            }
        }
    }

    private fun loadMessages() {
        messageRepository.getMessages(conversationId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val messages = task.result ?: emptyList()
                messageAdapter.updateMessages(messages)
            }
        }
    }

    private fun sendMessage(message: Message) {
        messageRepository.sendMessage(message).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.messageEditText.text.clear()
                loadMessages() // Recarregar as mensagens após o envio
            }
        }
    }
}
