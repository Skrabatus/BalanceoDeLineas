import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InicioView {
    private JFrame frame;

    public InicioView() {
        // Crear la ventana principal
        frame = new JFrame("Balanceo de Líneas - Inicio");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Crear el botón de inicio
        JButton startButton = new JButton("Iniciar");
        startButton.setPreferredSize(new Dimension(100, 40));

        // Agregar acción al botón
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mostrar la ventana con la tabla
                TablaView tablaView = new TablaView();
                tablaView.mostrar();
            }
        });

        // Agregar el botón al centro de la ventana
        frame.add(startButton, BorderLayout.CENTER);
    }

    public void mostrar() {
        frame.setLocationRelativeTo(null); // Centrar la ventana
        frame.setVisible(true);
    }
}
