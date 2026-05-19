package gui;

import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import log.Logger;

final class MenuBuilder
{
    private static final String FILE_MENU_TEXT = "Файл";
    private static final String FILE_MENU_DESCRIPTION = "Действия приложения";
    private static final String RESET_WINDOWS_TEXT = "Сброс окон";
    private static final String EXIT_TEXT = "Выход";

    private static final String LOOK_AND_FEEL_MENU_TEXT = "Режим отображения";
    private static final String LOOK_AND_FEEL_MENU_DESCRIPTION = "Управление режимом отображения приложения";
    private static final String SYSTEM_LAF_TEXT = "Системная схема";
    private static final String CROSS_PLATFORM_LAF_TEXT = "Универсальная схема";

    private static final String TEST_MENU_TEXT = "Тесты";
    private static final String TEST_MENU_DESCRIPTION = "Тестовые команды";
    private static final String ADD_LOG_MESSAGE_TEXT = "Сообщение в лог";

    private final JFrame owner;
    private final Runnable onReset;
    private final Runnable onExit;

    MenuBuilder(JFrame owner, Runnable onReset, Runnable onExit)
    {
        this.owner = owner;
        this.onReset = onReset;
        this.onExit = onExit;
    }

    JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createFileMenu()
    {
        JMenu fileMenu = new JMenu(FILE_MENU_TEXT);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(FILE_MENU_DESCRIPTION);

        JMenuItem resetWindowsItem = new JMenuItem(RESET_WINDOWS_TEXT, KeyEvent.VK_R);
        resetWindowsItem.addActionListener((event) -> onReset.run());
        fileMenu.add(resetWindowsItem);

        JMenuItem exitItem = new JMenuItem(EXIT_TEXT, KeyEvent.VK_X);
        exitItem.addActionListener((event) -> onExit.run());
        fileMenu.add(exitItem);
        return fileMenu;
    }

    private JMenu createLookAndFeelMenu()
    {
        JMenu lookAndFeelMenu = new JMenu(LOOK_AND_FEEL_MENU_TEXT);
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(LOOK_AND_FEEL_MENU_DESCRIPTION);

        JMenuItem systemLookAndFeel = new JMenuItem(SYSTEM_LAF_TEXT, KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            applyLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            owner.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem(CROSS_PLATFORM_LAF_TEXT, KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            applyLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            owner.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);
        return lookAndFeelMenu;
    }

    private JMenu createTestMenu()
    {
        JMenu testMenu = new JMenu(TEST_MENU_TEXT);
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(TEST_MENU_DESCRIPTION);

        JMenuItem addLogMessageItem = new JMenuItem(ADD_LOG_MESSAGE_TEXT, KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> Logger.debug("Новая строка"));
        testMenu.add(addLogMessageItem);
        return testMenu;
    }

    private void applyLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(owner);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // ignore
        }
    }
}
