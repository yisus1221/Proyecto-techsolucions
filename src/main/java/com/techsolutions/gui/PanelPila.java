package com.techsolutions.gui;

import com.techsolutions.model.Tarea;
import com.techsolutions.db.MongoDBManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

/**
 * Panel para gestionar tareas urgentes usando una estructura de datos tipo pila (LIFO)
 */
public class PanelPila extends JPanel {
    private Stack<Tarea> pilaTareasUrgentes;
    private JTable tablaPila;
    private DefaultTableModel modelPila;
    private JButton btnPush, btnPop, btnPeek;
    private MongoDBManager dbManager;

    /**
     * Constructor del panel de pila
     */
    public PanelPila() {
        this.pilaTareasUrgentes = new Stack<>();
        this.dbManager = MongoDBManager.getInstance();
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
    }

    /**
     * Inicializa los componentes del panel
     */
    private void inicializarComponentes() {
        // Configurar tabla
        String[] columnas = {"ID", "Descripción", "Departamento", "Urgencia", "Horas Est."};
        modelPila = new DefaultTableModel(columnas, 0);
        tablaPila = new JTable(modelPila);
        tablaPila.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configurar botones
        btnPush = new JButton("Agregar Tarea (Push)");
        btnPop = new JButton("Eliminar Tarea (Pop)");
        btnPeek = new JButton("Ver Última Tarea (Peek)");

        // Configurar tooltips
        btnPush.setToolTipText("Agrega una nueva tarea urgente al tope de la pila");
        btnPop.setToolTipText("Elimina la última tarea agregada (LIFO - Last In, First Out)");
        btnPeek.setToolTipText("Muestra la información de la última tarea sin eliminarla");
    }

    /**
     * Configura el layout del panel
     */
    private void configurarLayout() {
        setLayout(new BorderLayout());

        // Panel superior con información
        JPanel panelInfo = new JPanel();
        panelInfo.setBorder(BorderFactory.createTitledBorder("Tareas Urgentes - Estructura: Pila (LIFO)"));
        panelInfo.add(new JLabel("Las tareas se procesan en orden inverso al de llegada"));
        
        // Panel central con tabla
        JScrollPane scrollPane = new JScrollPane(tablaPila);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tareas en la Pila"));

        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnPush);
        panelBotones.add(btnPop);
        panelBotones.add(btnPeek);

