package noobchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
  public PrivateKey priavteKey;
  public PublicKey publicKey;
  // UTXOs owned by this wallet
  public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

  public Wallet() {
    generateKeyPair();
  }

  public void generateKeyPair() {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

      // Initialize the key generator and generate the key pair
      keyGen.initialize(ecSpec, secureRandom);
      KeyPair keyPair = keyGen.generateKeyPair();

      // Set the public and private keys
      priavteKey = keyPair.getPrivate();
      publicKey = keyPair.getPublic();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public float getBalance() {
    float total = 0;
    for (Map.Entry<String, TransactionOutput> item : NoobChain.UTXOs.entrySet()) {
      TransactionOutput UTXO = item.getValue();

      if (UTXO.isMine(publicKey)) { // Is coins is/are belongs to me
        UTXOs.put(UTXO.id, UTXO); // unspent transaction
        total += UTXO.value;
      }
    }
    return total;
  }

  /**
   * Generate and return new transaction from wallet
   */
  public Transaction sendFunds(PublicKey recipient, float value) {
    if (getBalance() < value) {
      System.out.println("Not enough funds to send; Trsansaction discarded!");
      return null;
    }

    // List of inputs
    ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    float total = 0;

    for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
      TransactionOutput UTXO = item.getValue();
      total += UTXO.value;
      inputs.add(new TransactionInput(UTXO.id));
      if (total > value) break;
    }

    Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
    newTransaction.generateSignature(priavteKey);

    for (TransactionInput input : inputs) {
      UTXOs.remove(input.transactionOutputId);
    }
    return newTransaction;
  }
}
