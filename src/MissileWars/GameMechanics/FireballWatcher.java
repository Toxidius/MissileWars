package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Fireball;

import MissileWars.Main.Core;

public class FireballWatcher implements Runnable{

	private Fireball fireball;
	private int id;
	
	public FireballWatcher(Fireball fireball) {
		this.fireball = fireball;
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 20*20, 20*20); // delay 20 secs, interval 20 secs
	}
	
	public void end(){
		Bukkit.getScheduler().cancelTask(id);
	}
	
	@Override
	public void run() {
		if (fireball == null
				|| fireball.isDead()){
			end();
		}
		fireball.setTicksLived(1); // reset the ticks lived (so it doesn't disappear/despawn)
		//Bukkit.getServer().broadcastMessage("reset ticks");
	}
}
