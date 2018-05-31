package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import MissileWars.Main.Core;

public class PlayerChat implements Listener{
	
	public PlayerChat(){
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		e.setCancelled(true); // complete overriding of message behavior
		
		if (Core.gameStarted == false){
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + e.getPlayer().getName() + ChatColor.WHITE + " " + e.getMessage());
		}
		else{
			int playerTeam = Core.gameManager.getPlayerTeam(e.getPlayer().getName());
			if (playerTeam == -1){
				// spectator
				Bukkit.getServer().broadcastMessage(ChatColor.GRAY + e.getPlayer().getName() + ChatColor.WHITE + " " + e.getMessage());
			}
			else if (playerTeam == 1){
				Bukkit.getServer().broadcastMessage(Core.team1Color + e.getPlayer().getName() + ChatColor.WHITE + " " + e.getMessage());
			}
			else if (playerTeam == 2){
				Bukkit.getServer().broadcastMessage(Core.team2Color + e.getPlayer().getName() + ChatColor.WHITE + " " + e.getMessage());
			}
		}
	}
}