package com.localgaji.taxi.party.passenger;

import com.localgaji.taxi.__global__.auth_global.AuthUser;
import com.localgaji.taxi.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.localgaji.taxi.__global__.utils.ApiUtil.*;
import static com.localgaji.taxi.party.passenger.dto.ResponsePassenger.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "", description = "")
@RequestMapping("/party/{partyId}/passenger")
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    @Operation(summary = "팀원 목록 조회")
    public ResponseEntity<Response<GetPassengersRes>> getPassengerList(@AuthUser User user,
                                                                       @PathVariable Long partyId) {
        GetPassengersRes responseBody = passengerService.getPassengers(user, partyId);
        return ResponseEntity.ok().body(success(responseBody));
    }

    @PostMapping
    @Operation(summary = "파티 가입")
    public ResponseEntity<Response<String>> postPassenger(@AuthUser User user,
                                                          @PathVariable Long partyId) {
        passengerService.join(user, partyId);
        return ResponseEntity.ok().body(success(null));
    }

    @DeleteMapping("/me")
    @Operation(summary = "파티 탈퇴")
    public ResponseEntity<Response<String>> deletePassenger(@AuthUser User user,
                                                            @PathVariable Long partyId) {
        passengerService.leave(user, partyId);
        return ResponseEntity.ok().body(success(null));
    }

    @DeleteMapping("{userId}")
    @Operation(summary = "팀원 강퇴")
    public ResponseEntity<Response<String>> kickoutPassenger(@AuthUser User user,
                                                             @PathVariable Long partyId,
                                                             @PathVariable Long userId) {
        passengerService.kickout(user, userId, partyId);
        return ResponseEntity.ok().body(success(null));
    }
}
