# Installtion

## Steps

### 1. Download and install [Eclipse IDE](https://www.eclipse.org/downloads/)

Any version is supported.

### 2. Download BattleArena from Github.

Head over to [BattleArena](https://github.com/Sanavesa/BattleArena) and click on the "Clone or Download" green button.

![Image of Step2](https://i.gyazo.com/c2316eabf4d01f9e44634208699a929a.png)

### 3. Extract the project and open up Eclipse.

### 4. Import BattleArena into Eclipse.

#### 4.1. Click on File > Open Projects from File System.

![Image of Step4.1](https://i.gyazo.com/488ed51ab0bcc14319fb1fc800c18b29.png)

#### 4.2. Import the project into Eclipse.

Locate the extracted folder that you downloaded from Step 2. Select the BattleArena-master folder (it is the one that has the src and doc folders inside)

Import Source should be something along the lines of: "C:\Users\Sanavesa\Desktop\Programming Projects\BattleArena-master".

#### 4.3 Press Finish to import the project into Eclipse.

### 5. Download [JavaFX SDK](https://www.dropbox.com/sh/kl51twzqsx5lham/AAC_-g__HOuY1rDDZF4TreeGa?dl=0).

Extract it also.

### 6. Configure JavaFX with our project.

#### 6.1. In Eclipse, right click your project (BattleArena) and choose Build Path > Configure Build Path.

![Image of Step6.1](https://i.gyazo.com/5096090c34e26fc4537bda8d0a58da3d.png)

#### 6.2. Under Modulepath, if you see JavaFX12, select it and click Remove.

#### 6.3. Select Modulepath, then click Add Library.

#### 6.4. Select User Library then press Next.

#### 6.5. Press User Libraries, and click New. Name the library "JavaFX".

#### 6.6. Select your newly created JavaFX library, and clicking Add External Jars.

#### 6.7. Locate the extracted JavaFX SDK you downloaded from Step5, and import all the jars.

In javafx-sdk-13 folder, open the lib folder. Select all .jar files when importing.

![Image of Step6.7](https://i.gyazo.com/33f23756a1540215c4321988c649e9c9.png)

#### 6.8. Press Apply and Close, then press Finish.

You should see JavaFX beneath Modulepath.

### 7. Configure Run Configuration with JavaFX.

#### 7.1. From the menu bar at the top, click on Run > Run Configurations.

#### 7.2. Select Java Application, then press add New Launch Configuration

![Image of Step7.2](https://i.gyazo.com/1a5fe1d251f7ce2523e8381d2ee8d52b.png)

#### 7.3. Fill the details of the configuration as such:

Name = BattleArenaConfig

Project = BattleArena

Main Class = Main

![Image of Step7.3](https://i.gyazo.com/d5519d7b9986284bed4a0e86a398a30f.png)

#### 7.4. Select Arguments tab, and paste the following in Arguments > VM Arguments.
--module-path ${PATH_TO_FX} --add-modules=ALL-MODULE-PATH

![Image of Step7.4](https://i.gyazo.com/3da42f4de4fbce2409e508a226a5d4d2.png)

#### 7.5. Press Apply then Close.

### 8. Run the application by opening BattleArena > scr > (default package) > Main.java, and press the green run button.

![Image of Step8](https://i.gyazo.com/dd15c5b859d61f2d407a60e333fb1645.png)
