import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Sinkhole {
    private final Image SINKHOLE = new Image("res/sinkhole.png");
    private final static int DAMAGE_POINTS = 30;
    private final Point position;
    private boolean isActive;

    public Sinkhole(int startX, int startY){
        this.position = new Point(startX, startY);
        this.isActive = true;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        if (isActive){
            SINKHOLE.drawFromTopLeft(this.position.x, this.position.y);
        }
    }

    public Rectangle getBoundingBox(){
        return new Rectangle(position, SINKHOLE.getWidth(), SINKHOLE.getHeight());
    }

    public int getDamagePoints(){
        return DAMAGE_POINTS;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}