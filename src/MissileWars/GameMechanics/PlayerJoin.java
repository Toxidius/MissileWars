package MissileWars.GameMechanics;

import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import MissileWars.Main.Core;

public class PlayerJoin implements Listener{
	
	public PlayerJoin(){
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		if (e.getPlayer().hasAchievement(Achievement.OPEN_INVENTORY) == false){
			e.getPlayer().awardAchievement(Achievement.OPEN_INVENTORY);
		}
		
		if (Core.gameStarted == false){
			e.getPlayer().teleport(Core.lobbySpawn);
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
			Core.gameManager.clearInventory(e.getPlayer());
		}
		else{
			// game in progress
			int playerTeam = Core.gameManager.getPlayerTeam(e.getPlayer());
			if (playerTeam == -1){
				// has no team, teleport to spawn and run the player setup runnable
				e.getPlayer().setGameMode(GameMode.SPECTATOR);
				e.getPlayer().teleport(Core.team1Spawn);
				e.getPlayer().setScoreboard(Core.gameManager.scoreboardManager.scoreboard);
				e.getPlayer().sendMessage(ChatColor.GOLD + "Placing you on a team...");
				// start the runnable that places the player on a team
				@SuppressWarnings("unused")
				PlayerJoinSetup joinSetup = new PlayerJoinSetup(e.getPlayer());
			}
			else{
				// they are already in the game and have a team, teleport to their team's spawn
				if (playerTeam == 1){
					e.getPlayer().teleport(Core.team1Spawn);
				}
				else if (playerTeam == 2){
					e.getPlayer().teleport(Core.team2Spawn);
				}
				e.getPlayer().setScoreboard(Core.gameManager.scoreboardManager.scoreboard);
			}
		}
	}
}