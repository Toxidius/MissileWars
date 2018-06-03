package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

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
		
		Player otherPlayer;
		for (String string : cause.split(" ")){
			if (isStringAPlayerName(string) == true){
				otherPlayer = Bukkit.getPlayer(string);
				if (otherPlayer != null){
					int otherPlayerTeam = Core.gameManager.getPlayerTeam(otherPlayer);
					if (otherPlayerTeam == 1){
						cause = cause.replace(string, Core.team1Color + otherPlayer.getName() + ChatColor.WHITE);
					}
					else if (otherPlayerTeam == 2){
						cause = cause.replace(string, Core.team2Color + otherPlayer.getName() + ChatColor.WHITE);
					}
				}
			}
		}
		
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
	
	public boolean isStringAPlayerName(String input){
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getName().equals(input)){
				return true;
			}
		}
		return false;
	}
	
	public String getColoredDeathCause(String cause){
		if (cause.contains(" by ") == true){
			// retrieve the other player's name who aided in the kill of this player
			int location = cause.indexOf("by") + 3;
			String otherPlayerName = cause.substring(location); // gets all characters after "name killed by "
			location = otherPlayerName.indexOf(" ");
			otherPlayerName = otherPlayerName.substring(0, location); // takes off characters after player's name
			
			int otherPlayerTeam = Core.gameManager.getPlayerTeam(otherPlayerName);
			if (otherPlayerTeam != -1){
				// replace the old player's name with the new colorized name
				String colorizedName = "";
				if (otherPlayerTeam == 1){
					colorizedName = Core.team1Color + otherPlayerName + ChatColor.WHITE;
				}
				else if (otherPlayerTeam == 2){
					colorizedName = Core.team2Color + otherPlayerName + ChatColor.WHITE;
				}
				else{
					colorizedName = ChatColor.WHITE + otherPlayerName + ChatColor.WHITE;
				}
				cause = cause.replace(otherPlayerName, colorizedName);
				return cause;
			}
		}
		return null;
	}
}