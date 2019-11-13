package org.options.bot.utils;

public class SystemUtils {
    public static void main(String[] args) {
        OS os = OS.getOS();
        System.out.println(os.getDisplayName());
    }
}
