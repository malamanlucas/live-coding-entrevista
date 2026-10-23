# Live coding

- Java 21 e Kotlin 2.4, Gradle (`./gradlew`). Testes: JUnit 5, AssertJ, kotlin.test, MockK e Mockito.
- Código em `src/main/kotlin` e `src/main/java`; testes em `src/test/kotlin` e `src/test/java`.
- Antes de codar, escreva os casos (inclusive as bordas) como teste parametrizado.
- Mudança pequena por vez, explicada em uma linha; sem dependência nova sem pedir.
- Rodar: `./gradlew test` (tudo), `./gradlew test --tests 'ExemploTest'` (uma classe), `./gradlew run` (o `main` do Kotlin).
- Ao terminar, diga a complexidade de tempo e de espaço.
- Fluxo: branch `feat/<nome>` → teste → código → commit Conventional → `git push -u origin HEAD` → `gh pr create --fill`.
