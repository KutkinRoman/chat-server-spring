package chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class LocaleConfig {

    @Value("${server.time-zone}")
    private String timeZoneId;

    @PostConstruct
    public void init () {
        TimeZone.setDefault (TimeZone.getTimeZone (timeZoneId));
    }
}
