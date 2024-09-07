package br.edu.ifsp.dmo.syncchat.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.syncchat.databinding.ActivityRegisterBinding
import br.edu.ifsp.dmo.syncchat.model.User
import br.edu.ifsp.dmo.syncchat.repository.UserRepository

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
                    Toast.makeText(this, "Registro realizado com sucesso", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Fecha a tela de registro
                } else {
                    Toast.makeText(this, "Erro ao registrar usuário", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
