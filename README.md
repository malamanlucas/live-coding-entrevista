# live-coding-entrevista

Esqueleto para live coding: Java 21, Kotlin 2.4, Gradle 9.7, JUnit 5, AssertJ, kotlin.test, MockK,
Mockito e kotlinx-coroutines-test.

## Rodar

```bash
./gradlew test                                        # tudo
./gradlew test --tests 'estudo.KotlinEmUmaTelaTest'   # uma classe
./gradlew run                                         # o main do Kotlin
```

`src/test/kotlin/estudo/KotlinEmUmaTelaTest.kt` tem um teste por recurso do Kotlin
(null-safety, `when`, `data class`, `sealed`, coleções, scope functions, coroutines, pegadinhas).

## Fluxo de PR

1. `git switch -c feat/<nome>`
2. Teste primeiro, código depois (com o Claude Code ou o Cursor).
3. `git commit -m "feat: ..."` (Conventional Commits)
4. `git push -u origin HEAD`
5. `gh pr create --fill`: o CI roda `./gradlew test` no PR.
6. Revisão e `gh pr merge --squash --delete-branch`.
