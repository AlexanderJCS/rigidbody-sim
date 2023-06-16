import jangl.coords.NDCoords;
import jangl.coords.PixelCoords;
import jangl.shapes.Shape;

import java.util.List;

public class CollisionHandler {
    private CollisionHandler() {}

    /**
     * Calculates a collision in one dimension across the x-axis. The order of object1 and object2 doesn't matter.
     * @param object1 The first object in the collision.
     * @param object2 The second object in the collision.
     */
    private static void oneDimensionalCollision(SimObject object1, SimObject object2) {
        // Perform a perfectly elastic collision between the two objects
        // For all the formulas: https://courses.lumenlearning.com/boundless-physics/chapter/collisions/

        // Define vars for shorter lines
        float m1 = (float) object1.getMass();
        float m2 = (float) object2.getMass();

        float v1i = object1.getVel().x;
        float v2i = object2.getVel().x;

        // Use equation 6 from the link
        float v2f = ((2 * m1) / (m2 + m1)) * v1i + ((m2 - m1) / (m2 + m1)) * v2i;

        // Apply the final velocities to the objects
        object1.getVel().x = v2f + v2i - v1i;  // equation 4 from the link
        object2.getVel().x = v2f;
    }

    /**
     * Rotates the axis of the SimObject's circle as well as its velocity
     * @param simObject The SimObject to rotate
     * @param theta The angle, in radians, to rotate it by.
     */
    private static void rotateAxis(SimObject simObject, double theta) {
        simObject.getCircle().rotateAxis(theta);

        PixelCoords vel = simObject.getVel();
        float[] velArr = Shape.rotateAxis(
                new float[]{ PixelCoords.distXtoNDC(vel.x), PixelCoords.distYtoNDC(vel.y) }, theta
        );

        vel.x = NDCoords.distXtoPixelCoords(velArr[0]);
        vel.y = NDCoords.distYtoPixelCoords(velArr[1]);
    }

    /**
     * Handles a collision between two objects. This assumes that there is a collision.
     * @param object1 The first object in the collision.
     * @param object2 The second object in the collision.
     */
    private static void handleCollision(SimObject object1, SimObject object2) {
        /*
         * This method simplifies the two-dimensional collision into a one-dimensional collision. Unlike most physics
         * billiard ball equations, we don't know the resulting angle, theta, after the collision, so a different
         * method is used.
         *
         * Follow these steps:
         * Find the angle between the two objects (theta)
         * Rotate the objects and the velocities by theta to align the collision on the x-axis
         * Do the 1D collision between the objects, using the x velocity component for their speed
         * Rotate the objects back.
         * Collision response done!
         */

        PixelCoords object1Coords = object1.getCircle().getCenter().toPixelCoords();
        PixelCoords object2Coords = object2.getCircle().getCenter().toPixelCoords();

        float deltaX = object1Coords.x - object2Coords.x;
        float deltaY = object1Coords.y - object2Coords.y;
        double theta = Math.atan2(deltaY, deltaX);

        rotateAxis(object1, theta);
        rotateAxis(object2, theta);

        oneDimensionalCollision(object1, object2);

        rotateAxis(object1, -theta);
        rotateAxis(object2, -theta);
    }

    /**
     * Handles the event of objects hitting each other.
     * @param objects The objects to be simulated.
     */
    public static void handleCollisions(List<SimObject> objects) {
        for (int i = 0; i < objects.size() - 1; i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                SimObject object1 = objects.get(i);
                SimObject object2 = objects.get(j);

                if (!object1.collidesWith(object2)) {
                    continue;
                }

                handleCollision(objects.get(i), objects.get(j));

                // Move the balls after collision, so they aren't stuck in each other
                for (int k = 0; k < Consts.RESOLUTION; k++) {
                    object1.update();
                    object2.update();
                }

                object1.playHitSound();
            }
        }
    }

    /**
     * Handles when an object goes out of the screen.
     * @param objects The list of objects that are being simulated.
     */
    public static void handleOutOfBounds(List<SimObject> objects) {
        for (SimObject object : objects) {
            // Defining variables here so the calculation code isn't so verbose
            float radiusX = object.getCircle().getRadiusX();
            float radiusY = object.getCircle().getRadiusY();
            NDCoords center = object.getCircle().getCenter();
            PixelCoords vel = object.getVel();

            if ((center.x + radiusX > 1 && vel.x > 0) || (center.x - radiusX < -1 && vel.x < 0)) {
                vel.x *= -1;
            }

            if ((center.y + radiusY > 1 && vel.y > 0) || (center.y - radiusY < -1 && vel.y < 0)) {
                vel.y *= -1;
            }
        }
    }
}
