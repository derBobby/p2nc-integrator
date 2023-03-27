package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NextcloudUserList {
    private List<String> users;
}