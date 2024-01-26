package fr.codinbox.pathy.impl.path;

import fr.codinbox.pathy.api.path.Point;
import org.bukkit.Location;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

public class PointImpl implements Point {

    private final Location location;

    public PointImpl(Location location) {
        this.location = location;
    }

    @Override
    public @NotNull Location getLocation() {
        return this.location;
    }

    @Override
    public @NonNegative double distance(@NotNull Point point) {
        return this.location.distance(point.getLocation());
    }

    @Override
    public @NonNegative double distance(@NotNull Location location) {
        return this.location.distance(location);
    }

}
