package com.techsolutions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// Importaciones de los modelos
import com.techsolutions.model.Tarea;
import com.techsolutions.model.TareaPrioridad;
import com.techsolutions.model.Empleado;
import com.techsolutions.model.ArbolEmpleados;

/**
 * SISTEMA DE GESTIÓN DE TAREAS - TechSolutions S.A. de C.V.
 * 
 * Aplicación principal que implementa un sistema completo de gestión de tareas
 * utilizando múltiples estructuras de datos y una interfaz gráfica moderna.
 * 
 * ESTRUCTURAS DE DATOS IMPLEMENTADAS:
 * =====================================
 * 
 * 1. PILA (Stack) - Tareas Urgentes:
 *    - Implementa LIFO (Last In, First Out)
 *    - Para tareas que requieren atención inmediata
 *    - Operaciones: Push, Pop, Peek
 * 
 * 2. COLA (Queue) - Tareas Programadas:
 *    - Implementa FIFO (First In, First Out)
 *    - Para tareas con secuencia específica
 *    - Operaciones: Enqueue, Dequeue, Front
 * 
 * 3. LISTA (List) - Tareas Departamentales:
 *    - Acceso indexado y búsquedas específicas
 *    - Para tareas organizadas por departamento
 *    - Operaciones: Insertar, Eliminar, Buscar por índice
 * 
 * 4. COLA DE PRIORIDAD (PriorityQueue) - Tareas Priorizadas:
 *    - Ordenamiento automático por prioridad numérica
 *    - Para tareas con niveles de importancia
 *    - Operaciones: Insertar con prioridad, Extraer más prioritaria
 * 
 * 5. ÁRBOL BINARIO DE BÚSQUEDA (BST) - Empleados:
 *    - Búsqueda eficiente O(log n)
 *    - Empleados ordenados por ID
 *    - Operaciones: Insertar, Buscar, Recorridos
 * 
 * CARACTERÍSTICAS TÉCNICAS:
 * =========================
 * - Interfaz gráfica moderna con Swing
 * - Integración con MongoDB para persistencia
 * - Algoritmos de búsqueda y ordenamiento
 * - Recursividad para cálculos complejos
 * - Divide y vencerás para distribución de tareas
 * - Grafos para dependencias entre tareas
 * 
 * @author TechSolutions Development Team
 * @version 3.0.0
 * @since 2025-09-21
 */
public class SistemaGestionTareas extends JFrame {
    
    // ===============================================
    // ESTRUCTURAS DE DATOS PRINCIPALES
    // ===============================================
    
    /** PILA - Para tareas urgentes (LIFO) */
    private final Stack<Tarea> pilaTareasUrgentes;
    
    /** COLA - Para tareas programadas (FIFO) */
    private final LinkedList<Tarea> colaTareasProgramadas;
    
    /** LISTA - Para tareas departamentales (acceso indexado) */
    private final List<Tarea> listaTareasDepartamento;
    
    // ===============================================
    // CONEXIÓN A BASE DE DATOS MONGODB
    // ===============================================
    
    /** Cliente de conexión a MongoDB */
    private MongoClient mongoClient;
    
    /** Base de datos MongoDB */
    private MongoDatabase database;
    
    /** Colección de tareas en MongoDB */
    private MongoCollection<Document> collection;
    
    /** Colección de empleados en MongoDB */
    private MongoCollection<Document> empleadosCollection;
    
    // ===============================================
    // COMPONENTES DE INTERFAZ GRÁFICA
    // ===============================================
    
    /** Panel con pestañas principales */
    private JTabbedPane tabbedPane;
    
    /** Paneles para cada estructura de datos */
    private JPanel panelPila, panelCola, panelLista, panelGeneral;
    
    // Panel Pila
    private JTable tablaPila;
    private DefaultTableModel modelPila;
    private JButton btnPush, btnPop, btnPeek;
    
    // Panel Cola
    private JTable tablaCola;
    private DefaultTableModel modelCola;
    private JButton btnEnqueue, btnDequeue, btnFront;
    
    // Panel Lista
    private JTable tablaLista;
    private DefaultTableModel modelLista;
    private JButton btnInsertar, btnEliminar, btnBuscar;
    private JTextField txtBusqueda;
    
    // Panel General
    private JTable tablaGeneral;
    private DefaultTableModel modelGeneral;
    private JButton btnActualizarGeneral;
    
    // Panel Cola de Prioridades
    private JPanel panelPrioridad;
    private JTable tablaPrioridad;
    private DefaultTableModel modelPrioridad;
    private JButton btnVerPrioridad, btnEliminarPrioridad; // Agrega esta línea junto con los otros botones
    
    // Panel Empleados
    private JPanel panelEmpleados;
    private JTable tablaEmpleados;
    private DefaultTableModel modelEmpleados;
    private JTextField txtBusquedaEmpleado;
    private JButton btnBuscarEmpleado, btnMostrarArbol, btnInsertarEmpleado;

    // Las clases de modelo ahora están en paquetes separados para mejor organización



    // Recursividad para sumar tiempo estimado
    private int calcularTiempoTotalRecursivo(List<Tarea> tareas, int idx) {
        if (idx >= tareas.size()) return 0;
        int tiempo = tareas.get(idx).getHorasEstimadas(); // Usa el campo real
        return tiempo + calcularTiempoTotalRecursivo(tareas, idx + 1);
    }

    // Divide y vencerás para distribuir tareas (puedes mostrar la asignación en la interfaz)
    private void distribuirTareasDivideVenceras(List<Tarea> tareas, int inicio, int fin) {
        if (inicio >= fin) return;
        int medio = (inicio + fin) / 2;
        // Aquí podrías asignar tareas.get(medio) a un empleado/proyecto
        distribuirTareasDivideVenceras(tareas, inicio, medio);
        distribuirTareasDivideVenceras(tareas, medio + 1, fin);
    }

