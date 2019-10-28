package me.zwoosksT;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		new CommandManager(this);
		registrarEventos();
		this.getLogger().info(ChatColor.GREEN + "Habilitado Lobby manager");
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info(ChatColor.RED + "Deshabilitado Lobby manager");
	}
	
	private void registrarEventos() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new Events(this), this);
	}
	
}