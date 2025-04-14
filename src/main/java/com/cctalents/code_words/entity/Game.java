package com.cctalents.code_words.entity;

import com.cctalents.code_words.enums.GameStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "games")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private String maskedWord;
    private int remainingAttempts;
    private GameStatus status;
    private String player;
}
