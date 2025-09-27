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
import me.limhax.tffa.util.ColorUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Getter
public class ConfigManager {

  private final File configFile;
  private YamlConfiguration config;

  public ConfigManager() {
    this.configFile = new File(TFFA.getInstance().getDataFolder(), "config.yml");
    loadConfig();
  }

  public void loadConfig() {
    if (!TFFA.getInstance().getDataFolder().exists()) {
      TFFA.getInstance().getDataFolder().mkdirs();
    }

    if (!configFile.exists()) {
      createDefaultConfig();
    }

    this.config = YamlConfiguration.loadConfiguration(configFile);
  }

  private void createDefaultConfig() {
    try (InputStream defaultConfig = getClass().getClassLoader().getResourceAsStream("config.yml")) {
      if (defaultConfig != null) {
        Files.copy(defaultConfig, configFile.toPath());
      }
    } catch (IOException e) {
      TFFA.getInstance().getLogger().severe("Could not create default config.yml");
      e.printStackTrace();
    }
  }

  public String getMessage(String path) {
    String message = config.getString("messages." + path, "Missing message: " + path);
    return ColorUtil.translate(message);
  }

  public boolean getBoolean(String path) {
    return config.getBoolean("settings." + path, false);
  }

  public String getSetting(String path) {
    return config.getString("settings." + path, "");
  }

  public int getInt(String path) {
    return config.getInt("settings." + path, 0);
  }
}
