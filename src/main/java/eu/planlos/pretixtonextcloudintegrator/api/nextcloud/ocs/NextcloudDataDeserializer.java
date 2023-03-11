//package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.ocs;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudData;
//import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudUser;
//import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudUserList;
//
//import java.io.IOException;
//
//public class NextcloudDataDeserializer extends JsonDeserializer<NextcloudData> {
//
//    @Override
//    public NextcloudData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
//            throws IOException {
//
//        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
//        JsonNode node = mapper.readTree(jsonParser);
//        String type = node.get("type").asText();
//
//        switch (type) {
//            case "user":
//                return mapper.treeToValue(node, NextcloudUser.class);
//            case "userlist":
//                return mapper.treeToValue(node, NextcloudUserList.class);
//            // add more cases for other types of NextcloudData
//            default:
//                throw new RuntimeException("Unknown NextcloudData type: " + type);
//        }
//    }
//}