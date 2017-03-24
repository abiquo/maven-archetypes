package ${package};

import com.abiquo.commons.model.ConnectionData;
import com.abiquo.commons.plugin.IConnection;
import com.abiquo.commons.plugin.exception.ComputeException;

public class CustomBackupConnection implements IConnection
{

    @Override
    public void connect(final ConnectionData connectionData) throws ComputeException
    {
        // Implement connect method to your backup api
    }

    @Override
    public void disconnect() throws ComputeException
    {
        // Implement disconnect method from your backup api
    }

}
