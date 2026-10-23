fun saudacao(nome: String): String {
    require(nome.isNotBlank()) { "nome vazio" }
    return "Olá, ${nome.trim()}!"
}
