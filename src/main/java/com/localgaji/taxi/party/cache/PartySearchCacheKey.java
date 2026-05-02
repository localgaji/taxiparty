package com.localgaji.taxi.party.cache;

import java.time.LocalDateTime;

public record PartySearchCacheKey(
        String departureAnchor,
        String dropoffAnchor,
        LocalDateTime timeSlot,
        int pickupRadius,
        int dropoffRadius,
        int page
) {}