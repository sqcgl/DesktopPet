# Pet Animation Application

This project is a Java Swing-based application that animates a virtual pet on the screen. The pet can perform various animations, such as idle, drag, and click-based movements. The application runs in a fullscreen window with transparent backgrounds, making the pet appear as an overlay on the screen.

## Features

- **Pet Animations**: The pet can perform different animations like idle, drag, and click responses, all represented by frame-based animations.
- **Fullscreen Overlay**: The application runs in fullscreen mode with a transparent background, so the pet moves freely on top of your screen without a window border.
- **Interactive**: The pet responds to user interactions like mouse clicks and drag events.
- **Smooth Animations**: The pet’s animations are frame-based and smoothly transition between states.

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Ensure you have Java 8 or later installed.
- **Swing and AWT Libraries**: These are typically included in most JDK distributions.

### Running the Application

1. Clone this repository to your local machine.
    ```bash
    git clone https://github.com/sqcgl/DesktopPet.git
    ```

2. Compile the Java files:
    ```bash
    javac Main.java Pet.java PetFrame.java
    ```

3. Run the application:
    ```bash
    java Main
    ```

### Project Structure

- **Main.java**: Entry point of the application. It launches the graphical interface in a separate thread using `SwingUtilities.invokeLater()`.
  
- **Pet.java**: The core class that handles pet animations. It loads various animation frames, handles user input (like mouse events), and manages the rendering of the animations on the screen.

- **PetFrame.java**: A `JFrame` that acts as the window for displaying the pet animation. It has a transparent background and allows the pet to move freely across the screen.

## Customization

You can add your own animations by modifying the `Pet.java` file. Simply load new frames into the appropriate animation lists (e.g., `idleFrames`, `dragRightFrames`) and ensure the application responds to user input accordingly.

## Future Improvements

- Add more interaction features, such as keyboard control or additional mouse events.
- Support for multiple pets or more complex animations.
- Improved performance for higher frame-rate animations.

