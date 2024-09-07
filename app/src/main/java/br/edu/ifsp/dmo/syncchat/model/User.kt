package br.edu.ifsp.dmo.syncchat.model

data class User(
    val id: String = "",
    val nome: String = "",
    var senha: String = "",
    val prontuario: String = ""
)
