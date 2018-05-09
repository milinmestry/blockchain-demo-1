import java.util.Date;

/**
 * Block class
 *
 * @see https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 */
public class Block {

  private long timeStamp;
  private int nonce;
  public String data;
  public String hash;
  public String previousHash;

  public Block(String data, String previousHash) {
    this.data = data;
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();
    this.hash = calculateHash();
  }

  public String calculateHash() {
    String calculatedHash = StringUtil.applySha256(
      this.previousHash + Long.toString(this.timeStamp) + Integer.toString(nonce) + data
    );
    return calculatedHash;
  }

  public void mineBlock(int difficulty) {
    String target = new String(new char[difficulty]).replace('\0', '0');

    while (!hash.substring(0, difficulty).equals(target)) {
      nonce++;
      hash = calculateHash();
    }
    System.out.println("Block mined!!! : " + hash);
  }
}