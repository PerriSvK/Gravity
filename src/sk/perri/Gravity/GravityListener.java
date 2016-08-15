package sk.perri.Gravity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class GravityListener implements Listener
{
    final Gravity plugin;

    public GravityListener(Gravity plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFDMG(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player && event.getCause().equals((Object)EntityDamageEvent.DamageCause.FALL))
        {
            if(plugin.players.containsKey(event.getEntity()))
            {
                event.setCancelled(true);
                event.getEntity().sendMessage("Ouch that hurt!");
                plugin.players.get(event.getEntity()).playerFall((Player) event.getEntity());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPortalEvent(EntityPortalEnterEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();
            if (plugin.players.containsKey(player))
                plugin.players.get(player).stageClearPortal(player);
        }
    }
}
