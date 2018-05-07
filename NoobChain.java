import java.util.Date;

/**
 * NoobChain
 *
 * @see https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 */
public class NoobChain {

  public static void main(String[] args) {
    Block genesisBlock = new Block("Im First Block in the chain.", "0");
    System.out.println("Hash for Block-1 : " + genesisBlock.hash);

    Block secondBlock = new Block(
      "Im Second Block in the chain.", genesisBlock.hash
    );
    System.out.println("Hash for Block-2 : " + secondBlock.hash);

    Block thirdBlock = new Block(
      "Im Third Block in the chain.", secondBlock.hash
    );
    System.out.println("Hash for Block-3 : " + thirdBlock.hash);
  }

}