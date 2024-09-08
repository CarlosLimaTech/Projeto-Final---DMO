package br.edu.ifsp.dmo.syncchat.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.dmo.syncchat.R
import br.edu.ifsp.dmo.syncchat.model.Conversation
import br.edu.ifsp.dmo.syncchat.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.*

class ConversationAdapter(
    private var conversations: MutableList<Conversation>,
    private val onConversationClick: (Conversation) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)
        holder.itemView.setOnClickListener {
            onConversationClick(conversation)
        }
    }

    override fun getItemCount(): Int = conversations.size

    fun updateConversations(newConversations: List<Conversation>) {
        // Remover duplicatas antes de atualizar a lista
        val distinctConversations = newConversations.distinctBy { it.id }
        conversations.clear()
        conversations.addAll(distinctConversations)
        notifyDataSetChanged()
    }

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.conversationUserTextView)
        private val lastMessageTextView: TextView = itemView.findViewById(R.id.conversationLastMessageTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.conversationTimestampTextView)

        fun bind(conversation: Conversation) {
            val context = itemView.context
            val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val currentUserId = sharedPreferences.getString("userId", "") ?: ""

            val otherUserId = if (conversation.user1Id == currentUserId) {
                conversation.user2Id
            } else {
                conversation.user1Id
            }

            // Busca o nome do usuário a partir do ID
            val userRepository = UserRepository()
            userRepository.getUserById(otherUserId).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result
                    if (user != null) {
                        userTextView.text = user.nome
                    } else {
                        userTextView.text = "Usuário Desconhecido"
                        Toast.makeText(context, "Erro ao carregar usuário", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    userTextView.text = "Erro ao carregar"
                    Toast.makeText(context, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show()
                }
            }

            lastMessageTextView.text = conversation.lastMessage
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            timestampTextView.text = sdf.format(Date(conversation.lastMessageTimestamp))
        }
    }
}
