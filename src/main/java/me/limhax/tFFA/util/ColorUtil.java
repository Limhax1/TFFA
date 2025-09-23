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

package me.limhax.tFFA.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {
  public static String translate(String message) {
    try {

      final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
      for (Matcher matcher = pattern.matcher(message); matcher.find(); matcher = pattern.matcher(message)) {
        final String hexCode = message.substring(matcher.start() + 1, matcher.end());
        final String colorCode = hexToMinecraft(hexCode);
        message = message.replace("&" + hexCode, colorCode);
      }
      message = message.replace('&', '§');
      return message;

    } catch (Exception e) {
      // well shit
    }
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  private static String hexToMinecraft(String hex) {
    if (hex.length() != 7 || !hex.startsWith("#")) return "";

    StringBuilder result = new StringBuilder("§x");
    for (int i = 1; i < hex.length(); i++) {
      result.append("§").append(hex.charAt(i));
    }
    return result.toString();
  }
}
