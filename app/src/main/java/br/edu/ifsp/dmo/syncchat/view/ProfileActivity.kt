package br.edu.ifsp.dmo.syncchat.view

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.syncchat.databinding.ActivityProfileBinding
import br.edu.ifsp.dmo.syncchat.model.User
import br.edu.ifsp.dmo.syncchat.repository.UserRepository
import com.google.android.gms.tasks.OnCompleteListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userRepository: UserRepository
    private var currentUser: User? = null
    private lateinit var sharedPreferences: SharedPreferences  // Variável para SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializando SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        userRepository = UserRepository()

        binding.btnChangePassword.setOnClickListener {
            changePassword()
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = sharedPreferences.getString("userId", null)
        userId?.let {
            userRepository.getUserById(it).addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    currentUser = task.result
                    displayUserInfo(currentUser!!)
                } else {
                    Toast.makeText(this, "Falha ao carregar informações do usuário", Toast.LENGTH_LONG).show()
                }
            })
        } ?: Toast.makeText(this, "ID de usuário não encontrado", Toast.LENGTH_LONG).show()
    }

    private fun displayUserInfo(user: User) {
        binding.tvUserName.text = user.nome
        binding.tvUserProntuario.text = user.prontuario
    }

    private fun changePassword() {
        // Criar AlertDialog para solicitar nova senha
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Trocar Senha")

        // Criação do campo de input para a nova senha
        val input = EditText(this)
        input.hint = "Digite a nova senha"
        builder.setView(input)

        // Botão de confirmação
        builder.setPositiveButton("Confirmar") { dialog, _ ->
            val newPassword = input.text.toString()
            if (newPassword.isNotEmpty()) {
                updatePassword(newPassword)
            } else {
                Toast.makeText(this, "A senha não pode estar vazia", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        // Botão de cancelamento
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun updatePassword(newPassword: String) {
        currentUser?.let {
            it.senha = newPassword  // Atualiza o campo de senha no objeto User

            // Chama o UserRepository para atualizar a senha no banco de dados
            userRepository.updateUserPassword(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Falha ao alterar a senha", Toast.LENGTH_LONG).show()
                }
            }
        } ?: Toast.makeText(this, "Erro: usuário não encontrado", Toast.LENGTH_LONG).show()
    }

    private fun logoutUser() {
        // Limpa o SharedPreferences removendo o userId
        val editor = sharedPreferences.edit()
        editor.remove("userId")
        editor.apply()

        // Redireciona para a LoginActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()  // Fecha a ProfileActivity
    }
}
