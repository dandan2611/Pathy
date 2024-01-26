package fr.codinbox.pathy.command;

import fr.codinbox.pathy.api.Pathy;
import fr.codinbox.pathy.api.path.Path;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PathyCommand implements CommandExecutor {

    private static final HashMap<UUID, Path> editingSessions = new HashMap<>();

    public PathyCommand(Plugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            editingSessions.entrySet().forEach(entry -> {
                var uuid = entry.getKey();
                var path = entry.getValue();
                var player = Bukkit.getPlayer(uuid);

                if (player == null)
                    return;
                path.getPoints().forEach(p -> {
                    // Spawn blue redstone particles
                    player.spawnParticle(Particle.REDSTONE, p.getLocation(),
                            1,
                            0,
                            0,
                            0,
                            0,
                            new Particle.DustOptions(Color.RED, 1));
                });
            });
        }, 5, 5);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            err(sender, "Only players can execute this command");
            return true;
        }
        if (!player.hasPermission("pathy.admin")) {
            err(sender, "You don't have permission to execute this command");
            return true;
        }
        if (args.length == 0)
            showHelp(sender);
        else if (args[0].equalsIgnoreCase("path"))
            path(player, args);
        else if (args[0].equalsIgnoreCase("point"))
            point(player, args);
        return true;
    }

    private void err(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.RED));
    }

    private void mess(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.AQUA));
    }

    private void showHelp(CommandSender sender) {
        mess(sender, "Pathy Help");
        mess(sender, "/pathy help - Show this help");
        mess(sender, "/pathy path create <name> - Create a path");
        mess(sender, "/pathy path remove <name> - Delete a path");
        mess(sender, "/pathy path edit <name> - Edit a path");
        mess(sender, "/pathy point add - Add a point to a path");
        mess(sender, "/pathy point remove [index] - Remove a point from a path");
        mess(sender, "/pathy list - List all paths");
        mess(sender, "/pathy info <name> - Show info about a path");
    }

    private void path(Player sender, String[] args) {
        if (args.length < 2) {
            err(sender, "Usage: /pathy path <subcommand>");
            return;
        }
        if (args[1].equalsIgnoreCase("create"))
            createPath(sender, args);
        else if (args[1].equalsIgnoreCase("edit"))
            editPath(sender, args);
        else if (args[1].equalsIgnoreCase("remove"))
            removePath(sender, args);
        else
            err(sender, "Unknown subcommand");
    }

    private void createPath(Player sender, String[] args) {
        if (args.length < 3) {
            err(sender, "Usage: /pathy path create <name>");
            return;
        }
        var name = args[2];

        Pathy.api().createPath(name);
        editingSessions.put(sender.getUniqueId(), Pathy.api().getPaths().get(name));
        mess(sender, "Path '" + name + "' created [entering edition mode]");
    }

    private void removePath(Player sender, String[] args) {
        if (args.length < 3) {
            err(sender, "Usage: /pathy path delete <name>");
            return;
        }
        var name = args[2];

        if (!Pathy.api().getPaths().containsKey(name)) {
            err(sender, "Path '" + name + "' doesn't exist");
            return;
        }
        Pathy.api().getPaths().remove(name);
        Set<UUID> toRemove = new HashSet<>();
        for (Map.Entry<UUID, Path> uuidPathEntry : editingSessions.entrySet()) {
            if (uuidPathEntry.getValue().getName().equals(name))
                toRemove.add(uuidPathEntry.getKey());
        }
        toRemove.forEach(editingSessions::remove);
        mess(sender, "Path '" + name + "' removed");
    }

    private void editPath(Player sender, String[] args) {
        if (args.length < 3) {
            err(sender, "Usage: /pathy path edit <name>");
            return;
        }
        var name = args[2];

        if (!Pathy.api().getPaths().containsKey(name)) {
            err(sender, "Path '" + name + "' doesn't exist");
            return;
        }
        editingSessions.put(sender.getUniqueId(), Pathy.api().getPaths().get(name));
        mess(sender, "Path '" + name + "' selected [entering edition mode]");
    }

    private void point(Player sender, String[] args) {
        if (args.length < 2) {
            err(sender, "Usage: /pathy point <subcommand>");
            return;
        }
        if (args[1].equalsIgnoreCase("add"))
            addPoint(sender, args);
        else if (args[1].equalsIgnoreCase("remove"))
            removePoint(sender, args);
        else
            err(sender, "Unknown subcommand");
    }

    private void addPoint(Player sender, String[] args) {
        var location = sender.getLocation();
        /*var x = location.getBlockX();
        var z = location.getBlockZ();
        location.add(0.5, 0.25, 0.5);*/
        location.setY(location.getBlockY() + 0.25);
        var path = editingSessions.get(sender.getUniqueId());
        path.getPoints().add(Pathy.PointBuilder.create()
                .setLocation(location)
                .build());
        mess(sender, "Point " + (path.getPoints().size() - 1) + " created at location " +
                (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ());
    }

    private void removePoint(Player sender, String[] args) {
        var path = editingSessions.get(sender.getUniqueId());
        var index = args.length < 3 ? path.getPoints().size() - 1 : Integer.parseInt(args[2]);
        if (index < 0 || index >= path.getPoints().size()) {
            err(sender, "Index out of bounds");
            return;
        }
        path.getPoints().remove(index);
        mess(sender, "Point " + index + " removed");
    }

}
