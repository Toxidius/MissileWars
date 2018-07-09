package MissileWars.GameMechanics;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import MissileWars.Main.Core;

public class Missiles {

	// missile spawn egg items
	public ItemStack guardianEgg = new ItemStack(Material.MONSTER_EGG, 1, (short)68);
	public ItemStack shieldbusterEgg = new ItemStack(Material.MONSTER_EGG, 1, (short)66);
	public ItemStack tomahawkEgg = new ItemStack(Material.MONSTER_EGG, 1, (short)50);
	public ItemStack juggernautEgg = new ItemStack(Material.MONSTER_EGG, 1, (short)95);
	public ItemStack lightningEgg = new ItemStack(Material.MONSTER_EGG, 1, (short)98);
	public ItemStack shieldEgg = new ItemStack(Material.SNOW_BALL, 1);
	public ItemStack fireballEgg = new ItemStack(Material.MONSTER_EGG, 1, (short)61);
	public ItemStack arrows = new ItemStack(Material.ARROW, 3);
	
	// team1 missile locations
	public Location team1GuardianLocation = new Location(Core.gameWorld, -94, 63, 25);
	public Location team1ShieldbusterLocation = new Location(Core.gameWorld, -108, 63, 24);
	public Location team1TomahawkLocation = new Location(Core.gameWorld, -112, 62, 24);
	public Location team1JuggernautLocation = new Location(Core.gameWorld, -103, 63, 24);
	public Location team1LightningLocation = new Location(Core.gameWorld, -98, 62, 25);
	
	// team2 missile locations
	public Location team2GuardianLocation = new Location(Core.gameWorld, -93, 63, -25);
	public Location team2ShieldbusterLocation = new Location(Core.gameWorld, -108, 63, -24);
	public Location team2TomahawkLocation = new Location(Core.gameWorld, -113, 62, -24);
	public Location team2JuggernautLocation = new Location(Core.gameWorld, -103, 63, -24);
	public Location team2LightningLocation = new Location(Core.gameWorld, -98, 62, -25);
	
	// missle lengths
	public int guardianLength = 8;
	public int shieldbusterLength = 15;
	public int tomahawkLength = 13;
	public int juggernautLength = 11;
	public int lightningLength = 10;
	
	// team1 shield locations (center of shield)
	Location team1ShieldLocation = new Location(Core.gameWorld, -115, 62, 1);
	
	// team2 shield locations (center of shield)
	Location team2ShieldLocation = new Location(Core.gameWorld, -115, 62, -1);
	
	public Missiles(){
		ItemMeta meta;
		
		meta = guardianEgg.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Deploy Guardian");
		guardianEgg.setItemMeta(meta);
		
		meta = shieldbusterEgg.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Deploy Shieldbuster");
		shieldbusterEgg.setItemMeta(meta);
		
		meta = tomahawkEgg.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Deploy Tomahawk");
		tomahawkEgg.setItemMeta(meta);
		
		meta = juggernautEgg.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Deploy Juggernaut");
		juggernautEgg.setItemMeta(meta);
		
		meta = lightningEgg.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Deploy Lightning");
		lightningEgg.setItemMeta(meta);
		
		meta = shieldEgg.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Deploy Shield");
		shieldEgg.setItemMeta(meta);
		
		meta = fireballEgg.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Deploy Fireball");
		fireballEgg.setItemMeta(meta);
	}
	
	/**
	 * Gets an array containing all the missile egg itemstacks
	 * @return array of itemstacks
	 */
	public ItemStack[] getSpawnEggs(){
		ItemStack[] stack = new ItemStack[8];
		stack[0] = guardianEgg;
		stack[1] = shieldbusterEgg;
		stack[2] = tomahawkEgg;
		stack[3] = juggernautEgg;
		stack[4] = lightningEgg;
		stack[5] = shieldEgg;
		stack[6] = fireballEgg;
		stack[7] = arrows;
		return stack;
	}
	
