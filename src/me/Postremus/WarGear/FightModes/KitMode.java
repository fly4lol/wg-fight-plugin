package me.Postremus.WarGear.FightModes;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;

import me.Postremus.WarGear.AdmincmdWrapper;
import me.Postremus.WarGear.Arena;
import me.Postremus.WarGear.IFightMode;
import me.Postremus.WarGear.TeamManager;
import me.Postremus.WarGear.TeamMember;
import me.Postremus.WarGear.TeamNames;
import me.Postremus.WarGear.WarGear;

import java.util.Timer;
import java.util.TimerTask;

public class KitMode implements IFightMode, Listener{

	TeamManager teams;
	WarGear plugin;
	Arena arena;
	Timer timer;
	int counter;
	
	public KitMode(TeamManager teams, WarGear plugin, Arena arena)
	{
		this.teams = teams;
		this.plugin = plugin;
		this.arena = arena;
	}
	
	@Override
	public void start() {
		this.plugin.getServer().broadcastMessage(ChatColor.YELLOW+"Gleich: WarGear-Kampf in der "+this.arena.getArenaName()+" Arena");
		for (TeamMember player : this.teams.getTeamMembers())
		{
			player.getPlayer().getInventory().clear();
			player.getPlayer().getInventory().setArmorContents(null);
		    AdmincmdWrapper.giveKit(this.plugin.getRepo().getKit(), player.getPlayer());
		    
		    AdmincmdWrapper.teleportToWarp(player.getPlayer(), this.plugin.getRepo().getRegionForTeam(player.getTeam(), this.arena), this.plugin.getRepo().getWorldName(this.arena));
		    player.getPlayer().setGameMode(GameMode.SURVIVAL);
			AdmincmdWrapper.disableFly(player.getPlayer());
			AdmincmdWrapper.heal(player.getPlayer());
			for (PotionEffect effect : player.getPlayer().getActivePotionEffects())
			{
				player.getPlayer().removePotionEffect(effect.getType());
			}
		}
		counter = 0;
		timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
	         public void run() {
				finalStartCountdown();          
	         }
		}, 0, 1000);
	}
	
	public void finalStartCountdown()
	{
		if (counter == 0)
		{
			this.arena.broadcastMessage(ChatColor.YELLOW+"Bitte alle Teilnehmer in ihre Wargears");
			this.arena.broadcastMessage(ChatColor.YELLOW+"Fight startet in:");
			this.arena.broadcastMessage(ChatColor.DARK_GREEN + "40 Sekunden");
		}
		else if (counter == 10)
		{
			this.arena.broadcastMessage(ChatColor.DARK_GREEN + "30 Sekunden");
		}
		else if (counter == 20)
		{
			this.arena.broadcastMessage(ChatColor.DARK_GREEN + "20 Sekunden");
		}
		else if (counter == 25)
		{
			this.arena.broadcastMessage(ChatColor.DARK_GREEN + "15 Sekunden");
		}
		else if (counter == 30)
		{
			this.arena.broadcastMessage(ChatColor.DARK_GREEN + "10 Sekunden");
		}
		else if (counter > 30 && 40-counter > 3)
		{
			int diff = 40-counter;
			this.arena.broadcastMessage(ChatColor.DARK_GREEN + "" + diff +" Sekunden");
		}
		else if (counter > 36 && 40-counter > 0)
		{
			int diff = 40-counter;
			this.arena.broadcastMessage(ChatColor.AQUA + ""+ diff +" Sekunden");
		}
		else if (40-counter == 0)
		{
			timer.cancel();
			this.plugin.getServer().getWorld(this.plugin.getRepo().getWorldName(this.arena)).setDifficulty(Difficulty.EASY);
			this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
			this.arena.open();
		}
		counter++;
	}

	@Override
	public void stop() {
		this.plugin.getServer().getWorld(this.plugin.getRepo().getWorldName(this.arena)).setDifficulty(Difficulty.PEACEFUL);
		PlayerMoveEvent.getHandlerList().unregister(this);
	}

	@Override
	public String getName() {
		return "kit";
	}
	
	@EventHandler
	public void playerMoveHandler(PlayerMoveEvent event)
	{
		if (event.getTo().getBlockY() > this.plugin.getRepo().getGroundHeight(this.arena))
		{
			return;
		}
		if (this.teams.isPlayerInTeam(event.getPlayer().getName(), TeamNames.Team1) || this.teams.isPlayerInTeam(event.getPlayer().getName(), TeamNames.Team2))
		{
			event.getPlayer().damage(1);
		}
	}
}
