// Hadi Ghahremannezhad - cs610 0206 prp

import java.io.*;
import java.util.PriorityQueue;

public class henc0206 {
    private String iFile;
    private HuffContext0206.HuffmanWriter huffmanWriter;
    private InputStream is;

    public static void main(String[] args) throws Exception {
        new henc0206(args[0]).startEncoding();
    }

    private henc0206(String inputFile) throws FileNotFoundException {
        is = new FileInputStream(inputFile);
        huffmanWriter = new HuffContext0206.HuffmanWriter(new BufferedOutputStream(new FileOutputStream(inputFile + ".huf")));
        iFile = inputFile;
    }

    private long[] getFreqTable(int[] ln) throws Exception {
        int i, b = 0x100;
        long[] freqTable = new long[b];

        while ((i = is.read()) != 0xffffffff) {
            freqTable[i]++;
            ln[0]++;
        }

        if (ln[0] == 0) {
            throw new Exception("File is empty!");
        }
        try {
            is.reset();
        } catch (IOException e) {
            is.close();
        }

        return freqTable;
    }

//    private HuffContext0206.HuffmanNode buildHeap(int[] data) {
//        PriorityQueue<HuffContext0206.HuffmanNode> heap = new PriorityQueue<>();
//
//        for (int i = 0; i < data.length; i++) {
//            int dt = data[i];
//            if (dt > 0) {
//                heap.add(new HuffContext0206.HuffmanNode(i, dt, null, null));
//            }
//        }
//
//        while (heap.size() > 1) {
//            HuffContext0206.HuffmanNode left = heap.poll();
//            HuffContext0206.HuffmanNode right = heap.poll();
//            heap.add(new HuffContext0206.HuffmanNode('_', left.count + (right != null ? right.count : 0), left, right));
//        }
//
//        return heap.poll();
//    }

    private void startEncoding() throws Exception {
        int[] ln = {0};
        int bytes;
        is = new FileInputStream(iFile);

//        build binary heap
        PriorityQueue<HuffContext0206.HuffmanNode> nodes = new PriorityQueue<>();
        int i = 0;
        long[] d = getFreqTable(ln);
        while (i < d.length) {
            long datum = d[i];
            if (datum > 0) {
                nodes.add(new HuffContext0206.HuffmanNode(i, datum, null, null));
            }
            i++;
        }
        while (nodes.size() > 1) {
            HuffContext0206.HuffmanNode left = nodes.poll();
            HuffContext0206.HuffmanNode right = nodes.poll();
            nodes.add(new HuffContext0206.HuffmanNode(-1, left.count + (right != null ? right.count : 0), left, right));
        }
        HuffContext0206.HuffmanNode heap = nodes.poll();

        String[] c = new String[256];
        createChars(c, "", heap);
        flushHeap(heap);
        huffmanWriter.write32bitInt(ln[0]);
        is = new FileInputStream(iFile);
        while ((bytes = is.read()) != -1) {
            for (char ch : c[bytes].toCharArray()) {
                huffmanWriter.writeOneBit(ch != '0');
            }
        }

        huffmanWriter.close();
        is.close();
    }

    private void flushHeap(HuffContext0206.HuffmanNode root) throws IOException {
        if (!root.leaf) {
            huffmanWriter.writeOneBit(false);
            flushHeap(root.left);
            flushHeap(root.right);
        } else {
            huffmanWriter.writeOneBit(true);
            huffmanWriter.writeOneByte(root.charByte);

        }
    }

    private void createChars(String[] strings, String strBuffer, HuffContext0206.HuffmanNode node) {
        if (node.leaf && node.charByte != -1) {
            strings[node.charByte] = strBuffer;
        } else {
            createChars(strings, strBuffer + "0", node.left);
            createChars(strings, strBuffer + "1", node.right);
        }
    }
}