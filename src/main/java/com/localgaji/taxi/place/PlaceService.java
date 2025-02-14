package com.localgaji.taxi.place;

import com.localgaji.taxi.party.Party;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Slf4j
public class PlaceService {
    private final PlaceRepository placeRepository;

    @Transactional
    public void savePartyPlaces(Party party) {
        Place pickup = party.getPickupPlace();
        Place dropoff = party.getDropoffPlace();

        placeRepository.save(pickup);
        placeRepository.save(dropoff);
    }

}
