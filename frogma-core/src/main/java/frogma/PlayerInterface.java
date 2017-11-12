package frogma;

public interface PlayerInterface {

    void processInput(Input input);

    int getLife();

    int getHealth();

    void setLife(int value);

}