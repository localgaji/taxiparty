package com.localgaji.taxi.party;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import com.localgaji.taxi.party.passenger.Passenger;
import com.localgaji.taxi.party.passenger.PassengerRepository;
import com.localgaji.taxi.party.passenger.PassengerService;
import com.localgaji.taxi.party.passenger.PassengerStatus;
import com.localgaji.taxi.address.AddressService;
import com.localgaji.taxi.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.localgaji.taxi.party.dto.RequestParty.*;
import static com.localgaji.taxi.party.dto.ResponseParty.*;
import static com.localgaji.taxi.party.dto.ResponseParty.GetPartyListRes.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;
    private final PassengerRepository passengerRepository;
    private final PassengerService passengerService;
    private final AddressService addressService;
    private final PartyLocationService locationService;

    /** 파티 개설 */
    @Transactional
    public void makeParty(User user, PostPartyReq dto) {
        // dto -> entity
        Point pickupPoint = locationService.newPoint(dto.pickup().coordinate());
        Point dropoffPoint = locationService.newPoint(dto.dropoff().coordinate());
        Party party = dto.toEntity(pickupPoint, dropoffPoint);

        // 승/하차 주소 entity 저장
        addressService.savePartyPlaces(party);

        // party entity 저장
        partyRepository.save(party);

        // passenger entity 저장
        Passenger passenger = Passenger.builder()
                .user(user)
                .party(party)
                .isManager(true)
                .build();
        passengerRepository.save(passenger);

        // passenger 와 user, party 양방향 연결
        passenger.joinPassengerInParty();
    }

    /** 파티 상세 정보 */
    public GetPartyRes getPartyDetail(User user, Long partyId) {
        Party party = findPartyByPartyId(partyId);

        // 소속 팀원인지 권한 확인
        passengerService.isUserInParty(user, party);

        return new GetPartyRes(party);
    }

    /** 파티에 계좌 등록 */
    @Transactional
    public void addAccount(User user, Long partyId) {
        Party party = findPartyByPartyId(partyId);

        // 매니저가 아닐때
        if (!passengerService.isManagerInParty(user, party)) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }

        // 개인정보에 등록된 계좌가 없을 때
        if (user.getAccount() == null) {
            throw new CustomException(ErrorType.NOT_FOUND);
        }

        // 등록
        party.addAccount(user.getAccount());
    }

    /** 요금 등록 */
    @Transactional
    public void addFare(User user, Long partyId, AddFareReq requestBody) {
        Party party = findPartyByPartyId(partyId);

        // 매니저가 아닐때
        if (!passengerService.isManagerInParty(user, party)) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }

        // 요금 등록
        party.addFare(requestBody.fare());
    }

    /** 파티 종료 */
    @Transactional
    public void endParty(User user, Long partyId) {
        Party party = findPartyByPartyId(partyId);

        // 매니저가 아닐때
        if (!passengerService.isManagerInParty(user, party)) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }

        // 끝내기
        party.endParty();
    }

    /** 내 파티 리스트 조회: 채팅 기능 도입 필요 */
    public GetPartyListRes getPartyList(User user) {

        List<MyPartyDTO> myPartyDTOList = user.getPassengerList().stream()
                .filter(passenger ->
                        passenger.getStatus() == PassengerStatus.ACTIVE
                        || passenger.getParty().getStatus() == PartyStatus.ACTIVE
                ).map(passenger ->
                        new MyPartyDTO( passenger.getParty() , "", 0 )
                ).toList();

        return new GetPartyListRes(myPartyDTOList);
    }

    /** util: ID로 entity 찾기 (없으면 404) */
    public Party findPartyByPartyId(Long partyId) {
        return partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND));
    }
}
