package ${package};

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.abiquo.backup.model.BackupResult;
import com.abiquo.backup.model.BackupResult.RestoreResult;
import com.abiquo.backup.model.BackupSchedule;
import com.abiquo.backup.model.OnDemandBackupOptions;
import com.abiquo.backup.model.VirtualMachineIdentifierWithDeployInfo;
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

    @Override
    public void applySchedule(final CustomBackupConnection connection,
        final VirtualMachineIdentifierWithDeployInfo virtualMachineIdentifier,
        final BackupSchedule backupSchedule) throws BackupPluginException
    {
        // Configure the given virtual machine to be backed up using the given schedule configuration
    }

    @Override
    public void applySchedule(final CustomBackupConnection connection,
        final VirtualMachineIdentifierWithDeployInfo virtualMachineIdentifier,
        final BackupSchedule oldBackupSchedule, final BackupSchedule newBackupSchedule)
        throws BackupPluginException
    {
        // Configure the given virtual machine to be backed up using the given new schedule configuration instead of the old one 
    }

    @Override
    public void removeSchedule(final CustomBackupConnection connection,
        final VirtualMachineIdentifierWithDeployInfo virtualMachineIdentifier)
        throws BackupPluginException
    {
        // Remove the schedule configuration of the given virtual machine because is going to be undeployed
    }

    // On demand actions //

    @Override
    public BackupResult executeBackup(final CustomBackupConnection connection,
        final VirtualMachineIdentifierWithDeployInfo virtualMachineIdentifier,
        final OnDemandBackupOptions options) throws BackupPluginException
    {
        // Execute a backup of the given virtualmachine
        return new BackupResult();
    }

    @Override
    public RestoreResult executeRestore(final CustomBackupConnection connection,
        final VirtualMachineIdentifierWithDeployInfo virtualMachineIdentifier, final String backupId)
        throws BackupPluginException
    {
        // Execute a restore of the given virtual machine and backup
        return new RestoreResult();
    }

    // Backup and restore results //

    @Override
    public List<BackupResult> listResults(final CustomBackupConnection connection,
        final ZonedDateTime lastCheckDate, final Set<String> inProgressResultProviderIds)
        throws BackupPluginException
    {
        // Retrieve the backup and restore results to store them in the Abiquo database and show them to the user.
        // Use lastCheckDate parametter to skip to process results already processed if apply.
        // Use inProgressResultProviderIds to check again the state of these results and return them.
        return new ArrayList<>();
    }

    @Override
    public List<String> getExpiredResults(final CustomBackupConnection connection,
        final List<String> providerIds) throws BackupPluginException
    {
        // Check the given results and return the expired ones.
        return new ArrayList<>();
    }

}
