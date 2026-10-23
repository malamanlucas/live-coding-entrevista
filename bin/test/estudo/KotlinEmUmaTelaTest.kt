package estudo

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

// Cada teste mostra uma linha da seção "Kotlin em uma tela" da cola.
// No IntelliJ: seta verde ao lado do teste. No terminal: ./gradlew test --tests 'estudo.KotlinEmUmaTelaTest'

data class Ponto(val x: Int, val y: Int)

sealed interface Resultado
data class Ok(val valor: Int) : Resultado
data class Erro(val mensagem: String) : Resultado

fun descrever(r: Resultado): String = when (r) {    // sem else: subtipo novo não compila até tratar
    is Ok -> "ok: ${r.valor}"                       // smart cast: aqui r já é Ok
    is Erro -> "erro: ${r.mensagem}"
}

object Config {
    const val MAX_ITENS = 10
}

class Pedido private constructor(val id: Long, val total: Int) {
    companion object {
        fun de(id: Long, total: Int): Pedido {      // fábrica no companion
            require(total >= 0) { "total negativo: $total" }
            return Pedido(id, total)
        }
    }
}

fun String.ehPalindromo(): Boolean = this == reversed()

fun talvezNome(tem: Boolean): String? = if (tem) "abc" else null

class KotlinEmUmaTelaTest {

    @Test
    fun `val e var`() {
        val lista = mutableListOf(1, 2)
        lista.add(3)                                // val fixa a referência; o objeto pode mudar
        assertEquals(listOf(1, 2, 3), lista)
        var i = 0
        i += 1
        assertEquals(1, i)
    }

    @Test
    fun `null safety`() {
        val s = talvezNome(false)                   // String?
        assertNull(s?.length)                       // ?. devolve null em vez de NPE
        assertEquals(0, s?.length ?: 0)             // ?: dá o valor padrão
        assertNull(s?.let { it.uppercase() })       // let só roda se não for null
        assertFailsWith<NullPointerException> { s!!.length }
        val t = talvezNome(true)
        if (t != null) assertEquals(3, t.length)    // smart cast: dentro do if, t é String
    }

    @Test
    fun `when como expressão`() {
        fun classificar(x: Int) = when (x) {
            0 -> "zero"
            in 1..9 -> "dígito"
            else -> "outro"                         // obrigatório: Int não é exaustivo
        }
        assertEquals("zero", classificar(0))
        assertEquals("dígito", classificar(7))
        assertEquals("outro", classificar(42))
    }

    @Test
    fun `data class`() {
        val a = Ponto(1, 2)
        val b = Ponto(1, 2)
        assertEquals(a, b)                          // equals gerado compara os campos
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals("Ponto(x=1, y=2)", a.toString())
        val c = a.copy(y = 5)                       // copy (rasa)
        assertEquals(Ponto(1, 5), c)
        val (x, y) = c                              // component1, component2
        assertEquals(1, x)
        assertEquals(5, y)
        assertEquals(1, setOf(a, b).size)           // serve de chave em HashSet/HashMap
    }

    @Test
    fun `sealed interface com when exaustivo`() {
        assertEquals("ok: 1", descrever(Ok(1)))
        assertEquals("erro: falhou", descrever(Erro("falhou")))
    }

    @Test
    fun `object e companion object`() {
        assertSame(Config, Config)                  // singleton: uma instância só
        assertEquals(10, Config.MAX_ITENS)
        assertEquals(100, Pedido.de(1, 100).total)  // construtor privado, fábrica no companion
    }

    @Test
    fun `extension function`() {
        assertTrue("arara".ehPalindromo())
        assertFalse("kotlin".ehPalindromo())
    }

    @Test
    fun `require, check e error`() {
        val e = assertFailsWith<IllegalArgumentException> { Pedido.de(1, -5) }   // require
        assertEquals("total negativo: -5", e.message)
        val conectado = talvezNome(false) != null
        assertFailsWith<IllegalStateException> { check(conectado) { "não conectado" } }
        assertFailsWith<IllegalStateException> { error("não deveria chegar aqui") }
    }

    @Test
    fun `ranges`() {
        assertEquals(listOf(0, 1, 2), (0 until 3).toList())     // exclui o fim
        assertEquals(listOf(0, 1, 2), (0..<3).toList())         // o mesmo que until
        assertEquals(listOf(0, 1, 2, 3), (0..3).toList())       // inclui o fim
        assertEquals(listOf(3, 2, 1, 0), (3 downTo 0).toList())
        assertEquals(listOf(0, 2, 4), (0..5 step 2).toList())
        assertEquals(0..2, intArrayOf(7, 8, 9).indices)
        assertTrue(5 in 1..10)
    }

