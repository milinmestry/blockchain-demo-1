package noobchain;

import java.util.ArrayList;
import java.util.Date;

/**
 * noobchain.Block class
 *
 * https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 */
public class Block {

  private int nonce;
  private long timeStamp;
  public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); // simple message
  public String data;
  public String hash;
  public String merkleRoot;
  public String previousHash;

  public Block(String previousHash) {
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();
    this.hash = calculateHash();
  }

  public String calculateHash() {
    String calculatedHash = StringUtil.applySha256(
      this.previousHash +
        Long.toString(this.timeStamp) + Integer.toString(nonce) + merkleRoot);
    return calculatedHash;
  }

  /**
   *
   * @param difficulty
   */
  public void mineBlock(int difficulty) {
    merkleRoot = StringUtil.getMerkleRoot(transactions);
    String target = StringUtil.getDifficultyString(difficulty);

    while (!hash.substring(0, difficulty).equals(target)) {
      nonce++;
      hash = calculateHash();
    }
    System.out.println("noobchain.Block mined!!! : " + hash);
  }

  /**
   * Add transaction to this block
   */
  public boolean addTransaction(Transaction transaction) {
    if (transaction == null) return false;

    if (previousHash != "0") {
      if (transaction.processTransaction() != true) {
        System.out.println("Transaction failed to process. discarded!");
        return false;
      }
    }
    transactions.add(transaction);
    System.out.println("Transaction successfully added to block.");
    return true;
  }
}