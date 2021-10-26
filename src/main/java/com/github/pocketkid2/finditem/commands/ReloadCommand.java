package com.github.pocketkid2.finditem.commands;

import com.github.pocketkid2.finditem.Internationalization;
import dev.hawu.plugins.finditem.FindItem;
import dev.hawu.plugins.api.collections.tuples.Pair;
import dev.hawu.plugins.api.commands.AbstractCommandClass;
import dev.hawu.plugins.api.commands.CommandArgument;
import dev.hawu.plugins.api.commands.CommandLine;
import dev.hawu.plugins.api.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.pocketkid2.finditem.Internationalization.tl;

public final class ReloadCommand extends AbstractCommandClass {

    private final FindItem plugin;
    private final CommandLine cli;

    public ReloadCommand(final FindItem pl) {
        super("reload");
        allowPlayers();
        allowConsole();
        this.plugin = pl;
        setPermission("afi.reload");
        this.cli = new CommandLine()
            .withFlag("-c")
            .withFlag("-m")
            .withFlag("-p")
            .withFlag("-b");
    }

    @Override
    public @NotNull List<String> tab(@NotNull final CommandSource sender, @NotNull final CommandArgument args) {
        return Stream.of("-?", "--help", "-c", "-m", "-p", "-b")
            .filter(s -> {
                if(args.size() == 0) return true;
                else return s.startsWith(args.getNonNull(args.size() - 1));
            })
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public void run(@NotNull final CommandSource sender, @NotNull final CommandArgument args) {
        final Pair<CommandArgument, Map<String, List<String>>> parsedPair = args.withCLI(cli).parse();
        final Map<String, List<String>> properties = parsedPair.getSecond();
        boolean reloaded = false;

        assert properties != null;
        if(properties.containsKey("-?") || properties.containsKey("--help")) {
            tl(sender, "help.reload", new Pair<>("version", plugin.getDescription().getVersion()));
            return;
        }

        if(properties.containsKey("-c")) {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            plugin.getSettings().reload();
            tl(sender, "reloaded", new Pair<>("file", "config.yml"));
            reloaded = true;
        }

        if(properties.containsKey("-m")) {
            Internationalization.reload();
            tl(sender, "reloaded", new Pair<>("file", "messages.yml"));
            reloaded = true;
        }

        if(properties.containsKey("-p")) {
            plugin.getPaginationSettings().reload(false);
            tl(sender, "reloaded", new Pair<>("file", "pagination.yml"));
            reloaded = true;
        }

        if(properties.containsKey("-b")) {
            plugin.getBuyOrSellSettings().load(false);
            tl(sender, "reloaded", new Pair<>("file", "buy-sell.yml"));
            reloaded = true;
        }

        if(!reloaded) tl(sender, "reloaded-nothing");
    }

}
