package mx.hotel.backend_cav.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApplicationTimeService {

    private final ZoneId zoneId;

    public ApplicationTimeService(@Value("${app.time-zone:America/Cancun}") String timeZone) {
        this.zoneId = ZoneId.of(timeZone);
    }

    public LocalDateTime now() {
        return LocalDateTime.now(zoneId);
    }

    public LocalDate today() {
        return LocalDate.now(zoneId);
    }
}