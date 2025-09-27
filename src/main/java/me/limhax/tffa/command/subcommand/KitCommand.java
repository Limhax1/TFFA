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

package me.limhax.tffa.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.limhax.tffa.TFFA;
import me.limhax.tffa.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("ffa|tffa")
@CommandPermission("tffa.kit")
public class KitCommand extends BaseCommand {

  @Subcommand("kit set")
  @CommandPermission("tffa.kit.set")
  public void onKitSet(Player sender, String kitName) {
    ConfigManager config = TFFA.getInstance().getConfigManager();

    TFFA.getInstance().getInventoryManager().saveKit(sender, kitName);
    sender.sendMessage(config.getMessage("kit-set-success"));
  }

  @Subcommand("kit give")
  @CommandPermission("tffa.kit.give")
  public void onKitGive(CommandSender sender, String playerName) {
    ConfigManager config = TFFA.getInstance().getConfigManager();
    Player target = Bukkit.getPlayer(playerName);

    if (target == null) {
      sender.sendMessage(config.getMessage("kit-player-not-online"));
      return;
    }

    if (TFFA.getInstance().getKitManager() != null) {
      TFFA.getInstance().getKitManager().giveKit(target);
      sender.sendMessage(config.getMessage("kit-give-success"));
    }
  }

  @Subcommand("kit reload")
  @CommandPermission("tffa.kit.reload")
  public void onKitReload(CommandSender sender) {
    ConfigManager config = TFFA.getInstance().getConfigManager();

    if (TFFA.getInstance().getKitManager() != null) {
      TFFA.getInstance().getKitManager().reloadKit();
      sender.sendMessage(config.getMessage("kit-reloaded"));
    }
  }
}
