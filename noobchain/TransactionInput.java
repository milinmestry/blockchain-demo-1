package noobchain;

public class TransactionInput {

  public String transactionOutputId; // Reference to TransactionOutputs -> transactionId
  public TransactionOutput UTXO; // Unspent transactions output

  public TransactionInput(String transactionOutputId) {
    this.transactionOutputId = transactionOutputId;
  }
}
