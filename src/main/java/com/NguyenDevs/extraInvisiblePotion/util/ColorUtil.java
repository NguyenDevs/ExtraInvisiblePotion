package com.NguyenDevs.extraInvisiblePotion.util;

import net.md_5.bungee.api.ChatColor;
import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:#([A-Fa-f0-9]{6}):#([A-Fa-f0-9]{6})>(.*?)</gradient>");
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    private static final Pattern HEX_CLOSE_PATTERN = Pattern.compile("</#([A-Fa-f0-9]{6})>");
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorize(String text) {
        if (text == null) return "";
        
        // Process gradients first
        Matcher gradientMatcher = GRADIENT_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (gradientMatcher.find()) {
            String startHex = gradientMatcher.group(1);
            String endHex = gradientMatcher.group(2);
            String content = gradientMatcher.group(3);
            gradientMatcher.appendReplacement(sb, Matcher.quoteReplacement(buildGradient(content, startHex, endHex)));
        }
        gradientMatcher.appendTail(sb);
        text = sb.toString();

        // Process hex colors <#RRGGBB>
        Matcher hexMatcher = HEX_PATTERN.matcher(text);
        sb = new StringBuffer();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1);
            hexMatcher.appendReplacement(sb, Matcher.quoteReplacement(ChatColor.of("#" + hex).toString()));
        }
        hexMatcher.appendTail(sb);
        text = sb.toString();

        // Remove closing hex tags </#RRGGBB>
        Matcher hexCloseMatcher = HEX_CLOSE_PATTERN.matcher(text);
        text = hexCloseMatcher.replaceAll("");

        // Process legacy hex &#RRGGBB
        Matcher legacyHexMatcher = LEGACY_HEX_PATTERN.matcher(text);
        sb = new StringBuffer();
        while (legacyHexMatcher.find()) {
            String hex = legacyHexMatcher.group(1);
            legacyHexMatcher.appendReplacement(sb, Matcher.quoteReplacement(ChatColor.of("#" + hex).toString()));
        }
        legacyHexMatcher.appendTail(sb);
        text = sb.toString();

        // Finally translate standard color codes
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static String buildGradient(String text, String startHex, String endHex) {
        if (text.isEmpty()) return text;
        
        Color start = Color.decode("#" + startHex);
        Color end = Color.decode("#" + endHex);
        
        StringBuilder result = new StringBuilder();
        char[] chars = text.toCharArray();
        int length = chars.length;
        
        for (int i = 0; i < length; i++) {
            float ratio = (length > 1) ? (float) i / (length - 1) : 0;
            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
            
            result.append(ChatColor.of(new Color(r, g, b))).append(chars[i]);
        }
        
        return result.toString();
    }
}