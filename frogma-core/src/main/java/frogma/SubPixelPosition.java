package frogma;

public interface SubPixelPosition {

    public double getdPosX();

    public double getdPosY();

    public double getdNewX();

    public double getdNewY();

    public double getdVelX();

    public double getdVelY();

    public void setdPosition(double x, double y);

    public void setdNewPosition(double x, double y);

    public void setdVelocity(double velX, double velY);

}