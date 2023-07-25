package ru.firstproject;

import com.wolfram.jlink.MathLinkException;
import ru.firstproject.mainform.mainForm;
import ru.firstproject.resourcescheck.CheckResources;

public class Main {
    public static void main(String[] args) throws MathLinkException {
        CheckResources checker = new CheckResources();
        checker.checkIsJar();
        System.out.println("Hello world!");
        mainForm form = new mainForm();
        form.setupKernel();
    }

}