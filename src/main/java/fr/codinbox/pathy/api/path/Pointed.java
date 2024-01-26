package fr.codinbox.pathy.api.path;

import org.bukkit.Location;

public class Pointed {

    private final Location location;

    public Pointed(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

}
