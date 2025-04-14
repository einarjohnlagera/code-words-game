package com.cctalents.code_words.repository;

import com.cctalents.code_words.entity.Word;
import com.cctalents.code_words.enums.Difficulty;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends ReadRepository<Word, Long> {
    List<Word> findAllByDifficulty(Difficulty difficulty);
}
