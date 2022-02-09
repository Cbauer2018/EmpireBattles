package com.tort.EmpireBattles.Files;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colors {
    private static final int CENTER_PX = 154;
    private static final Pattern HEX_PATTERN = Pattern.compile("#<([A-Fa-f0-9]){6}>");
    public static boolean HEX_USE = false;

    public static ChatColor getChatColorByCode(String colorCode) {
        switch (colorCode) {
            case "&b":
                return ChatColor.AQUA;
            case "&0":
                return ChatColor.BLACK;
            case "&9":
                return ChatColor.BLUE;
            case "&l":
                return ChatColor.BOLD;
            case "&3":
                return ChatColor.DARK_AQUA;
            case "&1":
                return ChatColor.DARK_BLUE;
            case "&8":
                return ChatColor.DARK_GRAY;
            case "&2":
                return ChatColor.DARK_GREEN;
            case "&5":
                return ChatColor.DARK_PURPLE;
            case "&4":
                return ChatColor.DARK_RED;
            case "&6":
                return ChatColor.GOLD;
            case "&7":
                return ChatColor.GRAY;
            case "&a":
                return ChatColor.GREEN;
            case "&o":
                return ChatColor.ITALIC;
            case "&d":
                return ChatColor.LIGHT_PURPLE;
            case "&k":
                return ChatColor.MAGIC;
            case "&c":
                return ChatColor.RED;
            case "&r":
                return ChatColor.RESET;
            case "&m":
                return ChatColor.STRIKETHROUGH;
            case "&n":
                return ChatColor.UNDERLINE;
            case "&f":
                return ChatColor.WHITE;
            case "&e":
                return ChatColor.YELLOW;
            default:
                return ChatColor.WHITE;
        }
    }

    public static Color translateChatColorToColor(ChatColor chatColor)
    {
        switch (chatColor) {
            case AQUA:
                return Color.AQUA;
            case BLACK:
                return Color.BLACK;
            case BLUE:
                return Color.BLUE;
            case DARK_AQUA:
                return Color.BLUE;
            case DARK_BLUE:
                return Color.BLUE;
            case DARK_GRAY:
                return Color.GRAY;
            case DARK_GREEN:
                return Color.GREEN;
            case DARK_PURPLE:
                return Color.PURPLE;
            case DARK_RED:
                return Color.RED;
            case GOLD:
                return Color.YELLOW;
            case GRAY:
                return Color.GRAY;
            case GREEN:
                return Color.GREEN;
            case LIGHT_PURPLE:
                return Color.PURPLE;
            case RED:
                return Color.RED;
            case WHITE:
                return Color.WHITE;
            case YELLOW:
                return Color.YELLOW;
            default:
                break;
        }

        return null;
    }

    public static String getCenteredMessage(String message) {
        if (message == null || message.equals("")) return "";

        message = ChatColor.translateAlternateColorCodes('&', message);
        message = message.replace("<center>", "").replace("</center>", "");

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '�') {
                previousCode = true;

            } else if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                } else isBold = false;

            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb.toString() + message;

    }

    public static String color(String message) {
        if (HEX_USE) {
            Matcher matcher = HEX_PATTERN.matcher(message);

            while (matcher.find()) {
                String hexString = matcher.group();

                hexString = "#" + hexString.substring(2, hexString.length() - 1);

                final ChatColor hex = ChatColor.valueOf(hexString);
                final String before = message.substring(0, matcher.start());
                final String after = message.substring(matcher.end());

                message = before + hex + after;
                matcher = HEX_PATTERN.matcher(message);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static int translateChatColorToDynmapColor(ChatColor chatColor)
    {
        switch (chatColor) {
            case AQUA:
                return 0x00EAFF;
            case BLACK:
                return 0x000000;
            case BLUE:
                return 0x0000FF;
            case DARK_AQUA:
                return 0x0092DB;
            case DARK_BLUE:
                return 0x001ADB;
            case DARK_GRAY:
                return 0x6B6B6B;
            case DARK_GREEN:
                return 0x007D00;
            case DARK_PURPLE:
                return 0x5C007D;
            case DARK_RED:
                return 0x960000;
            case GOLD:
                return 0x949600;
            case GRAY:
                return 0x949494;
            case GREEN:
                return 0x0CFA00;
            case LIGHT_PURPLE:
                return 0xBC46F2;
            case RED:
                return 0xFF0000;
            case WHITE:
                return 0xFFFFFF;
            case YELLOW:
                return 0xFFFF00;
            default:
                break;
        }

        return 0xFFFFFF;
    }

    public static ChatColor translateStringtoChatColor(String chatColor)
    {
        switch (chatColor) {
            case "AQUA":
                return ChatColor.AQUA;
            case "BLACK":
                return ChatColor.BLACK;
            case "BLUE":
                return ChatColor.BLUE;
            case "DARK_AQUA":
                return ChatColor.DARK_AQUA;
            case "DARK_BLUE":
                return ChatColor.DARK_BLUE;
            case "DARK_GRAY":
                return ChatColor.DARK_GRAY;
            case "DARK_GREEN":
                return ChatColor.DARK_GREEN;
            case "DARK_PURPLE":
                return ChatColor.DARK_PURPLE;
            case "DARK_RED":
                return ChatColor.DARK_RED;
            case "GOLD":
                return ChatColor.GOLD;
            case "GRAY":
                return ChatColor.GRAY;
            case "GREEN":
                return ChatColor.GREEN;
            case "LIGHT_PURPLE":
                return ChatColor.LIGHT_PURPLE;
            case "RED":
                return ChatColor.RED;
            case "WHITE":
                return ChatColor.WHITE;
            case "YELLOW":
                return ChatColor.YELLOW;
            default:
                break;
        }

        return ChatColor.WHITE;
    }

    public static String translateChatColorToString(ChatColor chatColor)
    {
        switch (chatColor) {
            case AQUA:
                return "AQUA";
            case BLACK:
                return "BLACK";
            case BLUE:
                return "BLUE";
            case DARK_AQUA:
                return "DARK_AQUA";
            case DARK_BLUE:
                return "DARK_BLUE";
            case DARK_GRAY:
                return "DARK_GRAY";
            case DARK_GREEN:
                return "DARK_GREEN";
            case DARK_PURPLE:
                return "DARK_PURPLE";
            case DARK_RED:
                return "DARK_RED";
            case GOLD:
                return "GOLD";
            case GRAY:
                return "GRAY";
            case GREEN:
                return "GREEN";
            case LIGHT_PURPLE:
                return "LIGHT_PURPLE";
            case RED:
                return "RED";
            case WHITE:
                return "WHITE";
            case YELLOW:
                return "YELLOW";
            default:
                break;
        }

        return "WHITE";
    }

    public static Material translateChatColorToWool(ChatColor chatColor)
    {
        switch (chatColor) {
            case AQUA:
                return Material.LIGHT_BLUE_WOOL;
            case BLACK:
                return Material.BLACK_WOOL;
            case BLUE:
                return Material.BLUE_WOOL;
            case DARK_AQUA:
                return Material.CYAN_WOOL;
            case DARK_BLUE:
                return Material.BLUE_WOOL;
            case DARK_GRAY:
                return Material.GRAY_WOOL;
            case DARK_GREEN:
                return Material.GREEN_WOOL;
            case DARK_PURPLE:
                return Material.PURPLE_WOOL;
            case DARK_RED:
                return  Material.RED_WOOL;
            case GOLD:
                return Material.YELLOW_WOOL;
            case GRAY:
                return Material.LIGHT_GRAY_WOOL;
            case GREEN:
                return Material.LIME_WOOL;
            case LIGHT_PURPLE:
                return Material.MAGENTA_WOOL;
            case RED:
                return Material.RED_WOOL;
            case WHITE:
                return Material.WHITE_WOOL;
            case YELLOW:
                return Material.YELLOW_WOOL;
            default:
                break;
        }

        return Material.WHITE_WOOL;
    }


}
