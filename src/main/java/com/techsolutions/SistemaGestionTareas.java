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
import com.techsolutions.model.Usuario;
import com.techsolutions.gui.LoginFrame;
import com.techsolutions.gui.ArbolEmpleadosViewer;
import com.techsolutions.gui.GestionUsuariosPanel;
import com.techsolutions.services.UsuarioService;
import com.techsolutions.gui.TableWithFilters;

/**
 * SISTEMA DE GESTION DE TAREAS - TechSolutions S.A. de C.V.
 * 
 * Aplicacion principal que implementa un sistema completo de gestion de tareas
 * utilizando multiples estructuras de datos y una interfaz grafica moderna.
 * 
 * ESTRUCTURAS DE DATOS IMPLEMENTADAS:
 * =====================================
 * 
 * 1. PILA (Stack) - Tareas Urgentes:
 *    - Implementa LIFO (Last In, First Out)
 *    - Para tareas que requieren atencion inmediata
 *    - Operaciones: Push, Pop, Peek
 * 
 * 2. COLA (Queue) - Tareas Programadas:
 *    - Implementa FIFO (First In, First Out)
 *    - Para tareas con secuencia especifica
 *    - Operaciones: Enqueue, Dequeue, Front
 * 
 * 3. LISTA (List) - Tareas Departamentales:
 *    - Acceso indexado y busquedas especificas
 *    - Para tareas organizadas por departamento
 *    - Operaciones: Insertar, Eliminar, Buscar por indice
 * 
 * 4. COLA DE PRIORIDAD (PriorityQueue) - Tareas Priorizadas:
 *    - Ordenamiento automatico por prioridad numerica
 *    - Para tareas con niveles de importancia
 *    - Operaciones: Insertar con prioridad, Extraer mas prioritaria
 * 
 * 5. ARBOL BINARIO DE BUSQUEDA (BST) - Empleados:
 *    - Busqueda eficiente O(log n)
 *    - Empleados ordenados por ID
 *    - Operaciones: Insertar, Buscar, Recorridos
 * 
 * CARACTERISTICAS TECNICAS:
 * =========================
 * - Interfaz grafica moderna con Swing
 * - Integracion con MongoDB para persistencia
 * - Algoritmos de busqueda y ordenamiento
 * - Recursividad para calculos complejos
 * - Divide y venceras para distribucion de tareas
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
    private final Stack<Tarea> pilaTareasUrgentes = new Stack<>();
    
    /** COLA - Para tareas programadas (FIFO) */
    private final LinkedList<Tarea> colaTareasProgramadas = new LinkedList<>();
    
    /** LISTA - Para tareas departamentales (acceso indexado) */
    private final List<Tarea> listaTareasDepartamento = new ArrayList<>();
    
    // ===============================================
    // CONEXION A BASE DE DATOS MONGODB
    // ===============================================
    
    /** Cliente de conexion a MongoDB */
    private MongoClient mongoClient;
    
    /** Base de datos MongoDB */
    private MongoDatabase database;
    
    /** Coleccion de tareas en MongoDB */
    private MongoCollection<Document> collection;
    
    /** Coleccion de empleados en MongoDB */
    private MongoCollection<Document> empleadosCollection;
    
    // ===============================================
    // COMPONENTES DE INTERFAZ GRAFICA
    // ===============================================
    
    /** Panel con pestanas principales */
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
    private JButton btnVerPrioridad, btnEliminarPrioridad; // Agrega esta linea junto con los otros botones
    
    // Panel Empleados
    private JPanel panelEmpleados;
    private JTable tablaEmpleados;
    private DefaultTableModel modelEmpleados;
    private JTextField txtBusquedaEmpleado;
    private JButton btnBuscarEmpleado, btnMostrarArbol, btnInsertarEmpleado;

    // Las clases de modelo ahora estan en paquetes separados para mejor organizacion



    // Recursividad para sumar tiempo estimado
    private int calcularTiempoTotalRecursivo(List<Tarea> tareas, int idx) {
        if (idx >= tareas.size()) return 0;
        int tiempo = tareas.get(idx).getHorasEstimadas(); // Usa el campo real
        return tiempo + calcularTiempoTotalRecursivo(tareas, idx + 1);
    }

    // Divide y venceras para distribuir tareas (puedes mostrar la asignacion en la interfaz)
    private void distribuirTareasDivideVenceras(List<Tarea> tareas, int inicio, int fin) {
        if (inicio >= fin) return;
        int medio = (inicio + fin) / 2;
        // Aqui podrias asignar tareas.get(medio) a un empleado/proyecto
        distribuirTareasDivideVenceras(tareas, inicio, medio);
        distribuirTareasDivideVenceras(tareas, medio + 1, fin);
    }

    // HashMap para tareas y empleados
    private final Map<String, Tarea> hashTareas = new HashMap<>();

    // Metodos de ordenamiento y busqueda
    private void ordenarTareasPorPrioridadFecha(List<TareaPrioridad> lista) {
        lista.sort(Comparator.naturalOrder());
    }

    // Ejemplo de busqueda eficiente
    private Tarea buscarTareaPorId(String id) {
        return hashTareas.get(id);
    }

    // Cola de prioridades global
    private final PriorityQueue<TareaPrioridad> colaPrioridad = new PriorityQueue<>();

    // Arbol binario de empleados global
    private final ArbolEmpleados arbolEmpleados = new ArbolEmpleados();

    // --- Dependencias entre tareas (grafo simple) ---
    private final Map<String, List<String>> dependenciasTareas = new HashMap<>();
    private JButton btnAgregarDependencia, btnVerDependencias;
    
    // Usuario actual del sistema
    private Usuario usuarioActual;

    public SistemaGestionTareas(Usuario usuario) {
        super("Sistema de Gestion de Tareas - TechSolutions S.A. de C.V.");
        this.usuarioActual = usuario;
        
        inicializarSistema();
    }
    
    // Constructor por defecto (para compatibilidad)
    public SistemaGestionTareas() {
        super("Sistema de Gestion de Tareas - TechSolutions S.A. de C.V.");
        // Crear usuario temporal para testing
        this.usuarioActual = new Usuario("temp", "temp", Usuario.Rol.JEFE, "Direccion");
        
        inicializarSistema();
    }
    
    /**
     * Inicializa todos los componentes del sistema
     */
    private void inicializarSistema() {
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
            System.err.println("Error configurando Look and Feel: " + e.getMessage());
        }
        
        // HashMap de tareas
        hashTareas.put("T1", new Tarea("T1", "Revisar codigo", "Desarrollo", "Alta"));

        // Conectar a MongoDB
        conectarMongoDB();

        // Configurar interfaz completa
        configurarInterfaz();

        // Cargar datos desde MongoDB
        cargarDatosDesdeMongoDB();
        
        // Cargar empleados en el arbol
        cargarEmpleadosDesdeMongoDB();

        // Ejemplo de ordenamiento
        java.util.List<TareaPrioridad> listaPrioridad = new ArrayList<>();
        listaPrioridad.add(new TareaPrioridad("T2", "Documentar", "Soporte Tecnico", "Media", 2, "2025-09-20"));
        listaPrioridad.add(new TareaPrioridad("T3", "Testear", "Desarrollo", "Alta", 1, "2025-09-18"));
        ordenarTareasPorPrioridadFecha(listaPrioridad);
        
        // Mostrar ventana
        setVisible(true);
    }
    
    // Metodo para crear icono de la aplicacion
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
    
    // Metodo para crear el panel principal con gradiente
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
    
    // Conectar a MongoDB tambien para empleados
    private void conectarMongoDB() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("techsolutions");
            collection = database.getCollection("tareas");
            empleadosCollection = database.getCollection("empleados");
            System.out.println("Conexion a MongoDB establecida correctamente");
        } catch (Exception e) {
            System.err.println("Error al conectar con MongoDB: " + e.getMessage());
            System.err.println("El sistema funcionara sin persistencia de datos");
            // No mostrar dialogo de error que podria interferir con la inicializacion
            mongoClient = null;
            database = null;
            collection = null;
            empleadosCollection = null;
        }
    }
    
    // Cargar empleados en el arbol desde MongoDB
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
        // Si no hay conexion a MongoDB, no intentar cargar datos
        if (collection == null || empleadosCollection == null) {
            System.out.println("MongoDB no disponible, usando datos predeterminados");
            cargarDatosPredeterminados();
            return;
        }
        
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

                // Si tiene prioridad y fecha, usalo como TareaPrioridad
                if (prioridad > 0 && fechaEntrega != null) {
                    TareaPrioridad tp = new TareaPrioridad(
                        tarea.getId(), tarea.getDescripcion(), tarea.getDepartamento(),
                        tarea.getUrgencia(), prioridad, fechaEntrega
                    );
                    colaPrioridad.add(tp);
                }

                // Logica original
                if ("urgente".equals(tipo)) pilaTareasUrgentes.push(tarea);
                else if ("programada".equals(tipo)) colaTareasProgramadas.add(tarea);
                else if ("departamento".equals(tipo)) {
                    // Solo agregar tareas del departamento del usuario actual
                    if (tarea.getDepartamento() != null && 
                        tarea.getDepartamento().equals(usuarioActual.getDepartamento())) {
                        listaTareasDepartamento.add(tarea);
                    }
                }

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

            // Cargar empleados en el arbol desde MongoDB
            for (Document doc : empleadosCollection.find()) {
                arbolEmpleados.insertar(new Empleado(
                    doc.getString("id"),
                    doc.getString("nombre"),
                    doc.getString("departamento")
                ));
            }

            actualizarTablas();
            mostrarTodosEmpleados(); // <-- Agrega esta linea
        } catch (Exception e) {
            System.err.println("Error al cargar datos desde MongoDB: " + e.getMessage());
            cargarDatosPredeterminados();
        }
    }
    
    /**
     * Carga algunos datos predeterminados cuando MongoDB no esta disponible
     */
    private void cargarDatosPredeterminados() {
        // Agregar algunas tareas de ejemplo
        Tarea tarea1 = new Tarea("T001", "Revisar codigo fuente", "Desarrollo", "Alta");
        Tarea tarea2 = new Tarea("T002", "Actualizar documentacion", "Soporte", "Media");
        Tarea tarea3 = new Tarea("T003", "Preparar presentacion", "Ventas", "Baja");
        
        // Agregar tareas con prioridad
        TareaPrioridad tarea4 = new TareaPrioridad("T004", "Implementar nueva funcionalidad", "Desarrollo", "Alta", 1, "2025-12-31");
        TareaPrioridad tarea5 = new TareaPrioridad("T005", "Revisar reportes mensuales", "Administracion", "Media", 2, "2025-11-30");
        
        pilaTareasUrgentes.push(tarea1);
        colaTareasProgramadas.add(tarea2);
        listaTareasDepartamento.add(tarea3);
        colaPrioridad.add(tarea4);
        colaPrioridad.add(tarea5);
        
        hashTareas.put(tarea1.getId(), tarea1);
        hashTareas.put(tarea2.getId(), tarea2);
        hashTareas.put(tarea3.getId(), tarea3);
        hashTareas.put(tarea4.getId(), tarea4);
        hashTareas.put(tarea5.getId(), tarea5);
        
        // Agregar empleados de ejemplo
        arbolEmpleados.insertar(new Empleado("E001", "Juan Perez", "Desarrollo"));
        arbolEmpleados.insertar(new Empleado("E002", "Maria Garcia", "Soporte"));
        arbolEmpleados.insertar(new Empleado("E003", "Carlos Lopez", "Ventas"));
        
        actualizarTablasSegunRol();
    }
    
    /**
     * Actualiza las tablas segun el rol del usuario
     */
    private void actualizarTablasSegunRol() {
        if (usuarioActual.esJefe()) {
            // Solo el jefe tiene acceso a todas las tablas
            actualizarTablas();
            mostrarTodosEmpleados();
        } else if (usuarioActual.esJefeDepartamento()) {
            // Jefe de departamento solo actualiza las tablas que maneja
            actualizarTablasDepartamento();
        } else if (usuarioActual.esEmpleado()) {
            // Empleados solo manejan sus propias tareas
            actualizarTareasEmpleado();
        }
    }
    
    /**
     * Actualiza solo las tablas disponibles para jefe de departamento
     */
    private void actualizarTablasDepartamento() {
        // Solo actualizar las tablas que el jefe de departamento puede ver
        if (modelLista != null) {
            actualizarTablaLista();
        }
        if (modelPrioridad != null) {
            actualizarTablaPrioridad();
        }
        if (modelEmpleados != null) {
            mostrarTodosEmpleados();
        }
    }
    
    /**
     * Actualiza solo las tareas visibles para empleados
     */
    private void actualizarTareasEmpleado() {
        // Actualizar la tabla de tareas para el empleado
        if (usuarioActual.getRol() == Usuario.Rol.EMPLEADO) {
            // Limpiar el modelo de la tabla
            DefaultTableModel modelo = (DefaultTableModel) tablaLista.getModel();
            modelo.setRowCount(0);
            
            // Recargar las tareas asignadas al empleado
            cargarTareasEmpleado(modelo);
            
            System.out.println("🔄 Vista de empleado actualizada correctamente");
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
                    .append("horasEstimadas", tarea.getHorasEstimadas())
                    .append("empleadoAsignado", tarea.getEmpleadoAsignado()) // NUEVO: empleado asignado
                    .append("tipo", tipo)
                    .append("prioridad", prioridad)
                    .append("fechaEntrega", fechaEntrega);
            collection.insertOne(doc);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar en MongoDB: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza una tarea existente en MongoDB
     */
    private void actualizarTareaEnMongoDB(Tarea tarea) {
        try {
            Document filtro = new Document("id", tarea.getId());
            Document actualizacion = new Document("$set", new Document()
                    .append("descripcion", tarea.getDescripcion())
                    .append("departamento", tarea.getDepartamento())
                    .append("urgencia", tarea.getUrgencia())
                    .append("horasEstimadas", tarea.getHorasEstimadas())
                    .append("empleadoAsignado", tarea.getEmpleadoAsignado()));
            
            collection.updateOne(filtro, actualizacion);
            System.out.println("Tarea actualizada en MongoDB: " + tarea.getId());
        } catch (Exception e) {
            System.err.println("Error al actualizar tarea en MongoDB: " + e.getMessage());
        }
    }
    
    private Tarea documentToTarea(Document doc) {
        String id = doc.getString("id");
        String descripcion = doc.getString("descripcion");
        String departamento = doc.getString("departamento");
        String urgencia = doc.getString("urgencia");
        int horasEstimadas = doc.getInteger("horasEstimadas", 1);
        String empleadoAsignado = doc.getString("empleadoAsignado"); // NUEVO
        
        Tarea tarea = new Tarea(id, descripcion, departamento, urgencia, horasEstimadas);
        if (empleadoAsignado != null && !empleadoAsignado.isEmpty()) {
            tarea.setEmpleadoAsignado(empleadoAsignado);
        }
        return tarea;
    }
    
    private void configurarInterfaz() {
        // Configurar ventana principal con tamano mas grande
        setTitle("Sistema de Gestion de Tareas - " + usuarioActual.getNombre() + " (" + usuarioActual.getRol().getDescripcion() + ")");
        setSize(1400, 900); // Aumentado de 1200x800 a 1400x900
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizar ventana
        
        // Configurar panel principal con gradiente
        JPanel mainPanel = createMainPanel();
        setContentPane(mainPanel);
        
        // Crear menu superior con informacion del usuario
        crearMenuSuperior(mainPanel);
        
        // Crear el tabbedPane con estilo moderno
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Aumentado de 13 a 14
        tabbedPane.setBackground(new Color(240, 248, 255));
        tabbedPane.setBorder(new EmptyBorder(15, 15, 15, 15)); // Aumentado padding

        // Configurar paneles segun el rol del usuario
        configurarPanelesPorRol();

        // Agregar el tabbedPane al panel principal
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Crea el menu superior con informacion del usuario y opciones de logout
     */
    private void crearMenuSuperior(JPanel mainPanel) {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(true); // Cambiar a true para que se vea
        panelSuperior.setBackground(new Color(60, 120, 170)); // Fondo azul
        panelSuperior.setBorder(new EmptyBorder(15, 20, 15, 20)); // Aumentar padding
        panelSuperior.setPreferredSize(new Dimension(0, 60)); // Altura fija
        
        // Informacion del usuario
        String nombreCompleto = usuarioActual.getNombre() != null ? usuarioActual.getNombre() : usuarioActual.getUsername();
        JLabel lblUsuario = new JLabel(String.format("%s | %s | %s", 
            nombreCompleto, 
            usuarioActual.getRol().getDescripcion(),
            usuarioActual.getDepartamento()));
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Aumentar fuente
        lblUsuario.setForeground(Color.WHITE);
        
        // Panel para el reloj (opcional)
        JLabel lblHora = new JLabel("[Hora] " + java.time.LocalTime.now().toString().substring(0, 5));
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblHora.setForeground(Color.WHITE);
        
        // Boton de logout
        JButton btnLogout = crearBotonModerno("Cerrar Sesion", new Color(220, 20, 60));
        btnLogout.setPreferredSize(new Dimension(150, 35)); // Tamano fijo
        btnLogout.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this, 
                "Esta seguro de que desea cerrar sesion?", 
                "Confirmar Logout", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        
        // Panel izquierdo para usuario y hora
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setOpaque(false);
        panelIzquierdo.add(lblUsuario, BorderLayout.WEST);
        panelIzquierdo.add(lblHora, BorderLayout.EAST);
        
        panelSuperior.add(panelIzquierdo, BorderLayout.WEST);
        panelSuperior.add(btnLogout, BorderLayout.EAST);
        mainPanel.add(panelSuperior, BorderLayout.NORTH);
    }
    
    /**
     * Configura los paneles segun el rol del usuario autenticado
     */
    private void configurarPanelesPorRol() {
        if (usuarioActual.esCEO()) {
            // CEO: Acceso completo a todo el sistema con permisos especiales
            configurarInterfazCEO();
        } else if (usuarioActual.esJefe()) {
            // JEFE GENERAL: Acceso completo a todo el sistema
            configurarInterfazJefe();
        } else if (usuarioActual.esJefeDepartamento()) {
            // JEFE DE DEPARTAMENTO: Solo gestion de su departamento
            configurarInterfazJefeDepartamento();
        } else if (usuarioActual.esEmpleado()) {
            // EMPLEADO: Solo sus tareas asignadas
            configurarInterfazEmpleado();
        }
    }
    
    /**
     * Configura la interfaz completa para el jefe general
     */
    private void configurarInterfazJefe() {
        // Configurar paneles con diseno mejorado
        panelPila = crearPanelPila();
        panelCola = crearPanelCola();
        panelLista = crearPanelLista();
        panelGeneral = crearPanelGeneral();
        panelPrioridad = crearPanelPrioridad();
        panelEmpleados = crearPanelEmpleados();
        JPanel panelAdministracion = crearPanelAdministracion();

        // Agregar tabs con iconos (acceso completo)
        tabbedPane.addTab("Tareas Urgentes", panelPila);
        tabbedPane.addTab("Tareas Programadas", panelCola);
        tabbedPane.addTab("Por Departamento", panelLista);
        tabbedPane.addTab("Todas las Tareas", panelGeneral);
        tabbedPane.addTab("Prioridades", panelPrioridad);
        tabbedPane.addTab("Empleados", panelEmpleados);
        tabbedPane.addTab("Administracion", panelAdministracion);
    }
    
    /**
     * Configura la interfaz completa para el CEO con permisos especiales
     */
    private void configurarInterfazCEO() {
        // Configurar titulo especial para CEO
        setTitle("TechSolutions - " + usuarioActual.getRol().getDescripcion() + " - Control Total");
        
        // CEO tiene acceso completo igual que jefe pero con permisos especiales
        panelPila = crearPanelPila();
        panelCola = crearPanelCola();
        panelLista = crearPanelLista();
        panelGeneral = crearPanelGeneral();
        panelPrioridad = crearPanelPrioridad();
        panelEmpleados = crearPanelEmpleados();
        JPanel panelAdministracion = crearPanelAdministracion();
        JPanel panelEjecutivo = crearPanelEjecutivo(); // Panel especial para CEO

        // Agregar tabs con acceso ejecutivo completo
        tabbedPane.addTab("Tareas Urgentes", panelPila);
        tabbedPane.addTab("Tareas Programadas", panelCola);
        tabbedPane.addTab("Por Departamento", panelLista);
        tabbedPane.addTab("Todas las Tareas", panelGeneral);
        tabbedPane.addTab("Prioridades", panelPrioridad);
        tabbedPane.addTab("Empleados", panelEmpleados);
        tabbedPane.addTab("Administracion", panelAdministracion);
        tabbedPane.addTab("Panel Ejecutivo", panelEjecutivo);
    }
    
    /**
     * Configura la interfaz para jefe de departamento con menus especificos
     */
    private void configurarInterfazJefeDepartamento() {
        // Configurar titulo personalizado
        setTitle("TechSolutions - " + usuarioActual.getRol().getDescripcion() + 
                 " (" + usuarioActual.getDepartamento() + ")");
        
        // Paneles basicos para jefes de departamento
        panelLista = crearPanelListaDepartamento();
        panelPrioridad = crearPanelPrioridadDepartamento();
        panelEmpleados = crearPanelEmpleadosDepartamento();
        JPanel panelGestionDepartamento = crearPanelGestionDepartamento();
        JPanel panelEspecializado = crearPanelEspecializadoPorDepartamento();

        // Agregar tabs limitados para jefe de departamento
        tabbedPane.addTab("Tareas del Departamento", panelLista);
        tabbedPane.addTab("Prioridades", panelPrioridad);
        tabbedPane.addTab("Empleados del Depto", panelEmpleados);
        tabbedPane.addTab("Gestion", panelGestionDepartamento);
        
        // Agregar panel especializado si existe
        if (panelEspecializado != null) {
            String nombreTab = obtenerNombreTabEspecializado();
            tabbedPane.addTab(nombreTab, panelEspecializado);
        }
        
        mostrarMensajeBienvenidaJefe();
    }
    
    /**
     * Configura la interfaz simplificada para empleado
     */
    private void configurarInterfazEmpleado() {
        JPanel panelMisTareas = crearPanelMisTareas();
        JPanel panelCompletarTareas = crearPanelCompletarTareas();
        JPanel panelTareasPendientes = crearPanelTareasPendientes();

        // Agregar tabs para empleado
        tabbedPane.addTab("Mis Tareas", panelMisTareas);
        tabbedPane.addTab("Completar Tareas", panelCompletarTareas);
        tabbedPane.addTab("Pendientes", panelTareasPendientes);
        
        // Agregar panel departamental especifico si esta disponible
        JPanel panelDepartamental = crearPanelDepartamentalEmpleado();
        if (panelDepartamental != null) {
            String nombreTab = obtenerNombreTabDepartamentalEmpleado();
            tabbedPane.addTab(nombreTab, panelDepartamental);
        }
        
        // Mostrar mensaje de bienvenida
        mostrarMensajeBienvenidaEmpleado();
    }

    private JPanel crearPanelPila() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Gestion de Tareas Urgentes", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
        
        // Modelo de tabla mejorado - NO EDITABLE
        String[] columnas = {"ID", "Descripcion", "Departamento", "Urgencia"};
        modelPila = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer tabla completamente no editable
            }
        };
        
        // Usar nueva tabla con filtros
        TableWithFilters tablaPila = new TableWithFilters(modelPila);
        this.tablaPila = tablaPila; // Asignar referencia
        
        // Panel de botones en la parte SUPERIOR
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(240, 248, 255));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            "Acciones", TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), new Color(70, 130, 180)
        ));
        
        btnPush = crearBotonModerno("Agregar", new Color(144, 238, 144));
        btnPop = crearBotonModerno("Eliminar", new Color(255, 182, 193));
        btnPeek = crearBotonModerno("Ver Último", new Color(173, 216, 230));
        
        btnPush.addActionListener(e -> agregarTareaPila());
        btnPop.addActionListener(e -> eliminarTareaPila());
        btnPeek.addActionListener(e -> verTareaPila());
        
        panelBotones.add(btnPush);
        panelBotones.add(btnPop);
        panelBotones.add(btnPeek);
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        
        // Agregar panel de filtros arriba de la tabla
        panelCentral.add(tablaPila.getFilterPanel(), BorderLayout.NORTH);
        
        // Scroll personalizado para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaPila);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        // Assemblar panel final
        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);
        
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
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Gestión de Tareas Programadas", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
        
        // Modelo de tabla NO EDITABLE
        String[] columnas = {"ID", "Descripción", "Departamento", "Urgencia"};
        modelCola = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Usar nueva tabla con filtros
        TableWithFilters tablaCola = new TableWithFilters(modelCola);
        this.tablaCola = tablaCola;
        
        // Panel de botones en la parte SUPERIOR
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(240, 248, 255));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            "Acciones", TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), new Color(70, 130, 180)
        ));
        
        btnEnqueue = crearBotonModerno("Agregar Tarea", new Color(144, 238, 144));
        btnDequeue = crearBotonModerno("Eliminar Tarea", new Color(255, 182, 193));
        btnFront = crearBotonModerno("Ver Primera", new Color(173, 216, 230));
        
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
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        
        // Agregar panel de filtros
        panelCentral.add(tablaCola.getFilterPanel(), BorderLayout.NORTH);
        
        // Scroll para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaCola);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        // Assemblar panel final
        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton btnVerPorIndice; // Agrega esto junto con los otros botones

    private JPanel crearPanelLista() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Gestión de Tareas por Departamento", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));

        // Modelo de tabla NO EDITABLE con columna de empleado asignado
        String[] columnas = {"ID", "Descripción", "Departamento", "Urgencia", "Empleado Asignado"};
        modelLista = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Usar nueva tabla con filtros
        TableWithFilters tablaLista = new TableWithFilters(modelLista);
        this.tablaLista = tablaLista;
        
        // Panel de botones en la parte SUPERIOR
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(240, 248, 255));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            "Acciones", TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), new Color(70, 130, 180)
        ));
        
        btnInsertar = crearBotonModerno("Insertar", new Color(144, 238, 144));
        btnEliminar = crearBotonModerno("Eliminar", new Color(255, 182, 193));
        btnBuscar = crearBotonModerno("Buscar", new Color(173, 216, 230));
        btnVerPorIndice = crearBotonModerno("Ver por Indice", new Color(255, 228, 181));
        JButton btnActualizarLista = crearBotonModerno("Actualizar", new Color(70, 130, 180));

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
        
        btnActualizarLista.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTablaLista();
            }
        });

        panelBotones.add(btnInsertar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnVerPorIndice);
        panelBotones.add(btnActualizarLista);
        
        // Panel de búsqueda personalizada (adicional a los filtros de la tabla)
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(new Color(240, 248, 255));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda Rápida"));
        
        JLabel lblBusqueda = new JLabel("Departamento:");
        lblBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 11));
        panelBusqueda.add(lblBusqueda);
        
        txtBusqueda = new JTextField(15);
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panelBusqueda.add(txtBusqueda);
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        
        // Agregar panel de filtros de la tabla
        panelCentral.add(tablaLista.getFilterPanel(), BorderLayout.NORTH);
        
        // Scroll para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaLista);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        // Panel superior con botones y búsqueda
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBotones, BorderLayout.CENTER);
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);
        
        // Assemblar panel final
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton btnCalcularTiempoTotal; // Nuevo botón
    private JButton btnDistribuirTareas;    // Nuevo botón
    private JButton btnBuscarPorId; // Nuevo botón
    private JTextField txtBuscarPorId; // Nuevo campo de texto
    private JButton btnOrdenarUrgenciaDepto; // Nuevo botón

    private JPanel crearPanelGeneral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Vista General de Todas las Tareas", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));

        // Modelo de tabla NO EDITABLE
        String[] columnas = {"ID", "Descripción", "Departamento", "Urgencia", "Tipo"};
        modelGeneral = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Usar nueva tabla con filtros
        TableWithFilters tablaGeneral = new TableWithFilters(modelGeneral);
        this.tablaGeneral = tablaGeneral;

        // Panel de botones en la parte SUPERIOR - organizado en filas
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBotones.setBackground(new Color(240, 248, 255));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            "Herramientas Avanzadas", TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), new Color(70, 130, 180)
        ));
        
        // Primera fila de botones
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        fila1.setBackground(new Color(240, 248, 255));
        
        btnActualizarGeneral = crearBotonModerno("Actualizar", new Color(173, 216, 230));
        btnCalcularTiempoTotal = crearBotonModerno("Tiempo Total", new Color(255, 228, 181));
        btnDistribuirTareas = crearBotonModerno("Distribuir", new Color(221, 160, 221));
        btnOrdenarUrgenciaDepto = crearBotonModerno("Ordenar", new Color(144, 238, 144));
        
        fila1.add(btnActualizarGeneral);
        fila1.add(btnCalcularTiempoTotal);
        fila1.add(btnDistribuirTareas);
        fila1.add(btnOrdenarUrgenciaDepto);
        
        // Segunda fila de botones
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        fila2.setBackground(new Color(240, 248, 255));
        
        // Panel de búsqueda por ID
        JPanel panelBusquedaId = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelBusquedaId.setBackground(new Color(240, 248, 255));
        
        JLabel lblId = new JLabel("ID:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtBuscarPorId = new JTextField(8);
        txtBuscarPorId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnBuscarPorId = crearBotonModerno("Buscar", new Color(255, 182, 193));
        
        panelBusquedaId.add(lblId);
        panelBusquedaId.add(txtBuscarPorId);
        panelBusquedaId.add(btnBuscarPorId);
        
        btnAgregarDependencia = crearBotonModerno("+ Dependencia", new Color(176, 196, 222));
        btnVerDependencias = crearBotonModerno("Ver Deps", new Color(255, 218, 185));
        
        fila2.add(panelBusquedaId);
        fila2.add(btnAgregarDependencia);
        fila2.add(btnVerDependencias);
        
        panelBotones.add(fila1);
        panelBotones.add(fila2);

        // Configurar eventos
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
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        
        // Agregar panel de filtros
        panelCentral.add(tablaGeneral.getFilterPanel(), BorderLayout.NORTH);
        
        // Scroll para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaGeneral);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        // Assemblar panel final
        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel crearPanelPrioridad() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Gestión de Tareas por Prioridad", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));

        // Modelo de tabla NO EDITABLE
        String[] columnas = {"ID", "Descripción", "Departamento", "Urgencia", "Prioridad", "Fecha Entrega"};
        modelPrioridad = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Usar nueva tabla con filtros
        TableWithFilters tablaPrioridad = new TableWithFilters(modelPrioridad);
        this.tablaPrioridad = tablaPrioridad;

        // Panel de botones en la parte SUPERIOR
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(240, 248, 255));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            "Acciones", TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), new Color(70, 130, 180)
        ));

        btnVerPrioridad = crearBotonModerno("Ver Mayor Prioridad", new Color(255, 215, 0));
        btnEliminarPrioridad = crearBotonModerno("Eliminar Mayor Prioridad", new Color(255, 182, 193));

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

        panelBotones.add(btnVerPrioridad);
        panelBotones.add(btnEliminarPrioridad);
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        
        // Agregar panel de filtros
        panelCentral.add(tablaPrioridad.getFilterPanel(), BorderLayout.NORTH);
        
        // Scroll para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaPrioridad);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        // Assemblar panel final
        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);

        return panel;
    }

    // Nueva lógica para la tabla de prioridades
    private void actualizarTablaPrioridad() {
        if (modelPrioridad == null) {
            System.err.println("Advertencia: modelPrioridad es null, omitiendo actualización");
            return;
        }
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
            
            if (modelLista == null) {
                System.err.println("Advertencia: modelLista es null en buscarTareasLista");
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
        if (modelPila == null) {
            System.err.println("Advertencia: modelPila es null, omitiendo actualización");
            return;
        }
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
        if (modelCola == null) {
            System.err.println("Advertencia: modelCola es null, omitiendo actualización");
            return;
        }
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
        if (modelLista == null) {
            System.err.println("Advertencia: modelLista es null, omitiendo actualización");
            return;
        }
        modelLista.setRowCount(0);
        
        // Conjunto para evitar duplicados
        java.util.Set<String> tareasYaAgregadas = new java.util.HashSet<>();
        
        // Función helper para agregar tarea sin duplicados
        java.util.function.Consumer<Tarea> agregarTarea = tarea -> {
            if (!tareasYaAgregadas.contains(tarea.getId())) {
                tareasYaAgregadas.add(tarea.getId());
                
                String empleadoAsignado = tarea.getEmpleadoAsignado();
                String nombreEmpleado = "Sin asignar";
                
                // Buscar el nombre del empleado asignado
                if (empleadoAsignado != null && !empleadoAsignado.isEmpty()) {
                    java.util.List<Empleado> empleados = new java.util.ArrayList<>();
                    arbolEmpleados.buscarPorDepartamento("", empleados); // Obtener todos
                    
                    for (Empleado emp : empleados) {
                        if (emp.getId().equals(empleadoAsignado)) {
                            nombreEmpleado = emp.getNombre();
                            break;
                        }
                    }
                }
                
                modelLista.addRow(new Object[]{
                    tarea.getId(),
                    tarea.getDescripcion(),
                    tarea.getDepartamento(),
                    tarea.getUrgencia(),
                    nombreEmpleado
                });
            }
        };
        
        // Agregar tareas de todas las estructuras de datos
        for (Tarea tarea : pilaTareasUrgentes) {
            agregarTarea.accept(tarea);
        }
        
        for (Tarea tarea : colaTareasProgramadas) {
            agregarTarea.accept(tarea);
        }
        
        for (Tarea tarea : listaTareasDepartamento) {
            agregarTarea.accept(tarea);
        }
        
        // Agregar tareas de la cola de prioridad
        for (TareaPrioridad tareaPrior : colaPrioridad) {
            if (tareaPrior != null) {
                agregarTarea.accept(tareaPrior);
            }
        }
    }
    
    private void actualizarTablaGeneral() {
        if (modelGeneral == null) {
            System.err.println("Advertencia: modelGeneral es null, omitiendo actualización");
            return;
        }
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
                JOptionPane.showMessageDialog(this, "Indice fuera de rango.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Tarea tarea = listaTareasDepartamento.get(idx);
            JOptionPane.showMessageDialog(this,
                    "Tarea en índice " + idx + ":\n" +
                    "ID: " + tarea.getId() + "\n" +
                    "Descripción: " + tarea.getDescripcion() + "\n" +
                    "Departamento: " + tarea.getDepartamento() + "\n" +
                    "Urgencia: " + tarea.getUrgencia(),
                    "Tarea por Indice", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al acceder por indice: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Agrega este método para crear el panel de empleados
    private JPanel crearPanelEmpleados() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Gestión de Empleados", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));

        // Modelo de tabla NO EDITABLE - Incluyendo columna de tareas
        String[] columnas = {"ID", "Nombre", "Departamento", "Tareas Asignadas"};
        modelEmpleados = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Usar nueva tabla con filtros
        TableWithFilters tablaEmpleados = new TableWithFilters(modelEmpleados);
        this.tablaEmpleados = tablaEmpleados;

        // Panel de botones en la parte SUPERIOR
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(240, 248, 255));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            "Acciones", TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), new Color(70, 130, 180)
        ));

        btnBuscarEmpleado = crearBotonModerno("Buscar", new Color(173, 216, 230));
        btnMostrarArbol = crearBotonModerno("Ver Arbol", new Color(144, 238, 144));
        btnInsertarEmpleado = crearBotonModerno("Insertar (Jefe)", new Color(255, 228, 181));

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

        panelBotones.add(btnBuscarEmpleado);
        panelBotones.add(btnMostrarArbol);
        panelBotones.add(btnInsertarEmpleado);
        
        // Panel de búsqueda personalizada (adicional a los filtros de la tabla)
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(new Color(240, 248, 255));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda Rápida"));
        
        JLabel lblBusqueda = new JLabel("Departamento:");
        lblBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 11));
        panelBusqueda.add(lblBusqueda);
        
        txtBusquedaEmpleado = new JTextField(15);
        txtBusquedaEmpleado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panelBusqueda.add(txtBusquedaEmpleado);
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        
        // Agregar panel de filtros de la tabla
        panelCentral.add(tablaEmpleados.getFilterPanel(), BorderLayout.NORTH);
        
        // Scroll para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaEmpleados);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        // Panel superior con botones y búsqueda
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBotones, BorderLayout.CENTER);
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);
        
        // Assemblar panel final
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);

        return panel;
    }

    // Agrega este método para buscar empleados por departamento
    private void buscarEmpleadosPorDepartamento() {
        String departamento = txtBusquedaEmpleado.getText().trim();
        if (modelEmpleados == null) {
            System.err.println("Advertencia: modelEmpleados es null en buscarEmpleadosPorDepartamento");
            return;
        }
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

    // Agrega este método para mostrar la estructura del árbol con interfaz moderna
    private void mostrarEstructuraArbol() {
        if (arbolEmpleados.estaVacio()) {
            JOptionPane.showMessageDialog(this, 
                "No hay empleados registrados en el sistema.\n\n" +
                "Agregue empleados primero usando el boton 'Insertar'.", 
                "Arbol Vacio", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Crear y mostrar la ventana moderna del árbol
        ArbolEmpleadosViewer viewer = new ArbolEmpleadosViewer(arbolEmpleados);
        viewer.setVisible(true);
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
        sb.append("Distribución de tareas:\n");
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

        if (modelGeneral == null) {
            System.err.println("Advertencia: modelGeneral es null en vista general");
            return;
        }
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
                // Abrir pantalla de login en lugar del sistema directamente
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error al inicializar la aplicación: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void mostrarTodosEmpleados() {
        if (modelEmpleados == null) return;
        modelEmpleados.setRowCount(0);
        
        // Obtener empleados filtrados segun el rol del usuario actual
        if (usuarioActual == null) return;
        
        String departamentoUsuario = usuarioActual.getDepartamento();
        List<Empleado> resultado = new ArrayList<>();
        arbolEmpleados.buscarPorDepartamento("", resultado); // "" para traer todos
        
        if (resultado.isEmpty()) return;
        
        for (Empleado emp : resultado) {
            // Aplicar filtros segun el rol
            boolean mostrarEmpleado = false;
            
            if (usuarioActual.getRol() == Usuario.Rol.CEO || usuarioActual.getRol() == Usuario.Rol.JEFE) {
                // CEO y JEFE ven todos los empleados
                mostrarEmpleado = true;
            } else if (usuarioActual.getRol() == Usuario.Rol.JEFE_DEPARTAMENTO) {
                // JEFE_DEPARTAMENTO solo ve empleados de su departamento
                mostrarEmpleado = emp.getDepartamento().equals(departamentoUsuario);
            } else {
                // EMPLEADO solo se ve a si mismo
                mostrarEmpleado = emp.getId() == usuarioActual.getId();
            }
            
            if (mostrarEmpleado) {
                // Contar tareas asignadas a este empleado
                int tareasAsignadas = contarTareasEmpleado(emp.getId());
                
                modelEmpleados.addRow(new Object[]{
                    emp.getId(),
                    emp.getNombre(),
                    emp.getDepartamento(),
                    tareasAsignadas + " tareas"
                });
            }
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

    /**
     * Cuenta las tareas relacionadas con un empleado específico según su departamento
     */
    private int contarTareasEmpleado(String empleadoId) {
        int contador = 0;
        String departamentoEmpleado = null;
        
        // Buscar el departamento del empleado
        Empleado empleado = arbolEmpleados.buscarPorId(empleadoId);
        if (empleado != null) {
            departamentoEmpleado = empleado.getDepartamento();
        }
        
        if (departamentoEmpleado == null) return 0;
        
        // Contar tareas por departamento en estructuras locales
        
        // Buscar en pila de tareas urgentes
        for (Tarea tarea : pilaTareasUrgentes) {
            if (departamentoEmpleado.equals(tarea.getDepartamento())) {
                contador++;
            }
        }
        
        // Buscar en cola de tareas programadas
        for (Tarea tarea : colaTareasProgramadas) {
            if (departamentoEmpleado.equals(tarea.getDepartamento())) {
                contador++;
            }
        }
        
        // Buscar en lista de tareas departamentales
        for (Tarea tarea : listaTareasDepartamento) {
            if (departamentoEmpleado.equals(tarea.getDepartamento())) {
                contador++;
            }
        }
        
        // Buscar en cola de prioridad
        for (TareaPrioridad tareaPrioridad : colaPrioridad) {
            if (departamentoEmpleado.equals(tareaPrioridad.getDepartamento())) {
                contador++;
            }
        }
        
        return contador;
    }

    // ===============================================
    // METODOS PARA INTERFACES ESPECIFICAS POR ROL
    // ===============================================
    
    /**
     * Panel de administración para el jefe general
     */
    private JPanel crearPanelAdministracion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Panel de Administración - Jefe General", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
        
        // Panel de estadísticas
        JPanel panelEstadisticas = new JPanel(new GridLayout(3, 3, 10, 10));
        panelEstadisticas.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Botones de administración
        JButton btnReportes = crearBotonModerno("Generar Reportes", new Color(34, 139, 34));
        JButton btnEstadisticas = crearBotonModerno("Ver Estadísticas", new Color(255, 140, 0));
        JButton btnFiltros = crearBotonModerno("Filtros Avanzados", new Color(70, 130, 180));
        JButton btnExportar = crearBotonModerno("💾 Exportar Datos", new Color(128, 0, 128));
        JButton btnConfiguracion = crearBotonModerno("⚙️ Configuración", new Color(220, 20, 60));
        JButton btnRespaldos = crearBotonModerno("💿 Respaldos", new Color(105, 105, 105));
        JButton btnGestionUsuarios = crearBotonModerno("👥 Gestión de Usuarios", new Color(72, 61, 139));
        JButton btnCrearCuentas = crearBotonModerno("🆔 Crear Cuentas", new Color(255, 99, 71));
        JButton btnResetearSistema = crearBotonModerno("🔄 Reiniciar Sistema", new Color(178, 34, 34));
        
        panelEstadisticas.add(btnReportes);
        panelEstadisticas.add(btnEstadisticas);
        panelEstadisticas.add(btnFiltros);
        panelEstadisticas.add(btnExportar);
        panelEstadisticas.add(btnConfiguracion);
        panelEstadisticas.add(btnRespaldos);
        panelEstadisticas.add(btnGestionUsuarios);
        panelEstadisticas.add(btnCrearCuentas);
        panelEstadisticas.add(btnResetearSistema);
        
        // Eventos
        btnEstadisticas.addActionListener(e -> mostrarEstadisticasGenerales());
        btnFiltros.addActionListener(e -> abrirFiltrosAvanzados());
        btnReportes.addActionListener(e -> generarReporteCompleto());
        btnGestionUsuarios.addActionListener(e -> abrirGestionUsuarios());
        btnCrearCuentas.addActionListener(e -> crearCuentasParaEmpleados());
        btnResetearSistema.addActionListener(e -> confirmarReseteoSistema());
        
        panel.add(panelEstadisticas, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Crea el panel ejecutivo especial para CEO con controles avanzados
     */
    private JPanel crearPanelEjecutivo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Panel Ejecutivo - CEO", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(139, 0, 0)));
        
        // Panel de controles ejecutivos
        JPanel panelControles = new JPanel(new GridLayout(4, 3, 10, 10));
        panelControles.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Botones de control ejecutivo
        JButton btnDashboard = crearBotonModerno("Dashboard Ejecutivo", new Color(139, 0, 0));
        JButton btnAnalisis = crearBotonModerno("Analisis Avanzado", new Color(25, 25, 112));
        JButton btnRendimiento = crearBotonModerno("Rendimiento Global", new Color(0, 100, 0));
        JButton btnFinanzas = crearBotonModerno("Control Financiero", new Color(184, 134, 11));
        JButton btnRecursosHumanos = crearBotonModerno("Recursos Humanos", new Color(72, 61, 139));
        JButton btnEstrategia = crearBotonModerno("Planificacion Estrategica", new Color(128, 0, 128));
        JButton btnAuditoria = crearBotonModerno("Auditoria de Sistema", new Color(220, 20, 60));
        JButton btnSeguridad = crearBotonModerno("Seguridad y Accesos", new Color(105, 105, 105));
        JButton btnReportesEjecutivos = crearBotonModerno("Reportes Ejecutivos", new Color(139, 69, 19));
        JButton btnConfigGlobal = crearBotonModerno("Configuracion Global", new Color(47, 79, 79));
        JButton btnBackupCompleto = crearBotonModerno("Backup Completo", new Color(85, 107, 47));
        JButton btnOverride = crearBotonModerno("Override del Sistema", new Color(178, 34, 34));
        
        panelControles.add(btnDashboard);
        panelControles.add(btnAnalisis);
        panelControles.add(btnRendimiento);
        panelControles.add(btnFinanzas);
        panelControles.add(btnRecursosHumanos);
        panelControles.add(btnEstrategia);
        panelControles.add(btnAuditoria);
        panelControles.add(btnSeguridad);
        panelControles.add(btnReportesEjecutivos);
        panelControles.add(btnConfigGlobal);
        panelControles.add(btnBackupCompleto);
        panelControles.add(btnOverride);
        
        // Eventos ejecutivos
        btnDashboard.addActionListener(e -> JOptionPane.showMessageDialog(this, "Dashboard Ejecutivo - Funcionalidad en desarrollo", "CEO", JOptionPane.INFORMATION_MESSAGE));
        btnAnalisis.addActionListener(e -> JOptionPane.showMessageDialog(this, "Analisis Avanzado - Funcionalidad en desarrollo", "CEO", JOptionPane.INFORMATION_MESSAGE));
        btnRendimiento.addActionListener(e -> JOptionPane.showMessageDialog(this, "Rendimiento Global - Funcionalidad en desarrollo", "CEO", JOptionPane.INFORMATION_MESSAGE));
        btnAuditoria.addActionListener(e -> JOptionPane.showMessageDialog(this, "Auditoria Completa - Funcionalidad en desarrollo", "CEO", JOptionPane.INFORMATION_MESSAGE));
        btnOverride.addActionListener(e -> JOptionPane.showMessageDialog(this, "Override del Sistema - Funcionalidad restringida", "CEO", JOptionPane.WARNING_MESSAGE));
        
        panel.add(panelControles, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Abre la ventana de gestión de usuarios (solo para jefe o CEO)
     */
    private void abrirGestionUsuarios() {
        if (!usuarioActual.esJefe() && !usuarioActual.esCEO()) {
            JOptionPane.showMessageDialog(this, 
                "Acceso denegado. Solo el jefe general o CEO pueden gestionar usuarios.",
                "Sin permisos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            GestionUsuariosPanel gestionPanel = new GestionUsuariosPanel();
            gestionPanel.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir la gestión de usuarios: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Crea cuentas automáticamente para todos los empleados que no tienen cuenta
     */
    private void crearCuentasParaEmpleados() {
        if (!usuarioActual.esJefe() && !usuarioActual.esCEO()) {
            JOptionPane.showMessageDialog(this, 
                "Acceso denegado. Solo el jefe general o CEO pueden crear cuentas.",
                "Sin permisos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        UsuarioService usuarioService = UsuarioService.getInstance();
        List<Empleado> todosLosEmpleados = new ArrayList<>();
        arbolEmpleados.buscarPorDepartamento(null, todosLosEmpleados); // null = obtener todos
        
        int cuentasCreadas = 0;
        StringBuilder reporte = new StringBuilder();
        reporte.append("🆔 REPORTE DE CREACIÓN DE CUENTAS\n");
        reporte.append("=".repeat(50)).append("\n\n");
        
        for (Empleado empleado : todosLosEmpleados) {
            // Generar contraseña temporal basada en el ID del empleado
            String passwordTemporal = "temp" + empleado.getId().toLowerCase();
            
            if (usuarioService.crearCuentaEmpleado(empleado, passwordTemporal)) {
                cuentasCreadas++;
                reporte.append(String.format("%s (%s) - Usuario: %s, Password: %s\n", 
                    empleado.getNombre(), empleado.getId(), 
                    generarUsernameEmpleado(empleado), passwordTemporal));
            } else {
                reporte.append(String.format("⚠️ %s (%s) - Ya tiene cuenta o error\n", 
                    empleado.getNombre(), empleado.getId()));
            }
        }
        
        reporte.append("\n").append("=".repeat(50)).append("\n");
        reporte.append(String.format("RESUMEN: %d nuevas cuentas creadas de %d empleados", 
            cuentasCreadas, todosLosEmpleados.size()));
        
        // Mostrar reporte en ventana
        JTextArea areaReporte = new JTextArea(reporte.toString());
        areaReporte.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaReporte.setEditable(false);
        
        JScrollPane scroll = new JScrollPane(areaReporte);
        scroll.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scroll, 
            "Reporte de Creación de Cuentas", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Genera un username para un empleado basado en su nombre
     */
    private String generarUsernameEmpleado(Empleado empleado) {
        String nombre = empleado.getNombre().toLowerCase().replaceAll("\\s+", ".");
        return nombre.replaceAll("[^a-z.]", "");
    }
    
    /**
     * Confirma y ejecuta el reseteo del sistema
     */
    private void confirmarReseteoSistema() {
        if (!usuarioActual.esJefe()) {
            JOptionPane.showMessageDialog(this, 
                "Acceso denegado. Solo el jefe general puede resetear el sistema.",
                "Sin permisos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] opciones = {"Resetear Todo", "Solo Tablas", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(this,
            "⚠️ ADVERTENCIA: Esta acción eliminará datos del sistema.\n\n" +
            "Seleccione el tipo de reseteo:\n" +
            "• Resetear Todo: Limpia todas las tablas y reinicia el sistema\n" +
            "• Solo Tablas: Limpia solo las tablas de tareas\n" +
            "• Cancelar: No hacer nada",
            "Confirmar Reseteo del Sistema",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            opciones,
            opciones[2]);
        
        switch (opcion) {
            case 0: // Resetear Todo
                resetearSistemaCompleto();
                break;
            case 1: // Solo Tablas
                resetearSoloTablas();
                break;
            default: // Cancelar
                JOptionPane.showMessageDialog(this, "Operación cancelada", 
                    "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }
    
    /**
     * Resetea completamente el sistema
     */
    private void resetearSistemaCompleto() {
        String confirmacion = JOptionPane.showInputDialog(this,
            "⚠️ CONFIRMACIÓN FINAL\n\n" +
            "Para confirmar el reseteo completo del sistema,\n" +
            "escriba exactamente: RESETEAR\n\n" +
            "Esto eliminará:\n" +
            "• Todas las tareas\n" +
            "• Todas las estructuras de datos\n" +
            "• Configuraciones (mantendrá usuarios)\n",
            "Confirmación de Reseteo");
        
        if ("RESETEAR".equals(confirmacion)) {
            // Limpiar modelos de tablas
            if (modelPila != null) modelPila.setRowCount(0);
            if (modelCola != null) modelCola.setRowCount(0);
            if (modelLista != null) modelLista.setRowCount(0);
            if (modelGeneral != null) modelGeneral.setRowCount(0);
            if (modelPrioridad != null) modelPrioridad.setRowCount(0);
            
            JOptionPane.showMessageDialog(this,
                "Sistema reseteado completamente.\n" +
                "Todas las estructuras de datos han sido limpiadas.",
                "Reseteo Completado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Confirmación incorrecta. Operación cancelada.",
                "Cancelado", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Resetea solo las tablas de tareas
     */
    private void resetearSoloTablas() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de limpiar solo las tablas de tareas?\n" +
            "Esta acción eliminará todas las tareas pero mantendrá empleados y usuarios.",
            "Confirmar Limpieza de Tablas",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Limpiar solo modelos de tablas de tareas
            if (modelPila != null) modelPila.setRowCount(0);
            if (modelCola != null) modelCola.setRowCount(0);
            if (modelLista != null) modelLista.setRowCount(0);
            if (modelGeneral != null) modelGeneral.setRowCount(0);
            if (modelPrioridad != null) modelPrioridad.setRowCount(0);
            
            JOptionPane.showMessageDialog(this,
                "Tablas de tareas limpiadas exitosamente.\n" +
                "Los empleados y usuarios se mantuvieron intactos.",
                "Limpieza Completada", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Panel para gestión de departamento (jefe de departamento)
     */
    private JPanel crearPanelGestionDepartamento() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Gestión de Departamento - " + usuarioActual.getDepartamento(), 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
        
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JButton btnCrearTarea = crearBotonModerno("➕ Crear Nueva Tarea", new Color(34, 139, 34));
        JButton btnAsignarTarea = crearBotonModerno("Asignar Tarea", new Color(70, 130, 180));
        JButton btnRevisarCompletadas = crearBotonModerno("Revisar Completadas", new Color(255, 140, 0));
        JButton btnEstadisticasDpto = crearBotonModerno("📊 Estadísticas Depto", new Color(128, 0, 128));
        
        panelBotones.add(btnCrearTarea);
        panelBotones.add(btnAsignarTarea);
        panelBotones.add(btnRevisarCompletadas);
        panelBotones.add(btnEstadisticasDpto);
        
        // Eventos
        btnCrearTarea.addActionListener(e -> crearNuevaTareaDepartamento());
        btnAsignarTarea.addActionListener(e -> asignarTareaAEmpleado());
        btnRevisarCompletadas.addActionListener(e -> revisarTareasCompletadas());
        
        panel.add(panelBotones, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Panel de tareas para empleado
     */
    private JPanel crearPanelMisTareas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Mis Tareas Asignadas", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
        
        // Tabla para mostrar solo las tareas del empleado
        String[] columnas = {"ID", "Descripción", "Urgencia", "Horas Est.", "Estado", "Tiempo Restante"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        personalizarTabla(tabla);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel de botones para empleado
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnActualizarTareas = crearBotonModerno("Actualizar Tareas", new Color(70, 130, 180));
        
        btnActualizarTareas.addActionListener(e -> {
            modelo.setRowCount(0);
            cargarTareasEmpleado(modelo);
            JOptionPane.showMessageDialog(this, "Lista de tareas actualizada", "Actualización", JOptionPane.INFORMATION_MESSAGE);
        });
        
        panelBotones.add(btnActualizarTareas);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        // Cargar tareas del empleado
        cargarTareasEmpleado(modelo);
        
        return panel;
    }
    
    /**
     * Panel para completar tareas (empleado)
     */
    private JPanel crearPanelCompletarTareas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Completar Tareas", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
        
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Campo ID de tarea
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("ID de Tarea:"), gbc);
        
        JTextField txtIdTarea = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        panelFormulario.add(txtIdTarea, gbc);
        
        // Comentarios
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Comentarios:"), gbc);
        
        JTextArea txtComentarios = new JTextArea(4, 15);
        txtComentarios.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollComentarios = new JScrollPane(txtComentarios);
        gbc.gridx = 1; gbc.gridy = 1;
        panelFormulario.add(scrollComentarios, gbc);
        
        // Botón completar
        JButton btnCompletar = crearBotonModerno("Marcar como Completada", new Color(34, 139, 34));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panelFormulario.add(btnCompletar, gbc);
        
        // Botón actualizar lista
        JButton btnActualizarCompletadas = crearBotonModerno("Actualizar Lista", new Color(70, 130, 180));
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1;
        panelFormulario.add(btnActualizarCompletadas, gbc);
        
        btnCompletar.addActionListener(e -> completarTarea(txtIdTarea.getText(), txtComentarios.getText()));
        btnActualizarCompletadas.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Lista de tareas actualizada", "Actualización", JOptionPane.INFORMATION_MESSAGE));
        
        panel.add(panelFormulario, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Versiones específicas de paneles para diferentes roles
     */
    private JPanel crearPanelListaDepartamento() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Tareas del Departamento: " + usuarioActual.getDepartamento(), 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));

        // Modelo de tabla con columna adicional para empleado asignado
        String[] columnas = {"ID", "Descripción", "Urgencia", "Horas Est.", "Empleado Asignado"};
        DefaultTableModel modelListaDepartamento = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tablaListaDepartamento = new JTable(modelListaDepartamento);
        personalizarTabla(tablaListaDepartamento);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(240, 248, 255));
        
        JButton btnActualizar = crearBotonModerno("🔄 Actualizar", new Color(70, 130, 180));
        JButton btnCrearTarea = crearBotonModerno("➕ Nueva Tarea", new Color(34, 139, 34));
        JButton btnAsignarTarea = crearBotonModerno("👥 Asignar Tarea", new Color(255, 140, 0));
        
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCrearTarea);
        panelBotones.add(btnAsignarTarea);
        
        // Eventos
        btnActualizar.addActionListener(e -> cargarTareasDepartamentoConAsignacion(modelListaDepartamento));
        btnCrearTarea.addActionListener(e -> crearNuevaTareaDepartamento());
        btnAsignarTarea.addActionListener(e -> asignarTareaAEmpleado());
        
        // Cargar tareas del departamento
        cargarTareasDepartamentoConAsignacion(modelListaDepartamento);
        
        JScrollPane scrollPane = new JScrollPane(tablaListaDepartamento);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }
    
    /**
     * Carga tareas del departamento mostrando el empleado asignado
     */
    private void cargarTareasDepartamentoConAsignacion(DefaultTableModel modelo) {
        modelo.setRowCount(0); // Limpiar tabla
        for (Tarea tarea : listaTareasDepartamento) {
            if (tarea.getDepartamento().equals(usuarioActual.getDepartamento())) {
                String empleadoAsignado = tarea.getEmpleadoAsignado();
                String nombreEmpleado = "Sin asignar";
                
                // Buscar el nombre del empleado asignado
                if (empleadoAsignado != null && !empleadoAsignado.isEmpty()) {
                    java.util.List<Empleado> empleados = new java.util.ArrayList<>();
                    arbolEmpleados.buscarPorDepartamento("", empleados); // Obtener todos
                    
                    for (Empleado emp : empleados) {
                        if (emp.getId().equals(empleadoAsignado)) {
                            nombreEmpleado = emp.getNombre();
                            break;
                        }
                    }
                }
                
                modelo.addRow(new Object[]{
                    tarea.getId(),
                    tarea.getDescripcion(),
                    tarea.getUrgencia(),
                    tarea.getHorasEstimadas() + " hrs",
                    nombreEmpleado
                });
            }
        }
    }
    
    /**
     * Método genérico para cargar tareas del departamento
     */
    private void cargarTareasDepartamento(DefaultTableModel modelo) {
        cargarTareasDepartamentoConAsignacion(modelo);
    }
    
    private JPanel crearPanelPrioridadDepartamento() {
        // Similar al panel prioridad pero filtrado por departamento
        return crearPanelPrioridad(); // Por ahora reutilizamos
    }
    
    private JPanel crearPanelEmpleadosDepartamento() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(null, "Empleados del Departamento: " + usuarioActual.getDepartamento(), 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
        
        // Modelo de tabla para empleados del departamento
        String[] columnas = {"ID", "Nombre", "Departamento", "Estado"};
        DefaultTableModel modelEmpleadosDepartamento = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        
        // Cargar solo empleados del departamento del jefe
        cargarEmpleadosPorDepartamento(modelEmpleadosDepartamento);
        
        TableWithFilters tablaEmpleadosDepartamento = new TableWithFilters(modelEmpleadosDepartamento);
        personalizarTabla(tablaEmpleadosDepartamento);
        
        JScrollPane scrollEmpleados = new JScrollPane(tablaEmpleadosDepartamento);
        scrollEmpleados.setPreferredSize(new Dimension(800, 400));
        
        // Panel de botones para jefe de departamento
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnAsignarTarea = crearBotonModerno("Asignar Tarea", new Color(34, 139, 34));
        JButton btnVerRendimiento = crearBotonModerno("Ver Rendimiento", new Color(255, 140, 0));
        JButton btnActualizar = crearBotonModerno("Actualizar", new Color(70, 130, 180));
        
        panelBotones.add(btnAsignarTarea);
        panelBotones.add(btnVerRendimiento);
        panelBotones.add(btnActualizar);
        
        // Eventos
        btnAsignarTarea.addActionListener(e -> asignarTareaAEmpleado());
        btnActualizar.addActionListener(e -> {
            modelEmpleadosDepartamento.setRowCount(0);
            cargarEmpleadosPorDepartamento(modelEmpleadosDepartamento);
        });
        
        panel.add(scrollEmpleados, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Carga empleados filtrados por departamento del usuario actual
     */
    private void cargarEmpleadosPorDepartamento(DefaultTableModel modelo) {
        String departamentoActual = usuarioActual.getDepartamento();
        
        // Crear lista para almacenar empleados del departamento
        List<Empleado> empleadosDepartamento = new ArrayList<>();
        
        // Debug: Imprimir información de búsqueda
        System.out.println("🔍 DEBUG: Buscando empleados para departamento: " + departamentoActual);
        
        // Buscar empleados del departamento actual
        arbolEmpleados.buscarPorDepartamento(departamentoActual, empleadosDepartamento);
        
        // Debug: Mostrar cuántos empleados se encontraron
        System.out.println("👥 DEBUG: Empleados encontrados: " + empleadosDepartamento.size());
        
        // Agregar empleados a la tabla
        for (Empleado empleado : empleadosDepartamento) {
            System.out.println("   - " + empleado.getId() + ": " + empleado.getNombre() + " (" + empleado.getDepartamento() + ")");
            modelo.addRow(new Object[]{
                empleado.getId(),
                empleado.getNombre(),
                empleado.getDepartamento(),
                "Activo"
            });
        }
        
        // Si no se encontraron empleados, mostrar mensaje informativo
        if (empleadosDepartamento.isEmpty()) {
            modelo.addRow(new Object[]{
                "-", 
                "No hay empleados en este departamento", 
                departamentoActual, 
                "Sin datos"
            });
            System.out.println("⚠️ DEBUG: No se encontraron empleados para el departamento: " + departamentoActual);
        }
    }
    
    // ===============================================
    // MÉTODOS DE LÓGICA DE NEGOCIO POR ROL
    // ===============================================
    
    /**
     * Carga las tareas específicas del empleado autenticado
     */
    private void cargarTareasEmpleado(DefaultTableModel modelo) {
        // Debug: Mostrar información del empleado actual
        System.out.println("🔍 DEBUG: Cargando tareas para empleado: " + usuarioActual.getId() + " (" + usuarioActual.getNombre() + ")");
        System.out.println("   Departamento: " + usuarioActual.getDepartamento());
        
        int tareasEncontradas = 0;
        
        // Filtrar solo las tareas asignadas al empleado actual
        for (Tarea tarea : listaTareasDepartamento) {
            // Verificar si la tarea está asignada al empleado actual
            if (tarea.getEmpleadoAsignado() != null && 
                tarea.getEmpleadoAsignado().equals(usuarioActual.getId()) &&
                tarea.getDepartamento().equals(usuarioActual.getDepartamento())) {
                
                System.out.println("   ✅ Tarea asignada encontrada: " + tarea.getId() + " - " + tarea.getDescripcion());
                tareasEncontradas++;
                
                int tiempoRestante = calcularTiempoRestante(tarea);
                modelo.addRow(new Object[]{
                    tarea.getId(),
                    tarea.getDescripcion(),
                    tarea.getUrgencia(),
                    tarea.getHorasEstimadas() + " hrs",
                    "Asignada",
                    tiempoRestante + " hrs"
                });
            }
        }
        
        System.out.println("📊 DEBUG: Total de tareas asignadas encontradas: " + tareasEncontradas);
        
        // Si no hay tareas asignadas, agregar una fila informativa
        if (tareasEncontradas == 0) {
            modelo.addRow(new Object[]{
                "-", 
                "No hay tareas asignadas actualmente", 
                "-", 
                "-", 
                "Sin asignar", 
                "-"
            });
            System.out.println("ℹ️ DEBUG: No se encontraron tareas asignadas al empleado");
        }
    }
    
    /**
     * Completa una tarea (empleado)
     */
    private void completarTarea(String idTarea, String comentarios) {
        if (idTarea.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID de la tarea", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Marcar tarea como pendiente de revisión
        JOptionPane.showMessageDialog(this, 
            "Tarea " + idTarea + " marcada como completada.\nPendiente de revisión por jefe de departamento.",
            "Tarea Completada", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Crea nueva tarea para el departamento (jefe de departamento)
     */
    private void crearNuevaTareaDepartamento() {
        String descripcion = JOptionPane.showInputDialog(this, "Descripción de la nueva tarea:");
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            String urgencia = (String) JOptionPane.showInputDialog(this, "Nivel de urgencia:",
                "Urgencia", JOptionPane.QUESTION_MESSAGE, null, 
                new String[]{"Alta", "Media", "Baja"}, "Media");
            
            if (urgencia != null) {
                // Generar ID automático
                String id = "T" + (hashTareas.size() + 1);
                
                // Crear la tarea
                Tarea nuevaTarea = new Tarea(id, descripcion, usuarioActual.getDepartamento(), urgencia);
                
                // Agregar a las estructuras
                listaTareasDepartamento.add(nuevaTarea);
                hashTareas.put(id, nuevaTarea);
                
                // Guardar en MongoDB
                guardarTareaEnMongoDB(nuevaTarea, "departamento");
                
                // Actualizar todas las interfaces
                actualizarTablasSegunRol();
                
                JOptionPane.showMessageDialog(this, "Tarea creada exitosamente: " + id,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Revisa tareas completadas por empleados (jefe de departamento)
     */
    private void revisarTareasCompletadas() {
        JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo.\nAquí se mostrarán las tareas completadas pendientes de revisión.",
            "Revisar Tareas", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Asigna una tarea existente a un empleado del departamento
     */
    private void asignarTareaAEmpleado() {
        // Paso 1: Obtener todas las tareas del departamento sin asignar
        java.util.List<Tarea> tareasDisponibles = new java.util.ArrayList<>();
        
        for (Tarea tarea : listaTareasDepartamento) {
            if (tarea.getDepartamento().equals(usuarioActual.getDepartamento()) && 
                (tarea.getEmpleadoAsignado() == null || tarea.getEmpleadoAsignado().isEmpty())) {
                tareasDisponibles.add(tarea);
            }
        }
        
        if (tareasDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay tareas disponibles para asignar en el departamento " + usuarioActual.getDepartamento(),
                "Sin Tareas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Paso 2: Seleccionar tarea
        String[] opcionesTareas = tareasDisponibles.stream()
            .map(t -> t.getId() + " - " + t.getDescripcion())
            .toArray(String[]::new);
            
        String tareaSeleccionada = (String) JOptionPane.showInputDialog(this,
            "Seleccione la tarea a asignar:",
            "Asignar Tarea - Seleccionar Tarea",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcionesTareas,
            opcionesTareas[0]);
            
        if (tareaSeleccionada == null) return;
        
        // Obtener el ID de la tarea seleccionada
        String idTareaSeleccionada = tareaSeleccionada.split(" - ")[0];
        
        // Paso 3: Obtener empleados del departamento usando buscarPorDepartamento
        java.util.List<Empleado> empleadosDepartamento = new java.util.ArrayList<>();
        arbolEmpleados.buscarPorDepartamento(usuarioActual.getDepartamento(), empleadosDepartamento);
        
        if (empleadosDepartamento.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay empleados disponibles en el departamento " + usuarioActual.getDepartamento(),
                "Sin Empleados", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Paso 4: Seleccionar empleado (todos los empleados del ArbolEmpleados son empleados regulares)
        String[] opcionesEmpleados = empleadosDepartamento.stream()
            .map(emp -> emp.getId() + " - " + emp.getNombre())
            .toArray(String[]::new);
            
        String empleadoSeleccionado = (String) JOptionPane.showInputDialog(this,
            "Seleccione el empleado para asignar la tarea:",
            "Asignar Tarea - Seleccionar Empleado",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcionesEmpleados,
            opcionesEmpleados[0]);
            
        if (empleadoSeleccionado == null) return;
        
        // Obtener el ID del empleado seleccionado
        String idEmpleadoSeleccionado = empleadoSeleccionado.split(" - ")[0];
        
        // Paso 5: Realizar la asignación
        Tarea tareaAsignar = hashTareas.get(idTareaSeleccionada);
        if (tareaAsignar != null) {
            tareaAsignar.setEmpleadoAsignado(idEmpleadoSeleccionado);
            
            // Guardar cambios en MongoDB
            actualizarTareaEnMongoDB(tareaAsignar);
            
            JOptionPane.showMessageDialog(this,
                "Tarea asignada exitosamente:\n\n" +
                "Tarea: " + tareaAsignar.getDescripcion() + "\n" +
                "Empleado: " + empleadoSeleccionado + "\n" +
                "Horas estimadas: " + tareaAsignar.getHorasEstimadas(),
                "Asignación Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Actualizar todas las interfaces automáticamente
            actualizarTablasSegunRol();
            
            // Actualizar específicamente al empleado asignado
            actualizarTablaEmpleado(idEmpleadoSeleccionado);
        }
    }
    
    /**
     * Actualiza las tablas de un empleado específico cuando se le asigna una tarea
     */
    private void actualizarTablaEmpleado(String idEmpleado) {
        // Si el usuario actual es el empleado que recibió la asignación
        if (usuarioActual != null && idEmpleado.equals(usuarioActual.getId())) {
            // Actualizar la tabla según el rol
            if (usuarioActual.getRol() == Usuario.Rol.EMPLEADO) {
                // Recargar las tareas para el empleado
                cargarTareasEmpleado((DefaultTableModel) tablaLista.getModel());
            }
        }
    }
    
    /**
     * Muestra estadísticas generales del sistema (jefe)
     */
    private void mostrarEstadisticasGenerales() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== ESTADÍSTICAS GENERALES ===\n\n");
        stats.append("📚 Tareas Urgentes: ").append(pilaTareasUrgentes.size()).append("\n");
        stats.append("⏰ Tareas Programadas: ").append(colaTareasProgramadas.size()).append("\n");
        stats.append("🏢 Tareas Departamentales: ").append(listaTareasDepartamento.size()).append("\n");
        stats.append("⭐ Tareas con Prioridad: ").append(colaPrioridad.size()).append("\n");
        stats.append("👥 Total Empleados: ").append(arbolEmpleados.contarEmpleados()).append("\n\n");
        
        // Tiempo total estimado
        int tiempoTotal = calcularTiempoTotalRecursivo(listaTareasDepartamento, 0) +
                         calcularTiempoTotalRecursivo(pilaTareasUrgentes, 0) +
                         calcularTiempoTotalRecursivo(colaTareasProgramadas, 0);
        stats.append("⏱️ Tiempo Total Estimado: ").append(tiempoTotal).append(" horas\n");
        
        JOptionPane.showMessageDialog(this, stats.toString(), "Estadísticas del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Abre filtros avanzados (jefe)
     */
    private void abrirFiltrosAvanzados() {
        JOptionPane.showMessageDialog(this, "Funcionalidad de filtros avanzados en desarrollo.",
            "Filtros Avanzados", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ==================== MÉTODOS AUXILIARES PARA MENÚS DEPARTAMENTALES ====================
    
    /**
     * Crea panel especializado según el departamento del jefe
     */
    private JPanel crearPanelEspecializadoPorDepartamento() {
        if (usuarioActual.getDepartamento() == null) return null;
        
        String dept = usuarioActual.getDepartamento().toLowerCase();
        
        if (dept.contains("desarrollo") || dept.contains("it") || dept.contains("tecnologia")) {
            return crearPanelIT();
        } else if (dept.contains("ventas") || dept.contains("comercial")) {
            return crearPanelVentas();
        } else if (dept.contains("marketing") || dept.contains("publicidad")) {
            return crearPanelMarketing();
        } else if (dept.contains("recursos") || dept.contains("rrhh")) {
            return crearPanelRRHH();
        }
        
        return null;
    }
    
    /**
     * Obtiene el nombre del tab especializado
     */
    private String obtenerNombreTabEspecializado() {
        if (usuarioActual.getDepartamento() == null) return "Especializado";
        
        String dept = usuarioActual.getDepartamento().toLowerCase();
        
        if (dept.contains("desarrollo") || dept.contains("it") || dept.contains("tecnologia")) {
            return "Gestión IT";
        } else if (dept.contains("ventas") || dept.contains("comercial")) {
            return "Gestión Ventas";
        } else if (dept.contains("marketing") || dept.contains("publicidad")) {
            return "Campañas";
        } else if (dept.contains("recursos") || dept.contains("rrhh")) {
            return "Gestión RRHH";
        }
        
        return "Especializado";
    }
    
    /**
     * Crea panel departamental para empleados
     */
    private JPanel crearPanelDepartamentalEmpleado() {
        if (usuarioActual.getDepartamento() == null) return null;
        
        String dept = usuarioActual.getDepartamento().toLowerCase();
        
        if (dept.contains("desarrollo") || dept.contains("it") || dept.contains("tecnologia")) {
            return crearPanelEmpleadoIT();
        } else if (dept.contains("ventas") || dept.contains("comercial")) {
            return crearPanelEmpleadoVentas();
        } else if (dept.contains("marketing") || dept.contains("publicidad")) {
            return crearPanelEmpleadoMarketing();
        }
        
        return crearPanelEmpleadoGenerico();
    }
    
    /**
     * Obtiene nombre del tab departamental para empleados
     */
    private String obtenerNombreTabDepartamentalEmpleado() {
        if (usuarioActual.getDepartamento() == null) return "Mi Departamento";
        
        String dept = usuarioActual.getDepartamento().toLowerCase();
        
        if (dept.contains("desarrollo") || dept.contains("it") || dept.contains("tecnologia")) {
            return "Tareas IT";
        } else if (dept.contains("ventas") || dept.contains("comercial")) {
            return "Mis Clientes";
        } else if (dept.contains("marketing") || dept.contains("publicidad")) {
            return "Mis Campañas";
        }
        
        return "Mi Departamento";
    }
    
    // ==================== PANELES ESPECIALIZADOS ====================
    
    /**
     * Panel especializado para departamento IT
     */
    private JPanel crearPanelIT() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Gestión IT - Proyectos y Mantenimiento"));
        
        JTextArea areaInfo = new JTextArea(
            "HERRAMIENTAS IT:\n\n" +
            "• Gestión de proyectos de desarrollo\n" +
            "• Mantenimiento de sistemas\n" +
            "• Soporte técnico\n" +
            "• Infraestructura y redes\n" +
            "• Seguridad informática\n\n" +
            "📊 Métricas IT:\n" +
            "• Tiempo de respuesta\n" +
            "• Uptime de sistemas\n" +
            "• Resolución de tickets"
        );
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Panel especializado para departamento de Ventas
     */
    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Gestión de Ventas - CRM y Seguimiento"));
        
        JTextArea areaInfo = new JTextArea(
            "HERRAMIENTAS VENTAS:\n\n" +
            "• Seguimiento de leads\n" +
            "• Gestión de clientes\n" +
            "• Reportes de ventas\n" +
            "• Metas y objetivos\n" +
            "• Pipeline de ventas\n\n" +
            "📊 KPIs Ventas:\n" +
            "• Conversión de leads\n" +
            "• Ticket promedio\n" +
            "• Retención de clientes"
        );
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Panel especializado para departamento de Marketing
     */
    private JPanel crearPanelMarketing() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Gestión Marketing - Campañas y Contenido"));
        
        JTextArea areaInfo = new JTextArea(
            "HERRAMIENTAS MARKETING:\n\n" +
            "• Campañas publicitarias\n" +
            "• Gestión de contenido\n" +
            "• Redes sociales\n" +
            "• Email marketing\n" +
            "• Análisis de mercado\n\n" +
            "📊 Métricas Marketing:\n" +
            "• ROI de campañas\n" +
            "• Engagement rate\n" +
            "• Lead generation"
        );
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Panel especializado para RRHH
     */
    private JPanel crearPanelRRHH() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Gestión RRHH - Personal y Desarrollo"));
        
        JTextArea areaInfo = new JTextArea(
            "HERRAMIENTAS RRHH:\n\n" +
            "• Gestión de personal\n" +
            "• Evaluaciones de desempeño\n" +
            "• Capacitaciones\n" +
            "• Reclutamiento\n" +
            "• Nómina y beneficios\n\n" +
            "📊 Métricas RRHH:\n" +
            "• Rotación de personal\n" +
            "• Satisfacción laboral\n" +
            "• Productividad por empleado"
        );
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }
    
    // ==================== PANELES PARA EMPLEADOS ====================
    
    /**
     * Panel de tareas pendientes para empleados
     */
    private JPanel crearPanelTareasPendientes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Mis Tareas Pendientes"));
        
        JTextArea areaInfo = new JTextArea(
            "TAREAS PENDIENTES:\n\n" +
            "• Lista de tareas asignadas\n" +
            "• Fechas límite\n" +
            "• Prioridades\n" +
            "• Estado de progreso\n\n" +
            "Recordatorio:\n" +
            "Mantén tus tareas actualizadas\n" +
            "para un mejor seguimiento."
        );
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Panel de botones para actualizar
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnActualizarPendientes = crearBotonModerno("Actualizar Estado", new Color(70, 130, 180));
        
        btnActualizarPendientes.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Estado de tareas actualizado", "Actualización", JOptionPane.INFORMATION_MESSAGE));
        
        panelBotones.add(btnActualizarPendientes);
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }
    
    /**
     * Panel específico para empleados IT
     */
    private JPanel crearPanelEmpleadoIT() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Mis Tareas IT"));
        
        JTextArea areaInfo = new JTextArea(
            "MIS TAREAS IT:\n\n" +
            "• Tickets de soporte\n" +
            "• Desarrollo de funcionalidades\n" +
            "• Mantenimiento de sistemas\n" +
            "• Documentación técnica\n" +
            "• Testing y QA\n\n" +
            "📚 Recursos:\n" +
            "• Base de conocimiento\n" +
            "• Documentación API\n" +
            "• Guías de procedimientos"
        );
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Panel específico para empleados de Ventas
     */
    private JPanel crearPanelEmpleadoVentas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Mis Clientes y Ventas"));
        
        JTextArea areaInfo = new JTextArea(
            "MIS ACTIVIDADES VENTAS:\n\n" +
            "• Llamadas programadas\n" +
            "• Seguimiento de leads\n" +
            "• Reuniones con clientes\n" +
            "• Propuestas pendientes\n" +
            "• Metas del mes\n\n" +
            "📊 Mi Performance:\n" +
            "• Ventas del mes\n" +
            "• Clientes contactados\n" +
            "• Conversiones logradas"
        );
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Panel específico para empleados de Marketing
     */
    private JPanel crearPanelEmpleadoMarketing() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Mis Campañas"));
        
        JTextArea areaInfo = new JTextArea(
            "MIS ACTIVIDADES MARKETING:\n\n" +
            "• Creación de contenido\n" +
            "• Gestión de redes sociales\n" +
            "• Campañas activas\n" +
            "• Análisis de métricas\n" +
            "• Diseño gráfico\n\n" +
            "Mis Métricas:\n" +
            "• Engagement generado\n" +
            "• Contenido publicado\n" +
            "• Leads generados"
        );
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Panel genérico para empleados
     */
    private JPanel crearPanelEmpleadoGenerico() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("📋 Mi Área de Trabajo"));
        
        JTextArea areaInfo = new JTextArea(
            "💼 MI DEPARTAMENTO:\n\n" +
            "• Tareas asignadas\n" +
            "• Proyectos activos\n" +
            "• Colaboraciones\n" +
            "• Reportes pendientes\n" +
            "• Comunicaciones internas\n\n" +
            "📞 Contacto:\n" +
            "• Jefe inmediato\n" +
            "• Compañeros de equipo\n" +
            "• Soporte interno"
        );
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }
    
    // ==================== MENSAJES DE BIENVENIDA ====================
    
    /**
     * Muestra mensaje de bienvenida para jefes de departamento
     */
    private void mostrarMensajeBienvenidaJefe() {
        Timer timer = new Timer(2000, e -> {
            String mensaje = String.format(
                "¡Bienvenido %s!\n\n" +
                "Como Jefe de %s tienes acceso a:\n" +
                "• Gestión completa de tu departamento\n" +
                "• Supervisión de tu equipo\n" +
                "• Reportes y métricas específicas\n" +
                "• Herramientas especializadas\n\n" +
                "Explora las pestañas para gestionar eficientemente tu área.",
                usuarioActual.getNombre(),
                usuarioActual.getDepartamento()
            );
            
            JOptionPane.showMessageDialog(this, mensaje, "Bienvenido Jefe de Departamento", JOptionPane.INFORMATION_MESSAGE);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Muestra mensaje de bienvenida para empleados
     */
    private void mostrarMensajeBienvenidaEmpleado() {
        Timer timer = new Timer(2000, e -> {
            String mensaje = String.format(
                "¡Hola %s!\n\n" +
                "Trabajas en: %s\n\n" +
                "En tu área personal puedes:\n" +
                "• Ver y gestionar tus tareas\n" +
                "• Acceder a herramientas de tu departamento\n" +
                "• Comunicarte con tu equipo\n" +
                "• Seguir el progreso de tus proyectos\n\n" +
                "¡Que tengas un día productivo!",
                usuarioActual.getNombre(),
                usuarioActual.getDepartamento()
            );
            
            JOptionPane.showMessageDialog(this, mensaje, "Bienvenido al Sistema", JOptionPane.INFORMATION_MESSAGE);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Genera reporte completo (jefe)
     */
    private void generarReporteCompleto() {
        JOptionPane.showMessageDialog(this, "Funcionalidad de reportes en desarrollo.",
            "Generar Reporte", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Calcula tiempo restante estimado para una tarea
     */
    private int calcularTiempoRestante(Tarea tarea) {
        // Implementación simple - en un sistema real se basaría en fechas
        return tarea.getHorasEstimadas(); // Por ahora retorna las horas estimadas
    }

    
}