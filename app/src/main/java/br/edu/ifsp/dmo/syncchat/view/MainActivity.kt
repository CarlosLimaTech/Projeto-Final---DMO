package br.edu.ifsp.dmo.syncchat.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.syncchat.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aqui você poderia configurar uma lista de contatos ou conversas disponíveis
        // Por exemplo, para selecionar com quem deseja conversar

        binding.startConversationButton.setOnClickListener {
            // Exemplo: iniciar uma nova conversa com outro usuário
            val intent = Intent(this, ConversationActivity::class.java)
            intent.putExtra("conversationId", "user1Id-user2Id")  // Passando o ID da conversa
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {
            // Exemplo: fazer logout e retornar para a tela de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
