package frogma;

import java.io.FileOutputStream;

public class ByteBuffer {
    private short[] buf;
    private int size;
    private int curPos;
    private boolean hasBeenErrors;
    private boolean expandable = true;

    public static final boolean DEBUG = false;

    public ByteBuffer(int size) {
        if (size < 1) {
            size = 1;
        }
        buf = new short[size];
        this.size = size;
        curPos = 0;
        hasBeenErrors = false;
    }

    ByteBuffer(byte[] content) {
        try {
            buf = new short[content.length];
            for (int i = 0; i < content.length; i++) {
                buf[i] = (short) (content[i] & 255);
            }
            size = content.length;
            curPos = 0;
            hasBeenErrors = false;
        } catch (Exception e) {
            System.out.println("ByteBuffer: Couldn't create buffer from empty array.");
        }
    }

    void setExpandable(boolean exp) {
        expandable = exp;
    }

    public byte[] getBytes() {
        byte[] ret = new byte[buf.length];
        for (int i = 0; i < buf.length; i++) {
            ret[i] = (byte) buf[i];
        }
        return ret;
    }

    public int getSize() {
        return this.size;
    }

    private void error() {
        hasBeenErrors = true;
    }

    public boolean hasHadErrors() {
        return hasBeenErrors;
    }

    private void resize(int newSize) {
        short[] newBuf = new short[newSize];
        System.arraycopy(buf, 0, newBuf, 0, Math.min(buf.length, newSize));
        buf = newBuf;
        size = newSize;
    }

    void gotoStart() {
        curPos = 0;
    }

    public void move(int howFar) {
        curPos += howFar;
        if (curPos < 0) {
            curPos = 0;
        }
        if (curPos >= size) {
            curPos = size - 1;
        }
    }

    private boolean inRange(int pos) {
        if (expandable) {
            if (pos < 0) {
                return false;
            } else if (pos >= size) {
                resize(pos + 1);
                return true;
            } else {
                return true;
            }
        } else {
            return (pos >= 0 && pos < size);
        }
    }

    private boolean inRange(int pos, int length) {
        if (expandable) {
            if (pos < 0 || length <= 0) {
                return false;
            } else if ((pos + length - 1) >= size) {
                resize(pos + length);
                return true;
            } else {
                return true;
            }
        } else {
            return (pos >= 0 && pos + (length - 1) < size && length > 0);
        }
    }

    public void putByte(short var) {
        if (inRange(curPos, 1)) {
            buf[curPos] = var;
            move(1);
        } else {
            error();
        }
    }

    public void putShort(short var) {
        boolean ret = putShort(var, curPos);
        if (ret) {
            move(2);
        }
    }

    private boolean putShort(short var, int pos) {
        if (inRange(pos, 2)) {
            buf[pos] = (short) ((var >> 8) & 255);
            buf[pos + 1] = (short) ((var) & 255);
            return true;
        } else {
            error();
            return false;
        }
    }

    public void putInt(int var) {
        boolean ret = putInt(var, curPos);
        if (ret) {
            move(4);
        }
    }

    private boolean putInt(int var, int pos) {
        if (inRange(pos, 4)) {
            buf[pos] = (short) ((var >> 24) & 255);
            buf[pos + 1] = (short) ((var >> 16) & 255);
            buf[pos + 2] = (short) ((var >> 8) & 255);
            buf[pos + 3] = (short) ((var) & 255);
            return true;
        } else {
            error();
            return false;
        }
    }

    void putString(String var) {
        boolean ret = putString(var, curPos);
        if (ret) {
            move(2 * var.length());
        }
    }

    private boolean putString(String var, int pos) {
        char[] charArr = var.toCharArray();
        short theChar;
        if (inRange(pos, var.length() * 2)) {
            for (int i = 0; i < var.length(); i++) {
                theChar = (short) (charArr[i]);
                buf[pos] = (short) ((theChar >> 8) & 255);
                buf[pos + 1] = (short) ((theChar) & 255);
                pos += 2;
            }
            return true;
        } else {
            error();
            return false;
        }
    }

    public void putStringAscii(String var) {
        boolean ret = putStringAscii(var, curPos);
        if (ret) {
            move(var.length());
        }
    }

    private boolean putStringAscii(String var, int pos) {
        char[] charArr = var.toCharArray();
        if (inRange(pos, var.length())) {
            for (int i = 0; i < var.length(); i++) {
                buf[pos] = (short) charArr[i];
                pos++;
            }
            return true;
        } else {
            error();
            return false;
        }
    }

