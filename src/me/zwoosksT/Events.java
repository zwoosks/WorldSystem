package me.zwoosksT;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
public class Events implements Listener {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public Events(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCommandPr(PlayerCommandPreprocessEvent e) {
		String[] args = e.getMessage().split(" ");
		if(args[0].equalsIgnoreCase("/nickname")) {
			e.getPlayer().sendMessage(Utils.chat("&cComando bloqueado."));
			e.setCancelled(true);
		} else if(args[0].equalsIgnoreCase("/nick")) {
			e.getPlayer().sendMessage(Utils.chat("&cComando bloqueado."));
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.getBlock().getWorld().getName().substring(0, 3).equalsIgnoreCase("ws_")) {
			if(!e.getBlock().getWorld().getName().equalsIgnoreCase("ws_" + e.getPlayer().getName())) {
				if(!e.getPlayer().hasPermission("ws.others")) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(Utils.chat("&2&l>> WorldSystem &c¡No puedes romper en mundos ajenos al tuyo!"));
				}
			}
		}
	}
	
	@EventHandler
	public void onCommandPlayer(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().substring(0, 3).equalsIgnoreCase("/ws")) {
			e.setCancelled(true);
			e.getPlayer().performCommand(e.getMessage().replace("/ws", "worldsystem"));
		}
	}
	
}