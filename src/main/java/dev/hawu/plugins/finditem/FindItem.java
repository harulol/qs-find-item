package dev.hawu.plugins.finditem;

import com.earth2me.essentials.Essentials;
import com.github.pocketkid2.finditem.BuyOrSellSettings;
import com.github.pocketkid2.finditem.Internationalization;
import com.github.pocketkid2.finditem.PaginationSettings;
import com.github.pocketkid2.finditem.Settings;
import com.github.pocketkid2.finditem.commands.BaseCommand;
import com.github.pocketkid2.finditem.commands.FindCommand;
import com.github.pocketkid2.finditem.gui.GUIManager;
import dev.hawu.plugins.api.Strings;
import dev.hawu.plugins.api.inventories.ClickEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class FindItem extends JavaPlugin {

	private Essentials essentials;
	private Settings settings;

	private PaginationSettings paginationSettings;
	private BuyOrSellSettings buyOrSellSettings;

	@Override
	public void onEnable() {
		if(Bukkit.getPluginManager().getPlugin("QuickShop") == null) {
			Bukkit.getConsoleSender().sendMessage(Strings.color("&cThis plugin is an addon for QuickShop so it must be installed for this to work."));
			return;
		}

		if(Bukkit.getPluginManager().getPlugin("HikariLibrary") == null) {
			Bukkit.getConsoleSender().sendMessage(Strings.color("&cThis plugin was developed against a framework."));
			Bukkit.getConsoleSender().sendMessage(Strings.color("&cCheck &bhttps://github.com/harulol/hikari-library/releases"));
			return;
		}

		new BaseCommand(this);
		Internationalization.onEnable(this);
		settings = new Settings(this);
		paginationSettings = new PaginationSettings(this);
		buyOrSellSettings = new BuyOrSellSettings(this);
		GUIManager.onEnable(this);

		if(getServer().getPluginManager().getPlugin("Essentials") != null) {
			essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
		}

		saveDefaultConfig();
		final FindCommand cmd = new FindCommand(this);
		Objects.requireNonNull(getCommand("finditem")).setExecutor(cmd);
		Objects.requireNonNull(getCommand("finditem")).setTabCompleter(cmd);
	}

	@Override
	public void onDisable() {
		essentials = null;
		settings = null;
		paginationSettings = null;
		buyOrSellSettings = null;
	}

	@Nullable
	public Essentials getEssentials() {
		return essentials;
	}

	@NotNull
	public PaginationSettings getPaginationSettings() {
		return paginationSettings;
	}

	@NotNull
	public BuyOrSellSettings getBuyOrSellSettings() {
		return buyOrSellSettings;
	}

	@NotNull
	public Settings getSettings() {
		return settings;
	}

}
