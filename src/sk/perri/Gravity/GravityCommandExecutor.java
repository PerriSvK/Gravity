package sk.perri.Gravity;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GravityCommandExecutor implements CommandExecutor
{
    private final Gravity plugin;

    public GravityCommandExecutor(Gravity plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(!(sender instanceof Player) || args == null)
        {
            sender.sendMessage("This command can be cast only by players!");
            return true;
        }

        switch (args[0])
        {
            case "join": addPlayer((Player) sender, args); break;
            case "leave": removePlayer((Player) sender, args); break;
            case "create": createMap((Player) sender, args); break;
            case "start": forceStart((Player) sender, args); break;
        }
        return true;
    }

    private void addPlayer(Player sender, String[] args)
    {
        GravityGame game = findGame();

        if(game == null)
        {
            plugin.games.add(new GravityGame(plugin.maps));
            addPlayer(sender, args);
        }
        else
        {
            if(game.addPlayer(sender))
            {
                plugin.players.put(sender, game);
                sender.sendMessage("You joined minigame!");
            }
            else
            {
                sender.sendMessage("You are in minigame!");
            }
        }
    }

    private GravityGame findGame()
    {
        GravityGame res = null;

        for ( GravityGame game : plugin.games)
        {
            if (game.isFree())
            {
                res = game;
                break;
            }
        }

        return res;
    }

    private void forceStart(Player sender, String[] args)
    {
        if(plugin.players.containsKey(sender))
        {
            plugin.players.get(sender).gameStart();
        }
        else
            sender.sendMessage("You are not in this minigame!");
    }

    private void removePlayer(Player sender, String[] args)
    {
        GravityGame game = plugin.players.get(sender);
        game.removePlayer(sender);
        boolean remove = plugin.players.remove(sender, game);

        if(remove)
        {
            sender.sendMessage("You left minigame!");
            if(game.getPlayersCount() == 0)
            {
                plugin.games.remove(game);
            }
        }
        else
            sender.sendMessage("You are not in this minigame!");
    }

    private void createMap(Player sender, String[] args)
    {
        if (args == null || args.length > 2)
        {
            sender.sendMessage("Use /gr create <mapName>");
            return;
        }

        Location loc = sender.getLocation();
        float[] data = {loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getYaw(), loc.getPitch()};
        List<Float> dataL = new ArrayList<>();
        for(float f : data) { dataL.add(f); }
        GravityMap map = new GravityMap(data, loc.getWorld(), args[1]);

        plugin.mapsData.set(args[1]+".world", loc.getWorld().getName());
        plugin.mapsData.set(args[1]+".spawn", dataL);
        try
        {
            plugin.mapsData.save(plugin.mapsFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        plugin.maps.add(map);
        sender.sendMessage("Map created!");
    }
}
