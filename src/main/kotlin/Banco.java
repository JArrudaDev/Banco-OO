import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Banco {
    private List<Conta> contas;

    public Banco() {
        this.contas = new ArrayList<Conta>();
    }

    public List<Conta> getContas() {
        return contas;
    }

    public Conta buscarPorNumero(String numero) {
        for (int i = 0; i < contas.size(); i++) {
            Conta c = contas.get(i);
            if (c.getNumero().equalsIgnoreCase(numero)) return c;
        }
        return null;
    }

    public void adicionarConta(Conta conta) throws BancoValorInvalidoException {
        if (conta == null) throw new BancoValorInvalidoException("Conta invalida");
        if (buscarPorNumero(conta.getNumero()) != null) {
            throw new BancoValorInvalidoException("Ja existe conta com numero: " + conta.getNumero());
        }
        contas.add(conta);
    }

    public void depositar(String numero, double valor) throws BancoException {
        Conta c = buscarOuFalhar(numero);
        c.depositar(valor);
    }

    public void sacar(String numero, double valor) throws BancoException {
        Conta c = buscarOuFalhar(numero);
        c.sacar(valor);
    }

    public void transferir(String origem, String destino, double valor) throws BancoException {
        Conta c1 = buscarOuFalhar(origem);
        Conta c2 = buscarOuFalhar(destino);
        c1.transferirPara(c2, valor);
    }

    private Conta buscarOuFalhar(String numero) throws BancoContaNaoEncontradaException {
        Conta c = buscarPorNumero(numero);
        if (c == null) throw new BancoContaNaoEncontradaException("Conta nao encontrada: " + numero);
        return c;
    }

    public void salvar(String arquivo) throws IOException {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(arquivo));

            for (int i = 0; i < contas.size(); i++) {
                Conta c = contas.get(i);

                String tipo = c.getTipo();
                String numero = escape(c.getNumero());
                String titular = escape(c.getTitular());
                double saldo = c.getSaldo();

                double limite = 0.0;
                double taxa = 0.0;
                if (c instanceof ContaCorrente) {
                    ContaCorrente cc = (ContaCorrente) c;
                    limite = cc.getLimite();
                    taxa = cc.getTaxaSaque();
                }

                bw.write("CONTA|" + tipo + "|" + numero + "|" + titular + "|" + saldo + "|" + limite + "|" + taxa);
                bw.newLine();

                for (int t = 0; t < c.getExtrato().size(); t++) {
                    Transacao tx = c.getExtrato().get(t);
                    bw.write("TX|" + numero + "|" + tx.getTimestamp() + "|" + tx.getTipo().name()
                            + "|" + tx.getValor() + "|" + escape(tx.getDescricao()));
                    bw.newLine();
                }
            }
        } finally {
            if (bw != null) bw.close();
        }
    }

    public void carregar(String arquivo) throws IOException {
        contas.clear();

        File f = new File(arquivo);
        if (!f.exists()) return;

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String linha;

            List<String> linhasTx = new ArrayList<String>();

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.length() == 0) continue;

                if (linha.startsWith("CONTA|")) {
                    String[] p = linha.split("\\|", -1);
                    if (p.length < 7) continue;

                    String tipo = p[1];
                    String numero = unescape(p[2]);
                    String titular = unescape(p[3]);

                    double saldo = parseDoubleSafe(p[4]);
                    double limite = parseDoubleSafe(p[5]);
                    double taxa = parseDoubleSafe(p[6]);

                    Conta c;
                    if ("CC".equals(tipo)) {
                        c = new ContaCorrente(numero, titular, limite, taxa);
                    } else {
                        c = new ContaPoupanca(numero, titular);
                    }

                    if (saldo > 0) c.creditar(saldo);
                    else if (saldo < 0) c.debitar(-saldo);

                    contas.add(c);

                } else if (linha.startsWith("TX|")) {
                    linhasTx.add(linha);
                }
            }

            for (int i = 0; i < linhasTx.size(); i++) {
                String l = linhasTx.get(i);
                String[] p = l.split("\\|", -1);
                if (p.length < 6) continue;

                String numero = unescape(p[1]);
                Conta c = buscarPorNumero(numero);
                if (c == null) continue;

                long ts = parseLongSafe(p[2]);
                TipoTransacao tipoTx = TipoTransacao.valueOf(p[3]);
                double valor = parseDoubleSafe(p[4]);
                String desc = unescape(p[5]);

                c.getExtrato().add(new Transacao(ts, tipoTx, valor, desc));
            }

        } finally {
            if (br != null) br.close();
        }
    }

    private double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }

    private long parseLongSafe(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("|", "\\|");
    }

    private String unescape(String s) {
        if (s == null) return "";
        String r = s;
        r = r.replace("\\|", "|");
        r = r.replace("\\\\", "\\");
        return r;
    }
}
