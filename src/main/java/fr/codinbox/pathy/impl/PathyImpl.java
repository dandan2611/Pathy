package fr.codinbox.pathy.impl;

import fr.codinbox.pathy.api.Pathy;
import fr.codinbox.pathy.config.PathySerializableConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;

public class PathyImpl extends Pathy {

    public PathyImpl(PathySerializableConfiguration config) {
        super(config);
        config.paths.forEach(path -> {
            var builder = PathBuilder.create(path.name);

            final String worldName = path.points.get(0).world;
            if (Bukkit.getWorld(worldName) == null) { // Try to load the world
                WorldCreator.name(worldName).createWorld();
            }

            path.points.forEach(p -> builder.addPoint(PointBuilder.create()
                    .setLocation(new Location(Bukkit.getWorld(p.world), p.x, p.y, p.z, p.yaw, p.pitch))
                    .build()));
            getPaths().put(path.name, builder.build());
        });
    }

}
