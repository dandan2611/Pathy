package fr.codinbox.pathy.impl.path;

import fr.codinbox.pathy.api.path.Point;
import fr.codinbox.pathy.api.path.Points;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointsImpl implements Points {

    private final Point firstPoint;
    private final Point secondPoint;

    public PointsImpl(Point firstPoint, Point secondPoint) {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    @Override
    public @NotNull Point getFirstPoint() {
        return this.firstPoint;
    }

    @Override
    public @NotNull Point getSecondPoint() {
        return this.secondPoint;
    }

    @Override
    public @NotNull Vector toVector() {
        return this.secondPoint.getLocation().toVector().subtract(this.firstPoint.getLocation().toVector());
    }

}
