package MissileWars.GameMechanics;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import MissileWars.Main.Core;

public class ShieldThrow implements Listener{

	public ShieldThrow() {
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onSnowballThrow(ProjectileLaunchEvent e){
		if (Core.gameStarted == true
				&& e.getEntity() instanceof Snowball
				&& e.getEntity().getShooter() instanceof Player){
			Player player = (Player) e.getEntity().getShooter();
			int team = Core.gameManager.getPlayerTeam(player);
			Snowball snowball = (Snowball) e.getEntity();
			/*
			 * start a new shield watcher which will watch the snowball for 1 second
			 * and then will place down the shield at it's location
			 */
			@SuppressWarnings("unused")
			ShieldWatcher watcher = new ShieldWatcher(snowball, team); 
		}
	}
}