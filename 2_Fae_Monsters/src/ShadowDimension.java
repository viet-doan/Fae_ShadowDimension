import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.DrawOptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import static java.lang.Math.PI;

/**
 * SWEN20003 Project 1, Semester 2, 2022
 *
 * @author Viet Doan
 *  Since I was not able to completely finished project 1 fully. I have
 *  used the solution code of the project 1 for my project 2 so that I can
 *  work on the project and implement my own code for the assestment of project 2
 */
public class ShadowDimension extends AbstractGame {
    private static int timer;
    private final static int THREE_SECONDS = 180;
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final static String LVL_ZERO_FILE = "res/level0.csv";
    private final static String LVL_ONE_FILE = "res/level1.csv";
    private final Image BACKGROUND_ZERO = new Image("res/background0.png");
    private final Image BACKGROUND_ONE = new Image("res/background1.png");
    private final Image navecFire = new Image("res/navec/navecFire.png");
    private final Image demonFire = new Image("res/demon/demonFire.png");
    private final static int TITLE_FONT_SIZE = 75;
    private final static int INSTRUCTION_FONT_SIZE = 40;
    private final static int TITLE_X = 260;
    private final static int TITLE_Y = 250;
    private final static int INS_X_OFFSET = 90;
    private final static int INS_Y_OFFSET = 190;
    private final static int INSTRUCTION_X = 350;
    private final static int INSTRUCTION_Y = 350;

    private final DrawOptions ROTATION = new DrawOptions();
    private final static double TOP_LEFT = (0) ;
    private final static double TOP_RIGHT = (1.0/2.0) * PI;
    private final static double BOTTOM_RIGHT = PI;
    private final static double BOTTOM_LEFT = (3.0/2.0) * PI;

    private final Font TITLE_FONT = new Font("res/frostbite.ttf", TITLE_FONT_SIZE);
    private final Font INSTRUCTION_FONT = new Font("res/frostbite.ttf", INSTRUCTION_FONT_SIZE);
    private final static String INSTRUCTION_MESSAGE_ZERO = "PRESS SPACE TO START\nUSE ARROW KEYS TO FIND GATE";
    private final static String INSTRUCTION_MESSAGE_ONE = "PRESS SPACE TO START\n" +
            "PRESS A TO ATTACK\nDEFEAT NAVEC TO WIN";
    private final static String END_MESSAGE = "GAME OVER!";
    private final static String LEVEL_MESSAGE = "LEVEL COMPLETE!";
    private final static String WIN_MESSAGE = "CONGRATULATIONS!";

    private final static int WALL_ARRAY_SIZE = 52;
    private final static int S_HOLE_ARRAY_SIZE = 5;
    private final static int TREE_ARRAY_SIZE = 15;
    private final static int DEMON_ARRAY_SIZE = 5;
    private final static int NAVEC_ATTACK_RANGE = 200;
    private final static int DEMON_ATTACK_RANGE = 150;
    private final static Wall[] walls = new Wall[WALL_ARRAY_SIZE];
    private final static Sinkhole[] sinkholes = new Sinkhole[S_HOLE_ARRAY_SIZE];
    private final static Tree[] trees = new Tree[TREE_ARRAY_SIZE];
    private final static Demon[] demons = new Demon[DEMON_ARRAY_SIZE];
    private Point topLeft;
    private Point bottomRight;
    private Player player;
    private Navec navec;
    private Demon demon;
    private boolean hasStarted;
    private boolean gameOver;
    private boolean playerWin;
    private boolean roundEnded;
    private boolean isLevelZero;
    private boolean isLevelOne;
    private static int timescale = 0;
    private final static int MAX_TIMESCALE = 3;
    private final static int MIN_TIMESCALE = -3;

