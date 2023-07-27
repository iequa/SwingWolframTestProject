package ru.firstproject.mainform;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;
import ru.firstproject.imgform.ImgForm;

import javax.imageio.ImageIO;
import javax.swing.*;
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


    public mainForm() {
        setTitle("Project");
        frame = JFrame.getFrames()[0];
        setContentPane(mainPanel);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultTextArea.setBorder(new BorderUIResource.LineBorderUIResource(Color.black, 2));
        btnGroup = new ButtonGroup();
        btnGroup.add(textVariant);
        btnGroup.add(imgVariant);
        setVisible(true);
        mainTextArea.setBorder(new BorderUIResource(new BorderUIResource.LineBorderUIResource(Color.CYAN)));
        buttonEvaluate.addActionListener(this::actionPerformed);
    }

    public void setupKernel() throws MathLinkException {
        System.out.println("Select a kernel to run");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select \"WolframKernel.exe\" or \"MathKernel.exe\"");
        chooser.showOpenDialog(frame);
        final String[] mlArgs = {
                "-linkmode",
                "launch",
                "-linkname",
                "%s".formatted(chooser.getSelectedFile().toPath())
        };
        kernelLink = MathLinkFactory.createKernelLink(mlArgs);
        kernelLink.connect();
        if (kernelLink.ready()) {
            System.out.println("Kernel is ready.");
        }
        kernelLink.discardAnswer();
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
            var resultString = "";
            for (int i = 0; i < multilineExpr.size(); i++) {
                resultString += i + ": " + kernelLink.evaluateToOutputForm(multilineExpr.get(i), 0) + "\n";
            }
            resultTextArea.setText(resultString);
        } else if (btnGroup.isSelected(imgVariant.getModel())) {
            //Конечный результат - изображение
            var resultString = "";
            if (multilineExpr.size() > 1) {
                for (int i = 0; i < multilineExpr.size() - 1; i++) {
                    resultString += i + ": " + kernelLink.evaluateToOutputForm(multilineExpr.get(i), 0) + "\n";
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
}