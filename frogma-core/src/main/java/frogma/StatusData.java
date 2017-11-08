package frogma;

public class StatusData {

    private String[] statusDataName = new String[0];
    private String[] statusData = new String[0];

    public StatusData() {
        // Do nothing.
    }

    public StatusData(byte[] buffer) {
        restoreStatusData(buffer);
    }

    public void saveStatusData(String name, String data) {
        if (name == null || data == null || name.equals("")) {
            return;
        }
        int index = getStatusDataIndex(name);
        if (index != -1) {
            statusData[index] = data;
        } else {
            String[] newName = new String[statusDataName.length + 1];
            String[] newData = new String[statusDataName.length + 1];
            for (int i = 0; i < statusDataName.length; i++) {
                newName[i] = statusDataName[i];
                newData[i] = statusData[i];
            }
            newName[statusDataName.length] = name;
            newData[statusDataName.length] = data;
            statusDataName = newName;
            statusData = newData;
        }
    }

    private int getStatusDataIndex(String name) {
        for (int i = 0; i < statusDataName.length; i++) {
            if (statusDataName[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public String retrieveStatusData(String name) {
        int index = getStatusDataIndex(name);
        if (index != -1) {
            return statusData[index];
        } else {
            return null;
        }
    }

    public byte[] getStatusDataBufferBytes() {
        ByteBuffer buf = getStatusDataBuffer();
        return buf.getBytes();
    }

    public ByteBuffer getStatusDataBuffer() {
        ByteBuffer buf = new ByteBuffer(100);
        buf.putInt(statusData.length);
        for (int i = 0; i < statusData.length; i++) {
            buf.putShort((short) (statusDataName[i].length()));
            buf.putString(statusDataName[i]);
        }
        for (int i = 0; i < statusData.length; i++) {
            buf.putShort((short) (statusData[i].length()));
            buf.putString(statusData[i]);
        }
        buf.compress(0);
        buf.appendChecksums(0, buf.getSize());
        return buf;
    }

    public boolean restoreStatusData(byte[] buffer) {
        ByteBuffer buf = new ByteBuffer(buffer);
        buf.setExpandable(false);
        if (!buf.validateChecksums(0, buf.getSize() - 8)) {
            System.out.println("Unable to restore state information from the specified file.");
            return false;
        }
        buf.decompress(0);
        buf.goTo(0);

        int size = buf.getInt();
        statusData = new String[size];
        statusDataName = new String[size];

        short strLen;
        for (int i = 0; i < size; i++) {
            strLen = buf.getShort();
            statusDataName[i] = buf.getString(strLen);
        }
        for (int i = 0; i < size; i++) {
            strLen = buf.getShort();
            statusData[i] = buf.getString(strLen);
        }
        return true;
    }

    public boolean writeToFile(String file) {
        ByteBuffer buf = getStatusDataBuffer();
        return buf.writeToFile(file);
    }


    public boolean loadFromFile(String file) {
        FilLeser fl = new FilLeser(file);
        return restoreStatusData(fl.readWholeFile(0));
    }

}