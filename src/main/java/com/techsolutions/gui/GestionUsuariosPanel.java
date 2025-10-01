package com.techsolutions.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.techsolutions.model.Usuario;
import com.techsolutions.services.UsuarioService;

/**
 * Panel para gestionar cuentas de usuarios del sistema
 */
public class GestionUsuariosPanel extends JFrame {
    
    private UsuarioService usuarioService;
    private DefaultTableModel modeloTablaUsuarios;
    private JTable tablaUsuarios;
    private JButton btnCrearUsuario, btnEditarUsuario, btnEliminarUsuario, btnDesactivar, btnReactivar, btnRestablecerPassword;
    private JLabel lblEstadisticas;
    
    public GestionUsuariosPanel() {
        this.usuarioService = UsuarioService.getInstance();
        initComponents();
        cargarDatos();
        actualizarEstadisticas();
    }
    
    private void initComponents() {
        setTitle("👥 Gestión de Usuarios - TechSolutions S.A. de C.V.");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel superior con título y estadísticas
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(70, 130, 180));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("👥 Administración de Cuentas de Usuario", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);
        
        lblEstadisticas = new JLabel("Cargando estadísticas...", JLabel.RIGHT);
        lblEstadisticas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstadisticas.setForeground(Color.WHITE);
        panelSuperior.add(lblEstadisticas, BorderLayout.EAST);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Crear tabla de usuarios
        String[] columnas = {"ID", "Username", "Nombre", "Rol", "Departamento", "Email", "Estado"};
        modeloTablaUsuarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(modeloTablaUsuarios);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaUsuarios.setRowHeight(25);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaUsuarios.getTableHeader().setBackground(new Color(70, 130, 180));
        tablaUsuarios.getTableHeader().setForeground(Color.WHITE);
        
        // Renderer personalizado para el estado
        tablaUsuarios.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (column == 6) { // Columna Estado
                        if ("🟢 Activo".equals(value)) {
                            c.setBackground(new Color(240, 255, 240));
                            c.setForeground(new Color(0, 100, 0));
                        } else {
                            c.setBackground(new Color(255, 240, 240));
                            c.setForeground(new Color(150, 0, 0));
                        }
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Usuarios del Sistema"));
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 3, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        panelBotones.setBackground(new Color(240, 248, 255));
        
        btnCrearUsuario = crearBoton("➕ Crear Usuario", new Color(144, 238, 144));
        btnEditarUsuario = crearBoton("✏️ Editar Usuario", new Color(255, 228, 181));
        btnEliminarUsuario = crearBoton("🗑️ Eliminar", new Color(255, 182, 193));
        btnDesactivar = crearBoton("⏸️ Desactivar", new Color(255, 222, 173));
        btnReactivar = crearBoton("▶️ Reactivar", new Color(173, 216, 230));
        btnRestablecerPassword = crearBoton("🔑 Restablecer", new Color(221, 160, 221));
        
        // Agregar listeners
        btnCrearUsuario.addActionListener(e -> crearNuevoUsuario());
        btnEditarUsuario.addActionListener(e -> editarUsuarioSeleccionado());
        btnEliminarUsuario.addActionListener(e -> eliminarUsuarioSeleccionado());
        btnDesactivar.addActionListener(e -> desactivarUsuarioSeleccionado());
        btnReactivar.addActionListener(e -> reactivarUsuarioSeleccionado());
        btnRestablecerPassword.addActionListener(e -> restablecerPasswordUsuario());
        
        panelBotones.add(btnCrearUsuario);
        panelBotones.add(btnEditarUsuario);
        panelBotones.add(btnEliminarUsuario);
        panelBotones.add(btnDesactivar);
        panelBotones.add(btnReactivar);
        panelBotones.add(btnRestablecerPassword);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.BLACK);
        boton.setBorder(BorderFactory.createRaisedBevelBorder());
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
    
