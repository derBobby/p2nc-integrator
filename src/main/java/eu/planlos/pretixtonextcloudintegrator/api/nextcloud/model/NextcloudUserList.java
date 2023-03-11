package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NextcloudUserList {

    public NextcloudUserList() {
        super();
    }

    private List<String> users;
}