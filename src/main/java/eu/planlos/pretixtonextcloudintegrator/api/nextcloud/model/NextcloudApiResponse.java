package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NextcloudApiResponse<T> {
    private NextcloudMeta meta;
    private T data;
}