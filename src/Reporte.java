/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author monic
 */

public class Reporte extends javax.swing.JFrame {

    private static final java.math.RoundingMode RM = java.math.RoundingMode.HALF_UP;

    private static java.math.BigDecimal div2(java.math.BigDecimal a, int b) {
        if (a == null || b <= 0) {
            return java.math.BigDecimal.ZERO;
        }
        return a.divide(java.math.BigDecimal.valueOf(b), 2, RM);
    }

// Vida útil SIEMPRE finita. Si viene “12 meses” → 1 año; “18 meses” → 2 años; vacío → 1 año.
    private static int parseVidaAnios(Object vida) {
        if (vida == null) {
            return 1;
        }
        String s = vida.toString().toLowerCase().trim();
        String digits = s.replaceAll("\\D+", "");           // deja solo números
        if (digits.isEmpty()) {
            return 1;
        }
        int n = Integer.parseInt(digits);
        if (s.contains("mes")) {
            return Math.max(1, (int) Math.ceil(n / 12.0));
        }
        return Math.max(1, n);
    }

// Meses transcurridos desde el MES de compra hasta el fin del periodo reportado (incluye mes de compra)
    private static int mesesTranscurridos(java.time.LocalDate fechaAdq, int repYear, int repMonth1_12, int vidaAnios) {
        if (fechaAdq == null) {
            return 0;
        }
        java.time.YearMonth compra = java.time.YearMonth.from(fechaAdq);
        java.time.YearMonth reporte = java.time.YearMonth.of(repYear, repMonth1_12);
        if (reporte.isBefore(compra)) {
            return 0;
        }
        int totalMesesVida = vidaAnios * 12;
        long diff = java.time.temporal.ChronoUnit.MONTHS.between(compra, reporte) + 1; // +1 incluye mes compra
        if (diff < 0) {
            diff = 0;
        }
        if (diff > totalMesesVida) {
            diff = totalMesesVida;
        }
        return (int) diff;
    }

    /**
     * Creates new form Reporte
     */
    public Reporte() {
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
        configurarTablaSimple();

        javax.swing.ButtonGroup grupo = new javax.swing.ButtonGroup();
        grupo.add(rbMes);
        grupo.add(rbAnio);
        rbMes.setSelected(true);

        cargarMesesYAnios();
        rbMes.addActionListener(e -> cbMes.setEnabled(true));
        rbAnio.addActionListener(e -> cbMes.setEnabled(false));

        btnGenerar.addActionListener(e -> onGenerar());
    }

    ///*******************************METODOS******************
    private javax.swing.table.DefaultTableModel modelo;
    private javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter;

    private void configurarTablaSimple() {
        String[] cols = {
            "Codigo", "Nombre", "Tipo", "Fecha Adquisición",
            "Costo", "Vida (años)",
            "Amort. Anual", "Amort. Mensual",
            "Amort. del ...", "Amort. Acum.",
            "Valor en Libros", "Cuotas Pend.", "Remanente"
        };
        modelo = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                return switch (c) {
                    case 4, 6, 7, 8, 9, 10, 12 ->
                        java.math.BigDecimal.class;
                    case 5, 11 ->
                        Integer.class;
                    default ->
                        String.class;
                };
            }
        };
        tblReporte.setModel(modelo);
        sorter = new javax.swing.table.TableRowSorter<>(modelo);
        tblReporte.setRowSorter(sorter);
    }
    ///*************************************************************************

    private static final String[] MESES = {
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    private void cargarMesesYAnios() {
        cbMes.removeAllItems();
        for (String m : MESES) {
            cbMes.addItem(m);
        }

        cbAnio.removeAllItems();
        int year = java.time.LocalDate.now().getYear();
        for (int y = year; y >= year - 15; y--) {
            cbAnio.addItem(String.valueOf(y));
        }

        cbMes.setEnabled(true);
        cbMes.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);
        cbAnio.setSelectedItem(String.valueOf(year));
    }

    private void onGenerar() {
        if (rbMes.isSelected()) {
            tblReporte.getColumnModel().getColumn(8).setHeaderValue("Amort. del Mes");
            tblReporte.getTableHeader().repaint();
            generarPorMes();
        } else {
            tblReporte.getColumnModel().getColumn(8).setHeaderValue("Amort. del Año");
            tblReporte.getTableHeader().repaint();
            generarPorAnio();
        }
    }
