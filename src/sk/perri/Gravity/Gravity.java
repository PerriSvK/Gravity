package sk.perri.Gravity;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Gravity extends JavaPlugin
{
    public Map<Player, GravityMap> players = new HashMap<>();
    public Vector<GravityMap> maps = new Vector<>();
    public File mapsFile = new File(getDataFolder(), "maps.yml");
    public FileConfiguration mapsData;

    @Override
    public void onEnable()
    {
        this.getCommand("gr").setExecutor(new GravityCommandExecutor(this));
        Bukkit.getPluginManager().registerEvents(new GravityListener(this), this);
        loadMaps();
        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable()
    {

    }

    public void reloadPlugin()
    {
        onDisable();
        onEnable();
    }

    public void loadMaps()
    {
        if(!mapsFile.exists())
        {
            getLogger().info("file maps.yml NOT found, trying create!");

            mapsFile.getParentFile().mkdir();

            try
            {
                if(mapsFile.createNewFile())
                {
                    getLogger().info("maps.yml created successfully!");
                }
                else
                {
                    getLogger().info("maps.yml NOT created!");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        mapsData = YamlConfiguration.loadConfiguration(mapsFile);
        for (String s : mapsData.getKeys(false))
        {
            List<Float> spawnL = mapsData.getFloatList(s+".spawn");
            float[] spawnF = new float[5];
            for (int i = 0; i < spawnF.length; i++) { spawnF[i] = spawnL.get(i); }
            String world = mapsData.getString(s+".world");
            maps.add(new GravityMap(spawnF, world, s));
        }
    }

}
