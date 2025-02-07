package com.localgaji.taxi.auth.oauth_id_cache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OauthIdCacheRepository extends JpaRepository<OauthIdCache, Long> {
    Optional<OauthIdCache> findByCode(String code);
}
