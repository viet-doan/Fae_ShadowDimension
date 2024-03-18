import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import java.util.concurrent.ThreadLocalRandom;

public class Navec {
    private final static String NAVEC_LEFT = "res/navec/navecLeft.png";
    private final static String NAVEC_RIGHT = "res/navec/navecRight.png";
    private final static String NAVEC_INV_LEFT = "res/navec/navecInvincibleLeft.png";
    private final static String NAVEC_INV_RIGHT = "res/navec/navecInvincibleRight.png";
    private final static int MAX_HEALTH_POINTS = 80;
    private final static int DAMAGE_POINTS = 20;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 15;
    private final static int HEALTH_Y_OFFSET = 6;
    private final static double HALF = 0.5;
    private final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    private Point position;
    private Point prevPosition;
    private static int healthPoints;
    private Image currentImage;
    private static int randomSpeed;
    private static double moveSize;
    private final static int MAX_SPEED = 7;
    private final static int MIN_SPEED = 2;
    private final static double SPEED_FACTOR = 0.1;
    private boolean startTimer;
    private static int invincibleTimer;
    private final static int INVINCIBLE_TIME = 180;
    private boolean facingRight;
    private boolean travelUp;
    private final static int DIRECTION_HORIZONTAL = 0;
    private final static int DIRECTION_VERTICAL = 1;
    private static boolean directionHorizontal;
    private static boolean isAttacking;
    private static boolean isInvincible;
    private final static double TIMESCALE = 1.5;
    private final static double DEFAULT_TIMESCALE = 1;
    private static double timescaleFactor;
    private final static int ZERO = 0;
    private final static int MAX_TIMESCALE = 3;
    private final static int MIN_TIMESCALE = -3;

    public Navec(int startX, int startY){
        this.position = new Point(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = new Image(NAVEC_RIGHT);
        this.facingRight = true;
        this.isInvincible = false;

        /** To get a randomSpeed, I have applied the random methods below which was
         * imported from java.util.concurrent.ThreadLocalRandom which was from a link
         * https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
         * and I will also use the same methods later to set whether the Navec is travelling
         * in a horizontal or vertical direction.
         */
        this.randomSpeed = ThreadLocalRandom.current().nextInt(MIN_SPEED, MAX_SPEED + 1);
        this.timescaleFactor = 1;
        COLOUR.setBlendColour(GREEN);
    }

    public void update(ShadowDimension navecObject) {

        moveSize = randomSpeed * SPEED_FACTOR * timescaleFactor;
        setPrevPosition();

        /**
         * Setting up an update to automatically allow Navec to constantly
         * move up or down or left or right depending on the initial level setup
         */
        if (directionHorizontal) {
            if (facingRight) {
                move(moveSize, 0);
            } else if (!facingRight) {
                move(-moveSize, 0);
            }
        } else if (!directionHorizontal) {
            if (travelUp) {
                move(0, -moveSize);
            } else if (!travelUp) {
                move(0, moveSize);
            }
        }

        if(isInvincible) {
            startTimer = true;
        }

        /** Making the Navec invincible for the duration of 3 seconds and then stop afterward **/
        if (startTimer) {
            invincibleTimer++;
            if (invincibleTimer >= INVINCIBLE_TIME) {
                startTimer = false;
                invincibleTimer = ZERO;
                isInvincible = false;
            }
        }

        //Drawing the Navec's left and right image when being invincible or not
        if (isInvincible && facingRight) {
            this.currentImage = new Image(NAVEC_INV_RIGHT);
        } else if (isInvincible && !facingRight) {
            this.currentImage = new Image(NAVEC_INV_LEFT);
        } else if (!isInvincible && facingRight) {
            this.currentImage = new Image(NAVEC_RIGHT);
        } else if (!isInvincible && !facingRight) {
            this.currentImage = new Image(NAVEC_LEFT);
        }

        /**
         * While Navec is not dead, draw them on the screen
         * while also rendering their health and checking their collision
         * with the walls and trees as well as being attacked
         */
        if (!isDead()) {
            this.currentImage.drawFromTopLeft(position.x, position.y);
            navecObject.navecCheckCollisions(this);
            renderHealthPoints();
            navecObject.navecCheckOutOfBounds(this);
            navecObject.inNavecRange(this);
        }
    }


    /** Method that stores Navec's previous position **/
    private void setPrevPosition(){
        this.prevPosition = new Point(position.x, position.y);
    }

    /** Method that moves Navec back to previous position **/
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

    /** Method that moves Navec given the direction **/
    private void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }

    /** Method that renders the current health as a percentage above Navec's bounding box **/
    private void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", position.x, position.y - HEALTH_Y_OFFSET, COLOUR);
    }

    public static boolean isDead() { return healthPoints <= 0; }

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

    public static int getMaxHealthPoints() { return MAX_HEALTH_POINTS; }

    public static int getDamagePoints() { return DAMAGE_POINTS; }

    /** This method set whether if the Navec is travelling (up & down) or (left & right) **/
    public void setDirection() {
        int type = ThreadLocalRandom.current().nextInt(DIRECTION_HORIZONTAL, DIRECTION_VERTICAL + 1);
        if (type == DIRECTION_HORIZONTAL) {
            directionHorizontal = true;
        } else if (type == DIRECTION_VERTICAL) {
            directionHorizontal = false;
        }
    }

    public static boolean checkInvincible() { return isInvincible; }
    public static void isAttacked() { isInvincible = true; }

    public static void adjustTimescale(int timescale) {
        int i;
        if (timescale == ZERO) {
            timescaleFactor = DEFAULT_TIMESCALE;
        } else if (timescale > ZERO && timescale < MAX_TIMESCALE) {
            for (i = 0; i < MAX_TIMESCALE; i++)
                timescaleFactor = timescale * TIMESCALE;
        } else if (timescale < ZERO && timescale < MIN_TIMESCALE) {
            for (i = 0; i > MIN_TIMESCALE; i--)
                timescaleFactor = timescale / TIMESCALE;
        }
    }
}