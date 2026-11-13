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

## Download

**[⬇ Download the latest Windows release](https://github.com/D3ICIDE/AMADEUS/releases/latest)**

No Java install needed. It's bundled in the download (about 170 MB zipped, about 324 MB unzipped).

> **Voice activation is currently unavailable.** AMADEUS uses Picovoice Porcupine for wake-word detection. Picovoice's free-tier AccessKeys stopped working after June 30, 2026, and the company is shifting to an enterprise (B2B) model, so wake words can't start with a free key. **Use the TEST button in the app to trigger each mode instead** (see [Using the TEST button](#using-the-test-button)). Replacing the wake-word engine is on the roadmap.

### Quick setup

1. Download `AMADEUS-0.1.0-windows.zip` from the [Releases page](https://github.com/D3ICIDE/AMADEUS/releases/latest) and unzip it.
2. Get a free **Gemini API key** from [Google AI Studio](https://aistudio.google.com/apikey).
3. Open the `.env` file inside the unzipped `AMADEUS` folder in Notepad, paste your key after the `=`, and save:
   ```
   GEMINI_API_KEY=your_gemini_key_here
   ```
   Leave `PV_API_KEY` blank unless you have a working Picovoice key (see [Known limitations](#known-limitations--roadmap)).
4. Run **`AMADEUS.exe`**.
5. Copy some code, click **TEST** to pick a mode, and read the answer in the popup.

**Windows SmartScreen** may warn about an unknown app because the build isn't code-signed. Click **More info**, then **Run anyway**.

**Keep your keys private.** Never share your `.env` file or post screenshots of it.

---

## The Problem

Beginner coders are becoming overly dependent on AI for debugging and logic building. We can't realistically stop beginners from using AI, but we can influence **how** they use it.

**AMADEUS** puts a price on AI help. Every request costs points, points are earned slowly over time, and the more hand-holding you ask for, the more it costs. The goal is to nudge you toward working through problems yourself.

## How It Works

AMADEUS is a Windows-only Java desktop assistant (JavaFX) designed to be triggered by voice. See the note above: voice activation currently needs a valid Picovoice key, so the in-app TEST button is the way to trigger it for most users.

1. **Trigger:** Picovoice Porcupine listens offline for one of three voice commands (*Arise*, *Ascend*, *Absolute*). Each command is its own wake word and also selects the help tier. (The **TEST** button triggers the same three modes without voice.)
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
| **"Arise"**    | **50 EXP**  | `gemini-3.1-flash-lite` | **Mentor Mode.** Finds the most critical issue, gives the line number and reasoning, and one actionable hint. Refuses to write the fix. |
| **"Ascend"**   | **100 EXP** | `gemini-3.6-flash` | **Guide Mode.** Diagnoses the error and gives a step-by-step conceptual guide. Pseudo-code or small snippets are allowed, but not the full corrected block. |
| **"Absolute"** | **250 EXP** | `gemma-4-26b-a4b-it` | **God Mode.** The last resort. Lists every fix and returns the full corrected code. |

![Alpha Avatar](https://github.com/user-attachments/assets/6a9653c7-a0f1-4546-b621-d6214eb82dc7)

*Example: Level 1 Alpha Avatar*

**NOTE:** AI has been used for all art.

### Using the TEST button

The avatar window has a small **TEST** button (bottom right) that cycles through Arise, Ascend and Absolute and triggers each one without voice. It follows the same cost rules as a spoken command. Because voice activation is currently unavailable without a valid Picovoice key, this is the main way to use AMADEUS.

---

## Build From Source

If you only want to use AMADEUS, the [Download](#download) section above is all you need. This section is for building it yourself.

### Prerequisites

- **Windows.** AMADEUS currently runs on Windows only.
- **JDK 24** or higher.
- **Maven 3.9+**, or the Maven support built into IntelliJ IDEA.
- **Google Gemini API Key** ([Google AI Studio](https://aistudio.google.com/)).
- **Picovoice Access Key** (optional, for voice activation only). Free-tier keys no longer work, so wake words need a key from a paid or enterprise plan. See the [Picovoice Console](https://console.picovoice.ai/).
- A working microphone (only needed for voice activation).
- An internet connection (needed for the Gemini API and for the response window's Markdown and syntax-highlighting libraries).

### 1. Clone the Repository

```bash
git clone https://github.com/D3ICIDE/AMADEUS.git
cd AMADEUS
```

### 2. Configure API Keys

Create a `.env` file in the **project root** (the directory you run the app from). The `.env_example` file in the project root shows the format:

```
GEMINI_API_KEY=your_gemini_key_here
PV_API_KEY=your_picovoice_key_here
```

`PV_API_KEY` can stay blank if you don't have a working Picovoice key. The app will print a Porcupine error at startup and carry on, and you can use the TEST button instead.

Environment variables with the same names take priority over the `.env` file, so if a key you've set in Windows or your IDE is outdated, it will override what's in the file.

### 3. Voice Setup (Optional)

This only matters if you have a working Picovoice key. Porcupine needs to load the keyword (`.ppn`) files from your local file system.

- **Keyword files:** The repo bundles the Windows keyword files for *Arise*, *Ascend* and *Absolute* under `models/`. Keep them where they are: their paths are hardcoded in `VoiceRecognition.java` and are relative to the directory you run the app from.

### 4. Build & Run

```bash
mvn clean compile
```

Then run the `Launcher` class from your IDE (it starts the JavaFX `Ui` application). Run it from the project root so the `.env` file and the `models/` folder are found. A `javafx-maven-plugin` is not configured yet, so `mvn javafx:run` is not available.

---

## Known Limitations / Roadmap

- **Voice activation is currently unavailable for most users.** Picovoice's free-tier AccessKeys stopped working after June 30, 2026, and the company is focusing on enterprise (B2B) customers. Use the TEST button as a fallback.
- **Replace the wake-word engine** with an alternative that doesn't depend on a paid key.
- **EXP persistence** across launches.
- **Configurable keyword path** instead of hardcoded constants.
- **Maven run target** (`javafx-maven-plugin`).
- **Smaller release download** by shipping a trimmed Java runtime instead of a full JDK.
