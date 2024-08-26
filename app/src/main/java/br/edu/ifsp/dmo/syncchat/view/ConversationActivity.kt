package br.edu.ifsp.dmo.syncchat.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.dmo.syncchat.databinding.ActivityConversationBinding
import br.edu.ifsp.dmo.syncchat.model.Message
import br.edu.ifsp.dmo.syncchat.model.Conversation
import br.edu.ifsp.dmo.syncchat.repository.MessageRepository
import com.google.firebase.firestore.FirebaseFirestore

class ConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConversationBinding
    private lateinit var messageRepository: MessageRepository
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var conversationId: String
    private lateinit var userName: String
    private lateinit var userProntuario: String
    private lateinit var receiverId: String // ID do destinatário
    private lateinit var currentUserId: String // ID do usuário logado
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageRepository = MessageRepository()

        // Obtendo o ID do usuário logado de SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString("userId", "") ?: ""

        // Recuperando o ID da conversa, nome e prontuário do usuário a partir do Intent
        receiverId = intent.getStringExtra("receiverId") ?: "defaultReceiverId" // Obtém o ID do destinatário
        conversationId = generateConversationId(currentUserId, receiverId)
        userName = intent.getStringExtra("userName") ?: "Nome desconhecido"
        userProntuario = intent.getStringExtra("userProntuario") ?: "Prontuário desconhecido"

        // Configurando o RecyclerView
        binding.messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(emptyList())
        binding.messageRecyclerView.adapter = messageAdapter

        // Exibindo o nome e o prontuário do outro usuário
        binding.userNameTextView.text = userName
        binding.userProntuarioTextView.text = userProntuario

        // Configurando o botão de voltar
        binding.backButton.setOnClickListener {
            finish()
        }

        loadMessages()

        binding.sendMessageButton.setOnClickListener {
            val messageContent = binding.messageEditText.text.toString()
            if (messageContent.isNotEmpty()) {
                val message = Message(
                    senderId = currentUserId,  // ID do usuário logado
                    receiverId = receiverId,  // ID do destinatário
                    content = messageContent
                )
                checkAndSendMessage(message)
            }
        }
    }

    private fun generateConversationId(user1Id: String, user2Id: String): String {
        return if (user1Id < user2Id) {
            "$user1Id-$user2Id"
        } else {
            "$user2Id-$user1Id"
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

    private fun checkAndSendMessage(message: Message) {
        val conversationRef = db.collection("conversations").document(conversationId)
        conversationRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Se a conversa já existe, apenas envie a mensagem
                sendMessage(message)
            } else {
                // Se a conversa não existe, crie uma nova e envie a mensagem
                createNewConversationAndSendMessage(message)
            }
        }
    }

    private fun createNewConversationAndSendMessage(message: Message) {
        val newConversation = Conversation(
            id = conversationId,
            user1Id = currentUserId,
            user2Id = receiverId,
            lastMessage = message.content,
            lastMessageTimestamp = System.currentTimeMillis()
        )

        db.collection("conversations").document(conversationId)
            .set(newConversation)
            .addOnCompleteListener {
                sendMessage(message)
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
