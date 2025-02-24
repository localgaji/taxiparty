package com.localgaji.taxi.party.passenger;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import com.localgaji.taxi.party.Party;
import com.localgaji.taxi.party.UtilPartyService;
import com.localgaji.taxi.user.User;
import com.localgaji.taxi.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.localgaji.taxi.party.passenger.dto.ResponsePassenger.*;

@Service
@RequiredArgsConstructor
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final UserService userService;
    private final UtilPartyService utilPartyService;

    /** 파티 가입 */
    @Transactional
    public void join(User user, Long partyId) {
        Party party = utilPartyService.findPartyByIdOr404(partyId);
        Passenger passenger = Passenger.builder()
                .user(user)
                .party(party)
                .build();

        // 이미 가입 -> 예외처리
        if (utilPartyService.isUserInParty(user, party)) {
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
        Party party = utilPartyService.findPartyByIdOr404(partyId);

        Passenger passenger = findActivePassengerByUser(user, party)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND));

        passenger.leavePassenger();
    }

    /** 파티 강퇴 */
    @Transactional
    public void kickout(User manager, Long kickUserId, Long partyId) {
        Party party = utilPartyService.findPartyByIdOr404(partyId);

        // 방장 권한 체크
        utilPartyService.checkManagerInPartyOrThrow(manager, party);

        // 쫓아낼 유저 찾기
        User kickUser = userService.findUserById(kickUserId);
        Passenger passenger = findActivePassengerByUser(kickUser, party)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND));

        // 쫓아내기
        passenger.kickPassenger();
    }

    /** 팀원 리스트 */
    public GetPassengersRes getPassengers(User user, Long partyId) {
        Party party = utilPartyService.findPartyByIdOr404(partyId);

        // 권한 확인
        utilPartyService.checkUserInPartyOrThrow(user, party);

        // 팀원 리스트 가공
        List<PassengerInfo> passengers = party.getPassengers().stream()
                .filter(p -> p.getStatus() == PassengerStatus.ACTIVE)
                .map( PassengerInfo::new )
                .toList();

        return new GetPassengersRes(passengers);
    }

    /** 강퇴당한 멤버인지 확인 */
    private boolean hasKicked(User user, Party party) {
        Long userId = user.getUserId();
        return party.getPassengers().stream()
                .anyMatch(p ->
                        userId.equals( p.getUser().getUserId() )
                                || p.getStatus() == PassengerStatus.KICKED_OUT
                );
    }

    /** user & party 로 passenger entity 찾기 */
    private Optional<Passenger> findActivePassengerByUser(User user, Party party) {
        List<Passenger> passengers = party.getPassengers();
        Long userId = user.getUserId();
        return passengers.stream()
                .filter(p ->
                        userId.equals( p.getUser().getUserId() )
                                || p.getStatus() == PassengerStatus.ACTIVE
                ).findAny();
    }
}