    private void cargarDatos() {
        modeloTablaUsuarios.setRowCount(0);
        
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        for (Usuario usuario : usuarios) {
            String estado = usuario.isActivo() ? "🟢 Activo" : "🔴 Inactivo";
            String rolTexto = obtenerTextoRol(usuario.getRol());
            
            Object[] fila = {
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombre(),
                rolTexto,
                usuario.getDepartamento(),
                usuario.getEmail(),
                estado
            };
            
            modeloTablaUsuarios.addRow(fila);
        }
    }
    
    private String obtenerTextoRol(Usuario.Rol rol) {
        switch (rol) {
            case JEFE:
                return "👑 Jefe General";
            case JEFE_DEPARTAMENTO:
                return "👨‍💼 Jefe Departamento";
            case EMPLEADO:
                return "👤 Empleado";
            default:
                return rol.getDescripcion();
        }
    }
    
    private void actualizarEstadisticas() {
        Map<String, Integer> stats = usuarioService.obtenerEstadisticas();
        
        String estadisticas = String.format(
            "Total: %d | Activos: %d | Inactivos: %d | Jefes: %d | J.Dept: %d | Empleados: %d",
            stats.get("total"),
            stats.get("activos"),
            stats.get("inactivos"),
            stats.get("jefes"),
            stats.get("jefes_departamento"),
            stats.get("empleados")
        );
        
        lblEstadisticas.setText(estadisticas);
    }
    
