package ru.firstproject.mainform;

import ru.firstproject.imgform.ImgForm;
import ru.firstproject.kernelwrapper.KernelLinkWrapper;
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


    public mainForm() throws Exception {
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
        final var propMng = new PropertiesManager();
        if (Boolean.parseBoolean(propMng.getIsPathNeeded()) && propMng.getKernelPath() == null) {
            String path = null;
            while (path == null) {
                path = getPathFromInfoMessage();
            }
            new PropertiesManager().setKernelPath(path).savePropertiesToFile();
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
            final var result = KernelLinkWrapper.evaluateString(multilineExpr);
            resultTextArea.setText(result);
        } else if (btnGroup.isSelected(imgVariant.getModel())) {
            //Конечный результат - изображение
            byte[] img = KernelLinkWrapper.evaluateImage(multilineExpr);
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
                        resultTextArea.setText("\nРезультат сохранён по адресу\n %s"
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

    public String getPathFromInfoMessage() {
        System.out.println("Select a kernel to run");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select \"WolframKernel\" or \"MathKernel\"");
        chooser.showOpenDialog(frame);
        if (chooser.getSelectedFile() == null) {
            return null;
        }
        return chooser.getSelectedFile().toPath().toString();
    }
}