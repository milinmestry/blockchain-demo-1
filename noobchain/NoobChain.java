package noobchain;

import com.google.gson.GsonBuilder;
import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

/**
 * noobchain.NoobChain
 *
 * https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 */
public class NoobChain {

  public static ArrayList<Block> blockchain = new ArrayList<Block>();
  // list of all unspent transactions
  public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
  public static int difficulty = 5;
  public static float minimumTransaction = 0.1f;
  public static Transaction genesisTrasaction;
  public static Wallet walletA;
  public static Wallet walletB;

  public static void main(String[] args) {
    // Set Bouncy Castle as security provider
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    // New Wallets
    walletA = new Wallet();
    walletB = new Wallet();
    Wallet coinBase = new Wallet();

    // Create a genesis transaction which send 100 noobcoins to WalletA
    genesisTrasaction = new Transaction(
      coinBase.publicKey, walletA.publicKey, 100f, null);
    genesisTrasaction.generateSignature(coinBase.priavteKey);
    genesisTrasaction.transactionId = "0"; // manually set transactionId
    genesisTrasaction.outputs.add(new TransactionOutput(
      genesisTrasaction.recipient, genesisTrasaction.value, genesisTrasaction.transactionId
    ));
    // its important to store our first transaction in the UTXOs list
    UTXOs.put(genesisTrasaction.outputs.get(0).id, genesisTrasaction.outputs.get(0));

    System.out.println("Creating and miningn genesis block.");
    Block genesis = new Block("0");
    genesis.addTransaction(genesisTrasaction);
    addBlock(genesis);

    // testing
    Block block1 = new Block(genesis.hash);
    System.out.println("\nWalletA balance=" + walletA.getBalance());
    System.out.println("Attempt to transfer funds 40 to walletB");
    block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
    addBlock(block1);

    // Test public and private keys
    System.out.println("[wallet A] public and private keys:");
    System.out.println(StringUtil.getStringFromKey(walletA.priavteKey));
    System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

    // Create a test transaction from WalletA to WalletB
    Transaction transaction = new Transaction(
      walletA.publicKey, walletB.publicKey, 5, null);
    transaction.generateSignature(walletA.priavteKey);

    // Verify signature works using public key
    System.out.println("Is signature verified?");
    System.out.println(transaction.verifySignature());


    // Add blocks to blockchain ArrayList
//    blockchain.add(new Block("Im First noobchain.Block in the chain.", "0"));
//    System.out.println("Trying to mine noobchain.Block 1.");
//    blockchain.get(0).mineBlock(difficulty);
//
//    blockchain.add(new Block(
//      "Im Second noobchain.Block in the chain.", blockchain.get(blockchain.size()-1).hash));
//    System.out.println("Trying to mine noobchain.Block 2.");
//    blockchain.get(1).mineBlock(difficulty);
//
//    blockchain.add(new Block(
//      "Im Third noobchain.Block in the chain.", blockchain.get(blockchain.size()-1).hash));
//    System.out.println("Trying to mine noobchain.Block 3.");
//    blockchain.get(2).mineBlock(difficulty);
//
//    System.out.println("\nIs Blockchain valid? " + isChainValid());
//
//    String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//    System.out.println("Json Blocks\n " + blockchainJson);

  }

  public static Boolean isChainValid() {
    Block currentBlock;
    Block previousBlock;

    int len = blockchain.size();
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');

    for (int i = 1; i < len; i++) {
      currentBlock = blockchain.get(i);
      previousBlock = blockchain.get(i - 1);

      // Compared registered hash and calculated hash
      if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
        System.out.println("Current hashes not equal.");
        return false;
      }

      // Compare previous hash and registered previous hash
      if (!previousBlock.hash.equals(currentBlock.previousHash)) {
        System.out.println("Previous hashes not equal.");
        return false;
      }

      // Check hash is solve
      if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
        System.out.println("This block hasn't been mined.");
        return false;
      }
    }
    return true;
  }

}