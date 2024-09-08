package br.edu.ifsp.dmo.syncchat.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.dmo.syncchat.databinding.ActivityAllConversationsBinding
import br.edu.ifsp.dmo.syncchat.model.Conversation
import br.edu.ifsp.dmo.syncchat.model.User
import br.edu.ifsp.dmo.syncchat.repository.ConversationRepository
import br.edu.ifsp.dmo.syncchat.repository.UserRepository
import com.google.firebase.firestore.ListenerRegistration

class AllConversationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllConversationsBinding
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var userRepository: UserRepository
    private lateinit var currentUserId: String
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllConversationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString("userId", "") ?: ""

        conversationRepository = ConversationRepository()
        userRepository = UserRepository()

        binding.conversationsRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationAdapter = ConversationAdapter(mutableListOf()) { conversation ->
            openConversation(conversation)
        }

        binding.conversationsRecyclerView.adapter = conversationAdapter

        loadConversations()

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val prontuario = s.toString()
                if (prontuario.isNotEmpty()) {
                    userRepository.getUserByProntuario(prontuario).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result
                            if (user != null) {
                                startConversationWithUser(user)
                            } else {
                                Toast.makeText(this@AllConversationsActivity, "Usuário não encontrado", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this@AllConversationsActivity, "Erro ao buscar usuário", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }

    private fun loadConversations() {
        if (currentUserId.isNotEmpty()) {
            // Remover o listener anterior para evitar duplicação
            listenerRegistration?.remove()
            listenerRegistration = conversationRepository.getAllConversations(currentUserId) { conversations ->
                if (conversations.isEmpty()) {
                    Toast.makeText(this, "Nenhuma conversa encontrada.", Toast.LENGTH_LONG).show()
                } else {
                    // Atualiza a lista sem duplicar
                    conversationAdapter.updateConversations(conversations)
                }
            }
        } else {
            Toast.makeText(this, "ID do usuário não encontrado.", Toast.LENGTH_LONG).show()
        }
    }

    fun onProfileIconClicked(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove() // Remove o listener ao destruir a atividade
    }

    private fun openConversation(conversation: Conversation) {
        val otherUserId = if (conversation.user1Id == currentUserId) conversation.user2Id else conversation.user1Id
        userRepository.getUserById(otherUserId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result
                if (user != null) {
                    val intent = Intent(this, ConversationActivity::class.java)
                    intent.putExtra("conversationId", conversation.id)
                    intent.putExtra("userName", user.nome)
                    intent.putExtra("userProntuario", user.prontuario)
                    intent.putExtra("receiverId", user.id)
                    startActivity(intent)
                }
            }
        }
    }

    private fun startConversationWithUser(user: User) {
        conversationRepository.findOrCreateConversation(currentUserId, user.id)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val conversation = task.result
                    if (conversation != null) {
                        val intent = Intent(this, ConversationActivity::class.java)
                        intent.putExtra("conversationId", conversation.id)
                        intent.putExtra("userName", user.nome)
                        intent.putExtra("userProntuario", user.prontuario)
                        intent.putExtra("receiverId", user.id)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "Erro ao iniciar conversa", Toast.LENGTH_LONG).show()
                }
            }
    }
}
