package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NextcloudUser {

    private String id;
    @JsonProperty("displayname")
    private String displayName;
    private String email;
    private List<String> groups;
}