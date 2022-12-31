package com.naeggeodo.entity.deal;

import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.user.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Deal {
    @Id
    @GeneratedValue
    @Column(name = "deal_id")
    private Long id;

    private LocalDateTime regDate;

    @ManyToOne
    @JoinColumn(name = "chatmain_id")
    private ChatMain chatMain;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public static Deal create(Users user, ChatMain chatMain) {
        return new Deal(null, LocalDateTime.now(), chatMain, user);
    }
}
