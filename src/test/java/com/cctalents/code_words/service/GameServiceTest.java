package com.cctalents.code_words.service;

import com.cctalents.code_words.config.GameProperties;
import com.cctalents.code_words.dto.CreateGameRequest;
import com.cctalents.code_words.dto.GameRequest;
import com.cctalents.code_words.entity.Game;
import com.cctalents.code_words.enums.Difficulty;
import com.cctalents.code_words.enums.GameStatus;
import com.cctalents.code_words.exception.GameAlreadyFinishedException;
import com.cctalents.code_words.exception.MultipleGuessLetterNotAllowedException;
import com.cctalents.code_words.repository.GameRepository;
import com.cctalents.code_words.util.GameUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @MockitoBean
    private GameRepository gameRepository;

    @MockitoBean
    private GameProperties gameProperties;

    @MockitoBean
    private WordService wordService;

    @Test
    @DisplayName("Should create a game with the provided player and difficulty when valid inputs are given")
    void testCreateGame_WithValidInputs() {
        CreateGameRequest request = CreateGameRequest.builder()
                .player("Player1")
                .difficulty("EASY")
                .build();

        String expectedWord = "example";
        String maskedWord = GameUtil.mask(expectedWord);

        when(gameProperties.getAllowedAttempts()).thenReturn(5);
        when(wordService.getRandomWordByDifficulty(Difficulty.valueOf("EASY"))).thenReturn(expectedWord);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.createGame(request);

        assertNotNull(result);
        assertEquals(expectedWord, result.getWord());
        assertEquals(maskedWord, result.getMaskedWord());
        assertEquals(5, result.getRemainingAttempts());
        assertEquals("Player1", result.getPlayer());
        assertNull(result.getStatus());

        verify(gameRepository, times(1)).save(any(Game.class));
        verify(wordService, times(1)).getRandomWordByDifficulty(Difficulty.valueOf("EASY"));
    }

    @Test
    @DisplayName("Should use default difficulty and player when inputs are missing")
    void testCreateGame_UsesDefaultValues() {
        CreateGameRequest request = CreateGameRequest.builder().build();

        String defaultPlayer = "DefaultPlayer";
        String defaultDifficulty = "MEDIUM";
        String expectedWord = "default";

        when(gameProperties.getDefaultPlayer()).thenReturn(defaultPlayer);
        when(gameProperties.getDefaultDifficulty()).thenReturn(defaultDifficulty);
        when(gameProperties.getAllowedAttempts()).thenReturn(6);
        when(wordService.getRandomWordByDifficulty(Difficulty.valueOf(defaultDifficulty))).thenReturn(expectedWord);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.createGame(request);

        assertNotNull(result);
        assertEquals(expectedWord, result.getWord());
        assertEquals(GameUtil.mask(expectedWord), result.getMaskedWord());
        assertEquals(6, result.getRemainingAttempts());
        assertEquals(defaultPlayer, result.getPlayer());
        assertNull(result.getStatus());

        verify(gameRepository, times(1)).save(any(Game.class));
        verify(wordService, times(1)).getRandomWordByDifficulty(Difficulty.valueOf(defaultDifficulty));
    }

    @Test
    @DisplayName("Should handle missing difficulty by using default difficulty")
    void testCreateGame_MissingDifficulty() {
        CreateGameRequest request = CreateGameRequest.builder()
                .player("Player2")
                .build();

        String defaultDifficulty = "HARD";
        String expectedWord = "hardword";

        when(gameProperties.getDefaultDifficulty()).thenReturn(defaultDifficulty);
        when(gameProperties.getAllowedAttempts()).thenReturn(3);
        when(wordService.getRandomWordByDifficulty(Difficulty.valueOf(defaultDifficulty))).thenReturn(expectedWord);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.createGame(request);

        assertNotNull(result);
        assertEquals(expectedWord, result.getWord());
        assertEquals(GameUtil.mask(expectedWord), result.getMaskedWord());
        assertEquals(3, result.getRemainingAttempts());
        assertEquals("Player2", result.getPlayer());
        assertNull(result.getStatus());

        verify(gameRepository, times(1)).save(any(Game.class));
        verify(wordService, times(1)).getRandomWordByDifficulty(Difficulty.valueOf(defaultDifficulty));
    }

    @Test
    @DisplayName("Should handle missing player by using default player")
    void testCreateGame_MissingPlayer() {
        CreateGameRequest request = CreateGameRequest.builder()
                .difficulty("EASY")
                .build();

        String defaultPlayer = "Anonymous";
        String expectedWord = "simple";

        when(gameProperties.getDefaultPlayer()).thenReturn(defaultPlayer);
        when(gameProperties.getAllowedAttempts()).thenReturn(4);
        when(wordService.getRandomWordByDifficulty(Difficulty.valueOf("EASY"))).thenReturn(expectedWord);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.createGame(request);

        assertNotNull(result);
        assertEquals(expectedWord, result.getWord());
        assertEquals(GameUtil.mask(expectedWord), result.getMaskedWord());
        assertEquals(4, result.getRemainingAttempts());
        assertEquals(defaultPlayer, result.getPlayer());
        assertNull(result.getStatus());

        verify(gameRepository, times(1)).save(any(Game.class));
        verify(wordService, times(1)).getRandomWordByDifficulty(Difficulty.valueOf("EASY"));
    }

    @Test
    @DisplayName("Should update game status to WON when the guessed word is fully correct")
    void testGuess_CorrectFullWord() {
        Long gameId = 1L;
        String correctWord = "example";

        GameRequest gameRequest = new GameRequest();
        gameRequest.setGuess(correctWord);

        Game game = Game.builder()
                .id(gameId)
                .word(correctWord)
                .maskedWord(GameUtil.mask(correctWord))
                .remainingAttempts(3)
                .status(null)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.guess(gameId, gameRequest);

        assertNotNull(result);
        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(correctWord, result.getMaskedWord());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    @DisplayName("Should reveal the guessed letter in the masked word")
    void testGuess_SingleCorrectLetter() {
        Long gameId = 2L;
        String correctWord = "example";
        String maskedWord = "_x_____";

        GameRequest gameRequest = new GameRequest();
        gameRequest.setGuess("e");

        Game game = Game.builder()
                .id(gameId)
                .word(correctWord)
                .maskedWord(maskedWord)
                .remainingAttempts(3)
                .status(null)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.guess(gameId, gameRequest);

        assertNotNull(result);
        assertEquals("ex____e", result.getMaskedWord());
        assertEquals(3, result.getRemainingAttempts());
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    @DisplayName("Should reduce remaining attempts when an incorrect guess is made")
    void testGuess_IncorrectGuess() {
        Long gameId = 3L;
        String correctWord = "example";
        String maskedWord = "_______";

        GameRequest gameRequest = new GameRequest();
        gameRequest.setGuess("z");

        Game game = Game.builder()
                .id(gameId)
                .word(correctWord)
                .maskedWord(maskedWord)
                .remainingAttempts(3)
                .status(null)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.guess(gameId, gameRequest);

        assertNotNull(result);
        assertEquals(2, result.getRemainingAttempts());
        assertEquals(maskedWord, result.getMaskedWord());
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    @DisplayName("Should tag game as LOST when remaining attempts were exhausted")
    void testGuess_RemainingAttemptsExhausted() {
        Long gameId = 3L;
        String correctWord = "example";
        String maskedWord = "_______";

        GameRequest gameRequest = new GameRequest();
        gameRequest.setGuess("z");

        Game game = Game.builder()
                .id(gameId)
                .word(correctWord)
                .maskedWord(maskedWord)
                .remainingAttempts(1)
                .status(GameStatus.IN_PROGRESS)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.guess(gameId, gameRequest);

        assertNotNull(result);
        assertEquals(0, result.getRemainingAttempts());
        assertEquals(maskedWord, result.getMaskedWord());
        assertEquals(GameStatus.LOST, result.getStatus());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    @DisplayName("Should throw exception when guessing in a completed game")
    void testGuess_CompletedGameThrowsException() {
        Long gameId = 4L;

        GameRequest gameRequest = new GameRequest();
        gameRequest.setGuess("e");

        Game game = Game.builder()
                .id(gameId)
                .word("example")
                .maskedWord("_x__ple")
                .remainingAttempts(0)
                .status(GameStatus.LOST)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));

        assertThrows(GameAlreadyFinishedException.class, () -> gameService.guess(gameId, gameRequest));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Should throw exception when a multiple letter guess is made")
    void testGuess_MultipleLetterGuessThrowsException() {
        Long gameId = 5L;

        GameRequest gameRequest = new GameRequest();
        gameRequest.setGuess("ab");

        Game game = Game.builder()
                .id(gameId)
                .word("example")
                .maskedWord("_______")
                .remainingAttempts(3)
                .status(null)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));

        assertThrows(MultipleGuessLetterNotAllowedException.class, () -> gameService.guess(gameId, gameRequest));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Should update game status to LOST when a game is forfeited")
    void testForfeit_GameMarkedAsLost() {
        Long gameId = 6L;

        Game game = Game.builder()
                .id(gameId)
                .word("example")
                .maskedWord("_______")
                .remainingAttempts(5)
                .status(GameStatus.IN_PROGRESS)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.forfeit(gameId);

        assertNotNull(result);
        assertEquals(GameStatus.LOST, result.getStatus());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    @DisplayName("Should throw exception when forfeiting a completed game")
    void testForfeit_CompletedGameThrowsException() {
        Long gameId = 7L;

        Game game = Game.builder()
                .id(gameId)
                .word("example")
                .maskedWord("example")
                .remainingAttempts(0)
                .status(GameStatus.WON)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));

        assertThrows(GameAlreadyFinishedException.class, () -> gameService.forfeit(gameId));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Should return the list of top players sorted by remaining attempts in descending order")
    void testGetTopPlayers_WithWinners() {
        Game game1 = Game.builder()
                .id(1L)
                .player("Player1")
                .remainingAttempts(3)
                .status(GameStatus.WON)
                .build();

        Game game2 = Game.builder()
                .id(2L)
                .player("Player2")
                .remainingAttempts(5)
                .status(GameStatus.WON)
                .build();

        Game game3 = Game.builder()
                .id(3L)
                .player("Player3")
                .remainingAttempts(2)
                .status(GameStatus.WON)
                .build();

        when(gameRepository.findAllByStatus(GameStatus.WON)).thenReturn(List.of(game1, game2, game3));

        List<Game> result = gameService.getTopPlayers();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Player2", result.get(0).getPlayer());
        assertEquals("Player1", result.get(1).getPlayer());
        assertEquals("Player3", result.get(2).getPlayer());

        verify(gameRepository, times(1)).findAllByStatus(GameStatus.WON);
    }

    @Test
    @DisplayName("Should return an empty list when no winners are found")
    void testGetTopPlayers_WithNoWinners() {
        when(gameRepository.findAllByStatus(GameStatus.WON)).thenReturn(List.of());

        List<Game> result = gameService.getTopPlayers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(gameRepository, times(1)).findAllByStatus(GameStatus.WON);
    }
}