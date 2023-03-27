package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.ocs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudApiResponse;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudMeta;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class NextcloudApiResponseDeserializer<T> extends StdDeserializer<NextcloudApiResponse<T>> {

    private Class<T> type;

    public NextcloudApiResponseDeserializer(Class<T> type) {
        super(NextcloudApiResponse.class);
        this.type = type;
    }

    @Override
    public NextcloudApiResponse<T> deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode rootNode = mapper.readTree(jsonParser);
        JsonNode metaNode = rootNode.get("ocs").get("meta");
        NextcloudMeta meta = mapper.treeToValue(metaNode, NextcloudMeta.class);

        JsonNode dataNode = rootNode.get("ocs").get("data");
        T data = mapper.treeToValue(dataNode, type);

        return new NextcloudApiResponse<>(meta, data);
    }
}