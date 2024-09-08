package br.edu.ifsp.dmo.syncchat.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.dmo.syncchat.databinding.ActivityConversationBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageRepository = MessageRepository()

        // Obtendo o ID do usuário logado de SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString("userId", "") ?: ""

        // Recuperando o ID da conversa, nome e prontuário do usuário a partir do Intent
        receiverId = intent.getStringExtra("receiverId") ?: "defaultReceiverId"
        conversationId = generateConversationId(currentUserId, receiverId)
        userName = intent.getStringExtra("userName") ?: "Nome desconhecido"
        userProntuario = intent.getStringExtra("userProntuario") ?: "Prontuário desconhecido"

        // Configurando o RecyclerView
        binding.messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(emptyList(), currentUserId)
        binding.messageRecyclerView.adapter = messageAdapter

        // Exibindo o nome e o prontuário do outro usuário
        binding.userNameTextView.text = userName
        binding.userProntuarioTextView.text = userProntuario

        // Setup messages listener
        setupMessagesListener()

        binding.sendMessageButton.setOnClickListener {
            val messageContent = binding.messageEditText.text.toString()
            if (messageContent.isNotEmpty()) {
                val messageData = hashMapOf<String, Any>(
                    "senderId" to currentUserId,
                    "receiverId" to receiverId,
                    "content" to messageContent,
                    "timestamp" to System.currentTimeMillis()
                )
                checkAndSendMessage(messageData)
            } else {
                Toast.makeText(this, "Mensagem vazia. Digite algo para enviar.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            finish()  // Encerra a atividade atual
        }
    }

    private fun generateConversationId(user1Id: String, user2Id: String): String {
        return if (user1Id < user2Id) "$user1Id-$user2Id" else "$user2Id-$user1Id"
    }

    private fun setupMessagesListener() {
        messageRepository.listenForMessages(conversationId) { messages ->
            messageAdapter.updateMessages(messages)
            if (messages.isNotEmpty()) {
                binding.messageRecyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }

    private fun checkAndSendMessage(messageData: HashMap<String, Any>) {
        val conversationRef = FirebaseFirestore.getInstance().collection("conversations").document(conversationId)
        conversationRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                sendMessage(messageData) // Se a conversa já existe, apenas envie a mensagem
            } else {
                createNewConversationAndSendMessage(messageData) // Crie uma nova conversa e envie a mensagem
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao verificar conversa existente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNewConversationAndSendMessage(messageData: HashMap<String, Any>) {
        val newConversation = hashMapOf<String, Any>(
            "id" to conversationId,
            "user1Id" to (messageData["senderId"] ?: ""),  // Uso de Elvis operator para garantir não-null
            "user2Id" to (messageData["receiverId"] ?: ""), // Uso de Elvis operator para garantir não-null
            "lastMessage" to (messageData["content"] ?: ""), // Uso de Elvis operator para garantir não-null
            "lastMessageTimestamp" to (messageData["timestamp"] ?: System.currentTimeMillis()) // Uso de Elvis operator
        )

        FirebaseFirestore.getInstance().collection("conversations").document(conversationId)
            .set(newConversation)
            .addOnSuccessListener {
                sendMessage(messageData)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao criar nova conversa", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendMessage(messageData: HashMap<String, Any>) {
        messageRepository.sendMessage(messageData).addOnSuccessListener {
            binding.messageEditText.text.clear()
            // Atualizações são gerenciadas pelo listener agora
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show()
        }
    }
}
