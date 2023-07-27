package ru.firstproject.imgform;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImgForm extends JFrame {
    private JPanel panel1;
    private JLabel textLabel;
    private JLabel imgLabel;

    public ImgForm(BufferedImage img, String imgPath) {
        this.setTitle("Сгенерированное изображение");
        setSize(800, 600);
        setContentPane(panel1);
        textLabel.setText(imgPath);
        imgLabel.setIcon(new ImageIcon(img));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
