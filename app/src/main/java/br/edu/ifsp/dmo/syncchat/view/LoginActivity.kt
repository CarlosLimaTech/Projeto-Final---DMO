package br.edu.ifsp.dmo.syncchat.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.syncchat.databinding.ActivityLoginBinding
import br.edu.ifsp.dmo.syncchat.repository.UserRepository
import com.google.android.material.snackbar.Snackbar

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

        // Login de usuário
        binding.loginButton.setOnClickListener {
            val prontuario = binding.prontuarioEditText.text.toString()
            val senha = binding.passwordEditText.text.toString()
            userRepository.loginUser(prontuario, senha).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result
                    if (user != null) {
                        // Armazena o userId no SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("userId", user.id)
                        editor.apply()

                        Snackbar.make(binding.root, "Login realizado com sucesso", Snackbar.LENGTH_LONG).show()
                        val intent = Intent(this, AllConversationsActivity::class.java)
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
