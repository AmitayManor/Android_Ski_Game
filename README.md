
![image](https://github.com/user-attachments/assets/b9bc4f4b-4150-4fb0-98d4-01b267c3f3ce)
![image](https://github.com/user-attachments/assets/15e78d6e-0ac3-48fd-948a-2ef804391928)
![image](https://github.com/user-attachments/assets/6d671969-fd9d-4720-b219-fcd25348f773)
![image](https://github.com/user-attachments/assets/2ba2c8f7-d396-4ef2-b560-5d34e5ed334f)
![image](https://github.com/user-attachments/assets/31e93daa-9700-4985-bc9a-b749bcef1ad2)









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