    // HashMap para tareas y empleados
    private final Map<String, Tarea> hashTareas = new HashMap<>();

    // Métodos de ordenamiento y búsqueda
    private void ordenarTareasPorPrioridadFecha(List<TareaPrioridad> lista) {
        lista.sort(Comparator.naturalOrder());
    }

    // Ejemplo de búsqueda eficiente
    private Tarea buscarTareaPorId(String id) {
        return hashTareas.get(id);
    }

    // Cola de prioridades global
    private final PriorityQueue<TareaPrioridad> colaPrioridad = new PriorityQueue<>();

    // Árbol binario de empleados global
    private final ArbolEmpleados arbolEmpleados = new ArbolEmpleados();

    // --- Dependencias entre tareas (grafo simple) ---
    private final Map<String, List<String>> dependenciasTareas = new HashMap<>();
    private JButton btnAgregarDependencia, btnVerDependencias;

    public SistemaGestionTareas() {
        super("Sistema de Gestión de Tareas - TechSolutions S.A. de C.V.");
        
        // Configurar look and feel moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Personalizar colores y fuentes
            UIManager.put("TabbedPane.selected", new Color(70, 130, 180));
            UIManager.put("TabbedPane.background", new Color(240, 248, 255));
            UIManager.put("Button.background", new Color(128, 128, 128));
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
            UIManager.put("Table.gridColor", new Color(200, 200, 200));
            UIManager.put("TableHeader.background", new Color(60, 120, 170));
            UIManager.put("TableHeader.foreground", Color.BLACK);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
        } catch (Exception e) {
            System.err.println("No se pudo establecer el look and feel: " + e.getMessage());
        }
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());
        
        // Configurar el panel principal con gradiente
        setContentPane(createMainPanel());

        // Inicializar estructuras de datos
        pilaTareasUrgentes = new Stack<>();
        colaTareasProgramadas = new LinkedList<>();
        listaTareasDepartamento = new ArrayList<>();

        // Ya no declares colaPrioridad ni arbolEmpleados aquí, ya son atributos globales

        // HashMap de tareas
        hashTareas.put("T1", new Tarea("T1", "Revisar código", "Desarrollo", "Alta"));

        // Conectar a MongoDB
        conectarMongoDB();

        // PRIMERO: Configurar interfaz (esto inicializa los modelos de tabla)
        configurarInterfaz();

        // SEGUNDO: Cargar datos desde MongoDB (después de inicializar los modelos)
        cargarDatosDesdeMongoDB();
        
        // Cargar empleados en el árbol
        cargarEmpleadosDesdeMongoDB();

        setVisible(true);

        // Ejemplo de ordenamiento
        java.util.List<TareaPrioridad> listaPrioridad = new ArrayList<>();
        listaPrioridad.add(new TareaPrioridad("T2", "Documentar", "Soporte Técnico", "Media", 2, "2025-09-20"));
        listaPrioridad.add(new TareaPrioridad("T3", "Testear", "Desarrollo", "Alta", 1, "2025-09-18"));
        ordenarTareasPorPrioridadFecha(listaPrioridad);
    }
    
    // Método para crear icono de la aplicación
    private Image createAppIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(70, 130, 180));
        g2d.fillRoundRect(4, 4, 24, 24, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("T", 12, 22);
        g2d.dispose();
        return icon;
    }
    
    // Método para crear el panel principal con gradiente
    private JPanel createMainPanel() {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(), new Color(225, 245, 254));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }
    
    // Conectar a MongoDB también para empleados
    private void conectarMongoDB() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("techsolutions");
            collection = database.getCollection("tareas");
            empleadosCollection = database.getCollection("empleados");
            System.out.println("Conexión a MongoDB establecida correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con MongoDB: " + e.getMessage(), 
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Cargar empleados en el árbol desde MongoDB
    private ArbolEmpleados cargarEmpleadosDesdeMongoDB() {
        ArbolEmpleados arbol = new ArbolEmpleados();
        for (Document doc : empleadosCollection.find()) {
            arbol.insertar(new Empleado(
                doc.getString("id"),
                doc.getString("nombre"),
                doc.getString("departamento")
            ));
        }
        return arbol;
    }
    
    private void cargarDatosDesdeMongoDB() {
        try {
            pilaTareasUrgentes.clear();
            colaTareasProgramadas.clear();
            listaTareasDepartamento.clear();
            colaPrioridad.clear();
            hashTareas.clear();
            // No podemos reasignar arbolEmpleados porque es final, pero podemos limpiarlo de otra manera
            dependenciasTareas.clear(); // Limpia el grafo

            for (Document doc : collection.find()) {
                Tarea tarea = documentToTarea(doc);
                hashTareas.put(tarea.getId(), tarea);

                String tipo = doc.getString("tipo");
                int prioridad = doc.getInteger("prioridad", 2); // Default Media
                String fechaEntrega = doc.getString("fechaEntrega");

                // Si tiene prioridad y fecha, úsalo como TareaPrioridad
                if (prioridad > 0 && fechaEntrega != null) {
                    TareaPrioridad tp = new TareaPrioridad(
                        tarea.getId(), tarea.getDescripcion(), tarea.getDepartamento(),
                        tarea.getUrgencia(), prioridad, fechaEntrega
                    );
                    colaPrioridad.add(tp);
                }

                // Lógica original
                if ("urgente".equals(tipo)) pilaTareasUrgentes.push(tarea);
                else if ("programada".equals(tipo)) colaTareasProgramadas.add(tarea);
                else if ("departamento".equals(tipo)) listaTareasDepartamento.add(tarea);

                // Cargar dependencias si existen
                Object depsObj = doc.get("dependencias");
                if (depsObj instanceof List<?>) {
                    List<?> depsList = (List<?>) depsObj;
                    List<String> deps = new ArrayList<>();
                    for (Object o : depsList) {
                        if (o != null) deps.add(o.toString());
                    }
                    if (!deps.isEmpty()) {
                        dependenciasTareas.put(tarea.getId(), deps);
                    }
                }
            }

            // Cargar empleados en el árbol desde MongoDB
            for (Document doc : empleadosCollection.find()) {
                arbolEmpleados.insertar(new Empleado(
                    doc.getString("id"),
                    doc.getString("nombre"),
                    doc.getString("departamento")
                ));
            }

            actualizarTablas();
            mostrarTodosEmpleados(); // <-- Agrega esta línea
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos desde MongoDB: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarTareaEnMongoDB(Tarea tarea, String tipo) {
        try {
            int prioridad = 2;
            String fechaEntrega = null;
            if (tipo.equals("urgente") || tipo.equals("programada")) {
                String[] opciones = {"Alta", "Media", "Baja"};
                prioridad = JOptionPane.showOptionDialog(this, "Prioridad de la tarea:", "Prioridad",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[1]) + 1;
                fechaEntrega = JOptionPane.showInputDialog(this, "Fecha de entrega (yyyy-MM-dd):");
            }
            Document doc = new Document("id", tarea.getId())
                    .append("descripcion", tarea.getDescripcion())
                    .append("departamento", tarea.getDepartamento())
                    .append("urgencia", tarea.getUrgencia())
                    .append("horasEstimadas", tarea.getHorasEstimadas()) // NUEVO
                    .append("tipo", tipo)
                    .append("prioridad", prioridad)
                    .append("fechaEntrega", fechaEntrega);
            collection.insertOne(doc);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar en MongoDB: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Tarea documentToTarea(Document doc) {
        String id = doc.getString("id");
        String descripcion = doc.getString("descripcion");
        String departamento = doc.getString("departamento");
        String urgencia = doc.getString("urgencia");
        int horasEstimadas = doc.getInteger("horasEstimadas", 1); // NUEVO
        return new Tarea(id, descripcion, departamento, urgencia, horasEstimadas);
    }
    
    private void configurarInterfaz() {
        // Crear el tabbedPane con estilo moderno
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(240, 248, 255));
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Configurar paneles con diseño mejorado
        panelPila = crearPanelPila();
        panelCola = crearPanelCola();
        panelLista = crearPanelLista();
        panelGeneral = crearPanelGeneral();
        panelPrioridad = crearPanelPrioridad();
        panelEmpleados = crearPanelEmpleados();

        // Agregar tabs con iconos
        tabbedPane.addTab("📚 Tareas Urgentes", panelPila);
        tabbedPane.addTab("⏰ Tareas Programadas", panelCola);
        tabbedPane.addTab("🏢 Por Departamento", panelLista);
        tabbedPane.addTab("📋 Todas las Tareas", panelGeneral);
        tabbedPane.addTab("⭐ Prioridades", panelPrioridad);
        tabbedPane.addTab("👥 Empleados", panelEmpleados);

        // Agregar el tabbedPane al panel principal
        JPanel mainPanel = (JPanel) getContentPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelPila() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "📚 Gestión de Tareas Urgentes (Pila LIFO)", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
        
        // Modelo de tabla mejorado
        String[] columnas = {"🆔 ID", "📝 Descripción", "🏢 Departamento", "⚠️ Urgencia"};
        modelPila = new DefaultTableModel(columnas, 0);
        tablaPila = new JTable(modelPila);
        
        // Personalizar la tabla
        personalizarTabla(tablaPila);
        
        // Panel de botones mejorado
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(245, 250, 255));
        panelBotones.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        btnPush = crearBotonModerno("➕ Agregar (Push)", new Color(105, 105, 105));
        btnPop = crearBotonModerno("➖ Eliminar (Pop)", new Color(169, 169, 169));
        btnPeek = crearBotonModerno("👁️ Ver Último (Peek)", new Color(128, 128, 128));
        
        btnPush.addActionListener(e -> agregarTareaPila());
        btnPop.addActionListener(e -> eliminarTareaPila());
        btnPeek.addActionListener(e -> verTareaPila());
        
        panelBotones.add(btnPush);
        panelBotones.add(btnPop);
        panelBotones.add(btnPeek);
        
        // Scroll personalizado
        JScrollPane scrollPane = new JScrollPane(tablaPila);
        scrollPane.setBorder(new EmptyBorder(10, 10, 5, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Método para crear botones modernos
    private JButton crearBotonModerno(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setBackground(colorFondo);
        
        // Usar siempre texto negro para mejor legibilidad
        boton.setForeground(Color.BLACK);
        
        boton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        boton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(150, 35));
        
        // Efectos hover manteniendo texto negro
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color hoverColor = colorFondo.darker();
                boton.setBackground(hoverColor);
                // Mantener texto negro en hover
                boton.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
                boton.setForeground(Color.BLACK);
            }
        });
        
        return boton;
    }
    
    // Método auxiliar para determinar si un color es oscuro
    private boolean esFondoOscuro(Color color) {
        // Fórmula para calcular luminancia
        double luminancia = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminancia < 0.5;
    }
    
    // Método para personalizar tablas
    private void personalizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(184, 207, 229));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setShowGrid(true);
        tabla.setIntercellSpacing(new Dimension(1, 1));
        
        // Personalizar header
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 30));
        
        // Renderer alternado para las filas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 252, 255));
                }
                return c;
            }
        });
    }
    
    private JPanel crearPanelCola() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Modelo de tabla
        String[] columnas = {"ID", "Descripción", "Departamento", "Urgencia"};
        modelCola = new DefaultTableModel(columnas, 0);
        tablaCola = new JTable(modelCola);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnEnqueue = crearBotonModerno("Agregar Tarea (Enqueue)", new Color(105, 105, 105));
        btnDequeue = crearBotonModerno("Eliminar Tarea (Dequeue)", new Color(169, 169, 169));
        btnFront = crearBotonModerno("Ver Primera Tarea (Front)", new Color(128, 128, 128));
        
        btnEnqueue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarTareaCola();
            }
        });
        
        btnDequeue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarTareaCola();
            }
        });
        
        btnFront.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verTareaCola();
            }
        });
        
        panelBotones.add(btnEnqueue);
        panelBotones.add(btnDequeue);
        panelBotones.add(btnFront);
        
        panel.add(new JScrollPane(tablaCola), BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton btnVerPorIndice; // Agrega esto junto con los otros botones

    private JPanel crearPanelLista() {
        JPanel panel = new JPanel(new BorderLayout());

        // Modelo de tabla
        String[] columnas = {"ID", "Descripción", "Departamento", "Urgencia"};
        modelLista = new DefaultTableModel(columnas, 0);
        tablaLista = new JTable(modelLista);
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout());
        panelBusqueda.add(new JLabel("Buscar por departamento:"));
        txtBusqueda = new JTextField(15);
        panelBusqueda.add(txtBusqueda);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnInsertar = crearBotonModerno("Insertar Tarea", new Color(105, 105, 105));
        btnEliminar = crearBotonModerno("Eliminar Tarea", new Color(169, 169, 169));
        btnBuscar = crearBotonModerno("Buscar Tareas", new Color(128, 128, 128));
        btnVerPorIndice = crearBotonModerno("Ver por Índice", new Color(112, 112, 112)); // Nuevo botón

        btnInsertar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarTareaLista();
            }
        });
        
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarTareaLista();
            }
        });
        
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarTareasLista();
            }
        });
        
        btnVerPorIndice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verTareaPorIndice();
            }
        });

        panelBotones.add(btnInsertar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnVerPorIndice); // Agrega el botón al panel

        panel.add(new JScrollPane(tablaLista), BorderLayout.CENTER);
        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton btnCalcularTiempoTotal; // Nuevo botón
    private JButton btnDistribuirTareas;    // Nuevo botón
    private JButton btnBuscarPorId; // Nuevo botón
    private JTextField txtBuscarPorId; // Nuevo campo de texto
    private JButton btnOrdenarUrgenciaDepto; // Nuevo botón

    private JPanel crearPanelGeneral() {
        JPanel panel = new JPanel(new BorderLayout());

        // Modelo de tabla
        String[] columnas = {"ID", "Descripción", "Departamento", "Urgencia", "Tipo"};
        modelGeneral = new DefaultTableModel(columnas, 0);
        tablaGeneral = new JTable(modelGeneral);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnActualizarGeneral = crearBotonModerno("Actualizar Vista", new Color(105, 105, 105));
        btnCalcularTiempoTotal = crearBotonModerno("Calcular Tiempo Total (Recursivo)", new Color(169, 169, 169));
        btnDistribuirTareas = crearBotonModerno("Distribuir Tareas (Divide y Vencerás)", new Color(128, 128, 128));
        txtBuscarPorId = new JTextField(10);
        btnBuscarPorId = crearBotonModerno("Buscar por ID", new Color(112, 112, 112));
        btnOrdenarUrgenciaDepto = crearBotonModerno("Ordenar por Urgencia y Departamento", new Color(96, 96, 96)); // Nuevo
        btnAgregarDependencia = crearBotonModerno("Agregar Dependencia", new Color(144, 144, 144));
        btnVerDependencias = crearBotonModerno("Ver Dependencias", new Color(160, 160, 160));

        btnActualizarGeneral.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTablaGeneral();
            }
        });

        btnCalcularTiempoTotal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarTiempoTotalRecursivo();
            }
        });

        btnDistribuirTareas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDistribucionDivideVenceras();
            }
        });

        // Acción para buscar por ID
        btnBuscarPorId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarTareaPorIdDesdeInterfaz();
            }
        });

        btnOrdenarUrgenciaDepto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ordenarTablaGeneralPorUrgenciaYDepto();
            }
        });

        btnAgregarDependencia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarDependenciaTarea();
            }
        });

        btnVerDependencias.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verDependenciasTarea();
            }
        });

        panelBotones.add(btnActualizarGeneral);
        panelBotones.add(btnCalcularTiempoTotal);
        panelBotones.add(btnDistribuirTareas);
        panelBotones.add(new JLabel("ID:"));
        panelBotones.add(txtBuscarPorId);
        panelBotones.add(btnBuscarPorId);
        panelBotones.add(btnOrdenarUrgenciaDepto); // Agrega el botón
        panelBotones.add(btnAgregarDependencia);
        panelBotones.add(btnVerDependencias);

        panel.add(new JScrollPane(tablaGeneral), BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel crearPanelPrioridad() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnas = {"ID", "Descripción", "Departamento", "Urgencia", "Prioridad", "Fecha Entrega"};
        modelPrioridad = new DefaultTableModel(columnas, 0);
        tablaPrioridad = new JTable(modelPrioridad);

        btnVerPrioridad = crearBotonModerno("Ver Tarea con Mayor Prioridad", new Color(105, 105, 105));
        btnEliminarPrioridad = crearBotonModerno("Eliminar Tarea con Mayor Prioridad", new Color(169, 169, 169)); // Nuevo botón

        btnVerPrioridad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verTareaMayorPrioridad();
            }
        });

        btnEliminarPrioridad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarTareaMayorPrioridad();
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnVerPrioridad);
        panelBotones.add(btnEliminarPrioridad);

        panel.add(new JScrollPane(tablaPrioridad), BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    // Nueva lógica para la tabla de prioridades
    private void actualizarTablaPrioridad() {
        modelPrioridad.setRowCount(0);
        // Copia la cola para no modificar el orden real
        PriorityQueue<TareaPrioridad> copia = new PriorityQueue<>(colaPrioridad);
        while (!copia.isEmpty()) {
            TareaPrioridad tarea = copia.poll();
            String prioridadStr = tarea.getPrioridad() == 1 ? "Alta" : tarea.getPrioridad() == 2 ? "Media" : "Baja";
            modelPrioridad.addRow(new Object[]{
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getDepartamento(),
                tarea.getUrgencia(),
                prioridadStr,
                tarea.getFechaEntrega()
            });
        }
    }

    private void verTareaMayorPrioridad() {
        if (colaPrioridad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay tareas en la cola de prioridades",
                    "Cola Vacía", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        TareaPrioridad tarea = colaPrioridad.peek();
        String prioridadStr = tarea.getPrioridad() == 1 ? "Alta" : tarea.getPrioridad() == 2 ? "Media" : "Baja";
        JOptionPane.showMessageDialog(this,
                "Tarea con mayor prioridad:\n" +
                "ID: " + tarea.getId() + "\n" +
                "Descripción: " + tarea.getDescripcion() + "\n" +
                "Departamento: " + tarea.getDepartamento() + "\n" +
                "Urgencia: " + tarea.getUrgencia() + "\n" +
                "Prioridad: " + prioridadStr + "\n" +
                "Fecha de Entrega: " + tarea.getFechaEntrega(),
                "Mayor Prioridad", JOptionPane.INFORMATION_MESSAGE);
    }

    // Lógica para eliminar la tarea con mayor prioridad
    private void eliminarTareaMayorPrioridad() {
        if (colaPrioridad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay tareas en la cola de prioridades",
                    "Cola Vacía", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        TareaPrioridad tarea = colaPrioridad.poll();
        eliminarTareaDeMongoDB(tarea.getId());

        // Elimina de otras estructuras
        pilaTareasUrgentes.removeIf(t -> t.getId().equals(tarea.getId()));
        colaTareasProgramadas.removeIf(t -> t.getId().equals(tarea.getId()));
        listaTareasDepartamento.removeIf(t -> t.getId().equals(tarea.getId()));
        hashTareas.remove(tarea.getId());

        actualizarTablas();
        JOptionPane.showMessageDialog(this, "Tarea eliminada: " + tarea.getDescripcion(),
                "Tarea Eliminada", JOptionPane.INFORMATION_MESSAGE);
    }

    // Modifica actualizarTablas para actualizar también la tabla de prioridades
    // Método duplicado eliminado para evitar error de compilación
    
    private void agregarTareaPila() {
        try {
            Tarea tarea = mostrarDialogoNuevaTarea("urgente");
            if (tarea != null) {
                // Validación: evitar IDs duplicados en la pila
                for (Tarea t : pilaTareasUrgentes) {
                    if (t.getId().equalsIgnoreCase(tarea.getId())) {
                        JOptionPane.showMessageDialog(this, "Ya existe una tarea con ese ID en la pila.",
                                "ID duplicado", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                pilaTareasUrgentes.push(tarea);
                guardarTareaEnMongoDB(tarea, "urgente");
                actualizarTablaPila();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agregar tarea: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarTareaPila() {
        try {
            if (pilaTareasUrgentes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay tareas urgentes para eliminar", 
                        "Pila Vacía", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            Tarea tarea = pilaTareasUrgentes.pop();
            eliminarTareaDeMongoDB(tarea.getId());
            actualizarTablaPila();
            
            JOptionPane.showMessageDialog(this, "Tarea eliminada: " + tarea.getDescripcion(), 
                    "Tarea Eliminada", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar tarea: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verTareaPila() {
        try {
            if (pilaTareasUrgentes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay tareas urgentes", 
                        "Pila Vacía", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            Tarea tarea = pilaTareasUrgentes.peek();
            JOptionPane.showMessageDialog(this, 
                    "Última tarea urgente:\n" +
                    "ID: " + tarea.getId() + "\n" +
                    "Descripción: " + tarea.getDescripcion() + "\n" +
                    "Departamento: " + tarea.getDepartamento() + "\n" +
                    "Urgencia: " + tarea.getUrgencia(),
                    "Última Tarea Urgente", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al ver tarea: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarTareaCola() {
        try {
            Tarea tarea = mostrarDialogoNuevaTarea("programada");
            if (tarea != null) {
                // Validación: evitar IDs duplicados en la cola
                for (Tarea t : colaTareasProgramadas) {
                    if (t.getId().equalsIgnoreCase(tarea.getId())) {
                        JOptionPane.showMessageDialog(this, "Ya existe una tarea con ese ID en la cola.",
                                "ID duplicado", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                colaTareasProgramadas.add(tarea);
                guardarTareaEnMongoDB(tarea, "programada");
                actualizarTablaCola();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agregar tarea: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarTareaCola() {
        try {
            if (colaTareasProgramadas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay tareas programadas para eliminar", 
                        "Cola Vacía", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            Tarea tarea = colaTareasProgramadas.remove();
            eliminarTareaDeMongoDB(tarea.getId());
            actualizarTablaCola();
            
            JOptionPane.showMessageDialog(this, "Tarea eliminada: " + tarea.getDescripcion(), 
                    "Tarea Eliminada", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar tarea: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verTareaCola() {
        try {
            if (colaTareasProgramadas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay tareas programadas", 
                        "Cola Vacía", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            Tarea tarea = colaTareasProgramadas.peek();
            JOptionPane.showMessageDialog(this, 
                    "Primera tarea programada:\n" +
                    "ID: " + tarea.getId() + "\n" +
                    "Descripción: " + tarea.getDescripcion() + "\n" +
                    "Departamento: " + tarea.getDepartamento() + "\n" +
                    "Urgencia: " + tarea.getUrgencia(),
                    "Primera Tarea Programada", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al ver tarea: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarTareaLista() {
        try {
            Tarea tarea = mostrarDialogoNuevaTarea("departamento");
            if (tarea != null) {
                // Validación: evitar IDs duplicados en la lista
                for (Tarea t : listaTareasDepartamento) {
                    if (t.getId().equalsIgnoreCase(tarea.getId())) {
                        JOptionPane.showMessageDialog(this, "Ya existe una tarea con ese ID en la lista.",
                                "ID duplicado", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                listaTareasDepartamento.add(tarea);
                guardarTareaEnMongoDB(tarea, "departamento");
                actualizarTablaLista();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agregar tarea: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarTareaLista() {
        try {
            int selectedRow = tablaLista.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una tarea para eliminar", 
                        "Selección Requerida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String id = (String) modelLista.getValueAt(selectedRow, 0);
            Tarea tareaEliminar = null;
            
            for (Tarea tarea : listaTareasDepartamento) {
                if (tarea.getId().equals(id)) {
                    tareaEliminar = tarea;
                    break;
                }
            }
            
            if (tareaEliminar != null) {
                listaTareasDepartamento.remove(tareaEliminar);
                eliminarTareaDeMongoDB(tareaEliminar.getId());
                actualizarTablaLista();
                
                JOptionPane.showMessageDialog(this, "Tarea eliminada: " + tareaEliminar.getDescripcion(), 
                        "Tarea Eliminada", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar tarea: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarTareasLista() {
        try {
            String departamento = txtBusqueda.getText().trim();
            if (departamento.isEmpty()) {
                actualizarTablaLista();
                return;
            }
            
            modelLista.setRowCount(0);
            for (Tarea tarea : listaTareasDepartamento) {
                if (tarea.getDepartamento().equalsIgnoreCase(departamento)) {
                    modelLista.addRow(new Object[]{
                        tarea.getId(),
                        tarea.getDescripcion(),
                        tarea.getDepartamento(),
                        tarea.getUrgencia()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al buscar tareas: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generarIdTarea() {
    int max = 0;
    for (Document doc : collection.find()) {
        String id = doc.getString("id");
        if (id != null && id.matches("T\\d+")) {
            int num = Integer.parseInt(id.substring(1));
            if (num > max) max = num;
        }
    }
    return "T" + (max + 1);
}

    private Tarea mostrarDialogoNuevaTarea(String tipo) {
        JTextField txtId = new JTextField(generarIdTarea());
        txtId.setEditable(false); // ID automático, no editable
        JTextField txtDescripcion = new JTextField();
        JComboBox<String> cmbDepartamento = new JComboBox<>(new String[]{
            "Desarrollo", "Soporte Técnico", "Recursos Humanos", "Administración", "Ventas"
        });
        JComboBox<String> cmbUrgencia = new JComboBox<>(new String[]{
            "Alta", "Media", "Baja"
        });
        JTextField txtHoras = new JTextField("1");

        JPanel panel = new JPanel(new GridLayout(0, 2));
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
                "Nueva Tarea " + (tipo.equals("urgente") ? "Urgente" : 
                                 tipo.equals("programada") ? "Programada" : "de Departamento"), 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String id = txtId.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La descripción es obligatoria", 
                        "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            String departamento = (String) cmbDepartamento.getSelectedItem();
            String urgencia = (String) cmbUrgencia.getSelectedItem();
            int horas = 1;
            try {
                horas = Integer.parseInt(txtHoras.getText().trim());
                if (horas <= 0) horas = 1;
            } catch (Exception ex) {
                horas = 1;
            }

            return new Tarea(id, descripcion, departamento, urgencia, horas);
        }
        return null;
    }
    
    private void actualizarTablas() {
        actualizarTablaPila();
        actualizarTablaCola();
        actualizarTablaLista();
        actualizarTablaGeneral();
        actualizarTablaPrioridad(); // NUEVO
    }
    
    private void actualizarTablaPila() {
        modelPila.setRowCount(0);
        for (Tarea tarea : pilaTareasUrgentes) {
            modelPila.addRow(new Object[]{
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getDepartamento(),
                tarea.getUrgencia()
            });
        }
    }
    
    private void actualizarTablaCola() {
        modelCola.setRowCount(0);
        for (Tarea tarea : colaTareasProgramadas) {
            modelCola.addRow(new Object[]{
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getDepartamento(),
                tarea.getUrgencia()
            });
        }
    }
    
    private void actualizarTablaLista() {
        modelLista.setRowCount(0);
        for (Tarea tarea : listaTareasDepartamento) {
            modelLista.addRow(new Object[]{
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getDepartamento(),
                tarea.getUrgencia()
            });
        }
    }
    
    private void actualizarTablaGeneral() {
        modelGeneral.setRowCount(0);
        
        // Agregar tareas urgentes
        for (Tarea tarea : pilaTareasUrgentes) {
            modelGeneral.addRow(new Object[]{
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getDepartamento(),
                tarea.getUrgencia(),
                "Urgente"
            });
        }
        
        // Agregar tareas programadas
        for (Tarea tarea : colaTareasProgramadas) {
            modelGeneral.addRow(new Object[]{
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getDepartamento(),
                tarea.getUrgencia(),
                "Programada"
            });
        }
        
        // Agregar tareas de departamento
        for (Tarea tarea : listaTareasDepartamento) {
            modelGeneral.addRow(new Object[]{
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getDepartamento(),
                tarea.getUrgencia(),
                "Departamento"
            });
        }
    }
    

    
    private void verTareaPorIndice() {
        try {
            String input = JOptionPane.showInputDialog(this, "Introduce el índice de la tarea (0 a " + (listaTareasDepartamento.size() - 1) + "):");
            if (input == null) return;
            int idx = Integer.parseInt(input.trim());
            if (idx < 0 || idx >= listaTareasDepartamento.size()) {
                JOptionPane.showMessageDialog(this, "Índice fuera de rango.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Tarea tarea = listaTareasDepartamento.get(idx);
            JOptionPane.showMessageDialog(this,
                    "Tarea en índice " + idx + ":\n" +
                    "ID: " + tarea.getId() + "\n" +
                    "Descripción: " + tarea.getDescripcion() + "\n" +
                    "Departamento: " + tarea.getDepartamento() + "\n" +
                    "Urgencia: " + tarea.getUrgencia(),
                    "Tarea por Índice", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al acceder por índice: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Agrega este método para crear el panel de empleados
    private JPanel crearPanelEmpleados() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabla de empleados
        String[] columnas = {"ID", "Nombre", "Departamento"};
        modelEmpleados = new DefaultTableModel(columnas, 0);
        tablaEmpleados = new JTable(modelEmpleados);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout());
        panelBusqueda.add(new JLabel("Buscar por departamento:"));
        txtBusquedaEmpleado = new JTextField(15);
        panelBusqueda.add(txtBusquedaEmpleado);

        btnBuscarEmpleado = crearBotonModerno("Buscar", new Color(105, 105, 105));
        btnMostrarArbol = crearBotonModerno("Mostrar estructura del árbol", new Color(169, 169, 169));
        btnInsertarEmpleado = crearBotonModerno("Insertar Empleado (Solo Jefe)", new Color(128, 128, 128));

        btnBuscarEmpleado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarEmpleadosPorDepartamento();
            }
        });

        btnMostrarArbol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarEstructuraArbol();
            }
        });

        btnInsertarEmpleado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertarEmpleadoSoloJefe();
            }
        });

        panelBusqueda.add(btnBuscarEmpleado);
        panelBusqueda.add(btnMostrarArbol);
        panelBusqueda.add(btnInsertarEmpleado); // Agrega el botón al panel de búsqueda

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaEmpleados), BorderLayout.CENTER);

        return panel;
    }

    // Agrega este método para buscar empleados por departamento
    private void buscarEmpleadosPorDepartamento() {
        String departamento = txtBusquedaEmpleado.getText().trim();
        modelEmpleados.setRowCount(0);
        if (departamento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un departamento para buscar.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Empleado> resultado = new ArrayList<>();
        arbolEmpleados.buscarPorDepartamento(departamento, resultado);
        if (resultado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron empleados en ese departamento.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
        for (Empleado emp : resultado) {
            modelEmpleados.addRow(new Object[]{emp.getId(), emp.getNombre(), emp.getDepartamento()});
        }
    }

    // Agrega este método para mostrar la estructura del árbol (inorden)
    private void mostrarEstructuraArbol() {
        StringBuilder sb = new StringBuilder();
        sb.append("Estructura del árbol (inorden):\n");
        recorrerInorden(arbolEmpleados.getRaiz(), sb);
        JOptionPane.showMessageDialog(this, sb.toString(), "Árbol de empleados (inorden)", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método recursivo para recorrer el árbol inorden
    private void recorrerInorden(Empleado actual, StringBuilder sb) {
        if (actual == null) return;
        recorrerInorden(actual.getIzquierda(), sb);
        sb.append("[").append(actual.getId()).append("] ").append(actual.getNombre()).append(" - ").append(actual.getDepartamento()).append("\n");
        recorrerInorden(actual.getDerecha(), sb);
    }

    // Método para insertar empleado solo si la contraseña es correcta
    private void insertarEmpleadoSoloJefe() {
        String password = JOptionPane.showInputDialog(this, "Ingrese la contraseña de jefe:");
        if (password == null) return;
        if (!password.equals("jefe123")) {
            JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField txtId = new JTextField(generarIdEmpleado());
        txtId.setEditable(false); // ID automático, no editable
        JTextField txtNombre = new JTextField();
        JComboBox<String> cmbDepartamento = new JComboBox<>(new String[]{
            "Desarrollo", "Soporte Técnico", "Recursos Humanos", "Administración", "Ventas"
        });

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("ID:"));
        panel.add(txtId);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Departamento:"));
        panel.add(cmbDepartamento);

        int result = JOptionPane.showConfirmDialog(this, panel, "Nuevo Empleado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String departamento = (String) cmbDepartamento.getSelectedItem();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar que no exista el ID (opcional, pero no debería ocurrir)
            List<Empleado> resultado = new ArrayList<>();
            arbolEmpleados.buscarPorDepartamento(departamento, resultado);
            for (Empleado emp : resultado) {
                if (emp.getId().equalsIgnoreCase(id)) {
                    JOptionPane.showMessageDialog(this, "Ya existe un empleado con ese ID en el departamento.", "ID duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Insertar en MongoDB
            try {
                Document doc = new Document("id", id)
                        .append("nombre", nombre)
                        .append("departamento", departamento);
                empleadosCollection.insertOne(doc);
                // Insertar en el árbol
                arbolEmpleados.insertar(new Empleado(id, nombre, departamento));
                mostrarTodosEmpleados(); // Refresca la tabla
                JOptionPane.showMessageDialog(this, "Empleado insertado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al insertar empleado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Muestra el tiempo total estimado usando recursividad
    private void mostrarTiempoTotalRecursivo() {
        int total = calcularTiempoTotalRecursivo(listaTareasDepartamento, 0)
                  + calcularTiempoTotalRecursivo(pilaTareasUrgentes, 0)
                  + calcularTiempoTotalRecursivo(colaTareasProgramadas, 0);
        JOptionPane.showMessageDialog(this,
                "Tiempo total estimado (recursivo): " + total + " horas (usando el campo real de cada tarea).",
                "Tiempo Total Estimado", JOptionPane.INFORMATION_MESSAGE);
    }

    // Muestra la distribución de tareas usando divide y vencerás
    private void mostrarDistribucionDivideVenceras() {
        StringBuilder sb = new StringBuilder();
        sb.append("Distribución de tareas (Divide y Vencerás):\n");
        List<Tarea> todas = new ArrayList<>();
        todas.addAll(pilaTareasUrgentes);
        todas.addAll(colaTareasProgramadas);
        todas.addAll(listaTareasDepartamento);
        distribuirTareasDivideVencerasMostrar(todas, 0, todas.size() - 1, sb, 1);
        JOptionPane.showMessageDialog(this, sb.toString(), "Distribución de Tareas", JOptionPane.INFORMATION_MESSAGE);
    }

    // Algoritmo divide y vencerás para mostrar la distribución
    private void distribuirTareasDivideVencerasMostrar(List<Tarea> tareas, int inicio, int fin, StringBuilder sb, int nivel) {
        if (inicio > fin) return;
        int medio = (inicio + fin) / 2;
        Tarea tarea = tareas.get(medio);
        sb.append("Nivel ").append(nivel).append(": [").append(tarea.getId()).append("] ")
          .append(tarea.getDescripcion()).append(" (").append(tarea.getDepartamento()).append(")\n");
        distribuirTareasDivideVencerasMostrar(tareas, inicio, medio - 1, sb, nivel + 1);
        distribuirTareasDivideVencerasMostrar(tareas, medio + 1, fin, sb, nivel + 1);
    }

    // Método para mostrar los detalles de la tarea buscada por ID
    private void buscarTareaPorIdDesdeInterfaz() {
        String id = txtBuscarPorId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID para buscar.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Tarea tarea = buscarTareaPorId(id);
        if (tarea == null) {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna tarea con ese ID.", "No encontrado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "ID: " + tarea.getId() + "\n" +
                "Descripción: " + tarea.getDescripcion() + "\n" +
                "Departamento: " + tarea.getDepartamento() + "\n" +
                "Urgencia: " + tarea.getUrgencia() + "\n" +
                "Horas estimadas: " + tarea.getHorasEstimadas(),
                "Tarea encontrada", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método para ordenar y mostrar la tabla general por urgencia y departamento
    private void ordenarTablaGeneralPorUrgenciaYDepto() {
        // Junta todas las tareas en una lista temporal
        List<Tarea> todas = new ArrayList<>();
        todas.addAll(pilaTareasUrgentes);
        todas.addAll(colaTareasProgramadas);
        todas.addAll(listaTareasDepartamento);

        // Ordena por urgencia (Alta, Media, Baja) y luego por departamento
        todas.sort(Comparator
            .comparing((Tarea t) -> urgenciaValor(t.getUrgencia()))
            .thenComparing(Tarea::getDepartamento)
        );

        modelGeneral.setRowCount(0);
        for (Tarea tarea : todas) {
            String tipo = "Departamento";
            if (pilaTareasUrgentes.contains(tarea)) tipo = "Urgente";
            else if (colaTareasProgramadas.contains(tarea)) tipo = "Programada";
            modelGeneral.addRow(new Object[]{
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getDepartamento(),
                tarea.getUrgencia(),
                tipo
            });
        }
    }

    // Método auxiliar para convertir urgencia a valor numérico
    private int urgenciaValor(String urgencia) {
        if ("Alta".equalsIgnoreCase(urgencia)) return 1;
        if ("Media".equalsIgnoreCase(urgencia)) return 2;
        if ("Baja".equalsIgnoreCase(urgencia)) return 3;
        return 4;
    }

    // Método para agregar una dependencia entre tareas
    private void agregarDependenciaTarea() {
        String idTarea = JOptionPane.showInputDialog(this, "ID de la tarea que depende de otra:");
        if (idTarea == null || idTarea.trim().isEmpty()) return;
        String idDepende = JOptionPane.showInputDialog(this, "ID de la tarea de la que depende:");
        if (idDepende == null || idDepende.trim().isEmpty()) return;

        if (!hashTareas.containsKey(idTarea) || !hashTareas.containsKey(idDepende)) {
            JOptionPane.showMessageDialog(this, "Uno o ambos IDs no existen.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (idTarea.equals(idDepende)) {
            JOptionPane.showMessageDialog(this, "Una tarea no puede depender de sí misma.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Actualiza el grafo en memoria
        dependenciasTareas.computeIfAbsent(idTarea, k -> new ArrayList<>()).add(idDepende);

        // Actualiza en MongoDB
        try {
            collection.updateOne(
                Filters.eq("id", idTarea),
                Updates.addToSet("dependencias", idDepende)
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar dependencia en MongoDB: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JOptionPane.showMessageDialog(this, "Dependencia agregada: " + idTarea + " depende de " + idDepende,
                "Dependencia agregada", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método para ver dependencias de una tarea
    private void verDependenciasTarea() {
        String idTarea = JOptionPane.showInputDialog(this, "ID de la tarea para ver sus dependencias:");
        if (idTarea == null || idTarea.trim().isEmpty()) return;
        if (!hashTareas.containsKey(idTarea)) {
            JOptionPane.showMessageDialog(this, "El ID no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<String> deps = dependenciasTareas.get(idTarea);
        if (deps == null || deps.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La tarea no tiene dependencias.", "Sin dependencias", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("La tarea [" + idTarea + "] depende de:\n");
        for (String dep : deps) {
            sb.append("- ").append(dep).append(": ").append(hashTareas.get(dep).getDescripcion()).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Dependencias de la tarea", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarTareaDeMongoDB(String id) {
    try {
        collection.deleteOne(Filters.eq("id", id));
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al eliminar tarea de MongoDB: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new SistemaGestionTareas();
            } catch (Exception e) {
                System.err.println("Error al inicializar la aplicación: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void mostrarTodosEmpleados() {
        if (modelEmpleados == null) return;
        modelEmpleados.setRowCount(0);
        List<Empleado> resultado = new ArrayList<>();
        arbolEmpleados.buscarPorDepartamento("", resultado); // "" para traer todos
        if (resultado.isEmpty()) return;
        for (Empleado emp : resultado) {
            modelEmpleados.addRow(new Object[]{emp.getId(), emp.getNombre(), emp.getDepartamento()});
        }
    }

    private String generarIdEmpleado() {
    int max = 0;
    for (Document doc : empleadosCollection.find()) {
        String id = doc.getString("id");
        if (id != null && id.matches("E\\d+")) {
            int num = Integer.parseInt(id.substring(1));
            if (num > max) max = num;
        }
    }
    return "E" + (max + 1);
}

    
}