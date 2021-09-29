package com.github.pocketkid2.finditem;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.maxgamer.quickshop.shop.Shop;

public class Settings {

	//
	// MESSAGES SETTINGS
	//

	private String msg_not_enough_args;
	private String msg_invalid_item_name;
	private String msg_no_shops_with_item;
	private String msg_no_selling_shops_with_item;
	private String msg_no_buying_shops_with_item;

	private boolean msg_teleport_enabled;
	private String msg_teleport_buy;
	private String msg_teleport_sell;

	//
	// BUY/SELL GUI SETTINGS
	//

	private String buy_sell_gui_title;
	private int buy_sell_gui_inv_size;

	private int buy_sell_gui_buy_icon_index;
	private String buy_sell_gui_buy_icon_material;
	private String buy_sell_gui_buy_icon_name;
	private List<String> buy_sell_gui_buy_icon_lores;

	private int buy_sell_gui_sell_icon_index;
	private String buy_sell_gui_sell_icon_material;
	private String buy_sell_gui_sell_icon_name;
	private List<String> buy_sell_gui_sell_icon_lores;

	private String buy_sell_gui_background_material;
	private String buy_sell_gui_background_name;

	//
	// PAGE GUI SETTINGS
	//

	private String page_gui_title;
	private String page_gui_title_check;
	private int page_gui_inv_size;

	private String page_gui_shop_icon_material;
	private String page_gui_shop_icon_name;
	private String page_gui_shop_icon_name_check;
	private List<String> page_gui_shop_icon_lores;

	private int page_gui_next_icon_index;
	private String page_gui_next_icon_material;
	private String page_gui_next_icon_name;
	private List<String> page_gui_next_icon_lores;

	private int page_gui_prev_icon_index;
	private String page_gui_prev_icon_material;
	private String page_gui_prev_icon_name;
	private List<String> page_gui_prev_icon_lores;

	private String page_gui_background_material;
	private String page_gui_background_name;

	private List<IntSegment> page_gui_segments;