    public ShadowDimension(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        hasStarted = false;
        gameOver = false;
        playerWin = false;
        roundEnded = false;
        isLevelZero = true;
        isLevelOne = false;
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDimension game = new ShadowDimension();
        game.run();
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    public void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        if (input.wasPressed(Keys.W)) {
            roundEnded = true;
            isLevelZero = false;
        }

        if (!hasStarted && isLevelZero) {
            readCSV(LVL_ZERO_FILE);
            drawStartScreen();
            if (input.wasPressed(Keys.SPACE)) {
                //Starting level zero after pressing SPACE key
                hasStarted = true;
            }
        } else if (roundEnded && !isLevelZero && timer > THREE_SECONDS) {
            // Initialising level one after pressing SPACE key
            if (input.wasPressed(Keys.SPACE)) {
                readCSV(LVL_ONE_FILE);
                hasStarted = true;
                roundEnded = false;
                isLevelOne = true;
            }
        }

        // Transitioning after level 0 is completed
        if (roundEnded) {
            timer++;
            if (timer <= THREE_SECONDS) {
                drawMessage(LEVEL_MESSAGE);
            }
            if (timer > THREE_SECONDS) {
                drawNextLevel();
            }
        }

        // End screen message for either winning or losing condition
        if (gameOver) {
            drawMessage(END_MESSAGE);
        } else if (playerWin) {
            drawMessage(WIN_MESSAGE);
        }

        // Initializing Level Zero
        if (hasStarted && !gameOver && !playerWin && !roundEnded && isLevelZero) {
            BACKGROUND_ZERO.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

            for (Wall current : walls) {
                current.update();
            }
            for (Sinkhole current : sinkholes) {
                current.update();
            }

            player.update(input, this);

            if (player.isDead()) {
                gameOver = true;
            }

            if (player.reachedGate()) {
                roundEnded = true;
                isLevelZero = false;
            }
        }

        // Initializing Level One
        if (hasStarted && !gameOver && !playerWin && !roundEnded && isLevelOne) {
            BACKGROUND_ONE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

            for (Tree current : trees) {
                current.update();
            }
            for (Sinkhole current : sinkholes) {
                current.update();
            }
            for (Demon current: demons) {
                current.update(this);
            }

            player.update(input, this);
            navec.update(this);


            if (player.isDead()) {
            gameOver = true;
            }


            if (Navec.isDead()) {
                playerWin = true;
            }
        }

        if (isLevelOne && input.wasPressed(Keys.K) && timescale < MAX_TIMESCALE) {
            timescale++;
            demon.adjustTimescale(timescale);
            navec.adjustTimescale(timescale);
            System.out.println("Speed up, Speed: " + timescale);
        } else if (isLevelOne && input.wasPressed(Keys.L) && timescale > MIN_TIMESCALE) {
            timescale--;
            demon.adjustTimescale(timescale);
            navec.adjustTimescale(timescale);
            System.out.println("Slow Down, Speed: " + timescale);
        }
    }

    /** Method that checks if Fae is within the Navec's attack range or not **/
    public void inNavecRange(Navec navec) {
        Point navecPosition = navec.getPosition();
        Point navecCentre = navec.getCentre();
        Point playerPosition = player.getCentre();

        double distanceX = navecCentre.x - playerPosition.x;
        double distanceY = navecCentre.y - playerPosition.y;
        double distance = Math.sqrt(distanceX*distanceX + distanceY*distanceY);

        if (distance <= NAVEC_ATTACK_RANGE) {
            drawFireNavec(navecCentre, navecPosition, playerPosition, navec);
        }
    }

    /** Method that checks if Fae is within the Demon's attack range or not **/
    public void inDemonRange(Demon demon) {
        Point demonPosition = demon.getPosition();
        Point demonCentre = demon.getPosition();
        Point playerPosition = player.getPosition();

        double distanceX = demonCentre.x - playerPosition.x;
        double distanceY = demonCentre.y - playerPosition.y;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if (distance <= DEMON_ATTACK_RANGE) {
            drawFireDemon(demonCentre, demonPosition, playerPosition, demon);
        }
    }

    /**
     * Method that checks for collisions between Fae and the other entities, and performs
     * corresponding actions.
     */
    public void checkCollisions(Player player) {
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());

