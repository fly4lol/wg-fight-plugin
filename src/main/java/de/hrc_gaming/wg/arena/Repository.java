package de.hrc_gaming.wg.arena;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.hrc_gaming.wg.Util;
import de.hrc_gaming.wg.WarGear;

public class Repository 
{
	private WarGear plugin;
	private File arenaConfig;
	private YamlConfiguration config;
	
	private String world;
	private ProtectedRegion arenaRegion;
	private String mode;
	private int groundHeight;
	private String groundSchematic;
	private boolean autoReset;
	private ProtectedRegion team1Region;
	private ProtectedRegion team2Region;
	private Location team1Warp;
	private Location team2Warp;
	private Location fightEndWarp;
	private boolean waterRemove;
	private int groundDamage;
	private boolean isScoreboardEnabled;
	private int scoreboardTime;
	private boolean isSpectatorModeEnabled;
	private int spectatorModeTime;
	
	private String worldPath;
	private String arenaRegionPath;
	private String modePath;
	private String groundHeightPath;
	private String groundSchematicPath;
	private String autoResetPath;
	private String team1RegionPath;
	private String team2RegionPath;
	private String team1Path;
	private String team2Path;
	private String fightEndPath;
	private String waterRemovePath;
	private String groundDamagePath;
	private String scoreboardEnabledPath;
	private String scoreboardTimePath;
	private String spectatorModeEnabledPath;
	private String spectatorModeTimePath;
	
	public Repository(WarGear plugin, Arena arena)
	{
		this.plugin = plugin;
		this.arenaConfig = new File(this.plugin.getArenaFolder(), arena.getName()+".yml");
		
		worldPath = "world";
		modePath = "mode";
		groundHeightPath = "ground.height";
		groundSchematicPath = "ground.schematic";
		groundDamagePath = "ground.damage";
		autoResetPath = "auto-reset";
		waterRemovePath = "water-remove";
		team1RegionPath = "regions.team1";
		team2RegionPath = "regions.team2";
		arenaRegionPath = "regions.arena";
		team1Path = "fightStart.team1";
		team2Path = "fightStart.team2";
		fightEndPath = "fightEnd";
		scoreboardEnabledPath = "scoreboard.enabled";
		scoreboardTimePath = "scoreboard.time";
		spectatorModeEnabledPath = "spectator-mode.enabled";
		spectatorModeTimePath = "spectator-mode.time";
	}
	
	public boolean load()
	{
		if (!this.arenaConfig.exists()) return false;
		this.config = YamlConfiguration.loadConfiguration(this.arenaConfig);
		
		if (!this.loadWorld()) return false;
		if (!this.loadArenaRegion()) return false;
		if (!this.loadMode()) return false;
		if (!this.loadGroundHeight()) return false;
		if (!this.loadGroundSchematic()) return false;
		if (!this.loadAutoReset()) return false;
		if (!this.loadTeam1Region()) return false;
		if (!this.loadTeam2Region()) return false;
		if (!this.loadTeam1Warp()) return false;
		if (!this.loadTeam2Warp()) return false;
		if (!this.loadFightEndWarp()) return false;
		if (!this.loadGroundDamage()) return false;
		if (!this.loadWaterRemove()) return false;
		if (!this.loadScoreboardEnabled()) return false;
		if (!this.loadScoreboardTime()) return false;
		if (!this.loadSpectatorModeEnabled()) return false;
		if (!this.loadSpectatorModeTime()) return false;
		
		this.team1Warp = Util.lookAt(this.team1Warp, this.team2Warp);
		this.team2Warp = Util.lookAt(this.team2Warp, this.team1Warp);
		return true;
	}
	
	private boolean loadWorld()
	{
		String worldName = this.config.getString(this.worldPath);
		if (!this.existsWorld(worldName))
		{
			return false;
		}
		this.world = worldName;
		return true;
	}
	
	private boolean loadArenaRegion()
	{
		String id = this.config.getString(this.arenaRegionPath);
		this.arenaRegion = this.getWorldGuardRegion(id);
		return this.arenaRegion != null;
	}
	
	private boolean loadMode()
	{
		this.mode = this.config.getString(modePath, "kit");
		return true;
	}
	
	private boolean loadGroundHeight()
	{
		int groundHeight = this.config.getInt(groundHeightPath, -1);
		if (groundHeight < 0 || groundHeight > this.getWorld().getMaxHeight())
		{
			return false;
		}
		this.groundHeight = groundHeight;
		return true;
	}
	
	private boolean loadGroundSchematic()
	{
		this.groundSchematic = this.config.getString(groundSchematicPath);
		return true;
	}
	
	private boolean loadAutoReset()
	{
		this.autoReset = this.config.getBoolean(autoResetPath, true);
		return true;
	}
	
	private boolean loadTeam1Region()
	{
		String id = this.config.getString(this.team1RegionPath);
		this.team1Region = this.getWorldGuardRegion(id);
		return this.team1Region != null;
	}
	
	private boolean loadTeam2Region()
	{
		String id = this.config.getString(this.team2RegionPath);
		this.team2Region = this.getWorldGuardRegion(id);
		return this.team2Region != null;
	}
	
	private boolean loadTeam1Warp()
	{
		this.team1Warp = this.loadLocation(this.team1Path, getWorld());
		return this.team1Warp != null;
	}
	
	private boolean loadTeam2Warp()
	{
		this.team2Warp = this.loadLocation(this.team2Path, getWorld());
		return this.team2Warp != null;
	}
	
	private boolean loadFightEndWarp()
	{
		this.fightEndWarp = this.loadLocation(this.fightEndPath, getWorld());
		return this.fightEndWarp != null;
	}
	
