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
public class NextcloudApiResponse<T> {

    @JsonProperty("ocs")
    private NextcloudOcs<T> ocs;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NextcloudOcs<T> {

        private NextcloudMeta meta;
        private T data;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NextcloudMeta {

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
}