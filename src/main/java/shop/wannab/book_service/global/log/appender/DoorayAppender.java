package shop.wannab.book_service.global.log.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DoorayAppender extends AppenderBase<ILoggingEvent> {

    private String webhookUrl;

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted() || !event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            return;
        }

        try {
            String logMessage = event.getFormattedMessage();

            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("botName", "ErrorBot");
            payloadMap.put("text", "[ERROR] " + logMessage);
            payloadMap.put("botIconImage", "https://your-icon.png");

            String payload = objectMapper.writeValueAsString(payloadMap);

            URI uri = URI.create(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
            conn.getInputStream().close();
        } catch (Exception e) {
            System.err.println("DoorayAppender error: " + e.getMessage());
        }
    }
}
