package frogma;

public class ObjectClassParams {

    private int[] type;
    private int[][] comboValues;

    private String[] name;
    private String[][] comboName;

    public ObjectClassParams() {
        type = new int[10];
        comboValues = new int[10][0];
        name = new String[10];
        comboName = new String[10][0];
        // The variables will be initialized so that the type will be 'none'.
        // The params won't be used if they don't get set explicitly.
    }

    public void setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName) {
        this.type[pIndex] = type;
        this.comboValues[pIndex] = comboValues;
        this.name[pIndex] = name;
        this.comboName[pIndex] = comboName;
    }

    public int getType(int i) {
        return type[i];
    }

    public int[] getComboValues(int i) {
        return comboValues[i];
    }

    public String getName(int i) {
        return name[i];
    }

    public String[] getComboNames(int i) {
        return comboName[i];
    }

}