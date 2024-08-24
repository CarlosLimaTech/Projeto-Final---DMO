package br.edu.ifsp.dmo.syncchat.repository

import com.google.firebase.firestore.FirebaseFirestore
import br.edu.ifsp.dmo.syncchat.model.Message
import br.edu.ifsp.dmo.syncchat.model.Conversation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference

class MessageRepository {

    private val db = FirebaseFirestore.getInstance()

    fun sendMessage(message: Message): Task<Void> {
        val conversationId = getConversationId(message.senderId, message.receiverId)
        val conversationRef = db.collection("conversations").document(conversationId)

        return conversationRef.collection("messages").add(message).continueWithTask { task ->
            if (task.isSuccessful) {
                updateConversation(message, conversationId)
            } else {
                task.exception?.let { throw it }
            }
        }
    }

    private fun updateConversation(message: Message, conversationId: String): Task<Void> {
        val conversation = Conversation(
            id = conversationId,
            user1Id = message.senderId,
            user2Id = message.receiverId,
            lastMessage = message.content,
            lastMessageTimestamp = message.timestamp
        )
        return db.collection("conversations").document(conversationId).set(conversation)
    }

    fun getMessages(conversationId: String): Task<List<Message>> {
        return db.collection("conversations").document(conversationId)
            .collection("messages").orderBy("timestamp")
            .get()
            .continueWith { task ->
                if (task.isSuccessful) {
                    task.result.toObjects(Message::class.java)
                } else {
                    emptyList()
                }
            }
    }

    private fun getConversationId(user1Id: String, user2Id: String): String {
        return if (user1Id < user2Id) "$user1Id-$user2Id" else "$user2Id-$user1Id"
    }
}
