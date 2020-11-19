package permission;

// Project imports:
//      CryptoProfile
//      SignAssignment
//      InfocryptCrypto
//      AbstractCustomPermission
//      CustomPermissionContext
//      AccessMode
//      DateUtilities

import org.apache.commons.lang.ObjectUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.containsAny;

/**
 * Java doc with business comment
 */
public class CustomPermission extends AbstractCustomPermission {

    // Trustee roles
    private final List<String> fullAccessRoles;

    public CustomPermission() {
        this.fullAccessRoles = Collections.emptyList();
    }

    public CustomPermission(List<String> fullAccessRoles) {
        this.fullAccessRoles = fullAccessRoles != null? fullAccessRoles : Collections.emptyList();
    }

    /**
     * Java doc with business comment
     *
     * @param ignored Current AccessMode value
     * @param context Handler context
     *
     * @return User access mode
     */
    @Override
    public AccessMode getAccessMode(AccessMode ignored, CustomPermissionContext context) {
        // FULL_ACCESS for Trustee with Infocrypt token
        if (containsAny(fullAccessRoles, context.getFuncRoleNames()) && isInfocrypt(context.getCryptoProfiles())) {
            return AccessMode.FULL_ACCESS;
        }

        // FULL_ACCESS for Individual Executive Agency
        if (isIndividualExecutiveAgency(context)) {
            return AccessMode.FULL_ACCESS;
        }

        return AccessMode.READ_ONLY;
    }

    /**
     * Java doc with business comment
     *
     * @param context Handler context
     *
     * @return <tt>true</tt> for Individual Executive Agency
     */
    private static boolean isIndividualExecutiveAgency(CustomPermissionContext context) {
        final Date today = new Date();

        for (CryptoProfile cryptoProfile : context.getCryptoProfiles()) {
            boolean isUsed      = cryptoProfile.getCryptoType().isUsed();
            boolean isAllowSign = cryptoProfile.isAllowSign();
            boolean isInfocrypt = InfocryptCryptoProvider.CRYPTO_TYPE.equals(cryptoProfile.getCryptoType().getName());

            if (isUsed && isAllowSign && isInfocrypt) {
                for (SignAssignment signAssignment : cryptoProfile.getSignAssignments().values()) {
                    boolean todayIsInSignAssignmentPeriod = signAssignment.isUnlimited() ||
                            DateUtilities.isDateInSoftRange(
                                    today,
                                    signAssignment.getPeriodFrom(),
                                    signAssignment.getPeriodTo()
                            );
                    boolean isForAnyContextOrg = isForAnyContextOrg(signAssignment, context.getOrgs());

                    if (todayIsInSignAssignmentPeriod && isForAnyContextOrg && signAssignment.isIndividualExecutiveAgency()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Java doc with business comment
     *
     * @param cryptoProfiles List of crypto-profiles
     *
     * @return <tt>true</tt> for Infocrypt
     */
    private static boolean isInfocrypt(List<CryptoProfile> cryptoProfiles) {
        for (CryptoProfile cryptoProfile : cryptoProfiles) {
            if (InfocryptCrypto.CRYPTO_TYPE.equals(cryptoProfile.getCryptoType().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param signAssignment Sign assignment
     * @param contextOrgs    List of organizations from context
     *
     * @return <tt>true</tt>, if sign assignment assigned to at least one organization from the context
     */
    private static boolean isForAnyContextOrg(SignAssignment signAssignment, List<CustomPermissionContext.PermissionOrg> contextOrgs) {
        for (CustomPermissionContext.PermissionOrg permOrg : contextOrgs) {
            if (ObjectUtils.equals(permOrg.getOrgId(), signAssignment.getOrg().getId())) {
                return true;
            }
        }
        return false;
    }
}
