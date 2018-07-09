package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
				
				// set the player's velocity (up and forward) depending if there are blocks in front of the player
				Vector newVelocity = new Vector(0.0, 0.0, 0.0);
				Block blockInFront = player.getLocation().add(player.getLocation().getDirection()).getBlock();
				if (blockInFront.getType() != Material.AIR
						&& blockInFront.getType().isSolid() == true){
					// the player has blocks in front of them so we will give them velocity that minicks forward movement
					newVelocity = player.getLocation().getDirection().multiply(0.15);
				}
				newVelocity.setY(0.1); // upward velocity to help get the player unstuck
				player.setVelocity(newVelocity);
			}
		}
	}
}
