package MissileWars.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.Vector;

import MissileWars.Main.Core;

public class DisabledEvents implements Listener{
	
	public DisabledEvents() {
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerLobbyDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player
				&& Core.gameStarted == false){
			e.setCancelled(true); // no lobby damage
			
			if (e.getCause() == DamageCause.VOID){
				e.getEntity().teleport(Core.lobbySpawn);
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		if (Core.gameStarted == true
				&& e.getPlayer().getGameMode() == GameMode.SURVIVAL
				&& e.getTo().getBlock().getType() != Material.AIR
				&& e.getTo().getBlock().getType().isSolid() == true
				&& e.getTo().getBlock().getType() != Material.STAINED_GLASS_PANE
				&& e.getTo().getBlock().getType() != Material.PISTON_EXTENSION){
			e.setCancelled(true); // player it getting glitched
			
			// set player velocity forward and up
			Vector newVelocity = new Vector(0.0, 0.1, 0.0);
			e.getPlayer().setVelocity(newVelocity);
			
			//Bukkit.getServer().broadcastMessage("prevented glitch");
		}
	}
	
	@EventHandler
	public void onFireballExplodePortal(EntityExplodeEvent e){
		if (e.getEntity() instanceof Fireball){
			for (Block block : e.blockList()){
				if (block.getType() == Material.PORTAL){
					e.blockList().clear(); // prevent the portal from being damaged by fireballs (team griefing)
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onServerPing(ServerListPingEvent e){
		int numOnline = Bukkit.getOnlinePlayers().size();
		String serverName = ChatColor.GREEN + "Missile Wars!";
		String MOTD = Core.MOTD;
		
		if (numOnline <= 0){
			e.setMotd(serverName + ChatColor.WHITE + " - " + ChatColor.WHITE + MOTD);
		}
		else if (numOnline > 0 && (MOTD.length()+" - ".length()+serverName.length()) >= 45){ // was 35
			e.setMotd(serverName + ChatColor.WHITE + " - " + ChatColor.WHITE + MOTD
					+ "   " + ChatColor.AQUA + ChatColor.UNDERLINE + numOnline + ChatColor.RESET + ChatColor.AQUA + " Currently online!");
		}
		else{
			e.setMotd(serverName + ChatColor.WHITE + " - " + ChatColor.WHITE + MOTD
					+ "\n" + ChatColor.AQUA + ChatColor.UNDERLINE + numOnline + ChatColor.RESET + ChatColor.AQUA + " Currently online!");
		}
		
		String address = e.getAddress().toString();
		address = address.substring(1, address.length());
		//System.out.println("Ping from " + address);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if (Core.gameStarted == false){
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
				e.setCancelled(true); // creative players allowed to break blocks in lobby (game not started)
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e){
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e){
		if (Core.gameStarted == false
				&& e.getPlayer().getGameMode() != GameMode.CREATIVE){
			e.setCancelled(true); // prevent picking up items
		}
	}
	
	@EventHandler
	public void onInventoryItemMove(InventoryClickEvent e){
		Player player = (Player) e.getWhoClicked();
		if (player.getGameMode() != GameMode.CREATIVE
				&& e.getSlotType() == SlotType.ARMOR){
			e.setCancelled(true); // only creative players allowed to move armor items in inventory
		}
	}
	
	@EventHandler
	public void onPortalTravel(PlayerPortalEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerAchievement(PlayerAchievementAwardedEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onHungerDeplete(FoodLevelChangeEvent e){
		if (e.getEntity() instanceof Player){
			e.setCancelled(true); // cancel the regular health depleat so the following modifications take place
			Player player = (Player) e.getEntity();
			player.setFoodLevel(20); // set food level to full
			player.setSaturation(20); // set saturation to 20
		}
	}
	
	@EventHandler
	public void spectatorVoidDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();
			if (player.getGameMode() == GameMode.SPECTATOR){
				e.setCancelled(true); // cancel spectator damage by void
			}
		}
	}
}