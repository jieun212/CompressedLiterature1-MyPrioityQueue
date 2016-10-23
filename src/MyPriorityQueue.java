/* TCSS 342 - Spring 2016
 * Assignment 3 - Compressed Literature
 * Jieun Lee
 */

import java.util.ArrayList;
import java.util.List;

/**
 * MyPriorityQueue is to support the Huffman's algorithm in CodingTree class.
 * 
 * @author Jieun Lee
 * @version 04-27-2016
 * @param <T> The type of elements held in this collection
 */
public class MyPriorityQueue<T extends Comparable<T>> {

	/**
	 * A list of elements
	 */
	private List<T> myElements;

	/**
	 * A size of MyPriorityQueue.
	 */
	private int mySize;

	/**
	 * Constructs a MyriorityQueue.
	 */
	public MyPriorityQueue() {
		myElements = new ArrayList<T>();
		myElements.add(null);
		mySize = 0;
	}

	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @param theValue The value.
	 */
	public void offer(T theValue) {
		// adds the value.
		// increases the size of this MyPriorityQueue.
		myElements.add(theValue);
		mySize++;

		// bubbles up if the value is less than its parent.
		int i = mySize;
		while (i > 1) {
			int parent = i / 2;
			if (myElements.get(i).compareTo(myElements.get(parent)) < 0) {
				T temp = myElements.get(i);
				myElements.set(i, myElements.get(parent));
				myElements.set(parent, temp);
				i = parent;
			} else {
				break;
			}
		}
	}

	/**
	 * Retrieves and removes the head of this queue, or returns null if this
	 * queue is empty.
	 * 
	 * @return The head of this queue or null if the queue is empty.
	 */
	public T poll() {
		if (isEmpty()) {
			return null;
		}

		// before removing element, swap the minimum element and the last
		// element of the list.
		T result = myElements.get(1);
		myElements.set(1, myElements.get(mySize));
		
		// removes the minimum value and decreases the size.
		myElements.remove(mySize);
		mySize--;

		// sinks down if the first element([1]) is not the minimum.
		if (!isEmpty()) {
			int i = 1;
			T element = myElements.get(1);
			while (2 * i <= mySize) {
				int left = 2 * i;
				int right = (2 * i) + 1;

				int min;
				if (left < mySize && (myElements.get(left).compareTo(myElements.get(right))) > 0) {
					min = right;
				} else {
					min = left;
				}
				if (element.compareTo(myElements.get(min)) > 0) {
					final T temp = myElements.get(i);
					myElements.set(i, myElements.get(min));
					myElements.set(min, temp);
					i = min;
				} else {
					break;
				}

			}
		}

		return result;
	}

	/**
	 * Returns the number of elements in this collection.
	 * 
	 * @return The size of this priority queue.
	 */
	public int size() {
		return mySize;
	}

	/**
	 * Returns true if there is no element in this priority queue.
	 * 
	 * @return True if there is no element in this priority queue.
	 */

	public boolean isEmpty() {
		return mySize == 0;
	}

	/**
	 * Prints list of this priority queue.
	 * 
	 * @return The list of this priority queue.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		if (isEmpty()) {
			result.append("]");
		} else {
			result.append(myElements.get(1));
			for (int i = 2; i < myElements.size(); i++) {
				result.append(", " + myElements.get(i));
			}
			result.append("]");
		}
		return result.toString();
	}

}
