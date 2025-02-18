package com.localgaji.taxi.party;

import com.localgaji.taxi.__global__.auth_global.AuthUser;
import com.localgaji.taxi.__global__.utils.ApiUtil.Response;
import com.localgaji.taxi.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.localgaji.taxi.__global__.utils.ApiUtil.success;
import static com.localgaji.taxi.party.dto.RequestParty.*;
import static com.localgaji.taxi.party.dto.ResponseParty.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "", description = "")
@RequestMapping("/party")
public class PartyController {

    private final PartyService partyService;
    private final PartyLocationService locationService;

    @PostMapping
    @Operation(summary = "파티 개설")
    public ResponseEntity<Response<String>> postAddParty(@AuthUser User user,
                                                         @RequestBody PostPartyReq requestBody) {
        partyService.makeParty(user, requestBody);
        return ResponseEntity.ok().body(success(null));
    }

    // 채팅 연결 필요..
    @GetMapping("/myList")
    @Operation(summary = "내 파티 리스트 조회")
    public ResponseEntity<Response<GetPartyListRes>> getPartyList(@AuthUser User user) {
        GetPartyListRes responseBody = partyService.getPartyList(user);
        return ResponseEntity.ok().body(success(responseBody));
    }

    @GetMapping("/{partyId}")
    @Operation(summary = "파티 상세 정보 조회")
    public ResponseEntity<Response<GetPartyRes>> getPartyList(@AuthUser User user,
                                                              @PathVariable Long partyId) {
        GetPartyRes responseBody = partyService.getPartyDetail(user, partyId);

        return ResponseEntity.ok().body(success(responseBody));
    }

    @PostMapping("/{partyId}/account")
    @Operation(summary = "파티에 계좌 등록")
    public ResponseEntity<Response<String>> postAccount(@AuthUser User user,
                                                        @PathVariable Long partyId) {
        partyService.addAccount(user, partyId);
        return ResponseEntity.ok().body(success(null));
    }

    @PatchMapping("/{partyId}/fare")
    @Operation(summary = "요금 등록")
    public ResponseEntity<Response<String>> patchFare(@AuthUser User user,
                                                      @PathVariable Long partyId,
                                                      @RequestBody AddFareReq requestBody) {
        partyService.addFare(user, partyId, requestBody);
        return ResponseEntity.ok().body(success(null));
    }

    @PatchMapping("/{partyId}/end")
    @Operation(summary = "파티 끝내기")
    public ResponseEntity<Response<String>> patchPartyEnd(@AuthUser User user,
                                                          @PathVariable Long partyId) {
        partyService.endParty(user, partyId);
        return ResponseEntity.ok().body(success(null));
    }

    @GetMapping("/{partyId}/locations")
    @Operation(summary = "승차, 하차 위치 상세조회")
    public ResponseEntity<Response<GetLocationsRes>> getLocation2(@PathVariable Long partyId) {
        Party party = partyService.findPartyByPartyId(partyId);
        GetLocationsRes response = locationService.getLocations(party);
        return ResponseEntity.ok().body(success(response));
    }

    @PostMapping("/party/_search")
    @Operation(summary = "조건에 맞는 파티 리스트 조회")
    public ResponseEntity<Response<GetPartiesSearchRes>> getSearchParty(@RequestBody GetPartiesSearchReq requestBody) {
        GetPartiesSearchRes response = locationService.partySearch(requestBody);
        return ResponseEntity.ok().body(success(response));
    }
}
