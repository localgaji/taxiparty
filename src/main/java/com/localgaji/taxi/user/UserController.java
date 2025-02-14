package com.localgaji.taxi.user;

import com.localgaji.taxi.__global__.auth_global.AuthUser;
import com.localgaji.taxi.__global__.utils.ApiUtil.Response;
import com.localgaji.taxi.account.Account;
import com.localgaji.taxi.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.localgaji.taxi.__global__.utils.ApiUtil.*;
import static com.localgaji.taxi.user.user_dto.ResponseUser.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 정보", description = "회원 정보 조회/수정 관련 API")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AccountService accountService;

    @GetMapping
    @Operation(summary = "내 정보 조회")
    public ResponseEntity<Response<GetMyInfoResponse>> getMyInfo(@AuthUser User user) {
        GetMyInfoResponse responseBody = new GetMyInfoResponse(user);
        return ResponseEntity.ok().body(success(responseBody));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "다른 회원 정보 조회")
    public ResponseEntity<Response<GetUserInfoResponse>> getUserInfo(@PathVariable Long userId) {
        User user = userService.findUserById(userId);
        GetUserInfoResponse responseBody = new GetUserInfoResponse(user);
        return ResponseEntity.ok().body(success(responseBody));
    }

    @PatchMapping("/account")
    @Operation(summary = "계좌 등록")
    public ResponseEntity<Response<String>> patchAccount(@AuthUser User user,
                                                         @RequestBody Account body) {
        accountService.addAccount(user, body);
        return ResponseEntity.ok().body(success(null));
    }
}
