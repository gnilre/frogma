package frogma.gameobjects;

import frogma.input.Input;

public interface PlayerInterface {

    void processInput(Input input);

    int getLife();

    int getHealth();

    void setLife(int value);

}