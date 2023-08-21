import java.util.*;

class Point implements Comparable<Point> {
    int radiusSquared;
    int x, y, z;

    public Point(int radiusSquared, int x, int y, int z) {
        this.radiusSquared = radiusSquared;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int compareTo(Point other) {
        if (radiusSquared != other.radiusSquared) {
            return Integer.compare(radiusSquared, other.radiusSquared);
        }
        if (x != other.x) {
            return Integer.compare(x, other.x);
        }
        if (y != other.y) {
            return Integer.compare(y, other.y);
        }
        return Integer.compare(z, other.z);
    }
}

public class PointGenerator {
    public static List<Point> generatePoints(int numPoints, int r2) {
        PriorityQueue<Point> priorityQueue = new PriorityQueue<>();
        Set<Point> visitedPoints = new HashSet<>();
        List<Point> generatedPoints = new ArrayList<>();

        priorityQueue.offer(new Point(0, 0, 0, 0));
        visitedPoints.add(new Point(0, 0, 0, 0));

        while (generatedPoints.size() < numPoints && !priorityQueue.isEmpty()) {
            Point current = priorityQueue.poll();
            generatedPoints.add(current);

            for (int i = 0; i < 3; i++) {
                int[] dx = {0, 0, 0};
                dx[i] = 1;

                int newX = current.x + dx[0];
                int newY = current.y + dx[1];
                int newZ = current.z + dx[2];

                int newRadiusSquared = newX * newX + newY * newY + newZ * newZ;

                Point newPoint = new Point(newRadiusSquared, newX, newY, newZ);
                if (!visitedPoints.contains(newPoint) && newRadiusSquared <= r2) {
                    visitedPoints.add(newPoint);
                    priorityQueue.offer(newPoint);
                }
            }
        }

        return generatedPoints;
    }

    public static void main(String[] args) {
        int numPoints = 1000; // Change this to the desired number of points
        int r2 = 100;       // Change this to the desired squared radius threshold

        List<Point> points = generatePoints(numPoints, r2);
        for (Point point : points) {
            System.out.println("(" + point.x + ", " + point.y + ", " + point.z + ")");
        }
    }
}