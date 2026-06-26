import easyaccept.EasyAccept;

/**
 * Ponto de entrada da aplicação para execução dos testes de aceitação via EasyAccept.
 *
 * <p>Esta classe está intencionalmente no pacote <em>default</em> para simplificar
 * a invocação via linha de comando ({@code java -cp ... Main}). Ela não faz parte
 * da lógica de negócio do sistema Jackut.</p>
 */
public class Main {
    public static void main(String[] args) {
        // Milestone 1 — US1 a US4
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us1_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us1_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us2_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us2_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us3_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us3_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us4_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us4_2.txt" });

        // Milestone 2 — US5 a US9
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us5_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us5_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us6_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us6_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us7_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us7_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us8_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us8_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us9_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us9_2.txt" });
    }
}