    public String toString() {
        StringBuilder strBuf = new StringBuilder();
        short tmp;
        for (int i = 0; i < (size - 1); i += 2) {
            tmp = (short) ((buf[i] << 8) | (buf[i + 1]));
            strBuf.append((char) (tmp));
        }
        return strBuf.toString();
    }

    public short getByte() throws ArrayIndexOutOfBoundsException {
        short ret = getByte(curPos);
        move(1);
        return ret;
    }

    private short getByte(int pos) throws ArrayIndexOutOfBoundsException {
        if (inRange(pos)) {
            return buf[pos];
        } else {
            error();
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    short getShort() throws ArrayIndexOutOfBoundsException {
        short ret = getShort(curPos);
        move(2);
        return ret;
    }

    private short getShort(int pos) throws ArrayIndexOutOfBoundsException {
        if (inRange(pos, 2)) {
            return (short) ((buf[pos] << 8) | (buf[pos + 1]));
        } else {
            error();
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public int getInt() throws ArrayIndexOutOfBoundsException {
        int ret = getInt(curPos);
        move(4);
        return ret;
    }

    private int getInt(int pos) throws ArrayIndexOutOfBoundsException {
        int ret = 0;
        if (inRange(pos, 4)) {
            ret |= (buf[pos] << 24);
            ret |= (buf[pos + 1] << 16);
            ret |= (buf[pos + 2] << 8);
            ret |= (buf[pos + 3]);
            return ret;
        } else {
            error();
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private char getChar(int pos) throws ArrayIndexOutOfBoundsException {
        if (inRange(pos, 2)) {
            return (char) (getShort(pos));
        } else {
            error();
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private char getCharAscii(int pos) throws ArrayIndexOutOfBoundsException {
        if (inRange(pos, 1)) {
            return (char) (getByte(pos) & 255);
        } else {
            error();
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    String getString(int length) throws ArrayIndexOutOfBoundsException {
        if (length > 0) {
            String ret = getString(curPos, length);
            move(ret.length() * 2);
            return ret;
        } else {
            return "";
        }
    }

    private String getString(int pos, int length) throws ArrayIndexOutOfBoundsException {
        char[] tmp;
        if (inRange(pos, length * 2) && length > 0) {
            tmp = new char[length];
            for (int i = 0; i < length; i++) {
                tmp[i] = getChar(pos + i * 2);
            }
            return new String(tmp);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private String getStringAscii(int pos, int length) throws ArrayIndexOutOfBoundsException {
        char[] tmp;
        if (inRange(pos, length) && length > 0) {
            tmp = new char[length];
            for (int i = 0; i < length; i++) {
                tmp[i] = getCharAscii(pos + i);
            }
            return new String(tmp);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    String getStringAsciiWithShortLength() throws ArrayIndexOutOfBoundsException {
        String ret = getStringAsciiWithShortLength(curPos);
        move(ret.length() + 2);
        return ret;
    }

    private String getStringAsciiWithShortLength(int pos) throws ArrayIndexOutOfBoundsException {
        short len;
        if (inRange(pos, 2)) {
            len = getShort(pos);
            if (len > 0) {
                return getStringAscii(pos + 2, len);
            } else {
                return "";
            }
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Method for compressing the data.
     */
    public void compress(int offset) {
        short[] newBuff = new short[size * 2]; // Leave a little extra room in case there are lots of different bytes..
        int bufPos = 0;
        int newPos = 0;
        short sameCount;
        short diffCount;
        int blockSize;
        int bestSameCount;
        int bestBlockSize;
        double compFactor;
        double maxCompFactor;

        if (DEBUG) {
            System.out.println("Uncompressed size: " + size);
            System.out.println("Compressing..");
        }

        if (offset > 0) {
            // Write offset number of bytes to the new buffer:
            System.arraycopy(buf, 0, newBuff, 0, offset);
            bufPos = offset;
            newPos = offset;
        }

        if (DEBUG) {
            System.out.println("Size of byte buffer: " + size);
        }
        while (bufPos < size) {
            // Count number of consecutive identical bytes:

            maxCompFactor = 0;
            bestSameCount = 0;
            bestBlockSize = 1;

            for (int i = 1; i < 17; i++) {
                sameCount = (short) consecutiveBlockCount(buf, bufPos, i);
                compFactor = (double) (sameCount * i) / (double) (3 + i);
                if (compFactor > maxCompFactor && sameCount > 1) {
                    maxCompFactor = compFactor;
                    bestSameCount = sameCount;
                    bestBlockSize = i;
                }
            }

            sameCount = (short) bestSameCount;
            blockSize = bestBlockSize;

            if ((sameCount > 1) && (maxCompFactor > 1)) {
                // Write a compressed area to the new buffer, then jump forward:
                //*****************************************
                if (sameCount > 255) {
                    sameCount = 255;
                }

                newBuff[newPos] = (short) blockSize;
                newBuff[newPos + 1] = sameCount;

                for (int i = 0; i < blockSize; i++) {
                    newBuff[newPos + 2 + i] = buf[bufPos + i];
                }

                newPos += (2 + blockSize);
                bufPos += (sameCount * blockSize);
                //*****************************************
            } else {
                // Find the number of different bytes, & write them to the buffer:
                //*****************************************
                diffCount = (short) differentByteCount(buf, bufPos, 2);
                newBuff[newPos] = 0;
                newBuff[newPos + 1] = diffCount;

                System.arraycopy(buf, bufPos, newBuff, newPos + 2, diffCount);

                bufPos += (diffCount);
                newPos += (diffCount + 2);
                //*****************************************
            }
        }
        // Assign:
        int oldSize = buf.length;
        buf = new short[newPos];
        System.arraycopy(newBuff, 0, buf, 0, newPos);
        this.curPos = 0;
        this.size = newPos;

        if (DEBUG) {
            System.out.println("Compression completed successfully. Compressed size: " + newPos + " Ratio: " + ((oldSize - newPos) / oldSize));
        }

    }

    private int consecutiveBlockCount(short[] arr, int arrpos, int blockSize) {
        short[] block = new short[blockSize];
        int blockCount;
        boolean foundOther;

        if (arrpos + blockSize >= arr.length) {
            return 0;
        }

        System.arraycopy(arr, arrpos, block, 0, blockSize);

        blockCount = 1;
        arrpos += blockSize;
        foundOther = false;
        while ((arrpos + blockSize < arr.length) && (!foundOther) && (blockCount < 255)) {
            foundOther = false;
            for (int i = 0; i < blockSize; i++) {
                if (arr[arrpos + i] != block[i]) {
                    foundOther = true;
                    break;
                }
            }
            if (!foundOther) {
                blockCount++;
                arrpos += blockSize;
            }
        }
        return blockCount;

    }

    private int differentByteCount(short[] arr, int arrpos, int maxIgnoreIdentical) {
        int diffCount = 1;
        short lastByte;
        int identicalCount;
        boolean foundTooManyIdentical = false;


        lastByte = arr[arrpos];
        arrpos++;
        while ((!foundTooManyIdentical) && (arrpos < arr.length) && (diffCount < 255)) {
            if (arr[arrpos] == lastByte) {
                // Find the number of identical bytes, and check whether it's lower than
                // the max argument:
                if (arrpos + 1 >= arr.length) {
                    // Stop here.
                    return diffCount;
                }
                // Else:
                identicalCount = consecutiveBlockCount(arr, arrpos, 1);
                if (identicalCount > maxIgnoreIdentical) {
                    // Stop here.
                    foundTooManyIdentical = true;
                } else {
                    // Ignore, and add those bytes:
                    arrpos += identicalCount;
                    diffCount += identicalCount;
                }
            } else {
                lastByte = arr[arrpos];
                diffCount++;
                arrpos++;
            }
        }
        return diffCount;
    }

    /**
     * Method for decompressing the data.
     */
    boolean decompress() {
        int excessCapacity = 131072;
        short[] newBuff = new short[excessCapacity];
        int bufPos = 0;
        int newPos = 0;
        int diffCount = 0;
        int sameCount = 0;
        int blockSize;
        boolean lastCompression = false;

        while (bufPos + 2 < size) {
            if (buf[bufPos] == 0) {
                // Uncompressed bytes:
                //*****************************************
                diffCount = buf[bufPos + 1];//(buf[bufPos+1]<<8)|(buf[bufPos+2]);
                if (newPos + diffCount >= newBuff.length) {
                    // Expand array:
                    newBuff = expandShortArray(newBuff, newPos + diffCount - newBuff.length + excessCapacity);
                }
                // Copy bytes:
                for (int i = 0; i < diffCount; i++) {
                    System.arraycopy(buf, bufPos + 2, newBuff, newPos, diffCount);
                }
                bufPos += (diffCount + 2);
                newPos += diffCount;
                lastCompression = false;
                //*****************************************
            } else if (buf[bufPos] < 65) {
                // Compressed block:
                //*****************************************
                blockSize = buf[bufPos];
                sameCount = buf[bufPos + 1];//(buf[bufPos+1]<<8)|(buf[bufPos+2]);
                if (newPos + sameCount * blockSize >= newBuff.length) {
                    // Expand array:
                    newBuff = expandShortArray(newBuff, newPos + sameCount * blockSize - newBuff.length + excessCapacity);
                }
                //theByte = buf[bufPos+2];
                // Fill in bytes:
                for (int i = 0; i < sameCount; i++) {
                    //newBuff[newPos+i] = (short)theByte;
                    System.arraycopy(buf, bufPos + 2, newBuff, newPos + i * blockSize, blockSize);
                }
                bufPos += 2 + blockSize;
                newPos += sameCount * blockSize;
                lastCompression = true;
                //*****************************************
            } else {
                // Invalid compression:
                //*****************************************
                System.out.println("Invalid compression, unable to decompress buffer.");
                System.out.println("bufPos=" + bufPos + " , value=" + buf[bufPos] + " , lastCompression=" + lastCompression + " , diffCount=" + diffCount + " , sameCount=" + sameCount);
                return false;
                //*****************************************
            }

            // Return to top of While.
        }

        // Finished decompression:
        buf = new short[newPos];
        System.arraycopy(newBuff, 0, buf, 0, newPos);

        if (DEBUG) {
            System.out.println("Decompression successfully completed.");
            System.out.println("Decompressed size: " + newPos);
        }

        this.curPos = 0;
        this.size = newPos;

        return true;
    }

    private short[] expandShortArray(short[] array, int size) {
        short[] newArr = new short[array.length + size];
        if (size > 0) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        } else {
            System.arraycopy(array, 0, newArr, 0, newArr.length);
        }
        return newArr;
    }

    private short[] calculateChecksums(int start, int length) {
        short[] srcBuf = new short[length];
        short[] csum = new short[8];

        if (start + length > buf.length) {
            System.out.println("Invalid length on calculating checksums. Would cause IndexOutOfBoundsException.");
            System.out.println("Real buffer size:" + buf.length);
            length = buf.length - start;
        }

        System.arraycopy(buf, start, srcBuf, 0, length);

        for (int i = 0; i < length; i++) {
            csum[0] += srcBuf[i];
            csum[1] += srcBuf[(i * 2) % length];
            csum[2] += srcBuf[(i * 3 + 19) % length];
            csum[3] += srcBuf[(i * 4 + 127) % length];
            csum[4] += srcBuf[(i * 2 + 1) % length];
            csum[5] += srcBuf[(i + 23) % length];
            csum[6] += srcBuf[(i / 2 + 75) % length];
            csum[7] += srcBuf[(i / 3 + 377) % length];

            csum[0] %= 7;
            csum[1] %= 19;
            csum[2] %= 23;
            csum[3] %= 53;
            csum[4] %= 71;
            csum[5] %= 137;
            csum[6] %= 237;
            csum[7] %= 213;
        }

        return csum;
    }

    public void appendChecksums(int start, int length) {
        short[] checkSums;
        if ((start + length) <= size) {
            checkSums = calculateChecksums(start, length);
        } else {
            System.out.println("Couldn't append checksums, the range is invalid.");
            return;
        }
        buf = expandShortArray(buf, 8);
        curPos = size;
        size += 8;

        for (int i = 0; i < 8; i++) {
            putByte(checkSums[i]);
        }
    }

    private short[] getChecksums() {
        short[] ret = new short[8];

        if (size > 8) {
            for (int i = 0; i < 8; i++) {
                ret[i] = getByte(size - 8 + i);
            }
            return ret;
        } else {
            System.out.println("Cannot return the checksums, as there aren't any..");
            return null;
        }
    }

    boolean removeChecksums() {
        if (size > 8) {
            buf = expandShortArray(buf, -8);
            size -= 8;
            curPos = 0;
            return true;
        } else {
            System.out.println("Unable to remove checksums, there aren't any..");
            return false;
        }
    }

    boolean validateChecksums(int length) {
        short[] newCsums = calculateChecksums(0, length);
        short[] oldCsums = getChecksums();
        boolean foundMismatch = false;

        // Check that they're the same:
        if (oldCsums != null) {
            for (int i = 0; i < 8; i++) {
                if (newCsums[i] != oldCsums[i]) {
                    System.out.println("Mismatching checksum: found " + newCsums[i] + ", should be " + oldCsums[i]);
                    foundMismatch = true;
                    //break;
                }
            }
            return (!foundMismatch);
        } else {
            System.out.println("Checksums not included, skipping.");
            return true;
        }
    }

    boolean writeToFile(String file) {
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(getBytes());
            return true;
        } catch (Exception e) {
            return false;
        }

    }

}