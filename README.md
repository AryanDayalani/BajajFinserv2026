# 🚀 Quiz Leaderboard System

## 📌 Overview
This project implements a backend-style solution for a Quiz Leaderboard System. It consumes API data across multiple polls, handles duplicate responses, aggregates scores, and submits a final leaderboard.

The system simulates real-world distributed systems where duplicate events may occur, requiring idempotent processing.

---

## 🎯 Objective
- Poll the API **10 times (poll = 0 to 9)**
- Collect quiz event data
- Remove duplicate events
- Compute:
  - Total score per participant
  - Leaderboard (sorted descending)
  - Total combined score
- Submit results to validator API

---

## ⚙️ Tech Stack
- Language: Java (JDK 11+)
- HTTP Client: `java.net.http.HttpClient`
- Parsing: Regex-based (no external libraries)

---

## 🧠 Key Concepts

### Idempotent Processing
Duplicate API events are handled using a unique key:
```
(roundId + participant)
```
This ensures each event is processed only once.

---

### Data Structures Used

| Purpose            | Structure              |
|-------------------|----------------------|
| Deduplication     | `Set<String>`         |
| Aggregation       | `Map<String, Integer>`|
| Leaderboard       | `List<Map.Entry>`     |

---

## 🔁 Processing Flow
```
API Polling (0–9)
        ↓
Collect Responses
        ↓
Deduplicate Events
        ↓
Aggregate Scores
        ↓
Sort Leaderboard
        ↓
Submit Result
```

---

## ▶️ How to Run

### Compile
```
javac QuizLeaderboard.java
```

### Run
```
java QuizLeaderboard
```

⏱ Runtime: ~50 seconds (due to 5-second delay between polls)

---

## 🧪 Sample Output
```
Polling: 0
Added: R1_Alice -> 10
Duplicate ignored: R1_Alice
...

Final Leaderboard:
Bob -> 120
Alice -> 100

Submission Response:
{
  "isCorrect": true,
  "submittedTotal": 220,
  "expectedTotal": 220,
  "message": "Correct!"
}
```

---

## ⚠️ Important Notes
- Must execute exactly **10 polls**
- Must include **5-second delay** between calls
- Must handle duplicate events correctly
- Leaderboard must be sorted in **descending order**
- Submit only once

---

## 🚧 Challenges
- Handling duplicate API responses
- Parsing JSON without external libraries
- Ensuring correct aggregation across multiple polls

---

## 💡 Design Decisions
- Used regex parsing instead of fragile string splitting
- Avoided external dependencies for standalone execution
- Maintained clean logic separation:
  - Polling
  - Deduplication
  - Aggregation
  - Submission

---

## 🌍 Real-World Relevance
This problem reflects real backend scenarios:
- Distributed systems producing duplicate events
- Idempotent data processing
- Event aggregation pipelines

---

## 🔮 Future Improvements
- Use Jackson for robust JSON parsing
- Add retry mechanism for API failures
- Add structured logging
- Convert into Spring Boot service

---

## 🔗 Repository
https://github.com/your-username/quiz-leaderboard-system

---

## 👤 Author
Name: Aryan  
Registration Number: 2024CS101
