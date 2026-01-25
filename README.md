# Project-AMADEUS
<img width="320" height="320" alt="01" src="https://github.com/user-attachments/assets/c7dd60f3-78ab-411e-8094-7634d64dbf62" />

Beginner coders are often overly dependent on AI for debugging and logic building, which is a significant concern. While we cannot realistically stop beginners from using AI, we can influence how they use it.

AMADEUS was created to address this. It tracks your AI usage and encourages you to rely less on automated fixes and more on your own logic.

How AMADEUS Works
AMADEUS is a voice-activated coding assistant. When the trigger phrase (default: "Command Arise") is spoken, the assistant copies your code from the clipboard. It then uses the Google Gemini Model to analyze the code, identify errors, and suggest fixes.

**Key Limitation: To maintain security and focus, it can only access code through the clipboard.

How AMADEUS Reduces AI Dependency
According to the project's design philosophy, AMADEUS uses "Gamified Friction" to turn debugging into a learning experience rather than a shortcut:

Tiered Assistance (The "EXP" System): The assistant assigns a "cost" to help.

Command Arise (50 EXP): The AI acts as a strict mentor. It points out the error and gives a vague hint, but it refuses to write the fix for you.

Command Ascend (100 EXP): The AI provides a detailed conceptual guide and pseudo-code. You still have to write the final code yourself.

Command Absolute (250 EXP): The "last resort." This provides the full, corrected code block but carries the highest cost to your progression.

Active Learning: By making full solutions "expensive," the tool nudges users toward the Arise command, forcing them to engage with the logic and reinforce their learning.
<img width="320" height="320" alt="alpha" src="https://github.com/user-attachments/assets/6a9653c7-a0f1-4546-b621-d6214eb82dc7" />

The Point System:

Once the program has been started, it will earn points after a specific interval. These points when reach the maximum threshold will result in a LEVEL UP which will result in some change in the Avatar.
Everytime the trigger word is used some points will be deducted. If enough points are deducted it will lead to a loss of level.
AMADEUS also require a minimum of 500 points for it to work if points decrease below this threshold, it will lead to a freeze. The trigger word will not work unless the minimum threshold is reached again.

Keyword Detection:





** All art has been created using AI
