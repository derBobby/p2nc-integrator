package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
