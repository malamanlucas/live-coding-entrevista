import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculadoraTest {

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({"1, 1, 2", "-1, 1, 0", "0, 0, 0"})
    void soma(int a, int b, int esperado) {
        assertThat(Calculadora.soma(a, b)).isEqualTo(esperado);
    }

    @Test
    void estouroLancaExcecao() {
        assertThatThrownBy(() -> Calculadora.soma(Integer.MAX_VALUE, 1))
            .isInstanceOf(ArithmeticException.class);
    }
}
