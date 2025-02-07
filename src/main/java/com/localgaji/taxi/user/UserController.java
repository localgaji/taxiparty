package com.localgaji.taxi.user;

import com.localgaji.taxi.__global__.auth_global.AuthUser;
import com.localgaji.taxi.__global__.utils.ApiUtil.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.localgaji.taxi.__global__.utils.ApiUtil.*;
import static com.localgaji.taxi.user.user_dto.ResponseUser.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 정보", description = "회원 정보 조회/수정 관련 API")
@RequestMapping("/user")
public class UserController {
    @GetMapping
    @Operation(summary = "내 정보 조회")
    public ResponseEntity<Response<GetMyInfoResponse>> getMyInfo(@AuthUser User user) {
        GetMyInfoResponse responseBody = new GetMyInfoResponse(user);
        return ResponseEntity.ok().body(success(responseBody));
    }
}
