// Hadi Ghahremannezhad - cs610 0206 prp

import java.io.*;


class HuffContext0206 {
    static class HuffmanNode implements Comparable<HuffmanNode> {
        HuffmanNode left, right;
        int charByte;
        long count;
        boolean leaf;

        @Override
        public int compareTo(HuffmanNode node) {
            return getComparedValue(node);
        }

        public HuffmanNode(int charByte, long count, HuffmanNode left, HuffmanNode right) {
            leaf = right == null || left == null;
            this.left = left;
            this.right = right;
            this.charByte = charByte;
            this.count = count;
        }

        int getComparedValue(HuffmanNode node) {
            return (int) (count - node.count);
        }
    }

    static class HuffmanWriter {
        BufferedOutputStream outputStream;
        int currentValue = 0, bitFilled = 0;

        HuffmanWriter(BufferedOutputStream stream) {
            this.outputStream = stream;
        }

        void writeOneByte(int n) throws IOException {
            if (bitFilled == 0) {
                outputStream.write(n);
                return;
            }
            int i = 0;
            while (i < 8) {
                boolean b;
                b = ((n >>> (0b1000 - i - 0b1)) & 0b1) == 1;
                writeOneBit(b);
                i++;
            }
        }

        private void flushByteBuffer() {
            if (bitFilled == 0) return;
            if (bitFilled > 0) currentValue <<= (8 - bitFilled);
            try {
                outputStream.write(currentValue);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitFilled = 0;
            currentValue = 0;
        }

        void writeOneBit(boolean b) {
            currentValue <<= 1;
            if (b) currentValue |= 1;
            bitFilled++;
            if (bitFilled == 8) flushByteBuffer();
        }

        void write32bitInt(int i) throws IOException {
            writeOneByte(0b11111111 & (i >>> 0x18));
            writeOneByte(0b11111111 & (i >>> 0x10));
            writeOneByte(0b11111111 & (i >>> 0x8));
            writeOneByte((i) & 0b11111111);
        }

        void close() throws IOException {
            flushByteBuffer();
            outputStream.flush();
            outputStream.close();
        }
    }

    static class HuffmanReader {
        private int currentValue, bitsRem;
        private FileInputStream fileInputStream;

        HuffmanReader(FileInputStream fileInputStream) {
            currentValue = bitsRem = 0;
            this.fileInputStream = fileInputStream;
            fillBuffer();
        }

        void close() throws IOException {
            fileInputStream.close();
        }

        void fillBuffer() {
            try {
                currentValue = fileInputStream.read();
                if (currentValue < 0) {
                    bitsRem = 0xffffffff;
                } else bitsRem = 0x8;
            } catch (IOException e) {
                bitsRem = -1;
            }
        }

        int readOneBit() {
            if (bitsRem != -1) {
                bitsRem--;
                int bit = (currentValue >> bitsRem & 1);
                if (bitsRem == 0) {
                    fillBuffer();
                }
                return bit;
            } else {
                return bitsRem;
            }
        }

        int readOneByte() {
            if (bitsRem == 0xffffffff) {
                return bitsRem;
            }

            if (bitsRem == 0x8) {
                int i = currentValue;
                fillBuffer();
                return (char) (i & 0xff);
            }

            int bitUsed = 0x8 - bitsRem, i = (currentValue & (0xff >>> bitUsed)) << bitUsed, i1 = bitsRem;
            fillBuffer();
            if (bitsRem == 0xffffffff) {
                return i;
            }
            bitsRem = i1;
            i |= (currentValue >>> bitsRem);
            return (char) (i & 0xff);
        }

        int read32BitInt() {
            int n = 0x0, i = 0;
            while (i < 4) {
                int i1 = readOneByte();
                n <<= 0b1000;
                n |= i1;
                i++;
            }
            return n;
        }
    }
}
