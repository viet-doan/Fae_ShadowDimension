import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import java.util.concurrent.ThreadLocalRandom;

public class Demon {
    private final static Image DEMON_LEFT = new Image("res/demon/demonLeft.png");
    private final static Image DEMON_RIGHT = new Image("res/demon/demonRight.png");
    private final static Image DEMON_INV_LEFT = new Image("res/demon/demonInvincibleLeft.png");
    private final static Image DEMON_INV_RIGHT = new Image("res/demon/demonInvincibleRight.png");
    private final static int MAX_HEALTH_POINTS = 40;
    private final static int DAMAGE_POINTS = 10;

    private final int ORANGE_BOUNDARY = 65;
    private final int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 15;
    private final static int HEALTH_Y_OFFSET = 6;
    private final static double HALF = 0.5;
    private final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    private final DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    private Point position;
    private Point prevPosition;
    private int healthPoints;
    private Image currentImage;
    private static int randomSpeed;
    private static double moveSize;
    private final static int MAX_SPEED = 7;
    private final static int MIN_SPEED = 2;
    private final static double SPEED_FACTOR = 0.1;
    private boolean startTimer;
    private int invincibleTimer;
    private final static int INVINCIBLE_TIME = 180;
    private boolean facingRight;
    private boolean travelUp;
    private final static int RANDOM_ZERO = 0;
    private final static int RANDOM_ONE = 1;
    private boolean directionHorizontal;
    private boolean isInvincible;
    private boolean isPassive;
    private final static double TIMESCALE = 1.5;
    private final static double DEFAULT_TIMESCALE = 1;
    private static double timescaleFactor;
    private final static int ZERO = 0;
    private final static int MAX_TIMESCALE = 3;
    private final static int MIN_TIMESCALE = -3;

    public Demon(int startX, int startY){
        this.position = new Point(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = DEMON_RIGHT;
        this.isInvincible = false;
        this.setUp();

        /** To get a randomSpeed, I have applied the random methods below which was
         * imported from java.util.concurrent.ThreadLocalRandom which was from a link
         * https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
         * and I will also use the same methods later to set whether the Demon is travelling
         * in a horizontal or vertical direction or whether if they are passive or aggressive.
         */
        this.randomSpeed = ThreadLocalRandom.current().nextInt(MIN_SPEED, MAX_SPEED + 1);
        this.timescaleFactor = 1;
        COLOUR.setBlendColour(GREEN);
    }

    public void update(ShadowDimension demonObject) {

        moveSize = randomSpeed * SPEED_FACTOR * timescaleFactor;
        setPrevPosition();
        /**
         * Setting up an update to automatically allow Demons to constantly
         * move up or down or left or right depending on the initial level setup
         */
        if (directionHorizontal && !isPassive) {
            if (facingRight) {
                move(moveSize, 0);
            } else if (!facingRight) {
                move(-moveSize, 0);
            }
        } else if (!directionHorizontal && !isPassive) {
            if (travelUp) {
                move(0, -moveSize);
            } else if (!travelUp) {
                move(0, moveSize);
            }
        }

        if(isInvincible) {
                startTimer = true;
            }

        /** Making the Demon invincible for the duration of 3 seconds and then stop afterward **/
        if (startTimer) {
            invincibleTimer++;
            if (invincibleTimer >= INVINCIBLE_TIME) {
                startTimer = false;
                isInvincible = false;
                invincibleTimer = ZERO;
            }
        }

        /**Drawing the Demon's left and right image when being invincible or not
        if (isInvincible && facingRight) {
            this.currentImage = DEMON_INV_RIGHT;
        } else if (isInvincible && !facingRight) {
            this.currentImage = DEMON_INV_LEFT;
        } else if (!isInvincible && facingRight) {
            this.currentImage = DEMON_RIGHT;
        } else if (!isInvincible && !facingRight) {
            this.currentImage = DEMON_LEFT;
        }**/

        /**
         * While each demon is not dead, draw them on the screen
         * while also rendering their health and checking their collision
         * with the walls and trees as well as being attacked
         */
        if (!isDead()) {
            this.drawDemonImage(this.isDead(), isInvincible, facingRight, position);
            //this.currentImage.drawFromTopLeft(position.x, position.y);
            demonObject.demonsCheckCollisions(this);
            //this.renderHealthPoints(this);
            demonObject.demonCheckOutOfBounds(this);
            demonObject.inDemonRange(this);
        }
    }

    private void drawDemonImage(boolean dead, boolean isInvincible, boolean facingRight, Point position) {
        if (isInvincible && facingRight) {
            this.currentImage = DEMON_INV_RIGHT;
        } else if (isInvincible && !facingRight) {
            this.currentImage = DEMON_INV_LEFT;
        } else if (!isInvincible && facingRight) {
            this.currentImage = DEMON_RIGHT;
        } else if (!isInvincible && !facingRight) {
            this.currentImage = DEMON_LEFT;
        }

        this.currentImage.drawFromTopLeft(position.x, position.y);
        this.renderHealthPoints(this);
    }

    /** Method that stores Demon's previous position **/
    private void setPrevPosition(){
        this.prevPosition = new Point(position.x, position.y);
    }

    /** Method that moves Demon back to previous position **/
    public void moveBack(){
        if (directionHorizontal) {
            if (facingRight) {
                facingRight = false;
                this.position = prevPosition;
            } else if (!facingRight) {
                facingRight = true;
                this.position = prevPosition;
            }
        } else if (!directionHorizontal) {
            if (travelUp) {
                travelUp = false;
                this.position = prevPosition;
            } else if (!travelUp) {
                travelUp = true;
                this.position = prevPosition;
            }
        }
    }

    /** Method that moves Demon given the direction **/
    private void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }

