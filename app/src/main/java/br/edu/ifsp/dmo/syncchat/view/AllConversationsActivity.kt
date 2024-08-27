package br.edu.ifsp.dmo.syncchat.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.dmo.syncchat.databinding.ActivityAllConversationsBinding
import br.edu.ifsp.dmo.syncchat.model.Conversation
import br.edu.ifsp.dmo.syncchat.model.User
import br.edu.ifsp.dmo.syncchat.repository.ConversationRepository
import br.edu.ifsp.dmo.syncchat.repository.UserRepository
import com.google.android.material.snackbar.Snackbar

class AllConversationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllConversationsBinding
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var userRepository: UserRepository
    private lateinit var currentUserId: String // Definido como lateinit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllConversationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mover a inicialização do SharedPreferences para onCreate
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString("userId", "") ?: ""

        conversationRepository = ConversationRepository()
        userRepository = UserRepository()

        binding.conversationsRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationAdapter = ConversationAdapter(emptyList()) { conversation ->
            openConversation(conversation)
        }
        binding.conversationsRecyclerView.adapter = conversationAdapter

        loadConversations() // Certifique-se de carregar as conversas após configurar o adapter

        // Implementação da barra de busca
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
                                Snackbar.make(binding.root, "Usuário não encontrado", Snackbar.LENGTH_LONG).show()
                            }
                        } else {
                            Snackbar.make(binding.root, "Erro ao buscar usuário", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }

    private fun loadConversations() {
        if (currentUserId.isNotEmpty()) {
            Log.d("AllConversationsActivity", "Current User ID: $currentUserId")  // Log do userId
            conversationRepository.getAllConversations(currentUserId).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val conversations = task.result ?: emptyList()
                    if (conversations.isEmpty()) {
                        Snackbar.make(binding.root, "Nenhuma conversa encontrada.", Snackbar.LENGTH_LONG).show()
                    } else {
                        Log.d("AllConversationsActivity", "Conversations found: ${conversations.size}")  // Log do número de conversas encontradas
                        conversationAdapter.updateConversations(conversations)
                    }
                } else {
                    Snackbar.make(binding.root, "Erro ao carregar conversas: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        } else {
            Snackbar.make(binding.root, "ID do usuário não encontrado.", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun openConversation(conversation: Conversation) {
        val intent = Intent(this, ConversationActivity::class.java)
        intent.putExtra("conversationId", conversation.id)
        startActivity(intent)
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
                    Snackbar.make(binding.root, "Erro ao iniciar conversa", Snackbar.LENGTH_LONG).show()
                }
            }
    }
}
