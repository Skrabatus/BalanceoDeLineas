import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RelacionesView {
    private JFrame frame;
    private RelacionesPanel relacionesPanel;
    private ArrayList<Tarea> tareas;

    public RelacionesView(ArrayList<Tarea> tareas) {
        this.tareas = tareas;

        // Crear la ventana
        frame = new JFrame("Relaciones Secuenciales");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Crear el panel de relaciones
        relacionesPanel = new RelacionesPanel(tareas);
        frame.add(relacionesPanel, BorderLayout.CENTER);
    }

    public void mostrar() {
        frame.setLocationRelativeTo(null); // Centrar la ventana
        frame.setVisible(true);
    }
}

// Clase personalizada para representar una tarea
class Tarea {
    String nombre;
    int tiempo;
    String precedencia;

    public Tarea(String nombre, int tiempo, String precedencia) {
        this.nombre = nombre;
        this.tiempo = tiempo;
        this.precedencia = precedencia;
    }
}

// Panel para dibujar las relaciones
class RelacionesPanel extends JPanel {
    private ArrayList<Tarea> tareas;
    private Map<String, Point> posiciones; // Mapa para guardar posiciones de las tareas

    public RelacionesPanel(ArrayList<Tarea> tareas) {
        this.tareas = tareas;
        this.posiciones = new HashMap<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Configurar estilo gráfico
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        int x = 50; // Posición inicial
        int y = 100;
        int espacioHorizontal = 150; // Espacio entre tareas
        int radio = 40; // Radio de los círculos

        // Dibujar las tareas
        for (Tarea tarea : tareas) {
            // Dibujar el círculo
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(x - radio / 2, y - radio / 2, radio, radio);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x - radio / 2, y - radio / 2, radio, radio);

            // Dibujar el texto de la tarea y el tiempo
            g2d.drawString(tarea.nombre, x - 10, y + 5);
            g2d.drawString(String.valueOf(tarea.tiempo) + " seg", x - 20, y - 30);

            // Guardar la posición para dibujar flechas después
            posiciones.put(tarea.nombre, new Point(x, y));

            // Avanzar al siguiente nodo horizontalmente
            x += espacioHorizontal;
        }

        // Dibujar las flechas según las precedencias
        for (Tarea tarea : tareas) {
            if (tarea.precedencia != null && posiciones.containsKey(tarea.precedencia)) {
                Point origen = posiciones.get(tarea.precedencia);
                Point destino = posiciones.get(tarea.nombre);

                // Dibujar línea entre los nodos
                g2d.setColor(Color.BLACK);
                g2d.drawLine(origen.x, origen.y, destino.x - radio / 2, destino.y);

                // Dibujar la flecha
                dibujarFlecha(g2d, origen.x, origen.y, destino.x - radio / 2, destino.y);
            }
        }
    }

    private void dibujarFlecha(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        int flechaLongitud = 10;
        int flechaAncho = 5;

        double angulo = Math.atan2(y2 - y1, x2 - x1);
        int xFlecha1 = (int) (x2 - flechaLongitud * Math.cos(angulo - Math.PI / 6));
        int yFlecha1 = (int) (y2 - flechaLongitud * Math.sin(angulo - Math.PI / 6));
        int xFlecha2 = (int) (x2 - flechaLongitud * Math.cos(angulo + Math.PI / 6));
        int yFlecha2 = (int) (y2 - flechaLongitud * Math.sin(angulo + Math.PI / 6));

        g2d.drawLine(x2, y2, xFlecha1, yFlecha1);
        g2d.drawLine(x2, y2, xFlecha2, yFlecha2);
    }
}
