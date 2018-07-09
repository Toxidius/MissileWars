package MissileWars.GameMechanics;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import MissileWars.Main.Core;
import MissileWars.Main.GameStates.GameState;

public class MissilePlacing implements Listener{

	public MissilePlacing() {
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerPlaceMissile(PlayerInteractEvent e){
		if (Core.gameStarted == true
				&& Core.gameState == GameState.Running
				&& e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& e.getPlayer().getItemInHand() != null
				&& e.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG
				&& e.getPlayer().getGameMode() != GameMode.SPECTATOR){
			e.setCancelled(true); // prevent regular egg placing
			int team = Core.gameManager.getPlayerTeam(e.getPlayer());
			if (team != -1){
				// player is on a valid team
				
				boolean spawned = false;
				if (itemIsFireball(e.getPlayer().getItemInHand()) == true){
					// item in the player's hand is a fireball
					Location spawnLocation = e.getClickedBlock().getLocation().add(0.5, 2.0, 0.5);
					Fireball fireball = (Fireball) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.FIREBALL);
					fireball.setVelocity(new Vector(0.0, 0.0, 0.0));
					fireball.setDirection(new Vector(0.0, 0.0, 0.0));
					fireball.setCustomName("Punch Me!");
					fireball.setCustomNameVisible(true);
					@SuppressWarnings("unused")
					FireballWatcher watcher = new FireballWatcher(fireball);
					spawned = true;
				}else{
					// item in the player's hand is a missile; spawn it
					spawned = Core.gameManager.missiles.spawnMissile(e.getClickedBlock(), e.getPlayer());
				}
				
				// remove this item from the player's hand if the missile/fireball was spawned correctly
				if (spawned == true
						&& e.getPlayer().getGameMode() != GameMode.CREATIVE){
					e.getPlayer().getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
				}
			}
		}
	}
	
	public boolean itemIsFireball(ItemStack item){
		if (item != null
				&& item.hasItemMeta()
				&& item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().contains(
						Core.gameManager.missiles.fireballEgg.getItemMeta().getDisplayName() )){
			return true;
		}
		return false;
	}
	
}
