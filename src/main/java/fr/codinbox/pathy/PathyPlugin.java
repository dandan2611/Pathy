package fr.codinbox.pathy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.codinbox.pathy.api.Pathy;
import fr.codinbox.pathy.api.path.Path;
import fr.codinbox.pathy.command.PathyCommand;
import fr.codinbox.pathy.config.PathySerializableConfiguration;
import fr.codinbox.pathy.impl.PathyImpl;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class PathyPlugin extends JavaPlugin {

    public static final String DATA_FILE_NAME = "data.json";

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private File dataFile;

    @Override
    public void onEnable() {
        var dataFolder = getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs())
            throw new RuntimeException("Could not create data folder");

        dataFile = new File(dataFolder, DATA_FILE_NAME);
        if (!dataFile.exists()) {
            try {
                var writer = new FileWriter(dataFile);
                GSON.toJson(new PathySerializableConfiguration(), writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("Could not create data file", e);
            }
        }
        PathySerializableConfiguration config;
        try {
            var reader = new FileReader(dataFile);
            config = GSON.fromJson(reader, PathySerializableConfiguration.class);
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var impl = new PathyImpl(config);
        Pathy.init(impl);

        getLogger().info("Loaded configuration with " + impl.getConfig().paths.size() + " paths");

        Objects.requireNonNull(getCommand("pathy")).setExecutor(new PathyCommand(this));

        getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        var config = new PathySerializableConfiguration();
        for (Path value : Pathy.api().getPaths().values()) {
            var path = new PathySerializableConfiguration.Path();
            path.name = value.getName();
            path.points = new ArrayList<>();
            for (var point : value.getPoints()) {
                var serializableLocation = new PathySerializableConfiguration.SerializableLocation();
                serializableLocation.world = point.getLocation().getWorld().getName();
                serializableLocation.x = point.getLocation().getX();
                serializableLocation.y = point.getLocation().getY();
                serializableLocation.z = point.getLocation().getZ();
                serializableLocation.yaw = 0;
                serializableLocation.pitch = 0;
                path.points.add(serializableLocation);
            }
            config.paths.add(path);
        }
        if (!Pathy.api().isDisablePersistentDataHolder()) {
            try {
                var writer = new FileWriter(dataFile);
                GSON.toJson(config, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("Could not create data file", e);
            }
        } else {
            getLogger().info("Persistent data holder is disabled, not saving data");
        }
        getLogger().info("Plugin disabled");
    }

}
