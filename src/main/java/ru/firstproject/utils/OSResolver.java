package ru.firstproject.utils;

public class OSResolver {
    public static String getOSName() {
        return System.getProperty("os.name");
    }

    public static Boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static String getSystemLineSeparator() {
        return System.lineSeparator();
    }

    public static String getSystemPathSeparator() {
        if (isLinux()) {
            return "/";
        }
        return "\\";
    }
}
