package me.zwoosksT;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CommandManager implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public CommandManager(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("author").setExecutor(this);
		plugin.getCommand("worldsystem").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("author")) {
			sender.sendMessage(Utils.chat("&eAuthor of the plugin: zwoosks."));
		} else if(cmd.getName().equalsIgnoreCase("worldsystem")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(args.length > 0) {
					if(args[0].equalsIgnoreCase("create")) {
						if(player.hasPermission("ws.create")) {
							String playerName = player.getName().toLowerCase();
							if(worldExists("ws_" + playerName)) {
								player.sendMessage(Utils.chat("&cActualmente ya tienes un mundo creado!"));
							} else {
								// Continuar
								String worldName = "ws_" + playerName;
								WorldCreator worldCreator = new WorldCreator(worldName);
								worldCreator.generateStructures(false);
								worldCreator.type(WorldType.FLAT);
								worldCreator.environment(Environment.NORMAL);
								Bukkit.getServer().createWorld(worldCreator);
								World world = Bukkit.getServer().getWorld(worldName);
								world.getWorldBorder().setCenter(0,0);
								world.getWorldBorder().setSize(2000);
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv load ws_" + player.getName().toLowerCase());
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv modify set gamemode creative ws_" + player.getName().toLowerCase());
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv modify set animals false ws_" + player.getName().toLowerCase());
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv modify set monsters false ws_" + player.getName().toLowerCase());
								player.sendMessage(Utils.chat("&aMundo creado exitosamente."));
								player.teleport(new Location(world, 0, 4, 0));
								
								// Worldedit and WorldGuard
								Vector vmin = new Vector(999, 0, 999);
								Vector vmax = new Vector(-999, 1000, -999);
								BlockVector min = vmin.toBlockVector();
								BlockVector max = vmax.toBlockVector();
								RegionManager rManager = getWorldGuard().getRegionManager(world);
								ProtectedRegion protectedRegion = new ProtectedCuboidRegion("wsregion_" + player.getName().toLowerCase(), min, max);
//								protectedRegion.getOwners().addPlayer(player.getName());
								protectedRegion.getMembers().addPlayer(player.getName());
								rManager.addRegion(protectedRegion);
								try {
									rManager.save();
								} catch (StorageException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						} else {
							player.sendMessage(Utils.chat("&cCareces del permiso '&ews.create&c'"));
						}
					} else if(args[0].equalsIgnoreCase("author")) {
						player.sendMessage(Utils.chat("&aAutor del plugin: &ezwoosks"));
					} else if(args[0].equalsIgnoreCase("home")) {
						if(player.hasPermission("ws.home")) {
							String wName = "ws_" + player.getName().toLowerCase();
							World w = Bukkit.getServer().getWorld(wName);
							try {
								player.teleport(new Location(w, 0, 4, 0));
								player.sendMessage(Utils.chat("&aTeletransportado exitosamente."));
							} catch(Exception e) {
								player.sendMessage(Utils.chat("&c¡No tienes ningún mundo creado!"));
							}
						} else {
							player.sendMessage(Utils.chat("&cCareces del permiso '&ews.home&c'"));
						}
					} else if(args[0].equalsIgnoreCase("remove")) {
						if(player.hasPermission("ws.remove")) {
							String wName = "ws_" + player.getName().toLowerCase();
							if(worldExists(wName)) {
								World currentWorld = player.getWorld();
								World toDelete = Bukkit.getServer().getWorld(wName);
								if(currentWorld == toDelete) {
									player.sendMessage(Utils.chat("&cPara eliminar tu mundo, no debes estar dentro de él."));
								} else {
									player.sendMessage(Utils.chat("&eEliminando mundo..."));
									Bukkit.getServer().unloadWorld(toDelete, true);
									File deleteFolder = toDelete.getWorldFolder();
									deleteWorld(deleteFolder);
									player.sendMessage(Utils.chat("&aEl mundo se ha eliminado correctamente."));
								}
							} else {
								player.sendMessage(Utils.chat("&c¡No puedes eliminar un mundo que no existe!"));
							}
						} else {
							player.sendMessage(Utils.chat("&cCareces del permiso '&ews.remove&c'"));
						}
					}
				} else {
					player.sendMessage(Utils.chat("&b/ws create\n&b/ws home\n&b/ws remove"));
				}
			} else {
				sender.sendMessage(Utils.chat("&cSolamente los jugadores pueden ejecutar este comando."));
			}
		}
		return true;
	}
	
	private boolean worldExists(String name) {
		World world = Bukkit.getServer().getWorld(name);
		if(world == null) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	                  deleteWorld(files[i]);
	              } else {
	                  files[i].delete();
	              }
	          }
	      }
	      return(path.delete());
	}
	
	private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldGuardPlugin) plugin;
    }
   
//    private WorldEditPlugin getWorldEdit() {
//            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
//             
//        // WorldGuard may not be loaded
//        if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
//            return null; // Maybe you want throw an exception instead
//        }
//     
//        return (WorldEditPlugin) plugin;
//    }
	
}
