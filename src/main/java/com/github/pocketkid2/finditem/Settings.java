package com.github.pocketkid2.finditem;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.util.Objects;

public final class Settings {

    private boolean filtersShop;
    private boolean filtersSelf;
    private final JavaPlugin plugin;

    public Settings(final JavaPlugin plugin) {
        reload();
        this.plugin = plugin;
    }

    public void reload() {
        final FileConfiguration config = plugin.getConfig();
        final FileConfiguration defaultFile = YamlConfiguration.loadConfiguration(new InputStreamReader(
            Objects.requireNonNull(plugin.getResource("config.yml"))));

        filtersShop = config.getBoolean("filters-shop", defaultFile.getBoolean("filters-shop"));
        filtersSelf = config.getBoolean("filters-self", defaultFile.getBoolean("filters-self"));
    }

    public boolean filtersShop() {
        return filtersShop;
    }

    public boolean filtersSelf() {
        return filtersSelf;
    }

}
