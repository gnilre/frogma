package frogma.gameobjects;

import frogma.ObjectProps;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.StaticObject;

public class TimeOut extends StaticObject {
    private int currentFrame;
    private int frameCount;
    private BasicGameObject receiver;
    private String messageToSend;

    public TimeOut(BasicGameObject receiver, String msg, int frameCount) {
        super(0, 0, null, null, false);
        this.receiver = receiver;
        this.messageToSend = msg;
        this.frameCount = frameCount;
        this.currentFrame = 0;
    }

    public void advanceCycle() {
        if (currentFrame++ >= frameCount) {
            if (receiver != null) {
                receiver.timerEvent(messageToSend);
                setProp(ObjectProps.PROP_ALIVE, false);
            }
        }
    }
}