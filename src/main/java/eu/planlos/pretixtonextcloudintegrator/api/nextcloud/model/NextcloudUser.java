package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class NextcloudUser {

    private String id;
    @JsonProperty("displayname")
    private String displayName;
    private String email;
    private List<String> groups;
    private boolean enabled;
}