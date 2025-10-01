package com.techsolutions.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import com.techsolutions.model.ArbolEmpleados;
import com.techsolutions.model.Empleado;

/**
 * Ventana moderna para visualizar el árbol de empleados de forma jerárquica y atractiva
 */
public class ArbolEmpleadosViewer extends JFrame {
    
    private ArbolEmpleados arbolEmpleados;
    private JTree arbolVisual;
    private JPanel panelInfo;
    private JLabel lblEmpleadoSeleccionado;
    private JTextArea txtDetalles;
    
    public ArbolEmpleadosViewer(ArbolEmpleados arbol) {
        this.arbolEmpleados = arbol;
        initComponents();
        crearArbolVisual();
    }
    
    private void initComponents() {
        setTitle("Estructura Organizacional - TechSolutions S.A. de C.V.");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(70, 130, 180));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("Organigrama de Empleados", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        
        // Estadísticas del árbol
        JLabel lblEstadisticas = new JLabel(obtenerEstadisticas(), JLabel.RIGHT);
        lblEstadisticas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstadisticas.setForeground(Color.WHITE);
        panelTitulo.add(lblEstadisticas, BorderLayout.EAST);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.6);
        
        // Panel izquierdo - Árbol visual
        JPanel panelArbol = new JPanel(new BorderLayout());
        panelArbol.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "🌲 Estructura Jerárquica",
            0, 0, new Font("Segoe UI", Font.BOLD, 14),
            new Color(70, 130, 180)
        ));
        
        arbolVisual = new JTree();
        arbolVisual.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        arbolVisual.setRowHeight(25);
        
        // Renderer personalizado para el árbol
        arbolVisual.setCellRenderer(new CustomTreeCellRenderer());
        
        // Listener para mostrar detalles al seleccionar
        arbolVisual.addTreeSelectionListener(e -> {
            TreePath path = e.getPath();
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.getUserObject() instanceof Empleado) {
                    mostrarDetallesEmpleado((Empleado) node.getUserObject());
                }
            }
        });
        
        JScrollPane scrollArbol = new JScrollPane(arbolVisual);
        scrollArbol.setPreferredSize(new Dimension(350, 400));
        panelArbol.add(scrollArbol, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(panelArbol);
        
        // Panel derecho - Detalles del empleado
        crearPanelDetalles();
        splitPane.setRightComponent(panelInfo);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(new Color(240, 248, 255));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnExpandir = crearBoton("Expandir Todo", new Color(144, 238, 144));
        JButton btnContraer = crearBoton("Contraer Todo", new Color(255, 228, 181));
        JButton btnExportar = crearBoton("Exportar", new Color(173, 216, 230));
        JButton btnCerrar = crearBoton("Cerrar", new Color(255, 182, 193));
        
        btnExpandir.addActionListener(e -> expandirTodo());
        btnContraer.addActionListener(e -> contraerTodo());
        btnExportar.addActionListener(e -> exportarEstructura());
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnExpandir);
        panelBotones.add(btnContraer);
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.BLACK);
        boton.setBorder(BorderFactory.createRaisedBevelBorder());
        boton.setPreferredSize(new Dimension(140, 35));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
            }
        });
        
        return boton;
    }
    
    private void crearPanelDetalles() {
        panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Información del Empleado",
            0, 0, new Font("Segoe UI", Font.BOLD, 14),
            new Color(70, 130, 180)
        ));
        
        // Panel superior con nombre
        lblEmpleadoSeleccionado = new JLabel("Seleccione un empleado", JLabel.CENTER);
        lblEmpleadoSeleccionado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEmpleadoSeleccionado.setForeground(new Color(70, 130, 180));
        lblEmpleadoSeleccionado.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        panelInfo.add(lblEmpleadoSeleccionado, BorderLayout.NORTH);
        
        // Área de detalles
        txtDetalles = new JTextArea();
        txtDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDetalles.setEditable(false);
        txtDetalles.setBackground(new Color(248, 248, 255));
        txtDetalles.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        txtDetalles.setText("📋 Seleccione un empleado del árbol\n    para ver sus detalles aquí...");
        
        JScrollPane scrollDetalles = new JScrollPane(txtDetalles);
        scrollDetalles.setBorder(BorderFactory.createEmptyBorder());
        panelInfo.add(scrollDetalles, BorderLayout.CENTER);
    }
    
    private void crearArbolVisual() {
        if (arbolEmpleados.estaVacio()) {
            DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("🏢 Sin empleados");
            arbolVisual.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
            return;
        }
        
        // Crear nodo raíz
        DefaultMutableTreeNode nodoRaiz = new DefaultMutableTreeNode("🏢 TechSolutions S.A. de C.V.");
        
        // Agrupar empleados por departamento
        Map<String, DefaultMutableTreeNode> departamentos = new HashMap<>();
        
        // Agregar empleados recursivamente
        agregarEmpleadosAlArbol(arbolEmpleados.getRaiz(), departamentos, nodoRaiz);
        
        // Crear modelo del árbol
        javax.swing.tree.DefaultTreeModel modelo = new javax.swing.tree.DefaultTreeModel(nodoRaiz);
        arbolVisual.setModel(modelo);
        
        // Expandir primer nivel
        arbolVisual.expandRow(0);
    }
    
    private void agregarEmpleadosAlArbol(Empleado empleado, Map<String, DefaultMutableTreeNode> departamentos, DefaultMutableTreeNode raiz) {
        if (empleado == null) return;
        
        // Procesar empleado actual
        String departamento = empleado.getDepartamento();
        DefaultMutableTreeNode nodoDepartamento = departamentos.get(departamento);
        
        if (nodoDepartamento == null) {
            nodoDepartamento = new DefaultMutableTreeNode("🏬 " + departamento);
            departamentos.put(departamento, nodoDepartamento);
            raiz.add(nodoDepartamento);
        }
        
        DefaultMutableTreeNode nodoEmpleado = new DefaultMutableTreeNode(empleado);
        nodoDepartamento.add(nodoEmpleado);
        
        // Recursión para hijos
        agregarEmpleadosAlArbol(empleado.getIzquierda(), departamentos, raiz);
        agregarEmpleadosAlArbol(empleado.getDerecha(), departamentos, raiz);
    }
    
    private void mostrarDetallesEmpleado(Empleado empleado) {
        lblEmpleadoSeleccionado.setText("👤 " + empleado.getNombre());
        
        StringBuilder detalles = new StringBuilder();
        detalles.append("🆔 ID: ").append(empleado.getId()).append("\n\n");
        detalles.append("👨‍💼 Nombre Completo: ").append(empleado.getNombre()).append("\n\n");
        detalles.append("🏢 Departamento: ").append(empleado.getDepartamento()).append("\n\n");
        detalles.append("📊 Posición en el Árbol:\n");
        detalles.append("   • Nivel: ").append(calcularNivel(empleado)).append("\n");
        detalles.append("   • Empleados bajo supervisión: ").append(contarSubordinados(empleado)).append("\n\n");
        detalles.append("🌐 Estado: Activo\n");
        detalles.append("📅 Sistema: TechSolutions v1.0");
        
        txtDetalles.setText(detalles.toString());
    }
    
    private int calcularNivel(Empleado empleado) {
        // Simplificado: buscar desde la raíz
        return calcularNivelRec(arbolEmpleados.getRaiz(), empleado.getId(), 1);
    }
    
    private int calcularNivelRec(Empleado actual, String id, int nivel) {
        if (actual == null) return 0;
        if (actual.getId().equals(id)) return nivel;
        
        int nivelIzq = calcularNivelRec(actual.getIzquierda(), id, nivel + 1);
        if (nivelIzq > 0) return nivelIzq;
        
        return calcularNivelRec(actual.getDerecha(), id, nivel + 1);
    }
    
    private int contarSubordinados(Empleado empleado) {
        int count = 0;
        if (empleado.getIzquierda() != null) count += 1 + contarTodos(empleado.getIzquierda());
        if (empleado.getDerecha() != null) count += 1 + contarTodos(empleado.getDerecha());
        return count;
    }
    
    private int contarTodos(Empleado empleado) {
        if (empleado == null) return 0;
        return 1 + contarTodos(empleado.getIzquierda()) + contarTodos(empleado.getDerecha());
    }
    
    private String obtenerEstadisticas() {
        int total = arbolEmpleados.contarEmpleados();
        int altura = arbolEmpleados.obtenerAltura();
        return String.format("👥 %d empleados | 📏 Altura: %d", total, altura);
    }
    
    private void expandirTodo() {
        for (int i = 0; i < arbolVisual.getRowCount(); i++) {
            arbolVisual.expandRow(i);
        }
    }
    
    private void contraerTodo() {
        for (int i = arbolVisual.getRowCount() - 1; i >= 1; i--) {
            arbolVisual.collapseRow(i);
        }
    }
    
    private void exportarEstructura() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("ESTRUCTURA ORGANIZACIONAL - TECHSOLUTIONS S.A. DE C.V.\n");
        contenido.append("=".repeat(60)).append("\n\n");
        contenido.append("Total de empleados: ").append(arbolEmpleados.contarEmpleados()).append("\n");
        contenido.append("Altura del árbol: ").append(arbolEmpleados.obtenerAltura()).append("\n\n");
        contenido.append("EMPLEADOS POR DEPARTAMENTO:\n");
        contenido.append("-".repeat(30)).append("\n");
        
        exportarEmpleadosRec(arbolEmpleados.getRaiz(), contenido);
        
        JTextArea areaTexto = new JTextArea(contenido.toString());
        areaTexto.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaTexto.setEditable(false);
        
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scroll, "📄 Exportar Estructura", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportarEmpleadosRec(Empleado empleado, StringBuilder sb) {
        if (empleado == null) return;
        
        exportarEmpleadosRec(empleado.getIzquierda(), sb);
        sb.append(String.format("• [%s] %s - %s\n", 
                empleado.getId(), empleado.getNombre(), empleado.getDepartamento()));
        exportarEmpleadosRec(empleado.getDerecha(), sb);
    }
    
    /**
     * Renderer personalizado para mostrar iconos y colores
     */
    private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            
            if (node.getUserObject() instanceof Empleado) {
                Empleado emp = (Empleado) node.getUserObject();
                setText(emp.getNombre() + " (" + emp.getId() + ")");
                setForeground(new Color(25, 25, 112));
            } else {
                String texto = node.getUserObject().toString();
                if (texto.contains("TechSolutions")) {
                    setForeground(new Color(70, 130, 180));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (texto.startsWith("🏬")) {
                    setForeground(new Color(184, 134, 11));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            }
            
            return this;
        }
    }
}