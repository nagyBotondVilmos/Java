# Author
Nagy Botond-Vilmos

# Minesweeper
This is a simple minesweeper game written in Java.
It has GUI based on swing and awt.
When you start the game, you are presented with a menu:
- `New Game`:
you can choose the size of the board and the number of mines.
- `Leaderboard`:
you can see the previous games and their times.
- `Exit`:
you can exit the game.

# Running the game
To run the game, you need to have a JDK installed on your computer.
If you don't want to install a JDK, you can run the game in an IDE like IntelliJ IDEA.
My suggestion is to use IntelliJ IDEA, because it is the most simple way to run the game.
Otherwise, you can use the command line to compile and run the game, but it is a bit more complicated and I don't recommend it.
If you really want to use the command line, you can use the following commands:
Windows (PowerShell):
```bash
javac -d out -sourcepath src src\*.java
java -cp out MinesweeperUI
```
Linux (Bash):
```bash
javac -d out -sourcepath src src/*.java
java -cp out MinesweeperUI
```

# Final notes
I hope you will enjoy the game and have fun playing it :).

[Back](../README.md)