/* TCSS 342 - Spring 2016
 * Assignment 3 - Compressed Literature
 * Jieun Lee
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This main class is for Assignment 3 - Compressed Literature.
 * It is a controller that uses the CodingTree to compress a file
 * 
 * @author Jieun Lee
 * @version 05-06-2016
 */
public class Main {
	
	/**
	 * A test text file name.
	 */
	public static String TEST_FILE = "LesMiserables.txt";
	
	/**
	 * A sample text file name.
	 */
	public static String SAMPLE_FILE = "WarAndPeace.txt";
	
	/**
	 * Runs Compressed Literature project.
	 * 
	 * @param args The argument.
	 */
	public static void main(final String[] args) {
		
		// calls compress() to compress the input file. 
		compress(SAMPLE_FILE);
//		compress(TEST_FILE);
		
			/* File Name: WarAndPeace.txt 
			 * Original file Size: 3214.00 KB
			 * Compressed file size: 1831.00 KB 
			 * Compression ratio: 56.97 % 
			 * Elapsed time for compression: 822 milliseconds
			 * 
			 * Decoding Done! 
			 * Original file Size: 3214.00 KB 
			 * Decoded file size: 3214.00 KB
			 */
		
			/* File Name: LesMiserables.txt 
			 * Original file Size: 3246.00 KB
			 * Compressed file size: 1869.00 KB 
			 * Compression ratio: 57.58 % 
			 * Elapsed time for compression: 766 milliseconds
			 * 
			 * Decoding Done! 
			 * Original file Size: 3246.00 KB 
			 * Decoded file size: 3246.00 KB
			 */

		// tests CodingTree class.
		testCodingTree();
		
		// test MyPriorityQueue();
		testMyPriorityQueue();
	}
	
