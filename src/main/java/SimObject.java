import jangl.color.ColorFactory;
import jangl.coords.PixelCoords;
import jangl.graphics.shaders.ColorShader;
import jangl.shapes.Circle;
import jangl.shapes.Shape;
import jangl.sound.Sound;
import jangl.time.Clock;

import java.util.Objects;
import java.util.Random;

public class SimObject {
    private static final Random RANDOM = new Random();

    private final Circle circle;
    private final PixelCoords vel;
    private final double mass;
    private final ColorShader colorShader;
    private final Sound collideSound;

    public SimObject(Circle circle, PixelCoords velocity, double mass) {
        this.circle = circle;
        this.vel = velocity;
        this.mass = mass;
        this.colorShader = new ColorShader(ColorFactory.fromNormalized(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 1));
        this.collideSound = new Sound("src/main/resources/hit.ogg");
        this.collideSound.setVolume(0.2f);
    }

    public void draw() {
        this.circle.draw(this.colorShader);
    }

    public void update() {
        float deltaTime = (float) Clock.getTimeDelta();

        // If the delta time is greater than a specified amount then it is likely the window has been moved
        // since moving the window freezes it. To avoid objects clipping into each other,
        // it should not update if deltaTime is over a specified amount.
        if (deltaTime > 0.1) {
            deltaTime = 0;
        }

        this.circle.shift(
                PixelCoords.distXtoNDC(this.vel.x * deltaTime / Consts.RESOLUTION),
                PixelCoords.distYtoNDC(this.vel.y * deltaTime / Consts.RESOLUTION)
        );
    }

    public Circle getCircle() {
        return circle;
    }

    public PixelCoords getVel() {
        return vel;
    }

    public double getMass() {
        return mass;
    }

    public boolean collidesWith(SimObject other) {
        return Shape.collides(this.getCircle(), other.getCircle());
    }

    public void playHitSound() {
        this.collideSound.play();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimObject simObject = (SimObject) o;
        return Double.compare(simObject.mass, mass) == 0 && circle.equals(simObject.circle) && vel.equals(simObject.vel) && colorShader.equals(simObject.colorShader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(circle, vel, mass, colorShader);
    }
}
