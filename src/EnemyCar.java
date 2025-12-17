import java.awt.Color;
import java.awt.Graphics;

public class EnemyCar extends GameObject {
    private int lane = -1;
    private int speedY = 4;
    
    public EnemyCar(int x, int y, int width, int height) {
        super(x, y, width, height, 4);  
        this.speedY = 4;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public int getLane() {
        return lane;
    }

    public void setSpeed(int speed) {
        this.speedY = speed;
    }

    public EnemyCar(int x, int y, int width, int height, int speedY) {
        super(x, y, width, height, speedY);
    }

    @Override
    public void update() {
        y += speedY;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }
}