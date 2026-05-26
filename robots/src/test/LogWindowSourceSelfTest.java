package test;

import java.util.ArrayList;
import log.LogEntry;
import log.LogLevel;
import log.LogWindowSource;

public class LogWindowSourceSelfTest
{
    public static void main(String[] args)
    {
        testBufferCapacity();
        testRangeSnapshot();
        testAllSnapshot();
        System.out.println("LogWindowSourceSelfTest: OK");
    }

    private static void testBufferCapacity()
    {
        LogWindowSource source = new LogWindowSource(3);
        source.append(LogLevel.Debug, "one");
        source.append(LogLevel.Debug, "two");
        source.append(LogLevel.Debug, "three");
        source.append(LogLevel.Debug, "four");

        assertTrue(source.size() == 3, "Expected size 3 after overflow");
        ArrayList<String> messages = toMessages(source.all());
        assertTrue(messages.size() == 3, "Expected 3 messages in all() snapshot");
        assertTrue("two".equals(messages.get(0)), "Expected oldest to be 'two'");
        assertTrue("three".equals(messages.get(1)), "Expected middle to be 'three'");
        assertTrue("four".equals(messages.get(2)), "Expected newest to be 'four'");
    }

    private static void testRangeSnapshot()
    {
        LogWindowSource source = new LogWindowSource(5);
        source.append(LogLevel.Debug, "a");
        source.append(LogLevel.Debug, "b");
        source.append(LogLevel.Debug, "c");
        source.append(LogLevel.Debug, "d");
        source.append(LogLevel.Debug, "e");

        ArrayList<String> range = toMessages(source.range(1, 3));
        assertTrue(range.size() == 3, "Expected range size 3");
        assertTrue("b".equals(range.get(0)), "Expected range[0] = b");
        assertTrue("c".equals(range.get(1)), "Expected range[1] = c");
        assertTrue("d".equals(range.get(2)), "Expected range[2] = d");
    }

    private static void testAllSnapshot()
    {
        LogWindowSource source = new LogWindowSource(2);
        source.append(LogLevel.Debug, "x");
        Iterable<LogEntry> snapshot = source.all();
        source.append(LogLevel.Debug, "y");
        source.append(LogLevel.Debug, "z");

        ArrayList<String> messages = toMessages(snapshot);
        assertTrue(messages.size() == 1, "Snapshot should keep original size");
        assertTrue("x".equals(messages.get(0)), "Snapshot should keep original content");
    }

    private static ArrayList<String> toMessages(Iterable<LogEntry> entries)
    {
        ArrayList<String> messages = new ArrayList<String>();
        for (LogEntry entry : entries)
        {
            messages.add(entry.getMessage());
        }
        return messages;
    }

    private static void assertTrue(boolean condition, String message)
    {
        if (!condition)
        {
            throw new AssertionError(message);
        }
    }
}
