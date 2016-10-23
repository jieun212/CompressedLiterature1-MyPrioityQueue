/* TCSS 342 - Spring 2016
 * Assignment 3 - Compressed Literature
 * Jieun Lee
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CodingTree class implements Huffman's coding algorithm for compressing data
 * based on character frequency in a message.
 * 
 * @author Jieun Lee
 * @version 05-06-2016
 */
public class CodingTree {

	/* fields */

	/**
	 * A data member that is a map of characters in the message to binary codes.
	 */
	public Map<Character, String> codes;

	/**
	 * A data member that is message encoded using the Huffman codes.
	 */
	public List<Byte> bits;

	/**
	 * A data member that is a map of characters to frequency of the character.
	 */
	private Map<Character, Integer> myCharFrequencyMap;

	/* constructor */

	/**
	 * Constructor that takes the text of a message to be compressed.
	 * 
	 * @param message The message.
	 */
	public CodingTree(final String message) {
		myCharFrequencyMap = new HashMap<Character, Integer>();
		codes = new HashMap<Character, String>();
		bits = new ArrayList<Byte>();
		countFrequency(message);
		compressedEncoding(message);
	}

	/* For encoding */

	/**
	 * Counts frequency of character in the given message.
	 * 
	 * @param message The message.
	 * @return The map of character in the message to counts of frequency.
	 */
	private void countFrequency(final String message) {
		if (message == null || message.isEmpty()) {
			return;
		}
		final List<Character> keyList = new ArrayList<Character>();
		for (int i = 0; i < message.length(); i++) {
			char ch = message.charAt(i);

			int count;
			if (myCharFrequencyMap.containsKey(ch)) {
				count = myCharFrequencyMap.get(ch) + 1;
				myCharFrequencyMap.put(ch, count);
			} else {
				count = 1;
				myCharFrequencyMap.put(ch, count);
				keyList.add(ch);
			}
		}
		buildTree(keyList);
	}

	/**
	 * Build HuffmanTree for each character with a non-zero count.
	 */
	private void buildTree(final List<Character> keyList) {

		// To handle selecting the minimum weight tree using MyPriorityQueue.
		final MyPriorityQueue<Node> pq = new MyPriorityQueue<Node>();
		for (int i = 0; i < keyList.size(); i++) {
			pq.offer(new Node(myCharFrequencyMap.get(keyList.get(i)), keyList.get(i)));
		}

		// Merges 2 trees with minimum weight into a single tree with weight
		// equal to the sum of 2 tree weights
		// by creating a new root and adding two trees as L and R sub tree
		Node root;
		while (pq.size() != 1) { // repeating this step until there's only a
									// single tree
			final Node left = pq.poll();
			final Node right = pq.poll();
			root = new Node(left.myFrequency + right.myFrequency, ' ', left, right);
			pq.offer(root);
		}

		labeling(pq.poll(), "");
	}

	/**
	 * Labels the single tree's left branch with "0", and right branch with "1".
	 * 
	 * @param node The single tree.
	 * @param label "0" or "1".
	 */
	private void labeling(final Node node, final String label) {
		if (node == null) {
			return;
		}
		if (node != null && node.myLeftNode == null && node.myRightNode == null) {
			codes.put(node.myData, label);
		}
		labeling(node.myRightNode, label + "1");
		labeling(node.myLeftNode, label + "0");
	}

	/**
	 * Creates a compressed encoding of the given message using the codes for
	 * each character.
	 * 
	 * @param message The message.
	 */
	private void compressedEncoding(final String message) {
		if (message == null || message.isEmpty()) {
			return;
		}
		StringBuilder str = new StringBuilder();

		for (int i = 0; i < message.length(); i++) {
			str.append(codes.get(message.charAt(i)));

			while (str.length() >= 8) {
				// how to convert binary string to byte.
				// http://stackoverflow.com/questions/13694312/converting-string-type-binary-number-to-bit-in-java
				int charByte = Integer.parseInt(str.substring(0, 8), 2);
				bits.add((byte) charByte);
				str.delete(0, 8);
			}
		}
	}

