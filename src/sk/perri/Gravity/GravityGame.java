package sk.perri.Gravity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.*;

class GravityGame
{
    private Map<Player, Location> prevLoc = new HashMap<>();
    private Gravity plugin;
    private int capacity; // how many player can be in this game
    private int noMaps; // how many maps
    private boolean running = false;
    private Vector<GravityMap> maps = new Vector<>();
    private Map<Player, Float> players = new HashMap<>();
    private Vector<Player> winners = new Vector<>();
    private Map<Integer, Player> status = new HashMap<>();
    private Sidebar sidebar = new Sidebar(ChatColor.AQUA.toString()+ChatColor.BOLD+"Gravity "+ChatColor.WHITE+"minigame");

    GravityGame(Vector<GravityMap> maps, Gravity plugin)
    {
        this.maps = maps;
        this.plugin = plugin;
        capacity = plugin.CAPACITY;
        noMaps = plugin.MAPS;

        if (maps.size() < noMaps)
        {
            noMaps = maps.size();
        }

        setupScoreboard();

    }

    private void setupScoreboard()
    {
        String waitMsg = "Waiting for "+ChatColor.AQUA.toString()+ChatColor.BOLD+"players";
        String statusMsg = "Status:";
        sidebar.clearEntries();
        sidebar.addEntry(" ", waitMsg, statusMsg, players.size()+" / "+capacity);

        sidebar.update();
    }

    private void scoreboardStartMsg()
    {

        sidebar.addEntry(
        " ",
        ChatColor.AQUA.toString()+ChatColor.BOLD+"1."+ChatColor.WHITE+" ---",
        ChatColor.AQUA.toString()+ChatColor.BOLD+"2."+ChatColor.WHITE+" ---",
        ChatColor.AQUA.toString()+ChatColor.BOLD+"3."+ChatColor.WHITE+" ---",
        ChatColor.AQUA.toString()+ChatColor.BOLD+"4."+ChatColor.WHITE+" ---",
        ChatColor.AQUA.toString()+ChatColor.BOLD+"5."+ChatColor.WHITE+" ---");

        sidebar.update();
    }

    boolean addPlayer(Player player)
    {
        if(isFree() && !players.containsKey(player))
        {
            players.put(player, -1f);
            sidebar.removeEntry(3);
            sidebar.addEntry(players.size()+" / "+capacity);

            sidebar.showTo(player);
            sidebar.update();
            if(isFull()) gameStart();
            return true;
        }

        return false;
    }

    public void sbBroadcast(String msg, int line)
    {
        List<String> ent = sidebar.getEntries();
        for(int i = 0; i < ent.size(); i++)
            sidebar.removeEntry(i);

        if( ent.size() >= line )
            ent.set(line-1, msg);
        else
            ent.add(msg);
        sidebar.setEntries(ent);

        sidebar.update();
    }

    boolean isFree()
    {
        return players.size() < capacity;
    }

    private boolean isFull()
    {
        return players.size() >= capacity;
    }

    boolean isRunning()
    {
        return running;
    }

    void gameStart()
    {
        generateMapList();
        sidebar.clearEntries();
        scoreboardStartMsg();
        running = true;
        for (Player p : players.keySet())
        {
            prevLoc.put(p, p.getLocation());
            maps.get(0).teleportPlayer(p);
            players.replace(p, 0f);
        }
    }

    void stageClearPortal(Player player)
    {
        if(!players.containsKey(player))
            return;

        if(players.get(player) < noMaps-1)
        {
            players.replace(player,  players.get(player) + 0.25f);
            if(players.get(player) % 1 == 0)
            {
                player.sendMessage(ChatColor.GREEN+"[Gravity] Stage " + Math.round(players.get(player)) + " cleared, next stage!");
                playerFall(player);
                updatePosition(player, Math.round(players.get(player))-1);
                updateScoreboard();
            }
        }
        else
        {
            winners.add(player);
            players.remove(player);
            player.sendMessage(ChatColor.GREEN+"[Gravity] Yeeey you finish on "+winners.size()+". place!");
            if(winners.size() == 1)
            {
                for(ItemStack is : plugin.REWARD)
                {
                    player.getInventory().addItem(is);
                }

                player.sendMessage(ChatColor.GREEN+"[Gravity] You received your "+ChatColor.BOLD+"reward");
                plugin.gce.removePlayer(player, false, null);
            }
        }
    }

    private void updatePosition(Player player, int stage)
    {
        Map<Integer, Player> plTop = new HashMap<>();
        Map<Integer, Player> plBot= new HashMap<>();

        if(status.containsValue(player))
        {
            status.remove(stage-1, player);

            for (Map.Entry<Integer, Player> m : status.entrySet())
            {
                if (m.getKey() >= stage)
                {
                    plTop.put(m.getKey(), m.getValue());
                }
                else
                {
                    plBot.put(m.getKey(), m.getValue());
                }
            }

            status.clear();
            status.putAll(plTop);
            status.put(stage, player);
            status.putAll(plBot);
        }
        else
        {
            status.put(1, player);
        }
    }

    private Player getPosition(int pos)
    {
        if(winners.size() >= pos)
        {
            return winners.get(pos-1);
        }

        if(status.size() >= pos)
        {
            int i = 1;
            for(Map.Entry<Integer, Player> e : status.entrySet())
            {
                if(i == pos)
                {
                    return e.getValue();
                }
            }
        }
        return null;
    }

    private void updateScoreboard()
    {
        for(int i = 0; i < 5; i++)
        {
            Player pl = getPosition(i+1);
            sidebar.replaceEntry(i+1, ChatColor.AQUA.toString()+ChatColor.BOLD+(i+1)+". "+ChatColor.WHITE+(pl != null ? pl.getName() : "---"));
        }

        sidebar.update();
    }

    void playerFall(Player player)
    {
        maps.get(Math.round(players.get(player))).teleportPlayer(player);
    }

    private void generateMapList()
    {
        while(maps.size() > noMaps)
        {
            maps.remove((int)(Math.random()*maps.size()));
        }
    }

    void removePlayer(Player player)
    {
        players.remove(player);
        player.teleport(prevLoc.get(player));
        prevLoc.remove(player);
        if(winners.contains(player))
            winners.remove(player);

        sidebar.hideFrom(player);
    }

    int getPlayersCount()
    {
        return players.size();
    }
}
