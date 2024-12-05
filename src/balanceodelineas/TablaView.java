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
    private ArrayList<Tarea> tareas; // Lista para almacenar las tareas
    private double tiempoCicloCalculado = 0; // Ciclo de estación
    private int numeroEstacionesCalculado = 0; // Número de estaciones

    public TablaView() {
        // Crear la ventana
        frame = new JFrame("Tabla de Balanceo de Líneas");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        tareas = new ArrayList<>(); // Inicializar la lista de tareas
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
                tiempoCicloCalculado = tiempoCiclo; // Almacenar el valor

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

        JButton estacionesButton = new JButton("Calcular Estaciones");
        estacionesButton.addActionListener(e -> {
            if (tiempoCiclo <= 0) {
                JOptionPane.showMessageDialog(frame,
                        "Debe calcular el ciclo de la estación antes de asignar tareas.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            EstacionesView estacionesView = new EstacionesView(tiempoCiclo, tareas);
            estacionesView.mostrar();
        });
        
        panelInferior.add(estacionesButton);
        JButton eliminarButton = new JButton("Eliminar Tarea");
        eliminarButton.addActionListener(e -> eliminarTarea());
        panelInferior.add(eliminarButton);

        JButton actualizarButton = new JButton("Actualizar Tarea");
        actualizarButton.addActionListener(e -> actualizarTarea());
        panelInferior.add(actualizarButton);
        JButton calcularEficienciaButton = new JButton("Calcular Eficiencia");
        calcularEficienciaButton.addActionListener(e -> calcularEficiencia());
        panelInferior.add(calcularEficienciaButton);



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
            numeroEstacionesCalculado = (int) Math.ceil(numEstaciones); // Redondear hacia arriba y almacenar como entero

            // Mostrar el resultado
            JOptionPane.showMessageDialog(frame,
                    "El número mínimo teórico de estaciones es: " + numeroEstacionesCalculado,
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
        String precedencia = JOptionPane.showInputDialog(frame, "Ingrese la precedencia (nombres de tareas separadas por comas o '-'):");

        if (precedencia == null || precedencia.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Debe ingresar un valor para la precedencia.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que la precedencia sea válida (puede ser una lista separada por comas)
        boolean precedenciaValida = precedencia.equals("-");

        if (!precedencia.equals("-")) {
    // Separar las precedencias por comas
    String[] precedencias = precedencia.split(",");

    // Validar que cada tarea en la lista de precedencias exista
    for (String tareaPrecedente : precedencias) {
        tareaPrecedente = tareaPrecedente.trim();  // Eliminar espacios extra

        boolean tareaEncontrada = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nombreTarea = (String) tableModel.getValueAt(i, 0);
            if (nombreTarea.equalsIgnoreCase(tareaPrecedente)) { // Comparación sin distinción entre mayúsculas/minúsculas
                tareaEncontrada = true;
                break;
            }
        }

        if (!tareaEncontrada) {
            JOptionPane.showMessageDialog(frame, 
                "La tarea '" + tareaPrecedente + "' no existe o no se ha ingresado antes.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}


        // Validar que el tiempo sea un número entero
        try {
            int tiempo = Integer.parseInt(tiempoStr); // Validar que sea un número

            // Crear y agregar la nueva tarea
            Tarea nuevaTarea = new Tarea(tarea, tiempo, precedencia);
            tareas.add(nuevaTarea);

            // Agregar a la tabla
            tableModel.addRow(new Object[]{tarea, tiempo, precedencia});
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "El tiempo debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(frame, "El nombre de la tarea no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    private void eliminarTarea() {
        int selectedRow = table.getSelectedRow(); // Obtener la fila seleccionada

        if (selectedRow != -1) {
            // Confirmación del usuario
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "¿Está seguro de que desea eliminar esta tarea?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String nombreTarea = (String) tableModel.getValueAt(selectedRow, 0);

                // Eliminar de la lista de tareas
                tareas.removeIf(tarea -> tarea.nombre.equals(nombreTarea));

                // Eliminar de la tabla
                tableModel.removeRow(selectedRow);

                JOptionPane.showMessageDialog(frame,
                        "La tarea ha sido eliminada exitosamente.",
                        "Eliminación exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Por favor seleccione una tarea para eliminar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTarea() {
        int selectedRow = table.getSelectedRow(); // Obtener la fila seleccionada

        if (selectedRow != -1) {
            String nombreActual = (String) tableModel.getValueAt(selectedRow, 0);

            // Pedir nuevos valores al usuario
            String nuevoNombre = JOptionPane.showInputDialog(frame,
                    "Ingrese el nuevo nombre de la tarea:", nombreActual);

            if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tiempoStr = JOptionPane.showInputDialog(frame,
                    "Ingrese el nuevo tiempo de la tarea (segundos):",
                    tableModel.getValueAt(selectedRow, 1));
            try {
                int nuevoTiempo = Integer.parseInt(tiempoStr);

                String nuevaPrecedencia = JOptionPane.showInputDialog(frame,
                        "Ingrese la nueva precedencia (nombres separados por comas o '-'):",
                        tableModel.getValueAt(selectedRow, 2));

                if (nuevaPrecedencia == null || nuevaPrecedencia.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "La precedencia no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Actualizar en la lista de tareas
                for (Tarea tarea : tareas) {
                    if (tarea.nombre.equals(nombreActual)) {
                        tarea.nombre = nuevoNombre;
                        tarea.tiempo = nuevoTiempo;
                        tarea.precedencia = nuevaPrecedencia;
                        break;
                    }
                }

                // Actualizar en la tabla
                tableModel.setValueAt(nuevoNombre, selectedRow, 0);
                tableModel.setValueAt(nuevoTiempo, selectedRow, 1);
                tableModel.setValueAt(nuevaPrecedencia, selectedRow, 2);

                JOptionPane.showMessageDialog(frame,
                        "La tarea ha sido actualizada exitosamente.",
                        "Actualización exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "El tiempo debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Por favor seleccione una tarea para actualizar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void calcularEficiencia() {
        if (tiempoCicloCalculado <= 0 || numeroEstacionesCalculado <= 0) {
            JOptionPane.showMessageDialog(frame,
                    "Debe calcular el ciclo de la estación y el número de estaciones antes de calcular la eficiencia.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Sumar los tiempos de las tareas
        double sumaTiemposTareas = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            sumaTiemposTareas += (int) tableModel.getValueAt(i, 1);
        }

        // Calcular la eficiencia
        double eficiencia = (sumaTiemposTareas / (numeroEstacionesCalculado * tiempoCicloCalculado)) * 100;

        // Determinar el mensaje basado en la eficiencia
        String mensaje;
        if (eficiencia < 60) {
            mensaje = "De acuerdo a las reglas de balanceo del proyecto, el resultado de eficiencia es insatisfactorio.";
        } else if (eficiencia >= 60 && eficiencia <= 90) {
            mensaje = "De acuerdo a las reglas de balanceo del proyecto, el resultado de eficiencia es satisfactorio.";
        } else {
            mensaje = "De acuerdo a las reglas de balanceo del proyecto, el resultado de eficiencia es sobresaliente.";
        }

        // Mostrar el resultado con el mensaje adicional
        JOptionPane.showMessageDialog(frame,
                String.format("La eficiencia del balanceo es: %.2f %%.\n%s", eficiencia, mensaje),
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
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

