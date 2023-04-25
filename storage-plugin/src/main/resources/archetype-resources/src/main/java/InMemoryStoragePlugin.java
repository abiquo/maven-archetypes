package ${package};

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abiquo.commons.model.enumerator.ConstraintKey;
import com.abiquo.commons.plugin.annotation.PluginMetadata;
import com.abiquo.commons.plugin.exception.HypervisorPluginError;
import com.abiquo.commons.plugin.exception.HypervisorPluginException;
import com.abiquo.ssm.model.Address;
import com.abiquo.ssm.model.ClientDetails;
import com.abiquo.ssm.model.Device;
import com.abiquo.ssm.model.Pool;
import com.abiquo.ssm.model.Volume;
import com.abiquo.ssm.plugin.Storage;
import com.google.common.base.Enums;
import com.google.common.base.Optional;

/**
 * In memory example storage plugin for the Abiquo storage manager.
 */
@PluginMetadata(type = "IN_MEMORY", // Unique name for this plugin (example: NETAPP, LVM)
    friendlyName = "generic iSCSI storage", // Fiendly name to show in the UI
    version = "4.0.0", supportedVersions = "4.0.0")
public class InMemoryStoragePlugin implements Storage<Device>
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
    public String getConstraint(final String key)
    {
        Optional<ConstraintKey> constraint = Enums.getIfPresent(ConstraintKey.class, key);
        if (!constraint.isPresent())
        {
            return null;
        }
        switch (constraint.get())
        {
            case STORAGE_DEFAULT_MANAGEMENT_PORT:
                return "3260";
            case STORAGE_DEFAULT_SERVICE_PORT:
                return "3260";
            case STORAGE_REQUIRES_AUTHENTICATION:
                return "false";
            default:
                break;
        }
        return null;
    }

    @Override
    public void validateConfiguration() throws IllegalStateException
    {
        // Plugin is properly configured
    }

    @Override
    public List<Volume> listVolumes(final Device device, final String poolProviderId)
        throws HypervisorPluginException
    {
        return new ArrayList<Volume>(volumes.values());
    }

    @Override
    public List<Volume> listVolumesAllTiers(final Device device) throws HypervisorPluginException
    {
        return new ArrayList<Volume>(volumes.values());
    }

    @Override
    public Volume getVolume(final Device device, final String poolProviderId, final String uuid)
        throws HypervisorPluginException
    {
        Volume volume = volumes.get(uuid);
        if (volume == null)
        {
            throw new HypervisorPluginException(HypervisorPluginError.NOT_FOUND,
                "Unexisting volume");
        }
        return volume;
    }

    @Override
    public Volume createVolume(final Device device, final String poolProviderId,
        final Volume volume) throws HypervisorPluginException
    {
        if (volumes.get(volume.getUuid()) != null)
        {
            throw new HypervisorPluginException(HypervisorPluginError.RESPONSE,
                "Volume already exists");
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
        Pool updatedPool = pools.get(poolProviderId);
        updatedPool.setUsedInMB(updatedPool.getUsedInMB() + created.getSizeInMB());
        updatedPool.setAvailableInMB(updatedPool.getSizeInMB() - updatedPool.getUsedInMB());

        return created;
    }

    @Override
    public void deleteVolume(final Device device, final String poolProviderId, final String uuid)
        throws HypervisorPluginException
    {
        if (volumes.get(uuid) == null)
        {
            throw new HypervisorPluginException(HypervisorPluginError.NOT_FOUND,
                "Unexisting volume");
        }

        Volume removed = volumes.remove(uuid);

        // Update the available size in the pool
        Pool updatedPool = pools.get(poolProviderId);
        updatedPool.setUsedInMB(updatedPool.getUsedInMB() - removed.getSizeInMB());
        updatedPool.setAvailableInMB(updatedPool.getSizeInMB() - updatedPool.getUsedInMB());
    }

    @Override
    public Volume resizeVolume(final Device device, final String poolProviderId,
        final Volume volume) throws HypervisorPluginException
    {
        if (volumes.get(volume.getUuid()) == null)
        {
            throw new HypervisorPluginException(HypervisorPluginError.NOT_FOUND,
                "Unexisting volume");
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
        Pool updatedPool = pools.get(poolProviderId);
        updatedPool.setUsedInMB(updatedPool.getUsedInMB() + sizeIncrement);
        updatedPool.setAvailableInMB(updatedPool.getSizeInMB() - updatedPool.getUsedInMB());

        return resize;
    }
}
