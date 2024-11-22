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
    private JTextField tiempoProduccionField;
    private JTextField produccionRequeridaField;
    private double tiempoCiclo = 0; // Variable para almacenar el tiempo de ciclo calculado

    public TablaView() {
        // Crear la ventana
        frame = new JFrame("Tabla de Balanceo de Líneas");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Crear el panel superior para los inputs
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel tiempoProduccionLabel = new JLabel("Tiempo de producción por día (segs):");
        tiempoProduccionField = new JTextField(10); // Campo para tiempo de producción
        JLabel produccionRequeridaLabel = new JLabel("Producción requerida por día (uds):");
        produccionRequeridaField = new JTextField(10); // Campo para producción requerida

        // Añadir componentes al panel superior
        panelSuperior.add(tiempoProduccionLabel);
        panelSuperior.add(tiempoProduccionField);
        panelSuperior.add(produccionRequeridaLabel);
        panelSuperior.add(produccionRequeridaField);

        // Columnas de la tabla
        String[] columnNames = {"Tarea", "Tiempo tarea (seg)", "Precedencia"};

        // Modelo de la tabla
        tableModel = new DefaultTableModel(new Object[][]{}, columnNames);

        // Crear la tabla
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Panel inferior con botones
        JPanel panelInferior = new JPanel();
        JButton agregarButton = new JButton("Agregar Tarea");
        agregarButton.addActionListener(e -> agregarFila());

        JButton relacionesButton = new JButton("Mostrar Relaciones");
        relacionesButton.addActionListener(e -> mostrarRelaciones());

        panelInferior.add(agregarButton);
        panelInferior.add(relacionesButton);
        
        JButton calcularCicloButton = new JButton("Calcular Ciclo de Estación");
        calcularCicloButton.addActionListener(e -> {
            try {
                String tiempoProduccionStr = tiempoProduccionField.getText();
                String produccionRequeridaStr = produccionRequeridaField.getText();

                if (tiempoProduccionStr.isEmpty() || produccionRequeridaStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Por favor, complete ambos campos: tiempo de producción y producción requerida.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double tiempoProduccionMinutos = Double.parseDouble(tiempoProduccionStr);
                int produccionRequerida = Integer.parseInt(produccionRequeridaStr);

                if (tiempoProduccionMinutos <= 0 || produccionRequerida <= 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Ambos valores deben ser mayores a cero.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Calcular el ciclo de la estación
                tiempoCiclo = (tiempoProduccionMinutos * 60) / produccionRequerida;

                // Mostrar el resultado
                JOptionPane.showMessageDialog(frame,
                        "El ciclo de la estación de trabajo es: " + String.format("%.2f", tiempoCiclo) + " segundos.",
                        "Resultado",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Asegúrese de ingresar valores numéricos válidos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


// Agregar el botón al panel inferior
        panelInferior.add(calcularCicloButton);
        JButton calcularEstacionesButton = new JButton("Calcular Número de Estaciones");
        calcularEstacionesButton.addActionListener(e -> {
            if (tiempoCiclo <= 0) {
                JOptionPane.showMessageDialog(frame,
                        "Primero debe calcular el ciclo de la estación.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Sumar los tiempos de las tareas
            double sumaTiemposTareas = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                sumaTiemposTareas += (int) tableModel.getValueAt(i, 1);
            }

            // Calcular el número mínimo teórico de estaciones
            double numEstaciones = sumaTiemposTareas / tiempoCiclo;

            // Mostrar el resultado
            JOptionPane.showMessageDialog(frame,
                    "El número mínimo teórico de estaciones es: " + String.format("%.2f", numEstaciones),
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
        });

// Agregar el botón al panel inferior
        panelInferior.add(calcularEstacionesButton);

        // Agregar los componentes a la ventana
        frame.add(panelSuperior, BorderLayout.NORTH); // Inputs en la parte superior
        frame.add(scrollPane, BorderLayout.CENTER); // Tabla en el centro
        frame.add(panelInferior, BorderLayout.SOUTH); // Botones en la parte inferior
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

    private void mostrarRelaciones() {
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
}

