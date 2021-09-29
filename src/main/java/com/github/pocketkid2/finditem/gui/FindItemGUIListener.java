package com.github.pocketkid2.finditem.gui;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.maxgamer.quickshop.shop.Shop;
import org.maxgamer.quickshop.shop.ShopType;

import com.github.pocketkid2.finditem.MVFindItem;
import com.github.pocketkid2.finditem.Settings;

public class FindItemGUIListener implements Listener {

	private MVFindItem plugin;

	public FindItemGUIListener(MVFindItem p) {
		plugin = p;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onClick(InventoryClickEvent event) {
		Settings s = plugin.getSettings();
		String title = event.getView().getTitle();
		if (title.equals(s.getBuySellGUITitle())) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				String name = event.getCurrentItem().getItemMeta().getDisplayName();
				Player player = (Player) event.getWhoClicked();
				if (name.equals(s.getBuySellGUIBuyIconName())) {
					// Do buy
					new BukkitRunnable() {

						@Override
						public void run() {
							List<Shop> shops = plugin.getBuyShopsForPlayer(player);
							if (shops.size() == 0) {
								player.sendMessage(s.getNoSellingShopWithItemMessage());
								player.closeInventory();
								plugin.clearPlayerFromSystem(player);
							} else {
								shops.sort((Shop o1, Shop o2) -> (int) (o1.getPrice() * 100.0D)
										- (int) (o2.getPrice() * 100.0D));
								FindItemGUI.showPage(plugin, player, shops, 1);
							}
						}

					}.runTask(plugin);
				} else if (name.equals(s.getBuySellGUISellIconName())) {
					// Do sell
					new BukkitRunnable() {

						@Override
						public void run() {
							List<Shop> shops = plugin.getSellShopsForPlayer(player);
							if (shops.size() == 0) {
								player.sendMessage(s.getNoBuyingShopWithItemMessage());
								player.closeInventory();
								plugin.clearPlayerFromSystem(player);
							} else {
								// Sort shops by distance to player using lambda comparator expression
								shops.sort((Shop o1, Shop o2) -> (int) (o2.getPrice() * 100.0D)
										- (int) (o1.getPrice() * 100.0D));
								FindItemGUI.showPage(plugin, player, shops, 1);
							}
						}

					}.runTask(plugin);
				}
			}
		} else if (title.contains(s.getPageGUITitleCheck())) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				String name = event.getCurrentItem().getItemMeta().getDisplayName();
				Player player = (Player) event.getWhoClicked();
				if (name.equals(s.getPageGUINextIconName())) {
					// Do next
					new BukkitRunnable() {

						@Override
						public void run() {
							FindItemGUI.showPage(plugin, player, plugin.getAllShopsForPlayer(player),
									plugin.getCurrentPage(player) + 1);
						}

					}.runTask(plugin);
				} else if (name.equals(s.getPageGUIPrevIconName())) {
					// Do prev
					new BukkitRunnable() {

						@Override
						public void run() {
							FindItemGUI.showPage(plugin, player, plugin.getAllShopsForPlayer(player),
									plugin.getCurrentPage(player) - 1);
						}

					}.runTask(plugin);
				} else if (name.contains(s.getPageGUIShopIconNameCheck())) {

					// Do shop
					new BukkitRunnable() {

						@Override
						public void run() {
							Shop shop = FindItemGUI.getShop(plugin, plugin.getAllShopsForPlayer(player),
									event.getSlot(), plugin.getCurrentPage(player));

							teleportFacingSign(player, shop.getSigns().get(0));

							if (s.isTeleportMessageEnabled()) {
								if (shop.getShopType() == ShopType.BUYING) {
									player.sendMessage(s.getSellShopTeleportMessage(shop));
								} else if (shop.getShopType() == ShopType.SELLING) {
									player.sendMessage(s.getBuyShopTeleportMessage(shop));
								}
							}

							player.closeInventory();
							plugin.clearPlayerFromSystem(player);
						}

					}.runTask(plugin);
				}
			}
		}
	}

	private void teleportFacingSign(Player player, Sign sign) {
		Location loc = sign.getLocation();

		loc.add(0.5, 0, 0.5);
		loc.setPitch(45);

		WallSign ws = (WallSign) sign.getBlockData();

		switch (ws.getFacing()) {

		case EAST:
			loc.setYaw(90);
			loc.add(1.0, 0, 0);
			break;

		case NORTH:
			loc.setYaw(0);
			loc.add(0, 0, -1.0);
			break;

		case SOUTH:
			loc.setYaw(180);
			loc.add(0, 0, 1.0);
			break;

		case WEST:
			loc.setYaw(270);
			loc.add(-1.0, 0, 0);
			break;

		default:
			plugin.getLogger().severe("Sign does not face N/S/E/W");
			break;

		}

		player.teleport(loc);
	}
}
