package com.cctalents.code_words.entity;

import com.cctalents.code_words.enums.Difficulty;
import jakarta.persistence.*;
import lombok.Data;

@Table(name = "words")
@Entity
@Data
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
}
