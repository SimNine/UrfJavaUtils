package urf.huffmancompression;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.PriorityQueue;

public class QuickHuffman {
	private static int currRank = 1;

    /*
     * generates a map of characters to encodings with maximum possible compression
     * the encodings are in the form of binary strings
     */
    public static HashMap<Character, String> compressionMap(String seed) {
        if (seed == null || seed.isEmpty()) {
            throw new IllegalArgumentException();
        }

        HashMap<Character, Integer> alphabet = new HashMap<Character, Integer>();
        for (int i = 0; i < seed.length(); i++) {
            char currChar = seed.charAt(i);
            if (alphabet.containsKey(currChar)) {
                alphabet.put(currChar, alphabet.get(currChar) + 1);
            } else {
                alphabet.put(currChar, 1);
            }
        }

        if (alphabet.size() < 2) {
            throw new IllegalArgumentException();
        }

        PriorityQueue<HuffNode> minHeap = new PriorityQueue<HuffNode>();
        Iterator<Entry<Character, Integer>> entryIt = alphabet.entrySet().iterator();
        while (entryIt.hasNext()) {
            Entry<Character, Integer> currEntry = entryIt.next();
            minHeap.add(new HuffNode(currEntry.getValue(), currEntry.getKey(), currRank));
            currRank++;
        }
        
        while (minHeap.size() > 1) {
            HuffNode n1 = minHeap.poll();
            HuffNode n2 = minHeap.poll();
            HuffNode newNode = new HuffNode(n1.key + n2.key, n1, n2, currRank);
            currRank++;
            minHeap.add(newNode);
        }
        
        HuffNode topNode = minHeap.poll();

        HashMap<Character, String> ret = new HashMap<Character, String>();

        Stack<Entry<HuffNode, String>> s = new Stack<Entry<HuffNode, String>>();
        s.push(new AbstractMap.SimpleEntry<HuffNode, String>(topNode, ""));

        while (!s.isEmpty()) {
            Entry<HuffNode, String> curr = s.pop();

            if (curr.getKey().getLeftChild().isLeaf()) {
                ret.put(curr.getKey().getLeftChild().getValue(), curr.getValue() + "0");
            } else {
                s.push(new AbstractMap.SimpleEntry<HuffNode, String>(curr.getKey().getLeftChild(),
                        curr.getValue() + "0"));
            }

            if (curr.getKey().getRightChild().isLeaf()) {
                ret.put(curr.getKey().getRightChild().getValue(), curr.getValue() + "1");
            } else {
                s.push(new AbstractMap.SimpleEntry<HuffNode, String>(curr.getKey().getRightChild(),
                        curr.getValue() + "1"));
            }
        }

        return ret;
    }
    
    static class HuffNode implements Comparable<Object> {
        private int key;
        private Character value;
        private HuffNode leftChild, rightChild;
        private int rank;

        public HuffNode(int k, char val, int rank) {
            this.key = k;
            this.value = val;
            this.leftChild = null;
            this.rightChild = null;
            this.rank = rank;
        }

        public HuffNode(int k, HuffNode lc, HuffNode rc, int rank) {
            this.key = k;
            this.value = null;
            this.leftChild = lc;
            this.rightChild = rc;
            this.rank = rank;
        }

        public boolean isLeaf() {
            return (leftChild == null && rightChild == null);
        }

        public HuffNode getLeftChild() {
            return this.leftChild;
        }

        public HuffNode getRightChild() {
            return this.rightChild;
        }

		public int getKey() {
            return this.key;
        }

        public char getValue() {
            return this.value;
        }

        public int compareTo(Object given) {

            if (this.key < ((HuffNode) given).key) {
                return -1;
            } else if (this.key > ((HuffNode) given).key) {
                return 1;
            } else { // if this.key == ((HuffNode)given).key
                if (!this.isLeaf() || !((HuffNode) given).isLeaf()) {
                    if (this.rank < ((HuffNode) given).rank) {
                        return -1;
                    } else if (this.rank > ((HuffNode) given).rank) {
                        return 1;
                    } else { // if this.rank == ((HuffNode)given).rank
                        return 0; // this shouldn't be able to happen
                    }
                } else { // if both nodes are leaves
                    if (this.value < ((HuffNode) given).value) {
                        return -1;
                    } else if (this.value > ((HuffNode) given).value) {
                        return 1;
                    } else { // if this.value == ((HuffNode)given).value
                        return 0; // this shouldn't be able to happen
                    }
                }
            }
        }

        // unsure if this method is necessary for priorityqueue to function
        // properly
        public boolean equals(Object other) {
            if (this.getClass().equals(other.getClass())) {
                HuffNode o = (HuffNode) other;
                if (this.getValue() == o.getValue()) {
                    return (this.key == o.key && this.value == o.value);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }

}
