package com.github.pocketkid2.finditem.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.maxgamer.quickshop.shop.Shop;

import com.github.pocketkid2.finditem.MVFindItem;
import com.github.pocketkid2.finditem.IntSegment;
import com.github.pocketkid2.finditem.Settings;

public class FindItemGUI {

	private static ItemStack rename(ItemStack is, String name) {
		return renameWithLoresAndOwner(is, name, null, null);
	}

	private static ItemStack renameWithLores(ItemStack is, String name, List<String> lores) {
		return renameWithLoresAndOwner(is, name, lores, null);
	}

	private static ItemStack renameWithLoresAndOwner(ItemStack is, String name, List<String> lores,
			OfflinePlayer owner) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lores);
		is.setItemMeta(im);
		if (im instanceof SkullMeta && owner != null) {
			SkullMeta sm = (SkullMeta) im;
			sm.setOwningPlayer(owner);
			is.setItemMeta(sm);
		}
		return is;
	}

	public static void showFirstGUI(MVFindItem plugin, Player player, List<Shop> shops) {
		Settings s = plugin.getSettings();

		Inventory inv = Bukkit.createInventory(player, s.getBuySellGUIInvSize(), s.getBuySellGUITitle());
		ItemStack[] contents = inv.getContents();

		for (int i = 0; i < contents.length; i++) {
			if (i == s.getBuySellGUIBuyIconIndex()) {
				// Buy icon
				contents[i] = renameWithLores(new ItemStack(s.getBuySellGUIBuyIconMaterail()),
						s.getBuySellGUIBuyIconName(), s.getBuySellGUIBuyIconLores());
			} else if (i == s.getBuySellGUISellIconIndex()) {
				// Sell icon
				contents[i] = renameWithLores(new ItemStack(s.getBuySellGUISellIconMaterial()),
						s.getBuySellGUISellIconName(), s.getBuySellGUISellIconLores());
			} else {
				// Background icon
				contents[i] = rename(new ItemStack(s.getBuySellGUIBackgroundMaterial()),
						s.getBuySellGUIBackgroundName());
			}
		}

		inv.setContents(contents);

		player.closeInventory();
		player.openInventory(inv);

		plugin.setShopsForPlayer(player, shops);
	}

	public static void showPage(MVFindItem plugin, Player player, List<Shop> shops, int number) {
		Settings s = plugin.getSettings();

		Inventory inv = Bukkit.createInventory(player, s.getPageGUIInvSize(), s.getPageGUITitle(number));
		ItemStack[] contents = inv.getContents();

		for (int i = 0; i < contents.length; i++) {
			final int x = i;
			if (i == s.getPageGUINextIconIndex() && !isLastPage(s.getPageGUISegments(), shops.size(), number)) {
				// Next icon
				contents[i] = renameWithLores(new ItemStack(s.getPageGUINextIconMaterial()), s.getPageGUINextIconName(),
						s.getPageGUINextIconLores());
				continue;
			}
			if (i == s.getPageGUIPrevIconIndex() && number > 1) {
				// Prev icon
				contents[i] = renameWithLores(new ItemStack(s.getPageGUIPrevIconMaterial()), s.getPageGUIPrevIconName(),
						s.getPageGUIPrevIconLores());
				continue;
			}
			if (s.getPageGUISegments().stream().anyMatch(n -> n.contains(x))) {
				// Shop icon

				// Get which shop index we should grab for this inventory slot
				int index = getShopListIndex(s.getPageGUIInvSize(), number, i, s.getPageGUISegments());

				if (shops.size() > index) {
					Shop shop = shops.get(index);
					contents[i] = renameWithLoresAndOwner(new ItemStack(s.getPageGUIShopIconMaterial()),
							s.getPageGUIShopIconName(shop), s.getPageGUIShopIconLores(shop),
							Bukkit.getOfflinePlayer(shop.getOwner()));
					continue;
				}
			}
			// Background icon
			contents[i] = rename(new ItemStack(s.getPageGUIBackgroundMaterial()), s.getPageGUIBackgroundName());
		}

		inv.setContents(contents);

		player.closeInventory();
		player.openInventory(inv);

		plugin.setShopsForPlayer(player, shops);
		plugin.setPageForPlayer(player, number);
	}

	// Given where we are in creating the GUI page, what shop do we need to grab
	// from the list?
	private static int getShopListIndex(int size, int page, int curr, List<IntSegment> segments) {
		int x = 0;
		for (int i = 0; curr >= 0; i++, curr--) {
			final int a = i;
			if (segments.stream().anyMatch(n -> n.contains(a))) {
				x++;
			}
		}

		return x + IntSegment.listSum(segments) * (page - 1) - 1;
	}

	private static boolean isLastPage(List<IntSegment> segments, int listSize, int currPage) {
		return currPage >= listSize / IntSegment.listSum(segments);
	}

	public static Shop getShop(MVFindItem plugin, List<Shop> shops, int slot, int page) {
		return shops.get(getShopListIndex(plugin.getSettings().getPageGUIInvSize(), page, slot,
				plugin.getSettings().getPageGUISegments()));
	}

}
