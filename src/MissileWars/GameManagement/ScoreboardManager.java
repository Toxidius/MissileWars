package MissileWars.GameManagement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import MissileWars.Main.Core;

public class ScoreboardManager{
	
	public Scoreboard scoreboard;
	public Scoreboard emptyScoreboard;
	
	private Team team1;
	private Team team2;
	//private Objective sidebarObjective;
	private Objective healthObjective;
	private Objective killsObjective;
	//private Score line1;
	//private Score line2;
	//private Score line3;
	//private Score line4;
	
	public int time = 0;
	
	public ScoreboardManager(){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	
	public void setupScoreboard(){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		team1 = scoreboard.registerNewTeam("Team1");
		team1.setAllowFriendlyFire(false);
		team1.setPrefix(Core.team1Color + "");
		
		team2 = scoreboard.registerNewTeam("Team2");
		team2.setAllowFriendlyFire(false);
		team2.setPrefix(Core.team2Color + "");
		
		healthObjective = scoreboard.registerNewObjective("Health", "health");
		healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		
		killsObjective = scoreboard.registerNewObjective("Kills", "playerKillCount");
		killsObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		// reset the current time(calls) for scoreboard runnable
		time = 0;
	}
	
	public void addPlayerToTeam(String playerName, int team){
		if (team == 1){
			team1.addEntry(playerName);
		}
		else if (team == 2){
			team2.addEntry(playerName);
		}
	}
	
	public void removePlayerFromTeam(String playerName, int team){
		if (team == 1){
			team1.removeEntry(playerName);
		}
		else if (team == 2){
			team2.removeEntry(playerName);
		}
	}
	
	/*
	 * Not needed
	public void updateSidebar(String title, String line1, String line2, String line3, String line4){
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		if (sidebarObjective != null){
			sidebarObjective.unregister(); // sidebar is already active, unregister the active one so it can be replaced
		}
		sidebarObjective = scoreboard.registerNewObjective("gameObjective", "dummy");
		sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		sidebarObjective.setDisplayName(title); // ChatColor.GOLD + "Paint Percent";
		
		this.line1 = sidebarObjective.getScore(line1); // time remaining
		this.line1.setScore(4);
		this.line2 = sidebarObjective.getScore(line2); // purple score
		this.line2.setScore(3);
		if (line3.length() > 1){
			this.line3 = sidebarObjective.getScore(line3); // green score
			this.line3.setScore(2);
		}
		if (line4.length() > 1){
			this.line4 = sidebarObjective.getScore(line4);
			this.line4.setScore(1);
		}
	}
	*/
	
	/*
	 * Not needed
	public void updateScoreboard(){
		String title, line1, line2, line3, line4;
		title = ChatColor.GOLD + "Paint Count";
		line1 = ChatColor.WHITE + "Time Remaining: " + Core.gameManager.timeRemaining;
		line2 = Core.team1Color + "Purple: " + ChatColor.WHITE + team1Percent() + "% (" + Core.gameManager.team1Blocks + ")";
		line3 = Core.team2Color + "Green: " + ChatColor.WHITE + team2Percent() + "% (" + Core.gameManager.team2Blocks + ")";
		line4 = "";
		
		updateSidebar(title, line1, line2, line3, line4);
		time++;
	}
	*/
	
	public boolean adminOnline(){
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOp()){
				return true;
			}
		}
		return false;
	}

}