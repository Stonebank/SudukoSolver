package image.trayicon;

import settings.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Notification {

    private final String description;

    private final TrayIcon tray;

    public Notification(String description) throws IOException, AWTException {
        this.description = description;

        if (!Settings.NOTIFICATION_ICON.exists())
            throw new FileNotFoundException(Settings.NOTIFICATION_ICON.getPath() + " was not found.");

        this.tray = new TrayIcon(Toolkit.getDefaultToolkit().getImage("./resources/image/icon/notification_icon.png"), "Sudoku");
        this.tray.setImageAutoSize(Settings.NOTIFICATION_IMAGE_RESIZE);

        SystemTray systemTray = SystemTray.getSystemTray();
        systemTray.add(tray);

    }

    public static void send(Notification notification) {
        if (!SystemTray.isSupported()) {
            System.err.println("Notification is not supported on this machine.");
            return;
        }
        notification.tray.displayMessage("Sudoku", notification.getDescription(), TrayIcon.MessageType.INFO);
    }

    public String getDescription() {
        return description;
    }

}
