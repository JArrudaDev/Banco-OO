import java.util.ArrayList;
import java.util.List;

public abstract class Conta {
    private String numero;
    private String titular;
    private double saldo;
    private List<Transacao> extrato;

    protected Conta(String numero, String titular) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = 0.0;
        this.extrato = new ArrayList<Transacao>();
    }

    public String getNumero() { return numero; }
    public String getTitular() { return titular; }
    public double getSaldo() { return saldo; }
    public List<Transacao> getExtrato() { return extrato; }

    protected void creditar(double valor) {
        this.saldo += valor;
    }

    protected void debitar(double valor) {
        this.saldo -= valor;
    }

    protected void registrar(TipoTransacao tipo, double valor, String descricao) {
        extrato.add(new Transacao(System.currentTimeMillis(), tipo, valor, descricao));
    }

    public void depositar(double valor) throws BancoValorInvalidoException {
        if (valor <= 0) throw new BancoValorInvalidoException("Valor de deposito deve ser > 0");
        creditar(valor);
        registrar(TipoTransacao.DEPOSITO, valor, "Deposito");
    }

    public void sacar(double valor) throws BancoException {
        if (valor <= 0) throw new BancoValorInvalidoException("Valor de saque deve ser > 0");
        validarSaque(valor); // hook para regras específicas
        debitar(valor);
        registrar(TipoTransacao.SAQUE, valor, "Saque");
    }

    public void transferirPara(Conta destino, double valor) throws BancoException {
        if (destino == null) throw new BancoContaNaoEncontradaException("Conta destino nao encontrada");
        if (this.numero.equals(destino.getNumero())) throw new BancoValorInvalidoException("Nao pode transferir para a mesma conta");
        if (valor <= 0) throw new BancoValorInvalidoException("Valor de transferencia deve ser > 0");

        validarSaque(valor);
        debitar(valor);
        registrar(TipoTransacao.TRANSFERENCIA_ENVIO, valor, "Transferencia para " + destino.getNumero());

        destino.creditar(valor);
        destino.registrar(TipoTransacao.TRANSFERENCIA_RECEBIMENTO, valor, "Transferencia de " + this.numero);
    }

    protected abstract void validarSaque(double valor) throws BancoException;

    public abstract String getTipo();
}
