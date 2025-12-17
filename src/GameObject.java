import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GameObject {

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int speedY; // vertical speed for enemies; 0 for player

    public GameObject(int x, int y, int width, int height, int speedY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speedY = speedY;
    }

    public abstract void update();        // movement / logic
    public abstract void draw(Graphics g); // drawing

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean intersects(GameObject other) {
        return getBounds().intersects(other.getBounds());
    }

    // Getters & setters
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
}