	private boolean loadGroundDamage()
	{
		this.groundDamage = this.config.getInt(groundDamagePath, 4);
		return true;
	}
	
	private boolean loadWaterRemove()
	{
		this.waterRemove = this.config.getBoolean(waterRemovePath, true);
		return true;
	}
	
	private boolean loadScoreboardEnabled()
	{
		this.isScoreboardEnabled = this.config.getBoolean(scoreboardEnabledPath, true);
		return true;
	}
	
	private boolean loadScoreboardTime()
	{
		this.scoreboardTime = this.config.getInt(scoreboardTimePath, 30);
		return true;
	}
	
	private boolean loadSpectatorModeEnabled()
	{
		this.isSpectatorModeEnabled = this.config.getBoolean(spectatorModeEnabledPath, false);
		return true;
	}
	
	private boolean loadSpectatorModeTime()
	{
		this.spectatorModeTime = this.config.getInt(spectatorModeTimePath, 120);
		return true;
	}
	
	private Location loadLocation(String node, World world)
	{
		String location = this.config.getString(node, "");
		String[] splited = location.split(";");
		if (splited.length != 3)
		{
			return null;
		}
		try
		{
			return new Location(world, Double.parseDouble(splited[0]), Double.parseDouble(splited[1]), Double.parseDouble(splited[2]));
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	private boolean existsWorld(String name)
	{
		if (name == null) return false;
		return this.plugin.getServer().getWorld(name) != null;
	}
	
	private ProtectedRegion getWorldGuardRegion(String id)
	{
		RegionManager rm = this.plugin.getRepo().getWorldGuard().getRegionManager(this.getWorld());
		return rm.getRegion(id);
	}
	
	public boolean save()
	{
		return false;
	}
	
	public World getWorld()
	{
		return this.plugin.getServer().getWorld(world);
	}
	
	public void setWorld(String name)
	{
		if (this.existsWorld(name))
		{
			this.world = name;
		}
	}
	
	public ProtectedRegion getArenaRegion()
	{
		return this.arenaRegion;
	}
	
	public void setArenaRegion(String id)
	{
		RegionManager rm = this.plugin.getRepo().getWorldGuard().getRegionManager(this.getWorld());
		ProtectedRegion rg = rm.getRegion(id);
		if (rg != null)
		{
			this.arenaRegion = rg;
		}
	}

	public String getFightMode()
	{
		return this.mode;
	}
	
	public void setFightMode(String modeName)
	{
		this.mode = modeName;
	}
	
	public int getGroundHeight()
	{
		return this.groundHeight;
	}
	
	public void setGroundHeight(int height)
	{
		if (height >= 0 && height <= this.getWorld().getMaxHeight())
		{
			this.groundHeight = height;
		}
	}
	
	public String getGroundSchematic()
	{
		return this.groundSchematic;
	}
	
	public void setGroundSchematic(String groundSchematic)
	{
		this.groundSchematic = groundSchematic;
	}
	
	public boolean getAutoReset()
	{
		return this.autoReset;
	}
	
	public void setAutoReset(boolean autoReset)
	{
		this.autoReset = autoReset;
	}
	
	public ProtectedRegion getTeam1Region()
	{
		return this.team1Region;
	}
	
	public void setTeam1Region(String id)
	{
		RegionManager rm = this.plugin.getRepo().getWorldGuard().getRegionManager(this.getWorld());
		ProtectedRegion rg = rm.getRegion(id);
		if (rg != null)
		{
			this.team1Region = rg;
		}
	}
	
	public ProtectedRegion getTeam2Region()
	{
		return this.team2Region;
	}
	
	public void setTeam2Region(String id)
	{
		RegionManager rm = this.plugin.getRepo().getWorldGuard().getRegionManager(this.getWorld());
		ProtectedRegion rg = rm.getRegion(id);
		if (rg != null)
		{
			this.team2Region = rg;
		}
	}
	
	public Location getTeam1Warp()
	{
		return this.team1Warp;
	}
	
	public void setTeam1Warp(Location warp)
	{
		this.team1Warp = warp;
	}
	
	public Location getTeam2Warp()
	{
		return this.team2Warp;
	}
	
	public void setTeam2Warp(Location warp)
	{
		this.team2Warp = warp;
	}
	
	public Location getFightEndWarp()
	{
		return this.fightEndWarp;
	}
	
	public void setFightEndWarp(Location warp)
	{
		this.fightEndWarp = warp;
	}

	public boolean isWaterRemove() {
		return waterRemove;
	}

	public void setWaterRemove(boolean waterRemove) {
		this.waterRemove = waterRemove;
	}

	public int getGroundDamage() {
		return groundDamage;
	}

	public void setGroundDamage(int groundDamage) {
		this.groundDamage = groundDamage;
	}

	public boolean isScoreboardEnabled() {
		return this.isScoreboardEnabled;
	}

	public void setScoreboardEnabled(boolean isScoreboardEnabled) {
		this.isScoreboardEnabled = isScoreboardEnabled;
	}

	public int getScoreboardTime() {
		return this.scoreboardTime;
	}

	public void setScoreboardTime(int scoreboardTime) {
		this.scoreboardTime = scoreboardTime;
	}

	public boolean isSpectatorModeEnabled() {
		return this.isSpectatorModeEnabled;
	}

	public void setSpectatorModeEnabled(boolean isSpectatorModeEnabled) {
		this.isSpectatorModeEnabled = isSpectatorModeEnabled;
	}

	public int getSpectatorModeTime() {
		return this.spectatorModeTime;
	}

	public void setSpectatorModeTime(int spectatorModeTime) {
		this.spectatorModeTime = spectatorModeTime;
	}
	
}
