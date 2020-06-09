package urf;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * Implements construction, encoding, and decoding logic of the Huffman coding
 * algorithm. Characters not in the given seed or alphabet should not be
 * compressible, and attempts to use those characters should result in the
 * throwing of an {@link IllegalArgumentException} if used in
 * {@link #compress(String)}.
 *
 * @author scheiber (Max Scheiber)
 */
public class Huffman {
    private HuffNode huffTree;
    private HashMap<Character, String> huffMap;
    private double expectedEncodingLength = -1.0;
    private int totalCompressionInput = 0;
    private int totalCompressionOutput = 0;

    private static int currRank = 1;

    /**
     * Constructs a {@code Huffman} instance from a seed string, from which to
     * deduce the alphabet and corresponding frequencies.
     * <p/>
     * Do NOT modify this constructor header.
     *
     * @param seed
     *            the String from which to build the encoding
     * @throws IllegalArgumentException
     *             seed is null, seed is empty, or resulting alphabet only has 1
     *             character
     */
    public Huffman(String seed) {
        if (seed == null || seed.isEmpty()) {
            throw new IllegalArgumentException();
        }

        HashMap<Character, Integer> alphabet = makeFreqMap(seed);

        if (alphabet.size() < 2) {
            throw new IllegalArgumentException();
        }

        huffTree = makeHuffTree(makeMinHeap(alphabet));
        huffMap = makeHuffMap(huffTree);
    }

    // generates a character-to-frequency map of the input
    public static HashMap<Character, Integer> makeFreqMap(String seed) {
        HashMap<Character, Integer> alphabet = new HashMap<Character, Integer>();
        for (int i = 0; i < seed.length(); i++) {
            char currChar = seed.charAt(i);
            if (alphabet.containsKey(currChar)) {
                alphabet.put(currChar, alphabet.get(currChar) + 1);
            } else {
                alphabet.put(currChar, 1);
            }
        }
        return alphabet;
    }

    /**
     * Constructs a {@code Huffman} instance from a frequency map of the input
     * alphabet.
     * <p/>
     * Do NOT modify this constructor header.
     *
     * @param alphabet
     *            a frequency map for characters in the alphabet
     * @throws IllegalArgumentException
     *             if the alphabet is null, empty, has fewer than 2 characters,
     *             or has any non-positive frequencies
     */
    public Huffman(Map<Character, Integer> alphabet) {
        if (alphabet == null || alphabet.isEmpty() || alphabet.size() < 2) {
            throw new IllegalArgumentException();
        }

        // checks each frequency for being non-positive
        Iterator<Entry<Character, Integer>> entryIt = alphabet.entrySet().iterator();
        while (entryIt.hasNext()) {
            if (entryIt.next().getValue() < 1) {
                throw new IllegalArgumentException();
            }
        }

        huffTree = makeHuffTree(makeMinHeap(alphabet));
        huffMap = makeHuffMap(huffTree);
    }

    // generates huffTree, a tree filled with HuffNodes
    static HuffNode makeHuffTree(PriorityQueue<HuffNode> minHeap) {
        while (minHeap.size() > 1) {
            HuffNode n1 = minHeap.poll();
            HuffNode n2 = minHeap.poll();
            HuffNode newNode = new HuffNode(n1.key + n2.key, n1, n2, currRank);
            currRank++;
            minHeap.add(newNode);
        }

        return minHeap.poll();
    }

    // helper, to test independently
    static PriorityQueue<HuffNode> makeMinHeap(Map<Character, Integer> map) {
        PriorityQueue<HuffNode> minHeap = new PriorityQueue<HuffNode>();
        Iterator<Entry<Character, Integer>> entryIt = map.entrySet().iterator();
        while (entryIt.hasNext()) {
            Entry<Character, Integer> currEntry = entryIt.next();
            minHeap.add(new HuffNode(currEntry.getValue(), currEntry.getKey(), currRank));
            currRank++;
        }
        return minHeap;
    }

    /**
     * @throws IllegalArgumentException
     *             {@inheritDoc}
     */
    public String compress(String input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (huffMap.get(input.charAt(i)) == null) {
                throw new IllegalArgumentException();
            } else {
                sb.append(huffMap.get(input.charAt(i)));
            }
        }

        totalCompressionInput += input.length() * 16;
        totalCompressionOutput += sb.toString().length();

        return sb.toString();
    }

    // helper, to be tested independently
    static HashMap<Character, String> makeHuffMap(HuffNode topNode) {
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

    /**
     * @throws IllegalArgumentException
     *             {@inheritDoc}
     */
    public String decompress(String input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }

        HuffNode currNode = huffTree;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '0') {
                if (currNode.getLeftChild().isLeaf()) {
                    sb.append(currNode.getLeftChild().getValue());
                    currNode = huffTree;
                } else {
                    currNode = currNode.getLeftChild();
                }
            } else if (input.charAt(i) == '1') {
                if (currNode.getRightChild().isLeaf()) {
                    sb.append(currNode.getRightChild().getValue());
                    currNode = huffTree;
                } else {
                    currNode = currNode.getRightChild();
                }
            } else {
                throw new IllegalArgumentException();
            }
        }

        if (currNode != huffTree) {
            throw new IllegalArgumentException();
        }

        return sb.toString();
    }

    public double expectedEncodingLength() {
        if (expectedEncodingLength > 0) {
            return expectedEncodingLength;
        } // else

        Iterator<Entry<Character, String>> it = huffMap.entrySet().iterator();

        int total = 0;
        while (it.hasNext()) {
            total += it.next().getValue().length();
        }
        expectedEncodingLength = ((double) total) / ((double) huffMap.size());
        return expectedEncodingLength;
    }

    /**
     * @throws IllegalStateException
     *             {@inheritDoc}
     */
    public double compressionRatio() {
        if (this.totalCompressionInput == 0) {
            throw new IllegalStateException();
        }

        return ((double) totalCompressionOutput) / ((double) totalCompressionInput);
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
