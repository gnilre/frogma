package frogma;

import frogma.gameobjects.models.DynamicObject;

/**
 * <p>Title: Animation</p>
 * <p>Description: Class used to handle the animations for DynamicObjects </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Alf B�rge Lerv�g
 * @version 1.0
 */
public class Animation {
    public int animState;
    public int imageNr;
    public int direction;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;

    public int walkFirst;
    public int walkLast;
    public int jumpFirst;
    public int jumpLast;
    public int walkOffset;
    public int jumpOffset;
    public int deathStart;
    public int deathEnd;
    public int deathOffset;

    /**
     * Initialize the animation with first and last pictures...
     *
     * @param walkFirst   First picture in walk animation.
     * @param walkLast    Last ...
     * @param jumpFirst   First picture in jump animation.
     * @param jumpLast    Last ...
     * @param walkOffset  How many pictures we shall jump to turn around.
     * @param jumpOffset  How many pictures we shall jump to turn around.
     * @param deathStart  First picture in death animation.
     * @param deathEnd    Last...
     * @param deathOffset Offset.
     *                    <p>
     *                    The image with the animations should be so that we have
     *                    two different animations for walking left and walking right, and these should be
     *                    mirrored so that by adding walkOffset to walkFirst, we get to the first image
     *                    in the animation where we walk LEFT.
     *                    walkFirst should be the image where you walk RIGHT.
     *                    Likewise with jump...
     */
    public Animation(int walkFirst, int walkLast, int jumpFirst, int jumpLast, int walkOffset, int jumpOffset, int deathStart, int deathEnd, int deathOffset) {
        this.imageNr = 0;
        this.walkFirst = walkFirst;
        this.walkLast = walkLast;
        this.jumpFirst = jumpFirst;
        this.jumpLast = jumpLast;
        this.walkOffset = walkOffset;
        this.jumpOffset = jumpOffset;
        this.deathStart = deathStart;
        this.deathEnd = deathEnd;
        this.deathOffset = deathOffset;
    }

