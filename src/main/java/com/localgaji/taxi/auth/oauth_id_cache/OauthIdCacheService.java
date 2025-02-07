package com.localgaji.taxi.auth.oauth_id_cache;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import com.localgaji.taxi.auth.OauthType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class OauthIdCacheService {

    private final OauthIdCacheRepository oauthIdCacheRepository;

    // 인증 캐시 생성
    public void createCache(String code, OauthType oauthType, Long oauthId) {
        OauthIdCache newOauthIdCache = OauthIdCache.builder()
                .code(code)
                .oauthType(oauthType)
                .oauthId(oauthId)
                .build();
        oauthIdCacheRepository.save(newOauthIdCache);
    }

    // 인증 캐시 조회
    public OauthIdCache findOauthIdByCode(String code) {
        return oauthIdCacheRepository.findByCode(code)
                .orElseThrow(()-> new CustomException(ErrorType.NOT_FOUND));
    }

    // 인증 캐시 삭제
    public void deleteWaiting(OauthIdCache oauthIdCache) {
        oauthIdCacheRepository.delete(oauthIdCache);
    }

}
