package noobchain;

import java.security.*;
import java.util.ArrayList;

public class Transaction {

  public String transactionId; // Hash of the transaction
  public PublicKey sender;
  public PublicKey recipient;
  public float value;
  // This is to prevent anybody else from spending funds in our
  public byte[] signature;

  public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
  public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

  private static int sequence = 0; // approximate transaction count

  public Transaction(
    PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
    this.sender = from;
    this.recipient = to;
    this.value = value;
    this.inputs = inputs;
  }

  /**
   * Generate Hash value for transaction.
   *
   * @return
   */
  private String calculateHash() {
    sequence++; // increment to avoid two identical transactions

    return StringUtil.applySha256(
      StringUtil.getStringFromKey(sender) +
        StringUtil.getStringFromKey(recipient) +
        Float.toString(value) +
        sequence
    );
  }

  /**
   * Generate signature for transaction
   *
   * @param privateKey
   */
  public void generateSignature(PrivateKey privateKey) {
    String data = StringUtil.getStringFromKey(sender) +
      StringUtil.getStringFromKey(recipient) + Float.toString(value);
    signature = StringUtil.applyECDSASig(privateKey, data);
  }

  /**
   * Verify the data signed with Keys
   * @return
   */
  public boolean verifySignature() {
    String data = StringUtil.getStringFromKey(sender) +
      StringUtil.getStringFromKey(recipient) + Float.toString(value);
    return StringUtil.verifyECDSASig(sender, data, signature);
  }

  public boolean processTransaction() {
    if (verifySignature() == false) {
      System.out.println("Transaction signature failed to verify.");
      return false;
    }

    // collect inputs (unspent)
    for (TransactionInput i : inputs) {
      i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
    }

    // transaction is valid
    if (getInputsValue() < NoobChain.minimumTransaction) {
      System.out.println("Transaction inputs are small." + getInputsValue());
      return false;
    }

    // Generate transaction outputs
    float leftOver = getInputsValue() - value;
    transactionId = calculateHash();

    // send value to recipient
    outputs.add(new TransactionOutput(this.recipient, value, transactionId));
    // Send left over back to Sender
    outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

    // add outputs to unspent list
    for (TransactionOutput o : outputs) {
      NoobChain.UTXOs.put(o.id, o);
    }

    // Remove transaction from UTXO lists as spent
    for (TransactionInput i : inputs) {
      if (i.UTXO == null) continue;
      NoobChain.UTXOs.remove(i.UTXO.id);
    }
    return true;
  }

  public float getInputsValue() {
    float total = 0;
    for (TransactionInput i : inputs) {
      if (i.UTXO == null) continue;
      total += i.UTXO.value;
    }
    return total;
  }

  public float getOutputsValue() {
    float total = 0;
    for (TransactionOutput o : outputs) {
      total += i.UTXO.value;
    }
    return total;
  }
}
