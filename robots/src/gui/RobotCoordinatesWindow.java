package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RobotCoordinatesWindow extends JInternalFrame implements Observer
{
    private final RobotModel model;
    private final JLabel coordinatesLabel;

    public RobotCoordinatesWindow(RobotModel model) //создает внутреннее окно для отображения координат робота, принимает модель робота в качестве аргумента и добавляет себя в качестве наблюдателя за изменениями модели
    {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        this.coordinatesLabel = new JLabel();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordinatesLabel, BorderLayout.CENTER);
        getContentPane().add(panel); //добавляем панель с меткой для отображения координат в содержимое окна
        pack();

        updateCoordinates();//обновляем отображаемые координаты при создании окна
        model.addObserver(this);//регистрируем окно в качестве наблюдателя за моделью робота, чтобы получать уведомления об изменениях координат и обновлять отображение
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (arg instanceof RobotModel.State)
        {
            RobotModel.State state = (RobotModel.State) arg;
            EventQueue.invokeLater(() -> updateCoordinates(state));
        }
        else
        {
            EventQueue.invokeLater(this::updateCoordinates);
        }
    }

    private void updateCoordinates()
    {
        RobotModel.State state = model.getState();
        updateCoordinates(state);
    }

    private void updateCoordinates(RobotModel.State state)
    {
        coordinatesLabel.setText(String.format(Locale.ROOT, "X: %.2f, Y: %.2f",
            state.positionX, state.positionY));
    }
}
