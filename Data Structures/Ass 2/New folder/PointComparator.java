import java.util.Comparator;


public class PointComparator implements Comparator<Point> {
    Boolean axis;

    public PointComparator(Boolean axis) {
        this.axis = axis;
    }

    public int compare(Point o1, Point o2) {
        if (axis) {
            if (o1.getX() < o2.getX()) {
                return -1;
            } else if (o1.getX() > o2.getX()) {
                return 1;
            } else {
                return 0;
            }
        } else {
            if (o1.getY() < o2.getY()) {
                return -1;
            } else if (o1.getY() > o2.getY()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
