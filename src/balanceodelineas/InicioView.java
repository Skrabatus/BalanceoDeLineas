import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class InicioView {
    private JFrame frame;

    public InicioView() {
        // Crear la ventana principal
        frame = new JFrame("Balanceo de Líneas - Inicio");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(20, 20));

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240)); // Color de fondo suave

        // Título
        JLabel titleLabel = new JLabel("Balanceo de Líneas de Producción", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204)); // Color azul para el título

        // Descripción de la aplicación
        JTextArea descriptionArea = new JTextArea("Este sistema permite realizar el balanceo de líneas de producción. "
                + "Se encarga de calcular el ciclo de trabajo, asignar tareas a las estaciones y optimizar "
                + "el flujo de trabajo de acuerdo a las necesidades de producción.");
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(mainPanel.getBackground());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setForeground(Color.BLACK);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear JLabel para la imagen
        JLabel imageLabel = new JLabel();
        try {
            // URL de la imagen
            URL imageUrl = new URL("https://th.bing.com/th/id/OIP.B_AeVczmyf5l5zCGMQmsTgHaEu?rs=1&pid=ImgDetMain");
            ImageIcon imageIcon = new ImageIcon(imageUrl);
            Image image = imageIcon.getImage(); // Obtiene la imagen
            Image scaledImage = image.getScaledInstance(400, 200, Image.SCALE_SMOOTH); // Escala la imagen
            imageLabel.setIcon(new ImageIcon(scaledImage)); // Asigna la imagen escalada al JLabel
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Botón de inicio
        JButton startButton = new JButton("Iniciar");
        startButton.setPreferredSize(new Dimension(120, 40));
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(new Color(0, 102, 204)); // Azul para el botón
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false); // Evita que se resalte al hacer clic

        // Acción al hacer clic en el botón "Iniciar"
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mostrar la ventana con la tabla
                TablaView tablaView = new TablaView();
                tablaView.mostrar();
                frame.dispose(); // Cerrar la ventana de inicio
            }
        });

        // Crear panel inferior para el botón
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(mainPanel.getBackground());
        buttonPanel.add(startButton);

        // Agregar componentes al panel principal
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(descriptionArea, BorderLayout.CENTER);
        mainPanel.add(imageLabel, BorderLayout.AFTER_LINE_ENDS); // Agregar la imagen entre la descripción y el botón
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Agregar panel principal a la ventana
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    public void mostrar() {
        frame.setLocationRelativeTo(null); // Centrar la ventana
        frame.setVisible(true);
    }
}
