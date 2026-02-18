package com.NguyenDevs.extraInvisiblePotion.util;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>(.*?)</#([A-Fa-f0-9]{6})>",
            Pattern.DOTALL);
    private static final Pattern GRADIENT_PATTERN = Pattern
            .compile("<gradient:#([A-Fa-f0-9]{6}):#([A-Fa-f0-9]{6})>(.*?)</gradient>", Pattern.DOTALL);
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorize(String text) {
        if (text == null)
            return "";
        text = applyGradients(text);
        text = applyHexColors(text);
        text = applyLegacyHex(text);
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    private static String applyGradients(String text) {
        Matcher matcher = GRADIENT_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String startHex = matcher.group(1);
            String endHex = matcher.group(2);
            String content = matcher.group(3);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(buildGradient(content, startHex, endHex)));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String applyHexColors(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            String content = matcher.group(2);
            String colored = ChatColor.of("#" + hex) + content;
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(colored));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String applyLegacyHex(String text) {
        Matcher matcher = LEGACY_HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(ChatColor.of("#" + hex).toString()));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String buildGradient(String text, String startHex, String endHex) {
        if (text.isEmpty())
            return text;
        Color start = Color.decode("#" + startHex);
        Color end = Color.decode("#" + endHex);
        char[] chars = text.toCharArray();
        StringBuilder result = new StringBuilder();
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            double ratio = length == 1 ? 0 : (double) i / (length - 1);
            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
            result.append(ChatColor.of(new Color(r, g, b))).append(chars[i]);
        }
        return result.toString();
    }
}
