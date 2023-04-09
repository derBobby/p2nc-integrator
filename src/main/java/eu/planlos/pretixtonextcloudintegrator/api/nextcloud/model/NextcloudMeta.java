package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NextcloudMeta {

    private String status;
    @JsonProperty("statuscode")
    private int statusCode;
    private String message;
    private String totalitems;
    private String itemsperpage;

    public int getTotalItemsInt() {
        return Integer.parseInt(totalitems);
    }
}
