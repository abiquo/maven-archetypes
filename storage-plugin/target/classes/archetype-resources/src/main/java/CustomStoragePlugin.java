package ${package};

import java.util.List;

import com.abiquo.ssm.exception.PluginException;
import com.abiquo.ssm.model.Device;
import com.abiquo.ssm.model.Pool;
import com.abiquo.ssm.model.Volume;
import com.abiquo.ssm.plugin.AbstractStoragePlugin;
import com.abiquo.ssm.plugin.annotations.Plugin;

/**
 * Custom storage plugin for the Abiquo storage manager.
 */
@Plugin(type = "CHANGEME!", // Unique name for this plugin (example: NETAPP, LVM)
defaultIscsiPort = 3260, // Port used to connect to the device using the iSCSI protocol
defaultManagementPort = 0, // CHANGE! Port used to connect to teh device administration API
requiresAuthentication = true // Does the management APi require authentication?
)
public class CustomStoragePlugin extends AbstractStoragePlugin
{

    @Override
    public List<Pool> listPools(final Device device) throws PluginException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pool getPool(final Device device, final String poolName) throws PluginException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Volume> listVolumes(final Device device, final Pool pool) throws PluginException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Volume getVolume(final Device device, final Pool pool, final String uuid)
        throws PluginException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Volume createVolume(final Device device, final Pool pool, final Volume volume)
        throws PluginException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteVolume(final Device device, final Pool pool, final String uuid)
        throws PluginException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Volume resizeVolume(final Device device, final Pool pool, final Volume volume)
        throws PluginException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String addInitiatorToPool(final Device device, final Pool pool, final String ostype,
        final String initiatorIQN, final String uuid) throws PluginException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeInitiatorFromPool(final Device device, final Pool pool, final String ostype,
        final String initiatorIQN) throws PluginException
    {
        throw new UnsupportedOperationException();
    }

}
