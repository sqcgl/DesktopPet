public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PetFrame frame = new PetFrame();
                frame.setVisible(true);
            }
        });
    }
}