import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.border.*;
import java.util.concurrent.*;

public class BatteryConservationController extends JFrame {
    private static final String CONSERVATION_PATH = "/sys/bus/platform/drivers/ideapad_acpi/VPC2004:00/conservation_mode";
    private JLabel statusLabel;
    private Timer notificationTimer;
    private JWindow notificationWindow;

    public BatteryConservationController() {
        setupWindow();
        createGUI();
        checkRoot();
        checkPath();
        updateStatus();
    }

    private void setupWindow() {
        setTitle("Battery Conservation Mode");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Battery Conservation Mode");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel statusTextLabel = new JLabel("Current Status: ");
        statusTextLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel = new JLabel("Unknown");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusPanel.add(statusTextLabel);
        statusPanel.add(statusLabel);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton enableButton = createStyledButton("Enable", new Color(46, 204, 113));
        JButton disableButton = createStyledButton("Disable", new Color(231, 76, 60));

        enableButton.addActionListener(e -> setConservationMode(true));
        disableButton.addActionListener(e -> setConservationMode(false));

        buttonPanel.add(enableButton);
        buttonPanel.add(disableButton);

        // Main Panel Components ^^
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(statusPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private void checkRoot() {
        if (!isRoot()) {
            JOptionPane.showMessageDialog(this,
                "This application needs root privileges.\nPlease run with sudo.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void checkPath() {
        File path = new File(CONSERVATION_PATH);
        if (!path.exists()) {
            JOptionPane.showMessageDialog(this,
                "Conservation mode path not found.\nIs this a Lenovo IdeaPad laptop?",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private boolean isRoot() {
        try {
            Process process = Runtime.getRuntime().exec("id -u");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine();
            return result != null && result.equals("0");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateStatus() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CONSERVATION_PATH));
            String status = reader.readLine();
            reader.close();

            boolean isEnabled = "1".equals(status);
            statusLabel.setText(isEnabled ? "Enabled" : "Disabled");
            statusLabel.setForeground(isEnabled ? new Color(46, 204, 113) : new Color(231, 76, 60));
        } catch (IOException e) {
            statusLabel.setText("Error");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void setConservationMode(boolean enable) {
        try {
            ProcessBuilder pb = new ProcessBuilder("sudo", "tee", CONSERVATION_PATH);
            pb.redirectInput(ProcessBuilder.Redirect.PIPE);
            Process process = pb.start();
            
            try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
                writer.println(enable ? "1" : "0");
            }

            int result = process.waitFor();
            if (result == 0) {
                updateStatus();
                showNotification(enable ? "Conservation mode enabled" : "Conservation mode disabled");
            } else {
                throw new IOException("Failed to set conservation mode");
            }
        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(this,
                "Error setting conservation mode: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showNotification(String message) {
        if (notificationWindow != null) {
            notificationWindow.dispose();
        }
        if (notificationTimer != null) {
            notificationTimer.stop();
        }

        notificationWindow = new JWindow();
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        panel.setBackground(new Color(0, 0, 0, 200));
        
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label);
        
        notificationWindow.add(panel);
        notificationWindow.pack();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        notificationWindow.setLocation(
            screenSize.width - notificationWindow.getWidth() - 20,
            screenSize.height - notificationWindow.getHeight() - 40
        );
        
        notificationWindow.setVisible(true);
        
        notificationTimer = new Timer(2000, e -> {
            notificationWindow.dispose();
            ((Timer)e.getSource()).stop();
        });
        notificationTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BatteryConservationController().setVisible(true);
        });
    }
}
