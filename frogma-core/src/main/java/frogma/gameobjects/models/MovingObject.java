package frogma.gameobjects.models;

import frogma.GameEngine;

import java.awt.*;

public class MovingObject extends StaticObject implements SubPixelPosition {

    public MovingObject(int tileW, int tileH, GameEngine referrer, Image objImage, boolean visible) {
        super(tileW, tileH, referrer, objImage, visible);
    }

    protected double dposX, dposY;
    protected double dnewX, dnewY;
    protected double dvelX, dvelY;

    public int getPosX() {
        return (int) dposX;
    }

    public int getPosY() {
        return (int) dposY;
    }

    public int getNewX() {
        return (int) dnewX;
    }

    public int getNewY() {
        return (int) dnewY;
    }

    public int getVelX() {
        return (int) dvelX;
    }

    public int getVelY() {
        return (int) dvelY;
    }

    public void setDPosition(double x, double y) {
        dposX = x;
        dposY = y;
    }

    public void setDNewPosition(double x, double y) {
        dnewX = x;
        dnewY = y;
    }

    public void setDVelocity(double velX, double velY) {
        this.dvelX = velX;
        this.dvelY = velY;
    }

    public void setPosition(int x, int y) {
        dposX = x;
        dposY = y;
    }

    public void setNewPosition(int x, int y) {
        dnewX = x;
        dnewY = y;
    }

    public void setVelocity(int velX, int velY) {
        this.dvelX = velX;
        this.dvelY = velY;
    }

}