# AMADEUS 
![Java](https://img.shields.io/badge/Java-24-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-25.0.1-blue)
![Gemini](https://img.shields.io/badge/AI-Google%20Gemini%202.5-purple)
![Porcupine](https://img.shields.io/badge/Voice-Picovoice%203.0.5-red)
![Status](https://img.shields.io/badge/Status-Beta-green)

<div align="center">
  <img width="320" height="320" alt="Amadeus Avatar" src="https://github.com/user-attachments/assets/c7dd60f3-78ab-411e-8094-7634d64dbf62" />
  <br>
  <em>"Don't just fix the code. Evolve the coder."</em>
</div>

---

## The Problem
Beginner coders are becoming overly dependent on AI for debugging and logic building. While we cannot realistically stop beginners from using AI, we can influence **how** they use it.

**AMADEUS** tracks your AI usage and gamifies the experience, encouraging you to rely less on automated fixes and more on your own logic.

## How It Works
AMADEUS is a Java-based voice-activated assistant.
1.  **Trigger:** Uses **Picovoice Porcupine** for wake-word detection (offline & low latency).
2.  **Capture:** Once triggered, it captures the code currently in your clipboard.
3.  **Analyze:** It sends the code to **Google Gemini** for analysis.
4.  **Respond:** Based on your requested "tier" of help, it provides hints rather than direct solutions.

---

## Gamification Mechanics
AMADEUS transforms debugging from a passive shortcut into an active RPG.

### The Point System
* **Passive Gains:** Points are earned automatically over time while you are coding independently (monitored via active window tracking).
* **Progression:** Accumulating points triggers a **Level Up**, evolving your personal Avatar.
* **The Freeze:** You need a minimum of **500 points** to operate the AI. If you drop below this, AMADEUS freezes until you earn points back manually.
* **De-leveling:** Frequent AI use deducts points, potentially causing you to lose levels.

### Cost Of Help (Voice Commands)
Each command carries a specific point cost:

| Command | Cost | Description |
| :--- | :--- | :--- |
| **"Arise"** | **50 pts** | **Mentor Mode.** Identifies the error and provides a hint. Refuses to write code. |
| **"Ascend"** | **100 pts** | **Guide Mode.** Provides a conceptual guide and pseudo-code. You implement the logic. |
| **"Absolute"** | **250 pts** | **God Mode.** The last resort. Provides the full corrected code block. High cost. |

<div align="center">
  <img width="200" alt="Alpha Avatar" src="https://github.com/user-attachments/assets/6a9653c7-a0f1-4546-b621-d6214eb82dc7" />
  <p><em>Example: Level 1 Alpha Avatar</em></p>
</div>

---

## Installation & Setup

### Prerequisites
* **Java Development Kit (JDK) 24** or higher.
* **Maven 3.9+** .
* **Picovoice Access Key** (Get it from [Picovoice Console](https://console.picovoice.ai/)).
* **Google Gemini API Key** (Get it from [Google AI Studio](https://aistudio.google.com/)).

### 1. Clone the Repository
```bash
git clone https://github.com/D3ICIDE/AMADEUS_0.5.git
cd AMADEUS_0.5
```
### 2. Configure API Keys
Create .env files for your API Keys:
```bash
PV_ACCESS_KEY=your_picovoice_key_here
GEMINI_API_KEY=your_gemini_key_here
```
### 3. Voice Setup (Required)
AMADEUS uses **Picovoice Porcupine** for wake-word detection. Since Porcupine requires direct access to your local file system, follow these steps:

* **Train Your Keywords**
Sign up at the [Picovoice Console](https://console.picovoice.ai/), train your desired commands (e.g., "Arise"), and download the `.ppn` files for your specific OS (Windows/Mac/Linux).

* **Organize Models**
Place your downloaded `.ppn` files into a stable folder on your hard drive (e.g., `C:/AMADEUS/Models/`).

* **Update paths in AMADEUS_VOICE_RECOG**
Open `src/main/java/com/AMADEUS/phase/AMADEUS_VOICE_RECOG.java` and update the absolute file paths to point to your local `.ppn` files.

### 4. Build & Run
Finally, use Maven to compile and launch the JavaFX interface:

```bash
mvn clean compile
mvn javafx:run
```


