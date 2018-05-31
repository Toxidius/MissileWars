package MissileWars.GameMechanics;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
				&& e.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG){
			e.setCancelled(true); // prevent regular egg placing
			int team = Core.gameManager.getPlayerTeam(e.getPlayer());
			if (team != -1){
				// player is on a team
				// TODO: check if the player is spawning the missile behind the playable zone
				
				// spawn the missile
				Core.gameManager.missiles.spawnMissile(e.getClickedBlock(), e.getPlayer());
				
				// remove this missile item from the player's hand
				if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
					e.getPlayer().getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
				}
			}
		}
	}
	
}
