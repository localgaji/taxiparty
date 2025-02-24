package com.localgaji.taxi.party;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import com.localgaji.taxi.chat.ChatRepository;
import com.localgaji.taxi.party.passenger.Passenger;
import com.localgaji.taxi.party.passenger.PassengerRepository;
import com.localgaji.taxi.address.AddressService;
import com.localgaji.taxi.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.localgaji.taxi.party.dto.RequestParty.*;
import static com.localgaji.taxi.party.dto.ResponseParty.*;
import static com.localgaji.taxi.party.dto.ResponseParty.GetPartyListRes.*;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;
    private final PassengerRepository passengerRepository;
    private final AddressService addressService;
    private final PartyLocationService locationService;
    private final UtilPartyService utilPartyService;
    private final ChatRepository chatRepository;

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
        Party party = utilPartyService.findPartyByIdOr404(partyId);

        // 소속 팀원인지 권한 확인
        utilPartyService.checkUserInPartyOrThrow(user, party);

        return new GetPartyRes(party);
    }

    /** 파티에 계좌 등록 */
    @Transactional
    public void addAccount(User user, Long partyId) {
        Party party = utilPartyService.findPartyByIdOr404(partyId);

        // 매니저 체크
        utilPartyService.checkManagerInPartyOrThrow(user, party);

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
        Party party = utilPartyService.findPartyByIdOr404(partyId);

        // 매니저 체크
        utilPartyService.checkManagerInPartyOrThrow(user, party);

        // 요금 등록
        party.addFare(requestBody.fare());
    }

    /** 파티 종료 */
    @Transactional
    public void endParty(User user, Long partyId) {
        Party party = utilPartyService.findPartyByIdOr404(partyId);

        // 매니저 체크
        utilPartyService.checkManagerInPartyOrThrow(user, party);

        // 끝내기
        party.endParty();
    }

    /** 내 파티 리스트 조회 */
    public GetPartyListRes getPartyList(User user) {

        // 파티를 가져오면서 각 파티의 최신 채팅을 1개씩 가져오기
        List<MyPartyDTO> myPartyDTOList = chatRepository.findPartiesAndLatestChatByUser(user).stream()
                .map(dto ->
                        new MyPartyDTO(dto.party(), dto.latestChat().getMessage(), 0 )
                ).toList();

        return new GetPartyListRes(myPartyDTOList);
    }
}
