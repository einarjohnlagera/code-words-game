package com.cctalents.code_words.service;

import com.cctalents.code_words.entity.Word;
import com.cctalents.code_words.enums.Difficulty;
import com.cctalents.code_words.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository repository;

    public Word getWordByName(String word) {
        return repository.findByName(word);
    }

    public String getRandomWordByDifficulty(Difficulty difficulty) {
        List<Word> words = repository.findAllByDifficulty(difficulty);
        return getRandomElement(words).getName();
    }

    private Word getRandomElement(List<Word> words) {
        Random r = new Random();
        return words.get(r.nextInt(words.size()));
    }
}
