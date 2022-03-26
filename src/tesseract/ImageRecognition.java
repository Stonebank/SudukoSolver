package tesseract;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import settings.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ImageRecognition {

    public static void main(String[] args) throws IOException, TesseractException {
        ImageRecognition imageRecognition = new ImageRecognition(new File("./resources/image/board.png"));

        int y2 = imageRecognition.getImage().getHeight() / 9;
        int y1 = 1;

        for (int i = 1; i <= 9; i++) {
            imageRecognition.crop(imageRecognition.getImage(), i, y1, imageRecognition.getImage().getWidth() - 1, y2);
            y1 += imageRecognition.getImage().getHeight() / 9;
        }

        imageRecognition.read();

    }

    private final BufferedImage image;

    private final ArrayList<Integer> number = new ArrayList<>();

    private final Tesseract tesseract;

    public ImageRecognition(File file) throws IOException {

        System.out.println("Reading image: " + file.getPath() + "...");

        if (!file.exists())
           throw new FileNotFoundException(file.getPath() + " was not found.");

        image = ImageIO.read(file);

        System.out.println("Image read: " + file.getPath());
        System.out.println("Initiating tesseract....");

        this.tesseract = new Tesseract();
        tesseract.setDatapath("./resources/tesseract_data/");
        tesseract.setTessVariable("user_defined_dpi", "300");

        System.out.println("tesseract initiated!");
        System.out.println("user_defined_dpi set to: " + Settings.TESSERACT_DPI + "dpi");

    }

    public void read() {

        try {

            for (int row = 1; row <= 9; row++) {
                File rowFile = new File("./resources/image/board_" + row + ".png");
                String text = tesseract.doOCR(rowFile);

                for (char c : text.toCharArray()) {

                    if (Character.getNumericValue(c) <= -1 || Character.isAlphabetic(c) || c == '|' || c == ']' || c == '(')
                        continue;

                    number.add(c == ' ' ? 0 : Character.getNumericValue(c));

                }

            }

            for (int i = 0; i < number.size(); i++) {
                System.out.print(number.get(i) + " ");
                if (i % 9 == 0)
                    System.out.println();
            }

        } catch (TesseractException e) {
            System.err.println("Tesseract could not perform OCR.");
            e.printStackTrace();
        }
    }

    public void crop(BufferedImage source, int row, int startY, int endX, int endY) {
        BufferedImage img = source.getSubimage(1, startY, endX, endY);

        Graphics g = img.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        try {
            String name = "./resources/image/board_" + row + ".png";
            System.out.println("Cropping (" + row + ", " + startY + ", " + endX + ", " + endY + "): " + name);
            ImageIO.write(img, "png", new File(name));
        } catch (IOException e) {
            System.err.println("Image could not be written.");
            e.printStackTrace();
        }

    }

    public BufferedImage getImage() {
        return image;
    }

}