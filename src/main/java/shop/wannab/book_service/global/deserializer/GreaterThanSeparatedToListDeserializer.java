package shop.wannab.book_service.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GreaterThanSeparatedToListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getText();
        if (raw == null || raw.isBlank()) return Collections.emptyList();
        return Arrays.stream(raw.split(">"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
