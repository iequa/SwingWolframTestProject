package ru.firstproject;

import ru.firstproject.mainform.mainForm;
import ru.firstproject.utils.PropertiesManager;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0] != null) {
            if (args[0].equals("true")) {
                new PropertiesManager().setIsPathNeeded("true").savePropertiesToFile();
            }
            new PropertiesManager().setIsPathNeeded("false").savePropertiesToFile();
        } else new PropertiesManager().setIsPathNeeded("false").savePropertiesToFile();
        mainForm form = new mainForm();
        //form.setupKernel();
    }
}