	/**
	 * Calculates necessary information for placing down the corrisponding missile that
	 * the user has in their hand.
	 * @param clickedBlock the block the player clicked
	 * @param player the player who clicked
	 * @return true if the missile was spawned, otherwise, false
	 */
	public boolean spawnMissile(Block clickedBlock, Player player){
		ItemStack itemInHand = player.getItemInHand();
		if (itemInHand == null
				|| itemInHand.getType() == Material.AIR){
			return false; // empty item
		}
		
		int team = Core.gameManager.getPlayerTeam(player);
		if (team == -1){
			return false; // player isn't on a team
		}
		
		// determine the coordinates and build the missile there
		Location startingLocation = null;
		Location missileLocation = null;
		int missileLength = getMissileLength(itemInHand);
		if (team == 1){
			// green team
			startingLocation = clickedBlock.getLocation().add(0, -3, -3);
			if (startingLocation.getBlockZ() > 66){
				// player is trying to place the missile behind their own portal
				player.sendMessage(ChatColor.RED + "You can't place missiles back here!");
				return false;
			}
			if (startingLocation.getBlockX() <= -71){
				// player is trying to place a missile near or inside the barrier blocks (to grief missile storage)
				player.sendMessage(ChatColor.RED + "You can't place missiles over here!");
				return false;
			}
			if (startingLocation.getBlock().getType() == Material.STAINED_GLASS
					&& startingLocation.getBlock().getZ() >= 51){
				player.sendMessage(ChatColor.RED + "You can't place missiles in your shield!");
				return false;
			}
			missileLocation = getMissileLocation(team, itemInHand);
			
			buildMissile(startingLocation, missileLocation, missileLength, team);
		}
		else if (team == 2){
			// green team
			startingLocation = clickedBlock.getLocation().add(0, -3, 3);
			if (startingLocation.getBlockZ() < -65){
				// player is trying to place the missile behind their own portal
				player.sendMessage(ChatColor.RED + "You can't place missiles back here!");
				return false;
			}
			if (startingLocation.getBlockX() <= -71){
				// player is trying to place a missile near or inside the barrier blocks (to grief missile storage)
				player.sendMessage(ChatColor.RED + "You can't place missiles over here!");
				return false;
			}
			if (startingLocation.getBlock().getType() == Material.STAINED_GLASS
					&& startingLocation.getBlockZ() <= -51){
				player.sendMessage(ChatColor.RED + "You can't place missiles in your shield!");
				return false;
			}
			missileLocation = getMissileLocation(team, itemInHand);
			
			buildMissile(startingLocation, missileLocation, missileLength, team);
		}
		return true;
	}
	
	public void printLocation(Location location, String preMessage){
		Bukkit.getServer().broadcastMessage(preMessage 
				+ " x:" + location.getBlockX() 
				+ " y:" + location.getBlockY() 
				+ " z:" + location.getBlockZ());
	}
	
