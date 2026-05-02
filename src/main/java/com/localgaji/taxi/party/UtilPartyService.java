package com.localgaji.taxi.party;

import com.localgaji.taxi.__global__.utils.AbstractBaseService;
import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import com.localgaji.taxi.passenger.PassengerStatus;
import com.localgaji.taxi.user.User;
import org.springframework.stereotype.Service;

@Service
public class UtilPartyService extends AbstractBaseService<Party> {

    public UtilPartyService(PartyRepository partyRepository) {
        super(partyRepository);
    }

    public Party findPartyByIdOr404(Long partyId) {
        return findByIdOr404(partyId);
    }

    /** 소속 팀원이 아니면 403 에러 발생 */
    public void checkUserInPartyOrThrow(User user, Party party) {
        if ( !isUserInParty(user, party) ) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }
    }

    /** 소속 팀원인지 확인 */
    public boolean isUserInParty(User user, Party party) {
        Long userId = user.getId();

        return party.getPassengers().stream()
                .anyMatch(p ->
                        userId.equals( p.getUser().getId() )
                                && p.getStatus() == PassengerStatus.ACTIVE
                );
    }

    /** 파티의 매니저가 아니면 403 에러 발생 */
    public void checkManagerInPartyOrThrow(User user, Party party) {
        if ( !isManagerInParty(user, party) ) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }
    }

    /** 파티의 매니저인지 확인 */
    public boolean isManagerInParty(User manager, Party party) {
        Long managerUserId = manager.getId();
        return party.getPassengers().stream()
                .anyMatch(p ->
                        managerUserId.equals(p.getUser().getId())
                                && p.getStatus() == PassengerStatus.ACTIVE
                                && p.getIsManager()
                );
    }
}
