import javax.swing.*;
import java.awt.*;

public class PetFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private Pet pet;

    public PetFrame() {
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparent background

        pet = new Pet();
        setLayout(null); // Allow movements
        add(pet);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        pet.setBounds(screenSize.width / 2, screenSize.height / 2, pet.getPreferredSize().width, pet.getPreferredSize().height); // Starting size and place


        getContentPane().setBackground(new Color(0, 0, 0, 0));
        setVisible(true);
    }

    public Pet getPet() {
        return pet;
    }
}
