import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class Pet extends JPanel {
    private static final long serialVersionUID = 1L;

    // Lists to hold frames for different animations
    private List<BufferedImage> idleFrames;
    private List<BufferedImage> dragRightFrames;
    private List<BufferedImage> dragLeftFrames;
    private List<BufferedImage> downRightFrames;
    private List<BufferedImage> downLeftFrames;
    private List<BufferedImage> lowerLeftFrames;
    private List<BufferedImage> lowerLeftDropFrames;
    private List<BufferedImage> lowerRightFrames;
    private List<BufferedImage> lowerRightDropFrames;
    private List<BufferedImage> clickLeftUpperFrames;
    private List<BufferedImage> clickRightUpperFrames;
    private List<BufferedImage> clickLeftLowerFrames;
    private List<BufferedImage> clickRightLowerFrames;

    // Frames for drag stop animations
    private BufferedImage dragRightStopFrame;
    private BufferedImage dragLeftStopFrame;
    private BufferedImage lowerLeftStopFrame;
    private BufferedImage lowerRightStopFrame;

    // Current animation frames and index
    private List<BufferedImage> currentFrames;
    private int currentFrame;

    // Dimensions and position
    private int frameWidth;
    private int frameHeight;
    private int x, y;

    // Dragging state
    private boolean isDragging = false;
    private boolean dragAnimationFinished = true; // Initialize to true
    private boolean draggingRight = false;
    private boolean draggingDown = false;
    private int offsetX, offsetY;

    // Timers for animation and click detection
    private Timer animationTimer;
    private Timer clickTimer;
    private boolean isLongPress = false;

    // Queue for animations
    private Queue<Runnable> animationQueue = new LinkedList<>();

    public Pet() {
        try {
            // Load animation frames
            idleFrames = readGif(new File("src/resources/stand.gif"));
            dragLeftFrames = readGif(new File("src/resources/drag.gif"));
            dragRightFrames = mirrorFrames(dragLeftFrames);
            downLeftFrames = readGif(new File("src/resources/dragEnd.gif"));
            downRightFrames = mirrorFrames(downLeftFrames);
            dragLeftStopFrame = ImageIO.read(new File("src/resources/dragStop.gif"));
            dragRightStopFrame = mirrorImage(dragLeftStopFrame);
            lowerLeftFrames = readGif(new File("src/resources/lowerDrag.gif"));
            lowerLeftStopFrame = ImageIO.read(new File("src/resources/lowerDragStop.gif"));
            lowerLeftDropFrames = readGif(new File("src/resources/lowerDrop.gif"));
            lowerRightFrames = mirrorFrames(lowerLeftFrames);
            lowerRightStopFrame = mirrorImage(lowerLeftStopFrame);
            lowerRightDropFrames = mirrorFrames(lowerLeftDropFrames);
            clickLeftUpperFrames = readGif(new File("src/resources/click.gif"));
            clickRightUpperFrames = mirrorFrames(clickLeftUpperFrames);
            clickLeftLowerFrames = readGif(new File("src/resources/lowerClick.gif"));
            clickRightLowerFrames = mirrorFrames(clickLeftLowerFrames);

            // Set initial frames to idle animation
            currentFrames = idleFrames;
            if (!idleFrames.isEmpty()) {
                frameWidth = idleFrames.get(0).getWidth();
                frameHeight = idleFrames.get(0).getHeight();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentFrame = 0;
        x = 100;
        y = 100;

        // Mouse listeners for click and drag detection
        addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent evt) {
                isLongPress = false;
                clickTimer = new Timer(500, e -> {
                    isLongPress = true;
                    startDrag(evt);
                    clickTimer.stop();
                });
                clickTimer.setRepeats(false);
                clickTimer.start();
            }

            public void mouseReleased(MouseEvent evt) {
                if (clickTimer != null && clickTimer.isRunning()) {
                    clickTimer.stop();
                    if (!isLongPress) {
                        handleClick(evt);
                    }
                }
                if (isDragging) {
                    stopDrag(evt);
                }
            }

            public void mouseClicked(MouseEvent evt) {}
            public void mouseEntered(MouseEvent evt) {}
            public void mouseExited(MouseEvent evt) {}
        });

        addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent evt) {
                if (isDragging) {
                    x = evt.getXOnScreen() - offsetX;
                    y = evt.getYOnScreen() - offsetY;
                    setLocation(x, y); // Update position
                }
            }
            public void mouseMoved(MouseEvent evt) {}
        });

        // Timer for handling animation frame updates
        animationTimer = new Timer(100, e -> {
            if (dragAnimationFinished || !isDragging) {
                currentFrame = (currentFrame + 1) % currentFrames.size();
            } else if (currentFrame < currentFrames.size() - 1) {
                currentFrame++;
            }
            repaint();
        });
        animationTimer.start();
    }

    // Start dragging based on mouse position
    private void startDrag(MouseEvent evt) {
        enqueueAnimation(() -> {
            if (isInsideRightUpper(evt.getX(), evt.getY())) {
                isDragging = true;
                draggingRight = true;
                draggingDown = false;
                dragAnimationFinished = false;
                offsetX = evt.getX();
                offsetY = evt.getY();
                playAnimationOnce(dragRightFrames, () -> {
                    dragAnimationFinished = true;
                    currentFrames = new ArrayList<>();
                    currentFrames.add(dragRightStopFrame);
                    currentFrame = 0;
                    dequeueAnimation();
                }); // Play right drag animation
            } else if (isInsideLeftUpper(evt.getX(), evt.getY())) {
                isDragging = true;
                draggingRight = false;
                draggingDown = false;
                dragAnimationFinished = false;
                offsetX = evt.getX();
                offsetY = evt.getY();
                playAnimationOnce(dragLeftFrames, () -> {
                    dragAnimationFinished = true;
                    currentFrames = new ArrayList<>();
                    currentFrames.add(dragLeftStopFrame);
                    currentFrame = 0;
                    dequeueAnimation();
                }); // Play left drag animation
            } else if (isInsideLeftLower(evt.getX(), evt.getY())) {
                isDragging = true;
                draggingRight = false;
                draggingDown = true;
                dragAnimationFinished = false;
                offsetX = evt.getX();
                offsetY = evt.getY();
                playAnimationOnce(lowerLeftFrames, () -> {
                    dragAnimationFinished = true;
                    currentFrames = new ArrayList<>();
                    currentFrames.add(lowerLeftStopFrame);
                    currentFrame = 0;
                    dequeueAnimation();
                }); // Play lower left drag animation
            } else if (isInsideRightLower(evt.getX(), evt.getY())) {
                isDragging = true;
                draggingRight = true;
                draggingDown = true;
                dragAnimationFinished = false;
                offsetX = evt.getX();
                offsetY = evt.getY();
                playAnimationOnce(lowerRightFrames, () -> {
                    dragAnimationFinished = true;
                    currentFrames = new ArrayList<>();
                    currentFrames.add(lowerRightStopFrame);
                    currentFrame = 0;
                    dequeueAnimation();
                }); // Play lower right drag animation
            }
        });
    }

    // Stop dragging and play the corresponding release animation
    private void stopDrag(MouseEvent evt) {
        enqueueAnimation(() -> {
            isDragging = false;
            if (draggingRight && !draggingDown) {
                playAnimationOnce(downRightFrames, () -> {
                    playAnimation(idleFrames); // Play right release animation, then idle
                    dequeueAnimation();
                });
            } else if (draggingDown && !draggingRight) {
                playAnimationOnce(lowerLeftDropFrames, () -> {
                    playAnimation(idleFrames); // Play lower left release animation, then idle
                    dequeueAnimation();
                });
            } else if (draggingDown && draggingRight) {
                playAnimationOnce(lowerRightDropFrames, () -> {
                    playAnimation(idleFrames); // Play lower right release animation, then idle
                    dequeueAnimation();
                });
            } else {
                playAnimationOnce(downLeftFrames, () -> {
                    playAnimation(idleFrames); // Play left release animation, then idle
                    dequeueAnimation();
                });
            }
        });
    }

    // Handle click events and play corresponding animations
    private void handleClick(MouseEvent evt) {
        if (!dragAnimationFinished) return; // Prevent click animations from interrupting drag animations

        enqueueAnimation(() -> {
            if (isInsideLeftUpper(evt.getX(), evt.getY())) {
                playAnimationOnce(clickLeftUpperFrames, () -> {
                    playAnimation(idleFrames); // Play click animation, then idle
                    dequeueAnimation();
                });
            } else if (isInsideRightUpper(evt.getX(), evt.getY())) {
                playAnimationOnce(clickRightUpperFrames, () -> {
                    playAnimation(idleFrames); // Play click animation, then idle
                    dequeueAnimation();
                });
            } else if (isInsideLeftLower(evt.getX(), evt.getY())) {
                playAnimationOnce(clickLeftLowerFrames, () -> {
                    playAnimation(idleFrames); // Play click animation, then idle
                    dequeueAnimation();
                });
            } else if (isInsideRightLower(evt.getX(), evt.getY())) {
                playAnimationOnce(clickRightLowerFrames, () -> {
                    playAnimation(idleFrames); // Play click animation, then idle
                    dequeueAnimation();
                });
            }
        });
    }

    // Read frames from a GIF file
    private List<BufferedImage> readGif(File file) throws IOException {
        List<BufferedImage> images = new ArrayList<>();
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        try (ImageInputStream stream = ImageIO.createImageInputStream(file)) {
            reader.setInput(stream);
            int count = reader.getNumImages(true);
            for (int index = 0; index < count; index++) {
                BufferedImage frame = reader.read(index);
                BufferedImage imageWithAlpha = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = imageWithAlpha.createGraphics();
                g2d.drawImage(frame, 0, 0, null);
                g2d.dispose();
                images.add(imageWithAlpha);
            }
        }
        return images;
    }

    // Mirror frames for left and right animations
    private List<BufferedImage> mirrorFrames(List<BufferedImage> frames) {
        List<BufferedImage> mirroredFrames = new ArrayList<>();
        for (BufferedImage frame : frames) {
            mirroredFrames.add(mirrorImage(frame));
        }
        return mirroredFrames;
    }

    // Create a mirrored image
    private BufferedImage mirrorImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage mirroredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mirroredImage.createGraphics();
        g2d.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
        g2d.dispose();
        return mirroredImage;
    }

    // Check if the mouse is inside the right upper region
    private boolean isInsideRightUpper(int mouseX, int mouseY) {
        return mouseX > frameWidth / 2 && mouseY < frameHeight / 2;
    }

    // Check if the mouse is inside the left upper region
    private boolean isInsideLeftUpper(int mouseX, int mouseY) {
        return mouseX < frameWidth / 2 && mouseY < frameHeight / 2;
    }

    // Check if the mouse is inside the left lower region
    private boolean isInsideLeftLower(int mouseX, int mouseY) {
        return mouseX < frameWidth / 2 && mouseY > frameHeight / 2;
    }

    // Check if the mouse is inside the right lower region
    private boolean isInsideRightLower(int mouseX, int mouseY) {
        return mouseX > frameWidth / 2 && mouseY > frameHeight / 2;
    }

    // Play a looping animation
    private void playAnimation(List<BufferedImage> frames) {
        currentFrames = frames;
        currentFrame = 0;
    }

    // Play an animation once and run a callback when finished
    private void playAnimationOnce(List<BufferedImage> frames, Runnable onAnimationEnd) {
        currentFrames = frames;
        currentFrame = 0;
        Timer tempTimer = new Timer(100, e -> {
            if (currentFrame < frames.size() - 1) {
                currentFrame++;
            } else {
                ((Timer) e.getSource()).stop();
                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
            }
            repaint();
        });
        tempTimer.start();
    }

    // Enqueue an animation to be played
    private void enqueueAnimation(Runnable animation) {
        animationQueue.add(animation);
        if (animationQueue.size() == 1) {
            animation.run();
        }
    }

    // Dequeue and play the next animation in the queue
    private void dequeueAnimation() {
        animationQueue.poll();
        if (!animationQueue.isEmpty()) {
            animationQueue.peek().run();
        }
    }

    // Paint the current frame of the animation
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!currentFrames.isEmpty() && currentFrame < currentFrames.size()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.Src);
            g2d.drawImage(currentFrames.get(currentFrame), 0, 0, this);
            g2d.dispose();
        }
    }

    // Return the preferred size of the component
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(frameWidth, frameHeight);
    }

    // Main method to run the animation
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pet Animation");
        Pet pet = new Pet();
        frame.add(pet);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}