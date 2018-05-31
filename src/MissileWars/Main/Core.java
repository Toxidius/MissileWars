package MissileWars.Main;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import MissileWars.GameManagement.GameManager;
import MissileWars.GameMechanics.DisabledEvents;
import MissileWars.GameMechanics.MissilePlacing;
import MissileWars.GameMechanics.PlayerChat;
import MissileWars.GameMechanics.PlayerDeath;
import MissileWars.GameMechanics.PlayerJoin;
import MissileWars.Main.GameStates.GameState;

public class Core extends JavaPlugin{
	public static JavaPlugin thisPlugin;
	public static Random r;
	public static boolean gameStarted;
	public static GameState gameState;
	public static int gameID;
	public static String MOTD;
	
	public static World lobbyWorld;
	public static World gameWorld;
	
	public static float lobbyYaw; 
	public static Location lobbySpawn;
	public static float team1Yaw;
	public static Location team1Spawn;
	public static float team2Yaw;
	public static Location team2Spawn;
	
	public static Location team1PortalLocation1;
	public static Location team1PortalLocation2;
	public static Location team2PortalLocation1;
	public static Location team2PortalLocation2;
	
	public static ChatColor team1Color;
	public static ChatColor team2Color;
	public static Color team1LeatherColor;
	public static Color team2LeatherColor;
	
	// global objects
	public static GameManager gameManager;
	public static PluginManager pluginMan;
	
	@SuppressWarnings("unused")
	@Override
	public void onEnable(){
		thisPlugin = this;
		r = new Random();
		gameStarted = false;
		gameState = GameState.NotStarted;
		gameID = 1;
		MOTD = "Missile Wars!";
		pluginMan = Bukkit.getPluginManager();
		
		lobbyWorld = Bukkit.getWorld("world");
		gameWorld = null;
		lobbyYaw = 90;
		lobbySpawn = new Location(lobbyWorld, -100.5, 70, 0.5, lobbyYaw, 0F);
		team1Yaw = 180;
		team1Spawn = new Location(lobbyWorld, -26.5, 77, 65.5, team1Yaw, 0F);
		team2Yaw = 0;
		team2Spawn = new Location(lobbyWorld, -26.5, 77, -64.5, team2Yaw, 0F);
		
		team1PortalLocation1 = new Location(lobbyWorld, -28, 72, 72);
		team1PortalLocation2 = new Location(lobbyWorld, -26, 72, 72);
		team2PortalLocation1 = new Location(lobbyWorld, -26, 72, -72);
		team2PortalLocation2 = new Location(lobbyWorld, -28, 72, -72);
		
		team1Color = ChatColor.GREEN;
		team2Color = ChatColor.RED;
		team1LeatherColor = Color.GREEN;
		team2LeatherColor = Color.RED;
		
		// non-global objects
		PlayerJoin playerJoin = new PlayerJoin();
		PlayerChat playerChat = new PlayerChat();
		PlayerDeath playerDeath = new PlayerDeath();
		DisabledEvents disabledEvents = new DisabledEvents();
		MissilePlacing missilePlacing = new MissilePlacing();
		
		// initialize objects
		gameManager = new GameManager();
		
		// register listeners
		// no longer needed -- each listener class is responsible for registering as a listener by calling registerListener()
	}
	
	@Override
	public void onDisable(){
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("start")){
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			
			// force start game with random world
			gameManager.startGame(null);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("end")){
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			
			// force end game
			gameManager.endGameInitiate(-1); // end with no team winning
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("warps")){
			if ( !(sender instanceof Player)){
				sender.sendMessage("Must be player to use this command.");
				return true;
			}
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			sender.sendMessage(ChatColor.GRAY + "Currently loaded worlds: ");
			for (World world : Bukkit.getWorlds()){
				sender.sendMessage(ChatColor.GRAY + world.getName());
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("warp")){
			if ( !(sender instanceof Player)){
				sender.sendMessage("Must be player to use this command.");
				return true;
			}
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			if (args.length < 1){
				sender.sendMessage("Must specify a world to warp to. Ex: /warp world");
				return true;
			}
			if (gameManager.worldManager.worldTools.checkWorldExists(args[0]) == false){
				sender.sendMessage("The world " + args[0] + " doesn't exist!");
				return true;
			}
			// load the world if it isn't already loaded
			gameManager.worldManager.worldTools.loadWorld(args[0]);
			Player player = (Player) sender;
			World world = Bukkit.getWorld(args[0]);
			Location spawnLocation = world.getSpawnLocation();
			player.teleport(spawnLocation);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("getmissiles")){
			if ( !(sender instanceof Player)){
				sender.sendMessage("Must be player to use this command.");
				return true;
			}
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			
			// gives the player all the missile eggs
			Player player = (Player) sender;
			for (ItemStack item : Core.gameManager.missiles.getMissileEggs()){
				player.getInventory().addItem(item);
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("setteam")){
			if ( !(sender instanceof Player)){
				sender.sendMessage("Must be player to use this command.");
				return true;
			}
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			
			// sets the player's team
			if (gameStarted == false){
				sender.sendMessage("The game hasn't started yet!");
				return true;
			}
			if (args.length < 2){
				sender.sendMessage("Must specify a player and the team. Ex: /setteam Notch 1");
				return true;
			}
			if (Bukkit.getPlayer(args[0]) == null){
				sender.sendMessage(args[0] + " doesn't appear to be online.");
				return true;
			}
			if ((Integer.valueOf(args[1]) < 1) || (Integer.valueOf(args[1]) > 2)){
				sender.sendMessage("Must specify player and team number (1 or 2)");
				return true;
			}
			String name = args[0];
			Player player = Bukkit.getPlayer(name);
			int newTeam = Integer.valueOf(args[1]);
			gameManager.setPlayerTeam(player, newTeam, true); // set new team and teleport to team spawn
			sender.sendMessage(ChatColor.GRAY + name + " has been moved to team " + newTeam);
			return true;
		}
		return false;
	}
	
	public static void registerListener(Listener listener){
		pluginMan.registerEvents(listener, thisPlugin);
	}
}
