fun main() {
    println("Kotlin " + KotlinVersion.CURRENT + " na JVM " + System.getProperty("java.version"))
    println(saudacao("Sympla"))
    println("Java: 2 + 3 = " + Calculadora.soma(2, 3))
}
