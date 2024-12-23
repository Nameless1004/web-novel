package com.webnovel.user.entity;

import com.webnovel.novel.entity.Novel;
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
    private String name; // 이름
    private String email; // 이메일
    private String role; // 역할
    private String password; // 비밀번호

    @OneToMany(mappedBy = "author")
    private List<Novel> myNovels = new ArrayList<>();

    @Builder
    public User(String username, String name, String email, String role, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public void update(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
