package br.edu.ifsp.dmo.syncchat.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.dmo.syncchat.R
import br.edu.ifsp.dmo.syncchat.model.Conversation
import java.text.SimpleDateFormat
import java.util.*

class ConversationAdapter(
    private var conversations: List<Conversation>,
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
        conversations = newConversations
        notifyDataSetChanged()
    }

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.conversationUserTextView)
        private val lastMessageTextView: TextView = itemView.findViewById(R.id.conversationLastMessageTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.conversationTimestampTextView)

        fun bind(conversation: Conversation) {
            val userName = if (conversation.user1Id == "currentUserId") conversation.user2Id else conversation.user1Id
            userTextView.text = userName
            lastMessageTextView.text = conversation.lastMessage
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            timestampTextView.text = sdf.format(Date(conversation.lastMessageTimestamp))
        }
    }
}
