package define.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class NoPhantom extends JavaPlugin implements Listener {
	FileConfiguration config = this.getConfig();
	File datafolder = this.getDataFolder();
	
	public void onEnable() {
		getLogger().info("Subscribe to PewDiePie!");
		
		// Gets the player's file
		File dataFile = new File(datafolder + File.separator + "datafile.yml");
		
		FileConfiguration applicantsConfig = YamlConfiguration.loadConfiguration(dataFile);
		
		try {
			applicantsConfig.createSection("playerdata");
			applicantsConfig.save(dataFile);
		} catch (IOException e) {
			Log.error("Unable to save data file!  Error 1");
			e.printStackTrace();
		}
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {
		//literally nothing
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("togglephantoms") && sender instanceof Player) {
			Player player = (Player)sender;
			File dataFile = new File(datafolder + File.separator + "datafile.yml");
			FileConfiguration applicantsConfig = YamlConfiguration.loadConfiguration(dataFile);
			if (applicantsConfig.getConfigurationSection("playerdata").contains(player.getUniqueId().toString())) {
				player.sendMessage(ChatColor.AQUA + "You have enabled phantoms");
				applicantsConfig.getConfigurationSection("playerdata").set(player.getUniqueId().toString(), null);
				try {
					applicantsConfig.save(dataFile);
				} catch (IOException e) {
					Log.error("Unable to save data file! Error 2");
					e.printStackTrace();
				}
			} else {
				Bukkit.broadcastMessage("3");
				applicantsConfig.getConfigurationSection("playerdata").set(player.getUniqueId().toString(), "true");
				player.sendMessage(ChatColor.AQUA + "You have disabled phantoms");
				try {
					applicantsConfig.save(dataFile);
				} catch (IOException e) {
					Log.error("Unable to save data file! Error 2");
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onMobTargetEvent(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			if (event.getEntityType() == org.bukkit.entity.EntityType.PHANTOM) {
				event.setCancelled(/*(event.getSpawnReason() == SpawnReason.NATURAL) && */hasPlayerDisabledPhantom((Player)event.getTarget()));
			}
		}
	}
	
	@EventHandler
	public void onPhantomSpawnEvent(CreatureSpawnEvent event) {
		if (event.getEntityType() == org.bukkit.entity.EntityType.PHANTOM) {
			event.setCancelled(/*(event.getSpawnReason() == SpawnReason.NATURAL) && */hasPlayerDisabledPhantom(event.getLocation()));
		}
	}
	
	public Boolean hasPlayerDisabledPhantom(Player player) {
		File dataFile = new File(datafolder + File.separator + "datafile.yml");
		FileConfiguration applicantsConfig = YamlConfiguration.loadConfiguration(dataFile);
		Bukkit.broadcastMessage(applicantsConfig.getConfigurationSection("playerdata").contains(player.getUniqueId().toString()) + "");
		return applicantsConfig.getConfigurationSection("playerdata").contains(player.getUniqueId().toString());
	}
	
	public Boolean hasPlayerDisabledPhantom(Location locationGiven) {
		File dataFile = new File(datafolder + File.separator + "datafile.yml");
		FileConfiguration applicantsConfig = YamlConfiguration.loadConfiguration(dataFile);
		Bukkit.broadcastMessage(applicantsConfig.getConfigurationSection("playerdata").contains(getClosestPlayer(locationGiven).getUniqueId().toString()) + "");
		return applicantsConfig.getConfigurationSection("playerdata").contains(getClosestPlayer(locationGiven).getUniqueId().toString());
	}
	
	public Player getClosestPlayer(Location locationGiven) {
		Player playerClosest = null;
		for (Player player : Bukkit.getOnlinePlayers()) {
			int distance = Integer.MIN_VALUE;
			if (player.getLocation().distanceSquared(locationGiven) > distance) {
				playerClosest = player;
			}
		}
		return playerClosest;
	}
}
