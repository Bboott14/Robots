package gui;

import java.awt.Point;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class RobotModel extends Observable
{
    private static final int UPDATE_INTERVAL_MS = 10;
    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;
    private static final double TARGET_EPSILON = 0.5;
    private static final double ANGLE_EPSILON = 1e-6;

    private double positionX = 100;
    private double positionY = 100;
    private double direction = 0;

    private double targetX = 150;
    private double targetY = 100;
    private Timer timer;

    public static final class State
    {
        public final double positionX;
        public final double positionY;
        public final double direction;
        public final double targetX;
        public final double targetY;

        public State(double positionX, double positionY, double direction, double targetX, double targetY)
        {
            this.positionX = positionX;
            this.positionY = positionY;
            this.direction = direction;
            this.targetX = targetX;
            this.targetY = targetY;
        }
    }

    public synchronized double getPositionX()
    {
        return positionX;
    }

    public synchronized double getPositionY()
    {
        return positionY;
    }

    public synchronized double getDirection()
    {
        return direction;
    }

    public synchronized double getTargetX()
    {
        return targetX;
    }

    public synchronized double getTargetY()
    {
        return targetY;
    }

    public synchronized void start()
    {
        if (timer != null)
        {
            return;
        }
        timer = new Timer("robot-model-update", true);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                update(UPDATE_INTERVAL_MS);
            }
        }, 0, UPDATE_INTERVAL_MS);
    }

    public synchronized void stop()
    {
        if (timer == null)
        {
            return;
        }
        timer.cancel();
        timer = null;
    }

    public State getState()
    {
        synchronized (this)
        {
            return new State(positionX, positionY, direction, targetX, targetY);
        }
    }

    public void setTargetPosition(Point p) //устанавливает новую целевую позицию для робота, которая будет использоваться при обновлении его состояния
    {
        State snapshot;
        synchronized (this)
        {
            targetX = p.x;
            targetY = p.y;
            snapshot = new State(positionX, positionY, direction, targetX, targetY);
        }
        notifyChange(snapshot);
    }

    public void update(double duration)
    {
        double x;
        double y;
        double dir;
        double tx;
        double ty;
        synchronized (this)
        {
            x = positionX;
            y = positionY;
            dir = direction;
            tx = targetX;
            ty = targetY;
        }

        double distance = distance(tx, ty, x, y);
        if (distance < TARGET_EPSILON)
        {
            return;
        }

        double angleToTarget = angleTo(x, y, tx, ty);
        double angleDiff = shortestAngleDiff(dir, angleToTarget);
        double angularVelocity = 0;
        if (Math.abs(angleDiff) > ANGLE_EPSILON)
        {
            angularVelocity = Math.signum(angleDiff) * MAX_ANGULAR_VELOCITY;
        }

        double velocity = MAX_VELOCITY;
        double newX;
        double newY;
        if (Math.abs(angularVelocity) < ANGLE_EPSILON)
        {
            newX = x + velocity * duration * Math.cos(dir);
            newY = y + velocity * duration * Math.sin(dir);
        }
        else
        {
            newX = x + velocity / angularVelocity *
                (Math.sin(dir + angularVelocity * duration) - Math.sin(dir));
            newY = y - velocity / angularVelocity *
                (Math.cos(dir + angularVelocity * duration) - Math.cos(dir));
        }
        double newDirection = normalizeAngle(dir + angularVelocity * duration);

        synchronized (this)
        {
            positionX = newX;
            positionY = newY;
            direction = newDirection;
        }
        notifyChange(new State(newX, newY, newDirection, tx, ty));
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return normalizeAngle(Math.atan2(diffY, diffX));
    }

    private static double normalizeAngle(double angle)
    {
        while (angle < 0)
        {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI)
        {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static double shortestAngleDiff(double from, double to) //вычисляет кратчайшую разницу между двумя углами (from и to), учитывая циклическую природу углов, и возвращает результат в диапазоне от -π до π
    {
        double diff = to - from;
        return Math.atan2(Math.sin(diff), Math.cos(diff));
    }

    private void notifyChange(State state)
    {
        setChanged();
        notifyObservers(state);
    }
}
