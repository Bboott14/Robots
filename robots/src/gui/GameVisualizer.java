package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel
{
    private static final int REDRAW_INTERVAL_MS = 50;
    private static final int MODEL_UPDATE_INTERVAL_MS = 10;

    private final Timer timer = initTimer(); //инициализируем таймер, который будет использоваться для генерации событий перерисовки панели и обновления модели робота
    private final RobotModel model;
    
    private static Timer initTimer() 
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }
    
    public GameVisualizer(RobotModel model) 
    {
        this.model = model;
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent(); //запускаем таймер, который будет вызывать событие перерисовки панели каждые 50 миллисекунд
            }
        }, 0, REDRAW_INTERVAL_MS);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent(); //запускаем таймер, который будет вызывать событие обновления модели робота каждые 10 миллисекунд
            }
        }, 0, MODEL_UPDATE_INTERVAL_MS);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setTargetPosition(e.getPoint());
                repaint();
            }
        }); //добавляем обработчик событий мыши, который будет вызываться при клике на панели и устанавливать новую целевую позицию для робота
        setDoubleBuffered(true);
    }

    protected void setTargetPosition(Point p)
    {
        model.setTargetPosition(p);
    }
    
    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent()
    {
        model.update(MODEL_UPDATE_INTERVAL_MS);
    }
    
    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g; 
        double robotX = model.getPositionX();
        double robotY = model.getPositionY();
        double direction = model.getDirection();
        double targetX = model.getTargetX();
        double targetY = model.getTargetY();
        drawRobot(g2d, round(robotX), round(robotY), direction);
        drawTarget(g2d, round(targetX), round(targetY));
    }
    
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        int robotCenterX = x; 
        int robotCenterY = y;
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY); 
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }
    
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
