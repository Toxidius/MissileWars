package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import MissileWars.Main.Core;
import MissileWars.Main.GameStates.GameState;

public class PortalChecker implements Runnable{

	private int id;
	
	public PortalChecker() {
		start();
	}
	
	public void start(){
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 25, 20); // delay, interval -- runs every second (25 tick initial delay)
	}
	
	public void stop(){
		Bukkit.getScheduler().cancelTask(id);
	}

	@Override
	public void run() {
		if (Core.gameStarted != true
				|| Core.gameState != GameState.Running){
			return; // game is not started or is ended
		}
		
		if (Core.team1PortalLocation1.getBlock().getType() != Material.PORTAL
				|| Core.team1PortalLocation2.getBlock().getType() != Material.PORTAL){
			Core.gameManager.endGameInitiate(2); // red wins (green lost one of their portals)
			stop();
		}
		else if (Core.team2PortalLocation1.getBlock().getType() != Material.PORTAL
				|| Core.team2PortalLocation2.getBlock().getType() != Material.PORTAL){
			Core.gameManager.endGameInitiate(1); // green wins (red lost one of their portals)
			stop();
		}
	}
}