package ru.firstproject.utils;

public class LibLoader {
    public static void loadNativeLibrary(String path) {
        System.load(path);
    }
}
