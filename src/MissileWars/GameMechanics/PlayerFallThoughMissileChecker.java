package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import MissileWars.Main.Core;

public class PlayerFallThoughMissileChecker implements Runnable{

	private int id;
	
	public PlayerFallThoughMissileChecker() {
		start();
	}
	
	public void start(){
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 2, 1); // delay, interval -- runs every tick
	}
	
	public void stop(){
		Bukkit.getScheduler().cancelTask(id);
	}
	
	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getLocation().getBlock().getType().isSolid() == true
					&& player.getLocation().getBlock().getType() != Material.STAINED_GLASS_PANE
					&& player.getLocation().getBlock().getType() != Material.PISTON_EXTENSION
					&& player.getGameMode() == GameMode.SURVIVAL){
				// this player is getting stuck inside a missile block, teleport them back up
				Location newLocation = player.getLocation();
				newLocation.setY((int)player.getLocation().getY() + 1);
				//newLocation.setDirection(player.getLocation().getDirection());
				player.teleport(newLocation);
				Vector newVelocity = player.getLocation().getDirection().multiply(0.1);
				newVelocity.setY(0.0);
				player.setVelocity(newVelocity);
			}
		}
	}
}
