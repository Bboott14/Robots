package log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Состояние:
 * 1. Утечка слушателей устраняется при отписке окна при закрытии.
 * 2. Хранилище лога ограничено и вытесняет старые записи.
 */
public class LogWindowSource
{
    private final int m_iQueueLength;
    private final Object m_bufferLock = new Object();
    private final LogEntryBuffer m_buffer;
    private final ArrayList<LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;
    
    public LogWindowSource(int iQueueLength) 
    {
        m_iQueueLength = Math.max(1, iQueueLength);
        m_buffer = new LogEntryBuffer(m_iQueueLength);
        m_listeners = new ArrayList<LogChangeListener>();
    }
    
    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }
    
    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }
    
    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        synchronized (m_bufferLock)
        {
            m_buffer.add(entry);
        }
        LogChangeListener [] activeListeners = m_activeListeners;
        if (activeListeners == null)
        {
            synchronized (m_listeners)
            {
                if (m_activeListeners == null)
                {
                    activeListeners = m_listeners.toArray(new LogChangeListener [0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }
    
    public int size()
    {
        synchronized (m_bufferLock)
        {
            return m_buffer.size();
        }
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        synchronized (m_bufferLock)
        {
            return m_buffer.rangeSnapshot(startFrom, count);
        }
    }

    public Iterable<LogEntry> all()
    {
        synchronized (m_bufferLock)
        {
            return m_buffer.rangeSnapshot(0, m_buffer.size());
        }
    }

    private static final class LogEntryBuffer
    {
        private final LogEntry[] entries;
        private int start;
        private int size;

        LogEntryBuffer(int capacity)
        {
            entries = new LogEntry[capacity];
        }

        void add(LogEntry entry)
        {
            int capacity = entries.length;
            if (size < capacity)
            {
                int index = (start + size) % capacity;
                entries[index] = entry;
                size++;
                return;
            }
            entries[start] = entry;
            start = (start + 1) % capacity;
        }

        int size()
        {
            return size;
        }

        Iterable<LogEntry> rangeSnapshot(int startFrom, int count)
        {
            if (count <= 0 || startFrom < 0 || startFrom >= size)
            {
                return Collections.emptyList();
            }
            int indexTo = Math.min(startFrom + count, size);
            ArrayList<LogEntry> snapshot = new ArrayList<LogEntry>(indexTo - startFrom);
            int capacity = entries.length;
            for (int i = startFrom; i < indexTo; i++)
            {
                snapshot.add(entries[(start + i) % capacity]);
            }
            return snapshot;
        }
    }
}
