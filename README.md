
![image](https://github.com/user-attachments/assets/b9bc4f4b-4150-4fb0-98d4-01b267c3f3ce)


# Android_Ski_Game
Ski Game is an Android mobile application that simulates a skiing experience. Players control a skier character, navigating through trees as obstecles and collecting hot choco as a prize while descending a virtual slope.

# **Features**

Two control modes:

1. Button-based controls (MainActivity)
2. Tilt-based controls using device accelerometer (TiltcontrolActivity)


1. Obstacle avoidance (trees)
2. Reward collection (hot chocolate)
3. Score tracking
4. Lives system
5. Pause menu
6. Game over screen
7. Scoreboard with location tracking
8. Adjustable game speed (slow/fast modes)

# **Technical Details**

Developed for Android platform
Uses Android's Sensor API for tilt controls
Implements Google Maps for scoreboard location display
Utilizes SharedPreferences for local data storage
Incorporates sound effects for enhanced user experience

# **Main Components**

1. **MainActivity:** Handles the main game logic with button controls.
2. **TiltcontrolActivity:** Implements tilt-based controls using the device's accelerometer.
3. **MenuActivity:** Provides options to start the game, view scoreboard, and select game speed.
4. **ScoreboardActivity:** Displays high scores and their locations on a map.
5. **GameManager:** Manages the core game logic, including object movement and collision detection.
6. **LocationHandler:** Handles location services and permissions for scoreboard features.
7. **ScoreBoardManager:** Manages the storing and retrieving of high scores.
8. **SoundPlayer:** Handles sound effects for the game.

# **How to Play**

1. Start the game from the main menu.
2. Choose between button controls or tilt controls.
3. Navigate the skier left or right to avoid trees and collect hot chocolate.
4. Game ends when all lives are lost.
5. Enter your name to save your score on the leaderboard.
