package com.webnovel.domain.user.entity;

import com.webnovel.domain.novel.entity.Novel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 아이디
    private String nickname; // 닉네임
    private String name; // 이름
    private String email; // 이메일
    private String role; // 역할
    private String password; // 비밀번호

    @OneToMany(mappedBy = "author")
    private List<Novel> myNovels = new ArrayList<>();

    @Builder
    public User(String username, String nickname, String name, String email, String role, String password) {
        this.username = username;
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public User(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public void update(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
}
