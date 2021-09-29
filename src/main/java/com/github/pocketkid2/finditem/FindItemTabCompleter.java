package com.github.pocketkid2.finditem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class FindItemTabCompleter implements TabCompleter {

	private MVFindItem plugin;

	public FindItemTabCompleter(MVFindItem p) {
		plugin = p;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (plugin.getEssentials() != null) {
			Collection<String> names = plugin.getEssentials().getItemDb().listNames();
			List<String> matches = new ArrayList<String>();
			StringUtil.copyPartialMatches(args[0], names, matches);
			Collections.sort(matches);
			return matches;
		}
		return null;
	}

}
