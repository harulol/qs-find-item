package com.github.pocketkid2.finditem;

import dev.hawu.plugins.api.inventories.Button;
import dev.hawu.plugins.api.inventories.InventorySize;
import dev.hawu.plugins.api.inventories.Widget;
import dev.hawu.plugins.api.inventories.item.ItemStackBuilder;
import dev.hawu.plugins.api.inventories.style.StaticStyle;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Objects;

public final class BuyOrSellSettings {

    private final JavaPlugin plugin;

    private String title;
    private InventorySize size;

    private ItemStack buyIcon;
    private int buyIndex;
    private ItemStack sellIcon;
    private int sellIndex;

    private ItemStack backgroundIcon;

    public BuyOrSellSettings(final JavaPlugin plugin) {
        load(false);
        this.plugin = plugin;
    }

    public void load(final boolean forcefully) {
        plugin.saveResource("buy-sell.yml", forcefully);
        final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "buy-sell.yml"));
        final FileConfiguration defaultFile = YamlConfiguration.loadConfiguration(new InputStreamReader(
            Objects.requireNonNull(plugin.getResource("buy-sell.yml"))));

        title = config.getString("title", defaultFile.getString("title"));
        size = InventorySize.values()[new IntSegment(0, 5).coerceBetween(config.getInt("rows", defaultFile.getInt("rows")) - 1)];
        buyIndex = config.getInt("buy-icon.index", defaultFile.getInt("buy-icon.index"));
        sellIndex = config.getInt("sell-icon.index", defaultFile.getInt("sell-icon.index"));

        final Material buyMaterial = Material.matchMaterial(Objects.requireNonNull(config.getString("buy-icon.material",
            defaultFile.getString("buy-icon.material"))));
        assert buyMaterial != null;
        buyIcon = new ItemStackBuilder().material(buyMaterial).withMeta()
            .displayName(config.getString("buy-icon.display-name", defaultFile.getString("buy-icon.display-name"))).withLore()
            .add(config.getStringList("buy-icon.lore").toArray(new String[0]))
            .build().build().build();

        final Material sellMaterial = Material.matchMaterial(Objects.requireNonNull(config.getString("sell-icon.material",
            defaultFile.getString("sell-icon.material"))));
        assert sellMaterial != null;
        sellIcon = new ItemStackBuilder().material(sellMaterial).withMeta()
            .displayName(config.getString("sell-icon.display-name", defaultFile.getString("sell-icon.display-name"))).withLore()
            .add(config.getStringList("sell-icon.lore").toArray(new String[0]))
            .build().build().build();

        final Material backgroundMaterial = Material.matchMaterial(Objects.requireNonNull(config.getString("background.material",
            defaultFile.getString("background.material"))));
        assert backgroundMaterial != null;
        if(backgroundMaterial == Material.AIR) {
            backgroundIcon = new ItemStack(Material.AIR);
            return;
        }
        backgroundIcon = new ItemStackBuilder().material(backgroundMaterial).withMeta()
            .displayName(config.getString("background.display-name", defaultFile.getString("background.display-name"))).withLore()
            .add(config.getStringList("background.lore").toArray(new String[0]))
            .build().build().build();
    }

    @NotNull
    public Widget createBuyOrSellWidget(final boolean firstTime) {
        try {
            final Widget widget = new Widget(size, title);
            for(int i = 0; i < widget.getInventory().getSize(); i++) {
                final Button<StaticStyle> button = new Button<>(new StaticStyle(backgroundIcon));
                button.setHandler(e -> e.setCancelled(true));
                widget.put(i, button);
            }

            final Button<StaticStyle> buyingButton = new Button<>(new StaticStyle(buyIcon));
            widget.put(buyIndex, buyingButton);
            final Button<StaticStyle> sellingButton = new Button<>(new StaticStyle(sellIcon));
            widget.put(sellIndex, sellingButton);

            return widget;
        } catch(final Exception exception) {
            if(firstTime)  {
                load(true);
                return createBuyOrSellWidget(false);
            }
            throw exception;
        }
    }

    public int getBuyIndex() {
        return buyIndex;
    }

    public int getSellIndex() {
        return sellIndex;
    }

}
