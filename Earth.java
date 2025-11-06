package tp.dataflight;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class Earth extends Group {
    private final Sphere sph;
    private final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    public static final double RADIUS = 300.0;

    public Earth() {
        sph = new Sphere(RADIUS);
        sph.getTransforms().add(ry);
        // Texture
        PhongMaterial mat = new PhongMaterial();
        // Use a commonly available world map texture URL if you have one locally; here we embed a small fallback color
        // Better: put "earth.jpg" into resources and load via getResourceAsStream. Placeholder transparent.
        Image tex = new Image("https://upload.wikimedia.org/wikipedia/commons/8/80/Equirectangular_projection_SW.jpg", 2048, 1024, true, true);
        mat.setDiffuseMap(tex);
        sph.setMaterial(mat);

        this.getChildren().add(sph);

        // Rotation animation: one full turn in 15s
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long time) {
                double seconds = time / 1_000_000_000.0;
                double angle = (seconds % 15.0) / 15.0 * 360.0;
                ry.setAngle(angle);
            }
        };
        timer.start();
    }

    public Sphere createSphere(Aeroport a, Color color) {
        Sphere s = new Sphere(2.0);
        s.setMaterial(new PhongMaterial(color));
        double theta = Math.toRadians(a.getLatitude());  // Θ
        double phi = Math.toRadians(a.getLongitude());   // Φ
        double x = RADIUS * Math.cos(theta) * Math.sin(phi);
        double y = -RADIUS * Math.sin(theta);
        double z = -RADIUS * Math.cos(theta) * Math.cos(phi);
        s.setTranslateX(x);
        s.setTranslateY(y);
        s.setTranslateZ(z);
        return s;
    }

    public void displayRedSphere(Aeroport a) {
        if (a == null) return;
        this.getChildren().add(createSphere(a, Color.RED));
    }

    public void displayYellowSphere(Aeroport a) {
        if (a == null) return;
        this.getChildren().add(createSphere(a, Color.YELLOW));
    }

    public Sphere getGlobe() {
        return sph;
    }

    public static double[] uvToLatLon(Point2D uv) {
        double u = uv.getX(); // 0..1
        double v = uv.getY(); // 0..1
        double lat = 180.0 * (0.5 - v);
        double lon = 360.0 * (u - 0.5);
        return new double[]{lat, lon};
    }

    public static Point2D pickUV(Node node, PickResult pr) {
        if (pr == null || pr.getIntersectedNode() == null) return null;
        return pr.getIntersectedTexCoord();
    }
}
