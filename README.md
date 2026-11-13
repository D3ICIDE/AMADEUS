# AMADEUS

![Java](https://img.shields.io/badge/Java-24-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-25.0.1-blue)
![LLM](https://img.shields.io/badge/LLM-Google%20Gemini%20%2F%20Gemma-purple)
![Porcupine](https://img.shields.io/badge/Picovoice-Porcupine%203.0.5-red)
![Platform](https://img.shields.io/badge/Platform-Windows-blue)
![Status](https://img.shields.io/badge/Status-Beta-green)

![Amadeus Avatar](https://github.com/user-attachments/assets/c7dd60f3-78ab-411e-8094-7634d64dbf62)

*"Don't just fix the code. Evolve the coder."*

---

## The Problem

Beginner coders are becoming overly dependent on AI for debugging and logic building. We can't realistically stop beginners from using AI, but we can influence **how** they use it.

**AMADEUS** puts a price on AI help. Every request costs points, points are earned slowly over time, and the more hand-holding you ask for, the more it costs. The goal is to nudge you toward working through problems yourself.

## How It Works

AMADEUS is a Windows-only Java desktop assistant (JavaFX) that you control by voice.

1. **Trigger:** Picovoice Porcupine listens offline for one of three voice commands (*Arise*, *Ascend*, *Absolute*). Each command is its own wake word and also selects the help tier.
2. **Capture:** On a match, AMADEUS reads the text currently in your **clipboard** (copy your code first).
3. **Analyze:** The code is sent to a Google model, with a system prompt specific to the tier you asked for. The model is chosen per tier (see below).
4. **Respond:** The answer streams into a popup window, formatted as Markdown.

A 25-second cooldown applies between voice triggers, so one command isn't fired twice by accident.

---

## Gamification Mechanics

### The Point System

- **Starting balance:** 500 EXP.
- **Passive gains:** EXP is added automatically on a timer: a random 40-49 EXP (picked once per launch) every 60 seconds, starting 5 seconds after launch. *Gains are time-based, not activity-based; AMADEUS does not currently detect what you're doing.*
- **Spending:** Each command deducts its cost *before* the request is sent. If your balance is lower than the cost of the command, the request is refused with a "Low EXP" message and nothing is deducted. Once deducted, the cost is not refunded if the request fails (for example, an empty clipboard or an API error).
- **Levels:** Your avatar evolves with your EXP, and drops back down if you spend below a threshold. "LEVEL UP" / "LEVEL DOWN" notifications appear on level changes (except when climbing out of level 0).

| Level | EXP range |
|:------|:----------|
| 0 | below 500 |
| 1 | 500 - 799 |
| 2 | 800 - 1200 |
| 3 | 1201 - 1800 |
| 4 | above 1800 |

- **Persistence:** EXP is held in memory only. It resets to 500 each time the app starts.

### Cost Of Help (Voice Commands)

| Command        | Cost        | Model (as configured in `Ai.java`) | Description |
|:---------------|:------------|:-------------------------------------------|:------------|
| **"Command Arise"**    | **50 EXP**  | `gemini-3.1-flash-lite` | **Mentor Mode.** Finds the most critical issue, gives the line number and reasoning, and one actionable hint. Refuses to write the fix. |
| **"Command Ascend"**   | **100 EXP** | `gemini-3.6-flash` | **Guide Mode.** Diagnoses the error and gives a step-by-step conceptual guide. Pseudo-code or small snippets are allowed, but not the full corrected block. |
| **" Command Absolute"** | **250 EXP** | `gemma-4-26b-a4b-it` | **God Mode.** The last resort. Lists every fix and returns the full corrected code. |

![Alpha Avatar](https://github.com/user-attachments/assets/6a9653c7-a0f1-4546-b621-d6214eb82dc7)

*Example: Level 1 Alpha Avatar*

**NOTE:** AI has been used for all art.

### Testing without a microphone

The avatar window has a small **TEST** button (bottom right) that cycles through Arise, Ascend and Absolute and triggers each one without voice. It follows the same cost rules as a spoken command.

---

## Installation & Setup

### Prerequisites

- **Windows.** AMADEUS currently runs on Windows only.
- **JDK 24** or higher.
- **Maven 3.9+**.
- **Picovoice Access Key** ([Picovoice Console](https://console.picovoice.ai/)).
- **Google Gemini API Key** ([Google AI Studio](https://aistudio.google.com/)).
- A working microphone.
- An internet connection (needed for the Gemini API and for the response window's Markdown and syntax-highlighting libraries).

### 1. Clone the Repository

```bash
git clone https://github.com/D3ICIDE/AMADEUS.git
cd AMADEUS
```

### 2. Configure API Keys

Create a `.env` file in the **project root** (the directory you run the app from). The `.env_example` file in the project root shows the format:

```
PV_API_KEY=your_picovoice_key_here
GEMINI_API_KEY=your_gemini_key_here
```


### 3. Voice Setup (Required)

Porcupine needs to load the keyword (`.ppn`) files from your local file system.

- **Keyword files:** The repo bundles the Windows keyword files for *Arise*, *Ascend* and *Absolute* under `models/`. Keep them where they are: their paths are hardcoded in `VoiceRecognition.java` and are relative to the directory you run the app from.

### 4. Build & Run

```bash
mvn clean compile
```

Then run the `Launcher` class from your IDE (it starts the JavaFX `Ui` application). Run it from the project root so the `.env` file and the `models/` folder are found. A `javafx-maven-plugin` is not configured yet, so `mvn javafx:run` is not available.

---

## Known Limitations / Roadmap

- **Broken Voice Recognition** due to picovoice revoking its free tier and switching to B2B business model free api keys aren't available anymore. We are looking for a replacement
- **EXP persistence** across launches.
- **Configurable keyword path** instead of hardcoded constants.
- **Maven run target** (`javafx-maven-plugin`).
