package br.edu.ifsp.dmo.syncchat.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.dmo.syncchat.R
import br.edu.ifsp.dmo.syncchat.model.Message

class MessageAdapter(private var messages: List<Message>, private val currentUserId: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message, currentUserId)
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val messageLayout: LinearLayout = itemView.findViewById(R.id.messageLayout)

        fun bind(message: Message, currentUserId: String) {
            messageTextView.text = message.content

            // Alinha e muda o fundo da mensagem baseado no remetente
            val layoutParams = messageLayout.layoutParams as LinearLayout.LayoutParams

            if (message.senderId == currentUserId) {
                layoutParams.gravity = GravityCompat.END
                messageTextView.background = itemView.context.getDrawable(R.drawable.background_right)
            } else {
                layoutParams.gravity = GravityCompat.START
                messageTextView.background = itemView.context.getDrawable(R.drawable.background_left)
            }

            messageLayout.layoutParams = layoutParams
        }
    }
}
