package fr.codinbox.pathy.api.path;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

public interface Point {

    @NotNull Location getLocation();

    @NonNegative double distance(@NotNull Point point);

    @NonNegative double distance(@NotNull Location location);

    default @NotNull Vector toVector() {
        return this.getLocation().toVector();
    }

}
