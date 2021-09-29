package com.github.pocketkid2.finditem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.quickshop.shop.Shop;
import org.maxgamer.quickshop.shop.ShopType;

import com.earth2me.essentials.Essentials;
import com.github.pocketkid2.finditem.gui.FindItemGUIListener;

public class MVFindItem extends JavaPlugin {

	private Settings settings;

	private Essentials essentials;

	private Map<Player, List<Shop>> openShopList;
	private Map<Player, Integer> openPages;

	@Override
	public void onEnable() {
		openPages = new HashMap<Player, Integer>();
		openShopList = new HashMap<Player, List<Shop>>();

		saveDefaultConfig();
		settings = new Settings(this);

		getCommand("finditem").setExecutor(new FindItemCommand(this));
		getCommand("finditem").setTabCompleter(new FindItemTabCompleter(this));

		getServer().getPluginManager().registerEvents(new FindItemGUIListener(this), this);

		if (getServer().getPluginManager().getPlugin("Essentials") != null) {
			essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
			getLogger().info("Found " + essentials.getDescription().getFullName() + ", using");
		} else {
			getLogger().info("Did not find Essentials");
		}
		getLogger().info("Enabled!");
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabled!");
	}

	public Settings getSettings() {
		return settings;
	}

	public Essentials getEssentials() {
		return essentials;
	}

	public List<Shop> getAllShopsForPlayer(Player player) {
		return openShopList.get(player);
	}

	public List<Shop> getBuyShopsForPlayer(Player player) {
		if (openShopList.containsKey(player))
			// Continue
			return openShopList.get(player).stream().filter(x -> x.getShopType() == ShopType.SELLING)
					.collect(Collectors.toList());
		getLogger().severe("getBuyShopsForPlayer() was called but full shop list does not exist!");
		return null;
	}

	public List<Shop> getSellShopsForPlayer(Player player) {
		if (openShopList.containsKey(player))
			// Continue
			return openShopList.get(player).stream().filter(x -> x.getShopType() == ShopType.BUYING)
					.collect(Collectors.toList());
		getLogger().severe("getSellShopsForPlayer() was called but full shop list does not exist!");
		return null;
	}

	public void setShopsForPlayer(Player player, List<Shop> shops) {
		openShopList.put(player, shops);
	}

	public void setPageForPlayer(Player player, int number) {
		openPages.put(player, number);
	}

	public int getCurrentPage(Player player) {
		return openPages.get(player);
	}

	public void clearPlayerFromSystem(Player player) {
		openPages.remove(player);
		openShopList.remove(player);
	}
}
