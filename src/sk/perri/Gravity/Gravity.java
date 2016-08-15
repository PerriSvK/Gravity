package sk.perri.Gravity;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class Gravity extends JavaPlugin
{
    Map<Player, GravityGame> players = new HashMap<>();
    Vector<GravityGame> games = new Vector<>();
    Vector<GravityMap> maps = new Vector<>();
    File mapsFile = new File(getDataFolder(), "maps.yml");
    FileConfiguration mapsData;
    private File configFile = new File(getDataFolder(), "config.yml");
    private FileConfiguration config = this.getConfig();
    int GAMES = 10;
    int MAPS = 5;
    int CAPACITY = 12;
    Vector<ItemStack> REWARD = new Vector<>();
    GravityCommandExecutor gce = new GravityCommandExecutor(this);

    @Override
    public void onEnable()
    {
        this.getCommand("gr").setExecutor(gce);
        Bukkit.getPluginManager().registerEvents(new GravityListener(this), this);
        loadMaps();
        getLogger().info("Plugin enabled!");

        if(config.isSet("max_games") && config.isInt("max_games"))
            GAMES = config.getInt("max_games");

        if(config.isSet("max_maps") && config.isInt("max_maps"))
            MAPS = config.getInt("max_maps");

        if(config.isSet("max_capacity") && config.isInt("max_capacity"))
            CAPACITY = config.getInt("max_capacity");

        if(config.isSet("reward"))
        {
            for(String s : config.getConfigurationSection("reward").getKeys(false))
            {
                ItemStack rew = new ItemStack(Material.matchMaterial(s), config.getInt("reward."+s+".amount"));
                ItemMeta itemMeta = rew.getItemMeta();
                if(itemMeta != null )
                {
                    if(config.isSet("reward." + s + ".name"))
                    {
                        String cName = config.getString("reward." + s + ".name");
                        itemMeta.setDisplayName(cName);
                    }

                    if(config.isSet("reward." + s + ".name"))
                    {
                        List<String> lore = new ArrayList<>();
                        lore.add(config.getString("reward." + s + ".lore"));
                        itemMeta.setLore(lore);
                    }

                    rew.setItemMeta(itemMeta);
                }
                REWARD.add(rew);
            }
        }
    }

    @Override
    public void onDisable()
    {
        configFile = null;
        mapsFile = null;
        config = null;
        mapsData = null;
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

        if(!configFile.exists())
            saveDefaultConfig();

        mapsData = YamlConfiguration.loadConfiguration(mapsFile);
        for (String s : mapsData.getKeys(false))
        {
            List<Float> spawnL = mapsData.getFloatList(s+".spawn");
            float[] spawnF = new float[5];
            for (int i = 0; i < spawnF.length; i++) { spawnF[i] = spawnL.get(i); }
            String world = mapsData.getString(s+".world");
            maps.add(new GravityMap(spawnF, Bukkit.getWorld(world), s));
        }
    }
}
