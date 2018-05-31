package MissileWars.GameMechanics;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import MissileWars.Main.Core;
import MissileWars.Main.GameStates.GameState;

public class MissileSpawner implements Runnable{

	private int id;
	private int calls;
	
	public MissileSpawner() {
		start();
	}
	
	public void start(){
		calls = 0;
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 80, 60*20); // delay (4 seconds), interval (1 minute)
	}
	
	public void stop(){
		Bukkit.getScheduler().cancelTask(id);
	}
	
	@Override
	public void run() {
		if (Core.gameStarted == false
				|| Core.gameState != GameState.Running){
			return; // game not running or game ending
		}
		calls++;
		
		// gives all players on teams a random missile
		int team;
		Random random = new Random();
		ItemStack[] missiles = Core.gameManager.missiles.getMissileEggs();
		ItemStack chosenMissile = missiles[random.nextInt(missiles.length)];
		
		// TODO: Remove -- temporary for testing
		if (calls == 1){
			for (Player player : Bukkit.getOnlinePlayers()){
				team = Core.gameManager.getPlayerTeam(player);
				
				if (team != -1){
					for (ItemStack stack : missiles){
						player.getInventory().addItem(stack);
					}
				}
			}
			return;
		}
		
		for (Player player : Bukkit.getOnlinePlayers()){
			team = Core.gameManager.getPlayerTeam(player);
			
			if (team != -1){
				// player is on a team and playing
				boolean alreadyHasMissile = Core.gameManager.doesPlayerHaveItemWithName(player, chosenMissile.getItemMeta().getDisplayName());
				if (alreadyHasMissile == true){
					player.sendMessage(ChatColor.RED + "You already have " 
							+ chosenMissile.getItemMeta().getDisplayName() 
							+ ChatColor.RED + " so you didn't recieve one!");
				} else{
					player.getInventory().addItem(chosenMissile);
				}
			}
		}
	}

}
