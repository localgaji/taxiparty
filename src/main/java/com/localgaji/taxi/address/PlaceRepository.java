package com.localgaji.taxi.address;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Address, Long> {
}
