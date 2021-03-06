package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import MissileWars.Main.Core;

public class PlayerJoinSetup implements Runnable{
	
	private Player player;
	
	public PlayerJoinSetup(Player player) {
		this.player = player;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, this, 20L); // perform team setting in 1 second
	}
	
	@Override
	public void run() {
		int team = getLowestTeam(); // get the team with the least amount of players
		
		player.setGameMode(GameMode.SURVIVAL);
		player.setFallDistance(0); // so they don't die if falling
		
		player.setHealth(18); // near full health
		//regen 5 for 1 second -- makes the scoreboard health value for the players automatically update
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 4));
		
		player.setFoodLevel(20); // set food level to full
		player.setSaturation(40); // set saturation to 40
		Core.gameManager.clearInventory(player);
		Core.gameManager.givePlayerTeamArmor(player);
		Core.gameManager.givePlayerBow(player);
		Core.gameManager.setPlayerTeam(player, team, true); // set the player's team and teleport them to their team spawn
		
		if (team == 1){
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + " has been placed on Green");
		}
		else if (team == 2){
			Bukkit.getServer().broadcastMessage(ChatColor.GRAY + player.getName() + " has been placed on Red");
		}
		
		player.sendMessage(ChatColor.LIGHT_PURPLE + "All chat is team unless prefaced with a \"!\" for global message");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Ex: \"!Hi Everybody\" for a global message");
	}
	
	public int getLowestTeam(){
		int numTeam1 = 0, numTeam2 = 0;
		int team = 1; // default
		
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.hasMetadata("game" + Core.gameID + "team")){
				team = player.getMetadata("game" + Core.gameID + "team").get(0).asInt();
				if (team == 1){
					numTeam1++;
				}
				else if (team == 2){
					numTeam2++;
				}
			}
		}
		
		if (numTeam1 == numTeam2){
			/*
			 * red gets player first if both are even
			 * green gets most players when the game starts if the count is odd
			 * this is a way of making it somewhat "even"
			 */
			team = 2; 
		} else if (numTeam1 < numTeam2){
			team = 1; // green
		} else{
			team = 2; // red
		}
		
		return team;
	}

}