	/**
	 * Retrieves the location of the head of the missile in storage
	 * @param team the team the player is on (1 or 2)
	 * @param item the item the player is holding (should be a missile spawn egg)
	 * @return the location of the missile head in storage
	 */
	public Location getMissileLocation(int team, ItemStack item){
		if (item == null
				|| item.hasItemMeta() == false){
			return null; // bad item or has no name
		}
		
		ItemMeta meta = item.getItemMeta();
		if (team == 1){
			// green team
			if (meta.getDisplayName().contains("Guardian")){
				return team1GuardianLocation;
			}
			else if (meta.getDisplayName().contains("Shieldbuster")){
				return team1ShieldbusterLocation;
			}
			else if (meta.getDisplayName().contains("Tomahawk")){
				return team1TomahawkLocation;
			}
			else if (meta.getDisplayName().contains("Juggernaut")){
				return team1JuggernautLocation;
			}
			else if (meta.getDisplayName().contains("Lightning")){
				return team1LightningLocation;
			}
			else if (meta.getDisplayName().contains("Tomahawk")){
				return team1TomahawkLocation;
			}
		}
		else if (team == 2){
			// red team
			if (meta.getDisplayName().contains("Guardian")){
				return team2GuardianLocation;
			}
			else if (meta.getDisplayName().contains("Shieldbuster")){
				return team2ShieldbusterLocation;
			}
			else if (meta.getDisplayName().contains("Tomahawk")){
				return team2TomahawkLocation;
			}
			else if (meta.getDisplayName().contains("Juggernaut")){
				return team2JuggernautLocation;
			}
			else if (meta.getDisplayName().contains("Lightning")){
				return team2LightningLocation;
			}
			else if (meta.getDisplayName().contains("Tomahawk")){
				return team2TomahawkLocation;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the length of the missile the player is holding
	 * @param item the missile spawn egg the player is holding
	 * @return the length of that missile
	 */
	public int getMissileLength(ItemStack item){
		if (item == null
				|| item.hasItemMeta() == false){
			return -1; // bad item or has no name
		}
		
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().contains("Guardian")){
			return guardianLength;
		}
		else if (meta.getDisplayName().contains("Shieldbuster")){
			return shieldbusterLength;
		}
		else if (meta.getDisplayName().contains("Tomahawk")){
			return tomahawkLength;
		}
		else if (meta.getDisplayName().contains("Juggernaut")){
			return juggernautLength;
		}
		else if (meta.getDisplayName().contains("Lightning")){
			return lightningLength;
		}
		else if (meta.getDisplayName().contains("Tomahawk")){
			return tomahawkLength;
		}
		
		return -1; // no item found
	}
	
	/**
	 * Builds the missile on the map using the given values then triggers it to start moving
	 * @param startLocation the head location where the missile should spawn on the map
	 * @param missileLocation the head location of the missile in storage
	 * @param length the length of the missile to build
	 * @param team the team the player is on
	 */
	@SuppressWarnings("deprecation")
	public void buildMissile(Location startLocation, Location missileLocation, int length, int team){
		ArrayList<Block> updateableBlocks = new ArrayList<>();
		
		if (team == 1){
			// green team
			Block tempBlock;
			Block missileBlock;
			
			for (int z = length-1; z >= 0; z--){
				for (int y = -2; y <= 0; y++){
					for (int x = -1; x <= 1; x++){
						missileBlock = missileLocation.clone().add(x, y, -z).getBlock();
						tempBlock = startLocation.clone().add(x, y, -z).getBlock();
						
						if (missileBlock.getType() == Material.AIR){
							continue; // skip air blocks
						}
						
						// sets this block to the same data as the one in storage (basically copies it)
						tempBlock.setType(missileBlock.getType(), false); // set the type and don't apply physics to it
						tempBlock.setData(missileBlock.getData(), false); // set the data and don't apply physics to it
						
						/*
						 * checks if the block is a piston and if it has an adjacent piston. If so,
						 * this piston can be updated to trigger the missile to move
						 */
						if (missileBlock.getType() == Material.PISTON_BASE
								|| missileBlock.getType() == Material.PISTON_STICKY_BASE){
							if (doesBlockHaveNearbyPiston(missileBlock) == true){
								updateableBlocks.add(tempBlock);
							}
						}
					}
				}
			}
		}
		else if (team == 2){
			Block tempBlock;
			Block missileBlock;
			
			for (int z = length-1; z >= 0; z--){
				for (int y = -2; y <= 0; y++){
					for (int x = 1; x >= -1; x--){
						missileBlock = missileLocation.clone().add(x, y, z).getBlock();
						tempBlock = startLocation.clone().add(x, y, z).getBlock();
						
						if (missileBlock.getType() == Material.AIR){
							continue; // skip air blocks
						}
						
						// sets this block to the same data as the one in storage (basically copies it)
						tempBlock.setType(missileBlock.getType(), false); // set the type and apply physics to it
						tempBlock.setData(missileBlock.getData(), false); // set the data and apply physics to it
						
						/*
						 * checks if the block is a piston and if it has an adjacent piston. If so,
						 * this piston can be updated to trigger the missile to move
						 */
						if (missileBlock.getType() == Material.PISTON_BASE
								|| missileBlock.getType() == Material.PISTON_STICKY_BASE){
							if (doesBlockHaveNearbyPiston(missileBlock) == true){
								updateableBlocks.add(tempBlock);
							}
						}
					}
				}
			}
		}
		
		// loop though all updateable piston blocks and updating them with fire (stays for 1 tick causing a block update)
		if (updateableBlocks.isEmpty() == false){
			for (Block block : updateableBlocks){
				//block.getState().update(true, true); // old force update -- does not work with quasi-connectivity
				updatePistonBlock(block);
			}
		}
		
	}
	
	/**
	 * Determines if the block has a piston on any direction
	 * @param block the block to check
	 * @return true if there is a nearby piston, otherwise, false
	 */
	public boolean doesBlockHaveNearbyPiston(Block block){
		if (block.getRelative(BlockFace.SOUTH).getType() == Material.PISTON_BASE
				|| block.getRelative(BlockFace.SOUTH).getType() == Material.PISTON_STICKY_BASE){
			return true;
		}
		if (block.getRelative(BlockFace.NORTH).getType() == Material.PISTON_BASE
				|| block.getRelative(BlockFace.NORTH).getType() == Material.PISTON_STICKY_BASE){
			return true;
		}
		if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON_BASE
				|| block.getRelative(BlockFace.EAST).getType() == Material.PISTON_STICKY_BASE){
			return true;
		}
		if (block.getRelative(BlockFace.WEST).getType() == Material.PISTON_BASE
				|| block.getRelative(BlockFace.WEST).getType() == Material.PISTON_STICKY_BASE){
			return true;
		}
		if (block.getRelative(BlockFace.UP).getType() == Material.PISTON_BASE
				|| block.getRelative(BlockFace.UP).getType() == Material.PISTON_STICKY_BASE){
			return true;
		}
		if (block.getRelative(BlockFace.DOWN).getType() == Material.PISTON_BASE
				|| block.getRelative(BlockFace.DOWN).getType() == Material.PISTON_STICKY_BASE){
			return true;
		}
		return false;
	}
	
	/**
	 * Tries to update the piston using a temporary fire
	 * @param block the piston block to update
	 * @return true if the block could be updated with fire, otherwise, false
	 */
	public boolean updatePistonBlock(Block block){
		if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR){
			block.getRelative(BlockFace.DOWN).setType(Material.FIRE);
			return true;
		}
		if (block.getRelative(BlockFace.UP).getType() == Material.AIR){
			block.getRelative(BlockFace.UP).setType(Material.FIRE);
			return true;
		}
		else if (block.getRelative(BlockFace.NORTH).getType() == Material.AIR){
			block.getRelative(BlockFace.NORTH).setType(Material.FIRE);
			return true;
		}
		else if (block.getRelative(BlockFace.SOUTH).getType() == Material.AIR){
			block.getRelative(BlockFace.SOUTH).setType(Material.FIRE);
			return true;
		}
		else if (block.getRelative(BlockFace.EAST).getType() == Material.AIR){
			block.getRelative(BlockFace.EAST).setType(Material.FIRE);
			return true;
		}
		else if (block.getRelative(BlockFace.WEST).getType() == Material.AIR){
			block.getRelative(BlockFace.WEST).setType(Material.FIRE);
			return true;
		}
		return false;
	}
	
	
}