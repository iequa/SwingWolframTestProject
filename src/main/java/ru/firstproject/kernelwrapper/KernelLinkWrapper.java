package ru.firstproject.kernelwrapper;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkFactory;
import ru.firstproject.utils.PropertiesManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class KernelLinkWrapper {
    private static KernelLink kernelLink;
    private String kernelPath = null;


    public KernelLinkWrapper(String kernelPath) {
        this.kernelPath = kernelPath;
        tryToConnectKernel(kernelPath);
    }

    private boolean tryToConnectKernel(String pathToKernel) {
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
            System.out.println("Kernel is ready.");
        } catch (Exception e) {
            System.out.printf("some error!%n%s%n", e.getMessage());
            return false;
        }
        return true;
    }

    public void setupKernel() throws Exception {
        try {
            connectKernel();
            kernelLink.discardAnswer();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private boolean connectKernel(String path) throws IOException {
        PropertiesManager properties = new PropertiesManager();
        String kernelPath = null;
        try {
            if (properties.isEmpty()) {
                while (kernelLink == null) {
                    kernelPath = path;
                    if (kernelPath != null) {
                        tryToConnectKernel(kernelPath);
                    }
                }
            } else {
                kernelPath = properties.getKernelPath();
                if (!tryToConnectKernel(kernelPath)) {
                    while (kernelLink == null) {
                        getPathFromInfoMessage();
                    }
                }
            }
            if (kernelPath != null && Files.isExecutable(Path.of(kernelPath))) {
                properties.setProperty("kernelPath", kernelPath);
                properties.savePropertiesToFile();
                return true;
            }
        } catch (Exception e) {
            throw new IOException("Error in kernel connection process");
        }
        return true;
    }
}
