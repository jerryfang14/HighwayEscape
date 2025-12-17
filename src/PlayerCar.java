import java.awt.Color;
import java.awt.Graphics;

public class PlayerCar extends GameObject {

    private int laneIndex;
    private int laneWidth;
    private int roadLeftX;
    private int roadRightX;

    public PlayerCar(int laneIndex, int laneWidth, int roadLeftX, int roadRightX, int y, int carWidth, int carHeight) {
        super(0, y, carWidth, carHeight, 0);
        this.laneIndex = laneIndex;
        this.laneWidth = laneWidth;
        this.roadLeftX = roadLeftX;
        this.roadRightX = roadRightX;
        updateXFromLane();
    }

    private void updateXFromLane() {
        this.x = roadLeftX + laneIndex * laneWidth + (laneWidth - width) / 2;
    }

    public void moveLeft() {
        if (laneIndex > 0) {
            laneIndex--;
            updateXFromLane();
        }
    }

    public void moveRight() {
        int maxLanes = (roadRightX - roadLeftX) / laneWidth;
        if (laneIndex < maxLanes - 1) {
            laneIndex++;
            updateXFromLane();
        }
    }

    @Override
    public void update() {
        // Player car doesn't move vertically in this game
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }
}