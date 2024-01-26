package fr.codinbox.pathy.api.path;

import fr.codinbox.pathy.api.Pathy;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface Points {

    static Points of(@NotNull Point firstPoint, @NotNull Point secondPoint) {
        return Pathy.PointsBuilder.of(firstPoint, secondPoint);
    }

    @NotNull Point getFirstPoint();

    @NotNull Point getSecondPoint();

    @NotNull Vector toVector();

}
