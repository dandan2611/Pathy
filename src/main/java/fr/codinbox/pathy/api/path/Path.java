package fr.codinbox.pathy.api.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public interface Path {

    @NotNull String getName();

    @NotNull ArrayList<@NotNull Point> getPoints();

    @Nullable Pointed getPointFromStart(double start);

    /**
     * Get points each step from a start offset.
     * @param start start offset
     * @param step step
     * @return points each step, or null of the path does not have enough points
     */
    @Nullable ArrayList<Pointed> getPointsEach(double start, double step);

}
