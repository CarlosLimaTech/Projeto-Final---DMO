package br.edu.ifsp.dmo.syncchat.repository

import com.google.firebase.firestore.FirebaseFirestore
import br.edu.ifsp.dmo.syncchat.model.Conversation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource

class ConversationRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getAllConversations(userId: String): Task<List<Conversation>> {
        val taskCompletionSource = TaskCompletionSource<List<Conversation>>()

        db.collection("conversations")
            .whereEqualTo("user1Id", userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val conversations1 = task.result?.toObjects(Conversation::class.java) ?: emptyList()

                    db.collection("conversations")
                        .whereEqualTo("user2Id", userId)
                        .get()
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                val conversations2 = task2.result?.toObjects(Conversation::class.java) ?: emptyList()
                                taskCompletionSource.setResult(conversations1 + conversations2)
                            } else {
                                taskCompletionSource.setException(task2.exception ?: Exception("Erro ao buscar conversas"))
                            }
                        }
                } else {
                    taskCompletionSource.setException(task.exception ?: Exception("Erro ao buscar conversas"))
                }
            }

        return taskCompletionSource.task
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
