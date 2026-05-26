package test;

import gui.RobotModel;
import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

public class RobotModelSelfTest
{
    private static final double EPS = 1e-6;

    private static class CountingObserver implements Observer
    {
        private int count;
        private Object lastArg;

        @Override
        public void update(Observable o, Object arg)
        {
            count++;
            lastArg = arg;
        }
    }

    public static void main(String[] args)
    {
        testObserverNotifiedOnSetTarget();
        testObserverNotifiedOnUpdate();
        testTwoObserversNotified();
        System.out.println("RobotModelSelfTest: OK");
    }

    private static void testObserverNotifiedOnSetTarget()
    {
        RobotModel model = new RobotModel();
        CountingObserver observer = new CountingObserver();
        model.addObserver(observer);

        model.setTargetPosition(new Point(200, 300));

        assertTrue(observer.count == 1, "Expected one notification after setTargetPosition");
        assertTrue(observer.lastArg instanceof RobotModel.State, "Expected RobotModel.State in notification");
        RobotModel.State state = (RobotModel.State) observer.lastArg;
        assertClose(200.0, state.targetX, "targetX mismatch");
        assertClose(300.0, state.targetY, "targetY mismatch");
        assertClose(100.0, state.positionX, "positionX mismatch");
        assertClose(100.0, state.positionY, "positionY mismatch");
    }

    private static void testObserverNotifiedOnUpdate()
    {
        RobotModel model = new RobotModel();
        CountingObserver observer = new CountingObserver();
        model.addObserver(observer);

        model.update(10);

        assertTrue(observer.count == 1, "Expected one notification after update");
        assertTrue(observer.lastArg instanceof RobotModel.State, "Expected RobotModel.State in notification");
        RobotModel.State state = (RobotModel.State) observer.lastArg;
        assertClose(101.0, state.positionX, "positionX mismatch after update");
        assertClose(100.0, state.positionY, "positionY mismatch after update");
    }

    private static void testTwoObserversNotified()
    {
        RobotModel model = new RobotModel();
        CountingObserver observerOne = new CountingObserver();
        CountingObserver observerTwo = new CountingObserver();
        model.addObserver(observerOne);
        model.addObserver(observerTwo);

        model.update(10);

        assertTrue(observerOne.count == 1, "Expected first observer to be notified");
        assertTrue(observerTwo.count == 1, "Expected second observer to be notified");
    }

    private static void assertTrue(boolean condition, String message)
    {
        if (!condition)
        {
            throw new AssertionError(message);
        }
    }

    private static void assertClose(double expected, double actual, String message)
    {
        if (Math.abs(expected - actual) > EPS)
        {
            throw new AssertionError(message + ": expected=" + expected + " actual=" + actual);
        }
    }
}