    /**
     * getNext image in animation cycle
     *
     * @param animState
     * @param direction
     * @return int
     * <p>
     * We print out an integer here.
     * This could be an image instead, but it's faster to have one large image
     * which contains all the images we need, and then cut out the one we need
     * when we need it. The integer we return here is used to choose which image
     * to cut out.
     */
    public int getNext(int animState, int direction) {
        switch (animState) {
            case DynamicObject.WALKING:
                switch (direction) {
                    case LEFT:
                        // We're walking in the same direction as last time.
                        if (this.animState == animState && this.direction == direction) {
                            this.imageNr = this.imageNr + 1;
                        }
                        // We've changed direction.
                        if (this.animState == animState && this.direction != direction) {
                            this.imageNr = this.imageNr + walkOffset;
                        }
                        // We have changed animState. Just landed I presume.
                        if (this.animState != animState) {
                            this.imageNr = walkFirst + walkOffset;
                        }
                        // Making sure we stay within the correct images.
                        if (this.imageNr < (this.walkFirst + walkOffset) ||
                                this.imageNr > (this.walkLast + walkOffset)) {
                            this.imageNr = walkFirst + walkOffset;
                        }
                        this.direction = direction;
                        break;

                    case RIGHT:
                        // We're walking in the same direction as last time.
                        if (this.animState == animState && this.direction == direction) {
                            this.imageNr = this.imageNr + 1;
                        }
                        // We've changed direction.
                        if (this.animState == animState && this.direction != direction) {
                            this.imageNr = this.imageNr - walkOffset;
                        }
                        // We have changed animState. Just landed I presume.
                        if (this.animState != animState) {
                            this.imageNr = walkFirst;
                        }
                        // Making sure we stay within the correct images.
                        if (this.imageNr < this.walkFirst || this.imageNr > this.walkLast) {
                            this.imageNr = walkFirst;
                        }
                        this.direction = direction;
                        break;
                }
                this.animState = animState;
                break;
            case DynamicObject.JUMPING:
                switch (direction) {
                    case LEFT:
                        // We're jumping in the same direction as last time.
                        if (this.animState == animState && this.direction == direction) {
                            this.imageNr = this.imageNr + 1;
                        }
                        // We've changed direction.
                        if (this.animState == animState && this.direction != direction) {
                            this.imageNr = this.imageNr + jumpOffset;
                        }
                        // We have changed animState. Just taken off I presume.
                        if (this.animState != animState) {
                            this.imageNr = this.jumpFirst + jumpOffset;
                        }
                        // Making sure we stay within the correct images.
                        if (this.imageNr < (this.jumpFirst + jumpOffset) ||
                                this.imageNr > (this.jumpLast + jumpOffset)) {
                            this.imageNr = this.jumpLast + jumpOffset;
                        }
                        this.direction = direction;
                        break;
                    case RIGHT:
                        // We're jumping in the same direction as last time.
                        if (this.animState == animState && this.direction == direction) {
                            this.imageNr = this.imageNr + 1;
                        }
                        // We've changed direction.
                        if (this.animState == animState && this.direction != direction) {
                            this.imageNr = this.imageNr - jumpOffset;
                        }
                        // We have changed animState. Just taken off I presume.
                        if (this.animState != animState) {
                            this.imageNr = this.jumpFirst;
                        }
                        // Making sure we stay within the correct images.
                        if (this.imageNr < (this.jumpFirst + jumpOffset) ||
                                this.imageNr > (this.jumpLast + jumpOffset)) {
                            this.imageNr = this.jumpLast;
                        }
                        this.direction = direction;
                        break;
                }

                this.animState = animState;
                break;
            case DynamicObject.FALLING:
                switch (direction) {
                    case LEFT:
                        // We've changed direction.
                        if (this.animState == animState && this.direction != direction) {
                            this.imageNr = this.imageNr + jumpOffset;
                        }
                        // We have changed animState. Just landed I presume.
                        if (this.animState != animState) {
                            this.imageNr = walkFirst + walkOffset;
                        }
                        // Making sure we stay within the correct images.
                        if (this.imageNr < (this.jumpFirst + jumpOffset) ||
                                this.imageNr > (this.jumpLast + jumpOffset)) {
                            this.imageNr = jumpLast + jumpOffset;
                        }
                        this.direction = direction;
                        break;

                    case RIGHT:
                        // We've changed direction.
                        if (this.animState == animState && this.direction != direction) {
                            this.imageNr = this.imageNr - jumpOffset;
                        }
                        // We have changed animState. Just landed I presume.
                        if (this.animState != animState) {
                            this.imageNr = walkFirst + walkOffset;
                        }
                        // Making sure we stay within the correct images.
                        if (this.imageNr < this.jumpFirst ||
                                this.imageNr > this.jumpLast) {
                            this.imageNr = jumpLast;
                        }
                        this.direction = direction;
                        break;
                }
                this.animState = animState;
                break;
            case DynamicObject.DYING:
                switch (direction) {
                    case LEFT:
                        // We've died. Let's have fun doing it.
                        // First we check if this is the first image in the death sequence.
                        if (this.animState < (this.deathStart + this.deathOffset) ||
                                this.animState > (this.deathEnd + this.deathOffset)) {
                            this.imageNr = (this.deathStart + this.deathOffset);
                            // Is it the last image in the death sequence?
                        } else if (this.animState < (this.deathEnd + this.deathOffset)) {
                            this.imageNr = this.imageNr + 1;
                            // We stop at the last image. Object shouldn't be visible for much longer.
                        } else {
                            this.imageNr = (this.deathEnd + this.deathOffset);
                        }
                        break;

                    case RIGHT:
                        // We've died. Let's have fun doing it.
                        // First we check if this is the first image in the death sequence.
                        if (this.animState < this.deathStart ||
                                this.animState > this.deathEnd) {
                            this.imageNr = this.deathStart;
                            // Is it the last image in the death sequence?
                        } else if (this.animState < this.deathEnd) {
                            this.imageNr = this.imageNr + 1;
                        } else {
                            this.imageNr = this.deathEnd;
                        }
                        break;
                }
                this.animState = animState;
                break;
        }
        return this.imageNr;
    }
}