	public Settings(MVFindItem plugin) {
		FileConfiguration cfg = plugin.getConfig();

		//
		// MESSAGES OPTIONS
		//

		msg_not_enough_args = cfg.getString("messages.not-enough-args", "Not enough arguments!");
		msg_invalid_item_name = cfg.getString("messages.invalid-item-name", "Invalid item name '%s'");
		msg_no_shops_with_item = cfg.getString("messages.no-shops-with-item", "There aren't any shops with that item!");
		msg_no_selling_shops_with_item = cfg.getString("messages.no-selling-shops-with-item",
				"There are no shops with that item that you can buy from!");
		msg_no_buying_shops_with_item = cfg.getString("messages.no-buying-shops-with-item",
				"There are no shops with that item that you can sell to!");

		msg_teleport_enabled = cfg.getBoolean("messages.teleport-msg-enabled", true);
		msg_teleport_buy = cfg.getString("messages.teleport-msg-buy",
				"Warping to %s shop owned by %s. Price is %,.2f with %d current stock available.");
		msg_teleport_sell = cfg.getString("messages.teleport-msg-sell",
				"Warping to %s shop owned by %s. Price is %,.2f with %d new space available.");

		//
		// READ BUY/SELL GUI OPTIONS
		//

		buy_sell_gui_title = cfg.getString("buy-sell-gui.title", "Are you buying or selling?");
		buy_sell_gui_inv_size = cfg.getInt("buy-sell-gui.total-size", 27);

		buy_sell_gui_buy_icon_index = cfg.getInt("buy-sell-gui.buy-icon.index", 11);
		buy_sell_gui_buy_icon_material = cfg.getString("buy-sell-gui.buy-icon.material", "CHEST");
		buy_sell_gui_buy_icon_name = cfg.getString("buy-sell-gui.buy-icon.name", "Buying");
		buy_sell_gui_buy_icon_lores = cfg.getStringList("buy-sell-gui.buy-icon.lore");

		buy_sell_gui_sell_icon_index = cfg.getInt("buy-sell-gui.sell-icon.index", 15);
		buy_sell_gui_sell_icon_material = cfg.getString("buy-sell-gui.sell-icon.material", "CHEST");
		buy_sell_gui_sell_icon_name = cfg.getString("buy-sell-gui.sell-icon.name", "Selling");
		buy_sell_gui_sell_icon_lores = cfg.getStringList("buy-sell-gui.sell-icon.lore");

		buy_sell_gui_background_material = cfg.getString("buy-sell-gui.background.material",
				"BLACK_STAINED_GLASS_PANE");
		buy_sell_gui_background_name = cfg.getString("buy-sel-gui.background.name", " ");

		//
		// READ PAGE GUI OPTIONS
		//

		page_gui_title = cfg.getString("page-gui.title", "Page %d");
		page_gui_title_check = cfg.getString("page-gui.title-check", "Page ");
		page_gui_inv_size = cfg.getInt("page-gui.total-size", 27);

		page_gui_shop_icon_material = cfg.getString("page-gui.shop-icon.material", "CHEST");
		page_gui_shop_icon_name = cfg.getString("page-gui.shop-icon.name", "%s for %,.2f, %d in stock");
		page_gui_shop_icon_name_check = cfg.getString("page-gui.shop-icon.name-check", " in stock");
		page_gui_shop_icon_lores = cfg.getStringList("page-gui.shop-icon.lore");

		page_gui_next_icon_index = cfg.getInt("page-gui.next-page-icon.index", 25);
		page_gui_next_icon_material = cfg.getString("page-gui.next-page-icon.material", "PAPER");
		page_gui_next_icon_name = cfg.getString("page-gui.next-page-icon.name", "Next page");
		page_gui_next_icon_lores = cfg.getStringList("page-gui.next-page-icon.lore");

		page_gui_prev_icon_index = cfg.getInt("page-gui.prev-page-icon.index", 19);
		page_gui_prev_icon_material = cfg.getString("page-gui.prev-page-icon.material", "PAPER");
		page_gui_prev_icon_name = cfg.getString("page-gui.prev-page-icon.name", "Previous page");
		page_gui_prev_icon_lores = cfg.getStringList("page-gui.prev-page-icon.lore");

		page_gui_background_material = cfg.getString("page-gui.background.material", "BLACK_STAINED_GLASS_PANE");
		page_gui_background_name = cfg.getString("page-gui.background.name", " ");

		page_gui_segments = cfg.getStringList("page-gui.page-segments").stream().map(IntSegment::new)
				.collect(Collectors.toList());

		//
		// MAKE SURE THE NAME CHECKS ARE CORRECT
		//

		if (!page_gui_title.contains(page_gui_title_check)) {
			plugin.getLogger().severe("Config Error: Page GUI title and check don't match!");
			new BukkitRunnable() {

				@Override
				public void run() {
					plugin.getServer().getPluginManager().disablePlugin(plugin);
				}

			}.runTask(plugin);
		}
		if (!page_gui_shop_icon_name.contains(page_gui_shop_icon_name_check)) {
			plugin.getLogger().severe("Config Error: Page GUI Shop Icon title and check don't match!");
			new BukkitRunnable() {

				@Override
				public void run() {
					plugin.getServer().getPluginManager().disablePlugin(plugin);
				}

			}.runTask(plugin);
		}
	}

	//
	// GETTERS FOR MESSAGES
	//

	public String getNotEnoughArgsMessage() {
		return msg_not_enough_args;
	}

	public String getInvalidItemNameMessage(String s) {
		return String.format(msg_invalid_item_name, s);
	}

	public String getNoShopWithItemMessage() {
		return msg_no_shops_with_item;
	}

	public String getNoSellingShopWithItemMessage() {
		return msg_no_selling_shops_with_item;
	}

	public String getNoBuyingShopWithItemMessage() {
		return msg_no_buying_shops_with_item;
	}

	public boolean isTeleportMessageEnabled() {
		return msg_teleport_enabled;
	}

	public String getBuyShopTeleportMessage(Shop shop) {
		return format(msg_teleport_buy, shop);
	}

