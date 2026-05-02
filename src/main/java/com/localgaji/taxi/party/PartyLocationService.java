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

import static com.localgaji.taxi.party.PartyRepository.*;
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

        Slice<PartyWithDistance> nearestParties = partyRepository.findNearestParties(
                departurePoint, requestBody.departureRadiusMeter(),
                dropoffPoint, requestBody.dropoffRadiusMeter(),
                requestBody.pickupTime(), requestBody.rangeMinute(),
                PageRequest.of(requestBody.page() - 1, 8)
        );
        List<SearchPartyDTO> parties = nearestParties.stream()
                .map(pwd ->
                        new SearchPartyDTO( pwd.getParty(), pwd.getDistance() )
                ).toList();
        return new GetPartiesSearchRes( parties, nearestParties.hasNext() );
    }

    /** 파티 검색 결과 조회 : CTE 를 사용 */
    public GetPartiesSearchRes partySearchWithCTE(GetPartiesSearchReq requestBody) {

//        System.out.println("🔥 실제 DB 조회 실행!");

        Point departurePoint = newPoint( requestBody.departure() );
        Point dropoffPoint = newPoint( requestBody.dropoff() );

        Slice<PartyNecessaryColumns> nearestParties = partyRepository.findNearestPartiesWithCTE(
                departurePoint, requestBody.departureRadiusMeter(),
                dropoffPoint, requestBody.dropoffRadiusMeter(),
                requestBody.pickupTime(), requestBody.rangeMinute(),
                PageRequest.of(requestBody.page() - 1, 8)
        );
        List<SearchPartyDTO> parties = nearestParties.stream()
                .map(SearchPartyDTO::new)
                .toList();
        return new GetPartiesSearchRes( parties, nearestParties.hasNext() );
    }

    /** 파티 검색 결과 조회 */
    public GetPartiesSearchRes partySearchSelectIndex(GetPartiesSearchReq requestBody) {
        Point departurePoint = newPoint( requestBody.departure() );
        Point dropoffPoint = newPoint( requestBody.dropoff() );

        Slice<PartyNecessaryColumns> nearestParties = partyRepository.findNearestPartiesForceIndex(
                departurePoint, requestBody.departureRadiusMeter(),
                dropoffPoint, requestBody.dropoffRadiusMeter(),
                requestBody.pickupTime(), requestBody.rangeMinute(),
                PageRequest.of(requestBody.page() - 1, 8)
        );
        List<SearchPartyDTO> parties = nearestParties.stream()
                .map(SearchPartyDTO::new)
                .toList();
        return new GetPartiesSearchRes( parties, nearestParties.hasNext() );
    }

    public Point newPoint(CoordinateDTO dto) {
        Coordinate coordinate = new Coordinate( dto.longitude(), dto.latitude() );
        return gf.createPoint(coordinate);
    }

    private AddressDTO locationToDTO(Address address, Point point) {
        return new AddressDTO(
                address.getPlaceName(),
                address.getRoadNameAddress().toStringAddress(),
                pointToDTO(point)
        );
    }

    private CoordinateDTO pointToDTO(Point point) {
        // Coordinate : 경도 위도 순서
        Coordinate coordinate = point.getCoordinate();
        return new CoordinateDTO( coordinate.getY(), coordinate.getX() );
    }
}
