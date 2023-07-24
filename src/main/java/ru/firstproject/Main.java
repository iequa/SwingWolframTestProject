package ru.firstproject;

import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;
import ru.firstproject.mainForm.mainForm;
import ru.firstproject.resourcescheck.CheckResources;
import com.wolfram.jlink.KernelLink;

public class Main {
    KernelLink kernelLink;
    public static void main(String[] args) throws MathLinkException {
        CheckResources checker = new CheckResources();
        checker.checkIsJar();
        System.out.println("Hello world!");
        mainForm form = new mainForm();
    }

}