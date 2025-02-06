package com.localgaji.taxi.auth.kakao_oauth;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import com.localgaji.taxi.auth.OauthType;
import com.localgaji.taxi.auth.kakao_oauth.fetch.KakaoAPIFetcher;
import com.localgaji.taxi.auth.oauth_id_cache.OauthIdCache;
import com.localgaji.taxi.auth.oauth_id_cache.OauthIdCacheService;
import com.localgaji.taxi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service @RequiredArgsConstructor
public class KakaoOAuthService {

    private final KakaoAPIFetcher kakaoAPIFetcher;
    private final KakaoOAuthRepository kakaoOAuthRepository;
    private final OauthIdCacheService oauthIdCacheService;

    public void makeNewKakaoOAuth(String code, User newUser) {
        // 캐시로 카카오 아이디 찾기
        OauthIdCache oauthIdCache = oauthIdCacheService.findOauthIdByCode(code);

        // 카카오 정보 객체 생성
        KakaoOAuth newKakaoOAuth = KakaoOAuth.builder()
                .user(newUser)
                .kakaoId(oauthIdCache.getOauthId())
                .build();
        // 카카오 정보 객체 저장
        kakaoOAuthRepository.save(newKakaoOAuth);

        // 캐시 삭제
        oauthIdCacheService.deleteWaiting(oauthIdCache);
    }

    public KakaoOAuth findKakaoOAuthByCode(String code) {
        Long kakaoId = kakaoAPIFetcher.codeToKakaoId(code);
        Optional<KakaoOAuth> optKakaoAuth = kakaoOAuthRepository.findByKakaoId(kakaoId);
        if (optKakaoAuth.isEmpty()) {
            oauthIdCacheService.createCache(code, OauthType.KAKAO, kakaoId);
            throw new CustomException(ErrorType.MEMBER_NOT_FOUND);
        }
        return optKakaoAuth.get();
    }
}
