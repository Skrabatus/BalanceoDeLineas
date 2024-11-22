import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EstacionesView {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private double tiempoCiclo;
    private ArrayList<Tarea> tareas;

    public EstacionesView(double tiempoCiclo, ArrayList<Tarea> tareas) {
        this.tiempoCiclo = tiempoCiclo;
        this.tareas = tareas;

        // Crear la ventana
        frame = new JFrame("Asignación de Estaciones");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Crear el modelo de la tabla
        String[] columnNames = {
            "Estación", "Tarea", "Tiempo Tarea (seg)", 
            "Tiempo Restante No Asignado", "Tareas Factibles", 
            "Tarea con Tiempo Mayor", "Selección"
        };
        tableModel = new DefaultTableModel(columnNames, 0);

        // Crear la tabla
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Llenar la tabla según reglas
        llenarTabla();

        // Agregar los componentes a la ventana
        frame.add(scrollPane, BorderLayout.CENTER);
    }

    public void mostrar() {
        frame.setLocationRelativeTo(null); // Centrar la ventana
        frame.setVisible(true);
    }

    private void llenarTabla() {
        List<Tarea> tareasAsignadas = new ArrayList<>();
        int estacion = 1;
        double tiempoRestante = tiempoCiclo;

        while (tareasAsignadas.size() < tareas.size()) {
            // Obtener las tareas factibles (cumplen precedencia y no están asignadas)
            List<Tarea> tareasFactibles = obtenerTareasFactibles(tareasAsignadas);

            // Seleccionar la tarea con el mayor tiempo que cabe en el tiempo restante
            Tarea seleccionada = null;
            for (Tarea tarea : tareasFactibles) {
                if (tarea.tiempo <= tiempoRestante &&
                        (seleccionada == null || tarea.tiempo > seleccionada.tiempo)) {
                    seleccionada = tarea;
                }
            }

            if (seleccionada != null) {
                // Agregar fila a la tabla
                tableModel.addRow(new Object[]{
                    estacion,
                    seleccionada.nombre,
                    seleccionada.tiempo,
                    tiempoRestante - seleccionada.tiempo,
                    listaNombres(tareasFactibles),
                    seleccionada.nombre,
                    "Seleccionada"
                });

                // Actualizar tiempo restante y lista de tareas asignadas
                tiempoRestante -= seleccionada.tiempo;
                tareasAsignadas.add(seleccionada);
            } else {
                // Si no se puede asignar ninguna tarea, iniciar una nueva estación
                estacion++;
                tiempoRestante = tiempoCiclo;
            }
        }
    }

    private List<Tarea> obtenerTareasFactibles(List<Tarea> tareasAsignadas) {
        List<Tarea> factibles = new ArrayList<>();

        for (Tarea tarea : tareas) {
            if (!tareasAsignadas.contains(tarea)) {
                // Comprobar si todas las precedencias de esta tarea están asignadas
                boolean precedenciasCumplidas = true;

                if (tarea.precedencia != null && !tarea.precedencia.equals("-")) {
                    String[] precedencias = tarea.precedencia.split(",");
                    for (String precedencia : precedencias) {
                        precedencia = precedencia.trim();
                        boolean precedenciaAsignada = false;

                        for (Tarea tareaAsignada : tareasAsignadas) {
                            if (tareaAsignada.nombre.equals(precedencia)) {
                                precedenciaAsignada = true;
                                break;
                            }
                        }

                        if (!precedenciaAsignada) {
                            precedenciasCumplidas = false;
                            break;
                        }
                    }
                }

                if (precedenciasCumplidas) {
                    factibles.add(tarea);
                }
            }
        }

        return factibles;
    }

    private String listaNombres(List<Tarea> tareas) {
        StringBuilder nombres = new StringBuilder();
        for (Tarea tarea : tareas) {
            if (nombres.length() > 0) {
                nombres.append(", ");
            }
            nombres.append(tarea.nombre);
        }
        return nombres.toString();
    }
}
