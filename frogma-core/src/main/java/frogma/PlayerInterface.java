package frogma;

public interface PlayerInterface {

    public void processInput(Input input);

    public int getLife();

    public int getHealth();

    public void setLife(int value);

    public void setHealth(int value);

}