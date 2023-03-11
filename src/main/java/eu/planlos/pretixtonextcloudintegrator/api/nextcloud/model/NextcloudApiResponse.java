package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NextcloudApiResponse<T extends NextcloudData> {

    @JsonProperty("ocs")
    private void unpackOcs(Map<String, Object> ocs) {
        Map<String, Object> metaMap = (Map<String, Object>) ocs.get("meta");
        meta = new NextcloudMeta(
                (String) metaMap.get("status"),
                (int) metaMap.get("statuscode"),
                (String) metaMap.get("message"),
                (String) metaMap.get("totalitems"),
                (String) metaMap.get("itemsperpage")
        );

        Map<String, Object> dataMap = (Map<String, Object>) ocs.get("data");
        data = new ObjectMapper().convertValue(dataMap, new TypeReference<T>() {});
    }

//    @JsonProperty("ocs.meta")
    private NextcloudMeta meta;
//    @JsonProperty("ocs.data")
    private T data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextcloudMeta {

        private String status;
//        @JsonProperty("statuscode")
        private int statusCode;
        private String message;
//        @JsonProperty("totalitems")
        private String totalItems;
//        @JsonProperty("itemsperpage")
        private String itemsPerPage;

        public int getTotalItemsInt() {
            return Integer.parseInt(totalItems);
        }
    }
}