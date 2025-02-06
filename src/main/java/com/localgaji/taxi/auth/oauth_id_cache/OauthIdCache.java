package com.localgaji.taxi.auth.oauth_id_cache;

import com.localgaji.taxi.__global__.utils.BaseTime;
import com.localgaji.taxi.auth.OauthType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "oauth_id_cache")
public class OauthIdCache extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column @NotNull
    private String code;

    @Column @NotNull
    private Long oauthId;

    @Column @NotNull
    private OauthType oauthType;
}
