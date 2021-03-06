package de.pro_crafting.wg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import de.pro_crafting.commandframework.CommandArgs;
import de.pro_crafting.commandframework.CommandFramework;
import de.pro_crafting.commandframework.Completer;
import de.pro_crafting.common.scoreboard.ScoreboardManager;
import de.pro_crafting.generator.BlockGenerator;
import de.pro_crafting.kit.KitAPI;
import de.pro_crafting.wg.arena.Arena;
import de.pro_crafting.wg.arena.ArenaManager;
import de.pro_crafting.wg.commands.ArenaCommands;
import de.pro_crafting.wg.commands.TeamCommands;
import de.pro_crafting.wg.commands.WarGearCommands;
import de.pro_crafting.wg.group.PlayerGroupKey;
import de.pro_crafting.wg.group.invite.InviteManager;
import de.pro_crafting.wg.ui.ScoreboardDisplay;

public class WarGear extends JavaPlugin {
	private Repository repo;
	private BlockGenerator generator;
	private ArenaManager arenaManager;
	private KitAPI kitApi;
	private CommandFramework cmdFramework;
	private WgEconomy eco;
	private MetricsLite metrics;
	private Updater updater;
	private WgListener wgListener;
	private File arenaFolder;
	private OfflineManager offlineManager;
	private ScoreboardManager<PlayerGroupKey> scoreboardManager;
	private ScoreboardDisplay scoreboard;
	private InviteManager inviteManager;
	
	@Override
	public void onEnable() {
		this.loadConfig();
		this.repo = new Repository(this);
		this.generator = new BlockGenerator(this, 50000);
		this.arenaManager = new ArenaManager(this);
		this.kitApi = new KitAPI();
		if (this.repo.isEconomyEnabled())
		{
			this.eco = new WgEconomy(this);
		}
		startMetrics();
		startUpdater();
		registerCommands();
		this.wgListener = new WgListener(this);
		this.offlineManager = new OfflineManager(this);
		this.scoreboardManager = new ScoreboardManager<PlayerGroupKey>();
		this.scoreboard = new ScoreboardDisplay(this);
		
		this.inviteManager = new InviteManager(this);
		
		this.getLogger().info("Plugin erfolgreich geladen!");
	}
	
	private void registerCommands() {
		this.cmdFramework = new CommandFramework(this);
		this.cmdFramework.registerCommands(new WarGearCommands(this));
		this.cmdFramework.registerCommands(new TeamCommands(this));
		this.cmdFramework.registerCommands(new ArenaCommands(this));
		this.cmdFramework.registerCommands(this);
		this.cmdFramework.registerHelp();
		
		this.cmdFramework.setInGameOnlyMessage("Der Command muss von einem Spieler ausgeführt werden.");
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this.eco);
		HandlerList.unregisterAll(this.wgListener);
		this.arenaManager.unloadArenas();
		this.getLogger().info("Plugin erfolgreich deaktiviert!");
	}
	
	@Completer (name="wgk")
	public List<String> completeCommands(CommandArgs args) {
		List<String> ret = new ArrayList<String>();
		String label = args.getCommand().getLabel();
		for (String arg : args.getArgs()) {
			label += " " + arg;
		}
		for(String currentLabel : this.cmdFramework.getCommandLabels()) {
			String current = currentLabel.replace('.', ' ');
			if (current.contains(label)) {
				current = current.substring(label.lastIndexOf(' ')).trim();
				current = current.substring(0, current.indexOf(' ') != -1 ? current.indexOf(' ') : current.length()).trim();
				if (!ret.contains(current)) {
					ret.add(current);
				}
			}
		}
		return ret;
	}
	
	private void loadConfig() {
		saveDefaultConfig();
		this.getLogger().info("config.yml geladen.");
		
		arenaFolder = new File(this.getDataFolder(), "arenas/");
		if (!arenaFolder.exists()) {
			this.saveResource("arenas/arena.yml", false);
		}
	}
	
	private void startMetrics() {
		if (repo.areMetricsEnabled()) {
			try {
				metrics = new MetricsLite(this);
				if (metrics.start()) {
					this.getLogger().info("Metrics gestartet!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startUpdater() {
		if (repo.isUpdateCheckEnabled()) {
			updater = new Updater(this, 66631, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
		}
	}
	
	public Repository getRepo() {
		return this.repo;
	}
	
	public BlockGenerator getGenerator() {
		return this.generator;
	}
	
	public ArenaManager getArenaManager() {
		return this.arenaManager;
	}
	
	public KitAPI getKitApi() {
		return this.kitApi;
	}
	
	public CommandFramework GetCmdFramework() {
		return this.cmdFramework;
	}
	
	public Updater getUpdater() {
		return this.updater;
	}
	
	public File getArenaFolder()
	{
		return this.arenaFolder;
	}

	public OfflineManager getOfflineManager() {
		return this.offlineManager;
	}
	
	public ScoreboardManager<PlayerGroupKey> getScoreboardManager() {
		return this.scoreboardManager;
	}
	
	public ScoreboardDisplay getScoreboard() {
		return this.scoreboard;
	}
	
	public InviteManager getInviteManager() {
		return this.inviteManager;
	}
}
