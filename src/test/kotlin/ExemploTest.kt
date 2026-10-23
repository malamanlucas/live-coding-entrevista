import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExemploTest {

    @ParameterizedTest(name = "\"{0}\" vira \"{1}\"")
    @CsvSource("Ana, 'Olá, Ana!'", "'  Bia  ', 'Olá, Bia!'")
    fun `cumprimenta pelo nome sem os espaços`(nome: String, esperado: String) {
        assertEquals(esperado, saudacao(nome))
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   "])
    fun `recusa nome vazio`(nome: String) {
        assertFailsWith<IllegalArgumentException> { saudacao(nome) }
    }
}
