package frogma;

import java.io.*;

/**
 * Class for reading Strings, ints, shorts and bytes from a file.
 *
 * @author Andreas Wigmostad Bjerkhaug
 */
public class FilLeser {
    BufferedReader buffer;
    InputStream inStream;
    File minFil;

    /**
     * Creates a new FilLeser which reads from the specified filename.
     *
     * @param filnavn the path of the file which are to be read
     */
    public FilLeser(String filnavn) {
        System.out.println("Fil: " + filnavn);
        //try{
        //inStream = new FileInputStream(filnavn);//getClass().getResourceAsStream(filnavn);
        //buffer = new BufferedReader(new InputStreamReader(new FileInputStream(filnavn)));
        inStream = getClass().getResourceAsStream(filnavn);
        if (inStream != null) {
            buffer = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filnavn)));
        } else {
            System.out.println("Unable to read file!");
        }
        //}catch(FileNotFoundException f){
        //	System.out.println("Unable to open file.");
        //}
    }

    /**
     * Creates a new FilLeser which reads from the specified file.
     *
     * @param fil the path of the file which are to be read
     */
    public FilLeser(File fil) {
        System.out.println("Fil: " + fil.getAbsolutePath());
        this.minFil = fil;
        if (minFil.exists()) {
            try {
                buffer = new BufferedReader(new InputStreamReader(new FileInputStream(minFil)));
                inStream = new FileInputStream(fil);
            } catch (FileNotFoundException f) {
                System.out.println("Unable to open file.");
            }
        }
    }

    public void setStart() {
        try {
            buffer.mark(128);
        } catch (IOException ioe) {
            // Ignore.
        }
    }

    public void resetPosition() {
        try {
            buffer.reset();
        } catch (IOException ioe) {
            // Ignore.
        }
    }

    /**
     * Reads a String from a line in a file.
     *
     * @return the text that were read from the file
     */
    public String lesString() {
        try {
            return buffer.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Reads an integer from a file
     */
    public int lesInt() {
        String tekst;
        try {
            tekst = buffer.readLine();
        } catch (IOException e) {
            tekst = "";
        }
        try {
            return Integer.parseInt(tekst);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Reads a short from a file
     */
    public short lesShort() {
        String tekst;
        try {
            tekst = buffer.readLine();
        } catch (IOException e) {
            tekst = "";
        }
        try {
            return Short.parseShort(tekst);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Reads a byte from a file
     */
    public byte lesByte() {
        String tekst;
        try {
            tekst = buffer.readLine();
        } catch (IOException e) {
            tekst = "";
        }
        try {
            return Byte.parseByte(tekst);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Method for closing the buffer.
     */
    public void close() {
        try {
            buffer.close();
        } catch (IOException ioe) {
        }
    }

    public byte readSingleByte() throws IOException {
        return (byte) inStream.read();
    }

    public short readSingleShort() throws IOException {
        int b1, b2;
        b1 = inStream.read();
        b2 = inStream.read();
        return (short) (b1 << 8 | b2);
    }

    public int readSingleInt() throws IOException {
        int b1, b2, b3, b4;
        b1 = inStream.read();
        b2 = inStream.read();
        b3 = inStream.read();
        b4 = inStream.read();
        return (b1 << 24 | b2 << 16 | b3 << 8 | b4);
    }

    public byte[] readWholeFile(int offset) {
        boolean notEndOfFile = true;
        byte[] readByte;
        int byteCount;
        byte[] buff;
        byte[] newbuff;
        int excessCapacity = 150000;
        int index = 0;

        long t1, t2;

        t1 = System.currentTimeMillis();

        buff = new byte[excessCapacity];
        readByte = new byte[excessCapacity];
        if (offset > 0) {
            try {
                inStream.skip(offset);
            } catch (IOException ioe) {
                // Ignore.
            }
        }
        while (notEndOfFile) {
            try {
                byteCount = inStream.read(readByte);
            } catch (IOException ioe) {
                byteCount = -1;
            }
            if (byteCount != -1) {
                if (index + byteCount >= buff.length) {
                    // Resize buffer:
                    newbuff = new byte[buff.length + excessCapacity];
                    System.arraycopy(buff, 0, newbuff, 0, buff.length);
                    buff = newbuff;
                }
                System.arraycopy(readByte, 0, buff, index, byteCount);
                index += byteCount;

            } else {
                // End of file reached.
                notEndOfFile = false;
            }
        }
        // index+1 is now the length of the file.
        // Remove excess capacity:
        newbuff = new byte[index];
        System.arraycopy(buff, 0, newbuff, 0, index);

        t2 = System.currentTimeMillis();
        //System.out.println("Time taken reading the file: "+((t2-t1)/1000));

        return newbuff;
    }
}