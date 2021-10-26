package com.github.pocketkid2.finditem.commands;

import com.github.pocketkid2.finditem.gui.GUIManager;
import dev.hawu.plugins.api.collections.tuples.Pair;
import dev.hawu.plugins.api.commands.AbstractCommandClass;
import dev.hawu.plugins.api.commands.CommandArgument;
import dev.hawu.plugins.api.commands.CommandLine;
import dev.hawu.plugins.api.commands.CommandSource;
import dev.hawu.plugins.finditem.FindItem;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.pocketkid2.finditem.Internationalization.tl;

public final class FindCommand extends AbstractCommandClass {

    private final Material[] materials = Material.values();
    private final FindItem plugin;
    private final CommandLine cli;

    public FindCommand(@NotNull final FindItem plugin) {
        super("finditem");
        this.plugin = plugin;
        setPermission("afi.find");
        allowPlayers();
        this.cli = new CommandLine().withFlag("-b").withFlag("--buying").withFlag("-s").withFlag("--selling");
        register(plugin);
    }

    @Override
    public @Nullable List<String> tab(@NotNull final CommandSource sender, @NotNull final CommandArgument args) {
        if(args.size() == 1) {
            if(plugin.getEssentials() != null) {
                // Use the Essentials' ItemDB if it exists, as PocketKid2 has made.
                return plugin.getEssentials().getItemDb().listNames().stream()
                    .filter(s -> s.startsWith(args.getNonNull(0)))
                    .sorted()
                    .collect(Collectors.toList());
            } else {
                // Use a lower-cased version of Material in the case Essentials is not available.
                return Arrays.stream(materials)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .filter(s -> s.startsWith(args.getNonNull(0)))
                    .sorted()
                    .collect(Collectors.toList());
            }
        } else if(args.size() > 1) {
            return Stream.of("-b", "--buying", "-s", "--selling")
                .filter(s -> s.startsWith(args.getNonNull(args.size() - 1)))
                .filter(s -> !args.getUnderlyingList().contains(s))
                .sorted()
                .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void run(@NotNull final CommandSource sender, @NotNull final CommandArgument args) {
        final Pair<CommandArgument, Map<String, List<String>>> parsed = args.withCLI(cli).parse();
        final CommandArgument arguments = Objects.requireNonNull(parsed.getFirst());
        final Map<String, List<String>> properties = Objects.requireNonNull(parsed.getSecond());

        // At least 1 argument check:
        if(arguments.size() < 1) {
            tl(sender, "not-enough-args", new Pair<>("expected", "1+"), new Pair<>("got", args.size()));
            return;
        }

        if(properties.containsKey("-?") || properties.containsKey("--help")) {
            tl(sender, "help.find", new Pair<>("version", plugin.getDescription().getVersion()));
            return;
        }

        final String typedMaterial = String.join("_", arguments.getUnderlyingList());
        Material mat;

        if(plugin.getEssentials() != null) {
            try {
                mat = plugin.getEssentials().getItemDb().get(typedMaterial).getType();
            } catch(final Exception e) {
                tl(sender, "invalid-item-name", new Pair<>("name", typedMaterial));
                return;
            }
        } else {
            mat = Material.matchMaterial(typedMaterial, false);
            if(mat == null) Material.matchMaterial(typedMaterial, true);

            if(mat == null) {
                tl(sender, "invalid-item-name", new Pair<>("name", typedMaterial));
                return;
            }
        }

        if(properties.containsKey("-b") || properties.containsKey("--buying")) {
            GUIManager.recollectShops(sender.getPlayer(), mat, true);
        } else if(properties.containsKey("-s") || properties.containsKey("--selling")) {
            GUIManager.recollectShops(sender.getPlayer(), mat, false);
        } else GUIManager.openBuyOrSell(sender.getPlayer(), mat);
    }

}
