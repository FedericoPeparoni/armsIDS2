package ca.ids.abms.modules.reports2.common;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

import com.google.common.base.Preconditions;

/**
 * A BIRT task paired with a JDBC connection that cleans up after itself.
 *
 * This class contains a BIRT task and a JDBC connection, which will be closed
 * by this class'es "close" method. This class is used to avoid connection
 * leaks when a JDBC connection is allocated and assigned to a BIRT task.
 * 
 */
public class BirtCloseableRunAndRenderTaskWrapper implements Closeable {
    
    private final IRunAndRenderTask task;
    private final Connection connection;
    
    public BirtCloseableRunAndRenderTaskWrapper (final IRunAndRenderTask task) {
        this (task, null);
    }
    
    public BirtCloseableRunAndRenderTaskWrapper (final IRunAndRenderTask task, final Connection connection) {
        Preconditions.checkNotNull (task);
        this.task = task;
        this.connection = connection;
    }
    
    public IRunAndRenderTask task() {
        return task;
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (final SQLException x) {
                throw new IOException (x);
            }
        }
    }

}
