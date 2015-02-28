package se.fredsfursten.donationboardplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands {
	private static Commands singleton = null;
	private static final String SHIFT_COMMAND = "/donationboard shift";
	private static final String PRINT_COMMAND = "/donationboard print";
	private static final String LOAD_COMMAND = "/donationboard load";
	private static final String SAVE_COMMAND = "/donationboard save";
	private static final String REGISTER_COMMAND = "/donationboard register <player>";
	private static final String DONATE_COMMAND = "/donationboard donate <player> <E-tokens>";

	private JavaPlugin _plugin = null;

	private Commands() {
	}

	static Commands get()
	{
		if (singleton == null) {
			singleton = new Commands();
		}
		return singleton;
	}

	void enable(JavaPlugin plugin){
		this._plugin = plugin;
	}

	void disable() {
	}

	void shiftCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "donationboard.shift")) return;
		if (!arrayLengthIsWithinInterval(args, 1, 1)) {
			player.sendMessage(SHIFT_COMMAND);
			return;
		}

		BoardController.get().shiftLeft();
	}

	void printCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "donationboard.print")) return;
		if (!arrayLengthIsWithinInterval(args, 1, 1)) {
			player.sendMessage(PRINT_COMMAND);
			return;
		}

		BoardController.get().print(player);
	}

	void loadCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "donationboard.load")) return;
		if (!arrayLengthIsWithinInterval(args, 1, 1)) {
			player.sendMessage(LOAD_COMMAND);
			return;
		}

		BoardController.get().loadNow();
	}

	void saveCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "donationboard.save")) return;
		if (!arrayLengthIsWithinInterval(args, 1, 1)) {
			player.sendMessage(SAVE_COMMAND);
			return;
		}

		BoardController.get().saveNow();
	}

	@SuppressWarnings("deprecation")
	void registerCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "donationboard.register")) return;
		if (!arrayLengthIsWithinInterval(args, 1, 2)) {
			player.sendMessage(REGISTER_COMMAND);
			return;
		}

		Player registerPlayer = player;
		if (args.length > 1) {
			registerPlayer = Bukkit.getPlayer(args[1]);
		}

		BoardController.get().register(registerPlayer);
	}

	@SuppressWarnings("deprecation")
	public void donateCommand(CommandSender sender, String[] args)
	{
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!verifyPermission(player, "donationboard.donate")) return;
		}
		if (!arrayLengthIsWithinInterval(args, 3, 3)) {
			sender.sendMessage(DONATE_COMMAND);
			return;
		}

		Player donatePlayer  = Bukkit.getPlayer(args[1]);
		if (donatePlayer == null) {
			sender.sendMessage(String.format("Unknown player: %s", args[1]));
			return;
		}

		int tokens = 0;
		try {
			tokens = Integer.parseUnsignedInt(args[2]);
		} catch (Exception e) {
			sender.sendMessage(String.format("Number of tokens could not be understood: %s", args[2]));
			return;
		}

		BoardController.get().donate(donatePlayer, tokens);
	}


	private boolean verifyPermission(Player player, String permission)
	{
		if (player.hasPermission(permission)) return true;
		player.sendMessage("You must have permission " + permission);
		return false;
	}

	private boolean arrayLengthIsWithinInterval(Object[] args, int min, int max) {
		return (args.length >= min) && (args.length <= max);
	}
}
