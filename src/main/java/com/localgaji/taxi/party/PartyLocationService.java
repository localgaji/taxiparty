package com.localgaji.taxi.party;

import com.localgaji.taxi.address.Address;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.localgaji.taxi.party.dto.LocationDTO.*;
import static com.localgaji.taxi.party.dto.RequestParty.*;
import static com.localgaji.taxi.party.dto.ResponseParty.*;

@Service @RequiredArgsConstructor
public class PartyLocationService {

    private final int SRID = 4326;
    private final GeometryFactory gf = new GeometryFactory(new PrecisionModel(), SRID);
    private final PartyRepository partyRepository;

    /** 승하차 위치 상세 조회 */
    public GetLocationsRes getLocations(Party party) {

        AddressDTO pickup = locationToDTO( party.getPickupAddress(), party.getPickupPoint() );
        AddressDTO dropoff = locationToDTO( party.getDropoffAddress(), party.getDropoffPoint() );

        return new GetLocationsRes(pickup, dropoff);
    }

    /** 파티 검색 결과 조회 */
    public GetPartiesSearchRes partySearch(GetPartiesSearchReq requestBody) {
        Point departurePoint = newPoint( requestBody.departure() );
        Point dropoffPoint = newPoint( requestBody.dropoff() );

        Slice<Party> nearestParties = partyRepository.findNearestParties(
                departurePoint, 1000,
                dropoffPoint, 1000,
                requestBody.pickupTime(), 3600,
                PageRequest.of(requestBody.page() - 1, 5)
        );

        List<SearchPartyDTO> parties = nearestParties.stream()
                .map(party ->
                        new SearchPartyDTO(party, "??")
                ).toList();
        return new GetPartiesSearchRes( parties, nearestParties.hasNext() );
    }

    public Point newPoint(CoordinateDTO dto) {
        Coordinate coordinate = new Coordinate( dto.longitude(), dto.latitude() );
        return gf.createPoint(coordinate);
    }

    public AddressDTO locationToDTO(Address address, Point point) {
        return new AddressDTO(
                address.getPlaceName(),
                address.getRoadNameAddress(),
                pointToDTO(point)
        );
    }

    private CoordinateDTO pointToDTO(Point point) {
        // Coordinate : 경도 위도 순서
        Coordinate coordinate = point.getCoordinate();
        return new CoordinateDTO( coordinate.getY(), coordinate.getX() );
    }
}
