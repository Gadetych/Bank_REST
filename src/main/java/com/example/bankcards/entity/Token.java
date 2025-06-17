package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "token_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "access_token", unique = true)
    private String accessToken;
    @Column(name = "refresh_token", unique = true)
    private String refreshToken;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
