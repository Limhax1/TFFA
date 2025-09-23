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

package me.limhax.tFFA.manager;

import lombok.Getter;
import me.limhax.tFFA.TFFA;
import me.limhax.tFFA.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class KitManager {
  private final File kitFile;
  @Getter
  private Inventory kitInventory;
  private YamlConfiguration kitConfig;

  public KitManager() {
    this.kitFile = new File(TFFA.getInstance().getDataFolder(), "kits.yml");
    loadKit();
  }

  public void loadKit() {
    if (!TFFA.getInstance().getDataFolder().exists()) {
      TFFA.getInstance().getDataFolder().mkdirs();
    }

    if (!kitFile.exists()) {
      createDefaultKit();
    }

    this.kitConfig = YamlConfiguration.loadConfiguration(kitFile);
    loadKitFromConfig();
  }

  private void createDefaultKit() {
    try {
      kitFile.createNewFile();
      kitConfig = YamlConfiguration.loadConfiguration(kitFile);
      kitConfig.set("kit.name", "ffa-kit");
      int kitSize = TFFA.getInstance().getConfigManager().getInt("kit-size");
      kitConfig.set("kit.size", kitSize);

      kitConfig.save(kitFile);
      TFFA.getInstance().getLogger().info(ColorUtil.translate("&aDefault kit file created."));
    } catch (IOException e) {
      TFFA.getInstance().getLogger().severe("Could not create default kit file");
      e.printStackTrace();
    }
  }

  private void loadKitFromConfig() {
    if (kitConfig == null) return;

    String kitName = kitConfig.getString("kit.name", "ffa-kit");
    int size = kitConfig.getInt("kit.size", TFFA.getInstance().getConfigManager().getInt("kit-size"));

    this.kitInventory = Bukkit.createInventory(null, size, kitName);

    for (int i = 0; i < size; i++) {
      if (i < 41) {
        String itemData = kitConfig.getString("kit.items." + i);
        if (itemData != null) {
          ItemStack item = deserializeItemStack(itemData);
          if (item != null) {
            kitInventory.setItem(i, item);
          }
        }
      }
    }

    TFFA.getInstance().getLogger().info(ColorUtil.translate("&aKit loaded: " + kitName));
  }

  public void saveKit(Player player, String kitName) {
    if (kitConfig == null) return;

    kitConfig.set("kit.name", kitName);
    int kitSize = TFFA.getInstance().getConfigManager().getInt("kit-size");
    kitConfig.set("kit.size", kitSize);

    for (int i = 0; i < kitSize; i++) {
      ItemStack item = (i < 41) ? player.getInventory().getItem(i) : null;
      if (item != null && item.getType() != Material.AIR) {
        String serializedItem = serializeItemStack(item);
        kitConfig.set("kit.items." + i, serializedItem);
      } else {
        kitConfig.set("kit.items." + i, null);
      }
    }

    try {
      kitConfig.save(kitFile);
      loadKit();
      TFFA.getInstance().getLogger().info(ColorUtil.translate("&aKit saved: " + kitName));
    } catch (IOException e) {
      TFFA.getInstance().getLogger().severe("Could not save kit");
      e.printStackTrace();
    }
  }

  public void giveKit(Player player) {
    ConfigManager config = TFFA.getInstance().getConfigManager();
    if (kitInventory == null) {
      player.sendMessage(config.getMessage("kit-no-kit-available"));
      return;
    }

    player.getInventory().clear();

    ItemStack[] kitContents = kitInventory.getContents();
    ItemStack[] playerContents = new ItemStack[41];

    for (int i = 0; i < 41 && i < kitContents.length; i++) {
      if (kitContents[i] != null) {
        playerContents[i] = kitContents[i].clone();
      }
    }

    player.getInventory().setContents(playerContents);
    player.sendMessage(config.getMessage("kit-given"));
  }

  public void reloadKit() {
    loadKit();
  }

  private String serializeItemStack(ItemStack item) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
      dataOutput.writeObject(item);
      dataOutput.close();
      return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private ItemStack deserializeItemStack(String data) {
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
      BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
      ItemStack item = (ItemStack) dataInput.readObject();
      dataInput.close();
      return item;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
