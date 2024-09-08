package br.edu.ifsp.dmo.syncchat.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.syncchat.databinding.ActivityLoginBinding
import br.edu.ifsp.dmo.syncchat.repository.UserRepository

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userRepository = UserRepository()

        // Verifica se o usuário já está logado
        checkIfUserIsLoggedIn()

        // Login de usuário
        binding.loginButton.setOnClickListener {
            val prontuario = binding.prontuarioEditText.text.toString()
            val senha = binding.passwordEditText.text.toString()

            if (prontuario.isNotEmpty() && senha.isNotEmpty()) {
                userRepository.loginUser(prontuario, senha).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result
                        if (user != null) {
                            // Armazena o userId no SharedPreferences
                            val editor = sharedPreferences.edit()
                            editor.putString("userId", user.id)
                            editor.apply()

                            Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, AllConversationsActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Usuário ou senha inválidos", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Erro ao realizar login", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Verifica se o userId já está no SharedPreferences
    private fun checkIfUserIsLoggedIn() {
        val userId = sharedPreferences.getString("userId", null)
        if (userId != null) {
            // Redireciona para AllConversationsActivity se o usuário já estiver logado
            val intent = Intent(this, AllConversationsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
