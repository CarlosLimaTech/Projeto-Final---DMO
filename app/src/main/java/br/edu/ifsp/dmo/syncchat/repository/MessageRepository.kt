package br.edu.ifsp.dmo.syncchat.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task

class MessageRepository {

    private val db = FirebaseFirestore.getInstance()

    fun sendMessage(messageData: HashMap<String, Any>): Task<Void> {
        val conversationId = getConversationId(messageData["senderId"] as String, messageData["receiverId"] as String)
        val conversationRef = db.collection("conversations").document(conversationId)

        return conversationRef.collection("messages").add(messageData).continueWithTask { task ->
            if (task.isSuccessful) {
                updateConversation(messageData, conversationId)
            } else {
                task.exception?.let { throw it }
            }
        }
    }

    private fun updateConversation(messageData: HashMap<String, Any>, conversationId: String): Task<Void> {
        val conversation = hashMapOf(
            "id" to conversationId,
            "user1Id" to messageData["senderId"],
            "user2Id" to messageData["receiverId"],
            "lastMessage" to messageData["content"],
            "lastMessageTimestamp" to messageData["timestamp"]
        )
        return db.collection("conversations").document(conversationId).set(conversation)
    }

    fun getMessages(conversationId: String): Task<List<HashMap<String, Any>>> {
        return db.collection("conversations").document(conversationId)
            .collection("messages").orderBy("timestamp")
            .get()
            .continueWith { task ->
                if (task.isSuccessful) {
                    task.result.documents.map { it.data as HashMap<String, Any> }
                } else {
                    emptyList()
                }
            }
    }

    private fun getConversationId(user1Id: String, user2Id: String): String {
        return if (user1Id < user2Id) "$user1Id-$user2Id" else "$user2Id-$user1Id"
    }
}
