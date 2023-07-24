package ru.firstproject.mainForm;

import com.wolfram.jlink.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class mainForm extends JFrame {

    private KernelLink kernelLink;
    private JPanel mainPanel;
    private JLabel testLabel;
    private JButton buttonEvaluate;
    private JTextArea resultTextArea;
    private JRadioButton textVariant;
    private JRadioButton imgVariant;
    private JLabel resultLabel;
    private JTextArea mainTextArea;

    private ButtonGroup btnGroup;


    public mainForm() throws MathLinkException {
        setContentPane(mainPanel);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultTextArea.setBorder(new BorderUIResource.LineBorderUIResource(Color.black, 2));
        btnGroup = new ButtonGroup();
        btnGroup.add(textVariant);
        btnGroup.add(imgVariant);
        setVisible(true);
        setupKernel();
        buttonEvaluate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Проверяем входные данные
                final var expr = mainTextArea.getText();
                if (expr.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Выражение не может быть пустым!");
                    return;
                }
                final var multilineExpr = expr.lines().toList();
                //В зависимости от выбранного условия отрабатывает разный evaluate
                if (btnGroup.isSelected(textVariant.getModel())) {
                    kernelLink.clearError();
                    kernelLink.clearInterrupt();
                    var resultString = "";
                    for (int i = 0; i < multilineExpr.size(); i++) {
                        resultString += i + ": " + kernelLink.evaluateToOutputForm(multilineExpr.get(i), 0) + "\n";
                    }
                    resultTextArea.setText(resultString);
                } else if (btnGroup.isSelected(imgVariant.getModel())) {
                    try {
                        var resultString = "";
                        for (int i = 0; i < multilineExpr.size() - 1; i++) {
                            resultString += i + ": " + kernelLink.evaluateToOutputForm(multilineExpr.get(i), 0) + "\n";
                        }
                        byte[] img = kernelLink.evaluateToImage(multilineExpr.get(multilineExpr.size()), 0, 0);
                        System.out.println(img.length > 0 ? "Image created." : "some error");
                        if (img.length > 0) {
                            final var extension = ".jpg";
                            JFileChooser fileChooser = new JFileChooser(".\\");
                            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".jpg", extension));
                            fileChooser.setAcceptAllFileFilterUsed(true);
                            int option = fileChooser.showSaveDialog(mainForm.getFrames()[0]);
                            if (option == JFileChooser.APPROVE_OPTION) {
                                File file = fileChooser.getSelectedFile();
                            }
                            final var resultPath = Path.of(fileChooser.getSelectedFile().toPath() + extension);
                            Files.write(
                                    resultPath,
                                    img
                            );
                            resultTextArea.setText(resultString + "\nРезультат сохранён по адресу\n %s"
                                    .formatted(resultPath)
                            );
                            JOptionPane.showMessageDialog(null, "File saved!");
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    public void setupKernel() throws MathLinkException {
        final String[] mlArgs = {
                "-linkmode",
                "launch",
                "-linkname"
                //"E:\\mathematica\\mathkernel"
        };
        System.out.println("Select a kernel to run");
        JOptionPane.showMessageDialog(null, "Select \"WolframKernel.exe\" or \"MathKernel.exe\"");
        kernelLink = MathLinkFactory.createKernelLink(mlArgs);
        kernelLink.connect();
        if (kernelLink.ready()) {
            System.out.println("Kernel is ready.");
        }
        kernelLink.discardAnswer();
    }
}