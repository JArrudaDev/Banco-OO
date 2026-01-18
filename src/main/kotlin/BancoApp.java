import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class BancoApp {

    private static final String ARQUIVO = "banco.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Banco banco = new Banco();

        try {
            banco.carregar(ARQUIVO);
            System.out.println("Dados carregados de " + ARQUIVO + " (contas: " + banco.getContas().size() + ")");
        } catch (IOException e) {
            System.out.println("Nao foi possivel carregar arquivo: " + e.getMessage());
        }

        boolean rodando = true;
        while (rodando) {
            mostrarMenu();
            int op = lerInt(sc, "Escolha: ");

            try {
                switch (op) {
                    case 1:
                        criarConta(sc, banco);
                        break;
                    case 2:
                        depositar(sc, banco);
                        break;
                    case 3:
                        sacar(sc, banco);
                        break;
                    case 4:
                        transferir(sc, banco);
                        break;
                    case 5:
                        verSaldo(sc, banco);
                        break;
                    case 6:
                        verExtrato(sc, banco);
                        break;
                    case 7:
                        listarContas(banco);
                        break;
                    case 8:
                        salvar(banco);
                        break;
                    case 0:
                        salvar(banco);
                        System.out.println("Saindo... (salvo em " + ARQUIVO + ")");
                        rodando = false;
                        break;
                    default:
                        System.out.println("Opcao invalida!");
                }
            } catch (BancoException e) {
                System.out.println("Erro: " + e.getMessage());
            }

            System.out.println();
        }

        sc.close();
    }

    static void mostrarMenu() {
        System.out.println("==== SISTEMA BANCARIO (Java 7) ====");
        System.out.println("1 - Criar conta (corrente/poupanca)");
        System.out.println("2 - Depositar");
        System.out.println("3 - Sacar");
        System.out.println("4 - Transferir");
        System.out.println("5 - Ver saldo");
        System.out.println("6 - Ver extrato");
        System.out.println("7 - Listar contas");
        System.out.println("8 - Salvar agora");
        System.out.println("0 - Sair");
    }

    static void criarConta(Scanner sc, Banco banco) throws BancoException {
        System.out.println("Tipo de conta:");
        System.out.println("1 - Conta Corrente");
        System.out.println("2 - Conta Poupanca");
        int tipo = lerInt(sc, "Opcao: ");

        String numero = lerLinha(sc, "Numero da conta: ").trim();
        String titular = lerLinha(sc, "Titular: ").trim();

        if (tipo == 1) {
            double limite = lerDouble(sc, "Limite (cheque especial, ex: 500): ");
            double taxa = lerDouble(sc, "Taxa por saque (ex: 1.50, ou 0): ");
            banco.adicionarConta(new ContaCorrente(numero, titular, limite, taxa));
        } else if (tipo == 2) {
            banco.adicionarConta(new ContaPoupanca(numero, titular));
        } else {
            throw new BancoValorInvalidoException("Tipo de conta invalido");
        }

        System.out.println("Conta criada com sucesso!");
    }

    static void depositar(Scanner sc, Banco banco) throws BancoException {
        String numero = lerLinha(sc, "Conta: ").trim();
        double valor = lerDouble(sc, "Valor: ");
        banco.depositar(numero, valor);
        System.out.println("Deposito realizado!");
    }

    static void sacar(Scanner sc, Banco banco) throws BancoException {
        String numero = lerLinha(sc, "Conta: ").trim();
        double valor = lerDouble(sc, "Valor: ");
        banco.sacar(numero, valor);
        System.out.println("Saque realizado!");
    }

    static void transferir(Scanner sc, Banco banco) throws BancoException {
        String origem = lerLinha(sc, "Conta origem: ").trim();
        String destino = lerLinha(sc, "Conta destino: ").trim();
        double valor = lerDouble(sc, "Valor: ");
        banco.transferir(origem, destino, valor);
        System.out.println("Transferencia realizada!");
    }

    static void verSaldo(Scanner sc, Banco banco) throws BancoException {
        String numero = lerLinha(sc, "Conta: ").trim();
        Conta c = banco.getContas() != null ? banco.buscarPorNumero(numero) : null;
        if (c == null) throw new BancoContaNaoEncontradaException("Conta nao encontrada: " + numero);

        System.out.println("Titular: " + c.getTitular());
        System.out.println("Saldo: " + formatar2(c.getSaldo()));
        System.out.println("Tipo: " + c.getTipo());
        if (c instanceof ContaCorrente) {
            ContaCorrente cc = (ContaCorrente) c;
            System.out.println("Limite: " + formatar2(cc.getLimite()));
            System.out.println("Taxa saque: " + formatar2(cc.getTaxaSaque()));
        }
    }

    static void verExtrato(Scanner sc, Banco banco) throws BancoException {
        String numero = lerLinha(sc, "Conta: ").trim();
        Conta c = banco.buscarPorNumero(numero);
        if (c == null) throw new BancoContaNaoEncontradaException("Conta nao encontrada: " + numero);

        System.out.println("==== EXTRATO (" + c.getNumero() + ") ====");
        if (c.getExtrato().isEmpty()) {
            System.out.println("(sem movimentacoes)");
        } else {
            for (int i = 0; i < c.getExtrato().size(); i++) {
                Transacao tx = c.getExtrato().get(i);
                System.out.println((i + 1) + ") " + tx.getTipo()
                        + " | valor: " + formatar2(tx.getValor())
                        + " | " + tx.getDescricao()
                        + " | ts: " + tx.getTimestamp());
            }
        }
        System.out.println("Saldo atual: " + formatar2(c.getSaldo()));
    }

    static void listarContas(Banco banco) {
        List<Conta> contas = banco.getContas();
        if (contas.isEmpty()) {
            System.out.println("(nenhuma conta cadastrada)");
            return;
        }

        System.out.println("==== CONTAS ====");
        for (int i = 0; i < contas.size(); i++) {
            Conta c = contas.get(i);
            System.out.println((i + 1) + ") " + c.getNumero()
                    + " | " + c.getTitular()
                    + " | " + c.getTipo()
                    + " | saldo: " + formatar2(c.getSaldo()));
        }
    }

    static void salvar(Banco banco) {
        try {
            banco.salvar(ARQUIVO);
            System.out.println("Salvo em " + ARQUIVO);
        } catch (IOException e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
        }
    }

    static int lerInt(Scanner sc, String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("Digite um inteiro valido."); }
        }
    }

    static double lerDouble(Scanner sc, String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim().replace(",", ".");
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) { System.out.println("Digite um numero valido (ex: 10.5)."); }
        }
    }

    static String lerLinha(Scanner sc, String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }

    static String formatar2(double v) {
        return String.format(java.util.Locale.US, "%.2f", v).replace(".", ",");
    }
}
