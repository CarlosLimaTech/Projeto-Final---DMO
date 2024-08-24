package br.edu.ifsp.dmo.syncchat.repository

import com.google.firebase.firestore.FirebaseFirestore
import br.edu.ifsp.dmo.syncchat.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.TaskCompletionSource

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun registerUser(user: User): Task<Void> {
        val userId = auth.currentUser?.uid ?: return TaskCompletionSource<Void>().apply { setException(Exception("User not logged in")) }.task
        return db.collection("users").document(userId).set(user)
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
