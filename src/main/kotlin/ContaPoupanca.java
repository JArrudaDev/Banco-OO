public class ContaPoupanca extends Conta {

    public ContaPoupanca(String numero, String titular) {
        super(numero, titular);
    }

    @Override
    protected void validarSaque(double valor) throws BancoException {
        if (getSaldo() - valor < 0) {
            throw new BancoSaldoInsuficienteException("Saldo insuficiente na poupanca (nao permite negativo).");
        }
    }

    @Override
    public String getTipo() {
        return "PP";
    }
}
