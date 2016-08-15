package sk.perri.Gravity;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


class GravityCommandExecutor implements CommandExecutor {
    private final Gravity plugin;

    GravityCommandExecutor(Gravity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args == null) {
            sender.sendMessage(ChatColor.RED + "[Gravity] This command can be cast only by players!");
            return true;
        }

        switch (args[0]) {
            case "join":
                addPlayer((Player) sender, args);
                break;
            case "rjoin":
                quickJoin((Player) sender);
                break;
            case "help":
                showHelp((Player) sender);
                break;
            case "leave":
                removePlayer((Player) sender, true, args);
                break;
            case "create":
                createMap((Player) sender, args);
                break;
            case "start":
                forceStart((Player) sender, args);
                break;

            default:
                showErrMsg((Player) sender);
        }
        return true;
    }

    private void quickJoin(Player sender) {
        List<GravityGame> game = findGame();

        if (plugin.players.containsKey(sender)) {
            sender.sendMessage(ChatColor.RED + "[Gravity] You are in minigame!");
            return;
        }

        if (game.size() == 0) {
            if (plugin.games.size() < plugin.GAMES) {
                plugin.games.add(new GravityGame(plugin.maps, plugin));
                sender.sendMessage(ChatColor.YELLOW + "[Gravity] Creating new room!");
            } else {
                sender.sendMessage(ChatColor.RED + "[Gravity] Sorry but all rooms are full!");
            }
        }

        String[] a = new String[2];
        a[0] = "join";
        a[1] = "1";
        addPlayer(sender, a);
    }

    private void showHelp(Player sender) {
        sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "[Gravity] Help for plugin Gravity:");
        sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "/gr join <roomNumber> " + ChatColor.WHITE + "- Join to game room");
        sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "/gr rjoin " + ChatColor.WHITE + "- Join to 1st found game room");
        sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "/gr leave " + ChatColor.WHITE + "- Leave from minigame");
        sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "/gr start " + ChatColor.WHITE + "- Start game in your room");
        sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "/gr create <mapName> " + ChatColor.WHITE + "- Setup map");
    }

    private void showErrMsg(Player sender) {
        sender.sendMessage(ChatColor.RED + "[Gravity] Use /gr help !");
    }

    private void addPlayer(Player sender, String[] args) {
        List<GravityGame> game = findGame();

        if (plugin.players.containsKey(sender)) {
            sender.sendMessage(ChatColor.RED + "[Gravity] You are in minigame!");
            return;
        }

        if (args.length == 1) {
            if (game.size() == 0) {
                if (plugin.games.size() < plugin.GAMES) {
                    plugin.games.add(new GravityGame(plugin.maps, plugin));
                    addPlayer(sender, args);
                } else {
                    sender.sendMessage(ChatColor.RED + "[Gravity] Sorry but all rooms are full!");
                }
            } else {
                sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "[Gravity] Available rooms:");
                for (int i = 0; i < game.size(); i++) {
                    sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "[Gravity] room " + (i + 1) + " status: " + game.get(i).getPlayersCount() + " / " + plugin.CAPACITY);
                }

                sender.sendMessage(ChatColor.RED + "[Gravity] Use /gr join <roomNumber>");
            }
        } else if (args.length == 2 && NumberUtils.isNumber(args[1])) {
            if (game.size() >= Integer.parseInt(args[1]) && 0 < Integer.parseInt(args[1])) {
                game.get(Integer.parseInt(args[1]) - 1).addPlayer(sender);
                plugin.players.put(sender, game.get(Integer.parseInt(args[1]) - 1));
                sender.sendMessage(ChatColor.GREEN + "[Gravity] You joined minigame!");
            } else {
                sender.sendMessage(ChatColor.RED + "[Gravity] Bad room number or no room created!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "[Gravity] Use /gr join <roomNumber>");
        }
    }

    private List<GravityGame> findGame() {

        return plugin.games.stream().filter(game -> !game.isRunning() && game.isFree()).collect(Collectors.toList());
    }

    private void forceStart(Player sender, String[] args) {
        if (sender.hasPermission("gravity.start")) {
            if (plugin.players.containsKey(sender)) {
                plugin.players.get(sender).gameStart();
            } else
                sender.sendMessage(ChatColor.RED + "[Gravity] You are not in this minigame!");
        } else
            sender.sendMessage(ChatColor.RED + "[Gravity] You can't do that, no permissions!");
    }

    void removePlayer(Player sender, boolean plr, String[] args) {
        if (plugin.players.containsKey(sender)) {
            GravityGame game = plugin.players.get(sender);
            game.removePlayer(sender);

            plugin.players.remove(sender, game);

            if (plr)
                sender.sendMessage(ChatColor.YELLOW + "[Gravity] You left minigame!");

            if (game.getPlayersCount() == 0) {
                plugin.games.remove(game);
            }
        } else
            sender.sendMessage(ChatColor.RED + "[Gravity] You are not in this minigame!");
    }

    private void createMap(Player sender, String[] args)
    {
        if(sender.hasPermission("gravity.create"))
        {
            if (args == null || args.length > 2)
            {
                sender.sendMessage(ChatColor.RED + "[Gravity] Use /gr create <mapName>");
                return;
            }

            Location loc = sender.getLocation();
            float[] data = {loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getYaw(), loc.getPitch()};
            List<Float> dataL = new ArrayList<>();

            for (float f : data)
            {
                dataL.add(f);
            }
            GravityMap map = new GravityMap(data, loc.getWorld(), args[1]);

            plugin.mapsData.set(args[1] + ".world", loc.getWorld().getName());
            plugin.mapsData.set(args[1] + ".spawn", dataL);
            try
            {
                plugin.mapsData.save(plugin.mapsFile);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            plugin.maps.add(map);
            sender.sendMessage(ChatColor.GREEN + "[Gravity] Map created!");
        }
        else
            sender.sendMessage(ChatColor.RED + "[Gravity] You can't do that, no permissions!");
    }
}