    /** Method that renders the current health as a percentage above each Demon's bounding box **/
    private void renderHealthPoints(Demon demon){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", position.x, position.y - HEALTH_Y_OFFSET, COLOUR);
    }

    public boolean isDead() { return healthPoints <= ZERO; }

    public Point getPosition() { return position; }
    public Point getCentre() {
        Point pointTopLeft = this.getPosition();
        double centreX, centreY;

        centreX = pointTopLeft.x + (this.getCurrentImage().getWidth()) * HALF;
        centreY = pointTopLeft.y + (this.getCurrentImage().getHeight()) * HALF;
        Point centre = new Point(centreX, centreY);
        return centre;
    }

    public Image getCurrentImage() { return currentImage;    }

    public int getHealthPoints() { return healthPoints; }

    public void setHealthPoints(int healthPoints) { this.healthPoints = healthPoints; }

    public int getMaxHealthPoints() { return MAX_HEALTH_POINTS; }

    public static int getDamagePoints() { return DAMAGE_POINTS; }

    /**
     * This method set whether if the demon is travelling (up & down) or (left & right)
     * or is facing left or right or if the demon is passive or aggressive
     * and is randomly initialised once each individual demons spawns in.
     */
    public void setUp() {
        int horizontalOrVertical = ThreadLocalRandom.current().nextInt(RANDOM_ZERO, RANDOM_ONE + 1);
        if (horizontalOrVertical == RANDOM_ZERO) {
            directionHorizontal = true;
        } else if (horizontalOrVertical == RANDOM_ONE) {
            directionHorizontal = false;
        }

        int passiveOrAggressive = ThreadLocalRandom.current().nextInt(RANDOM_ZERO, RANDOM_ONE + 1);
        if (passiveOrAggressive == RANDOM_ZERO) {
            isPassive = true;
        } else if (passiveOrAggressive == RANDOM_ONE) {
            isPassive = false;
        }

        int upOrDown = ThreadLocalRandom.current().nextInt(RANDOM_ZERO, RANDOM_ONE + 1);
        if (upOrDown == RANDOM_ZERO) {
            travelUp = true;
        } else if (upOrDown == RANDOM_ONE) {
            travelUp = false;
        }

        int leftOrRight = ThreadLocalRandom.current().nextInt(RANDOM_ZERO, RANDOM_ONE + 1);
        if (leftOrRight == RANDOM_ZERO) {
            facingRight = true;
        } else if (leftOrRight == RANDOM_ONE) {
            facingRight = false;
        }
    }

    public boolean checkInvincible() { return isInvincible; }
    public void setInvincible(boolean invincible) { isInvincible = invincible; }

    public static void adjustTimescale(int timescale) {
        int i;
        if (timescale == ZERO) {
            timescaleFactor = DEFAULT_TIMESCALE;
        } else if (timescale > ZERO && timescale < MAX_TIMESCALE) {
            for(i=0; i<MAX_TIMESCALE; i++)
                timescaleFactor = timescale * TIMESCALE;
        } else if (timescale < ZERO && timescale < MIN_TIMESCALE) {
            for (i = 0; i > MIN_TIMESCALE; i--)
                timescaleFactor = timescale / TIMESCALE;
        }
    }
}