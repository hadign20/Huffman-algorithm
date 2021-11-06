// Hadi Ghahremannezhad - cs610 0206 prp

import java.io.*;

public class hdec0206 {
    private HuffContext0206.HuffmanReader huffmanReader;
    private HuffContext0206.HuffmanWriter huffmanWriter;

    public static void main(String[] args) throws Exception {
        new hdec0206(args[0]).startDecoding();
    }

    private hdec0206(String s) throws FileNotFoundException {
        String outputFile;
        outputFile = s.replace(".huf", "");
        huffmanReader = new HuffContext0206.HuffmanReader(new FileInputStream(s));
        huffmanWriter = new HuffContext0206.HuffmanWriter(new BufferedOutputStream(new FileOutputStream(outputFile)));
    }

    private void startDecoding() throws Exception {
        HuffContext0206.HuffmanNode heap = readHeap();
        int anInt = huffmanReader.read32BitInt();

        if (anInt > 0) {
            int i = 0;
            do {
                HuffContext0206.HuffmanNode root = heap;

                while (!root.leaf) {
                    i = huffmanReader.readOneBit();
                    if (i == 0xffffffff) {
                        break;
                    }

                    if (i == 0b0) {
                        root = root.left;
                    } else {
                        root = root.right;
                    }
                }
                anInt--;

                huffmanWriter.writeOneByte(root.charByte);
            } while (anInt > 0);
        }
        close();
    }

    private HuffContext0206.HuffmanNode readHeap() {
        if (huffmanReader.readOneBit() != 0b1) {
            return new HuffContext0206.HuffmanNode(0xffffffff, 0xffffffff, readHeap(), readHeap());
        } else {
            return new HuffContext0206.HuffmanNode(huffmanReader.readOneByte(), 0xffffffff, null, null);
        }
    }

    private void close() throws IOException {
        huffmanWriter.close();
        huffmanReader.close();
    }
}
