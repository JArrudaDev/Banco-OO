public class ContaCorrente extends Conta {
    private double limite;
    private double taxaSaque;

    public ContaCorrente(String numero, String titular, double limite, double taxaSaque) {
        super(numero, titular);
        this.limite = limite;
        this.taxaSaque = taxaSaque;
    }

    public double getLimite() { return limite; }
    public double getTaxaSaque() { return taxaSaque; }

    @Override
    protected void validarSaque(double valor) throws BancoException {
        double total = valor + taxaSaque;
        double saldoApos = getSaldo() - total;
        if (saldoApos < -limite) {
            throw new BancoSaldoInsuficienteException("Saldo insuficiente. Limite CC: " + limite);
        }

        if (taxaSaque > 0) {
            debitar(taxaSaque);
            registrar(TipoTransacao.SAQUE, taxaSaque, "Taxa de saque");
        }
    }

    @Override
    public String getTipo() {
        return "CC";
    }
}