/// METODO PARA GENERAR POR MES 

    private void generarPorMes() {
        modelo.setRowCount(0);

        int anio = Integer.parseInt(cbAnio.getSelectedItem().toString());
        int mes = cbMes.getSelectedIndex() + 1;

        String sql
                = "SELECT codigo_, nombre_intangible, tipo_licencia_, "
                + "       fecha_adquisicion, costo, vida_util "
                + "FROM public.intangible "
                + "ORDER BY idintangible";

        try (java.sql.Connection cn = Conexion.getConnection(); java.sql.Statement st = cn.createStatement(); java.sql.ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String codigo = rs.getString("codigo_");
                String nombre = rs.getString("nombre_intangible");
                String tipo = rs.getString("tipo_licencia_");

                java.sql.Date fsql = rs.getDate("fecha_adquisicion");
                java.time.LocalDate fechaAdq = (fsql == null ? null : fsql.toLocalDate());
                String fAdq = (fsql == null ? "" : new java.text.SimpleDateFormat("d/M/yyyy").format(fsql));

                java.math.BigDecimal costo = rs.getBigDecimal("costo");
                int vidaAnios = parseVidaAnios(rs.getObject("vida_util"));

                int totalMeses = vidaAnios * 12;
                int mTrans = mesesTranscurridos(fechaAdq, anio, mes, vidaAnios);

                java.math.BigDecimal amortAnual = div2(costo, vidaAnios);
                java.math.BigDecimal amortMensual = div2(costo, totalMeses);
                java.math.BigDecimal amortDelMes = (mTrans > 0 && mTrans <= totalMeses) ? amortMensual : java.math.BigDecimal.ZERO;

                java.math.BigDecimal amortAcum = amortMensual.multiply(java.math.BigDecimal.valueOf(mTrans))
                        .setScale(2, RM);
                if (amortAcum.compareTo(costo) > 0) {
                    amortAcum = costo;
                }

                java.math.BigDecimal valorEnLibros = costo.subtract(amortAcum);
                if (valorEnLibros.signum() < 0) {
                    valorEnLibros = java.math.BigDecimal.ZERO;
                }

                int cuotasPend = Math.max(0, totalMeses - mTrans);
                java.math.BigDecimal remanente = valorEnLibros;

                modelo.addRow(new Object[]{
                    codigo, nombre, tipo, fAdq,
                    costo, vidaAnios,
                    amortAnual, amortMensual,
                    amortDelMes, amortAcum,
                    valorEnLibros, cuotasPend, remanente
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Error al generar reporte mensual:\n" + ex.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }

    }

/// METOOD PARA GENERAR POR AÑO 
    private void generarPorAnio() {
        modelo.setRowCount(0);

        int anio = Integer.parseInt(cbAnio.getSelectedItem().toString());

        String sql
                = "SELECT codigo_, nombre_intangible, tipo_licencia_, "
                + "       fecha_adquisicion, costo, vida_util "
                + "FROM public.intangible "
                + "ORDER BY idintangible";

        try (java.sql.Connection cn = Conexion.getConnection(); java.sql.Statement st = cn.createStatement(); java.sql.ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String codigo = rs.getString("codigo_");
                String nombre = rs.getString("nombre_intangible");
                String tipo = rs.getString("tipo_licencia_");

                java.sql.Date fsql = rs.getDate("fecha_adquisicion");
                java.time.LocalDate fechaAdq = (fsql == null ? null : fsql.toLocalDate());
                String fAdq = (fsql == null ? "" : new java.text.SimpleDateFormat("d/M/yyyy").format(fsql));

                java.math.BigDecimal costo = rs.getBigDecimal("costo");
                int vidaAnios = parseVidaAnios(rs.getObject("vida_util"));

                int totalMeses = vidaAnios * 12;

                int mHastaFin = (fechaAdq == null) ? 0 : mesesTranscurridos(fechaAdq, anio, 12, vidaAnios);
                int mHastaPrev = (fechaAdq == null) ? 0 : mesesTranscurridos(fechaAdq, anio - 1, 12, vidaAnios);
                int mEnAnio = Math.max(0, mHastaFin - mHastaPrev);

                java.math.BigDecimal amortAnual = div2(costo, vidaAnios);
                java.math.BigDecimal amortMensual = div2(costo, totalMeses);
                java.math.BigDecimal amortDelAnio = amortMensual.multiply(java.math.BigDecimal.valueOf(mEnAnio))
                        .setScale(2, RM);

                java.math.BigDecimal amortAcum = amortMensual.multiply(java.math.BigDecimal.valueOf(mHastaFin))
                        .setScale(2, RM);
                if (amortAcum.compareTo(costo) > 0) {
                    amortAcum = costo;
                }

                java.math.BigDecimal valorEnLibros = costo.subtract(amortAcum);
                if (valorEnLibros.signum() < 0) {
                    valorEnLibros = java.math.BigDecimal.ZERO;
                }

                int cuotasPend = Math.max(0, totalMeses - mHastaFin);
                java.math.BigDecimal remanente = valorEnLibros;

                modelo.addRow(new Object[]{
                    codigo, nombre, tipo, fAdq,
                    costo, vidaAnios,
                    amortAnual, amortMensual,
                    amortDelAnio, amortAcum,
                    valorEnLibros, cuotasPend, remanente
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Error al generar reporte anual:\n" + ex.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

//// METODO PARA FILTRAR POR EL NOMBRE 
    private void aplicarFiltroNombre() {
        if (sorter == null) {
            return; // por seguridad
        }
        String q = (txtBuscar.getText() == null) ? "" : txtBuscar.getText().trim();

        if (q.isEmpty()) {
            // sin texto: quitar filtro
            sorter.setRowFilter(null);
            return;
        }

        // Columna "Nombre" = índice 1 en tu modelo
        // Usamos regex case-insensitive y escapamos caracteres especiales
        String pattern = "(?i)" + java.util.regex.Pattern.quote(q);
        sorter.setRowFilter(javax.swing.RowFilter.regexFilter(pattern, 1));
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rbAnio = new javax.swing.JRadioButton();
        rbMes = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        cbMes = new javax.swing.JComboBox<>();
        btnGenerar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReporte = new javax.swing.JTable();
        cbAnio = new javax.swing.JComboBox<>();
        txtBuscar = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("REPORTE  AMORTIZACION");

        jLabel2.setText("Ver reporte por ");

        jLabel3.setText("Mes ");

        rbAnio.setText("Año ");
        rbAnio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbAnioActionPerformed(evt);
            }
        });

        rbMes.setText("Mes ");

        jLabel4.setText("Año");

        cbMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enero ", "Febrero ", "Marzo ", "Abril ", "Mayo ", "Junio ", "Julio ", "Agosto", "Septiembre ", "Octubre ", "Noviembre ", "Diciembre" }));

        btnGenerar.setText("Generar ");
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });

        tblReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Nombre", "Tipo", "Fecha Adquisicion ", "Costo ", "Vida Util ", "Amortizacion Mensual ", "Amortizacion Anual ", "Valor en libros ", "Cuotas Pendientes ", "Remanentes por Amortizar "
            }
        ));
        jScrollPane1.setViewportView(tblReporte);

        cbAnio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });

        btnFiltrar.setText("Filtrar ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(216, 216, 216)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(195, 195, 195)
                        .addComponent(jLabel2)
                        .addGap(313, 313, 313)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnGenerar)
                                .addGap(46, 46, 46)
                                .addComponent(rbAnio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnFiltrar)
                                .addGap(72, 72, 72)))
                        .addComponent(jLabel4)))
                .addGap(61, 61, 61)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(60, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(420, 420, 420))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1088, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(334, 334, 334)
                    .addComponent(rbMes)
                    .addContainerGap(741, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(jLabel1)
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(cbMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbAnio)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGenerar)
                            .addComponent(cbAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFiltrar))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(162, 162, 162)
                    .addComponent(rbMes)
                    .addContainerGap(349, Short.MAX_VALUE)))
        );

        rbAnio.getAccessibleContext().setAccessibleName("Año");

        jMenu1.setText("Menú ");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Gestión de Intangibles ");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Reporte ");
        jMenuBar1.add(jMenu3);

        jMenu4.setText("Historial ");
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbAnioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAnioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbAnioActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        // TODO add your handling code here:
        // Aplicar filtro al dar Enter en el campo
txtBuscar.addActionListener(e -> aplicarFiltroNombre());
// Aplicar filtro al dar clic en el botón
btnFiltrar.addActionListener(e -> aplicarFiltroNombre());
        
    }//GEN-LAST:event_btnGenerarActionPerformed

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
        java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
        java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
        java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
        java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new Reporte().setVisible(true);
        }
    });
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JComboBox<String> cbAnio;
    private javax.swing.JComboBox<String> cbMes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbAnio;
    private javax.swing.JRadioButton rbMes;
    private javax.swing.JTable tblReporte;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
