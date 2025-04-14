# CodeWords - Word Guessing Game API
This guessing game is a fun JSON API game built with Spring Boot where players guess hidden words. This project demonstrates simple API design, state management, and clean coding.

## 🎮 What is CodeWords?
**CodeWords** is a simple, turn-based word-guessing game played entirely through a RESTful JSON API. Think of it as a mashup between **Hangman** and **Wordle**, but without the graphics—just clean, logical backend interaction.

### 🧩 How the Game Works

1. **Game Start**
   - When a new game is started, the backend randomly selects a word (e.g., "apple").
   - The player sees a **masked version** of the word: `_ _ _ _ _` (one underscore per letter).
   - The player has a limited number of attempts (e.g., 6) to guess the word.

2. **Making Guesses**
   - Players can guess a **single letter** (e.g., "a") or try to guess the **entire word** (e.g., "apple").
   - Each incorrect guess reduces the number of remaining attempts by 1.
   - Correct letter guesses reveal that letter in its correct position(s) within the masked word.
   - If the player correctly guesses the entire word, the game ends immediately with a **WIN**.

3. **Game End Conditions**
   - **Win:** The player uncovers the entire word (via letter guesses or full word guess).
   - **Lose:** The player runs out of attempts before guessing the full word.

4. **Game Status**
   - The current status of the game can be:
     - `IN_PROGRESS`
     - `WON`
     - `LOST`

### 🧠 Example Game Flow

- Start a new game:
  - Server picks "apple" → masked: `_ _ _ _ _`, attempts: 6
- Guess "a" → masked: `a _ _ _ _`, attempts: 6
- Guess "e" → masked: `a _ _ _ e`, attempts: 6
- Guess "i" → incorrect, attempts: 5
- Guess "apple" → correct! Game ends: `WON`

---

## 🚀 How to Run

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

---

## 🧪 API Endpoints

### 🎮 Start a New Game
**POST** `/game`

**Response:**
```json
{
  "gameId": "abc123",
  "maskedWord": "_ _ _ _ _",
  "remainingAttempts": 6
}
```

---

### 🔡 Make a Guess
**POST** `/game/{gameId}/guess`

**Body:**
```json
{ "guess": "e" }
```

**Response:**
```json
{
  "gameId": "abc123",
  "maskedWord": "_ _ e _ _",
  "remainingAttempts": 5,
  "status": "IN_PROGRESS"
}
```

---

### 📄 Get Game State
**GET** `/game/{gameId}`

**Response:**
```json
{
  "maskedWord": "a p p l e",
  "remainingAttempts": 3,
  "status": "WON"
}
```

---

## 🎯 Game Rules
- Each game starts with a hidden word (e.g., "apple").
- The user can guess a **letter** or the **full word**.
- A wrong guess reduces the remaining attempts.
- Game ends when the word is guessed or attempts run out.

---

## 🔧 Tech Stack
- Java 17+
- Spring Boot
- Maven
- In-memory storage (no DB)

---

## ✨ Bonus Features (Optional)
- Random word generation
- Forfeit a game
- Leaderboard support
- Multi-player support
- Adjustable difficulty

---

## 🧊 Example curl Commands
```bash
curl -X POST http://localhost:8080/game

curl -X POST http://localhost:8080/game/abc123/guess \
     -H "Content-Type: application/json" \
     -d '{"guess":"e"}'

curl http://localhost:8080/game/abc123
```

---
