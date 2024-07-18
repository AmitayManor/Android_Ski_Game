# Android_Ski_Game
Ski Game is an Android mobile application that simulates a skiing experience. Players control a skier character, navigating through obstacles and collecting rewards while descending a virtual slope.

# **Features**

Two control modes:

Button-based controls (MainActivity)
Tilt-based controls using device accelerometer (TiltcontrolActivity)


Obstacle avoidance (trees)
Reward collection (hot chocolate)
Score tracking
Lives system
Pause menu
Game over screen
Scoreboard with location tracking
Adjustable game speed (slow/fast modes)

# **Technical Details**

Developed for Android platform
Uses Android's Sensor API for tilt controls
Implements Google Maps for scoreboard location display
Utilizes SharedPreferences for local data storage
Incorporates sound effects for enhanced user experience

# **Main Components**

MainActivity: Handles the main game logic with button controls.
TiltcontrolActivity: Implements tilt-based controls using the device's accelerometer.
MenuActivity: Provides options to start the game, view scoreboard, and select game speed.
ScoreboardActivity: Displays high scores and their locations on a map.
GameManager: Manages the core game logic, including object movement and collision detection.
LocationHandler: Handles location services and permissions for scoreboard features.
ScoreBoardManager: Manages the storing and retrieving of high scores.
SoundPlayer: Handles sound effects for the game.

# **How to Play**

Start the game from the main menu.
Choose between button controls or tilt controls.
Navigate the skier left or right to avoid trees and collect hot chocolate.
Game ends when all lives are lost.
Enter your name to save your score on the leaderboard.
