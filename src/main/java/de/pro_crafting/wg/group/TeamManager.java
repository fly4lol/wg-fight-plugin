package de.pro_crafting.wg.group;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import de.pro_crafting.wg.OfflineRunable;
import de.pro_crafting.wg.Util;
import de.pro_crafting.wg.WarGear;
import de.pro_crafting.wg.arena.Arena;

public class TeamManager
{
	private WarGear plugin;
	private Arena arena;
	private WgTeam team1;
	private WgTeam team2;
	
	public TeamManager(WarGear plugin, Arena arena)
	{
		this.plugin = plugin;
		this.team1 = new WgTeam(PlayerRole.Team1);
		this.team2 = new WgTeam(PlayerRole.Team2);
		this.arena = arena;
	}
	
	public Location getTeamSpawn(PlayerRole team)
	{
		if (team == PlayerRole.Team1)
		{
			return arena.getRepo().getTeam1Warp();
		}
		else
		{
			return arena.getRepo().getTeam2Warp();
		}
	}
	
	public void quitFight()
	{
		quiteFightForTeam(this.team1);
		quiteFightForTeam(this.team2);
		this.team1 = new WgTeam(PlayerRole.Team1);
		this.team2 = new WgTeam(PlayerRole.Team2);
	}
	
	private void quiteFightForTeam(WgTeam team)
	{
		final Location teleportLocation = this.arena.getRepo().getSpawnWarp();
		OfflineRunable fightQuiter = new OfflineRunable() {
			
			public void run(TeamMember member) {
				member.getPlayer().getInventory().clear();
				member.getPlayer().teleport(teleportLocation, TeleportCause.PLUGIN);
			}
		};
		this.plugin.getOfflineManager().run(fightQuiter, team);
	}
	
	public void sendWinnerOutput(PlayerRole teamName)
	{
		String team = "["+teamName.toString()+"] "+concateTeamPlayers(this.getTeamOfName(teamName));
		this.arena.broadcastMessage(ChatColor.DARK_GREEN + team + " hat gewonnen!");
	}
	
	private String concateTeamPlayers(WgTeam team)
	{
		String ret = "";
		for (TeamMember member : team.getTeamMembers())
		{
			if(member.isOnline()){
				ret += member.getPlayer().getDisplayName()+ " ";
			} else {
				ret += member.getOfflinePlayer().getName()+ " ";
			}
			
		}
		return ret.trim();
	}
	
	public void sendTeamOutput()
	{
		String team1 = arena.getRepo().getTeam1Prefix()+
				"[Team1]"+ ChatColor.YELLOW +""+ ChatColor.ITALIC+concateTeamPlayers(this.getTeam1());
		String team2 = arena.getRepo().getTeam2Prefix()+
				"[Team2]"+ ChatColor.YELLOW +""+ ChatColor.ITALIC+concateTeamPlayers(this.getTeam2());
		this.arena.broadcastMessage(ChatColor.YELLOW +""+ ChatColor.ITALIC+team1);
		this.arena.broadcastMessage(ChatColor.YELLOW +""+ ChatColor.ITALIC+team2);
	}
	
	public void healTeam(WgTeam team)
	{
		OfflineRunable healer = new OfflineRunable() {
			public void run(TeamMember member) {
				Util.makeHealthy(member.getPlayer());
			}
		};
		this.plugin.getOfflineManager().run(healer, team);
	}
	 
	public WgTeam getTeamOfName(PlayerRole name)
	{
		if (name == PlayerRole.Team1)
		{
			return this.team1;
		}
		else
		{
			return this.team2;
		}
	}
	
	public String getPrefix(PlayerRole role) {
		if (role == PlayerRole.Team1) {
			return this.arena.getRepo().getTeam1Prefix();
		} else if (role == PlayerRole.Team2) {
			return this.arena.getRepo().getTeam2Prefix();
		}
		return "§7";
	}
	
	public WgTeam getTeamOfPlayer(OfflinePlayer p)
	{
		if (this.team1.getTeamMember(p) != null)
		{
			return this.team1;
		}
		else if (this.team2.getTeamMember(p) != null)
		{
			return this.team2;
		}
		return null;
	}
	 
	public WgTeam getTeamWithOutLeader()
	{
		if (!this.team1.hasTeamLeader())
		{
			return this.team1;
		}
		else if (!this.team2.hasTeamLeader())
		{
			return this.team2;
		}
		return null;
	}
	 
	public boolean areBothTeamsReady()
	{
		return this.team1.isReady() && this.team2.isReady();
	}
	 
	public boolean isAlive(Player p)
	{
		if (this.getTeamOfPlayer(p) == null || this.getTeamOfPlayer(p).getTeamMember(p) == null)
		{
			 return false;
		}
		return this.getTeamOfPlayer(p).getTeamMember(p).isAlive();
	}
	 
	public TeamMember getTeamMember(OfflinePlayer p)
	{
		if (this.team1.getTeamMember(p) != null)
		{
			return this.team1.getTeamMember(p);
		}
		if (this.team2.getTeamMember(p) != null)
		{
			return this.team2.getTeamMember(p);
		}
		return null;
	}
	
	public WgTeam getTeam1() {
		return team1;
	}

	public WgTeam getTeam2() {
		return team2;
	}
}