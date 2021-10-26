package com.github.pocketkid2.finditem.gui;

import com.github.pocketkid2.finditem.IntSegment;
import dev.hawu.plugins.finditem.FindItem;
import dev.hawu.plugins.api.collections.tuples.Pair;
import dev.hawu.plugins.api.inventories.Button;
import dev.hawu.plugins.api.inventories.Widget;
import dev.hawu.plugins.api.inventories.WidgetPaginator;
import dev.hawu.plugins.api.inventories.style.StaticStyle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.api.ShopAPI;
import org.maxgamer.quickshop.shop.Shop;
import org.maxgamer.quickshop.shop.ShopType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.pocketkid2.finditem.Internationalization.tl;

/**
 * Main class for dealing with GUI related stuff.
 */
public final class GUIManager {

    private static FindItem plugin;

    /**
     * Injects dependency on the main class for this class.
     *
     * @param pl The main class.
     */
    public static void onEnable(@NotNull final FindItem pl) {
        plugin = pl;
    }

    private static boolean isBorder(int slot) {
        return slot % 9 == 0 || slot % 9 == 8 || new IntSegment(0, 8).contains(slot) || new IntSegment(45, 53).contains(slot);
    }

    private static void fillWidget(@NotNull final Widget widget) {
        final ItemStack background = plugin.getPaginationSettings().createBackgroundStack();
        final Button<StaticStyle> button = new Button<>(new StaticStyle(background));
        button.setHandler(e -> e.setCancelled(true));
        for(int i = 0; i < widget.getInventory().getSize(); i++) {
            if(widget.get(i) == null) widget.put(i, button);
        }
    }

    /**
     * Generates and opens a menu, that lets the player choose whether they want
     * to sell an item, or buy it.
     *
     * @param player   The player whom to open to.
     * @param material The item the player requested.
     */
    public static void openBuyOrSell(@NotNull final Player player, @NotNull final Material material) {
        final Widget widget = plugin.getBuyOrSellSettings().createBuyOrSellWidget(true);

        Objects.requireNonNull(widget.getButton(plugin.getBuyOrSellSettings().getBuyIndex())).setHandler(event -> {
            event.setCancelled(true);
            player.closeInventory();
            recollectShops(player, material, true);
        });

        Objects.requireNonNull(widget.getButton(plugin.getBuyOrSellSettings().getSellIndex())).setHandler(event -> {
            event.setCancelled(true);
            player.closeInventory();
            recollectShops(player, material, false);
        });

        widget.update();
        widget.open(player);
    }

    public static void recollectShops(@NotNull final Player player, @NotNull final Material material, final boolean playerBuying) {
        final ShopAPI api = QuickShopAPI.getShopAPI();

        if(api == null) {
            tl(player, "api-error");
            return;
        }

        Stream<Shop> shopStream = QuickShopAPI.getShopAPI().getAllShops().stream()
            .filter(s -> playerBuying ? s.getShopType() == ShopType.SELLING : s.getShopType() == ShopType.BUYING)
            .filter(s -> s.getItem().getType() == material);

        System.out.println(plugin.getSettings().filtersShop() + " " + plugin.getSettings().filtersSelf());
        if(plugin.getSettings().filtersShop()) shopStream = shopStream.filter(s -> playerBuying ? s.getRemainingStock() > 0 : s.getRemainingSpace() > 0);
        if(plugin.getSettings().filtersSelf()) shopStream = shopStream.filter(s -> !s.getOwner().equals(player.getUniqueId()));

        final List<Shop> shops = shopStream
            .sorted(playerBuying ? Comparator.comparingDouble(Shop::getPrice) : Comparator.comparingDouble(Shop::getPrice).reversed())
            .collect(Collectors.toList());

        if(shops.isEmpty()) {
            tl(player, playerBuying ? "no-selling-shops" : "no-buying-shops");
            return;
        }

        final double median;
        final double average = shops.stream().collect(Collectors.summarizingDouble(Shop::getPrice)).getAverage();

        if(shops.size() % 2 == 1) {
            final int index = Math.floorDiv(shops.size(), 2);
            median = shops.get(index).getPrice();
        } else {
            final int index1 = Math.floorDiv(shops.size(), 2);
            final int index2 = index1 - 1;

            // Since (price + price) / 2 might overflow, so we calculate the average between them by using
            // (1/2 * price) + (1/2 * price).
            median = (shops.get(index1).getPrice() / 2) + (shops.get(index2).getPrice() / 2);
        }

        paginate(player, shops, median, average);
    }

    /**
     * Paginates and opens the list of shops to a player.
     *
     * @param player The player whom to open to.
     * @param shops  The list of shops to paginate.
     */
    public static void paginate(@NotNull final Player player, @NotNull final List<Shop> shops, final double median, final double average) {
        final WidgetPaginator paginator = new WidgetPaginator();
        final int maxPages = shops.size() % 28 == 0 ? shops.size() / 28 : (int) Math.ceil(shops.size() / 28.0);
        int currentPage = 1;
        int currentIndex = 10;
        Widget widget = plugin.getPaginationSettings().createPageWidget(currentPage, maxPages);

        for(final Shop shop : shops) {
            while(isBorder(currentIndex)) currentIndex++;
            if(currentIndex >= widget.getInventory().getSize()) {
                fillWidget(widget);
                widget.update();
                paginator.append(widget);
                currentPage++;
                currentIndex = 10;
                widget = plugin.getPaginationSettings().createPageWidget(currentPage, maxPages);
            }

            final ItemStack icon = plugin.getPaginationSettings().createShopIcon(shop, median, average);
            final Button<StaticStyle> button = new Button<>(new StaticStyle(icon));
            button.setHandler(event -> {
                event.setCancelled(true);
                player.closeInventory();
                teleportFacingSign(player, shop);
            });
            widget.put(currentIndex, button);

            currentIndex++;
        }

        fillWidget(widget);
        widget.update();
        paginator.append(widget);
        paginator.setPrevious(plugin.getPaginationSettings().createPreviousPageStack());
        paginator.setNext(plugin.getPaginationSettings().createNextPageStack());
        paginator.addAllNextSlots(26, 35);
        paginator.addAllPreviousSlots(18, 27);
        paginator.createControls();
        paginator.open(player, 0);
    }

    /**
     * Method to teleport a player facing the sign.
     *
     * @param player The player to teleport.
     * @param shop   The shop whose sign to teleport to.
     * @author PocketKid2
     */
    private static void teleportFacingSign(@NotNull Player player, @NotNull Shop shop) {
        final Sign sign = shop.getSigns().get(0);
        final Location loc = sign.getLocation();
        loc.add(0.5, 0, 0.5);
        loc.setPitch(45);

        final WallSign ws = (WallSign) sign.getBlockData();
        final boolean isShopSelling = shop.getShopType() == ShopType.SELLING;

        switch(ws.getFacing()) {
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
                break;
        }

        if(!loc.getBlock().getType().isAir() || !loc.getBlock().getRelative(BlockFace.UP).getType().isAir()
        || !loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
            tl(player, "occluding-space");
            return;
        }

        if(!player.teleport(loc))
            tl(player, "plugin-blocked-teleportation");
        else tl(player, "teleport-msg", new Pair<>("owner", Bukkit.getOfflinePlayer(shop.getOwner()).getName()),
            new Pair<>("item", shop.getItem().getType().name().toLowerCase().replace('_', ' ')),
            new Pair<>("transaction", isShopSelling ? "&aselling" : "&cbuying"));
    }

}
