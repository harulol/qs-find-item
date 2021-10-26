package com.github.pocketkid2.finditem;

import dev.hawu.plugins.api.Strings;
import dev.hawu.plugins.api.collections.tuples.Pair;
import dev.hawu.plugins.api.inventories.InventorySize;
import dev.hawu.plugins.api.inventories.Widget;
import dev.hawu.plugins.api.inventories.item.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.shop.Shop;
import org.maxgamer.quickshop.shop.ShopType;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

/**
 * A class dedicated to hold setting keys and fields
 * related to pagination GUIs.
 */
public final class PaginationSettings {

    private final JavaPlugin plugin;
    private final FileConfiguration defaultFile;
    private FileConfiguration pagination;
    private final File paginationConfig;

    private String paginationTitle;

    private Material shopIconMaterial;
    private boolean customHeadEnabled;
    private String shopIconDisplayName;
    private List<String> shopIconLore;

    private Material nextPageMaterial;
    private String nextPageDisplayName;
    private List<String> nextPageLore;

    private Material previousPageMaterial;
    private String previousPageDisplayName;
    private List<String> previousPageLore;

    private Material backgroundMaterial;
    private String backgroundDisplayName;
    private List<String> backgroundLore;

    public PaginationSettings(final JavaPlugin plugin) {
        this.plugin = plugin;
        defaultFile = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource("pagination.yml"))));

        paginationConfig = new File(plugin.getDataFolder(), "pagination.yml");
        if(!paginationConfig.exists()) plugin.saveResource("pagination.yml", true);

        pagination = YamlConfiguration.loadConfiguration(paginationConfig);
        load();
    }

    private void load() {
        paginationTitle = pagination.getString("title", defaultFile.getString("title"));

        shopIconMaterial = Material.matchMaterial(Objects.requireNonNull(pagination.getString("shop-icon.material", defaultFile.getString("title"))));
        customHeadEnabled = pagination.getBoolean("shop-icon.custom-head", defaultFile.getBoolean("shop-icon.custom-head"));
        shopIconDisplayName = pagination.getString("shop-icon.display-name", defaultFile.getString("shop-icon.display-name"));
        shopIconLore = pagination.getStringList("shop-icon.lore");

        nextPageMaterial = Material.matchMaterial(Objects.requireNonNull(pagination.getString("next-page.material",
            defaultFile.getString("next-page.material"))));
        nextPageDisplayName = pagination.getString("next-page.display-name", defaultFile.getString("next-page.display-name"));
        nextPageLore = pagination.getStringList("next-page.lore");

        previousPageMaterial = Material.matchMaterial(Objects.requireNonNull(pagination.getString("previous-page.material",
            defaultFile.getString("previous-page.material"))));
        previousPageDisplayName = pagination.getString("previous-page.display-name", defaultFile.getString("previous-page.display-name"));
        previousPageLore = pagination.getStringList("previous-page.lore");

        backgroundMaterial = Material.matchMaterial(Objects.requireNonNull(pagination.getString("background.material",
            defaultFile.getString("background.material"))));
        backgroundDisplayName = pagination.getString("background.display-name", defaultFile.getString("background.display-name"));
        backgroundLore = pagination.getStringList("background.lore");
    }

    public void reload(final boolean forcefully) {
        plugin.saveResource("pagination.yml", forcefully);
        pagination = YamlConfiguration.loadConfiguration(paginationConfig);
        load();
    }

    private String formatNumber(final ShopType type, final double result, final boolean relative) {
        final String formattedResult = relative ? Strings.format(result) + "%" : Strings.format(result);
        final boolean goodDeal;

        if(type == ShopType.BUYING) {
            // Shop buying, player selling, higher price is better
            if(relative) goodDeal = result >= 100;
            else goodDeal = result >= 0;
        } else {
            // And when the shop is selling, higher price is worse.
            if(relative) goodDeal = result < 100;
            else goodDeal = result < 0;
        }

        final StringBuilder sb = new StringBuilder();

        if(goodDeal) sb.append("&a");
        else sb.append("&c");
        if(result > 0 && !relative) sb.append("+");
        sb.append(formattedResult);

        return sb.toString();
    }

    public String calculateDiff(final ShopType type, final double actual, final double base, final boolean relative) {
        final double result = relative ? actual / base * 100 : actual - base;
        return formatNumber(type, result, relative);
    }

    @NotNull
    public Widget createPageWidget(final int currentPage, final int maxPage) {
        final String name = Strings.fillPlaceholders(paginationTitle, new Pair<>("curr", currentPage), new Pair<>("max", maxPage));
        return new Widget(InventorySize.SIX_ROWS, name);
    }

    @NotNull
    public ItemStack createShopIcon(final @NotNull Shop shop, final double median, final double average) {
        if(shopIconMaterial == null) reload(true);

        final ItemStack item = new ItemStack(shopIconMaterial);
        final OfflinePlayer owner = Bukkit.getOfflinePlayer(shop.getOwner());
        final boolean isPlayerBuying = shop.getShopType() == ShopType.SELLING;

        if(shopIconMaterial == Material.PLAYER_HEAD && customHeadEnabled) {
            final SkullMeta meta = (SkullMeta) item.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(owner);
            item.setItemMeta(meta);
        }

        return new ItemStackBuilder(item).withMeta()
            .displayName(Strings.fillPlaceholders(shopIconDisplayName, new Pair<>("owner's name", owner.getName()))).withLore()
            .add(shopIconLore.toArray(new String[0]))
            .loopAndFill(new Pair<>("price", Strings.format(shop.getPrice())),
                new Pair<>("left", Strings.format(isPlayerBuying ? shop.getRemainingStock() : shop.getRemainingSpace())),
                new Pair<>("median's diff", calculateDiff(shop.getShopType(), shop.getPrice(), median, false)),
                new Pair<>("median's rdiff", calculateDiff(shop.getShopType(), shop.getPrice(), median, true)),
                new Pair<>("average's diff", calculateDiff(shop.getShopType(), shop.getPrice(), average, false)),
                new Pair<>("average's rdiff", calculateDiff(shop.getShopType(), shop.getPrice(), average, true)),
                new Pair<>("median", Strings.format(median)),
                new Pair<>("average", Strings.format(average)))
            .build().build().build();
    }

    @NotNull
    public ItemStack createNextPageStack() {
        if(nextPageMaterial == null) reload(true);

        return new ItemStackBuilder().material(nextPageMaterial).withMeta()
            .displayName(nextPageDisplayName).withLore()
            .add(nextPageLore.toArray(new String[0]))
            .build().build().build();
    }

    @NotNull
    public ItemStack createPreviousPageStack() {
        if(previousPageMaterial == null) reload(true);

        return new ItemStackBuilder().material(previousPageMaterial).withMeta()
            .displayName(previousPageDisplayName).withLore()
            .add(previousPageLore.toArray(new String[0]))
            .build().build().build();
    }

    @NotNull
    public ItemStack createBackgroundStack() {
        if(backgroundMaterial == null) reload(true);

        return new ItemStackBuilder().material(backgroundMaterial).withMeta()
            .displayName(backgroundDisplayName).withLore()
            .add(backgroundLore.toArray(new String[0]))
            .build().build().build();
    }

}
