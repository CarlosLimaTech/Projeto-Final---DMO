package br.edu.ifsp.dmo.syncchat.viewmodel

import androidx.lifecycle.ViewModel
import br.edu.ifsp.dmo.syncchat.model.User
import br.edu.ifsp.dmo.syncchat.repository.UserRepository

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()

    // Agora extraímos os parâmetros do objeto User e passamos para o repositório
    fun registerUser(user: User) {
        userRepository.registerUser(
            username = user.prontuario, // Usando o prontuário como o username
            name = user.nome,
            password = (user.senha) // Certifique-se de que senha seja um número válido
        )
    }

    fun loginUser(prontuario: String, senha: String) = userRepository.loginUser(prontuario, senha)
}
