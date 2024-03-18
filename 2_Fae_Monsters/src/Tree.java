import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Tree {
    private final Image TREE = new Image("res/tree.png");
    private final Point position;

    public Tree(int startX, int startY){
        this.position = new Point(startX, startY);
    }

    /**
     * Method that performs state update
     */
    public void update() { TREE.drawFromTopLeft(this.position.x, this.position.y);
    }

    public Rectangle getBoundingBox(){
        return new Rectangle(position, TREE.getWidth(), TREE.getHeight());
    }
}