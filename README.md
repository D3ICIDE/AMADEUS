# AMADEUS

![Java](https://img.shields.io/badge/Java-24-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-25.0.1-blue)
![LLM](https://img.shields.io/badge/LLM-Google%20Gemini%20%2F%20Gemma-purple)
![Porcupine](https://img.shields.io/badge/Picovoice-Porcupine%203.0.5-red)
![Status](https://img.shields.io/badge/Status-Beta-green)

![Amadeus Avatar](https://github.com/user-attachments/assets/c7dd60f3-78ab-411e-8094-7634d64dbf62)

*"Don't just fix the code. Evolve the coder."*

---

## The Problem

Beginner coders are becoming overly dependent on AI for debugging and logic building. We can't realistically stop beginners from using AI, but we can influence **how** they use it.

**AMADEUS** puts a price on AI help. Every request costs points, points are earned slowly over time, and the more hand-holding you ask for, the more it costs. The goal is to nudge you toward working through problems yourself.

## How It Works

AMADEUS is a Java desktop assistant (JavaFX) that you control by voice.

1. **Trigger:** Picovoice Porcupine listens offline for one of three voice commands (*Arise*, *Ascend*, *Absolute*). Each command is its own wake word and also selects the help tier.
2. **Capture:** On a match, AMADEUS reads the text currently in your **clipboard** (copy your code first).
3. **Analyze:** The code is sent to a Google model, with a system prompt specific to the tier you asked for. The model is chosen per tier (see below).
4. **Respond:** The answer streams into a popup window, formatted as Markdown.

A 25-second cooldown applies between voice triggers, so one command isn't fired twice by accident.

---

## Gamification Mechanics

### The Point System

- **Starting balance:** 500 EXP.
- **Passive gains:** EXP is added automatically on a timer: a random 40-49 EXP (picked once per launch) every 10 seconds, starting 5 seconds after launch. *Gains are time-based, not activity-based; AMADEUS does not currently detect what you're doing.*
- **Spending:** Each command deducts its cost *before* the request is sent. If your balance is lower than the cost of the command, the request is refused with a "Low EXP" message and nothing is deducted.
- **Levels:** Your avatar evolves with your EXP, and drops back down if you spend below a threshold ("LEVEL UP" / "LEVEL DOWN" notifications).

| Level | EXP range |
|:------|:----------|
| 0 | below 500 |
| 1 | 500 - 799 |
| 2 | 800 - 1200 |
| 3 | 1201 - 1800 |
| 4 | above 1800 |

- **Persistence:** EXP is held in memory only. It resets to 500 each time the app starts.

### Cost Of Help (Voice Commands)

| Command        | Cost        | Model (as configured in `AMADEUS_AI.java`) | Description |
|:---------------|:------------|:-------------------------------------------|:------------|
| **"Arise"**    | **50 EXP**  | `gemini-3.1-flash-lite` | **Mentor Mode.** Finds the most critical issue, gives the line number and reasoning, and one actionable hint. Refuses to write the fix. |
| **"Ascend"**   | **100 EXP** | `gemini-3.6-flash` | **Guide Mode.** Diagnoses the error and gives a step-by-step conceptual guide. Pseudo-code or small snippets are allowed, but not the full corrected block. |
| **"Absolute"** | **250 EXP** | `gemma-4-26b-a4b-it` | **God Mode.** The last resort. Lists every fix and returns the full corrected code. |

![Alpha Avatar](https://github.com/user-attachments/assets/6a9653c7-a0f1-4546-b621-d6214eb82dc7)

*Example: Level 1 Alpha Avatar*

**NOTE:** AI has been used for all art.

### Testing without a microphone

The avatar window has a small **TEST** button (bottom right) that cycles through Arise, Ascend and Absolute and triggers each one without voice. It follows the same cost rules as a spoken command.

---

## Installation & Setup

### Prerequisites

- **JDK 24** or higher.
- **Maven 3.9+**.
- **Picovoice Access Key** ([Picovoice Console](https://console.picovoice.ai/)).
- **Google Gemini API Key** ([Google AI Studio](https://aistudio.google.com/)).
- A working microphone.

### 1. Clone the Repository

```bash
git clone https://github.com/D3ICIDE/AMADEUS.git
cd AMADEUS
```

### 2. Configure API Keys

Create a `.env` file in the **project root** (the directory you run the app from). `src/main/resources/.env_example` shows the format:

```
PV_API_KEY=your_picovoice_key_here
GEMINI_API_KEY=your_gemini_key_here
```


### 3. Voice Setup (Required)

Porcupine needs to load the keyword (`.ppn`) files from your local file system.

- **Keyword files:** The repo bundles Windows keyword files for *Arise*, *Ascend* and *Absolute* under `models/`. On macOS or Linux, train the same three keywords in the [Picovoice Console](https://console.picovoice.ai/) and download the `.ppn` files for your OS.
- **Add files:** Unzip the files then open `models/` and add the files.

### 4. Build & Run

```bash
mvn clean compile
```

Then run `AMADEUS_UI` (the JavaFX `Application` class) from your IDE. A `javafx-maven-plugin` is not configured yet, so `mvn javafx:run` is not available.

---

## Known Limitations / Roadmap

- **EXP persistence** across launches.
- **Cross-platform keywords** and a configurable keyword path instead of hardcoded constants.
- **Maven run target** (`javafx-maven-plugin`) and removal of the unused template `org.example.Main`.
