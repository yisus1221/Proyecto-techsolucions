package com.techsolutions.utils;

import com.techsolutions.services.MongoDBService;
import com.techsolutions.model.Tarea;
import com.techsolutions.model.Empleado;
import org.bson.Document;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Clase para probar y verificar la conexión con MongoDB
 */
public class PruebaConexionMongoDB extends JFrame {
    private MongoDBService mongoDBService;
    private JTextArea areaResultados;
    private JButton btnProbarConexion;
    private JButton btnVerTareas;
    private JButton btnVerEmpleados;
    private JButton btnEstadisticas;
    private JButton btnInsertarDatosPrueba;

    public PruebaConexionMongoDB() {
        super("Prueba de Conexión MongoDB - TechSolutions");
        mongoDBService = new MongoDBService();
        inicializarUI();
    }

    private void inicializarUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Área de texto para mostrar resultados
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaResultados);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnProbarConexion = new JButton("Probar Conexión");
        btnVerTareas = new JButton("Ver Tareas");
        btnVerEmpleados = new JButton("Ver Empleados");
        btnEstadisticas = new JButton("Ver Estadísticas");
        btnInsertarDatosPrueba = new JButton("Insertar Datos de Prueba");

        // Listeners para los botones
        btnProbarConexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                probarConexion();
            }
        });

        btnVerTareas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verTareas();
            }
        });

        btnVerEmpleados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verEmpleados();
            }
        });

        btnEstadisticas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verEstadisticas();
            }
        });

        btnInsertarDatosPrueba.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertarDatosPrueba();
            }
        });

        panelBotones.add(btnProbarConexion);
        panelBotones.add(btnVerTareas);
        panelBotones.add(btnVerEmpleados);
        panelBotones.add(btnEstadisticas);
        panelBotones.add(btnInsertarDatosPrueba);

        // Layout principal
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // Realizar prueba inicial
        probarConexion();
    }

    private void probarConexion() {
        areaResultados.append("=== PRUEBA DE CONEXIÓN ===\n");
        
        boolean conexionOK = mongoDBService.probarConexion();
        
        if (conexionOK) {
            areaResultados.append("✓ Conexión con MongoDB establecida correctamente\n");
            areaResultados.append("✓ Base de datos: techsolutions\n");
            areaResultados.append("✓ Estado de conexión: " + mongoDBService.isConexionEstablecida() + "\n\n");
            
            // Habilitar otros botones
            btnVerTareas.setEnabled(true);
            btnVerEmpleados.setEnabled(true);
            btnEstadisticas.setEnabled(true);
            btnInsertarDatosPrueba.setEnabled(true);
        } else {
            areaResultados.append("✗ Error al conectar con MongoDB\n");
            areaResultados.append("✗ Verifica que MongoDB esté ejecutándose en localhost:27017\n");
            areaResultados.append("✗ Estado de conexión: " + mongoDBService.isConexionEstablecida() + "\n\n");
            
            // Deshabilitar otros botones
            btnVerTareas.setEnabled(false);
            btnVerEmpleados.setEnabled(false);
            btnEstadisticas.setEnabled(false);
            btnInsertarDatosPrueba.setEnabled(false);
        }
    }

    private void verTareas() {
        areaResultados.append("=== TAREAS EN BASE DE DATOS ===\n");
        
        List<Tarea> tareas = mongoDBService.cargarTodasLasTareas();
        
        if (tareas.isEmpty()) {
            areaResultados.append("No se encontraron tareas en la base de datos\n\n");
        } else {
            areaResultados.append("Total de tareas encontradas: " + tareas.size() + "\n\n");
            
            for (Tarea tarea : tareas) {
                areaResultados.append("ID: " + tarea.getId() + "\n");
                areaResultados.append("Descripción: " + tarea.getDescripcion() + "\n");
                areaResultados.append("Departamento: " + tarea.getDepartamento() + "\n");
                areaResultados.append("Urgencia: " + tarea.getUrgencia() + "\n");
                areaResultados.append("Horas estimadas: " + tarea.getHorasEstimadas() + "\n");
                areaResultados.append("---\n");
            }
            areaResultados.append("\n");
        }
    }

    private void verEmpleados() {
        areaResultados.append("=== EMPLEADOS EN BASE DE DATOS ===\n");
        
        List<Empleado> empleados = mongoDBService.cargarTodosLosEmpleados();
        
        if (empleados.isEmpty()) {
            areaResultados.append("No se encontraron empleados en la base de datos\n\n");
        } else {
            areaResultados.append("Total de empleados encontrados: " + empleados.size() + "\n\n");
            
            for (Empleado empleado : empleados) {
                areaResultados.append("ID: " + empleado.getId() + "\n");
                areaResultados.append("Nombre: " + empleado.getNombre() + "\n");
                areaResultados.append("Departamento: " + empleado.getDepartamento() + "\n");
                areaResultados.append("---\n");
            }
            areaResultados.append("\n");
        }
    }

    private void verEstadisticas() {
        areaResultados.append("=== ESTADÍSTICAS DE BASE DE DATOS ===\n");
        
        Document stats = mongoDBService.obtenerEstadisticasTareas();
        
        if (stats.isEmpty()) {
            areaResultados.append("No se pudieron obtener estadísticas\n\n");
        } else {
            areaResultados.append("Total de tareas: " + stats.getLong("total") + "\n");
            areaResultados.append("Tareas urgentes: " + stats.getLong("urgentes") + "\n");
            areaResultados.append("Tareas programadas: " + stats.getLong("programadas") + "\n");
            areaResultados.append("Tareas por departamento: " + stats.getLong("departamento") + "\n\n");
        }
    }

    private void insertarDatosPrueba() {
        areaResultados.append("=== INSERTANDO DATOS DE PRUEBA ===\n");
        
        try {
            // Insertar algunas tareas de prueba
            Tarea tarea1 = new Tarea("PRUEBA001", "Tarea de prueba 1", "Desarrollo", "Alta", 3);
            Tarea tarea2 = new Tarea("PRUEBA002", "Tarea de prueba 2", "Testing", "Media", 2);
            
            boolean resultado1 = mongoDBService.guardarTarea(tarea1, "urgente");
            boolean resultado2 = mongoDBService.guardarTarea(tarea2, "programada");
            
            // Insertar un empleado de prueba
            Empleado empleado1 = new Empleado("EMP001", "Juan Pérez", "Desarrollo");
            boolean resultado3 = mongoDBService.guardarEmpleado(empleado1);
            
            if (resultado1 && resultado2 && resultado3) {
                areaResultados.append("✓ Datos de prueba insertados correctamente\n");
                areaResultados.append("✓ Tareas insertadas: 2\n");
                areaResultados.append("✓ Empleados insertados: 1\n\n");
            } else {
                areaResultados.append("✗ Error al insertar algunos datos de prueba\n\n");
            }
            
        } catch (Exception e) {
            areaResultados.append("✗ Error al insertar datos de prueba: " + e.getMessage() + "\n\n");
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PruebaConexionMongoDB().setVisible(true);
            }
        });
    }

    @Override
    public void dispose() {
        // Cerrar la conexión al cerrar la ventana
        if (mongoDBService != null) {
            mongoDBService.cerrarConexion();
        }
        super.dispose();
    }
}