package com.localgaji.taxi.party.cache;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeSlotResolver {

    private static final int SLOT_MINUTE=5;

    public LocalDateTime resolve(LocalDateTime pickupTime) {
        int minute = pickupTime.getMinute();
        int truncated = (minute / SLOT_MINUTE) * SLOT_MINUTE;
        return pickupTime.withMinute(truncated).withSecond(0);
    }
}