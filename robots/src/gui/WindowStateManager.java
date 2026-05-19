package gui;

import java.awt.Frame;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JInternalFrame;

final class WindowStateManager
{
    private static final String STATE_FILE_NAME = ".robots-ui.properties";
    private static final String MAIN_PREFIX = "main";
    private static final String LOG_PREFIX = "logWindow";
    private static final String GAME_PREFIX = "gameWindow";
    private static final String COORDS_PREFIX = "coordsWindow";

    private final Frame mainFrame;
    private final JInternalFrame logWindow;
    private final JInternalFrame gameWindow;
    private final JInternalFrame coordinatesWindow;

    WindowStateManager(Frame mainFrame, JInternalFrame logWindow, JInternalFrame gameWindow,
        JInternalFrame coordinatesWindow)
    {
        this.mainFrame = mainFrame;
        this.logWindow = logWindow;
        this.gameWindow = gameWindow;
        this.coordinatesWindow = coordinatesWindow;
    }

    static boolean hasSavedWindowState()
    {
        return getStateFile().isFile();
    }

    void restoreWindowState()
    {
        Properties props = loadStateProperties();
        if (props == null)
        {
            return;
        }
        restoreMainFrameState(props);
        restoreInternalFrameState(props, LOG_PREFIX, logWindow);
        restoreInternalFrameState(props, GAME_PREFIX, gameWindow);
        restoreInternalFrameState(props, COORDS_PREFIX, coordinatesWindow);
    }

    void saveWindowState()
    {
        Properties props = new Properties();
        saveMainFrameState(props);
        saveInternalFrameState(props, LOG_PREFIX, logWindow);
        saveInternalFrameState(props, GAME_PREFIX, gameWindow);
        saveInternalFrameState(props, COORDS_PREFIX, coordinatesWindow);
        File file = getStateFile();
        try (FileOutputStream out = new FileOutputStream(file))
        {
            props.store(out, "Robots UI state");
        }
        catch (IOException e)
        {
            // ignore
        }
    }

    void clearSavedState()
    {
        File file = getStateFile();
        if (file.isFile())
        {
            file.delete();
        }
    }

    private static File getStateFile()
    {
        String home = System.getProperty("user.home");
        return new File(home, STATE_FILE_NAME);
    }

    private Properties loadStateProperties()
    {
        File file = getStateFile();
        if (!file.isFile())
        {
            return null;
        }
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file))
        {
            props.load(in);
            return props;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    private void saveMainFrameState(Properties props)
    {
        saveBounds(props, MAIN_PREFIX,
            mainFrame.getX(), mainFrame.getY(),
            mainFrame.getWidth(), mainFrame.getHeight());
        props.setProperty(MAIN_PREFIX + ".state", Integer.toString(mainFrame.getExtendedState()));
    }

    private void restoreMainFrameState(Properties props)
    {
        Integer x = readInt(props, MAIN_PREFIX + ".x");
        Integer y = readInt(props, MAIN_PREFIX + ".y");
        Integer w = readInt(props, MAIN_PREFIX + ".w");
        Integer h = readInt(props, MAIN_PREFIX + ".h");
        if (x != null && y != null && w != null && h != null)
        {
            mainFrame.setBounds(x, y, w, h);
        }
        Integer state = readInt(props, MAIN_PREFIX + ".state");
        if (state != null)
        {
            mainFrame.setExtendedState(state);
        }
    }

    private void saveInternalFrameState(Properties props, String prefix, JInternalFrame frame)
    {
        saveBounds(props, prefix, frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight());
        props.setProperty(prefix + ".icon", Boolean.toString(frame.isIcon()));
        props.setProperty(prefix + ".max", Boolean.toString(frame.isMaximum()));
    }

    private void restoreInternalFrameState(Properties props, String prefix, JInternalFrame frame)
    {
        Integer x = readInt(props, prefix + ".x");
        Integer y = readInt(props, prefix + ".y");
        Integer w = readInt(props, prefix + ".w");
        Integer h = readInt(props, prefix + ".h");
        if (x != null && y != null && w != null && h != null)
        {
            frame.setBounds(x, y, w, h);
        }
        boolean isMax = readBoolean(props, prefix + ".max", false);
        boolean isIcon = readBoolean(props, prefix + ".icon", false);
        try
        {
            if (isMax)
            {
                frame.setMaximum(true);
            }
            if (isIcon)
            {
                frame.setIcon(true);
            }
        }
        catch (PropertyVetoException e)
        {
            // ignore
        }
    }

    private void saveBounds(Properties props, String prefix, int x, int y, int w, int h)
    {
        props.setProperty(prefix + ".x", Integer.toString(x));
        props.setProperty(prefix + ".y", Integer.toString(y));
        props.setProperty(prefix + ".w", Integer.toString(w));
        props.setProperty(prefix + ".h", Integer.toString(h));
    }

    private static Integer readInt(Properties props, String key)
    {
        String value = props.getProperty(key);
        if (value == null)
        {
            return null;
        }
        try
        {
            return Integer.valueOf(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private static boolean readBoolean(Properties props, String key, boolean defaultValue)
    {
        String value = props.getProperty(key);
        if (value == null)
        {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}
