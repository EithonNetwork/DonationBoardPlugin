package se.fredsfursten.donationboardplugin;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.PluginConfig;

public final class DonationBoardPlugin extends JavaPlugin implements Listener {

	private static File donationsStorageFile;
	private static PluginConfig configuration;
	private static String mandatoryWorld;

	@Override
	public void onEnable() {
		if (configuration == null) {
			configuration = new PluginConfig(this, "config.yml");
		} else {
			configuration.load();
		}
		mandatoryWorld = DonationBoardPlugin.getPluginConfig().getString("MandatoryWorld");
		donationsStorageFile = new File(getDataFolder(), "donations.bin");
		getServer().getPluginManager().registerEvents(this, this);		
		BoardController.get().enable(this);
		Commands.get().enable(this);
	}

	public static File getDonationsStorageFile()
	{
		return donationsStorageFile;
	}
	
	@Override
	public void onDisable() {
		BoardController.get().disable();
		Commands.get().disable();
	}

	public static FileConfiguration getPluginConfig()
	{
		return configuration.getFileConfiguration();
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (mandatoryWorld != null) {
			if(!player.getWorld().getName().equalsIgnoreCase(mandatoryWorld)) return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		switch (event.getClickedBlock().getType()) {
		case STONE_BUTTON:
			BoardController.get().initialize(player, event.getClickedBlock());
			break;
		case WOOD_BUTTON:
			BoardController.get().donate(player, event.getClickedBlock());
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player!");
			return false;
		}
		if (args.length < 1) {
			sender.sendMessage("Incomplete command...");
			return false;
		}

		Player player = (Player) sender;

		String command = args[0].toLowerCase();
		if (command.equals("shift")) {
			Commands.get().shiftCommand(player, args);
		} else if (command.equals("print")) {
			Commands.get().printCommand(player, args);
		} else if (command.equals("load")) {
			Commands.get().loadCommand(player, args);
		} else if (command.equals("save")) {
			Commands.get().saveCommand(player, args);
		} else if (command.equals("promote")) {
			Commands.get().promoteCommand(player, args);
		} else if (command.equals("demote")) {
			Commands.get().demoteCommand(player, args);
		} else {
			sender.sendMessage("Could not understand command.");
			return false;
		}
		return true;
	}
}
