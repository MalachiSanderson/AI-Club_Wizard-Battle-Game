# BattleArena
 
 ![Image of Battle Arena](https://cdn.discordapp.com/attachments/503002454418259968/633374772906033191/Screen_Shot_2019-10-14_at_2.33.14_PM.png)
 
## Introduction

Battle Arena is a deathmatch game where two players fight to the death. Users create their own AI agent that can participate in the competition of ERAU Artificial Intelligence Club 2019.
 
## Rules
The following are the subsections of rules for the Battle Arena game.

### Map
The map is a 20x20 grid that is vertically symmetrical. The map is randomly generated for each game, populated with walls and health packs. Top-left coordinate is (0,0) and the bottom-right is (19,19). The y-axis is inverted.

### Round
The game is round-based, meaning that each round both players perform their actions simultaneously.

### Storm

![Image of Storm](https://media.discordapp.net/attachments/503002454418259968/633375622206586881/Screen_Shot_2019-10-14_at_2.48.13_PM.png)

Every 20 rounds, the lava storm will advance by 1 block. The storm is the number of diagonal blocks from the corners of the map.
If a player comes into contact with a storm, they will die instantly and horribly. We wall want our wizards to go to heaven, don't we? But <i>noooo</i>, they die a fiery death and have no after-life. :(

### Health
![Image of Player Health](https://cdn.discordapp.com/attachments/503002454418259968/635693739825954816/Untitled.png)

Health represents the amount of damage that a player can take before dying. When a player comes into contact with a projectile or a mine, they lose 1 health point. When their health reaches 0, they die an honorable death. The player starts with 3 health points, and can reach a maximum of 5 health points via health packs.

#### Health Packs
![Image of Health Pack](https://cdn.discordapp.com/attachments/503002454418259968/633375649586872349/Screen_Shot_2019-10-14_at_2.48.21_PM.png)

Health packs are scattered around the map. When a player comes into contact with it, they will gain 1 health point.

### Actions
An action is what a AI agent can perform in a single round. Only 1 action can be performed per round. If no actions are executed, a <code>Action.NoAction</code> is performed.

#### Movement
A player can move in 4 directions: up, down, left, right. They can only move to a cell that doesn't contain a wall.

#### Shooting
![Image of Shooting](https://cdn.discordapp.com/attachments/503002454418259968/633380728826232840/piccccccccccccc.PNG)

A player can shoot in 4 directions: up, down, left, right. Projectiles can collide with other hostile projectiles and mines, destroying both in the process. Projectiles will move 1 unit in the specified direction per round, matching the player's movement speed. Also, if a projectile comes into contact with a health pack, the shooter will gain +1 health point. After you shoot 1 shot there will be a 2 round shot cool down in which the player will not be allowed to shoot until the cool down timer is over ( only can shoot once per 3 rounds ).

#### Placing Mines
![Image of Mine](https://cdn.discordapp.com/attachments/503002454418259968/633378425783975937/Screen_Shot_2019-10-14_at_2.59.35_PM.png)

A player can place a mine in 4 directions: up, down, left, right. A mine does not move and will explode when it comes into contact with a player, another mine, and any projectile. Placing a mine has a cooldown of 20 rounds; meaning, that it can only be placed once every 10 rounds.

## API
For the complete API available to user, head to [API Link](https://sanavesa.github.io).

## Installation
For the installation instructions (only for Eclipse2019 or newer), head to [Installation](Installation.md).

![Gif of Gameplay](https://cdn.discordapp.com/attachments/503002454418259968/633385448382791690/gameplay.gif)