    private void crearNuevoUsuario() {
        CrearUsuarioDialog dialog = new CrearUsuarioDialog(this);
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            cargarDatos();
            actualizarEstadisticas();
        }
    }
    
    private void editarUsuarioSeleccionado() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarMensaje("Seleccione un usuario para editar", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String username = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        Usuario usuario = usuarioService.buscarPorUsername(username);
        
        EditarUsuarioDialog dialog = new EditarUsuarioDialog(this, usuario);
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            cargarDatos();
            actualizarEstadisticas();
        }
    }
    
    private void eliminarUsuarioSeleccionado() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarMensaje("Seleccione un usuario para eliminar", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String username = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        String nombre = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 2);
        
        if ("jefe".equals(username)) {
            mostrarMensaje("No se puede eliminar al usuario jefe principal", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al usuario '" + nombre + "'?\nEsta acción no se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (usuarioService.eliminarUsuario(username)) {
                mostrarMensaje("Usuario eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
                actualizarEstadisticas();
            } else {
                mostrarMensaje("Error al eliminar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void desactivarUsuarioSeleccionado() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarMensaje("Seleccione un usuario para desactivar", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String username = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        
        if (usuarioService.desactivarUsuario(username)) {
            mostrarMensaje("Usuario desactivado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
            actualizarEstadisticas();
        } else {
            mostrarMensaje("Error al desactivar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reactivarUsuarioSeleccionado() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarMensaje("Seleccione un usuario para reactivar", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String username = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        
        if (usuarioService.reactivarUsuario(username)) {
            mostrarMensaje("Usuario reactivado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
            actualizarEstadisticas();
        } else {
            mostrarMensaje("Error al reactivar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void restablecerPasswordUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarMensaje("Seleccione un usuario para restablecer su contraseña", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String username = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        String nombre = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 2);
        
        String nuevaPassword = JOptionPane.showInputDialog(this,
            "Ingrese la nueva contraseña para " + nombre + ":",
            "Restablecer Contraseña",
            JOptionPane.QUESTION_MESSAGE);
        
        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            if (usuarioService.restablecerPassword(username, nuevaPassword)) {
                mostrarMensaje("Contraseña restablecida exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                mostrarMensaje("Error al restablecer la contraseña", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
    
    /**
     * Dialog para crear nuevos usuarios
     */
    private class CrearUsuarioDialog extends JDialog {
        private JTextField txtUsername, txtNombre, txtEmail, txtDepartamento;
        private JPasswordField txtPassword;
        private JComboBox<Usuario.Rol> cmbRol;
        private boolean confirmado = false;
        
        public CrearUsuarioDialog(JFrame parent) {
            super(parent, "Crear Nuevo Usuario", true);
            initDialog();
        }
        
        private void initDialog() {
            setLayout(new BorderLayout());
            
            // Panel de campos
            JPanel panelCampos = new JPanel(new GridBagLayout());
            panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Username
            gbc.gridx = 0; gbc.gridy = 0;
            panelCampos.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            txtUsername = new JTextField(15);
            panelCampos.add(txtUsername, gbc);
            
            // Password
            gbc.gridx = 0; gbc.gridy = 1;
            panelCampos.add(new JLabel("Contraseña:"), gbc);
            gbc.gridx = 1;
            txtPassword = new JPasswordField(15);
            panelCampos.add(txtPassword, gbc);
            
            // Nombre
            gbc.gridx = 0; gbc.gridy = 2;
            panelCampos.add(new JLabel("Nombre Completo:"), gbc);
            gbc.gridx = 1;
            txtNombre = new JTextField(15);
            panelCampos.add(txtNombre, gbc);
            
            // Email
            gbc.gridx = 0; gbc.gridy = 3;
            panelCampos.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            txtEmail = new JTextField(15);
            panelCampos.add(txtEmail, gbc);
            
            // Rol
            gbc.gridx = 0; gbc.gridy = 4;
            panelCampos.add(new JLabel("Rol:"), gbc);
            gbc.gridx = 1;
            cmbRol = new JComboBox<>(Usuario.Rol.values());
            cmbRol.setSelectedItem(Usuario.Rol.EMPLEADO);
            panelCampos.add(cmbRol, gbc);
            
            // Departamento
            gbc.gridx = 0; gbc.gridy = 5;
            panelCampos.add(new JLabel("Departamento:"), gbc);
            gbc.gridx = 1;
            txtDepartamento = new JTextField(15);
            panelCampos.add(txtDepartamento, gbc);
            
            add(panelCampos, BorderLayout.CENTER);
            
            // Panel de botones
            JPanel panelBotones = new JPanel(new FlowLayout());
            JButton btnAceptar = new JButton("Crear Usuario");
            JButton btnCancelar = new JButton("Cancelar");
            
            btnAceptar.addActionListener(e -> {
                if (validarCampos()) {
                    crearUsuario();
                }
            });
            
            btnCancelar.addActionListener(e -> dispose());
            
            panelBotones.add(btnAceptar);
            panelBotones.add(btnCancelar);
            add(panelBotones, BorderLayout.SOUTH);
            
            pack();
            setLocationRelativeTo(getParent());
        }
        
        private boolean validarCampos() {
            if (txtUsername.getText().trim().isEmpty()) {
                mostrarMensaje("El username es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (new String(txtPassword.getPassword()).trim().isEmpty()) {
                mostrarMensaje("La contraseña es obligatoria", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (txtNombre.getText().trim().isEmpty()) {
                mostrarMensaje("El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (!usuarioService.isUsernameDisponible(txtUsername.getText().trim())) {
                mostrarMensaje("El username ya está en uso", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return true;
        }
        
        private void crearUsuario() {
            Usuario nuevoUsuario = new Usuario(
                txtUsername.getText().trim(),
                new String(txtPassword.getPassword()),
                (Usuario.Rol) cmbRol.getSelectedItem(),
                txtDepartamento.getText().trim()
            );
            
            nuevoUsuario.setNombre(txtNombre.getText().trim());
            nuevoUsuario.setEmail(txtEmail.getText().trim());
            
            // Simulamos la creación (en una implementación real usarías el servicio)
            confirmado = true;
            dispose();
            mostrarMensaje("Usuario creado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        
        public boolean isConfirmado() {
            return confirmado;
        }
    }
    
    /**
     * Dialog para editar usuarios existentes
     */
    private class EditarUsuarioDialog extends JDialog {
        private Usuario usuario;
        private JTextField txtNombre, txtEmail, txtDepartamento;
        private JPasswordField txtPassword;
        private JComboBox<Usuario.Rol> cmbRol;
        private JCheckBox chkActivo;
        private boolean confirmado = false;
        
        public EditarUsuarioDialog(JFrame parent, Usuario usuario) {
            super(parent, "Editar Usuario: " + usuario.getUsername(), true);
            this.usuario = usuario;
            initDialog();
        }
        
        private void initDialog() {
            setLayout(new BorderLayout());
            
            // Panel de campos
            JPanel panelCampos = new JPanel(new GridBagLayout());
            panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Username (solo lectura)
            gbc.gridx = 0; gbc.gridy = 0;
            panelCampos.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            JTextField txtUsernameRO = new JTextField(usuario.getUsername(), 15);
            txtUsernameRO.setEditable(false);
            txtUsernameRO.setBackground(Color.LIGHT_GRAY);
            panelCampos.add(txtUsernameRO, gbc);
            
            // Password
            gbc.gridx = 0; gbc.gridy = 1;
            panelCampos.add(new JLabel("Nueva Contraseña:"), gbc);
            gbc.gridx = 1;
            txtPassword = new JPasswordField(15);
            panelCampos.add(txtPassword, gbc);
            
            // Nombre
            gbc.gridx = 0; gbc.gridy = 2;
            panelCampos.add(new JLabel("Nombre Completo:"), gbc);
            gbc.gridx = 1;
            txtNombre = new JTextField(usuario.getNombre(), 15);
            panelCampos.add(txtNombre, gbc);
            
            // Email
            gbc.gridx = 0; gbc.gridy = 3;
            panelCampos.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            txtEmail = new JTextField(usuario.getEmail(), 15);
            panelCampos.add(txtEmail, gbc);
            
            // Rol
            gbc.gridx = 0; gbc.gridy = 4;
            panelCampos.add(new JLabel("Rol:"), gbc);
            gbc.gridx = 1;
            cmbRol = new JComboBox<>(Usuario.Rol.values());
            cmbRol.setSelectedItem(usuario.getRol());
            panelCampos.add(cmbRol, gbc);
            
            // Departamento
            gbc.gridx = 0; gbc.gridy = 5;
            panelCampos.add(new JLabel("Departamento:"), gbc);
            gbc.gridx = 1;
            txtDepartamento = new JTextField(usuario.getDepartamento(), 15);
            panelCampos.add(txtDepartamento, gbc);
            
            // Estado activo
            gbc.gridx = 0; gbc.gridy = 6;
            panelCampos.add(new JLabel("Estado:"), gbc);
            gbc.gridx = 1;
            chkActivo = new JCheckBox("Usuario Activo", usuario.isActivo());
            panelCampos.add(chkActivo, gbc);
            
            add(panelCampos, BorderLayout.CENTER);
            
            // Panel de botones
            JPanel panelBotones = new JPanel(new FlowLayout());
            JButton btnGuardar = new JButton("Guardar Cambios");
            JButton btnCancelar = new JButton("Cancelar");
            
            btnGuardar.addActionListener(e -> {
                guardarCambios();
            });
            
            btnCancelar.addActionListener(e -> dispose());
            
            panelBotones.add(btnGuardar);
            panelBotones.add(btnCancelar);
            add(panelBotones, BorderLayout.SOUTH);
            
            pack();
            setLocationRelativeTo(getParent());
        }
        
        private void guardarCambios() {
            // Actualizar los datos del usuario
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setRol((Usuario.Rol) cmbRol.getSelectedItem());
            usuario.setDepartamento(txtDepartamento.getText().trim());
            usuario.setActivo(chkActivo.isSelected());
            
            String nuevaPassword = new String(txtPassword.getPassword()).trim();
            if (!nuevaPassword.isEmpty()) {
                usuario.setPassword(nuevaPassword);
            }
            
            confirmado = true;
            dispose();
            mostrarMensaje("Usuario actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        
        public boolean isConfirmado() {
            return confirmado;
        }
    }
}