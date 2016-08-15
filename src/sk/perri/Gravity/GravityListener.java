package sk.perri.Gravity;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;

class GravityListener implements Listener
{
    private final Gravity plugin;

     GravityListener(Gravity plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFDMG(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
        {
            if(plugin.players.containsKey(event.getEntity()) && plugin.players.get(event.getEntity()).isRunning())
            {
                event.setCancelled(true);
                event.getEntity().sendMessage(ChatColor.DARK_PURPLE+"[Gravity] Send back to map spawn!");
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
            if (plugin.players.containsKey(player) && plugin.players.get(event.getEntity()).isRunning())
                plugin.players.get(player).stageClearPortal(player);
        }
    }
}
