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
}
