package ${package};

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.abiquo.backup.model.BackupResult;
import com.abiquo.backup.model.BackupResult.RestoreResult;
import com.abiquo.backup.model.BackupSchedule;
import com.abiquo.backup.model.OnDemandBackupOptions;
import com.abiquo.backup.model.VirtualMachineBackupRestoreInfo;
import com.abiquo.backup.plugin.BackupScheduling;
import com.abiquo.backup.plugin.annotation.BackupConnectionMetadata;
import com.abiquo.backup.plugin.exception.BackupPluginException;
import com.abiquo.commons.plugin.annotation.PluginMetadata;
import com.abiquo.commons.plugin.enumerator.FieldConstraint;

// This Annotation indicates which connection data fields are required or not.
// Valid values are FieldConstraint enum values. 
@BackupConnectionMetadata(endpoint = FieldConstraint.MANDATORY, credentials = FieldConstraint.MANDATORY)

// This annotation specifies the metadta of the plugin
@PluginMetadata(version = "${version}", // The version of the Custom Backup Plugin
type = "${artifactId}", // The type that identifies the Custom Backup Plugin in the Abiquo platform
friendlyName = "Custom Backup Plugin", // The friendly name to show to the user 
supportedVersions = {"1.0", "1.5"}) // The list of the backup technology supported by the plugin

// You can use com.abiquo.hypervisor.plugin.UnsupportedOperation annotation at method level to indicate
// which operations are note supported. Is recommended to implemnt that methos throwing an UnsupportedException. 
public class CustomBackupPlugin implements BackupScheduling<CustomBackupConnection>
{

    @Override
    public void validateConfiguration() throws IllegalStateException
    {
        // Check specific plugin configuration if needed an throw an IllegalStateException to skip plugin load if missconfigured.
    }

    // Schedule Configuration //

    /**
     * This method is called when a new virtual machine is deployed and it contains backup configuration.
     * <p>
     * So the virtual macine is already deployed at this point and is the time to configure the backup schedule configuration.
     */
    @Override
    public void applySchedule(final CustomBackupConnection connection,
        final VirtualMachineBackupRestoreInfo virtualMachineIdentifier,
        final BackupSchedule backupSchedule) throws BackupPluginException
    {
        // Configure the given virtual machine to be backed up using the given schedule configuration
    }

    /**
     * This method is called when a virtual machine is reconfigured and the backup schedule data is changed.
     * <p>
     * So the changes are already paplied in the virtual machine is time to change/add/remove the backup schedule configuration.
     */
    @Override
    public void applyNewSchedule(final CustomBackupConnection connection,
        final VirtualMachineBackupRestoreInfo virtualMachineIdentifier,
        final BackupSchedule oldBackupSchedule, final BackupSchedule newBackupSchedule)
        throws BackupPluginException
    {
        // Configure the given virtual machine to be backed up using the given new schedule configuration instead of the old one 
    }

    /**
    * This method is called when a virtual machine is going to be undeployed and it contains some backup configuration.
    * <p>
    * So the virtual machine still exist but the backup schedule configuration need to be removed before the virtual machine is undeployed.
    */
    @Override
    public void removeSchedule(final CustomBackupConnection connection,
        final VirtualMachineBackupRestoreInfo virtualMachineIdentifier)
        throws BackupPluginException
    {
        // Remove the schedule configuration of the given virtual machine because is going to be undeployed
    }

    // On demand actions //

    /**
     * This method is called for the on demand backup action.
     * <p>
     * So the virtual machine exists in the hypervisor and user request a backup.
     */
    @Override
    public BackupResult executeBackup(final CustomBackupConnection connection,
        final VirtualMachineBackupRestoreInfo virtualMachineIdentifier,
        final OnDemandBackupOptions options) throws BackupPluginException
    {
        // Execute a backup of the given virtualmachine
        return new BackupResult();
    }

    /**
     * This method is called when user request a restore of an existing backup.
     * <p>
     * The virtual machine could exist or not in the hypervisor.
     */
    @Override
    public RestoreResult executeRestore(final CustomBackupConnection connection,
        final VirtualMachineBackupRestoreInfo virtualMachineIdentifier, final String backupId)
        throws BackupPluginException
    {
        // Execute a restore of the given virtual machine and backup
        return new RestoreResult();
    }

    // Backup and restore results //

    /**
     * This method is called by a periodic check in Abiquo API in order to validate those backup results that
     * should be expired according to their expiration date field.
     */
    @Override
    public boolean isBackupExpired(final CustomBackupConnection connection, String backupId)
        throws BackupPluginException
    {
        // Return true if the backup correspongind to the given 'backupId' is expired.
        return false;
    }

    /**
     * This method is called by a periodic check in Abiquo API when a result still IN_PROGRESS or UNKNOWN state.
     */
    @Override
    public BackupResult getBackupResult(final CustomBackupConnection connection, String backupId)
        throws BackupPluginException
    {
        // Return the backup corresponding to the given id or null.
        return new BackupResult();
    }

    /**
     * This method is called by a periodic check in Abiquo API when a result still IN_PROGRESS or UNKNOWN state.
     */
    @Override
    public RestoreResult getRestoreResult(final CustomBackupConnection connection, String restoreId)
        throws BackupPluginException
    {
        // Return the restore result corresponding to the given id or null
        return new RestoreResult();
    }

    /**
     * This method is called for the Abiquo API periodical task in order to save the new backup results in the Abiquo database.
     */
    @Override
    public List<BackupResult> listNewBackupResults(final CustomBackupConnection connection,
        final Optional<ZonedDateTime> startDateFromLastProcessedResult)
        throws BackupPluginException
    {
        // Retrieve the backup results to store them in the Abiquo database and show them to the user.
        // Use startDateFromLastProcessedResult parameter to skip to process results already processed if apply.
        return new ArrayList<>();
    }

    /**
     * This method is called for the Abiquo API periodical task in order to save the new restore results in the Abiquo database.
     */
    @Override
    public List<RestoreResult> listNewRestoreResults(final CustomBackupConnection connection,
        final Optional<ZonedDateTime> startDateFromLastProcessedResult)
        throws BackupPluginException
    {
        // Retrieve the restore results to store them in the Abiquo database and show them to the user.
        // Use startDateFromLastProcessedResult parameter to skip to process results already processed if apply.
        return new ArrayList<>();
    }

}
