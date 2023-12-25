package ru.firstproject;

import ru.firstproject.mainform.mainForm;
import ru.firstproject.utils.PropertiesManager;

public class Main {
    public static void main(String[] args) throws Exception {
        final var pm = new PropertiesManager();
        if (args.length > 0 && args[0] != null) {
            if (args[0].equals("true")) {
                pm.setIsPathNeeded("true").savePropertiesToFile();
            }
        }
        else {
            if (pm.getIsPathNeeded() == null) {
                pm.setIsPathNeeded("true").savePropertiesToFile();
            }
        }
        mainForm form = new mainForm();
        //form.setupKernel();
    }
}