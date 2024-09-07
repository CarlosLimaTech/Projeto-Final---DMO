package br.edu.ifsp.dmo.syncchat.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.syncchat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Verifica se o usuário já está logado
        checkUserLoggedIn()

        // Configurações dos botões
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Função que verifica se o usuário está logado
    private fun checkUserLoggedIn() {
        val userId = sharedPreferences.getString("userId", null)
        if (userId != null) {
            // Se o userId estiver presente, o usuário está logado, redireciona para AllConversationsActivity
            val intent = Intent(this, AllConversationsActivity::class.java)
            startActivity(intent)
            finish()  // Finaliza a MainActivity para que o usuário não possa voltar para ela
        }
    }
}
