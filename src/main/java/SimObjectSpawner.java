import jangl.coords.NDCoords;
import jangl.coords.PixelCoords;
import jangl.shapes.Circle;

import java.util.List;
import java.util.Random;

public class SimObjectSpawner {
    private static final Random RANDOM = new Random();

    public static SimObject getNewSimObject(List<SimObject> simObjects) {
        double mass = RANDOM.nextDouble(1, 255);
        // a = pi * r^2
        // a / pi = r^2
        // sqrt(a / pi) = r

        float radius = (float) (Math.sqrt(mass / Math.PI)) * 0.01f;

        NDCoords randCoords = new NDCoords(
                RANDOM.nextFloat(-1 + radius + 0.01f, 1 - radius - 0.01f),
                RANDOM.nextFloat(-1 + radius + 0.01f, 1 - radius - 0.01f)
        );

        Circle circle = new Circle(randCoords, radius, 36);

        PixelCoords randVel = new PixelCoords(
                RANDOM.nextFloat(0, 600),
                RANDOM.nextFloat(0, 600)
        );

        for (SimObject simObject : simObjects) {
            if (Circle.collides(simObject.getCircle(), circle)) {
                circle.close();
                return getNewSimObject(simObjects);
            }
        }

        return new SimObject(circle, randVel, mass);
    }
}
