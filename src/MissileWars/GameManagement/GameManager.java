package MissileWars.GameManagement;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import MissileWars.GameMechanics.RespawnTimerRunnable;
import MissileWars.Main.Core;
import MissileWars.Main.GameStates.GameState;

public class GameManager {
	
	private Random r;
	public WorldManager worldManager;
	public ScoreboardManager scoreboardManager;
	public GameStarter gameStarter;
	
	public GameManager(){
		r = new Random();
		worldManager = new WorldManager();
		scoreboardManager = new ScoreboardManager();
		
		gameStarter = new GameStarter();
		gameStarter.start();
	}
	
	public boolean startGame(String worldName){
		// returns whether or not the game started successfully
		
		// check if a game can start
		if (Core.gameStarted == true){
			return false; // game is already in progress!
		}
		
		// reset some values
		scoreboardManager = new ScoreboardManager();
		gameStarter.stop();
		
		// create the game world
		//Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Loading game world...");
		boolean output = createGameWorld(worldName);
		if (output == false){
			return false; // game could not be started because the world couldn't be loaded
		}
		//Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Done loading game world.");
		
		// update world locations
		Core.team1Spawn.setWorld(Core.gameWorld);
		Core.team2Spawn.setWorld(Core.gameWorld);
		
		// generate the player teams
		generateTeams();
		
		// finish up the player and teleport into game
		int team;
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()){
				team = getPlayerTeam(player); // team
				player.setScoreboard(scoreboardManager.scoreboard);
				player.setGameMode(GameMode.SURVIVAL);
				player.setFallDistance(0); // so they don't die if falling
				player.setHealth(20); // full health
				player.setFoodLevel(20); // set food level to full
				player.setSaturation(40); // set saturation to 40
				clearInventory(player);
				givePlayerTeamArmor(player);
				if (team == 1){
					player.teleport(Core.team1Spawn);
				}
				else if (team == 2){
					player.teleport(Core.team2Spawn);
				}
			}
		}
		
		// game start messages
		Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "All chat is global.");
		
		// start scheduled events
		// TODO missile item spawner
		// set some final values
		Core.gameStarted = true;
		Core.gameState = GameState.Running;
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void endGameInitiate(int winningTeam){
		// initiates the game end sequence
		int seconds = 10;
		if (winningTeam == -1){
			seconds = 2;
		}
		
		// end all scheduled events
		Bukkit.getScheduler().cancelTasks(Core.thisPlugin);
		Core.gameState = GameState.Ending;
		
		// set all players in spectate mode
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()
					&& player.getGameMode() == GameMode.SURVIVAL){
				player.setGameMode(GameMode.SPECTATOR);
				Location newLocation = player.getLocation().add(0.0, 15.0, 0.0);
				if (newLocation.getY() < 30){
					newLocation.setY(70.0);
				}
				player.teleport(newLocation);
			}
		}
		
		String winningMessage = "";
		
		if (winningTeam == 1){
			winningMessage = ChatColor.BOLD + "" + Core.team1Color + "Green Team won the game!";
		}
		else if (winningTeam == 2){
			winningMessage = ChatColor.BOLD + "" + Core.team2Color + "Red Team won the game!";
		}
		else{
			winningMessage = ChatColor.DARK_RED + "" + ChatColor.BOLD + "The game was terminated!";
		}
		
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "-----------------------------------");
		Bukkit.getServer().broadcastMessage(winningMessage);
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "-----------------------------------");
		
		for (Player player : Bukkit.getOnlinePlayers()){
			player.sendTitle("", winningMessage);
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, new Runnable(){
			@Override
			public void run() {
				Core.gameManager.endGame();
			}
		}, seconds*20L); // 10 second delay
	}
	
	public boolean endGame(){
		// return whether or not the game ended successfully
		
		// teleport all players to lobby and reset their inventory and scoreboard
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()){
				if (player.hasMetadata("game" + Core.gameID + "team")){
					player.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
				}
				if (player.hasMetadata("game" + Core.gameID + "kit")){
					player.removeMetadata("game" + Core.gameID + "kit", Core.thisPlugin);
				}
				player.setScoreboard(scoreboardManager.emptyScoreboard);
				player.setGameMode(GameMode.SURVIVAL);
				player.setFallDistance(0); // so they don't die if falling
				player.setHealth(20); // full health
				player.setFoodLevel(20); // set food level to full
				player.setSaturation(40); // set saturation to 40
				player.setWalkSpeed(0.2F); // default walk speed
				for (PotionEffect effect : player.getActivePotionEffects()){ // remove all potion effects
					player.removePotionEffect(effect.getType());
				}
				clearInventory(player);
				player.teleport(Core.lobbySpawn);
			}
		}
		
		// reset some values
		Core.gameState = GameState.NotStarted;
		
		// startup game starter
		gameStarter.start();
		
		// delete the game world
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, new Runnable(){
			@Override
			public void run() {
				worldManager.deleteGameWorld();
				
				Core.gameStarted = false; // update this so the next game can start
			}
		}, 40L);
		
		return true;
	}
	
	public boolean createGameWorld(String worldName){
		return worldManager.createGameWorld(worldName);
	}
	
	public void generateTeams(){
		// give all players a random number and team of -1
		for (Player player : Bukkit.getOnlinePlayers()){
			player.setMetadata("game" + Core.gameID + "team", new FixedMetadataValue(Core.thisPlugin, new Integer(-1)));
			player.setMetadata( "randomNumber", new FixedMetadataValue(Core.thisPlugin, new Integer(r.nextInt(1000))) );
		}
		
		// loop through the players finding the next player (without team) with the smallest random number and place them on placedTeam
		int teamToBe = 1;
		while (true){
			int currentLowest = 1000000; // arbitrary value (greater than the maximum random) to start off with
			Player lowestPlayer = null;
			int random;
			int team;
			for (Player player : Bukkit.getOnlinePlayers()){
				random = player.getMetadata("randomNumber").get(0).asInt();
				team = player.getMetadata("game" + Core.gameID + "team").get(0).asInt();
				
				if ( (random < currentLowest) && (team == -1) ){
					currentLowest = random;
					lowestPlayer = player;
				}
			}
			
			if (lowestPlayer == null){
				// no player was choosen (all are on teams)
				// done looping through the array. all players should be on teams now...
				return;
			}
			
			lowestPlayer.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
			lowestPlayer.setMetadata("game" + Core.gameID + "team", new FixedMetadataValue(Core.thisPlugin, new Integer(teamToBe)));
			
			if (teamToBe == 1){
				scoreboardManager.addPlayerToTeam(lowestPlayer.getName(), 1);
			}
			else if (teamToBe == 2){
				scoreboardManager.addPlayerToTeam(lowestPlayer.getName(), 2);
			}
			
			// update teamToBe for next player
			teamToBe++;
			if (teamToBe > 2){
				teamToBe = 1;
			}
		}
	}
	
	public void givePlayerTeamArmor(Player player){
		LeatherArmorMeta meta1;
		LeatherArmorMeta meta2;
		
		ItemStack team1Chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		meta1 = (LeatherArmorMeta) team1Chestplate.getItemMeta();
		meta1.setColor(Core.team1LeatherColor);
		team1Chestplate.setItemMeta(meta1);
		ItemStack team2Chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		meta2 = (LeatherArmorMeta) team2Chestplate.getItemMeta();
		meta2.setColor(Core.team2LeatherColor);
		team2Chestplate.setItemMeta(meta2);
		
		ItemStack team1Leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		meta1 = (LeatherArmorMeta) team1Leggings.getItemMeta();
		meta1.setColor(Core.team1LeatherColor);
		team1Leggings.setItemMeta(meta1);
		ItemStack team2Leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		meta2 = (LeatherArmorMeta) team2Leggings.getItemMeta();
		meta2.setColor(Core.team2LeatherColor);
		team2Leggings.setItemMeta(meta2);
		
		ItemStack team1Boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		meta1 = (LeatherArmorMeta) team1Boots.getItemMeta();
		meta1.setColor(Core.team1LeatherColor);
		team1Boots.setItemMeta(meta1);
		ItemStack team2Boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		meta2 = (LeatherArmorMeta) team2Boots.getItemMeta();
		meta2.setColor(Core.team2LeatherColor);
		team2Boots.setItemMeta(meta2);
		
		int team = getPlayerTeam(player);
		if (team == 1){
			player.getInventory().setChestplate(team1Chestplate);
			player.getInventory().setLeggings(team1Leggings);
			player.getInventory().setBoots(team1Boots);
		}
		else if (team == 2){
			player.getInventory().setChestplate(team2Chestplate);
			player.getInventory().setLeggings(team2Leggings);
			player.getInventory().setBoots(team2Boots);
		}
	}
	
	public void setPlayerTeam(Player player, int team, boolean teleport){
		// set meta
		player.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
		player.setMetadata("game" + Core.gameID + "team", new FixedMetadataValue(Core.thisPlugin, new Integer(team)));
		
		// set scoreboard
		scoreboardManager.removePlayerFromTeam(player.getName(), team);
		scoreboardManager.addPlayerToTeam(player.getName(), team);
		
		// teleport to new team spawn
		if (teleport == true){
			if (team == 1){
				player.teleport(Core.team1Spawn);
			}
			else if (team == 2){
				player.teleport(Core.team2Spawn);
			}
		}
		
	}
	
	public int getPlayerTeam(String playerName){
		Player player = Bukkit.getPlayer(playerName);
		if (player == null){
			return -1; // no player with this name online
		}
		return getPlayerTeam(player);
	}
	
	public int getPlayerTeam(Player player){
		if (player.hasMetadata("game" + Core.gameID + "team") == true){
			return player.getMetadata("game" + Core.gameID + "team").get(0).asInt();
		}
		else{
			return -1; // no team
		}
	}
	
	public int getAmountOfItemInPlayerInventory(Player player, Material material, short durability){
		// gets the amount of a specific material in the players inventory
		int amount = 0;
		for (ItemStack stack : player.getInventory().getContents()){
			if (stack == null
					|| stack.getType() == Material.AIR){
				continue; // skip
			}
			if (stack.getType() == material
					&& stack.getDurability() == durability){
				amount += stack.getAmount();
			}
		}
		return amount;
	}
	
	@SuppressWarnings("deprecation")
	public void simulateDeath(Player player){
		// simulated death stuff
		player.setHealth(20); // respawn the player
		player.setFallDistance(0F);
		player.setFoodLevel(20); // set food level to full
		player.setSaturation(20); // set saturation to 20
		for (PotionEffect effect : player.getActivePotionEffects()){ // remove all potion effects
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(GameMode.SPECTATOR);
		Location newLocation = player.getLocation().add(0.0, 5.0, 0.0);
		if (newLocation.getY() < 20){
			newLocation.setY(70.0);
		}
		player.teleport(newLocation);
		
		// set the equipment the player will respawn with here
		givePlayerTeamArmor(player);
		
		// start the countdown timer thingy
		ItemStack[] keepArmor = player.getEquipment().getArmorContents();
		ItemStack[] keepInventory = player.getInventory().getContents();
		@SuppressWarnings("unused")
		RespawnTimerRunnable respawnTimer = new RespawnTimerRunnable(player, keepArmor, keepInventory);
		
		// send title "Respawning..."
		player.sendTitle(ChatColor.GOLD + "Respawning...", "");
	}
	
	public void removeItemWithName(Player player, String itemName){
		// removes the first instance of a item with a particular name from the players inventory
		
		PlayerInventory inv = player.getInventory();
		ItemStack[] contents = inv.getContents();
		for (int i = 0; i < contents.length; i++){
			if (contents[i] != null 
					&& contents[i].hasItemMeta() 
					&& contents[i].getItemMeta().hasDisplayName() 
					&& contents[i].getItemMeta().getDisplayName().equals(itemName)){
				inv.clear(i);
				return;
			}
		}
	}
	
	public void clearInventory(Player player){
		player.setExp(0F);
		player.setLevel(0);
		
		// clears the player's usable inventory
		player.getInventory().clear();
		
		// remove armor slot contents as getInventory().clear doesn't clear this
		PlayerInventory playerInvenotory = player.getInventory();
		ItemStack air = new ItemStack(Material.AIR);
		playerInvenotory.setHelmet(air);
		playerInvenotory.setChestplate(air);
		playerInvenotory.setLeggings(air);
		playerInvenotory.setBoots(air);
		
		// remove the item player has on their cursor
		player.setItemOnCursor(air);
		
		// remove any items in crafting window
		Inventory craftingInventory = player.getOpenInventory().getTopInventory();
		craftingInventory.setItem(1, air);
		craftingInventory.setItem(2, air);
		craftingInventory.setItem(3, air);
		craftingInventory.setItem(4, air);
	}

}