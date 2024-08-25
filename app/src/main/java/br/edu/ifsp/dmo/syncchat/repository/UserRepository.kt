package br.edu.ifsp.dmo.syncchat.repository

import com.google.firebase.firestore.FirebaseFirestore
import br.edu.ifsp.dmo.syncchat.model.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    fun registerUser(user: User): Task<Void> {
        val taskCompletionSource = TaskCompletionSource<Void>()

        // Salva diretamente os dados do usuário no Firestore sem autenticação
        db.collection("users")
            .add(user)
            .addOnCompleteListener { firestoreTask ->
                if (firestoreTask.isSuccessful) {
                    taskCompletionSource.setResult(null)
                } else {
                    taskCompletionSource.setException(firestoreTask.exception ?: Exception("Failed to save user data"))
                }
            }

        return taskCompletionSource.task
    }

    fun loginUser(prontuario: String, senha: String): Task<User?> {
        return db.collection("users")
            .whereEqualTo("prontuario", prontuario)
            .whereEqualTo("senha", senha)
            .get()
            .continueWith { task ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    task.result.documents[0].toObject(User::class.java)
                } else {
                    null
                }
            }
    }
}
