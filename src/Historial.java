/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author monic
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Historial extends javax.swing.JFrame {

    // ==== Helpers de cálculo (mismos que en Reporte) ====
    private static final java.math.RoundingMode RM = java.math.RoundingMode.HALF_UP;

    private static java.math.BigDecimal div2(java.math.BigDecimal a, int b) {
        if (a == null || b <= 0) {
            return java.math.BigDecimal.ZERO;
        }
        return a.divide(java.math.BigDecimal.valueOf(b), 2, RM);
    }
// "12 meses" -> 1 año; "18 meses" -> 2 años; vacío -> 1

    private static int parseVidaAnios(Object vida) {
        if (vida == null) {
            return 1;
        }
        String s = vida.toString().toLowerCase().trim();
        String digits = s.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            return 1;
        }
        int n = Integer.parseInt(digits);
        if (s.contains("mes")) {
            return Math.max(1, (int) Math.ceil(n / 12.0));
        }
        return Math.max(1, n);
    }
// Meses transcurridos (incluye mes de compra) hasta mes/año de referencia

    private static int mesesTranscurridos(java.time.LocalDate fechaAdq, int refYear, int refMonth1_12, int vidaAnios) {
        if (fechaAdq == null) {
            return 0;
        }
        java.time.YearMonth compra = java.time.YearMonth.from(fechaAdq);
        java.time.YearMonth ref = java.time.YearMonth.of(refYear, refMonth1_12);
        if (ref.isBefore(compra)) {
            return 0;
        }
        int totalMesesVida = vidaAnios * 12;
        long diff = java.time.temporal.ChronoUnit.MONTHS.between(compra, ref) + 1;
        if (diff < 0) {
            diff = 0;
        }
        if (diff > totalMesesVida) {
            diff = totalMesesVida;
        }
        return (int) diff;
    }

// ==== Modelos/Sorter de las tablas ====
    private DefaultTableModel modeloMaster;
    private DefaultTableModel modeloDetalle;
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Creates new form Historial
     */
    public Historial() {
        initComponents();

        //  **************BARRA DE NAVEGACION *********************
        // GESTION INTANGIBLE
        jMenu2.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                new BUSQUEDA().setVisible(true);
                dispose();
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {
            }

            public void menuCanceled(javax.swing.event.MenuEvent e) {
            }
        });

        // REPORTE
        jMenu3.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent e) {
                new Reporte().setVisible(true);
                dispose();
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {
            }

            @Override
            public void menuCanceled(javax.swing.event.MenuEvent e) {
            }
        });

        // HISTORIAL 
        jMenu4.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                new Historial().setVisible(true);
                dispose();
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {
            }

            @Override
            public void menuCanceled(javax.swing.event.MenuEvent e) {
            }
        });

        // MENU
        jMenu1.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                new Menu().setVisible(true);
                dispose();
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {
            }

            public void menuCanceled(javax.swing.event.MenuEvent e) {
            }
        });

        //********* BARRA DE NAVEGACION***************************
        // ===== Configurar tabla superior (MASTER) =====
        String[] colsMaster = {
            "Codigo", "Nombre", "Tipo", "Estado", "Fecha Adq.",
            "Costo", "Vida (años)",
            "Amortizacion Anual", "Amortizacion Mensual",
            "Amortizacion del Mes", "Amortizacion Acum.",
            "Valor en libros", "Cuotas pendientes", "Remanente",
            "Acciones" // botón
        };
        modeloMaster = new DefaultTableModel(colsMaster, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 14;
            } // solo columna Acciones

            @Override
            public Class<?> getColumnClass(int c) {
                if (c == 5 || c == 7 || c == 8 || c == 9 || c == 10 || c == 11 || c == 13) {
                    return java.math.BigDecimal.class;
                }
                if (c == 6 || c == 12) {
                    return Integer.class;
                }
                return String.class;
            }
        };
        tblMaster.setModel(modeloMaster);
        tblMaster.setRowHeight(24);
        sorter = new TableRowSorter<>(modeloMaster);
        tblMaster.setRowSorter(sorter);

// botón “Ver historial” en col 14
        new ButtonColumn(tblMaster, crearAccionVerHistorial(), 14);

