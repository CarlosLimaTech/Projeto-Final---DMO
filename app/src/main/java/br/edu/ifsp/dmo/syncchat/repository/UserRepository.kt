package br.edu.ifsp.dmo.syncchat.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import br.edu.ifsp.dmo.syncchat.model.User

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    // Método para registrar o usuário
    fun registerUser(username: String, name: String, password: String): Task<Void> { // Note o tipo String para senha
        val taskCompletionSource = TaskCompletionSource<Void>()
        val userData = hashMapOf(
            "nome" to username,
            "prontuario" to name,
            "senha" to password // Armazenando como string
        )

        db.collection("Usuarios").add(userData)
            .addOnSuccessListener {
                taskCompletionSource.setResult(null)
            }
            .addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }

        return taskCompletionSource.task
    }


    // Implementação do método loginUser
    fun loginUser(prontuario: String, senha: String): Task<User?> {
        val taskCompletionSource = TaskCompletionSource<User?>()

        db.collection("Usuarios")
            .whereEqualTo("prontuario", prontuario)
            .whereEqualTo("senha", senha)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val user = document.toObject(User::class.java)
                    user?.id = document.id
                    taskCompletionSource.setResult(user)
                } else {
                    taskCompletionSource.setResult(null) // Usuário ou senha inválidos
                }
            }
            .addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }

        return taskCompletionSource.task
    }


    // Método para buscar um usuário pelo prontuário (getUserByProntuario)
    fun getUserByProntuario(prontuario: String): Task<User?> {
        val taskCompletionSource = TaskCompletionSource<User?>()

        db.collection("Usuarios")
            .whereEqualTo("prontuario", prontuario)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val user = document.toObject(User::class.java)

                    // Atribui o id do documento ao objeto User
                    if (user != null) {
                        user.id = document.id
                    }

                    taskCompletionSource.setResult(user)
                } else {
                    taskCompletionSource.setResult(null) // Nenhum usuário encontrado
                }
            }
            .addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }

        return taskCompletionSource.task
    }

    // Método para atualizar a senha do usuário
    fun updateUserPassword(userId: String, newPassword: String): Task<Void> {
        val taskCompletionSource = TaskCompletionSource<Void>()

        // Atualiza apenas o campo "senha" do documento do usuário
        db.collection("Usuarios").document(userId)
            .update("senha", newPassword)
            .addOnSuccessListener {
                taskCompletionSource.setResult(null)
            }
            .addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }

        return taskCompletionSource.task
    }

    // Método: Busca de usuário pelo ID
    fun getUserById(userId: String): Task<User?> {
        val taskCompletionSource = TaskCompletionSource<User?>()

        db.collection("Usuarios").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null) {
                        user.id = documentSnapshot.id // Atribui o ID do documento ao objeto User
                    }
                    taskCompletionSource.setResult(user)
                } else {
                    taskCompletionSource.setResult(null)
                }
            }
            .addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }

        return taskCompletionSource.task
    }
}
