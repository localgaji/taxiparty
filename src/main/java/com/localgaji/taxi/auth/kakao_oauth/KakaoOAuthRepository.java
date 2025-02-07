package com.localgaji.taxi.auth.kakao_oauth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KakaoOAuthRepository extends JpaRepository<KakaoOAuth, Long> {
    Optional<KakaoOAuth> findByKakaoId(Long kakaoId);

}
