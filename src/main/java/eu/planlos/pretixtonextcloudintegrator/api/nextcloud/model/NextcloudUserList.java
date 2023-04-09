package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NextcloudUserList {
    private List<String> users;
}