// ===== Configurar tabla inferior (DETALLE) =====
        String[] colsDetalle = {"Mes/Año", "Amort. Mensual", "Amort. Acumulada", "Valor en Libros", "Cuotas Pendientes", "Remanente"};
        modeloDetalle = new DefaultTableModel(colsDetalle, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                if (c == 1 || c == 2 || c == 3 || c == 5) {
                    return java.math.BigDecimal.class;
                }
                if (c == 4) {
                    return Integer.class;
                }
                return String.class;
            }
        };
        tblDetalle.setModel(modeloDetalle);
        tblDetalle.setRowHeight(24);

// ===== Eventos de buscar/filtrar =====
        btnFiltrar.addActionListener(e -> aplicarFiltro());
        txtBuscar.addActionListener(e -> aplicarFiltro());

// ===== Cargar datos generales a la fecha actual =====
        cargarMasterGeneralHoy();
    }
    private void aplicarFiltro() {
        String q = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();
        if (q.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            String pattern = "(?i)" + java.util.regex.Pattern.quote(q);
            sorter.setRowFilter(RowFilter.regexFilter(pattern, 1)); // filtra por columna “Nombre”
        }
    }

    private void cargarMasterGeneralHoy() {
        modeloMaster.setRowCount(0);
        modeloDetalle.setRowCount(0);

        java.time.YearMonth ahora = java.time.YearMonth.now();
        int anioRef = ahora.getYear();
        int mesRef = ahora.getMonthValue();

        String sql
                = "SELECT codigo_, nombre_intangible, tipo_licencia_, "
                + "       COALESCE(estado_,'') AS estado_, "
                + // si no tienes estado_, cámbialo o quítalo
                "       fecha_adquisicion, costo, vida_util "
                + "FROM public.intangible "
                + "ORDER BY idintangible";

        try (java.sql.Connection cn = Conexion.getConnection(); java.sql.Statement st = cn.createStatement(); java.sql.ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String codigo = rs.getString("codigo_");
                String nombre = rs.getString("nombre_intangible");
                String tipo = rs.getString("tipo_licencia_");
                String estado = rs.getString("estado_");

                java.sql.Date fsql = rs.getDate("fecha_adquisicion");
                java.time.LocalDate fechaAdq = (fsql == null ? null : fsql.toLocalDate());
                String fAdq = (fsql == null ? "" : new java.text.SimpleDateFormat("d/M/yyyy").format(fsql));

                java.math.BigDecimal costo = rs.getBigDecimal("costo");
                int vidaAnios = parseVidaAnios(rs.getObject("vida_util"));
                int totalMeses = vidaAnios * 12;

                // Cálculos a mes actual
                java.math.BigDecimal amortAnual = div2(costo, vidaAnios);
                java.math.BigDecimal amortMensual = div2(costo, totalMeses);

                int mTrans = mesesTranscurridos(fechaAdq, anioRef, mesRef, vidaAnios);
                java.math.BigDecimal amortDelMes = (mTrans > 0 && mTrans <= totalMeses) ? amortMensual : java.math.BigDecimal.ZERO;
                java.math.BigDecimal amortAcum = amortMensual.multiply(java.math.BigDecimal.valueOf(mTrans)).setScale(2, RM);
                if (amortAcum.compareTo(costo) > 0) {
                    amortAcum = costo;
                }

                java.math.BigDecimal valorEnLibros = costo.subtract(amortAcum);
                if (valorEnLibros.signum() < 0) {
                    valorEnLibros = java.math.BigDecimal.ZERO;
                }

                int cuotasPend = Math.max(0, totalMeses - mTrans);
                java.math.BigDecimal remanente = valorEnLibros;

                modeloMaster.addRow(new Object[]{
                    codigo, nombre, tipo, estado, fAdq,
                    costo, vidaAnios,
                    amortAnual, amortMensual,
                    amortDelMes, amortAcum,
                    valorEnLibros, cuotasPend, remanente,
                    "Ver historial" // Acciones
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar historial general:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private javax.swing.Action crearAccionVerHistorial() {
        return new javax.swing.AbstractAction("Ver historial") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JTable t = (JTable) e.getSource();
                int viewRow = Integer.parseInt(e.getActionCommand());
                int row = t.convertRowIndexToModel(viewRow);

                // Datos necesarios desde la fila seleccionada
                String fAdqStr = String.valueOf(modeloMaster.getValueAt(row, 4));
                java.time.LocalDate fechaAdq = null;
                try {
                    if (fAdqStr != null && !fAdqStr.trim().isEmpty()) {
                        fechaAdq = java.time.LocalDate.parse(
                                fAdqStr, java.time.format.DateTimeFormatter.ofPattern("d/M/uuuu")
                        );
                    }
                } catch (Exception ignore) {
                }

                java.math.BigDecimal costo = (java.math.BigDecimal) modeloMaster.getValueAt(row, 5);
                int vidaAnios = (Integer) modeloMaster.getValueAt(row, 6);

                llenarTablaDetalle(fechaAdq, costo, vidaAnios);
            }
        };
    }

    private void llenarTablaDetalle(java.time.LocalDate fechaAdq,
            java.math.BigDecimal costo, int vidaAnios) {
        modeloDetalle.setRowCount(0);
        if (fechaAdq == null) {
            JOptionPane.showMessageDialog(this, "Este registro no tiene fecha de adquisición.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int totalMeses = Math.max(1, vidaAnios) * 12;
        java.math.BigDecimal amortMensual = div2(costo, totalMeses);
        java.math.BigDecimal acum = java.math.BigDecimal.ZERO;

        java.time.YearMonth ym = java.time.YearMonth.from(fechaAdq);
        for (int i = 1; i <= totalMeses; i++) {
            acum = acum.add(amortMensual).setScale(2, RM);
            if (acum.compareTo(costo) > 0) {
                acum = costo;
            }

            java.math.BigDecimal valorLibros = costo.subtract(acum);
            if (valorLibros.signum() < 0) {
                valorLibros = java.math.BigDecimal.ZERO;
            }

            int cuotasPend = totalMeses - i;
            java.math.BigDecimal remanente = valorLibros;

            String etiqueta = String.format("%02d/%d", ym.getMonthValue(), ym.getYear());
            modeloDetalle.addRow(new Object[]{
                etiqueta, amortMensual, acum, valorLibros, cuotasPend, remanente
            });
            ym = ym.plusMonths(1);
        }
    }
    // ===== Botón en JTable (renderer + editor) =====

    static class ButtonColumn extends AbstractCellEditor
            implements javax.swing.table.TableCellRenderer, javax.swing.table.TableCellEditor, java.awt.event.ActionListener {

        private final JTable table;
        private final javax.swing.Action action;
        private final JButton renderButton = new JButton();
        private final JButton editButton = new JButton();
        private int editingRow = -1;

        ButtonColumn(JTable table, javax.swing.Action action, int column) {
            this.table = table;
            this.action = action;
            renderButton.setText("Ver historial");
            editButton.setText("Ver historial");
            editButton.addActionListener(this);

            javax.swing.table.TableColumnModel cm = table.getColumnModel();
            cm.getColumn(column).setCellRenderer(this);
            cm.getColumn(column).setCellEditor(this);
            cm.getColumn(column).setPreferredWidth(110);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            return renderButton;
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            editingRow = r;
            return editButton;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            fireEditingStopped();
            java.awt.event.ActionEvent ev = new java.awt.event.ActionEvent(
                    table, java.awt.event.ActionEvent.ACTION_PERFORMED, String.valueOf(editingRow));
            action.actionPerformed(ev);
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMaster = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("HISTORIAL DE LICENCIAS DE SOFTWARE ");

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });

        btnFiltrar.setText("Filtrar");

        tblMaster.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Nombre", "Tipo", "Estado", "Costo", "Vida", "Amortizacion Anual ", "Amortizacion Mensual ", "Valor en Libros ", "Cuotas pendientes ", "Remanente ", "Acciones"
            }
        ));
        jScrollPane1.setViewportView(tblMaster);

        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblDetalle);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(310, 310, 310)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFiltrar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(396, 396, 396)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1049, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 903, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setText("Menú ");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Gestión de Intangibles ");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Reporte");
        jMenuBar1.add(jMenu3);

        jMenu4.setText("Historial ");
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Historial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Historial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Historial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Historial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Historial().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTable tblMaster;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