    @Test
    fun `withIndex e destructuring`() {
        val pares = mutableListOf<String>()
        for ((i, v) in listOf("a", "b").withIndex()) pares.add("$i=$v")
        assertEquals(listOf("0=a", "1=b"), pares)

        val idades = mapOf("ana" to 30, "bia" to 25)
        assertEquals(listOf("ana:30", "bia:25"), idades.map { (nome, idade) -> "$nome:$idade" })

        val (primeiro, segundo) = Pair("x", 1)
        assertEquals("x", primeiro)
        assertEquals(1, segundo)
    }

    class Servico {
        var calculos = 0
        val caro: Int by lazy { calculos++; 42 }    // calcula na primeira leitura, uma vez só
        lateinit var nome: String
    }

    @Test
    fun `by lazy e lateinit`() {
        val s = Servico()
        assertEquals(0, s.calculos)
        assertEquals(42, s.caro)
        assertEquals(42, s.caro)
        assertEquals(1, s.calculos)
        assertFailsWith<UninitializedPropertyAccessException> { s.nome.length }
        s.nome = "ok"
        assertEquals("ok", s.nome)
    }

    data class Venda(val evento: String, val qtd: Int, val preco: Int)

    private val vendas = listOf(
        Venda("show", 2, 100),
        Venda("teatro", 1, 80),
        Venda("show", 3, 100),
    )

    @Test
    fun `coleções`() {
        assertEquals(mapOf("show" to 5, "teatro" to 1),
            vendas.groupBy { it.evento }.mapValues { (_, v) -> v.sumOf { it.qtd } })
        assertEquals(mapOf("show" to 2, "teatro" to 1), vendas.groupingBy { it.evento }.eachCount())

        val (grandes, pequenas) = vendas.partition { it.qtd >= 2 }
        assertEquals(2, grandes.size)
        assertEquals(1, pequenas.size)

        assertEquals(580, vendas.sumOf { it.qtd * it.preco })   // 200 + 80 + 300
        assertEquals("teatro", vendas.minByOrNull { it.preco }?.evento)
        assertEquals(listOf("show", "teatro"), vendas.map { it.evento }.distinct())
        assertEquals(listOf(listOf(1, 2), listOf(3, 4), listOf(5)), (1..5).chunked(2))
        assertEquals(listOf(listOf(1, 2, 3), listOf(2, 3, 4)), (1..4).windowed(3))

        val porEvento = mutableMapOf<String, MutableList<Int>>()
        for (v in vendas) porEvento.getOrPut(v.evento) { mutableListOf() }.add(v.qtd)
        assertEquals(mapOf("show" to listOf(2, 3), "teatro" to listOf(1)), porEvento)

        val ordenadas = vendas.sortedWith(compareByDescending<Venda> { it.preco }.thenBy { it.qtd })
        assertEquals(listOf(2, 3, 1), ordenadas.map { it.qtd })

        assertEquals("1, 2, 3", listOf(1, 2, 3).joinToString(", "))
        assertEquals(6, listOf(1, 2, 3).fold(0) { acc, x -> acc + x })
        assertEquals(3, (1..1_000_000).asSequence().map { it * 3 }.first { it > 2 })  // para no 1º
    }

    @Test
    fun `scope functions`() {
        assertEquals(6, "kotlin".let { it.length })                        // it, devolve o resultado
        assertEquals(42, 21.run { this * 2 })                               // this, devolve o resultado
        assertEquals("x", with(StringBuilder()) { append("x"); toString() })
        val sb = StringBuilder().apply { append("a"); append("b") }         // this, devolve o objeto
        assertEquals("ab", sb.toString())
        val log = mutableListOf<String>()
        val lista = mutableListOf(1).also { log.add("criada com $it") }     // it, devolve o objeto
        assertEquals(listOf(1), lista)
        assertEquals(listOf("criada com [1]"), log)
    }

    @Test
    fun `coroutines em paralelo estruturado`() = runTest {
        suspend fun buscar(id: Int): Int {
            delay(100)                              // no runTest o tempo é virtual: não espera de verdade
            return id * 10
        }
        val resultados = coroutineScope { (1..3).map { async { buscar(it) } }.awaitAll() }
        assertEquals(listOf(10, 20, 30), resultados)
    }

    @Test
    fun `pegadinhas`() {
        val a = Ponto(1, 2)
        val b = Ponto(1, 2)
        assertTrue(a == b)                          // == chama equals
        assertFalse(a === b)                        // === compara referência

        data class ComArray(val v: IntArray)        // o compilador avisa: equals do array é por referência
        assertNotEquals(ComArray(intArrayOf(1)), ComArray(intArrayOf(1)))
        assertContentEquals(intArrayOf(1), intArrayOf(1))

        val maximo = Int.MAX_VALUE
        assertEquals(Int.MIN_VALUE, maximo + 1)     // estoura em silêncio
        assertFailsWith<ArithmeticException> { Math.addExact(maximo, 1) }

        assertEquals(-1, -7 % 3)
        assertEquals(2, (-7).mod(3))

        assertEquals(listOf("a", "b"), "a.b".split("."))   // em Kotlin é literal; em Java seria regex
    }
}
