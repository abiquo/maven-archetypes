package ${package};

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abiquo.ssm.exception.PluginError;
import com.abiquo.ssm.exception.PluginException;
import com.abiquo.ssm.model.Address;
import com.abiquo.ssm.model.ClientDetails;
import com.abiquo.ssm.model.Device;
import com.abiquo.ssm.model.Pool;
import com.abiquo.ssm.model.Volume;
import com.abiquo.ssm.plugin.AbstractStoragePlugin;
import com.abiquo.ssm.plugin.annotations.Plugin;

/**
 * In memory example storage plugin for the Abiquo storage manager.
 */
@Plugin(type = "IN_MEMORY", // Unique name for this plugin (example: NETAPP, LVM)
defaultManagementPort = 80, // CHANGE! Port used to connect to teh device administration API
defaultServicePort = 3260, // Port used by hypervisors to connect to the volumes in the device (iSCSI port, etc)
requiresAuthentication = false // Does the management APi require authentication?
)
public class InMemoryStoragePlugin extends AbstractStoragePlugin
{
    private Map<String, Volume> volumes = new HashMap<String, Volume>();

    private Map<String, Pool> pools = new HashMap<String, Pool>();

    public InMemoryStoragePlugin()
    {
        // Add a default storage pool
        Pool pool = new Pool();
        pool.setName("In Memory");
        pool.setType("ISCSI");
        pool.setSizeInMB(51200L); // 50 GB
        pool.setUsedInMB(25600L); // 25 GB
        pool.setAvailableInMB(25600L); // 25 GB

        pools.put(pool.getName(), pool);
    }
    
    @Override
    public void validateConfiguration() throws IllegalStateException
    {
        // Plugin is properly configured
    }

    @Override
    public List<Pool> listPools(final Device device) throws PluginException
    {
        return new ArrayList<Pool>(pools.values());
    }

    @Override
    public Pool getPool(final Device device, final String poolName) throws PluginException
    {
        Pool pool = pools.get(poolName);
        if (pool == null)
        {
            throw new PluginException(PluginError.RESOURCE_NOT_FOUND, "Unexisting pool");
        }
        return pool;
    }

    @Override
    public List<Volume> listVolumes(final Device device, final Pool pool) throws PluginException
    {
        return new ArrayList<Volume>(volumes.values());
    }

    @Override
    public Volume getVolume(final Device device, final Pool pool, final String uuid)
        throws PluginException
    {
        Volume volume = volumes.get(uuid);
        if (volume == null)
        {
            throw new PluginException(PluginError.RESOURCE_NOT_FOUND, "Unexisting volume");
        }
        return volume;
    }

    @Override
    public Volume createVolume(final Device device, final Pool pool, final Volume volume)
        throws PluginException
    {
        if (volumes.get(volume.getUuid()) != null)
        {
            throw new PluginException("Volume already exists");
        }

        Volume created = new Volume();
        created.setUuid(volume.getUuid());
        created.setSizeInMB(volume.getSizeInMB());
        created.setAvailableInMB(volume.getSizeInMB());
        created.setUsedInMB(0L);
        
        created.setAddress(Address.iscsi(device.getServiceIp(), device.getServicePort(),
            "iqn.1993-08.org.debian:01:b22bb69c97d3", 0));

        volumes.put(created.getUuid(), created);

        // Update the available size in the pool
        Pool updatedPool = pools.get(pool.getName());
        updatedPool.setUsedInMB(updatedPool.getUsedInMB() + created.getSizeInMB());
        updatedPool.setAvailableInMB(updatedPool.getSizeInMB() - updatedPool.getUsedInMB());

        return created;
    }

    @Override
    public void deleteVolume(final Device device, final Pool pool, final String uuid)
        throws PluginException
    {
        if (volumes.get(uuid) == null)
        {
            throw new PluginException(PluginError.RESOURCE_NOT_FOUND, "Unexisting volume");
        }

        Volume removed = volumes.remove(uuid);

        // Update the available size in the pool
        Pool updatedPool = pools.get(pool.getName());
        updatedPool.setUsedInMB(updatedPool.getUsedInMB() - removed.getSizeInMB());
        updatedPool.setAvailableInMB(updatedPool.getSizeInMB() - updatedPool.getUsedInMB());
    }

    @Override
    public Volume resizeVolume(final Device device, final Pool pool, final Volume volume)
        throws PluginException
    {
        if (volumes.get(volume.getUuid()) == null)
        {
            throw new PluginException(PluginError.RESOURCE_NOT_FOUND, "Unexisting volume");
        }

        Volume original = volumes.get(volume.getUuid());
        Volume resize = new Volume();
        resize.setUuid(original.getUuid());
        resize.setAddress(original.getAddress());
        // Update size
        resize.setSizeInMB(volume.getSizeInMB());
        resize.setAvailableInMB(volume.getSizeInMB());
        resize.setUsedInMB(0L);

        volumes.put(volume.getUuid(), resize);

        long sizeIncrement = volume.getSizeInMB() - original.getSizeInMB();

        // Update the available size in the pool
        Pool updatedPool = pools.get(pool.getName());
        updatedPool.setUsedInMB(updatedPool.getUsedInMB() + sizeIncrement);
        updatedPool.setAvailableInMB(updatedPool.getSizeInMB() - updatedPool.getUsedInMB());

        return resize;
    }

    @Override
    public Address grantAccess(final Device device, final Pool pool, final String volumeUuid,
        final ClientDetails clientDetails) throws PluginException
    {
        // Validate pool and volume existance
        getPool(device, pool.getName());
        Volume volume = getVolume(device, pool, volumeUuid);

        return volume.getAddress();
    }

    @Override
    public void revokeAccess(final Device device, final Pool pool, final String volumeUuid,
        final ClientDetails clientDetails) throws PluginException
    {
        // Validate pool and volume existance
        getPool(device, pool.getName());
        getVolume(device, pool, volumeUuid);

        // Revoke access
    }
}
