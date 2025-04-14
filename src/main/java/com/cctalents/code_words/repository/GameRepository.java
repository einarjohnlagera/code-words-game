package com.cctalents.code_words.repository;

import com.cctalents.code_words.entity.Game;
import com.cctalents.code_words.enums.GameStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    Game findByPlayer(String player);
    List<Game> findAllByStatus(GameStatus status);
}
