package fr.codinbox.pathy.impl.path;

import fr.codinbox.pathy.api.Pathy;
import fr.codinbox.pathy.api.path.Path;
import fr.codinbox.pathy.api.path.Point;
import fr.codinbox.pathy.api.path.Pointed;
import fr.codinbox.pathy.api.path.Points;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PathImpl implements Path {

    private final String name;
    private final @NotNull ArrayList<@NotNull Point> points;

    public PathImpl(String name, ArrayList<Point> points) {
        this.name = name;
        this.points = points;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public @NotNull ArrayList<@NotNull Point> getPoints() {
        return this.points;
    }

    @Override
    public @Nullable Pointed getPointFromStart(double start) {
        if (points.size() < 2)
            return null;

        double distanceWalked = 0;
        Point previousPoint = null;
        for (Point point : this.points) {
            if (previousPoint == null) {
                previousPoint = point;
                continue;
            }
            double distance = previousPoint.distance(point);
            if (distanceWalked + distance >= start) {
                double distanceLeft = Math.abs(start - distanceWalked);
                Points points = Pathy.PointsBuilder.of(previousPoint, point);
                Vector vector = points.toVector();
                vector.normalize();
                vector.multiply(distanceLeft).add(previousPoint.toVector());
                return new Pointed(vector.toLocation(previousPoint.getLocation().getWorld()));
            }
            distanceWalked += distance;
            previousPoint = point;
        }
        return null;
    }

    @Override
    public @Nullable ArrayList<@NotNull Pointed> getPointsEach(double start, double step) {
        if (points.size() < 2)
            return null;

        ArrayList<Pointed> pointedList = new ArrayList<>();
        double d = start;

        while (true) {
            var pointed = this.getPointFromStart(d);
            if (pointed == null)
                break;
            pointedList.add(pointed);
            d += step;
        }
        return pointedList;
    }
}
