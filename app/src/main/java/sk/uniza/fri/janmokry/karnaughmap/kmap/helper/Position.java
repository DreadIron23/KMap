package sk.uniza.fri.janmokry.karnaughmap.kmap.helper;

/**
 * Position data holder, just x, y.
 *
 * Created by Janci on 31.3.2017.
 */
public class Position {

    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
