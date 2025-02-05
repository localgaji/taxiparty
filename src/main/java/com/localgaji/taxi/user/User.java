package com.localgaji.taxi.user;

import com.localgaji.taxi.__global__.utils.BaseTime;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name = "user_table") @Hidden
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull @NotNull
    private String userName;

    @NotNull @NotNull
    private String email;

}