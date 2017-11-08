package frogma.gameobjects.models;

public interface Triggable {

    public static int MSG_FIRE = 0;
    public static int MSG_REACTIVATE = 1;

    public void receiveTrigger(int message);


}