package frogma.resources;

import java.io.*;

/**
 * Class for reading Strings, ints, shorts and bytes from a file.
 *
 * @author Andreas Wigmostad Bjerkhaug
 */
public class FilLeser {
    private BufferedReader buffer;
    private InputStream inStream;

    /**
     * Creates a new FilLeser which reads from the specified filename.
     *
     * @param filnavn the path of the file which are to be read
     */
    public FilLeser(String filnavn) {
        System.out.println("Fil: " + filnavn);
        inStream = getClass().getResourceAsStream(filnavn);
        if (inStream != null) {
            buffer = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filnavn)));
        } else {
            System.out.println("Unable to read file!");
        }
    }

    /**
     * Creates a new FilLeser which reads from the specified file.
     *
     * @param fil the path of the file which are to be read
     */
    public FilLeser(File fil) {
        System.out.println("Fil: " + fil.getAbsolutePath());
        if (fil.exists()) {
            try {
                buffer = new BufferedReader(new InputStreamReader(new FileInputStream(fil)));
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
            // ignore
        }
    }

    public byte[] readWholeFile(int offset) {
        boolean notEndOfFile = true;
        byte[] readByte;
        int byteCount;
        byte[] buff;
        byte[] newbuff;
        int excessCapacity = 150000;
        int index = 0;

        buff = new byte[excessCapacity];
        readByte = new byte[excessCapacity];
        if (offset > 0) {
            try {
                long skipped = inStream.skip(offset);
                if (skipped != offset) {
                    throw new IllegalStateException("Unable to skip to offset " + offset);
                }
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

        //System.out.println("Time taken reading the file: "+((t2-t1)/1000));

        return newbuff;
    }
}