package sk.perri.Gravity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Vector;

public class GravityMap
{
    float[] spawn = new float[5]; //x, y, z, yaw, pitch
    World world = null;
    String name = "";

    public GravityMap(float[] spawn, World world, String name)
    {
        this.spawn = spawn;
        this.world = world;
        this.name = name;
    }

    public void teleportPlayer(Player player)
    {
        player.teleport(new Location((World) world, spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]));
    }

    public String getName()
    {
        return name;
    }



}
