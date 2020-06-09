package urf;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.junit.Test;

public class HuffmanTest {

    @Test
    public void testHuffmanString() {
        // this test only to make sure no errors are thrown
        // all methods that the constructor uses are tested independently
        new Huffman("whao dude this is a CRAaazZZYy string");
        new Huffman("&@(*#)&(*&@#%^&%^#*!%&^(*&)(_()$()#&)(");
        new Huffman("+_+-=-=0=-987(%&%8725+^%90ua9IUDoghg97ta9ngwint27");
    }

    @Test
    public void testHuffmanStringAndCatchNull() {
        try {
            String test = null;
            new Huffman(test);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testHuffmanStringAndCatchEmpty() {
        try {
            String test = "";
            new Huffman(test);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testHuffmanStringAndCatchLessThanTwoCharacters() {
        try {
            String test = "bbbbbbb";
            new Huffman(test);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testHuffmanMapOfFrequencies() {
        // this test only to make sure no errors are thrown - all methods that
        // the constructor uses are tested independently
        new Huffman(Huffman.makeFreqMap("98023u098jida  ohdoaiuwehuiho"));
        new Huffman(Huffman.makeFreqMap("HHHHHHHHHHHHHSE"));
        new Huffman(Huffman.makeFreqMap("verrrrrrrrrrrrry goof string thing"));
    }

    @Test
    public void testHuffmanMapOfFrequenciesAndCatchNull() {
        try {
            Map<Character, Integer> m = null;
            new Huffman(m);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testHuffmanMapOfFrequenciesAndCatchEmpty() {
        try {
            new Huffman(Huffman.makeFreqMap(""));
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testHuffmanMapOfFrequenciesAndCatchLessThanTwoCharacters() {
        try {
            new Huffman(Huffman.makeFreqMap("aaaaaaaaa"));
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testHuffmanMapOfFrequenciesAndCatchNonpositiveFreqs() {
        try {
            Map<Character, Integer> m = Huffman.makeFreqMap("testing");
            m.put('s', -1);
            new Huffman(m);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testCreateFreqMap() {
        Map<Character, Integer> m1 = Huffman.makeFreqMap("this");
        assertEquals(4, m1.size());
        assertTrue(m1.containsKey('t'));
        assertTrue(m1.containsKey('h'));
        assertTrue(m1.containsKey('i'));
        assertTrue(m1.containsKey('s'));
        assertFalse(m1.containsKey('w'));
        assertFalse(m1.containsKey(' '));
        assertEquals(1, (int) m1.get('t'));
        assertEquals(1, (int) m1.get('h'));
        assertEquals(1, (int) m1.get('i'));
        assertEquals(1, (int) m1.get('s'));

        Map<Character, Integer> m2 = Huffman.makeFreqMap("a dog a pagoda");
        assertEquals(6, m2.size());
        assertTrue(m2.containsKey('a'));
        assertTrue(m2.containsKey(' '));
        assertTrue(m2.containsKey('d'));
        assertTrue(m2.containsKey('o'));
        assertTrue(m2.containsKey('g'));
        assertTrue(m2.containsKey('p'));
        assertFalse(m2.containsKey('w'));
        assertFalse(m2.containsKey('A'));
        assertEquals(4, (int) m2.get('a'));
        assertEquals(3, (int) m2.get(' '));
        assertEquals(2, (int) m2.get('d'));
        assertEquals(2, (int) m2.get('o'));
        assertEquals(2, (int) m2.get('g'));
        assertEquals(1, (int) m2.get('p'));

        Map<Character, Integer> m3 = Huffman.makeFreqMap("abbraacrrr");
        assertEquals(4, m3.size());
        assertTrue(m3.containsKey('a'));
        assertTrue(m3.containsKey('b'));
        assertTrue(m3.containsKey('r'));
        assertTrue(m3.containsKey('c'));
        assertFalse(m3.containsKey('B'));
        assertFalse(m3.containsKey(' '));
        assertEquals(3, (int) m3.get('a'));
        assertEquals(2, (int) m3.get('b'));
        assertEquals(4, (int) m3.get('r'));
        assertEquals(1, (int) m3.get('c'));
    }

    @Test
    public void testMakeMinHeap() {
        PriorityQueue<Huffman.HuffNode> minHeap = Huffman.makeMinHeap(Huffman.makeFreqMap("abbraacrrr"));
        // using same data as above

        assertEquals(4, minHeap.size());

        Huffman.HuffNode curr = minHeap.poll();
        assertEquals('c', (char) curr.getValue());
        assertEquals(1, curr.getKey());

        curr = minHeap.poll();
        assertEquals('b', (char) curr.getValue());
        assertEquals(2, curr.getKey());

        curr = minHeap.poll();
        assertEquals('a', (char) curr.getValue());
        assertEquals(3, curr.getKey());

        curr = minHeap.poll();
        assertEquals('r', (char) curr.getValue());
        assertEquals(4, curr.getKey());
    }

    @Test
    public void testMakeHuffTree() {
        Huffman.HuffNode currNode = Huffman
                .makeHuffTree(Huffman.makeMinHeap(Huffman.makeFreqMap("abbraacrrr")));
        // using same data as above

        assertTrue(currNode.getLeftChild().isLeaf());
        assertFalse(currNode.getRightChild().isLeaf());
        assertEquals('r', (char) currNode.getLeftChild().getValue());

        currNode = currNode.getRightChild();
        assertTrue(currNode.getLeftChild().isLeaf());
        assertFalse(currNode.getRightChild().isLeaf());
        assertEquals('a', (char) currNode.getLeftChild().getValue());

        currNode = currNode.getRightChild();
        assertTrue(currNode.getLeftChild().isLeaf());
        assertTrue(currNode.getRightChild().isLeaf());
        assertEquals('c', (char) currNode.getLeftChild().getValue());
        assertEquals('b', (char) currNode.getRightChild().getValue());
    }

    @Test
    public void testMakeHuffMap() {
        HashMap<Character, String> map = Huffman.makeHuffMap(
                Huffman.makeHuffTree(Huffman.makeMinHeap(Huffman.makeFreqMap("abbraacrrr"))));
        // using same data as above

        assertEquals("0", map.get('r'));
        assertEquals("10", map.get('a'));
        assertEquals("110", map.get('c'));
        assertEquals("111", map.get('b'));
    }

    @Test
    public void testCompressShort() {
        Huffman huff = new Huffman("abbraacrrr");

        String expected = "111";
        String actual = huff.compress("b");
        assertEquals(expected, actual);
    }

    @Test
    public void testCompressMedium() {
        Huffman huff = new Huffman("abbraacrrr");

        String expected = "111010110";
        String actual = huff.compress("brac");
        assertEquals(expected, actual);
    }

    @Test
    public void testCompressLong() {
        Huffman huff = new Huffman("abbraacrrr");

        String expected = "101110101101011110111010";
        String actual = huff.compress("abracababra");
        assertEquals(expected, actual);
    }

    @Test
    public void testCompressAndCatchNull() {
        Huffman huff = new Huffman("abbraacrrr");

        try {
            String nullStr = null;
            huff.compress(nullStr);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testCompressAndCatchNotInDictionary() {
        Huffman huff = new Huffman("abbraacrrr");

        try {
            huff.compress("abracadabra");
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testDecompress() {
        Huffman huff = new Huffman("abbraacrrr");
        assertEquals("brac", huff.decompress("111010110"));
        assertEquals("abracababra", huff.decompress("101110101101011110111010"));
    }

    @Test
    public void testDecompressAndCatchNull() {
        Huffman huff = new Huffman("abbraacrrr");

        try {
            String str = null;
            huff.decompress(str);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testDecompressAndCatchNonbinaryChar() {
        Huffman huff = new Huffman("abbraacrrr");

        try {
            huff.decompress("111010q110");
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testDecompressAndCatchInvalidBinarySequence() {
        Huffman huff = new Huffman("abbraacrrr");

        try {
            huff.decompress("1110101101");
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testCompressionRatio() {
        Huffman huff = new Huffman("abbraacrrr");

        String str1 = "brac";
        String out1 = huff.compress(str1);
        String str2 = "abracababra";
        String out2 = huff.compress(str2);

        double totalIn = str1.length() * 16 + str2.length() * 16;
        double totalOut = out1.length() + out2.length();

        // there should theoretically be no difference, but this assert requires
        // delta
        assertEquals(totalOut / totalIn, huff.compressionRatio(), 0.01);
    }

    @Test
    public void testCompressionRatioCatchNoPreviousCompressions() {
        Huffman huff = new Huffman("abbraacrrr");

        try {
            huff.compressionRatio();
        } catch (IllegalStateException e) {
            return;
        }
        fail("Exception not caught");
    }

    @Test
    public void testExpectedEncodingLength() {
        Huffman huff = new Huffman("abbraacrrr");
        // there should theoretically be no difference, but this assert requires
        // delta
        assertEquals((9.0 / 4.0), huff.expectedEncodingLength(), 0.01);
    }

}
