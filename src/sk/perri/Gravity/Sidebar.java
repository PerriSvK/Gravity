package sk.perri.Gravity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Sidebar implements ConfigurationSerializable
{

    private static transient ScoreboardManager bukkitManager = Bukkit.getScoreboardManager();

    static
    {
        ConfigurationSerialization.registerClass(Sidebar.class);
    }

    private List<String> entries;
    private transient Scoreboard bukkitScoreboard;
    private transient Objective bukkitObjective;
    private transient BukkitTask updateTask;
    private String title;

    public Sidebar(String title, String... entries)
    {

        bukkitScoreboard = bukkitManager.getNewScoreboard();

        bukkitObjective = bukkitScoreboard.registerNewObjective("obj", "dummy");

        this.entries = entries == null || entries.length == 0 ? new ArrayList<String>() : Arrays.asList(entries);
        this.title = title;

        update();

    }

    public Sidebar(String title, List<String> entries, Plugin plugin, int autoUpdateDelay)
    {

        bukkitScoreboard = bukkitManager.getNewScoreboard();

        bukkitObjective = bukkitScoreboard.registerNewObjective("obj", "dummy");

        this.entries = entries;
        this.title = title;

        update();

        startAutoUpdating(plugin, autoUpdateDelay);

    }

    /**
     * Auto-updating not supported!
     */
    @SuppressWarnings("unchecked")
    public Sidebar(Map<String, Object> map)
    {

        bukkitScoreboard = bukkitManager.getNewScoreboard();

        bukkitObjective = bukkitScoreboard.registerNewObjective("obj", "dummy");

        entries = (List<String>) map.get("entries");

        title = (String) map.get("title");

        update();

    }

    /**
     * Auto-updating not supported!
     */
    @Override
    public Map<String, Object> serialize()
    {

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("title", title);
        map.put("entries", entries);

        return map;

    }

    public String getTitle()
    {
        return title;
    }

    public Sidebar setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public List<String> getEntries()
    {
        return entries;
    }

    public Sidebar setEntries(List<String> entries)
    {
        this.entries = entries;
        return this;
    }

    public Sidebar addEntry(String... entries)
    {
        this.entries.addAll(Arrays.asList(entries));
        return this;
    }

    public Sidebar removeEntry(String entry)
    {
        entries.remove(entry);
        return this;
    }

    public Sidebar clearEntries()
    {
        entries.clear();
        return this;
    }

    public Sidebar removeEntry(int num)
    {
        entries.remove(num);
        return this;
    }

    public Sidebar replaceEntry(int num, String entry)
    {
        if(entries.size() >= num)
            entries.set(num, entry);

        return this;
    }

    public Sidebar showTo(Player player)
    {
        player.setScoreboard(bukkitScoreboard);
        return this;
    }

    public Sidebar hideFrom(Player player)
    {
        player.setScoreboard(bukkitManager.getMainScoreboard());
        return this;
    }

    public Sidebar update()
    {

        redoBukkitObjective();

        for (int i = entries.size(); i > 0; i--)
            bukkitObjective.getScore(entries.get(entries.size() - i)).setScore(i);

        return this;

    }

    public Sidebar startAutoUpdating(Plugin plugin, int delayInTicks)
    {

        if (updateTask != null) updateTask.cancel();

        updateTask = (new BukkitRunnable()
        {

            @Override
            public void run()
            {
                update();
            }

        }).runTaskTimer(plugin, delayInTicks, delayInTicks);

        return this;

    }

    public Sidebar stopAutoUpdating()
    {

        if (updateTask == null)
            throw new IllegalStateException("Auto-updating is not started!");

        updateTask.cancel();
        updateTask = null;

        return this;

    }

    private void redoBukkitObjective()
    {

        bukkitObjective.unregister();
        bukkitObjective = bukkitScoreboard.registerNewObjective("obj", "dummy");

        bukkitObjective.setDisplayName(title);
        bukkitObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

    }

}