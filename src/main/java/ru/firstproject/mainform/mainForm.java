package ru.firstproject.mainform;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkFactory;
import ru.firstproject.imgform.ImgForm;
import ru.firstproject.utils.PropertiesManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class mainForm extends JFrame implements ActionListener {

    Frame frame;
    private JPanel mainPanel;
    private JLabel testLabel;
    private JButton buttonEvaluate;
    private JTextArea resultTextArea;
    private JRadioButton textVariant;
    private JRadioButton imgVariant;
    private JLabel resultLabel;
    private JTextArea mainTextArea;

    private ButtonGroup btnGroup;


    public mainForm() {
        setTitle("Project");
        frame = JFrame.getFrames()[0];
        setContentPane(mainPanel);
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultTextArea.setBorder(new BorderUIResource.LineBorderUIResource(Color.black, 2));
        mainTextArea.setBorder(new BorderUIResource(new BorderUIResource.LineBorderUIResource(Color.CYAN)));
        btnGroup = new ButtonGroup();
        btnGroup.add(textVariant);
        btnGroup.add(imgVariant);
        setVisible(true);
        buttonEvaluate.addActionListener(this::actionPerformed);
    }

    public void setupKernel() throws Exception {
        try {
            connectKernel();
            kernelLink.discardAnswer();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Проверяем входные данные
        final var expr = mainTextArea.getText();
        if (expr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Выражение не может быть пустым!");
            return;
        }
        final var multilineExpr = expr.lines().toList();
        //В зависимости от выбранного условия отрабатывает разный evaluate
        if (btnGroup.isSelected(textVariant.getModel())) {
            //Не изображение
            kernelLink.clearError();
            kernelLink.clearInterrupt();
            StringBuilder resultString = new StringBuilder();
            for (int i = 0; i < multilineExpr.size(); i++) {
                final var currentExpression = multilineExpr.get(i);
                if (currentExpression.isEmpty()) {
                    resultString.append("expression in line %s skipped because it's empty%n".formatted(i));
                    continue;
                }
                resultString.append("%s: %s%n".formatted(
                        i,
                        kernelLink.evaluateToOutputForm(currentExpression, 0)
                ));
            }
            resultTextArea.setText(resultString.toString());
        } else if (btnGroup.isSelected(imgVariant.getModel())) {
            //Конечный результат - изображение
            StringBuilder resultString = new StringBuilder();
            if (multilineExpr.size() > 1) {
                for (int i = 0; i < multilineExpr.size() - 1; i++) {
                    final var currentExpression = multilineExpr.get(i);
                    if (currentExpression.isEmpty()) {
                        resultString.append("expression in line %s skipped because it's empty%n".formatted(i));
                        continue;
                    }
                    resultString.append(i).append(": ").append(kernelLink.evaluateToOutputForm(multilineExpr.get(i), 0)).append("\n");
                }
            }
            byte[] img = kernelLink.evaluateToImage(multilineExpr.get(multilineExpr.size() - 1), 0, 0);
            System.out.println(img.length > 0 ? "Image created." : "some error");
            if (img.length > 0) {
                final var resFileExtension = ".jpg";
                JFileChooser fileChooser = new JFileChooser(".\\");
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(resFileExtension, resFileExtension));
                fileChooser.setAcceptAllFileFilterUsed(true);
                final var option = fileChooser.showSaveDialog(mainForm.getFrames()[0]);
                try {
                    if (option == JFileChooser.APPROVE_OPTION) {
                        final var resultPath = Path.of(fileChooser.getSelectedFile().toPath() + resFileExtension);
                        Files.write(resultPath, img);
                        resultTextArea.setText(resultString + "\nРезультат сохранён по адресу\n %s"
                                .formatted(resultPath)
                        );
                        JOptionPane.showMessageDialog(null, "File saved!");
                        new ImgForm(ImageIO.read(resultPath.toUri().toURL()), resultPath.getFileName().toString());
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private boolean connectKernel() throws IOException {
        PropertiesManager properties = new PropertiesManager();
        String kernelPath = null;
        try {
            if (properties.isEmpty()) {
                while (kernelLink == null) {
                    kernelPath = getPathFromInfoMessage();
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

    private boolean tryToConnectKernel(String pathToKernel) {
        final String[] mlArgs = {
                "-linkmode",
                "launch",
                "-linkname",
                "%s".formatted(pathToKernel)
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

    private String getPathFromInfoMessage() {
        System.out.println("Select a kernel to run");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select \"WolframKernel.exe\" or \"MathKernel.exe\"");
        chooser.showOpenDialog(frame);
        if (chooser.getSelectedFile() == null) {
            return null;
        }
        return chooser.getSelectedFile().toPath().toString();
    }
}