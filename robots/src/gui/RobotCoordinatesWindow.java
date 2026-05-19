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

    public RobotCoordinatesWindow(RobotModel model)
    {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        this.coordinatesLabel = new JLabel();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordinatesLabel, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        updateCoordinates();
        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg)
    {
        EventQueue.invokeLater(this::updateCoordinates);
    }

    private void updateCoordinates()
    {
        double x = model.getPositionX();
        double y = model.getPositionY();
        coordinatesLabel.setText(String.format(Locale.ROOT, "X: %.2f, Y: %.2f", x, y));
    }
}