	public String getSellShopTeleportMessage(Shop shop) {
		return format(msg_teleport_sell, shop);
	}

	//
	// GETTERS FOR BUY/SELL GUI
	//

	public String getBuySellGUITitle() {
		return buy_sell_gui_title;
	}

	public int getBuySellGUIInvSize() {
		return buy_sell_gui_inv_size;
	}

	public int getBuySellGUIBuyIconIndex() {
		return buy_sell_gui_buy_icon_index;
	}

	public Material getBuySellGUIBuyIconMaterail() {
		return Material.matchMaterial(buy_sell_gui_buy_icon_material);
	}

	public String getBuySellGUIBuyIconName() {
		return buy_sell_gui_buy_icon_name;
	}

	public List<String> getBuySellGUIBuyIconLores() {
		return buy_sell_gui_buy_icon_lores;
	}

	public int getBuySellGUISellIconIndex() {
		return buy_sell_gui_sell_icon_index;
	}

	public Material getBuySellGUISellIconMaterial() {
		return Material.matchMaterial(buy_sell_gui_sell_icon_material);
	}

	public String getBuySellGUISellIconName() {
		return buy_sell_gui_sell_icon_name;
	}

	public List<String> getBuySellGUISellIconLores() {
		return buy_sell_gui_sell_icon_lores;
	}

	public Material getBuySellGUIBackgroundMaterial() {
		return Material.matchMaterial(buy_sell_gui_background_material);
	}

	public String getBuySellGUIBackgroundName() {
		return buy_sell_gui_background_name;
	}

	//
	// GETTERS FOR PAGE GUI
	//

	public String getPageGUITitle(int number) {
		return String.format(page_gui_title, number);
	}

	public String getPageGUITitleCheck() {
		return page_gui_title_check;
	}

	public int getPageGUIInvSize() {
		return page_gui_inv_size;
	}

	public Material getPageGUIShopIconMaterial() {
		return Material.matchMaterial(page_gui_shop_icon_material);
	}

	public String getPageGUIShopIconName(Shop shop) {
		return format(page_gui_shop_icon_name, shop);
	}

	public String getPageGUIShopIconNameCheck() {
		return page_gui_shop_icon_name_check;
	}

	public List<String> getPageGUIShopIconLores(Shop shop) {
		return page_gui_shop_icon_lores.stream().map(n -> format(n, shop)).collect(Collectors.toList());
	}

	public int getPageGUINextIconIndex() {
		return page_gui_next_icon_index;
	}

	public Material getPageGUINextIconMaterial() {
		return Material.matchMaterial(page_gui_next_icon_material);
	}

	public String getPageGUINextIconName() {
		return page_gui_next_icon_name;
	}

	public List<String> getPageGUINextIconLores() {
		return page_gui_next_icon_lores;
	}

	public int getPageGUIPrevIconIndex() {
		return page_gui_prev_icon_index;
	}

	public Material getPageGUIPrevIconMaterial() {
		return Material.matchMaterial(page_gui_prev_icon_material);
	}

	public String getPageGUIPrevIconName() {
		return page_gui_prev_icon_name;
	}

	public List<String> getPageGUIPrevIconLores() {
		return page_gui_prev_icon_lores;
	}

	public Material getPageGUIBackgroundMaterial() {
		return Material.matchMaterial(page_gui_background_material);
	}

	public String getPageGUIBackgroundName() {
		return page_gui_background_name;
	}

	public List<IntSegment> getPageGUISegments() {
		return page_gui_segments;
	}

	//
	// UTILITY FUNCTIONS
	//

	private String format(String s, Shop shop) {
		s = s.replaceAll("<ITEM>", shop.getItem().getType().toString());
		s = s.replaceAll("<OWNER>", shop.ownerName());
		s = s.replaceAll("<STOCK>", Integer.toString(shop.getRemainingStock()));
		s = s.replaceAll("<SPACE>", Integer.toString(shop.getRemainingSpace()));
		s = s.replaceAll("<PRICE>", "%,.2f");
		if (s.contains("%,.2f")) {
			s = String.format(s, shop.getPrice());
		}
		return s;
	}
}
