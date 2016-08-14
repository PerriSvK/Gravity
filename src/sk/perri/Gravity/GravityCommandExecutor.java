package sk.perri.Gravity;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Array;
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
            return false;
        }

        switch (args[0])
        {
            case "join": addPlayer((Player) sender, args); break;
            case "leave": removePlayer((Player) sender, args); break;
            case "create": createMap((Player) sender, args); break;
        }
        return true;
    }

    private void addPlayer(Player sender, String[] args)
    {
        GravityMap map = findMap();

        if(map == null)
        {
            sender.sendMessage("No map avaible!");
        }
        else
        {
            plugin.players.put(sender, map);
            map.teleportPlayer(sender);
            sender.sendMessage("You joined minigame! Map: "+map.getName());
        }
    }

    private GravityMap findMap()
    {
        GravityMap res = null;

        for ( GravityMap map : plugin.maps)
        {
            if (map.isFree())
            {
                res = map;
                break;
            }
        }

        return res;
    }

    private void removePlayer(Player sender, String[] args)
    {
        GravityMap map = plugin.players.get(sender);
        map.removePlayer(sender);
        boolean remove = plugin.players.remove(sender, map);

        if(remove)
            sender.sendMessage("You left minigame!");
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
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        plugin.maps.add(map);
        sender.sendMessage("Map created!");
    }
}
