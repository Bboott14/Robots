package gui;

import java.awt.Frame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram
{
    public static void main(String[] args) {
      try {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); //стиль компонентов Swing (кнопки, меню, окна)
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      SwingUtilities.invokeLater(() -> { //запускаем код, который создает и отображает окно, в потоке обработки событий Swing (Event Dispatch Thread)
        MainApplicationFrame frame = new MainApplicationFrame(); //создаем экземпляр главного окна приложения
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
      });
    }}
