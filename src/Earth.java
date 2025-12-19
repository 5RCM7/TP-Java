import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import java.io.InputStream;
import java.util.Objects;

public class Earth extends Group {
    public static final double RADIUS = 300.0;
    private final Sphere globe = new Sphere(RADIUS);
    private final Rotate rotY = new Rotate(0, Rotate.Y_AXIS);

    public Earth() {
        PhongMaterial mat = new PhongMaterial();

        // Chargement SÉCURISÉ de la texture depuis le classpath
        InputStream is = Objects.requireNonNull(
                getClass().getResourceAsStream("/textures/earth_4k.png"),
                "Texture not found on classpath: /textures/earth_4k.png"
        );
        Image texture = new Image(is);

        mat.setDiffuseMap(texture);
        globe.setMaterial(mat);

        getTransforms().add(rotY);
        getChildren().add(globe);

        // Rotation : 360° en ~15 s
        new AnimationTimer() {
            @Override public void handle(long t) {
                double secs = t / 1_000_000_000.0;
                double angle = (secs * (360.0 / 15.0)) % 360.0;
                rotY.setAngle(angle);
            }
        }.start();
    }

    public Sphere createSphere(Aeroport a, Color color) {
        Sphere s = new Sphere(2.5);
        s.setMaterial(new PhongMaterial(color));

        double lat = Math.toRadians(a.getLatitude());
        double lon = Math.toRadians(a.getLongitude());

        double x =  RADIUS * Math.cos(lat) * Math.sin(lon);
        double y = -RADIUS * Math.sin(lat);
        double z = -RADIUS * Math.cos(lat) * Math.cos(lon);

        s.setTranslateX(x);
        s.setTranslateY(y);
        s.setTranslateZ(z);
        return s;
    }

    public void displayRedSphere(Aeroport a) {
        getChildren().add(createSphere(a, Color.RED));
    }

    public static double[] texToLonLat(Point2D tex) {
        double u = tex.getX();
        double v = tex.getY();
        double lat = 180.0 * (0.5 - v);
        double lon = 360.0 * (u - 0.5);
        return new double[]{lon, lat};
    }
    public void displayYellowSphere(Aeroport a) {
        getChildren().add(createSphere(a, Color.YELLOW));
    }

}
