package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Snowball;

import MissileWars.Main.Core;

public class ShieldWatcher implements Runnable{

	private Snowball snowball;
	private int team;
	
	public ShieldWatcher(Snowball snowball, int team) {
		this.snowball = snowball;
		this.team = team;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, this, 20); // runs 1 second later
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (snowball == null
				|| snowball.isDead() == true){
			return; // snowball is gone, do nothing
		}
		
		// copy over the shield to the snowball's location
		Location center = snowball.getLocation();
		snowball.remove();
		
		Location shieldLocation = null;
		if (team == 1){
			shieldLocation = Core.gameManager.missiles.team1ShieldLocation.clone();
		}
		else if (team == 2){
			shieldLocation = Core.gameManager.missiles.team2ShieldLocation.clone();
		}
		
		Block tempBlock, shieldBlock;
		if (shieldLocation != null){
			for (int y = -3; y <= 3; y++){
				for (int x = -3; x <= 3; x++){
					shieldBlock = shieldLocation.clone().add(x, y, 0).getBlock();
					tempBlock = center.clone().add(x, y, 0).getBlock();
					
					if (shieldBlock.getType() != Material.AIR
							&& tempBlock.getType() != Material.OBSIDIAN
							&& tempBlock.getType() != Material.PORTAL){
						tempBlock.setType(shieldBlock.getType());
						tempBlock.setData(shieldBlock.getData());
					}
				}
			}
		}
	}
}