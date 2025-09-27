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

package me.limhax.tffa.manager;

import lombok.Getter;
import me.limhax.tffa.TFFA;
import org.bukkit.entity.Player;

public class InventoryManager {
  @Getter
  private final TFFA plugin;

  public InventoryManager() {
    this.plugin = TFFA.getInstance();
  }

  public void applyInventory(Player player) {
    if (plugin.getKitManager() != null) {
      plugin.getKitManager().giveKit(player);
    } else {
      player.getInventory().clear();
    }
  }

  public void saveKit(Player player, String kitName) {
    if (plugin.getKitManager() != null) {
      plugin.getKitManager().saveKit(player, kitName);
    }
  }
}
