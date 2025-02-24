package com.localgaji.taxi.chat;

import com.localgaji.taxi.__global__.utils.BaseTime;
import com.localgaji.taxi.party.Party;
import com.localgaji.taxi.user.User;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity @Table(name = "chat") @Hidden
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class Chat extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column @NotNull
    private String message;

    @ManyToOne(fetch = FetchType.LAZY) @NotNull @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @NotNull @JoinColumn(name = "party_id")
    private Party party;

    @CreatedDate
    private LocalDateTime createdDate;

    public void addChat() {
        this.party.getChatList().add(this);
    }
}
