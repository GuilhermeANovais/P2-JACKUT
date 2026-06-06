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
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us1_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us1_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us2_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us2_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us3_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us3_2.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us4_1.txt" });
        EasyAccept.main(new String[] { "br.ufal.ic.p2.jackut.Facade", "tests/us4_2.txt" });
    }
}