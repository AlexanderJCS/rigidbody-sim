import jangl.JANGL;
import jangl.io.Window;

import java.util.ArrayList;
import java.util.List;

public class RigidbodySim implements AutoCloseable {
    private final List<SimObject> simObjects;

    public RigidbodySim() {
        this.simObjects = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            this.simObjects.add(
                    SimObjectSpawner.getNewSimObject(this.simObjects)
            );
        }
    }

    private void draw() {
        Window.clear();

        for (SimObject object : this.simObjects) {
            object.draw();
        }
    }

    private void update() {
        CollisionHandler.handleOutOfBounds(this.simObjects);
        CollisionHandler.handleCollisions(this.simObjects);

        for (SimObject object : this.simObjects) {
            object.update();
        }
    }

    public void run() {
        while (Window.shouldRun()) {
            this.draw();

            for (int i = 0; i < Consts.RESOLUTION; i++) {
                this.update();
            }

            JANGL.update();
        }
    }

    @Override
    public void close() {
        for (SimObject simObject : this.simObjects) {
            try {
                simObject.getCircle().close();
            } catch (Exception e) {  // Exception is thrown by AutoCloseable for some reason
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        JANGL.init(1600, 900);
        Window.setVsync(true);

        RigidbodySim sim = new RigidbodySim();
        sim.run();
        sim.close();

        Window.close();
    }
}
