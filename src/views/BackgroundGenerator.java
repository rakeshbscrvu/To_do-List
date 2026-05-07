package utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.SnapshotParameters;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class BackgroundGenerator {

    public static Background createTickBackground(double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Deep charcoal background
        LinearGradient bg = new LinearGradient(0,0,1,1,true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#0a0a0f")),
            new Stop(0.5, Color.web("#080810")),
            new Stop(1.0, Color.web("#050508"))
        );
        gc.setFill(bg);
        gc.fillRect(0, 0, width, height);

        // Subtle teal glow bottom-left
        for (int r = 400; r > 0; r -= 20) {
            gc.setFill(Color.rgb(0, 212, 170, 0.002 * (400-r)/400.0));
            gc.fillOval(-r/2.0, height - r, r*2, r*2);
        }

        // Big watermark tick
        double cx = width / 2.0, cy = height / 2.0;
        double size = Math.min(width, height) * 0.58;
        double x1 = cx - size*0.50, y1 = cy - size*0.05;
        double x2 = cx - size*0.08, y2 = cy + size*0.40;
        double x3 = cx + size*0.55, y3 = cy - size*0.45;

        double[][] glows = {{100,0.008},{70,0.015},{45,0.025},{28,0.038},{15,0.052}};
        for (double[] g : glows) {
            gc.setLineWidth(g[0]);
            gc.setStroke(Color.rgb(0, 212, 170, g[1]));
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setLineJoin(StrokeLineJoin.ROUND);
            gc.beginPath(); gc.moveTo(x1,y1); gc.lineTo(x2,y2); gc.lineTo(x3,y3); gc.stroke();
        }

        gc.setLineWidth(5);
        gc.setStroke(Color.rgb(0, 212, 170, 0.08));
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.beginPath(); gc.moveTo(x1,y1); gc.lineTo(x2,y2); gc.lineTo(x3,y3); gc.stroke();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = canvas.snapshot(params, null);

        return new Background(new BackgroundImage(image,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)));
    }
}
