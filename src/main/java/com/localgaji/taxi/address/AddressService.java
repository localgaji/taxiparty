package com.localgaji.taxi.address;

import com.localgaji.taxi.party.Party;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    @Transactional
    public void savePartyPlaces(Party party) {
        Address pickup = party.getPickupAddress();
        Address dropoff = party.getDropoffAddress();

        addressRepository.save(pickup);
        addressRepository.save(dropoff);
    }

}