	/* For decoding */

	/**
	 * Takes the output of the encoding and produces the original message.
	 * 
	 * @param bits The binary String.
	 * @param codes The codes Map
	 * @return String of the original message.
	 */
	public String decode(final String bits, final Map<Character, String> codes) {
		final StringBuilder result = new StringBuilder();

		// switches the key of its character and the value of binary string to
		// get the character in the codes map.
		// how to get key of map from value
		// http://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key-from-value
		final Map<String, Character> binaryChar = new HashMap<String, Character>();
		for (Map.Entry<Character, String> e : codes.entrySet()) {
			binaryChar.put(e.getValue(), e.getKey());
		}

		StringBuilder str = new StringBuilder();
		for (int i = 0; i < bits.length(); i++) {
			str.append(bits.charAt(i));

			// if the binary string equals to the binary string in the bits,
			// it appends the character of the binary string to the result.
			if (binaryChar.containsKey(str.toString())) {
				result.append(binaryChar.get(str.toString()));
				str.delete(0, str.length());
			}
		}
		return result.toString();
	}

	/**
	 * creates a map for codes.
	 * 
	 * @param compressedText The String from codes text file.
	 * @return The Map of decoded codes.
	 */
	public Map<Character, String> rebuildCodes(final String codesText) {

		final Map<Character, String> decodedCodes = new HashMap<Character, String>();
		StringBuilder temp = new StringBuilder();

		for (int i = 0; i < codesText.length(); i++) {
			char ch = codesText.charAt(i);

			// if the character in the codeText is '=' and next is '0' or '1',
			// then i-1 is a character to be printed.
			if (codesText.charAt(i) == '=' && (codesText.charAt(i + 1) == '0' || codesText.charAt(i + 1) == '1')) {
				final char key = codesText.charAt(i - 1);
				i++;
				ch = codesText.charAt(i);

				// adds the binary to the value of map
				while (ch == '0' || ch == '1') {
					temp.append(ch);
					i++;
					ch = codesText.charAt(i);
				}
				decodedCodes.put(key, temp.toString());
				temp.delete(0, temp.length());
			}
		}

		return decodedCodes;
	}
	

	/* inner class */

	/**
	 * Inner Node class for CodingTree.
	 * 
	 * @author Jieun Lee
	 */
	private class Node implements Comparable<Node> {

		/**
		 * A data.
		 */
		private Character myData;

		/**
		 * Left Node.
		 */
		private Node myLeftNode;

		/**
		 * Right Node.
		 */
		private Node myRightNode;

		/**
		 * Frequency of occurrence of character.
		 */
		private int myFrequency;

		/**
		 * Constructs new Node with character and character's frequency.
		 * 
		 * @param theData The character
		 * @param theFrequency The frequency of the character
		 */
		public Node(final int theFrequency, final Character theData) {
			this(theFrequency, theData, null, null);
		}

		/**
		 * Full constructor.
		 * 
		 * @param theData The character
		 * @param theFrequency The frequency of the character
		 * @param theLeft The left child node
		 * @param theRight The right child node
		 */
		public Node(final int theFrequency, final Character theData, final Node theLeft, final Node theRight) {
			myData = theData;
			myFrequency = theFrequency;
			myLeftNode = theLeft;
			myRightNode = theRight;
		}

		/**
		 * Compares this object with the specified object for order.
		 * 
		 * @param theOther The other Node.
		 */
		@Override
		public int compareTo(final Node theOther) {
			if (theOther == null) {
				throw new IllegalArgumentException();
			}
			return myFrequency - theOther.myFrequency;
		}

		/**
		 * Prints a character and its frequency.
		 */
		@Override
		public String toString() {
			return "[" + myData + ", " + myFrequency + "]";
		}

	}

}
