package fr.codinbox.pathy.api;

import fr.codinbox.pathy.api.path.Path;
import fr.codinbox.pathy.api.path.Point;
import fr.codinbox.pathy.api.path.Points;
import fr.codinbox.pathy.config.PathySerializableConfiguration;
import fr.codinbox.pathy.impl.path.PathImpl;
import fr.codinbox.pathy.impl.path.PointImpl;
import fr.codinbox.pathy.impl.path.PointsImpl;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Pathy {

    protected static Pathy instance;

    private PathySerializableConfiguration config;

    private HashMap<String, Path> paths;

    private boolean disablePersistentDataHolder = false;

    protected Pathy(@NotNull PathySerializableConfiguration config) {
        this.config = config;
        this.paths = new HashMap<>();
    }

    public static Pathy api() {
        return instance;
    }

    public static void init(Pathy pathy) {
        instance = pathy;
    }

    public PathySerializableConfiguration getConfig() {
        return config;
    }

    public @NotNull Path createPath(@NotNull String name) {
        var path = PathBuilder.create(name).build();
        paths.put(name, path);
        return path;
    }

    public @NotNull HashMap<@NotNull String, @NotNull Path> getPaths() {
        return paths;
    }

    public @NotNull Path getPath(@NotNull String name) {
        var path = paths.get(name);
        if (path == null) throw new IllegalStateException("Path " + name + " does not exist");
        return path;
    }

    public @NotNull Path clonePath(@NotNull String original, @NotNull String clone, @NotNull World newWorld) {
        var path = getPath(original);
        var points = new ArrayList<Point>();
        for (var point : path.getPoints()) {
            var location = point.getLocation();
            var newLocation = new Location(newWorld, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            points.add(PointBuilder.create().setLocation(newLocation).build());
        }
        var newPath = PathBuilder.create(clone).addPoints(points.toArray(new Point[0])).build();
        paths.put(clone, newPath);
        return newPath;
    }

    /**
     * Disable the saving of the paths in a persistent data holder.
     */
    public void disablePersistentDataHolder() {
        this.disablePersistentDataHolder = true;
    }

    public boolean isDisablePersistentDataHolder() {
        return disablePersistentDataHolder;
    }

    public static final class PointBuilder {

        private Location location;

        private PointBuilder() {
        }

        public static PointBuilder create() {
            return new PointBuilder();
        }

        public PointBuilder setLocation(@NotNull Location location) {
            this.location = location;
            return this;
        }

        public Point build() {
            if (location == null) throw new IllegalStateException("Location cannot be null");
            return new PointImpl(location);
        }

    }

    public static final class PathBuilder {

        private final String name;
        private final ArrayList<Point> points = new ArrayList<>();

        private PathBuilder(String name) {
            this.name = name;
        }

        public static PathBuilder create(@NotNull String name) {
            return new PathBuilder(name);
        }

        private boolean checkWorld(Point point) {
            for (Point p : points)
                if (!p.getLocation().getWorld().getName().equals(point.getLocation().getWorld().getName()))
                    return false;
            return true;
        }

        public PathBuilder addPoint(@NotNull Point point) {
            if (!checkWorld(point)) throw new IllegalStateException("Points must be in the same world");
            this.points.add(point);
            return this;
        }

        public PathBuilder addPoint(@NotNull Location location) {
            var point = PointBuilder.create().setLocation(location).build();
            if (!checkWorld(point)) throw new IllegalStateException("Points must be in the same world");
            this.points.add(point);
            return this;
        }

        public PathBuilder addPoints(@NotNull Point... points) {
            for (Point point : points) {
                if (!checkWorld(point)) throw new IllegalStateException("Points must be in the same world");
                this.points.add(point);
            }
            return this;
        }

        public @NotNull Path build() {
            return new PathImpl(name, points);
        }

    }

    public static final class PointsBuilder {

        private final Point pointOne;
        private final Point pointTwo;

        private PointsBuilder(Point pointOne, Point pointTwo) {
            this.pointOne = pointOne;
            this.pointTwo = pointTwo;
        }

        public static Points of(@NotNull Point pointOne, @NotNull Point pointTwo) {
            return new PointsImpl(pointOne, pointTwo);
        }

    }

}
