package br.edu.ifsp.dmo.syncchat.repository

import com.google.firebase.firestore.FirebaseFirestore
import br.edu.ifsp.dmo.syncchat.model.Conversation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.ListenerRegistration

class ConversationRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getAllConversations(userId: String, onConversationsChanged: (List<Conversation>) -> Unit): ListenerRegistration {
        val conversationsList = mutableListOf<Conversation>()

        // Listener para conversas onde userId é user1Id
        val listener1 = db.collection("conversations")
            .whereEqualTo("user1Id", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle the error
                    return@addSnapshotListener
                }

                // Limpa a lista e adiciona novos dados
                conversationsList.clear()
                snapshots?.toObjects(Conversation::class.java)?.let {
                    conversationsList.addAll(it)
                }

                // Listener para conversas onde userId é user2Id
                db.collection("conversations")
                    .whereEqualTo("user2Id", userId)
                    .addSnapshotListener { snapshots2, e2 ->
                        if (e2 != null) {
                            // Handle the error
                            return@addSnapshotListener
                        }

                        snapshots2?.toObjects(Conversation::class.java)?.let {
                            conversationsList.addAll(it)
                        }

                        // Organiza a lista consolidada de conversas por timestamp descrescente antes de passar ao callback
                        val sortedConversations = conversationsList.sortedByDescending { it.lastMessageTimestamp }
                        onConversationsChanged(sortedConversations)
                    }
            }

        return listener1 // Retorna o primeiro listener para manter o registro ativo e evitar memory leaks
    }


    fun findOrCreateConversation(user1Id: String, user2Id: String): Task<Conversation> {
        val taskCompletionSource = TaskCompletionSource<Conversation>()
        val conversationId = if (user1Id < user2Id) "$user1Id-$user2Id" else "$user2Id-$user1Id"

        val docRef = db.collection("conversations").document(conversationId)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val conversation = task.result?.toObject(Conversation::class.java)
                if (conversation != null) {
                    taskCompletionSource.setResult(conversation)
                } else {
                    val newConversation = Conversation(
                        id = conversationId,
                        user1Id = user1Id,
                        user2Id = user2Id
                    )
                    docRef.set(newConversation).addOnCompleteListener { setTask ->
                        if (setTask.isSuccessful) {
                            taskCompletionSource.setResult(newConversation)
                        } else {
                            taskCompletionSource.setException(setTask.exception ?: Exception("Erro ao criar nova conversa"))
                        }
                    }
                }
            } else {
                taskCompletionSource.setException(task.exception ?: Exception("Erro ao buscar conversa"))
            }
        }

        return taskCompletionSource.task
    }
}
