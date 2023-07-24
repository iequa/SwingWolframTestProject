package ru.firstproject.resourcescheck;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CheckResources {
    public boolean checkIsJar() {
        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        //Загружаем библиотеку для использования JLink в зависимостях проекта
        if (jarFile.isFile()) {  // Run with JAR file
            final JarFile jar;
            try {
                jar = new JarFile(jarFile);
                jar.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        else return false;
    }
}
