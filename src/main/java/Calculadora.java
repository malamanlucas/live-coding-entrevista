public final class Calculadora {

    private Calculadora() {
    }

    public static int soma(int a, int b) {
        return Math.addExact(a, b);
    }

    public static void main(String[] args) {
        System.out.println("Java " + System.getProperty("java.version") + ": 2 + 3 = " + soma(2, 3));
    }
}
