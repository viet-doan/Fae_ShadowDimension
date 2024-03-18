import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

public class Player {
    private static int attackTimer;
    private final int ATTACK_COOLDOWN = 120;
    private final int ATTACK_DURATION = 60;
    private final static int DAMAGE_POINTS = 20;
    private final static String FAE_LEFT = "res/fae/faeLeft.png";
    private final static String FAE_RIGHT = "res/fae/faeRight.png";
    private final static String FAE_ATTACK_LEFT = "res/fae/faeAttackLeft.png";
    private final static String FAE_ATTACK_RIGHT = "res/fae/faeAttackRight.png";
    private final static int MAX_HEALTH_POINTS = 100;
    private final static double MOVE_SIZE = 4;
    private final static int WIN_X = 950;
    private final static int WIN_Y = 670;

    private final static int HEALTH_X = 20;
    private final static int HEALTH_Y = 25;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 30;
    private final static double HALF = 0.5;
    private final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    private Point position;
    private Point prevPosition;
    private int healthPoints;
    private Image currentImage;
    private boolean facingRight;
    private boolean attackOnCooldown;
    private boolean isAttackState;
    private boolean startTimer;
    private static int invincibleTimer;
    private final static int INVINCIBLE_TIME = 180;
    private final static int ZERO = 0;
    private boolean startAttackTimer;
    private static boolean isInvincible;

    public Player(int startX, int startY){
        this.position = new Point(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = new Image(FAE_RIGHT);
        this.facingRight = true;
        this.attackOnCooldown = false;
        this.isAttackState = false;
        this.isInvincible = false;
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Method that performs state update
     */
    public void update(Input input, ShadowDimension gameObject){
        /** Character's movement across the windows using arrow keys controlled by the gamer **/
        if (input.isDown(Keys.UP)){
            setPrevPosition();
            move(0, -MOVE_SIZE);
        } else if (input.isDown(Keys.DOWN)){
            setPrevPosition();
            move(0, MOVE_SIZE);
        } else if (input.isDown(Keys.LEFT)){
            setPrevPosition();
            move(-MOVE_SIZE,0);
            if (facingRight) {
                this.currentImage = new Image(FAE_LEFT);
                facingRight = !facingRight;
            }
        } else if (input.isDown(Keys.RIGHT)){
            setPrevPosition();
            move(MOVE_SIZE,0);
            if (!facingRight) {
                this.currentImage = new Image(FAE_RIGHT);
                facingRight = !facingRight;
            }
        }

        /** Drawing the Fae's attack state while having a timer for the attack duration and cool down **/
        if(input.wasPressed(Keys.A) && !attackOnCooldown) {
            startAttackTimer = true;
        }
        if (!attackOnCooldown && startAttackTimer) {
            if (attackTimer < ATTACK_DURATION) {
                isAttackState = true;
            } else if (attackTimer > ATTACK_DURATION) {
                isAttackState = false;
            }
        }

        if (startAttackTimer) {
            attackTimer++;
            if (attackTimer >= ATTACK_COOLDOWN) {
                attackOnCooldown = false;
                attackTimer = ZERO;
                startAttackTimer = false;
            }
        }

        //Drawing the left and right image basing on her direction that she is facing
        if (isAttackState && facingRight) {
            this.currentImage = new Image(FAE_ATTACK_RIGHT);
        } else if (isAttackState && !facingRight) {
            this.currentImage = new Image(FAE_ATTACK_LEFT);
        } else if (!isAttackState && facingRight) {
            this.currentImage = new Image(FAE_RIGHT);
        } else if (!isAttackState && !facingRight) {
            this.currentImage = new Image(FAE_LEFT);
        }

        if(isInvincible) {
            startTimer = true;
        }

        /** Making the Player invincible for the duration of 3 seconds and then stop afterward **/
        if (startTimer) {
            invincibleTimer++;
            if (invincibleTimer >= INVINCIBLE_TIME) {
                startTimer = false;
                isInvincible = false;
                invincibleTimer = ZERO;
            }
        }

        this.currentImage.drawFromTopLeft(position.x, position.y);
        gameObject.checkCollisions(this);
        renderHealthPoints();
        gameObject.checkOutOfBounds(this);
    }

    /**
     * Method that stores Fae's previous position
     */
    private void setPrevPosition(){
        this.prevPosition = new Point(position.x, position.y);
    }

    /**
     * Method that moves Fae back to previous position
     */
    public void moveBack(){
        this.position = prevPosition;
    }

    /**
     * Method that moves Fae given the direction
     */
    private void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    private void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, COLOUR);
    }

    /**
     * Method that checks if Fae's health has depleted
     */
    public boolean isDead() {
        return healthPoints <= ZERO;
    }

    /**
     * Method that checks if Fae has found the gate
     */
    public boolean reachedGate(){
        return (this.position.x >= WIN_X) && (this.position.y >= WIN_Y);
    }

    public Point getPosition() {
        return position;
    }
    public Point getCentre() {
        Point pointTopLeft = this.getPosition();
        double centreX, centreY;

        centreX = pointTopLeft.x + (this.getCurrentImage().getWidth()) * HALF;
        centreY = pointTopLeft.y + (this.getCurrentImage().getHeight()) * HALF;
        Point centre = new Point(centreX, centreY);
        return centre;
    }
    public Image getCurrentImage() {
        return currentImage;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public static int getMaxHealthPoints() {
        return MAX_HEALTH_POINTS;
    }

    public int getDamagePoints() { return DAMAGE_POINTS; }

    public boolean isAttackState() { return isAttackState; }

    public static boolean checkInvincible() { return isInvincible; }

    public static void setInvincible(boolean invincible) { isInvincible = invincible; }

}