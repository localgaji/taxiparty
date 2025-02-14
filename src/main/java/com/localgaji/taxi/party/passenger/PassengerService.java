package com.localgaji.taxi.party.passenger;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import com.localgaji.taxi.party.Party;
import com.localgaji.taxi.party.PartyService;
import com.localgaji.taxi.user.User;
import com.localgaji.taxi.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.localgaji.taxi.party.dto.ResponseParty.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final PartyService partyService;
    private final UserService userService;

    /** 파티 가입 */
    @Transactional
    public void join(User user, Long partyId) {
        Party party = partyService.findPartyByPartyId(partyId);
        Passenger passenger = Passenger.builder()
                .user(user)
                .party(party)
                .build();

        // 이미 가입 -> 예외처리
        if (isUserInParty(user, party)) {
            throw new CustomException(ErrorType.ALREADY_HAVE);
        }

        // 강퇴된 회원일 때
        if (hasKicked(user, party)) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }

        // 저장
        passengerRepository.save(passenger);
        passenger.joinPassengerInParty();
    }

    /** 파티 탈퇴 */
    @Transactional
    public void leave(User user, Long partyId) {
        Party party = partyService.findPartyByPartyId(partyId);

        Passenger passenger = findActivePassenger(user, party)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND));

        passenger.leavePassenger();
    }

    /** 파티 강퇴 */
    @Transactional
    public void kickout(User manager, Long kickUserId, Long partyId) {
        Party party = partyService.findPartyByPartyId(partyId);

        // 방장 권한 체크
        if (!isManagerInParty(manager, party)) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }

        // 쫓아낼 유저 찾기
        User kickUser = userService.findUserById(kickUserId);
        Passenger passenger = findActivePassenger(kickUser, party)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND));

        // 쫓아내기
        passenger.kickPassenger();
    }

    /** 팀원 리스트 */
    public GetPassengersRes getPassengers(User user, Long partyId) {
        Party party = partyService.findPartyByPartyId(partyId);

        // 권한 확인
        if (!isUserInParty(user, party)) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }

        // 팀원 리스트 가공
        List<PassengerInfo> passengers = party.getPassengers().stream()
                .filter(p -> p.getStatus() == PassengerStatus.ACTIVE)
                .map( PassengerInfo::new )
                .toList();

        return new GetPassengersRes(passengers);
    }

    /** util: 소속 팀원인지 확인 */
    public boolean isUserInParty(User user, Party party) {
        return findActivePassenger(user,party).isPresent();
    }

    /** util: 팀의 매니저인지 권한 확인 */
    public boolean isManagerInParty(User manager, Party party) {
        Long managerUserId = manager.getUserId();
        return party.getPassengers().stream()
                .anyMatch(p ->
                        managerUserId.equals( p.getUser().getUserId() )
                                || p.getStatus() == PassengerStatus.ACTIVE
                                || p.getIsManager()
                );
    }

    /** util: 강퇴당한 멤버인지 확인 */
    private boolean hasKicked(User user, Party party) {
        Long userId = user.getUserId();
        return party.getPassengers().stream()
                .anyMatch(p ->
                        userId.equals( p.getUser().getUserId() )
                                || p.getStatus() == PassengerStatus.KICKED_OUT
                );
    }

    /** util: user & party 로 passenger entity 찾기 */
    private Optional<Passenger> findActivePassenger(User user, Party party) {
        List<Passenger> passengers = party.getPassengers();
        Long userId = user.getUserId();
        return passengers.stream()
                .filter(p ->
                        userId.equals( p.getUser().getUserId() )
                                || p.getStatus() == PassengerStatus.ACTIVE
                ).findAny();
    }
}
