package eu.planlos.pretixtonextcloudintegrator.nextcloud;

import eu.planlos.pretixtonextcloudintegrator.nextcloud.model.NextcloudMeta;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.model.NextcloudUser;

import java.util.ArrayList;

public abstract class NextcloudTestDataUtility {

    protected NextcloudUser takenUser() {
        return new NextcloudUser(
                "kv-kraichgau-dname",
                "Display Name",
                "dname@example.com",
                new ArrayList<>(),
                true);
    }

    protected NextcloudMeta okMeta() {
        return new NextcloudMeta("200",
                200,
                "All fine",
                null,
                null);
    }
}