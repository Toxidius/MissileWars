package MissileWars.GameMechanics;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import MissileWars.Main.Core;

public class PlayerDeath implements Listener{

	public PlayerDeath() {
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		e.getDrops().clear(); // clear out all items in the drops
		Player player = e.getEntity();
		player.setHealth(20); // auto-respawn/cancel-respawn the player
		Core.gameManager.simulateDeath(player);
		
		int spaceIndex = e.getDeathMessage().indexOf(" ") + 1;
		String cause = e.getDeathMessage().substring(spaceIndex); // returns the text after the players name (cause of death)
		
		int team = Core.gameManager.getPlayerTeam(player);
		if (team == 1){
			e.setDeathMessage(Core.team1Color + player.getName() + " " + ChatColor.WHITE + cause);
		}
		else if (team == 2){
			e.setDeathMessage(Core.team2Color + player.getName() + " " + ChatColor.WHITE + cause);
		}
		else{
			e.setDeathMessage(ChatColor.GRAY + player.getName() + " " + ChatColor.WHITE + cause);
		}
	}
	
	@EventHandler
	public void onPlayerDamageByVoid(EntityDamageEvent e){
		if (Core.gameStarted == true
				&& e.getEntity() instanceof Player
				&& e.getCause() == DamageCause.VOID){
			Player player = (Player) e.getEntity();
			player.setHealth(0.1); // speed up their death by void
		}
	}
}