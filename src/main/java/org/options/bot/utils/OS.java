package org.options.bot.utils;

public enum OS {
    WINDOWS("windows", "win"),
    LINUX("linux", "nix | nux | aix"),
    MAC("mac", "mac"),
    SOLARIS("solaris", "sunos"),
    UNKNOWN("unknown", "");

    public static OS getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        System.out.println("SYSPROP : " + osName);
        if (WINDOWS.isKeywordMatches(osName))
            return WINDOWS;
        else if (LINUX.isKeywordMatches(osName))
            return LINUX;
        else if (MAC.isKeywordMatches(osName))
            return MAC;
        else if (SOLARIS.isKeywordMatches(osName))
            return SOLARIS;
        else
            return UNKNOWN;
    }

    private boolean isKeywordMatches(String osName) {
        String keyWord = getKeyWord();
        String[] words = keyWord.split("\\|");
        for (String word : words) {
            if (osName.contains(word.trim()))
                return true;
        }
        return false;
    }

    OS(String displayName, String keyWord) {
        this.displayName = displayName;
        this.keyWord = keyWord;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getDisplayName() {
        return displayName;
    }

    private String keyWord;
    private String displayName;
}