        if (isLevelZero) {
            for (Wall current : walls) {
                Rectangle wallBox = current.getBoundingBox();
                if (faeBox.intersects(wallBox)) {
                    player.moveBack();
                }
            }

            for (Sinkhole hole : sinkholes){
                Rectangle holeBox = hole.getBoundingBox();
                if (hole.isActive() && faeBox.intersects(holeBox)){
                    player.setHealthPoints(Math.max(player.getHealthPoints() - hole.getDamagePoints(), 0));
                    player.moveBack();
                    hole.setActive(false);
                    System.out.println("Sinkhole inflicts " + hole.getDamagePoints() + " damage points on Fae. " +
                            "Fae's current health: " + player.getHealthPoints() + "/" + Player.getMaxHealthPoints());
                }
            }
        }

        if (isLevelOne) {
            for (Tree current : trees) {
                Rectangle treeBox = current.getBoundingBox();
                if (faeBox.intersects(treeBox)) {
                    player.moveBack();
                }
            }

            for (Sinkhole hole : sinkholes){
                Rectangle holeBox = hole.getBoundingBox();
                if (hole.isActive() && faeBox.intersects(holeBox)){
                    player.setHealthPoints(Math.max(player.getHealthPoints() - hole.getDamagePoints(), 0));
                    player.moveBack();
                    hole.setActive(false);
                    System.out.println("Sinkhole inflicts " + hole.getDamagePoints() + " damage points on Fae. " +
                            "Fae's current health: " + player.getHealthPoints() + "/" + Player.getMaxHealthPoints());
                }
            }

            //Check if the demon is being attacked by the player Fae or not
            for (Demon demon : demons){
                Rectangle demonBox = new Rectangle(demon.getPosition(), demon.getCurrentImage().getWidth(),
                        demon.getCurrentImage().getHeight());;
                if (!demon.checkInvincible() && !demon.isDead() && faeBox.intersects(demonBox) && player.isAttackState()){
                    player.setHealthPoints(Math.max(player.getHealthPoints() - demon.getDamagePoints(), 0));
                    demon.setInvincible(true);
                    demon.setHealthPoints(Math.max(demon.getHealthPoints() - player.getDamagePoints(), 0));
                    System.out.println("Fae inflicts " + player.getDamagePoints() + " damage points on Demon. " +
                            "Demon's current health: " + demon.getHealthPoints() + "/" + demon.getMaxHealthPoints());
                }
            }
        }
    }

    /** Method that draws fire when player Fae is in range of Navec's attack range **/
    public void drawFireNavec(Point navecCentre, Point navecPosition, Point playerCentre,Navec navec){
        double topSide, bottomSide, leftSide, rightSide;
        boolean isDemon = false;
        topSide = navecPosition.y - navecFire.getHeight();
        bottomSide = navecPosition.y + navec.getCurrentImage().getHeight();
        leftSide = navecPosition.x - navecFire.getWidth();
        rightSide = navecPosition.x + navec.getCurrentImage().getWidth();

        if (playerCentre.x <= navecCentre.x && playerCentre.y <= navecCentre.y) {
            navecFire.drawFromTopLeft(leftSide, topSide, ROTATION.setRotation(TOP_LEFT));
            checkFireCollisions(new Point(leftSide, topSide), isDemon);
        } else if (playerCentre.x <= navecCentre.x && playerCentre.y > navecCentre.y) {
            navecFire.drawFromTopLeft(leftSide, bottomSide, ROTATION.setRotation(BOTTOM_LEFT));
            checkFireCollisions(new Point(leftSide, bottomSide), isDemon);
        } else if (playerCentre.x > navecCentre.x && playerCentre.y <= navecCentre.y) {
            navecFire.drawFromTopLeft(rightSide, topSide, ROTATION.setRotation(TOP_RIGHT));
            checkFireCollisions(new Point(rightSide, topSide), isDemon);
        } else if (playerCentre.x > navecCentre.x && playerCentre.y > navecCentre.y) {
            navecFire.drawFromTopLeft(rightSide, bottomSide, ROTATION.setRotation(BOTTOM_RIGHT));
            checkFireCollisions(new Point(rightSide, bottomSide), isDemon);
        }
    }

    /** Method that draws fire when player Fae is in range of Demon's attack range **/
    public void drawFireDemon(Point demonCentre, Point demonPosition, Point playerCentre, Demon demon) {
        double topSide, bottomSide, leftSide, rightSide;
        boolean isDemon = true;

        topSide = demonPosition.y - demonFire.getHeight();
        bottomSide = demonPosition.y + demon.getCurrentImage().getHeight();
        leftSide = demonPosition.x - demonFire.getWidth();
        rightSide = demonPosition.x + demon.getCurrentImage().getWidth();

        if (playerCentre.x <= demonCentre.x && playerCentre.y <= demonCentre.y) {
            demonFire.drawFromTopLeft(leftSide, topSide, ROTATION.setRotation(TOP_LEFT));
            checkFireCollisions(new Point(leftSide, topSide), isDemon);
        } else if (playerCentre.x <= demonCentre.x && playerCentre.y > demonCentre.y) {
            demonFire.drawFromTopLeft(leftSide, bottomSide, ROTATION.setRotation(BOTTOM_LEFT));
            checkFireCollisions(new Point(leftSide, bottomSide), isDemon);
        } else if (playerCentre.x > demonCentre.x && playerCentre.y <= demonCentre.y) {
            demonFire.drawFromTopLeft(rightSide, topSide, ROTATION.setRotation(TOP_RIGHT));
            checkFireCollisions(new Point(rightSide, topSide), isDemon);
        } else if (playerCentre.x > demonCentre.x && playerCentre.y > demonCentre.y) {
            demonFire.drawFromTopLeft(rightSide, bottomSide, ROTATION.setRotation(BOTTOM_RIGHT));
            checkFireCollisions(new Point(rightSide, bottomSide), isDemon);
        }

    }

    /**
     * Method to check when player Fae collide with the fire from Demon or Navec and
     * the player is taken damage by it, while print out a message with the details
     * of the damage taken by which enemy and how much is taken.
     */
    public void checkFireCollisions(Point firePoint, boolean isDemon) {
        double fireWidth = demonFire.getWidth(), fireHeight = demonFire.getHeight();
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());
        Rectangle fireBox = new Rectangle(firePoint, fireWidth, fireHeight);

        if (fireBox.intersects(faeBox) && isDemon && !player.checkInvincible()) {
            player.setInvincible(true);
            player.setHealthPoints(Math.max(player.getHealthPoints() - demon.getDamagePoints(), 0));
            System.out.println("Demon inflicts " + demon.getDamagePoints() + " damage points on Fae. " +
                    "Fae's current health: " + player.getHealthPoints() + "/" + player.getMaxHealthPoints());
        }

        if (fireBox.intersects(faeBox) && !isDemon && !player.checkInvincible()) {
            player.setInvincible(true);
            player.setHealthPoints(Math.max(player.getHealthPoints() - navec.getDamagePoints(), 0));
            System.out.println("Navec inflicts " + navec.getDamagePoints() + " damage points on Fae. " +
                    "Fae's current health: " + player.getHealthPoints() + "/" + player.getMaxHealthPoints());
        }
    }

    /** Method that checks for collisions between Navec with sinkholes and trees **/
    public void navecCheckCollisions(Navec navec) {
        Rectangle navecBox = new Rectangle(navec.getPosition(), navec.getCurrentImage().getWidth(),
                navec.getCurrentImage().getHeight());
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());

        for (Sinkhole hole : sinkholes) {
            Rectangle holeBox = hole.getBoundingBox();
            if (hole.isActive() && navecBox.intersects(holeBox)) {
                navec.moveBack();
            }
        }
        for (Tree current : trees) {
            Rectangle treeBox = current.getBoundingBox();
            if (navecBox.intersects(treeBox)) {
                navec.moveBack();
            }
        }

        // Check if the navec is being attacked by the player Fae or not
        if (player.isAttackState() && navecBox.intersects(faeBox) && !navec.checkInvincible()) {
            navec.isAttacked();
            navec.setHealthPoints(Math.max(navec.getHealthPoints() - player.getDamagePoints(), 0));
            System.out.println("Fae inflicts " + player.getDamagePoints() + " damage points on Navec. " +
                    "Navec's current health: " + navec.getHealthPoints() + "/" + navec.getMaxHealthPoints());
        }
    }

    /** Method that checks for collisions between Demons with sinkholes and trees **/
    public void demonsCheckCollisions(Demon demons) {
        Rectangle demonBox = new Rectangle(demons.getPosition(), demons.getCurrentImage().getWidth(),
                navec.getCurrentImage().getHeight());
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());

        for (Sinkhole hole : sinkholes) {
            Rectangle holeBox = hole.getBoundingBox();
            if (hole.isActive() && demonBox.intersects(holeBox)) {
                demons.moveBack();
            }
        }
        for (Tree current : trees) {
            Rectangle treeBox = current.getBoundingBox();
            if (demonBox.intersects(treeBox)) {
                demons.moveBack();
            }
        }
    }

    /** Method that checks if Fae has gone out-of-bounds and performs corresponding action **/
    public void checkOutOfBounds(Player player){
        Point currentPosition = player.getPosition();
        if ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeft.y) || (currentPosition.x < topLeft.x)
                || (currentPosition.x > bottomRight.x)){
            player.moveBack();
        }
    }

    /** Method used to check if Navec are within the bounds of the screen **/
    public void navecCheckOutOfBounds(Navec navec){
        Point currentPosition = navec.getPosition();
        if ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeft.y) || (currentPosition.x < topLeft.x)
                || (currentPosition.x > bottomRight.x)){
            navec.moveBack();
        }
    }

    /** Method used to check if the demons are within the bounds of the screen **/
    public void demonCheckOutOfBounds(Demon demon){
        Point currentPosition = demon.getPosition();
        if ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeft.y) || (currentPosition.x < topLeft.x)
                || (currentPosition.x > bottomRight.x)){
            demon.moveBack();
        }
    }

    /** Method used to draw the start screen title and instructions **/
    private void drawStartScreen(){
        TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
        INSTRUCTION_FONT.drawString(INSTRUCTION_MESSAGE_ZERO,TITLE_X + INS_X_OFFSET, TITLE_Y + INS_Y_OFFSET);
    }

    /** Method used to draw end screen messages **/
    private void drawMessage(String message){
        TITLE_FONT.drawString(message, (Window.getWidth()/2.0 - (TITLE_FONT.getWidth(message)/2.0)),
                (Window.getHeight()/2.0 + (TITLE_FONT_SIZE/2.0)));
    }

    /** Method to draw the instruction before level 1 starts **/
    private void drawNextLevel(){
        INSTRUCTION_FONT.drawString(INSTRUCTION_MESSAGE_ONE, INSTRUCTION_X, INSTRUCTION_Y);
    }

    /** Method to read the CSV file in cases for level Zero or level One **/
    private void readCSV(String levelFile){
        try (BufferedReader reader = new BufferedReader(new FileReader(levelFile))) {

            String line;
            int currentSinkholeCount = 0;
            int currentTreeCount = 0;
            int currentDemonCount = 0;
            int currentWallCount = 0;

            while ((line = reader.readLine()) != null) {
                String[] sections = line.split(",");
                int xCoord = Integer.parseInt(sections[1]);
                int yCoord = Integer.parseInt(sections[2]);

                switch (sections[0]) {
                    case "Player":
                        player = new Player(xCoord, yCoord);
                        break;
                    case "Wall":
                        walls[currentWallCount] = new Wall(xCoord, yCoord);
                        currentWallCount++;
                        break;
                    case "Sinkhole":
                        sinkholes[currentSinkholeCount] = new Sinkhole(xCoord, yCoord);
                        currentSinkholeCount++;
                        break;
                    case "Tree":
                        trees[currentTreeCount] = new Tree(xCoord, yCoord);
                        currentTreeCount++;
                        break;
                    case "Navec":
                        navec = new Navec(xCoord, yCoord);
                        navec.setDirection();
                        break;
                    case "Demon":
                        demons[currentDemonCount] = new Demon(xCoord, yCoord);
                        currentDemonCount++;
                        break;
                    case "TopLeft":
                        topLeft = new Point(xCoord, yCoord);
                        break;
                    case "BottomRight":
                        bottomRight = new Point(xCoord, yCoord);
                        break;
                }
            }
        } catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
