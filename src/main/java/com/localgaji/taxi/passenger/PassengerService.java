package com.localgaji.taxi.passenger;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import com.localgaji.taxi.party.Party;
import com.localgaji.taxi.party.UtilPartyService;
import com.localgaji.taxi.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.localgaji.taxi.passenger.dto.ResponsePassenger.*;

@Service
@RequiredArgsConstructor
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final UtilPartyService utilPartyService;

    /** 파티 가입 */
//    @org.springframework.transaction.annotation.Transactional(isolation= Isolation.READ_COMMITTED)
    @Transactional
    public void join(User user, Long partyId) {
        Party party = utilPartyService.findPartyByIdOr404(partyId);
        List<Passenger> passengers = passengerRepository.findAllPassengerByPartyId(partyId);

        // 인원 확인
        if (getHeadcount(passengers) >= party.getMaxHeadcount()) {
            throw new CustomException(ErrorType.ALREADY_CLOSED);
        }

        // 이미 가입 -> 예외처리
        if (hasAlreadyJoin(user, passengers)) {
            throw new CustomException(ErrorType.ALREADY_HAVE);
        }

        // 강퇴된 회원일 때 -> 예외처리
        if (hasKicked(user, passengers)) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }

        Passenger passenger = Passenger.builder()
                .user(user)
                .party(party)
                .build();

        // 저장
        passengerRepository.save(passenger);
        passenger.joinPassengerInParty();
    }

    /** 파티 탈퇴 */
    @Transactional
    public void leave(User user, Long partyId) {
        Passenger passenger = passengerRepository
                .findFirstByUserIdAndPartyIdAndStatusEquals(user.getId(), partyId, PassengerStatus.ACTIVE)
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
        Passenger passenger = passengerRepository
                .findFirstByUserIdAndPartyIdAndStatusEquals(kickUserId, partyId, PassengerStatus.ACTIVE)
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

    private Integer getHeadcount(List<Passenger> passengers) {
        return (int) passengers.stream()
                .filter(p -> p.getStatus() == PassengerStatus.ACTIVE)
                .count();
    }

    private Boolean hasAlreadyJoin(User user, List<Passenger> passengers) {
        Long userId = user.getId();
        return passengers.stream()
                .anyMatch(p ->
                        userId.equals( p.getUser().getId() )
                                && p.getStatus() == PassengerStatus.ACTIVE
                );
    }

    /** 강퇴당한 멤버인지 확인 */
    private boolean hasKicked(User user, List<Passenger> passengers) {
        Long userId = user.getId();
        return passengers.stream()
                .anyMatch(p ->
                        userId.equals( p.getUser().getId() )
                                && p.getStatus() == PassengerStatus.KICKED_OUT
                );
    }
}
