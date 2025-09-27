// This file is part of TFFA
// Copyright (C) 2025 Limhax
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package me.limhax.tFFA.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.limhax.tFFA.TFFA;
import me.limhax.tFFA.event.FFAEvent;
import me.limhax.tFFA.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("ffa|tffa")
@CommandPermission("tffa.start")
public class StartCommand extends BaseCommand {

  @Subcommand("start")
  public void execute(CommandSender sender) {
    ConfigManager config = TFFA.getInstance().getConfigManager();
    FFAEvent event = TFFA.getInstance().getEvent();

    if (event.isRunning()) {
      sender.sendMessage(config.getMessage("start-already-running"));
      return;
    }

    event.start();

    sender.sendMessage(config.getMessage("event-started-command"));

    // This isn't exactly optimal, Bukkit.broadcastMessage() is better, but it doesn't work on drowned.
    Bukkit.getOnlinePlayers().forEach(p -> {p.sendMessage(config.getMessage("start-announce"));});
  }
}
