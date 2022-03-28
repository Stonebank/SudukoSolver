package tesseract;

import board.SudukoBoard;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import settings.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageRecognition {

    public static void main(String[] args) throws IOException {
        ImageRecognition imageRecognition = new ImageRecognition(Settings.BOARD_IMAGE);

        int x1 = 1;
        int y1 = 6;

        for (int i = 1; i <= 81; i++) {
            imageRecognition.crop(imageRecognition.getImage(), i, x1, y1,50, 50);
            x1 += 55;
            if (i % 9 == 0) {
                x1 = 1;
                y1 += 55;
            }
        }

        imageRecognition.read();

    }

    private final BufferedImage image;

    // temp
    private final int[][] board = new int[9][9];
    private final int[][] pre_board = new int[][] {
            { 9, 1, 3, 4, 2, 7, 0, 8, 0 },
            { 6, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 2, 0, 0, 0, 0, 3, 0, 7, 0 },
            { 0, 0, 0, 1, 0, 2, 0, 0, 8 },
            { 0, 6, 2, 5, 0, 0, 0, 0, 3 },
            { 5, 3, 8, 7, 0, 0, 2, 9, 0 },
            { 3, 4, 0, 8, 7, 0, 0, 6, 0 },
            { 0, 0, 6, 0, 4, 9, 8, 1, 5 },
            { 8, 0, 1, 2, 0, 0, 0, 0, 0 }
    };

    private void correctBoard() {
        int mistake = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (pre_board[i][j] != board[i][j]) {
                    board[i][j] = pre_board[i][j];
                    mistake++;
                }
            }
        }
        System.out.println("The current board has " + (board.length * board.length) + " elements in which " + ((double) mistake / (board.length * board.length) * 100) + "% were mistakes.");
        System.out.println();
    }

    private final Tesseract tesseract;

    public ImageRecognition(File file) throws IOException {

        System.out.println("Reading image: " + file.getPath() + "...");

        if (!file.exists())
           throw new FileNotFoundException(file.getPath() + " was not found.");

        image = ImageIO.read(file);

        System.out.println("Image read: " + file.getPath());
        System.out.println("Initiating tesseract....");

        this.tesseract = new Tesseract();
        tesseract.setDatapath(Settings.TESSERACT_TRAINED_DATA.getPath());
        tesseract.setTessVariable("user_defined_dpi", String.valueOf(Settings.TESSERACT_DPI));
        tesseract.setPageSegMode(Settings.TESSERACT_PSM);

        System.out.println("tesseract initiated!");
        System.out.println("user_defined_dpi set to: " + Settings.TESSERACT_DPI + "dpi");
        System.out.println("page_seg_mode set to: " + Settings.TESSERACT_PSM);

    }

    public void read() {

        try {

            int col = 0;
            int rowIndex = 0;

            for (int row = 1; row <= 81; row++) {
                File rowFile = new File("./resources/image/board_" + row + ".png");
                String rowOCR = tesseract.doOCR(rowFile);

                for (char c : rowOCR.replaceAll("[^\\d]", "").toCharArray()) {
                    board[rowIndex][col] = Character.getNumericValue(c);
                    col++;
                    if (col > 8)
                        col = 0;
                }

                if (row % 9 == 0)
                    rowIndex++;

            }

            System.out.println("Row index: " + rowIndex);

            SudukoBoard sudukoBoard = new SudukoBoard(board, 9);
            sudukoBoard.displayBoard();

            correctBoard();

            sudukoBoard.displayBoard();

            System.out.println("solving...");
            if (sudukoBoard.canSolve())
                sudukoBoard.displayBoard();

        } catch (TesseractException e) {
            System.err.println("Tesseract could not perform OCR.");
            e.printStackTrace();
        }
    }

    public void crop(BufferedImage source, int row, int startX, int startY, int endX, int endY) {
        BufferedImage img = source.getSubimage(startX, startY, endX, endY);

        Graphics g = img.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        try {
            String name = "./resources/image/board_" + row + ".png";
            System.out.println("Cropping (" + row + ", " + startX + ", " + startY + ", " + endX + ", " + endY + "): " + name);
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
