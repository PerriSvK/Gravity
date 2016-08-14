package sk.perri.Gravity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Vector;

public class GravityMap
{
    float[] spawn = new float[5]; //x, y, z, yaw, pitch
    Object world = null;
    String name = "";
    int id = -1;
    int capacity = 12;
    Vector<Player> players = new Vector<>();

    public GravityMap(float[] spawn, Object world, String name)
    {
        this.spawn = spawn;
        this.world = world;
        this.name = name;
    }

    public boolean addPlayer(Player player)
    {
        if(players.size() > capacity)
            return false;

        players.add(player);
        return true;
    }

    public void removePlayer(Player player)
    {
        players.remove(player);
    }

    public void teleportPlayer(Player player)
    {
        player.teleport(new Location((World) world, spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]));
    }

    public boolean isFree()
    {
        return players.size() < capacity;
    }

    public String getName()
    {
        return name;
    }

}
