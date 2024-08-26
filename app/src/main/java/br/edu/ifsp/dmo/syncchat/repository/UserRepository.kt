package br.edu.ifsp.dmo.syncchat.repository

import com.google.firebase.firestore.FirebaseFirestore
import br.edu.ifsp.dmo.syncchat.model.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    fun registerUser(user: User): Task<Void> {
        val taskCompletionSource = TaskCompletionSource<Void>()
        val userRef = db.collection("users").document() // Gera uma referência de documento com ID único

        val userId = userRef.id // Obtém o ID gerado

        val userWithId = user.copy(id = userId) // Cria um novo objeto User com o ID gerado

        userRef.set(userWithId) // Salva o objeto User com o ID no Firestore
            .addOnCompleteListener { firestoreTask ->
                if (firestoreTask.isSuccessful) {
                    taskCompletionSource.setResult(null)
                } else {
                    taskCompletionSource.setException(
                        firestoreTask.exception ?: Exception("Failed to save user data")
                    )
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

    fun getUserByProntuario(prontuario: String): Task<User?> {
        val taskCompletionSource = TaskCompletionSource<User?>()
        db.collection("users")
            .whereEqualTo("prontuario", prontuario)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    val user = task.result.documents[0].toObject(User::class.java)
                    taskCompletionSource.setResult(user)
                } else {
                    taskCompletionSource.setResult(null)
                }
            }
        return taskCompletionSource.task
    }
}
