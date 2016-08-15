package sk.perri.Gravity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static org.bukkit.Bukkit.getLogger;

public class GravityGame
{
    private int capacity = 24; // how many player can be in this game
    private int noMaps = 5; // how many maps
    private Vector<GravityMap> maps = new Vector<>();
    private Map<Player, Float> players = new HashMap<>();
    private Vector<Player> winners = new Vector<>();
    private Map<Integer, Player> status = new HashMap<>();
    private Sidebar sidebar = new Sidebar("Gravity minigame");

    public GravityGame(Vector<GravityMap> maps)
    {
        this.maps = maps;

        if (maps.size() < noMaps)
        {
            noMaps = maps.size();
        }

        setupScoreboard();

    }

    public void setupScoreboard()
    {
        String none = " ";
        String waitMsg = "Waiting for players";
        String statusMsg = "Status:";
        sidebar.clearEntries();
        sidebar.addEntry(" ", waitMsg, statusMsg, players.size()+" / "+capacity);

        sidebar.update();
    }

    public void scorebordStartMsg()
    {

        sidebar.addEntry(
        " ",
        "1. ---",
        "2. ---",
        "3. ---",
        "4. ---",
        "5. ---");

        sidebar.update();
    }

    public boolean addPlayer(Player player)
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

    public boolean isFree()
    {
        return players.size() < capacity;
    }

    public boolean isFull()
    {
        return players.size() >= capacity;
    }

    public void gameStart()
    {
        generateMapList();
        sidebar.clearEntries();
        scorebordStartMsg();
        for (Player p : players.keySet())
        {
            maps.get(0).teleportPlayer(p);
            players.replace(p, 0f);
        }
    }

    public void stageClearPortal(Player player)
    {
        if(!players.containsKey(player))
            return;

        if(players.get(player) < noMaps-1)
        {
            players.replace(player,  players.get(player) + 0.25f);
            if(players.get(player) % 1 == 0)
            {
                player.sendMessage("Stage " + Math.round(players.get(player)) + " cleared, next stage!");
                playerFall(player);
                updatePosition(player, Math.round(players.get(player))-1);
                updateScoreboard();
            }
        }
        else
        {
            winners.add(player);
            players.remove(player);
            player.sendMessage("Yeeey you finish on "+winners.size()+". place!");
        }
    }

    public void updatePosition(Player player, int stage)
    {
        Map<Integer, Player> plTop = new HashMap<>();
        Map<Integer, Player> plBot= new HashMap<>();
        int ind = 0;

        getLogger().info("ss1 size: "+status.size()+" ss1 value 0: "+status.get(0) + " put1 plr: "+player+" "+status.keySet());

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
            getLogger().info("createee");
        }

        getLogger().info("ss size: "+status.size()+" ss value 0: "+status.get(0) + " put plr: "+player+" "+status.keySet()+" "+status.entrySet());
    }

    public Player getPosition(int pos)
    {
        if(winners.size() >= pos)
        {
            getLogger().info("winner");
            return winners.get(pos-1);
        }

        if(status.size() >= pos)
        {
            int i = 1;
            for(Map.Entry<Integer, Player> e : status.entrySet())
            {
                if(i == pos)
                {
                    getLogger().info("pos value: " + e.getValue());
                    return e.getValue();
                }
            }
        }

        getLogger().info("status size: "+status.size()+" pos: "+pos);
        return null;
    }

    public void updateScoreboard()
    {
        for(int i = 0; i < 5; i++)
        {
            Player pl = getPosition(i+1);
            sidebar.replaceEntry(i+1, i+1+". "+(pl != null ? pl.getName() : "---"));
            getLogger().info("debug "+i+" "+pl);
        }

        sidebar.update();
    }

    public void playerFall(Player player)
    {
        maps.get(Math.round(players.get(player))).teleportPlayer(player);
    }

    public void generateMapList()
    {
        while(maps.size() > noMaps)
        {
            maps.remove((int)(Math.random()*maps.size()));
        }
    }

    public void removePlayer(Player player)
    {
        players.remove(player);
        if(winners.contains(player))
            winners.remove(player);

        sidebar.hideFrom(player);
    }

    public int getPlayersCount()
    {
        return players.size();
    }
}
