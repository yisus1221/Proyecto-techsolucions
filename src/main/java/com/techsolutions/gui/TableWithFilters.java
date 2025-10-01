package com.techsolutions.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

/**
 * Tabla personalizada con filtros automáticos y mejor contraste visual
 */
public class TableWithFilters extends JTable {
    
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField[] filterFields;
    private JPanel filterPanel;
    
    public TableWithFilters(DefaultTableModel model) {
        super(model);
        
        // Configurar sorter con filtros
        sorter = new TableRowSorter<>(model);
        setRowSorter(sorter);
        
        // Crear campos de filtro
        createFilterFields();
        
        // Personalizar apariencia
        setupTableAppearance();
        
        // Hacer tabla no editable
        setDefaultEditor(Object.class, null);
    }
    
    private void createFilterFields() {
        int columnCount = getColumnCount();
        filterFields = new JTextField[columnCount];
        
        filterPanel = new JPanel(new GridLayout(1, columnCount, 2, 2));
        filterPanel.setBackground(new Color(240, 248, 255));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        
        for (int i = 0; i < columnCount; i++) {
            final int columnIndex = i;
            
            JTextField filterField = new JTextField();
            filterField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            filterField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
            ));
            
            // Placeholder text
            filterField.setForeground(Color.GRAY);
            filterField.setText("Filtrar " + getColumnName(columnIndex) + "...");
            
            filterField.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (filterField.getForeground() == Color.GRAY) {
                        filterField.setText("");
                        filterField.setForeground(Color.BLACK);
                    }
                }
                
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (filterField.getText().isEmpty()) {
                        filterField.setForeground(Color.GRAY);
                        filterField.setText("Filtrar " + getColumnName(columnIndex) + "...");
                    }
                }
            });
            
            // Agregar listener para filtrado en tiempo real
            filterField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    applyFilters();
                }
            });
            
            filterFields[i] = filterField;
            filterPanel.add(filterField);
        }
    }
    
    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        
        for (int i = 0; i < filterFields.length; i++) {
            JTextField field = filterFields[i];
            String text = field.getText();
            
            // Solo aplicar filtro si no es el placeholder
            if (!text.isEmpty() && !field.getForeground().equals(Color.GRAY)) {
                try {
                    filters.add(RowFilter.regexFilter("(?i)" + text, i));
                } catch (java.util.regex.PatternSyntaxException e) {
                    // Ignorar patrones inválidos
                }
            }
        }
        
        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }
    
    private void setupTableAppearance() {
        // Configuración general
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(28);
        setShowGrid(true);
        setGridColor(new Color(220, 220, 220));
        
        // Header personalizado
        JTableHeader header = getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        
        // Renderer para las celdas con mejor contraste
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(184, 207, 229));
                    c.setForeground(Color.BLACK);
                } else {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }
                    c.setForeground(Color.BLACK);
                }
                
                // Asegurar buen contraste
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                
                return c;
            }
        });
        
        // Centrar contenido numérico
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // Aplicar a columnas que típicamente contienen IDs o números
        if (getColumnCount() > 0) {
            getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        }
    }
    
    public JPanel getFilterPanel() {
        return filterPanel;
    }
    
    public void clearFilters() {
        for (JTextField field : filterFields) {
            field.setText("");
            field.setForeground(Color.GRAY);
        }
        sorter.setRowFilter(null);
    }
    
    public void refreshFilters() {
        // Actualizar placeholder texts si las columnas cambiaron
        for (int i = 0; i < filterFields.length && i < getColumnCount(); i++) {
            if (filterFields[i].getForeground() == Color.GRAY) {
                filterFields[i].setText("Filtrar " + getColumnName(i) + "...");
            }
        }
    }
}