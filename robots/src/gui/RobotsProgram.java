package gui;

import java.awt.Frame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram
{
    public static void main(String[] args) {
      MainApplicationFrame.configureLocale(); //настраиваем локализацию приложения на русский, чтобы все компоненты и диалоги отображались на русском языке
      try {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); //стиль компонентов Swing (кнопки, меню, окна)
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      MainApplicationFrame.configureRussianUiTexts();
      SwingUtilities.invokeLater(() -> { //запускаем код, который создает и отображает окно, в потоке обработки событий Swing (Event Dispatch Thread)
        MainApplicationFrame frame = new MainApplicationFrame(); //создаем экземпляр главного окна приложения
        if (!WindowStateManager.hasSavedWindowState())
        {
          frame.pack();
          frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        frame.setVisible(true);
      });
    }}
