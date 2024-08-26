package br.edu.ifsp.dmo.syncchat.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllConversationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        conversationRepository = ConversationRepository()
        userRepository = UserRepository()

        binding.conversationsRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationAdapter = ConversationAdapter(emptyList()) { conversation ->
            openConversation(conversation)
        }
        binding.conversationsRecyclerView.adapter = conversationAdapter

        loadConversations()

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
        val currentUserId = "currentUserId" // Substitua pelo ID do usuário logado
        conversationRepository.getAllConversations(currentUserId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val conversations = task.result ?: emptyList()
                conversationAdapter.updateConversations(conversations)
            }
        }
    }

    private fun openConversation(conversation: Conversation) {
        val intent = Intent(this, ConversationActivity::class.java)
        intent.putExtra("conversationId", conversation.id)
        startActivity(intent)
    }

    private fun startConversationWithUser(user: User) {
        conversationRepository.findOrCreateConversation("currentUserId", user.id)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val conversation = task.result
                    if (conversation != null) {
                        val intent = Intent(this, ConversationActivity::class.java)
                        intent.putExtra("conversationId", conversation.id)
                        startActivity(intent)
                    }
                } else {
                    Snackbar.make(binding.root, "Erro ao iniciar conversa", Snackbar.LENGTH_LONG).show()
                }
            }
    }
}
