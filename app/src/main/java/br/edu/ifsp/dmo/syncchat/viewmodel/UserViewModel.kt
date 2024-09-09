package br.edu.ifsp.dmo.syncchat.viewmodel

import androidx.lifecycle.ViewModel
import br.edu.ifsp.dmo.syncchat.model.User
import br.edu.ifsp.dmo.syncchat.repository.UserRepository

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()


    fun registerUser(user: User) {
        userRepository.registerUser(
            username = user.prontuario,
            name = user.nome,
            password = (user.senha)
        )
    }

    fun loginUser(prontuario: String, senha: String) = userRepository.loginUser(prontuario, senha)
}
