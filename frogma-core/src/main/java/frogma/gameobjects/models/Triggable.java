package frogma.gameobjects.models;

public interface Triggable {

    int MSG_FIRE = 0;
    int MSG_REACTIVATE = 1;

    void receiveTrigger(int message);

}