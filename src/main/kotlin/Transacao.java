public class Transacao {
    private long timestamp;
    private TipoTransacao tipo;
    private double valor;
    private String descricao;

    public Transacao(long timestamp, TipoTransacao tipo, double valor, String descricao) {
        this.timestamp = timestamp;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
    }

    public long getTimestamp() { return timestamp; }
    public TipoTransacao getTipo() { return tipo; }
    public double getValor() { return valor; }
    public String getDescricao() { return descricao; }
}
