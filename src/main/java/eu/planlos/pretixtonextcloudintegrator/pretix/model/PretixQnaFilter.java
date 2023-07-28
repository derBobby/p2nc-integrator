package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record PretixQnaFilter(Map<String, List<String>> filterMap) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : filterMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            sb.append("'").append(key).append("': ");

            if (!values.isEmpty()) {
                for (int i = 0; i < values.size() - 1; i++) {
                    sb.append("'").append(values.get(i)).append("', ");
                }
                sb.append("'").append(values.get(values.size() - 1)).append("'");
            }
        }

        return sb.toString();
    }

    public static PretixQnaFilter fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String[] entries = text.split(";");
        Map<String, List<String>> filterMap = new HashMap<>();
        for (String entry : entries) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                String key = parts[0];
                List<String> values = Arrays.asList(parts[1].split(","));
                filterMap.put(key, values);
            }
        }
        return new PretixQnaFilter(filterMap);
    }
}