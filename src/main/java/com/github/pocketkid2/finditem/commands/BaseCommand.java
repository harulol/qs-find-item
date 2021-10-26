package com.github.pocketkid2.finditem.commands;

import dev.hawu.plugins.finditem.FindItem;
import dev.hawu.plugins.api.commands.AbstractCommandClass;
import dev.hawu.plugins.api.commands.CommandArgument;
import dev.hawu.plugins.api.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class BaseCommand extends AbstractCommandClass {

    public BaseCommand(@NotNull final FindItem pl) {
        super("advancedfinditem");
        allowPlayers();
        allowConsole();
        setPermission("afi.main");

        bind(new ReloadCommand(pl));

        register(pl);
    }

    @Override
    public @NotNull List<String> tab(@NotNull final CommandSource sender, @NotNull final CommandArgument args) {
        return Collections.singletonList("reload");
    }

    @Override
    public void run(@NotNull final CommandSource sender, @NotNull final CommandArgument args) {
        sender.sendMessage("/afi [arguments]");
    }

}