        // Agregar componentes al panel principal
        add(panelInfo, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Configura los eventos de los botones
     */
    private void configurarEventos() {
        btnPush.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarTarea();
            }
        });

        btnPop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarTarea();
            }
        });

        btnPeek.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verUltimaTarea();
            }
        });
    }

    /**
     * Agrega una nueva tarea a la pila
     */
    private void agregarTarea() {
        try {
            Tarea nuevaTarea = mostrarDialogoNuevaTarea();
            if (nuevaTarea != null) {
                // Validar ID único en la pila
                if (existeIdEnPila(nuevaTarea.getId())) {
                    JOptionPane.showMessageDialog(this, 
                        "Ya existe una tarea con ese ID en la pila.",
                        "ID Duplicado", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Agregar a la pila
                pilaTareasUrgentes.push(nuevaTarea);

                // Guardar en base de datos
                boolean guardado = dbManager.guardarTarea(nuevaTarea, "urgente");
                if (!guardado) {
                    pilaTareasUrgentes.pop(); // Revertir si falla el guardado
                    JOptionPane.showMessageDialog(this, 
                        "Error al guardar la tarea en la base de datos",
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                actualizarTabla();
                mostrarMensajeExito("Tarea agregada correctamente al tope de la pila");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al agregar tarea: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina la última tarea agregada (tope de la pila)
     */
    private void eliminarTarea() {
        try {
            if (pilaTareasUrgentes.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay tareas urgentes para eliminar",
                    "Pila Vacía", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Tarea tareaEliminada = pilaTareasUrgentes.pop();

            // Eliminar de base de datos
            boolean eliminado = dbManager.eliminarTarea(tareaEliminada.getId());
            if (!eliminado) {
                pilaTareasUrgentes.push(tareaEliminada); // Revertir si falla
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar la tarea de la base de datos",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            actualizarTabla();
            mostrarMensajeExito("Tarea eliminada: " + tareaEliminada.getDescripcion());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al eliminar tarea: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra información de la última tarea sin eliminarla
     */
    private void verUltimaTarea() {
        try {
            if (pilaTareasUrgentes.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay tareas urgentes en la pila",
                    "Pila Vacía", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Tarea ultimaTarea = pilaTareasUrgentes.peek();
            String mensaje = String.format(
                "Última tarea urgente (tope de la pila):\n\n" +
                "ID: %s\n" +
                "Descripción: %s\n" +
                "Departamento: %s\n" +
                "Urgencia: %s\n" +
                "Horas estimadas: %d\n\n" +
                "Posición en la pila: %d de %d",
                ultimaTarea.getId(),
                ultimaTarea.getDescripcion(),
                ultimaTarea.getDepartamento(),
                ultimaTarea.getUrgencia(),
                ultimaTarea.getHorasEstimadas(),
                pilaTareasUrgentes.size(),
                pilaTareasUrgentes.size()
            );

            JOptionPane.showMessageDialog(this, mensaje,
                "Información de la Última Tarea", 
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al ver tarea: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actualiza la tabla con el contenido actual de la pila
     */
    public void actualizarTabla() {
        modelPila.setRowCount(0);
        
        // Mostrar las tareas desde el tope hacia abajo
        Stack<Tarea> temp = new Stack<>();
        temp.addAll(pilaTareasUrgentes);
        
        while (!temp.isEmpty()) {
            Tarea tarea = temp.pop();
            modelPila.addRow(new Object[]{
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getDepartamento(),
                tarea.getUrgencia(),
                tarea.getHorasEstimadas()
            });
        }
    }

    /**
     * Carga las tareas urgentes desde la base de datos
     */
    public void cargarTareasDesdeDB() {
        try {
            pilaTareasUrgentes.clear();
            var tareas = dbManager.cargarTareasPorTipo("urgente");
            
            for (Tarea tarea : tareas) {
                pilaTareasUrgentes.push(tarea);
            }
            
            actualizarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar tareas desde la base de datos: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Obtiene la pila de tareas urgentes
     */
    public Stack<Tarea> getPilaTareasUrgentes() {
        return pilaTareasUrgentes;
    }

    /**
     * Establece la pila de tareas urgentes
     */
    public void setPilaTareasUrgentes(Stack<Tarea> pilaTareasUrgentes) {
        this.pilaTareasUrgentes = pilaTareasUrgentes;
        actualizarTabla();
    }

    /**
     * Verifica si existe un ID en la pila
     */
    private boolean existeIdEnPila(String id) {
        return pilaTareasUrgentes.stream().anyMatch(t -> t.getId().equals(id));
    }

    /**
     * Muestra un diálogo para crear una nueva tarea
     */
    private Tarea mostrarDialogoNuevaTarea() {
        JTextField txtId = new JTextField(dbManager.generarIdTarea());
        txtId.setEditable(false);
        JTextField txtDescripcion = new JTextField();
        JComboBox<String> cmbDepartamento = new JComboBox<>(new String[]{
            "Desarrollo", "Soporte Técnico", "Recursos Humanos", "Administración", "Ventas"
        });
        JComboBox<String> cmbUrgencia = new JComboBox<>(new String[]{
            "Alta", "Media", "Baja"
        });
        JTextField txtHoras = new JTextField("1");

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Nueva Tarea Urgente"));
        panel.add(new JLabel("ID:"));
        panel.add(txtId);
        panel.add(new JLabel("Descripción:"));
        panel.add(txtDescripcion);
        panel.add(new JLabel("Departamento:"));
        panel.add(cmbDepartamento);
        panel.add(new JLabel("Nivel de Urgencia:"));
        panel.add(cmbUrgencia);
        panel.add(new JLabel("Horas estimadas:"));
        panel.add(txtHoras);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Nueva Tarea Urgente", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String id = txtId.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "La descripción es obligatoria",
                    "Datos Incompletos", 
                    JOptionPane.WARNING_MESSAGE);
                return null;
            }

            String departamento = (String) cmbDepartamento.getSelectedItem();
            String urgencia = (String) cmbUrgencia.getSelectedItem();
            int horasEstimadas = 1;
            
            try {
                horasEstimadas = Integer.parseInt(txtHoras.getText().trim());
                if (horasEstimadas <= 0) horasEstimadas = 1;
            } catch (NumberFormatException ex) {
                horasEstimadas = 1;
            }

            return new Tarea(id, descripcion, departamento, urgencia, horasEstimadas);
        }
        
        return null;
    }

    /**
     * Muestra un mensaje de éxito
     */
    private void mostrarMensajeExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Obtiene información estadística de la pila
     */
    public String obtenerEstadisticas() {
        if (pilaTareasUrgentes.isEmpty()) {
            return "La pila de tareas urgentes está vacía";
        }

        int totalHoras = pilaTareasUrgentes.stream().mapToInt(Tarea::getHorasEstimadas).sum();
        
        return String.format(
            "Estadísticas de Tareas Urgentes:\n" +
            "• Total de tareas: %d\n" +
            "• Horas estimadas totales: %d\n" +
            "• Promedio de horas por tarea: %.1f\n" +
            "• Estructura: Pila (LIFO - Last In, First Out)",
            pilaTareasUrgentes.size(),
            totalHoras,
            (double) totalHoras / pilaTareasUrgentes.size()
        );
    }
}