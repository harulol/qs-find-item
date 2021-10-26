package com.github.pocketkid2.finditem;

import dev.hawu.plugins.api.I18n;
import dev.hawu.plugins.api.collections.tuples.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for translating keys into messages and filling up
 * placeholders.
 */
public final class Internationalization {

    private static I18n i18n;

    /**
     * Enables the I18n module so key translations can work.
     * This must not be called before {@link JavaPlugin#onEnable()}.
     */
    public static void onEnable(final JavaPlugin plugin) {
        i18n = new I18n(plugin);
    }

    /**
     * Translates a key and fills in necessary placeholders.
     *
     * @param key    The key to translate.
     * @param params The placeholders to fill with.
     * @return The translated, formatted and colored {@link String}.
     */
    @NotNull
    public static String tl(@NotNull final String key, final @NotNull Pair<?, ?> @NotNull ... params) {
        return i18n.tl(key, params);
    }

    /**
     * Translates a key, fills in necessary placeholders then sends
     * that returned message to the sender.
     *
     * @param sender The sender to send the formatted message to.
     * @param key    The key to translate.
     * @param params The placeholders to fill with.
     */
    public static void tl(@NotNull final CommandSender sender, @NotNull final String key, final @NotNull Pair<?, ?> @NotNull ... params) {
        sender.sendMessage(tl(key, params));
    }

    /**
     * Reloads the i18n instance.
     */
    public static void reload() {
        i18n.reload();
    }

    private Internationalization() {}

}
