package br.edu.ifsp.dmo.syncchat.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.syncchat.databinding.ActivityRegisterBinding
import br.edu.ifsp.dmo.syncchat.model.User
import br.edu.ifsp.dmo.syncchat.repository.UserRepository
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository()

        binding.registerButton.setOnClickListener {
            val user = User(
                nome = binding.nameEditText.text.toString(),
                prontuario = binding.prontuarioEditText.text.toString(),
                senha = binding.passwordEditText.text.toString()
            )

            userRepository.registerUser(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(binding.root, "Registro realizado com sucesso", Snackbar.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Fecha a tela de registro
                } else {
                    Snackbar.make(binding.root, "Erro ao registrar usuário", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
