package fr.codinbox.pathy.config;

import java.util.ArrayList;

public final class PathySerializableConfiguration {

    public ArrayList<Path> paths = new ArrayList<>();

    public final static class Path {
        public String name;
        public long creationTime;
        public ArrayList<SerializableLocation> points;
    }

    public final static class SerializableLocation {
        public String world;
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;
    }

}
