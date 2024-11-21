import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TablaView {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;

    public TablaView() {
        // Crear la ventana
        frame = new JFrame("Tabla de Balanceo de Líneas");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Columnas de la tabla
        String[] columnNames = {"Tarea", "Tiempo tarea (seg)", "Precedencia"};

        // Modelo de la tabla
        tableModel = new DefaultTableModel(new Object[][]{}, columnNames);

        // Crear la tabla
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Panel inferior con botón para agregar datos
        JPanel panelInferior = new JPanel();
        JButton agregarButton = new JButton("Agregar Tarea");
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarFila();
            }
        });
        JButton tiempoCicloButton = new JButton("Determinar Tiempo Ciclo");
        tiempoCicloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tiempoTotal = 0;

                // Calcular el tiempo total del ciclo
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    int tiempo = (int) tableModel.getValueAt(i, 1);
                    tiempoTotal += tiempo;
                }

                // Mostrar el resultado
                JOptionPane.showMessageDialog(frame, "El tiempo total del ciclo es: " + tiempoTotal + " segundos",
                        "Tiempo del Ciclo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

// Agregar el botón al panel inferior
        panelInferior.add(tiempoCicloButton);

        JButton relacionesButton = new JButton("Mostrar Relaciones");
        relacionesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Convertir los datos de la tabla a una lista de Tareas
                ArrayList<Tarea> tareas = new ArrayList<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String nombre = (String) tableModel.getValueAt(i, 0);
                    int tiempo = (int) tableModel.getValueAt(i, 1);
                    String precedencia = (String) tableModel.getValueAt(i, 2);
                    tareas.add(new Tarea(nombre, tiempo, precedencia));
                }

                // Mostrar la ventana de relaciones
                RelacionesView relacionesView = new RelacionesView(tareas);
                relacionesView.mostrar();
            }
        });

        panelInferior.add(agregarButton);
        panelInferior.add(relacionesButton);

        // Agregar componentes a la ventana
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panelInferior, BorderLayout.SOUTH);
    }

    public void mostrar() {
        frame.setLocationRelativeTo(null); // Centrar la ventana
        frame.setVisible(true);
    }

    private void agregarFila() {
        // Mostrar un cuadro de diálogo para ingresar datos
        String tarea = JOptionPane.showInputDialog(frame, "Ingrese el nombre de la tarea:");

        // Verificar que la tarea no exista ya
        if (tarea != null && !tarea.isEmpty()) {
            boolean tareaExistente = false;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(tarea)) {
                    tareaExistente = true;
                    break;
                }
            }

            if (tareaExistente) {
                JOptionPane.showMessageDialog(frame, "El nombre de la tarea ya existe. Intente con otro nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tiempoStr = JOptionPane.showInputDialog(frame, "Ingrese el tiempo de la tarea (segundos):");
            String precedencia = JOptionPane.showInputDialog(frame, "Ingrese la precedencia (nombre de tarea o '-'):");

            // Validar la precedencia
            boolean precedenciaValida = precedencia.equals("-");

            // Verificar si la precedencia existe en las tareas ya ingresadas
            if (!precedencia.equals("-")) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String nombreTarea = (String) tableModel.getValueAt(i, 0);
                    if (nombreTarea.equals(precedencia)) {
                        precedenciaValida = true;
                        break;
                    }
                }
            }

            if (!precedenciaValida) {
                JOptionPane.showMessageDialog(frame, "La precedencia ingresada no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar que el tiempo sea un número entero
            try {
                int tiempo = Integer.parseInt(tiempoStr); // Validar que sea un número
                tableModel.addRow(new Object[]{tarea, tiempo, precedencia});
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "El tiempo debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "El nombre de la tarea no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
