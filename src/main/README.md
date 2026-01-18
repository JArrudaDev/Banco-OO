# Sistema Bancário — Console + OOP + Persistência

Projeto para praticar Orientação a Objetos em Java.

## Funcionalidades
- Criar conta:
    - Conta Corrente (CC): possui limite (cheque especial) e taxa por saque (opcional)
    - Conta Poupança (PP): não permite saldo negativo
- Depositar
- Sacar
- Transferir entre contas
- Ver saldo
- Ver extrato (histórico de transações)

## Conceitos praticados
- Encapsulamento:
    - saldo só é alterado por métodos controlados (depositar/sacar/transferir)
- Herança e polimorfismo:
    - `Conta` (base) -> `ContaCorrente` e `ContaPoupanca`
    - regra de saque fica em `validarSaque()` e muda por tipo de conta
- Coleções:
    - `ArrayList` para armazenar contas e extrato
- Exceções personalizadas:
    - `BancoValorInvalidoException`, `BancoSaldoInsuficienteException`, `BancoContaNaoEncontradaException`

## Estrutura de arquivos
- BancoApp (menu console)
- Banco (regras do banco + persistência)
- Conta (classe abstrata com operações)
- ContaCorrente (limite + taxa de saque)
- ContaPoupanca (sem negativo)
- Transacao / TipoTransacao (extrato)
- Exceções: BancoException e subclasses

## Como executar
1. Compile todos os arquivos:
    - Pela IDE
