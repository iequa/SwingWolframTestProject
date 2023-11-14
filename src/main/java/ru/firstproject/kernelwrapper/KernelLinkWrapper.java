package ru.firstproject.kernelwrapper;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkFactory;
import ru.firstproject.utils.PropertiesManager;

import java.util.List;

public class KernelLinkWrapper {
    private static KernelLink kernelLink;
    private static String kernelPath = new PropertiesManager().getKernelPath();

    private KernelLinkWrapper() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static KernelLink getInstance() throws Exception {
        if (kernelLink != null) {
            return kernelLink;
        }
        if (tryToConnectKernel(kernelPath)) {
            setupKernel();
            return kernelLink;
        }
        throw new RuntimeException("can't connect to kernel");
    }

    public static String evaluateString(List<String> requests) {
        try {
            final var kl = getInstance();
        } catch (Exception ex) {
            System.out.println("Error kernel link create process");
        }
        kernelLink.clearError();
        kernelLink.clearInterrupt();
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < requests.size(); i++) {
            final var currentExpression = requests.get(i);
            if (currentExpression.isEmpty()) {
                resultString.append("expression in line %s skipped because it's empty%n".formatted(i));
                continue;
            }
            resultString.append("%s: %s%n".formatted(
                    i,
                    kernelLink.evaluateToOutputForm(currentExpression, 0)
            ));
        }
        return resultString.toString();
    }

    public static byte[] evaluateImage(List<String> requests) {
        try {
            final var kl = getInstance();
        } catch (Exception ex) {
            System.out.println("Error kernel link create process");
        }
        //TODO возможно, стоит разделить выполнение простых операций и последней, генерирующей картинку
        StringBuilder resultString = new StringBuilder();
        if (requests.size() > 1) {
            for (int i = 0; i < requests.size() - 1; i++) {
                final var currentExpression = requests.get(i);
                if (currentExpression.isEmpty()) {
                    resultString.append("expression in line %s skipped because it's empty%n".formatted(i));
                    continue;
                }
                resultString.append(i).append(": ").append(kernelLink.evaluateToOutputForm(requests.get(i), 0)).append("\n");
            }
        }
        return kernelLink.evaluateToImage(requests.get(requests.size() - 1), 0, 0);
    }

    private static boolean tryToConnectKernel(String pathToKernel) {
        kernelPath = pathToKernel;
        final String[] mlArgs = {
                "-linkmode",
                "launch",
                "-linkname",
                "%s".formatted(kernelPath)
        };
        try {
            kernelLink = MathLinkFactory.createKernelLink(mlArgs);
            kernelLink.connect();
            new PropertiesManager().setProperty("kernelPath", kernelPath);
            System.out.println("Kernel is ready.");
        } catch (Exception e) {
            System.out.printf("some error!%n%s%n", e.getMessage());
            return false;
        }
        return true;
    }

    public static void setupKernel() throws Exception {
        try {
            kernelLink.discardAnswer();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