	/**
	 * Compresses a text file.
	 * 
	 * @param The String of file name to be compressed.
	 */
	public static void compress(final String fileName) {

		try {
			final File inputFile = new File(fileName);
			FileReader input = new FileReader(inputFile);

			/*
			 * Encoding.
			 */
			// records the starting time for compression.
			final long start = System.currentTimeMillis();
			
			// reads the contents of a text file into a String.
			// how to read text file char by char 
			// http://stackoverflow.com/questions/7941554/reading-in-from-text-file-character-by-character
			int r;
			StringBuilder result = new StringBuilder();
			while ((r = input.read()) != -1) {
				char ch = (char) r;
				result.append(ch);
			}
			input.close();
			
			// passes the String into the Coding Tree in order to initiate
			// Huffman's encoding procedure and generate a map of codes.
			final String message = result.toString();
			final CodingTree tree = new CodingTree(message);
			
			// produces the codes to a text file .
			FileWriter codeOutput = new FileWriter("codes.txt");
			codeOutput.write(tree.codes.toString());
			codeOutput.close();
			
			// produces the compressed message to a binary file.
			final File compressedFile = new File("compressed.txt");
			FileOutputStream compressedOutput = new FileOutputStream(compressedFile);
			// how to convert character to byte
			// https://www.javacodegeeks.com/2010/11/java-best-practices-char-to-byte-and.html
			final byte[] b = new byte[tree.bits.size()];
			for (int i = 0; i < b.length; i++) {
				b[i] = tree.bits.get(i);
			}
			compressedOutput.write(b);
			compressedOutput.close();
			
			// records the ending time for compression.
			final long end = System.currentTimeMillis();

			// gets sizes of original and compressed text files in Kilobytes.
			final double originalSize = inputFile.length() / 1024;
			final double compressedSize = compressedFile.length() / 1024;
			
			// displays compression and the elapsed time for compression statistics.
			// expected result: 
			// compressed file size = less than 1KB of 1832KB// time = less than 3 seconds.
			System.out.println("File Name: " + fileName);
			System.out.println("Original file Size: " + String.format("%.2f", originalSize) + " KB");
			System.out.println("Compressed file size: " + String.format("%.2f",compressedSize) + " KB");
			System.out.println("Compression ratio: "+ String.format("%.2f",(compressedSize*100) / originalSize) + " %");
			System.out.println("Elapsed time for compression: " + (end - start) + " milliseconds");
			
			
			
			/*
			 * Decoding.
			 */
			// reads bytes in the compressed files
			// http://stackoverflow.com/questions/858980/file-to-byte-in-java
			final byte[] bytes = Files.readAllBytes(Paths.get(new File("compressed.txt").getPath()));
			
			// how to convert byte to binary string
			// http://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
			StringBuilder str = new StringBuilder();
			for (final byte bt: bytes) {
				String st = String.format("%8s", Integer.toBinaryString(bt & 0xFF)).replace(' ', '0');
				str.append(st);
			}
			
			// how to read file to string
			// http://www.adam-bien.com/roller/abien/entry/java_8_reading_a_file
			final String bits = new String(Files.readAllBytes(Paths.get("codes.txt")));
			
			// produces the decoded text file.
			FileWriter decodedOutput = new FileWriter("decoded.txt");
			// writes the codes in codes.txt to codes Map<character, binary String>
			decodedOutput.write(tree.decode(str.toString(), tree.rebuildCodes(bits)));
			decodedOutput.close();
			
			// after decoding, checks the file sizes between original text file and decoded text file
			final File decodedFile = new File("decoded.txt");
			final double decodedSize = decodedFile.length() / 1024;
			System.out.println("\nDecoding Done! <File Name: " + fileName +">");
			System.out.println("Original file Size: " + String.format("%.2f", originalSize) + " KB");
			System.out.println("Decoded file size: " + String.format("%.2f",decodedSize) + " KB\n");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
			
	/** 
	 * Tests the CodingTree on short simple phrases and verify their correctness.
	 */
	public static void testCodingTree() {
		System.out.println("============= Tests CodingTree ===========");
		
		// empty test
		final String str0 = "";
		System.out.println("Empty message test. " + str0);
		final CodingTree tree0 = new CodingTree(str0);
		System.out.println("codes" + tree0.codes.toString());
		System.out.println("bits size " + tree0.bits.size() + ", " + tree0.bits.toString() + '\n');

		
		// tests codes data and bits data and its size
		final String str1 = "ANNA HAS A BANANA IN A BANDANA";
		System.out.println("message 1. " + str1);
		final CodingTree tree1 = new CodingTree(str1);
		System.out.println("codes" + tree1.codes.toString());
		System.out.println("bits size " + tree1.bits.size() + ", " + tree1.bits.toString() + '\n');
		
		final String str2 = "ADAM THE MAN AND DAN THE MAD ARE MAD MAD MEN";
		final CodingTree tree2 = new CodingTree(str2);
		System.out.println("message 2. " + str2);
		System.out.println("codes" + tree2.codes.toString());
		System.out.println("bits size " + tree2.bits.size() + ", " + tree2.bits.toString() + '\n');

	}
	
	/**
	 * Tests MyPriorityQueue.
	 */
	public static void testMyPriorityQueue() {
		System.out.println("============= Tests MyPriorityQueue ===========");
		
		final MyPriorityQueue<Integer> pq = new MyPriorityQueue<Integer>();
		
		// empty test
		System.out.println("Empty test: Expected: [] size: 0, Actual : " + pq.toString() + " size: " + pq.size());
		
		// tests offer()
		pq.offer(9);
		pq.offer(0);
		pq.offer(1);
		pq.offer(2);
		pq.offer(6);
		pq.offer(8);
		pq.offer(4);
		System.out.println("Expected: [0, 2, 1, 9, 6, 8, 4] size: 7");
		System.out.println("Actual  : " + pq.toString() + " size: " + pq.size());
		
		// tests poll()
		System.out.println("poll() - Expected: '0', Actual: " + pq.poll());
		
		// after poll(), checks the elements of MyPriorityQueue.
		System.out.println("Expected: [1, 2, 4, 9, 6, 8] size: 6");
		System.out.println("Actual  : " + pq.toString() + " size: " + pq.size());
		
		// tests poll() again
		System.out.println("poll() - Expected: '1', Actual: " + pq.poll());

	}

}
