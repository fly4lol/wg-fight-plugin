package de.hrc_gaming.wg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import de.hrc_gaming.wg.arena.Arena;
import de.hrc_gaming.wg.team.TeamMember;
import de.hrc_gaming.wg.team.WgTeam;

public class OfflineManager implements Listener{
	private WarGear plugin;
	private Map<WgTeam, List<OfflineRunable>> teamRunnables;
	private Map<TeamMember, List<OfflineRunable>> memberRunnables;
	private List<TeamMember> offlineTeamMembers;
	private BukkitTask task;
	
	public OfflineManager(WarGear plugin) {
		this.plugin = plugin;
		this.teamRunnables = new HashMap<WgTeam, List<OfflineRunable>>();
		this.memberRunnables = new HashMap<TeamMember, List<OfflineRunable>>();
		this.offlineTeamMembers = new ArrayList<TeamMember>();
		this.task = this.plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
			public void run()
			{
				checkTeamMembers();
			}
		}, 0, 20);
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public boolean run(OfflineRunable runable, WgTeam team)
	{
		if (!team.isOnline())
		{
			if (!this.teamRunnables.containsKey(team))
			{
				this.teamRunnables.put(team, new ArrayList<OfflineRunable>());
			}
			this.teamRunnables.get(team).add(runable);
		}
		else
		{
			runTeam(runable, team);
		}
		return team.isOnline();
	}
	
	public boolean run(OfflineRunable runable, TeamMember member)
	{
		if (!member.isOnline())
		{
			if (!this.memberRunnables.containsKey(member))
			{
				this.memberRunnables.put(member, new ArrayList<OfflineRunable>());
			}
			this.memberRunnables.get(member).add(runable);
		}
		else
		{ 
			runable.run(member);
		} 
		return member.isOnline();
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled=true)
	public void handlePlayerQuits(PlayerQuitEvent event)
	{
		Arena arena = this.plugin.getArenaManager().getArenaOfTeamMember(event.getPlayer());
		if (arena != null)
		{
			TeamMember member = arena.getTeam().getTeamMember(event.getPlayer());
			if (member.isAlive())
			{
				this.offlineTeamMembers.add(member);
			}
		}
	}
	
	private boolean isTooLongOffline(TeamMember member)
	{
		return System.currentTimeMillis() - member.getOfflinePlayer().getLastPlayed() > 30;
	}
	
	private void checkTeamMembers()
	{
		 Iterator<TeamMember> offlineIterator = this.offlineTeamMembers.iterator();
		 while(offlineIterator.hasNext())
		 {
			 TeamMember current = offlineIterator.next();
			 if (current.isOnline())
			 {
				 offlineIterator.remove();
			 }
			 else if (isTooLongOffline(current))
			 {
				 offlineIterator.remove();
				 killTeamMember(current);
			 }
		 }
		 
		 Iterator<Entry<TeamMember, List<OfflineRunable>>> memberIterator = this.memberRunnables.entrySet().iterator();
		 while(memberIterator.hasNext())
		 {
			 Entry<TeamMember, List<OfflineRunable>> current = memberIterator.next();
			 if (current.getKey().isOnline())
			 {
				 for (OfflineRunable runable : current.getValue())
				 {
					 runable.run(current.getKey());
				 }
				 memberIterator.remove();
			 }
			 else if (isTooLongOffline(current.getKey()))
			 {
				 memberIterator.remove();
				 killTeamMember(current.getKey());
			 }
		 }
		 
		 Iterator<Entry<WgTeam, List<OfflineRunable>>> teamIterator = this.teamRunnables.entrySet().iterator();
		 while(teamIterator.hasNext())
		 {
			 Entry<WgTeam, List<OfflineRunable>> current = teamIterator.next();
			 boolean everyoneOnline = true;
			 for (TeamMember member : current.getKey().getTeamMembers().values())
			 {
				 if (isTooLongOffline(member))
				 {
					 killTeamMember(member);
				 }
				 else if (!member.isOnline())
				 {
					 everyoneOnline = false;
				 }
			 }
			 if (everyoneOnline)
			 {
				 for (OfflineRunable runable : current.getValue())
				 {
					 runTeam(runable, current.getKey());
				 }
				 memberIterator.remove();
			 }
		 }
	}
	
	private void killTeamMember(TeamMember member)
	{
		Arena arena = this.plugin.getArenaManager().getArenaOfTeamMember(member.getOfflinePlayer());
		if (arena != null)
		{
			arena.getScore().removeTeamMember(member, arena.getTeam().getTeamOfPlayer(member.getOfflinePlayer()).getTeamName());
			member.setAlive(false);
		}
	}
	
	private void runTeam(OfflineRunable runable, WgTeam team)
	{
		for (TeamMember member : team.getTeamMembers().values())
		{
			runable.run(member);
		}
	}
}