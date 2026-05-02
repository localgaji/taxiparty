package com.localgaji.taxi.party.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.localgaji.taxi.party.PartyLocationService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import static com.localgaji.taxi.party.dto.RequestParty.*;
import static com.localgaji.taxi.party.dto.ResponseParty.*;

@Service
@RequiredArgsConstructor
public class CachedPartySearchService {

    private final Cache<PartySearchCacheKey, GetPartiesSearchRes> cache;
    private final PartyLocationService delegate;
    private final LocationAnchorResolver anchorResolver;
    private final TimeSlotResolver timeSlotResolver;

    public GetPartiesSearchRes search(GetPartiesSearchReq req) {
        Point departure = delegate.newPoint(req.departure());
        Point dropoff = delegate.newPoint(req.dropoff());

        PartySearchCacheKey key = new PartySearchCacheKey(
                anchorResolver.resolve(departure),
                anchorResolver.resolve(dropoff),
                timeSlotResolver.resolve(req.pickupTime()),
                req.departureRadiusMeter(),
                req.dropoffRadiusMeter(),
                req.page()
        );

//        System.out.println("Cache key: " + key);

        return cache.get(key, k -> delegate.partySearchWithCTE(req));
    }
}