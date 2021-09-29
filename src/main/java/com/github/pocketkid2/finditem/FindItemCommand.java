package com.github.pocketkid2.finditem;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.shop.Shop;

import com.github.pocketkid2.finditem.gui.FindItemGUI;

public class FindItemCommand implements CommandExecutor {

	private MVFindItem plugin;

	public FindItemCommand(MVFindItem p) {
		plugin = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// Command must be run by a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by a player!");
			return true;
		}
		Player player = (Player) sender;

		// Must specify an item with at least one argument in the command
		if (args.length < 1) {
			player.sendMessage(plugin.getSettings().getNotEnoughArgsMessage());
			return false;
		}

		// Add all arguments together in case they typed a multiple-word item name
		// without underscores
		final StringBuilder sb = new StringBuilder(args[0]);
		for (int i = 1; i < args.length; i++) {
			sb.append("_").append(args[i]);
		}

		Material mat = null;
		if (plugin.getEssentials() == null) {

			// Search for item type without Essentials (the hard way)
			mat = Material.matchMaterial(sb.toString(), false);
			if (mat == null) {
				mat = Material.matchMaterial(sb.toString(), true);
				if (mat == null) {
					player.sendMessage(plugin.getSettings().getInvalidItemNameMessage(sb.toString()));
					return false;
				}
			}
		} else {
			try {
				mat = plugin.getEssentials().getItemDb().get(sb.toString()).getType();
			} catch (Exception e) {
				player.sendMessage(plugin.getSettings().getInvalidItemNameMessage(sb.toString()));
				return false;
			}
		}

		// Now it's time to start
		List<Shop> shops = QuickShopAPI.getShopAPI().getAllShops();

		// Filter out shops that don't have the item we want
		for (Iterator<Shop> i = shops.iterator(); i.hasNext();) {
			Shop s = i.next();
			if (s.getItem().getType() != mat) {
				i.remove();
			}
		}

		// Check for no shops
		if (shops.size() == 0) {
			player.sendMessage(plugin.getSettings().getNoShopWithItemMessage());
			return true;
		}

		// player.sendMessage("Number of shops with item '" + mat.toString() + "': " +
		// shops.size());
		FindItemGUI.showFirstGUI(plugin, player, shops);

		// player.sendMessage("Displaying GUI");
		return true;
	}

}
