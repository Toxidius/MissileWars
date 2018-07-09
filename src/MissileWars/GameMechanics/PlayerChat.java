package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import MissileWars.Main.Core;
import MissileWars.Main.GameStates.GameState;

public class PlayerChat implements Listener{
	
	public PlayerChat(){
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		e.setCancelled(true); // complete overriding of message behavior
		
		if (Core.gameStarted == false){
			// pregame message
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + e.getPlayer().getName() + ChatColor.WHITE + " " + e.getMessage());
		}
		else if (Core.gameState == GameState.Ending){
			// game ending message (global)
			Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "!" + ChatColor.WHITE + "] "
					+ getPlayerColor(e.getPlayer()) + e.getPlayer().getName() + ChatColor.WHITE + " " + e.getMessage());
		}
		else{
			// game message		
			if (e.getMessage().startsWith("!")){
				// global message
				String message = e.getMessage().substring(1); // remove the first "!" character
				
				Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "!" + ChatColor.WHITE + "] "
						+ getPlayerColor(e.getPlayer()) + e.getPlayer().getName() + ChatColor.WHITE + " " + message);
			}
			else {
				// team message
				String message = ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "Team" + ChatColor.WHITE + "] "
						+ getPlayerColor(e.getPlayer()) + e.getPlayer().getName() + ChatColor.WHITE + " " + e.getMessage();
				
				int playerTeam = Core.gameManager.getPlayerTeam(e.getPlayer().getName());
				
				Core.gameManager.teamMessage(playerTeam, message); // send out the message to all team mates
			}
		}
	}
	
	public ChatColor getPlayerColor(Player player){
		int playerTeam = Core.gameManager.getPlayerTeam(player);
		if (playerTeam == -1){
			// spectator
			return ChatColor.GRAY;
		}
		else if (playerTeam == 1){
			return Core.team1Color;
		}
		else if (playerTeam == 2){
			return Core.team2Color;
		}
		else{
			return ChatColor.GRAY;
		}
	}
}