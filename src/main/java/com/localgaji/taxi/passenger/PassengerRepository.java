package com.localgaji.taxi.passenger;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Passenger> findAllPassengerByPartyId(Long partyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Passenger> findFirstByUserIdAndPartyIdAndStatusEquals(Long userId, Long partyId, PassengerStatus status);
}
