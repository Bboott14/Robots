package gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private static final String LOG_WINDOW_KEY = "logWindow";
    private static final String GAME_WINDOW_KEY = "gameWindow";
    private static final String COORDS_WINDOW_KEY = "coordsWindow";

    private final JDesktopPane desktopPane = new JDesktopPane();  //главная панель, на которой располагаются внутренние окна приложения 
    private RobotModel robotModel;
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private RobotCoordinatesWindow coordinatesWindow;
    private final WindowStateManager windowStateManager;
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane); //устанавливаем главную панель в качестве панели содержимого окна
        
        
        robotModel = new RobotModel();

        logWindow = createLogWindow(); //создаем окно для отображения логов приложения
        addWindow(logWindow);

        gameWindow = createGameWindow(robotModel); //создаем окно для отображения игрового поля и управления игрой
        addWindow(gameWindow);

        coordinatesWindow = createCoordinatesWindow(robotModel);
        addWindow(coordinatesWindow);

        windowStateManager = new WindowStateManager(this);
        windowStateManager.registerInternalFrame(LOG_WINDOW_KEY, logWindow);
        windowStateManager.registerInternalFrame(GAME_WINDOW_KEY, gameWindow);
        windowStateManager.registerInternalFrame(COORDS_WINDOW_KEY, coordinatesWindow);
        windowStateManager.restoreWindowState(); //восстанавливаем сохраненное состояние окон приложения

        MenuBuilder menuBuilder = new MenuBuilder(this, this::resetWindowState, this::exitRequested); //создаем объект MenuBuilder, который отвечает за создание меню приложения, передаем ему ссылки на методы для сброса состояния окон и выхода из приложения
        setJMenuBar(menuBuilder.createMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //устанавливаем поведение при закрытии окна - не закрывать окно автоматически, а обрабатывать это событие вручную, чтобы показать диалог подтверждения выхода из приложения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitRequested();
            }
        }); //добавляем слушатель событий окна, вызываетсмя при потытке закрыть окно
    }

    public static void configureLocale()
    {
        Locale ru = new Locale("ru", "RU"); //создаем объект Locale для русского языка, который будет использоваться для настройки локализации приложения
        Locale.setDefault(ru); // по умолчанию русская локализация
        JComponent.setDefaultLocale(ru); // устанавливаем русскую локализацию для компонентов Swing
        UIManager.getDefaults().setDefaultLocale(ru);
    }

    public static void configureRussianUiTexts()
    {
        UIManager.put("OptionPane.yesButtonText", "Да"); //настраиваем русские тексты для стандартных диалогов Swing 
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.okButtonText", "Ок");

        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.lookInLabelText", "Папка");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов");
        UIManager.put("FileChooser.upFolderToolTipText", "На уровень вверх");
        UIManager.put("FileChooser.homeFolderToolTipText", "Домашняя папка");
        UIManager.put("FileChooser.newFolderToolTipText", "Новая папка");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");

        UIManager.put("ColorChooser.okText", "Ок");
        UIManager.put("ColorChooser.cancelText", "Отмена");
        UIManager.put("ColorChooser.resetText", "Сброс");
    }
    
    protected LogWindow createLogWindow() //создаем окно для отображения логов приложения
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected GameWindow createGameWindow(RobotModel model) //создаем окно для отображения игрового поля и управления игрой
    {
        GameWindow gameWindow = new GameWindow(model); //создаем окно для отображения игрового поля и управления игрой
        gameWindow.setLocation(310,10);
        gameWindow.setSize(400,  400);
        return gameWindow;
    }

    protected RobotCoordinatesWindow createCoordinatesWindow(RobotModel model)
    {
        RobotCoordinatesWindow coordinatesWindow = new RobotCoordinatesWindow(model);
        coordinatesWindow.setLocation(310, 420);
        coordinatesWindow.setSize(400, 120);
        return coordinatesWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }

    private void exitRequested()
    {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Выйти из приложения?",
            "Подтверждение выхода",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE); //показываем диалоговое окно с вопросом о подтверждении выхода из приложения и получаем выбор пользователя (да или нет)
        if (choice == JOptionPane.YES_OPTION)
        {
            windowStateManager.saveWindowState(); //сохраняем состояние окон приложения 
            dispose();
            System.exit(0);
        }
    }

    private void resetWindowState() //сбрасываем состояние окон приложения к значениям по умолчанию и удаляем сохраненный файл состояния
    {
        resetMainFrameToDefaults();
        resetInternalFrameToDefaults(logWindow, 10, 10, 300, 800);
        setMinimumSize(logWindow.getSize());
        resetInternalFrameToDefaults(gameWindow, 310, 10, 400, 400);
        resetInternalFrameToDefaults(coordinatesWindow, 310, 420, 400, 120);
        windowStateManager.clearSavedState();
    }

    private void resetMainFrameToDefaults()
    {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setExtendedState(Frame.NORMAL);
        setBounds(inset, inset,
            screenSize.width  - inset * 2,
            screenSize.height - inset * 2);
    }

    private void resetInternalFrameToDefaults(JInternalFrame frame, int x, int y, int w, int h) 
    {
        try
        {
            frame.setIcon(false);
            frame.setMaximum(false);
        }
        catch (PropertyVetoException e)
        {
            // ignore
        }
        frame.setBounds(x, y, w, h);
        frame.setVisible(true); 
    }
}
