package br.edu.ifsp.dmo.syncchat.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.syncchat.databinding.ActivityLoginBinding
import br.edu.ifsp.dmo.syncchat.model.User
import br.edu.ifsp.dmo.syncchat.repository.UserRepository
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository()

        // Registro de usuário
        binding.registerButton.setOnClickListener {
            val user = User(
                nome = binding.nameEditText.text.toString(),
                senha = binding.passwordEditText.text.toString(),
                prontuario = binding.prontuarioEditText.text.toString()
            )
            userRepository.registerUser(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(binding.root, "Registro realizado com sucesso", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(binding.root, "Erro ao registrar usuário", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        // Login de usuário
        binding.loginButton.setOnClickListener {
            val prontuario = binding.prontuarioEditText.text.toString()
            val senha = binding.passwordEditText.text.toString()
            userRepository.loginUser(prontuario, senha).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result
                    if (user != null) {
                        Snackbar.make(binding.root, "Login realizado com sucesso", Snackbar.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Snackbar.make(binding.root, "Erro ao realizar login", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
