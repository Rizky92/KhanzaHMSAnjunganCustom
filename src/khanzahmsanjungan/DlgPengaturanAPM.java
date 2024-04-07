/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgAdmin.java
 *
 * Created on 04 Des 13, 12:59:34
 */
package khanzahmsanjungan;

import bridging.ApiBPJS;
import bridging.BPJSCekReferensiDokterDPJP1;
import bridging.BPJSCekReferensiPenyakit;
import bridging.BPJSCekRiwayatPelayanan;
import bridging.BPJSCekRiwayatRujukanTerakhir;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 *
 * @author Kode
 */
public class DlgPengaturanAPM extends javax.swing.JDialog {
    private Connection koneksi = koneksiDB.condb();
    private sekuel query = new sekuel();
    private validasi valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private ApiBPJS api = new ApiBPJS();
    private BPJSCekReferensiDokterDPJP1 cariDokterBPJS = new BPJSCekReferensiDokterDPJP1(null, true);
    private BPJSCekReferensiPenyakit cariDiagnosaBPJS = new BPJSCekReferensiPenyakit(null, true);
    private DlgCariPoliBPJS cariPoliBPJS = new DlgCariPoliBPJS(null, true);
    private DlgCariPoli cariPoli = new DlgCariPoli(null, true);
    private DlgCariDokter2 cariDokter = new DlgCariDokter2(null, true);
    private BPJSCekRiwayatRujukanTerakhir cekRujukanPasien = new BPJSCekRiwayatRujukanTerakhir(null, true);
    private BPJSCekRiwayatPelayanan riwayatPelayananPasien = new BPJSCekRiwayatPelayanan(null, true);
    private final SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
    private String aksi = "",
                   noRawat = "",
                   noReg = "",
                   jamReg = "",
                   kodePoliRS = "",
                   kodeDokterRS = "",
                   namaPj = "",
                   alamatPj = "",
                   hubunganPj = "DIRI SENDIRI",
                   biayaReg = "",
                   statusPoli = "Lama",
                   umurDaftar = "0",
                   statusUmur = "Th",
                   statusDaftar = "Lama";
    private int kuota = 0;

    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private Calendar cal = Calendar.getInstance();
    private boolean statusfinger = false, aplikasiAktif = false;
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private JsonNode nameNode;
    private int day = cal.get(Calendar.DAY_OF_WEEK);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date parsedDate;

    /**
     * Creates new form DlgAdmin
     *
     * @param parent
     * @param id
     */
    public DlgPengaturanAPM(java.awt.Frame parent, boolean id) {
        super(parent, id);
        initComponents();

        try {
            ps = koneksi.prepareStatement(
                    "select nm_pasien,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) asal,"
                    + "namakeluarga,keluarga,pasien.kd_pj,penjab.png_jawab,if(tgl_daftar=?,'Baru','Lama') as daftar, "
                    + "TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun, "
                    + "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, "
                    + "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari from pasien "
                    + "inner join kelurahan inner join kecamatan inner join kabupaten inner join penjab "
                    + "on pasien.kd_kel=kelurahan.kd_kel and pasien.kd_pj=penjab.kd_pj "
                    + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                    + "where pasien.no_rkm_medis=?");
        } catch (Exception ex) {
            System.out.println(ex);
        }

        try {
            ps = koneksi.prepareStatement("select nama_instansi, alamat_instansi, kabupaten, propinsi, aktifkan, wallpaper,kontak,email,logo from setting");
            rs = ps.executeQuery();
            while (rs.next()) {
                nama_instansi = rs.getString("nama_instansi");
                alamat_instansi = rs.getString("alamat_instansi");
                kabupaten = rs.getString("kabupaten");
                propinsi = rs.getString("propinsi");
                kontak = rs.getString("kontak");
                email = rs.getString("email");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        cariDokterBPJS.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cariDokterBPJS.getTable().getSelectedRow() != -1) {
                    kodeDokter.setText(cariDokterBPJS.getTable().getValueAt(cariDokterBPJS.getTable().getSelectedRow(), 1).toString());
                    namaDokter.setText(cariDokterBPJS.getTable().getValueAt(cariDokterBPJS.getTable().getSelectedRow(), 2).toString());
                    if (jenisPelayanan.getSelectedIndex() == 1) {
                        kodeDokterLayanan.setText(cariDokterBPJS.getTable().getValueAt(cariDokterBPJS.getTable().getSelectedRow(), 1).toString());
                        namaDokterLayanan.setText(cariDokterBPJS.getTable().getValueAt(cariDokterBPJS.getTable().getSelectedRow(), 2).toString());
                    }
                    kodeDokter.requestFocus();

                }
            }
        });

        cariPoliBPJS.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cariPoliBPJS.getTable().getSelectedRow() != -1) {
                    kodePoli.setText(cariPoliBPJS.getTable().getValueAt(cariPoliBPJS.getTable().getSelectedRow(), 0).toString());
                    namaPoli.setText(cariPoliBPJS.getTable().getValueAt(cariPoliBPJS.getTable().getSelectedRow(), 1).toString());
                    kodeDokter.requestFocus();

                }
            }
        });

        cariPoli.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cariPoli.getTable().getSelectedRow() != -1) {
                    KdPoliTerapi.setText(cariPoli.getTable().getValueAt(cariPoli.getTable().getSelectedRow(), 0).toString());
                    NmPoliTerapi.setText(cariPoli.getTable().getValueAt(cariPoli.getTable().getSelectedRow(), 1).toString());
                    KodeDokterTerapi.requestFocus();

                }
            }
        });

        cariDokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cariDokter.getTable().getSelectedRow() != -1) {
                    KodeDokterTerapi.setText(cariDokter.getTable().getValueAt(cariDokter.getTable().getSelectedRow(), 0).toString());
                    NmDokterTerapi.setText(cariDokter.getTable().getValueAt(cariDokter.getTable().getSelectedRow(), 1).toString());
                    KodeDokterTerapi.requestFocus();

                }
            }
        });

        cariDiagnosaBPJS.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cariDiagnosaBPJS.getTable().getSelectedRow() != -1) {

                    kodeDiagnosa.setText(cariDiagnosaBPJS.getTable().getValueAt(cariDiagnosaBPJS.getTable().getSelectedRow(), 1).toString());
                    namaDiagnosa.setText(cariDiagnosaBPJS.getTable().getValueAt(cariDiagnosaBPJS.getTable().getSelectedRow(), 2).toString());
                    kodeDiagnosa.requestFocus();

                }
            }
        });

        cekRujukanPasien.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cekRujukanPasien.getTable().getSelectedRow() != -1) {
                    kodeDiagnosa.setText(cekRujukanPasien.getTable().getValueAt(cekRujukanPasien.getTable().getSelectedRow(), 0).toString());
                    namaDiagnosa.setText(cekRujukanPasien.getTable().getValueAt(cekRujukanPasien.getTable().getSelectedRow(), 1).toString());
                    noRujukan.setText(cekRujukanPasien.getTable().getValueAt(cekRujukanPasien.getTable().getSelectedRow(), 2).toString());
                    kodePoli.setText(cekRujukanPasien.getTable().getValueAt(cekRujukanPasien.getTable().getSelectedRow(), 3).toString());
                    namaPoli.setText(cekRujukanPasien.getTable().getValueAt(cekRujukanPasien.getTable().getSelectedRow(), 4).toString());
                    kodePPK.setText(cekRujukanPasien.getTable().getValueAt(cekRujukanPasien.getTable().getSelectedRow(), 6).toString());
                    namaPPK.setText(cekRujukanPasien.getTable().getValueAt(cekRujukanPasien.getTable().getSelectedRow(), 7).toString());
                    valid.SetTgl(tglRujuk, cekRujukanPasien.getTable().getValueAt(cekRujukanPasien.getTable().getSelectedRow(), 5).toString());
                    catatan.requestFocus();
                }
            }
        });

        riwayatPelayananPasien.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (riwayatPelayananPasien.getTable().getSelectedRow() != -1) {
                    if ((riwayatPelayananPasien.getTable().getSelectedColumn() == 6) || (riwayatPelayananPasien.getTable().getSelectedColumn() == 7)) {
                        noRujukan.setText(riwayatPelayananPasien.getTable().getValueAt(riwayatPelayananPasien.getTable().getSelectedRow(), riwayatPelayananPasien.getTable().getSelectedColumn()).toString());
                    }
                }
                noRujukan.requestFocus();
            }
        });
        
        URUTNOREG = koneksiDB.URUTNOREG();
        BASENOREG = koneksiDB.BASENOREG();
        URLAPIBPJS = koneksiDB.URLAPIBPJS();
        URLFINGERPRINTBPJS = koneksiDB.URLFINGERPRINTBPJS();
        USERFINGERPRINTBPJS = koneksiDB.USERFINGERPRINTBPJS();
        PASSFINGERPRINTBPJS = koneksiDB.PASSFINGERPRINTBPJS();
        URLAPLIKASIFINGERPRINTBPJS = koneksiDB.URLAPLIKASIFINGERPRINTBPJS();

        kodePPKPelayanan.setText(query.cariIsi("select setting.kode_ppk from setting"));
        namaPPKPelayanan.setText(query.cariIsi("select setting.nama_instansi from setting"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        WindowAksi = new javax.swing.JDialog();
        internalFrame1 = new widget.InternalFrame();
        pwUserId = new widget.PasswordBox();
        pwPass = new widget.PasswordBox();
        btnAksiKonfirmasi = new widget.Button();
        btnAksiBatal = new widget.Button();
        label1 = new widget.Label();
        label2 = new widget.Label();
        label3 = new widget.Label();
        jPanel1 = new component.Panel();
        jPanel2 = new component.Panel();
        namaPasien = new widget.TextBox();
        noRM = new widget.TextBox();
        noKartu = new widget.TextBox();
        jLabel20 = new widget.Label();
        tglSEP = new widget.Tanggal();
        jLabel23 = new widget.Label();
        noRujukan = new widget.TextBox();
        jLabel8 = new widget.Label();
        tglLahir = new widget.TextBox();
        jLabel18 = new widget.Label();
        jk = new widget.TextBox();
        jLabel24 = new widget.Label();
        jenisPeserta = new widget.TextBox();
        jLabel25 = new widget.Label();
        statusPeserta = new widget.TextBox();
        jLabel27 = new widget.Label();
        asalRujukan = new widget.ComboBox();
        jLabel12 = new widget.Label();
        jLabel6 = new widget.Label();
        noSuratKontrol = new widget.TextBox();
        jLabel26 = new widget.Label();
        nik = new widget.TextBox();
        jLabel7 = new widget.Label();
        pilihRujukan = new widget.Button();
        jPanel3 = new javax.swing.JPanel();
        btnSimpan = new component.Button();
        btnKeluar = new component.Button();

        WindowAksi.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowAksi.setModal(true);
        WindowAksi.setUndecorated(true);
        WindowAksi.setResizable(false);

        internalFrame1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pwUserId.setText("passwordBox1");
        pwUserId.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        pwUserId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pwUserIdKeyPressed(evt);
            }
        });
        internalFrame1.add(pwUserId, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 230, 23));

        pwPass.setText("passwordBox2");
        pwPass.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        pwPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pwPassKeyPressed(evt);
            }
        });
        internalFrame1.add(pwPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 230, 23));

        btnAksiKonfirmasi.setText("Konfirmasi");
        btnAksiKonfirmasi.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAksiKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiKonfirmasiActionPerformed(evt);
            }
        });
        internalFrame1.add(btnAksiKonfirmasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, -1, -1));

        btnAksiBatal.setText("Batal");
        btnAksiBatal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAksiBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiBatalActionPerformed(evt);
            }
        });
        internalFrame1.add(btnAksiBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        label1.setText("User ID :");
        label1.setFocusable(false);
        label1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        internalFrame1.add(label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 120, 23));

        label2.setText("Password :");
        label2.setFocusable(false);
        label2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        internalFrame1.add(label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 120, 23));

        label3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label3.setText("Konfirmasi Aksi");
        label3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        internalFrame1.add(label3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 400, -1));

        WindowAksi.getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.BorderLayout(1, 1));

        jPanel1.setBackground(new java.awt.Color(238, 238, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(238, 238, 255), 1, true), "::[ Pengaturan APM ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 12), new java.awt.Color(0, 131, 62))); // NOI18N
        jPanel1.setMinimumSize(new java.awt.Dimension(543, 106));
        jPanel1.setPreferredSize(new java.awt.Dimension(543, 106));
        jPanel1.setLayout(new java.awt.BorderLayout(0, 1));

        jPanel2.setBackground(new java.awt.Color(238, 238, 255));
        jPanel2.setForeground(new java.awt.Color(0, 131, 62));
        jPanel2.setPreferredSize(new java.awt.Dimension(390, 120));
        jPanel2.setLayout(null);

        namaPasien.setEditable(false);
        namaPasien.setBackground(new java.awt.Color(245, 250, 240));
        namaPasien.setHighlighter(null);
        namaPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaPasienActionPerformed(evt);
            }
        });
        jPanel2.add(namaPasien);
        namaPasien.setBounds(340, 10, 230, 30);

        noRM.setHighlighter(null);
        noRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noRMActionPerformed(evt);
            }
        });
        jPanel2.add(noRM);
        noRM.setBounds(230, 10, 110, 30);

        noKartu.setEditable(false);
        noKartu.setBackground(new java.awt.Color(255, 255, 153));
        noKartu.setHighlighter(null);
        jPanel2.add(noKartu);
        noKartu.setBounds(730, 70, 300, 30);

        jLabel20.setForeground(new java.awt.Color(0, 131, 62));
        jLabel20.setText("Tgl. SEP :");
        jLabel20.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel20);
        jLabel20.setBounds(625, 130, 100, 30);

        tglSEP.setEditable(false);
        tglSEP.setForeground(new java.awt.Color(50, 70, 50));
        tglSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "07-04-2024" }));
        tglSEP.setDisplayFormat("dd-MM-yyyy");
        tglSEP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        tglSEP.setOpaque(false);
        tglSEP.setPreferredSize(new java.awt.Dimension(95, 25));
        tglSEP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglSEPKeyPressed(evt);
            }
        });
        jPanel2.add(tglSEP);
        tglSEP.setBounds(730, 130, 170, 30);

        jLabel23.setForeground(new java.awt.Color(0, 131, 62));
        jLabel23.setText("Lokasi Aplikasi Fingerprint BPJS :");
        jLabel23.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel23.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel23);
        jLabel23.setBounds(75, 70, 150, 30);

        noRujukan.setEditable(false);
        noRujukan.setBackground(new java.awt.Color(255, 255, 153));
        noRujukan.setHighlighter(null);
        noRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noRujukanKeyPressed(evt);
            }
        });
        jPanel2.add(noRujukan);
        noRujukan.setBounds(230, 100, 340, 30);

        jLabel8.setForeground(new java.awt.Color(0, 131, 62));
        jLabel8.setText("Printer Registrasi / SEP : ");
        jLabel8.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel8);
        jLabel8.setBounds(75, 10, 150, 30);

        tglLahir.setEditable(false);
        tglLahir.setBackground(new java.awt.Color(245, 250, 240));
        tglLahir.setHighlighter(null);
        jPanel2.add(tglLahir);
        tglLahir.setBounds(230, 40, 110, 30);

        jLabel18.setForeground(new java.awt.Color(0, 131, 62));
        jLabel18.setText("J.K :");
        jLabel18.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel18);
        jLabel18.setBounds(905, 10, 30, 30);

        jk.setEditable(false);
        jk.setBackground(new java.awt.Color(245, 250, 240));
        jk.setHighlighter(null);
        jPanel2.add(jk);
        jk.setBounds(940, 10, 90, 30);

        jLabel24.setForeground(new java.awt.Color(0, 131, 62));
        jLabel24.setText("Peserta :");
        jLabel24.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel24.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel24);
        jLabel24.setBounds(75, 130, 150, 30);

        jenisPeserta.setEditable(false);
        jenisPeserta.setBackground(new java.awt.Color(245, 250, 240));
        jenisPeserta.setHighlighter(null);
        jPanel2.add(jenisPeserta);
        jenisPeserta.setBounds(230, 130, 340, 30);

        jLabel25.setForeground(new java.awt.Color(0, 131, 62));
        jLabel25.setText("Status :");
        jLabel25.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel25);
        jLabel25.setBounds(370, 40, 50, 30);

        statusPeserta.setEditable(false);
        statusPeserta.setBackground(new java.awt.Color(245, 250, 240));
        statusPeserta.setHighlighter(null);
        jPanel2.add(statusPeserta);
        statusPeserta.setBounds(420, 40, 150, 30);

        jLabel27.setForeground(new java.awt.Color(0, 131, 62));
        jLabel27.setText("Asal Rujukan :");
        jLabel27.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel27);
        jLabel27.setBounds(625, 100, 100, 30);

        asalRujukan.setForeground(new java.awt.Color(0, 131, 62));
        asalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        asalRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        asalRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                asalRujukanKeyPressed(evt);
            }
        });
        jPanel2.add(asalRujukan);
        asalRujukan.setBounds(730, 100, 170, 30);

        jLabel12.setForeground(new java.awt.Color(0, 131, 62));
        jLabel12.setText("Printer Barcode :");
        jLabel12.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel12);
        jLabel12.setBounds(75, 40, 150, 30);

        jLabel6.setForeground(new java.awt.Color(0, 131, 62));
        jLabel6.setText("NIK :");
        jLabel6.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel6);
        jLabel6.setBounds(625, 40, 100, 30);

        noSuratKontrol.setEditable(false);
        noSuratKontrol.setBackground(new java.awt.Color(255, 255, 153));
        noSuratKontrol.setHighlighter(null);
        noSuratKontrol.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noSuratKontrolKeyPressed(evt);
            }
        });
        jPanel2.add(noSuratKontrol);
        noSuratKontrol.setBounds(230, 70, 340, 30);

        jLabel26.setForeground(new java.awt.Color(0, 131, 62));
        jLabel26.setText("Username FP BPJS :");
        jLabel26.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel26);
        jLabel26.setBounds(75, 100, 150, 30);

        nik.setEditable(false);
        nik.setBackground(new java.awt.Color(255, 255, 153));
        nik.setHighlighter(null);
        jPanel2.add(nik);
        nik.setBounds(730, 40, 300, 30);

        jLabel7.setForeground(new java.awt.Color(0, 131, 62));
        jLabel7.setText("No. Kartu :");
        jLabel7.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel7);
        jLabel7.setBounds(625, 70, 100, 30);

        pilihRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        pilihRujukan.setMnemonic('X');
        pilihRujukan.setToolTipText("Alt+X");
        pilihRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pilihRujukan.setGlassColor(new java.awt.Color(238, 238, 255));
        pilihRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihRujukanActionPerformed(evt);
            }
        });
        pilihRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pilihRujukanKeyPressed(evt);
            }
        });
        jPanel2.add(pilihRujukan);
        pilihRujukan.setBounds(570, 100, 40, 30);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(238, 238, 255));
        jPanel3.setMinimumSize(new java.awt.Dimension(533, 120));
        jPanel3.setPreferredSize(new java.awt.Dimension(533, 120));

        btnSimpan.setForeground(new java.awt.Color(0, 131, 62));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        btnSimpan.setMnemonic('S');
        btnSimpan.setText("Simpan");
        btnSimpan.setToolTipText("Alt+S");
        btnSimpan.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnSimpan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSimpan.setPreferredSize(new java.awt.Dimension(300, 45));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        btnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnSimpanKeyPressed(evt);
            }
        });
        jPanel3.add(btnSimpan);

        btnKeluar.setForeground(new java.awt.Color(0, 131, 62));
        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/reset.png"))); // NOI18N
        btnKeluar.setMnemonic('K');
        btnKeluar.setText("Batal");
        btnKeluar.setToolTipText("Alt+K");
        btnKeluar.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnKeluar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnKeluar.setPreferredSize(new java.awt.Dimension(300, 45));
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        btnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnKeluarKeyPressed(evt);
            }
        });
        jPanel3.add(btnKeluar);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void btnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnKeluarActionPerformed(null);
        }
    }//GEN-LAST:event_btnKeluarKeyPressed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSimpanActionPerformed(null);
        }
    }//GEN-LAST:event_btnSimpanKeyPressed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cekFinger(noKartu.getText());
        if (TNoRw.getText().trim().equals("") || namaPasien.getText().trim().equals("")) {
            valid.textKosong(TNoRw, "Pasien");
        } else if (noKartu.getText().trim().equals("")) {
            valid.textKosong(noKartu, "Nomor Kartu");
        } else if (query.cariIntegerSmc("select count(*) from pasien where no_rkm_medis = ?", noRM.getText()) < 1) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, no RM tidak sesuai");
        } else if (kodePPK.getText().trim().equals("") || namaPPK.getText().trim().equals("")) {
            valid.textKosong(kodePPK, "PPK Rujukan");
        } else if (kodePPKPelayanan.getText().trim().equals("") || namaPPKPelayanan.getText().trim().equals("")) {
            valid.textKosong(kodePPKPelayanan, "PPK Pelayanan");
        } else if (kodeDiagnosa.getText().trim().equals("") || namaDiagnosa.getText().trim().equals("")) {
            valid.textKosong(kodeDiagnosa, "Diagnosa");
        } else if (catatan.getText().trim().equals("")) {
            valid.textKosong(catatan, "Catatan");
        } else if ((jenisPelayanan.getSelectedIndex() == 1) && (kodePoli.getText().trim().equals("") || namaPoli.getText().trim().equals(""))) {
            valid.textKosong(kodePoli, "Poli Tujuan");
        } else if ((lakaLantas.getSelectedIndex() == 1) && keterangan.getText().equals("")) {
            valid.textKosong(keterangan, "Keterangan");
        } else if (kodeDokter.getText().trim().equals("") || namaDokter.getText().trim().equals("")) {
            valid.textKosong(kodeDokter, "DPJP");
        } else if (! statusfinger && query.cariIntegerSmc("select timestampdiff(year, ?, CURRENT_DATE())", tglLahir.getText()) >= 17 && jenisPelayanan.getSelectedIndex() != 0 && !kodePoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Pasien belum melakukan Fingerprint");
            bukaAplikasiFingerprint();
        } else {
            if (!KdPoliTerapi.getText().equals("")) {
                kodepolireg = KdPoliTerapi.getText();
            } else {
                kodepolireg = query.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText());
            }

            if (!KodeDokterTerapi.getText().equals("")) {
                kodedokterreg = KodeDokterTerapi.getText();
            } else {
                kodedokterreg = query.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter.getText());
            }

            isPoli();
            isCekPasien();
            isNumber();

            // cek apabila pasien sudah pernah diregistrasikan sebelumnya
            if (query.cariIntegerSmc("select count(*) from reg_periksa where no_rkm_medis = ? and tgl_registrasi = ? and kd_poli = ? and kd_dokter = ? and kd_pj = ?", noRM.getText(), valid.SetTgl(tglSEP.getSelectedItem().toString()), kodepolireg, kodedokterreg, Kdpnj.getText()) > 0) {
                JOptionPane.showMessageDialog(rootPane, "Maaf, Telah terdaftar pemeriksaan hari ini. Mohon konfirmasi ke Bagian Admisi");
                emptTeks();
            } else {
                if (! registerPasien()) {
                    JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan pada saat pendaftaran pasien!");
                    this.setCursor(Cursor.getDefaultCursor());

                    return;
                }
                
                if (jenisPelayanan.getSelectedIndex() == 0) {
                    insertSEP();
                } else if (jenisPelayanan.getSelectedIndex() == 1) {
                    if (namaPoli.getText().toLowerCase().contains("darurat")) {
                        if (query.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan like '%darurat%'", no_peserta, jenisPelayanan.getSelectedItem().toString().substring(0, 1), valid.SetTgl(tglSEP.getSelectedItem().toString())) >= 3) {
                            JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                        } else {
                            if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                                SimpanAntrianOnSite();
                            }
                            insertSEP();
                        }
                    } else if (!namaPoli.getText().toLowerCase().contains("darurat")) {
                        if (query.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan not like '%darurat%'", no_peserta, jenisPelayanan.getSelectedItem().toString().substring(0, 1), valid.SetTgl(tglSEP.getSelectedItem().toString())) >= 1) {
                            JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                        } else {
                            if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                                SimpanAntrianOnSite();
                            }
                            insertSEP();
                        }
                    }
                }
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void asalRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_asalRujukanKeyPressed

    }//GEN-LAST:event_asalRujukanKeyPressed

    private void noRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noRujukanKeyPressed

    }//GEN-LAST:event_noRujukanKeyPressed

    private void tglSEPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglSEPKeyPressed
        valid.pindah(evt, tglRujuk, asalRujukan);
    }//GEN-LAST:event_tglSEPKeyPressed

    private void noRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noRMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noRMActionPerformed

    private void noSuratKontrolKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noSuratKontrolKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_noSuratKontrolKeyPressed

    private void pilihRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihRujukanActionPerformed
        if (noKartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "No.Kartu masih kosong...!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            cekRujukanPasien.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
            cekRujukanPasien.setLocationRelativeTo(jPanel1);
            cekRujukanPasien.tampil(noKartu.getText(), namaPasien.getText());
            cekRujukanPasien.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_pilihRujukanActionPerformed

    private void pilihRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pilihRujukanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pilihRujukanKeyPressed

    private void btnAksiKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiKonfirmasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (! noKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        } else {
            try {
                ps = koneksi.prepareStatement("select id_user from user where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi') limit 1");
                try {
                    ps.setString(1, new String(pwUserId.getPassword()));
                    ps.setString(2, new String(pwPass.getPassword()));
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        if (aksi.equals("Pengajuan")) {
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                utc = String.valueOf(api.GetUTCdatetimeAsString());
                                headers.add("X-Timestamp", utc);
                                headers.add("X-Signature", api.getHmac(utc));
                                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                URL = URLAPIBPJS + "/Sep/pengajuanSEP";
                                requestJson = " {"
                                    + "\"request\": {"
                                    + "\"t_sep\": {"
                                    + "\"noKartu\": \"" + noKartu.getText() + "\","
                                    + "\"tglSep\": \"" + valid.SetTgl(tglSEP.getSelectedItem() + "") + "\","
                                    + "\"jnsPelayanan\": \"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                                    + "\"jnsPengajuan\": \"2\","
                                    + "\"keterangan\": \"Pengajuan SEP Finger oleh Anjungan Pasien Mandiri RS Samarinda Medika Citra\","
                                    + "\"user\": \"NoRM:" + noRM.getText() + "\""
                                    + "}"
                                    + "}"
                                    + "}";
                                requestEntity = new HttpEntity(requestJson, headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                nameNode = root.path("metaData");
                                System.out.println("code : " + nameNode.path("code").asText());
                                System.out.println("message : " + nameNode.path("message").asText());
                                if (nameNode.path("code").asText().equals("200")) {
                                    JOptionPane.showMessageDialog(rootPane, "Pengajuan Berhasil");
                                } else {
                                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                }
                            } catch (Exception ex) {
                                System.out.println("Notifikasi Bridging : " + ex);
                                if (ex.toString().contains("UnknownHostException")) {
                                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                }
                            }
                        } else if (aksi.equals("Approval")) {
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                utc = String.valueOf(api.GetUTCdatetimeAsString());
                                headers.add("X-Timestamp", utc);
                                headers.add("X-Signature", api.getHmac(utc));
                                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                URL = URLAPIBPJS + "/Sep/aprovalSEP";
                                requestJson = " {"
                                    + "\"request\": {"
                                    + "\"t_sep\": {"
                                    + "\"noKartu\": \"" + noKartu.getText() + "\","
                                    + "\"tglSep\": \"" + valid.SetTgl(tglSEP.getSelectedItem() + "") + "\","
                                    + "\"jnsPelayanan\": \"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                                    + "\"jnsPengajuan\": \"2\","
                                    + "\"keterangan\": \"Approval FingerPrint karena Gagal FP melalui Anjungan Pasien Mandiri\","
                                    + "\"user\": \"NoRM:" + noRM.getText() + "\""
                                    + "}"
                                    + "}"
                                    + "}";
                                requestEntity = new HttpEntity(requestJson, headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                nameNode = root.path("metaData");
                                System.out.println("code : " + nameNode.path("code").asText());
                                System.out.println("message : " + nameNode.path("message").asText());
                                if (nameNode.path("code").asText().equals("200")) {
                                    JOptionPane.showMessageDialog(rootPane, "Arpoval Berhasil");
                                } else {
                                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                }
                            } catch (Exception ex) {
                                System.out.println("Notifikasi Bridging : " + ex);
                                if (ex.toString().contains("UnknownHostException")) {
                                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Anda tidak diizinkan untuk melakukan aksi ini...!!!");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan pada saat memproses aksi...!!!");
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnAksiKonfirmasiActionPerformed

    private void pwUserIdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pwUserIdKeyPressed
        valid.pindah(evt, btnAksiBatal, pwPass);
    }//GEN-LAST:event_pwUserIdKeyPressed

    private void pwPassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pwPassKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnAksiKonfirmasiActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            pwUserId.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            btnAksiKonfirmasi.requestFocus();
        }
    }//GEN-LAST:event_pwPassKeyPressed

    private void btnAksiBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiBatalActionPerformed
        pwUserId.setText("");
        pwPass.setText("");
        WindowAksi.dispose();
    }//GEN-LAST:event_btnAksiBatalActionPerformed

    private void namaPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaPasienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaPasienActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPengaturanAPM dialog = new DlgPengaturanAPM(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog WindowAksi;
    private widget.ComboBox asalRujukan;
    private widget.Button btnAksiBatal;
    private widget.Button btnAksiKonfirmasi;
    private component.Button btnKeluar;
    private component.Button btnSimpan;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel12;
    private widget.Label jLabel18;
    private widget.Label jLabel20;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private component.Panel jPanel1;
    private component.Panel jPanel2;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox jenisPeserta;
    private widget.TextBox jk;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.TextBox namaPasien;
    private widget.TextBox nik;
    private widget.TextBox noKartu;
    private widget.TextBox noRM;
    private widget.TextBox noRujukan;
    private widget.TextBox noSuratKontrol;
    private widget.Button pilihRujukan;
    private widget.PasswordBox pwPass;
    private widget.PasswordBox pwUserId;
    private widget.TextBox statusPeserta;
    private widget.TextBox tglLahir;
    private widget.Tanggal tglSEP;
    // End of variables declaration//GEN-END:variables

    private void isNumber() {
        switch (URUTNOREG) {
            case "poli":
                NoReg.setText(query.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and tgl_registrasi = ?",
                        kodepolireg, valid.SetTgl(tglSEP.getSelectedItem().toString())
                    )
                );
                break;
            case "dokter":
                NoReg.setText(query.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and tgl_registrasi = ?",
                        kodedokterreg, valid.SetTgl(tglSEP.getSelectedItem().toString())
                    )
                );
                break;
            case "dokter + poli":
                NoReg.setText(query.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                        kodepolireg, kodedokterreg, valid.SetTgl(tglSEP.getSelectedItem().toString())
                    )
                );
                break;
            default:
                NoReg.setText(query.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                        kodepolireg, kodedokterreg, valid.SetTgl(tglSEP.getSelectedItem().toString())
                    )
                );
                break;
        }
        
        TNoRw.setText(query.cariIsiSmc("select concat(date_format(tgl_registrasi, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_rawat, 6), signed)), 0) + 1, 6, '0')) from reg_periksa where tgl_registrasi = ?",
                valid.SetTgl(tglSEP.getSelectedItem().toString())
            )
        );
    }

    private void tentukanHari() {
        try {
            java.sql.Date hariperiksa = java.sql.Date.valueOf(valid.SetTgl(tglSEP.getSelectedItem().toString()));
            cal.setTime(hariperiksa);
            day = cal.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case 1:
                    hari = "AKHAD";
                    break;
                case 2:
                    hari = "SENIN";
                    break;
                case 3:
                    hari = "SELASA";
                    break;
                case 4:
                    hari = "RABU";
                    break;
                case 5:
                    hari = "KAMIS";
                    break;
                case 6:
                    hari = "JUMAT";
                    break;
                case 7:
                    hari = "SABTU";
                    break;
                default:
                    break;
            }
            System.out.println(hari);

        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

    }

    private void isCekPasien() {
        try {
            ps3 = koneksi.prepareStatement("select nm_pasien,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) asal,"
                    + "namakeluarga,keluarga,pasien.kd_pj,penjab.png_jawab,if(tgl_daftar=?,'Baru','Lama') as daftar, "
                    + "TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun,pasien.no_peserta, "
                    + "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, "
                    + "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari,pasien.no_ktp,pasien.no_tlp "
                    + "from pasien inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel "
                    + "inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                    + "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab "
                    + "inner join penjab on pasien.kd_pj=penjab.kd_pj "
                    + "where pasien.no_rkm_medis=?");
            try {
                ps3.setString(1, valid.SetTgl(tglSEP.getSelectedItem() + ""));
                ps3.setString(2, noRM.getText());
                rs = ps3.executeQuery();
                while (rs.next()) {
                    TAlmt.setText(rs.getString("asal"));
                    TPngJwb.setText(rs.getString("namakeluarga"));
                    THbngn.setText(rs.getString("keluarga"));
                    NoTelpPasien.setText(rs.getString("no_tlp"));
                    umur = "0";
                    sttsumur = "Th";
                    statuspasien = rs.getString("daftar");
                    if (rs.getInt("tahun") > 0) {
                        umur = rs.getString("tahun");
                        sttsumur = "Th";
                    } else if (rs.getInt("tahun") == 0) {
                        if (rs.getInt("bulan") > 0) {
                            umur = rs.getString("bulan");
                            sttsumur = "Bl";
                        } else if (rs.getInt("bulan") == 0) {
                            umur = rs.getString("hari");
                            sttsumur = "Hr";
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            } finally {
                if (rs != null) {
                    rs.close();
                }

                if (ps3 != null) {
                    ps3.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        status = "Baru";
        if (query.cariInteger("select count(*) from reg_periksa where no_rkm_medis = ? and kd_poli = ?", noRM.getText(), kodepolireg) > 0) {
            status = "Lama";
        }

    }

    private void cetakRegistrasi(String noSEP) {
        Map<String, Object> param = new HashMap<>();
        param.put("norawat", TNoRw.getText());
        param.put("parameter", noSEP);
        param.put("namars", query.cariIsi("select setting.nama_instansi from setting limit 1"));
        param.put("kotars", query.cariIsi("select setting.kabupaten from setting limit 1"));
        
        if (jenisPelayanan.getSelectedIndex() == 0) {
            valid.printReport("rptBridgingSEPAPM1.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            valid.MyReport("rptBridgingSEPAPM1.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        } else {
            valid.printReport("rptBridgingSEPAPM2.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            valid.MyReport("rptBridgingSEPAPM2.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        }
        
        valid.printReport("rptBarcodeRawatAPM.jasper", koneksiDB.PRINTER_BARCODE(), "::[ Barcode Perawatan ]::", 3, param);
        valid.MyReport("rptBarcodeRawatAPM.jasper", "report", "::[ Barcode Perawatan ]::", param);
    }

    private void insertSEP() {
        try {
            tglkkl = "0000-00-00";
            if (lakaLantas.getSelectedIndex() > 0) {
                tglkkl = valid.SetTgl(tglKLL.getSelectedItem() + "");
            }
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            
            URL = URLAPIBPJS + "/SEP/2.0/insert";
            requestJson = "{"
                + "\"request\":{"
                    + "\"t_sep\":{"
                        + "\"noKartu\":\"" + noKartu.getText() + "\","
                        + "\"tglSep\":\"" + valid.SetTgl(tglSEP.getSelectedItem() + "") + "\","
                        + "\"ppkPelayanan\":\"" + kodePPKPelayanan.getText() + "\","
                        + "\"jnsPelayanan\":\"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                        + "\"klsRawat\":{"
                            + "\"klsRawatHak\":\"" + kelas.getSelectedItem().toString().substring(0, 1) + "\","
                            + "\"klsRawatNaik\":\"\","
                            + "\"pembiayaan\":\"\","
                            + "\"penanggungJawab\":\"\""
                        + "},"
                        + "\"noMR\":\"" + noRM.getText() + "\","
                        + "\"rujukan\": {"
                            + "\"asalRujukan\":\"" + asalRujukan.getSelectedItem().toString().substring(0, 1) + "\","
                            + "\"tglRujukan\":\"" + valid.SetTgl(tglRujuk.getSelectedItem() + "") + "\","
                            + "\"noRujukan\":\"" + noRujukan.getText() + "\","
                            + "\"ppkRujukan\":\"" + kodePPK.getText() + "\""
                        + "},"
                        + "\"catatan\":\"" + catatan.getText() + "\","
                        + "\"diagAwal\":\"" + kodeDiagnosa.getText() + "\","
                        + "\"poli\": {"
                            + "\"tujuan\": \"" + kodePoli.getText() + "\","
                            + "\"eksekutif\": \"0\""
                        + "},"
                        + "\"cob\": {"
                            + "\"cob\": \"0\""
                        + "},"
                        + "\"katarak\": {"
                            + "\"katarak\": \"" + katarak.getSelectedItem().toString().substring(0, 1) + "\""
                        + "},"
                        + "\"jaminan\": {"
                            + "\"lakaLantas\":\"" + lakaLantas.getSelectedItem().toString().substring(0, 1) + "\","
                            + "\"penjamin\": {"
                                + "\"tglKejadian\": \"" + tglkkl.replaceAll("0000-00-00", "") + "\","
                                + "\"keterangan\": \"" + keterangan.getText() + "\","
                                + "\"suplesi\": {"
                                    + "\"suplesi\": \"" + suplesi.getSelectedItem().toString().substring(0, 1) + "\","
                                    + "\"noSepSuplesi\": \"" + noSEPSuplesi.getText() + "\","
                                    + "\"lokasiLaka\": {"
                                        + "\"kdPropinsi\": \"" + kodeProvKLL.getText() + "\","
                                        + "\"kdKabupaten\": \"" + kodeKabKLL.getText() + "\","
                                        + "\"kdKecamatan\": \"" + kodeKecKLL.getText() + "\""
                                    + "}"
                                + "}"
                            + "}"
                        + "},"
                        + "\"tujuanKunj\": \"" + tujuanKunjungan.getSelectedItem().toString().substring(0, 1) + "\","
                        + "\"flagProcedure\": \"" + (flagProsedur.getSelectedIndex() > 0 ? flagProsedur.getSelectedItem().toString().substring(0, 1) : "") + "\","
                        + "\"kdPenunjang\": \"" + (penunjang.getSelectedIndex() > 0 ? penunjang.getSelectedIndex() + "" : "") + "\","
                        + "\"assesmentPel\": \"" + (asesmenPelayanan.getSelectedIndex() > 0 ? asesmenPelayanan.getSelectedItem().toString().substring(0, 1) : "") + "\","
                        + "\"skdp\": {"
                            + "\"noSurat\": \"" + noSuratKontrol.getText() + "\","
                            + "\"kodeDPJP\": \"" + kodeDokter.getText() + "\""
                        + "},"
                        + "\"dpjpLayan\": \"" + (kodeDokterLayanan.getText().equals("") ? "" : kodeDokterLayanan.getText()) + "\","
                        + "\"noTelp\": \"" + noTelp.getText() + "\","
                        + "\"user\":\"" + noKartu.getText() + "\""
                    + "}"
                + "}"
            + "}";
            
            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            
            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());
            JOptionPane.showMessageDialog(rootPane, "Respon BPJS : " + nameNode.path("message").asText());
            
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep");
                System.out.println("SEP berhasil terbit!");
                System.out.println("No. SEP: " + response.asText());
                
                String isNoRawat = query.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ?", valid.SetTgl(tglSEP.getSelectedItem().toString()), noRM.getText(), kodepolireg, kodedokterreg);
                
                if (isNoRawat == null || (! isNoRawat.equals(TNoRw.getText()))) {
                    System.out.println("======================================================");
                    System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
                    System.out.println("No. Rawat: " + TNoRw.getText());
                    System.out.println("Tgl. Registrasi: " + valid.SetTgl(tglSEP.getSelectedItem().toString()));
                    System.out.println("No. Antrian: " + NoReg.getText() + " (Ditemukan: " + query.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("No. RM: " + noRM.getText() + " (Ditemukan: " + query.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("Kode Dokter: " + kodedokterreg + " (Ditemukan: " + query.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("Kode Poli: " + kodepolireg  + " (Ditemukan: " + query.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("======================================================");

                    return;
                }
                
                query.menyimpanSmc("bridging_sep", null,
                    response.asText(),
                    TNoRw.getText(),
                    valid.SetTgl(tglSEP.getSelectedItem().toString()),
                    valid.SetTgl(tglRujuk.getSelectedItem().toString()),
                    noRujukan.getText(),
                    kodePPK.getText(),
                    namaPPK.getText(),
                    kodePPKPelayanan.getText(),
                    namaPPKPelayanan.getText(),
                    jenisPelayanan.getSelectedItem().toString().substring(0, 1),
                    catatan.getText(),
                    kodeDiagnosa.getText(),
                    namaDiagnosa.getText(),
                    kodePoli.getText(),
                    namaPoli.getText(),
                    kelas.getSelectedItem().toString().substring(0, 1),
                    "",
                    "",
                    "",
                    lakaLantas.getSelectedItem().toString().substring(0, 1),
                    noRM.getText(),
                    noRM.getText(),
                    namaPasien.getText(),
                    tglLahir.getText(),
                    jenisPeserta.getText(),
                    jk.getText(),
                    noKartu.getText(),
                    "0000-00-00 00:00:00",
                    asalRujukan.getSelectedItem().toString(),
                    "0. Tidak",
                    "0. Tidak",
                    noTelp.getText(),
                    katarak.getSelectedItem().toString(),
                    tglkkl,
                    keterangan.getText(),
                    suplesi.getSelectedItem().toString(),
                    noSEPSuplesi.getText(),
                    kodeProvKLL.getText(),
                    namaProvKLL.getText(),
                    kodeKabKLL.getText(),
                    namaKabKLL.getText(),
                    kodeKecKLL.getText(),
                    namaKecKLL.getText(),
                    noSuratKontrol.getText(),
                    kodeDokter.getText(),
                    namaDokter.getText(),
                    tujuanKunjungan.getSelectedItem().toString().substring(0, 1),
                    (flagProsedur.getSelectedIndex() > 0 ? flagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
                    (penunjang.getSelectedIndex() > 0 ? String.valueOf(penunjang.getSelectedIndex()) : ""),
                    (asesmenPelayanan.getSelectedIndex() > 0 ? asesmenPelayanan.getSelectedItem().toString().substring(0, 1) : ""),
                    kodeDokterLayanan.getText(),
                    namaDokterLayanan.getText()
                );
                
                if (! simpanRujukan()) {
                    System.out.println("Terjadi kesalahan pada saat proses rujukan masuk pasien!");
                }
                
                
                if (jenisPelayanan.getSelectedIndex() == 1) {
                    query.mengupdateSmc("bridging_sep", "tglpulang = ?", "no_sep = ?", valid.SetTgl(tglSEP.getSelectedItem().toString()), response.asText());
                }

                if (! prb.equals("")) {
                    query.menyimpanSmc("bpjs_prb", null, response.asText(), prb);
                    
                    prb = "";
                }
                
                if (query.cariIntegerSmc("select count(*) from booking_registrasi where no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ? and status != 'Terdaftar'",
                    noRM.getText(), valid.SetTgl(tglSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
                ) == 1) {
                    query.mengupdateSmc("booking_registrasi", "status = 'Terdaftar', waktu_kunjungan = now()", "no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ?", noRM.getText(), valid.SetTgl(tglSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg);
                }

                cetakRegistrasi(response.asText());

                emptTeks();
                dispose();
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private void cekFinger(String noka) {
        statusfinger = false;

        if (!noKartu.getText().equals("")) {
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                URL = URLAPIBPJS + "/SEP/FingerPrint/Peserta/" + noka + "/TglPelayanan/" + valid.SetTgl(tglSEP.getSelectedItem().toString());
                requestEntity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                nameNode = root.path("metaData");
                System.out.println("kodecekstatus : " + nameNode.path("code").asText());
                // System.out.println("message : "+nameNode.path("message").asText());
                if (nameNode.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                    if (response.path("kode").asText().equals("1")) {
                        if (response.path("status").asText().contains(query.cariIsi("select current_date()"))) {
                            statusfinger = true;
                        } else {
                            statusfinger = false; 
                           JOptionPane.showMessageDialog(rootPane, response.path("status").asText());
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(rootPane, response.path("status").asText());
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi Bridging : " + ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih data peserta!");
        }
    }

    public void tampilKunjunganPertama(String noRujukan) {
        try {
            URL = URLAPIBPJS + "/Rujukan/Peserta/" + noRujukan;
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("URL : " + URL);
            peserta = "";
            
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                noRujukan.setText(response.path("noKunjungan").asText());
                if (response.path("peserta").path("hakKelas").path("kode").asText().equals("1")) {
                    kelas.setSelectedIndex(0);
                } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("2")) {
                    kelas.setSelectedIndex(1);
                } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("3")) {
                    kelas.setSelectedIndex(2);
                }
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                namaPasien.setText(response.path("peserta").path("nama").asText());
                noKartu.setText(response.path("peserta").path("noKartu").asText());
                noRM.setText(query.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", noKartu.getText()));
                nik.setText(query.cariIsiSmc("select pasien.no_ktp from pasien where pasien.no_rkm_medis = ?", noRM.getText()));
                jk.setText(response.path("peserta").path("sex").asText());
                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kdpoli.setText(query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText()));
                kodepolireg = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter.getText());
                isPoli();
                kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                valid.SetTgl(tglRujuk, response.path("tglKunjungan").asText());
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                noTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (noTelp.getText().equals("null") || noTelp.getText().isBlank()) {
                    noTelp.setText(query.cariIsiSmc("select pasien.no_tlp from pasien where pasien.no_rkm_medis = ?", noRM.getText()));
                }

                KdPoliTerapi.setText("");
                NmPoliTerapi.setText("");
                KodeDokterTerapi.setText("");
                NmDokterTerapi.setText("");
                KdPoliTerapi.setVisible(false);
                NmPoliTerapi.setVisible(false);
                KodeDokterTerapi.setVisible(false);
                NmDokterTerapi.setVisible(false);
                pilihPoliTerapi.setVisible(false);
                pilihDokterTerapi.setVisible(false);
                lblTerapi.setVisible(false);

                ps = koneksi.prepareStatement("select maping_dokter_dpjpvclaim.kd_dokter, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal on maping_dokter_dpjpvclaim.kd_dokter = jadwal.kd_dokter where jadwal.kd_poli = ? and jadwal.hari_kerja = ?");
                
                try {
                    switch (day) {
                        case 1:
                            hari = "AKHAD";
                            break;
                        case 2:
                            hari = "SENIN";
                            break;
                        case 3:
                            hari = "SELASA";
                            break;
                        case 4:
                            hari = "RABU";
                            break;
                        case 5:
                            hari = "KAMIS";
                            break;
                        case 6:
                            hari = "JUMAT";
                            break;
                        case 7:
                            hari = "SABTU";
                            break;
                        default:
                            break;
                    }

                    ps.setString(1, kdpoli.getText());
                    ps.setString(2, hari);
                    
                    rs = ps.executeQuery();
                    
                    if (rs.next()) {
                        kodeDokter.setText(rs.getString("kd_dokter_bpjs"));
                        namaDokter.setText(rs.getString("nm_dokter_bpjs"));
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
            } else {
                System.out.println("Pesan pencarian rujukan FKTP : " + nameNode.path("message").asText());
                JOptionPane.showMessageDialog(rootPane, "Pesan Pencarian Rujukan FKTP : " + nameNode.path("message").asText());
                tampilRujukanRS(noRujukan);
//                emptTeks();
//                dispose();

            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }

//        btnFingerPrintActionPerformed(null);
    }

    public void tampilKunjunganBedaPoli(String noRujukan) {
        try {
            URL = URLAPIBPJS + "/Rujukan/Peserta/" + noRujukan;
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("URL : " + URL);
            peserta = "";
            
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                noRujukan.setText(response.path("noKunjungan").asText());
                if (response.path("peserta").path("hakKelas").path("kode").asText().equals("1")) {
                    kelas.setSelectedIndex(0);
                } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("2")) {
                    kelas.setSelectedIndex(1);
                } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("3")) {
                    kelas.setSelectedIndex(2);
                }
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                namaPasien.setText(response.path("peserta").path("nama").asText());
                noKartu.setText(response.path("peserta").path("noKartu").asText());
                noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", noKartu.getText()));
                nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                jk.setText(response.path("peserta").path("sex").asText());
                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                asesmenPelayanan.setSelectedIndex(1);
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kdpoli.setText(query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText()));
                kodepolireg = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter.getText());
                kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                valid.SetTgl(tglRujuk, response.path("tglKunjungan").asText());
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                noTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (noTelp.getText().equals("null") || noTelp.getText().isBlank()) {
                    noTelp.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                }

                KdPoliTerapi.setText("");
                NmPoliTerapi.setText("");
                KodeDokterTerapi.setText("");
                NmDokterTerapi.setText("");
                KdPoliTerapi.setVisible(true);
                NmPoliTerapi.setVisible(true);
                KodeDokterTerapi.setVisible(true);
                NmDokterTerapi.setVisible(true);
                pilihPoliTerapi.setVisible(true);
                pilihDokterTerapi.setVisible(true);
                lblTerapi.setVisible(true);

            } else {
//                emptTeks();
//                dispose();
                System.out.println("Pesan pencarian rujukan FKTP : " + nameNode.path("message").asText());
                JOptionPane.showMessageDialog(rootPane, "Pesan Pencarian Rujukan FKTP : " + nameNode.path("message").asText());
                tampilRujukanRS(noRujukan);
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }

//        btnFingerPrintActionPerformed(null);
    }

    public void tampilKontrol(String noSKDP) {
        String noSEP = query.cariIsiSmc("select no_sep from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP);
        String noKartuPeserta = query.cariIsiSmc("select no_kartu from bridging_sep where no_sep = ?", noSEP);
        String tglRencanaKontrol = query.cariIsiSmc("select tgl_rencana from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP);
        String jenisKontrol = query.cariIsiSmc("select jnspelayanan from bridging_sep where no_sep = ?", noSEP);
        String asalFaskes = query.cariIsiSmc("select left(asal_rujukan, 1) from bridging_sep where no_sep = ?", noSEP);
        
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        
        String tanggal = format.format(today);

        if (! tglRencanaKontrol.equals(tanggal)) {
            updateSuratKontrol(noSKDP, noSEP, tanggal, noKartuPeserta);
        }

        if (jenisKontrol.equals("1")) {
            // kondisi post ranap
            try {
                URL = URLAPIBPJS + "/Peserta/nokartu/" + noKartuPeserta + "/tglSEP/" + valid.SetTgl(tglSEP.getSelectedItem().toString());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                
                requestEntity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                nameNode = root.path("metaData");
                System.out.println("URL : " + URL);
                peserta = "";
                
                if (nameNode.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("peserta");
                    kodeDiagnosa.setText("Z09.8");
                    namaDiagnosa.setText("Z09.8 - Follow-up examination after other treatment for other conditions");
                    noRujukan.setText(noSEP);
                    tujuanKunjungan.setSelectedIndex(0);
                    flagProsedur.setSelectedIndex(0);
                    penunjang.setSelectedIndex(0);
                    asesmenPelayanan.setSelectedIndex(0);
                    asalRujukan.setSelectedIndex(1);
                    kodePoli.setText(query.cariIsiSmc("select kd_poli_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    namaPoli.setText(query.cariIsiSmc("select nm_poli_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    kodeDokter.setText(query.cariIsiSmc("select kd_dokter_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    namaDokter.setText(query.cariIsiSmc("select nm_dokter_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    kodeDokterLayanan.setText(kodeDokter.getText());
                    namaDokterLayanan.setText(namaDokter.getText());
                    kdpoli.setText(query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText()));
                    kodepolireg = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText());
                    kodedokterreg = query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter.getText());
                    noSuratKontrol.setText(noSKDP);
                    if (response.path("peserta").path("hakKelas").path("kode").asText().equals("1")) {
                        kelas.setSelectedIndex(0);
                    } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("2")) {
                        kelas.setSelectedIndex(1);
                    } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("3")) {
                        kelas.setSelectedIndex(2);
                    }
                    prb = "";
                    peserta = response.path("jenisPeserta").path("keterangan").asText();
                    namaPasien.setText(response.path("nama").asText());
                    noKartu.setText(response.path("noKartu").asText());
                    noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", noKartu.getText()));
                    nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                    jk.setText(response.path("sex").asText());
                    statusPeserta.setText(response.path("statusPeserta").path("kode").asText() + " " + response.path("statusPeserta").path("keterangan").asText());
                    tglLahir.setText(response.path("tglLahir").asText());
                    jenisPeserta.setText(response.path("jenisPeserta").path("keterangan").asText());
                    kodePPK.setText(query.cariIsiSmc("select kode_ppk from setting"));
                    namaPPK.setText(query.cariIsiSmc("select nama_instansi from setting"));
                    isNumber();
                    Kdpnj.setText("BPJ");
                    nmpnj.setText("BPJS");
                    catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                    noTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                    if (noTelp.getText().equals("null") || noTelp.getText().isBlank()) {
                        noTelp.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                    }
                    KdPoliTerapi.setText("");
                    NmPoliTerapi.setText("");
                    KodeDokterTerapi.setText("");
                    NmDokterTerapi.setText("");
                    KdPoliTerapi.setVisible(false);
                    NmPoliTerapi.setVisible(false);
                    KodeDokterTerapi.setVisible(false);
                    NmDokterTerapi.setVisible(false);
                    pilihPoliTerapi.setVisible(false);
                    pilihDokterTerapi.setVisible(false);
                    lblTerapi.setVisible(false);
                } else {
                    emptTeks();
                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi Peserta : " + ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                }
            }
        } else {
            try {
                URL = URLAPIBPJS + "/Rujukan/Peserta/" + noKartuPeserta;
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                requestEntity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                nameNode = root.path("metaData");
                System.out.println("URL : " + URL);
                peserta = "";
                if (nameNode.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                    kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                    namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                    noRujukan.setText(response.path("noKunjungan").asText());
                    kodePoli.setText(query.cariIsiSmc("select kd_poli_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    namaPoli.setText(query.cariIsiSmc("select nm_poli_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    kodeDokter.setText(query.cariIsiSmc("select kd_dokter_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    namaDokter.setText(query.cariIsiSmc("select nm_dokter_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    kdpoli.setText(query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText()));
                    kodeDokterLayanan.setText(query.cariIsiSmc("select kd_dokter_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    namaDokterLayanan.setText(query.cariIsiSmc("select nm_dokter_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP));
                    kodepolireg = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText());
                    kodedokterreg = query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter.getText());
                    if (asalFaskes.equals("2")) {
                        asalRujukan.setSelectedIndex(1);
                    } else {
                        asalRujukan.setSelectedIndex(0);
                    }
                    tujuanKunjungan.setSelectedIndex(2);
                    flagProsedur.setSelectedIndex(0);
                    penunjang.setSelectedIndex(0);
                    asesmenPelayanan.setSelectedIndex(5);
                    valid.SetTgl(tglRujuk, response.path("tglKunjungan").asText());
                    noSuratKontrol.setText(noSKDP);
                    if (response.path("peserta").path("hakKelas").path("kode").asText().equals("1")) {
                        kelas.setSelectedIndex(0);
                    } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("2")) {
                        kelas.setSelectedIndex(1);
                    } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("3")) {
                        kelas.setSelectedIndex(2);
                    }
                    prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                    peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                    namaPasien.setText(response.path("peserta").path("nama").asText());
                    noKartu.setText(response.path("peserta").path("noKartu").asText());
                    noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", noKartu.getText()));
                    nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                    jk.setText(response.path("peserta").path("sex").asText());
                    statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                    tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                    jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                    kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                    namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                    noTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                    if (noTelp.getText().equals("null") || noTelp.getText().isBlank()) {
                        noTelp.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                    }
                    isNumber();
                    Kdpnj.setText("BPJ");
                    nmpnj.setText("BPJS");
                    catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                } else {
                    emptTeks();
//                dispose();
                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi Peserta : " + ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                }
            }
        }

//        btnFingerPrintActionPerformed(null);
    }

    public void SimpanAntrianOnSite() {
        if ((!noRujukan.getText().equals("")) || (!noSuratKontrol.getText().equals(""))) {
            if (tujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && flagProsedur.getSelectedItem().toString().equals("") && penunjang.getSelectedItem().toString().equals("") && asesmenPelayanan.getSelectedItem().toString().equals("")) {
                if (asalRujukan.getSelectedIndex() == 0) {
                    jeniskunjungan = "1";
                } else {
                    jeniskunjungan = "4";
                }
            } else if (tujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && flagProsedur.getSelectedItem().toString().equals("") && penunjang.getSelectedItem().toString().equals("") && asesmenPelayanan.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
                jeniskunjungan = "3";
            } else if (tujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && flagProsedur.getSelectedItem().toString().equals("") && penunjang.getSelectedItem().toString().equals("") && asesmenPelayanan.getSelectedItem().toString().equals("4. Atas Instruksi RS")) {
                jeniskunjungan = "2";
            } else if (tujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && flagProsedur.getSelectedItem().toString().equals("") && penunjang.getSelectedItem().toString().equals("") && asesmenPelayanan.getSelectedItem().toString().equals("1. Poli spesialis tidak tersedia pada hari sebelumnya")) {
                jeniskunjungan = "2";
            } else {
                if (tujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && asesmenPelayanan.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
                    jeniskunjungan = "3";
                } else {
                    jeniskunjungan = "2";
                }
            }

            try {
                day = cal.get(Calendar.DAY_OF_WEEK);
                switch (day) {
                    case 1:
                        hari = "AKHAD";
                        break;
                    case 2:
                        hari = "SENIN";
                        break;
                    case 3:
                        hari = "SELASA";
                        break;
                    case 4:
                        hari = "RABU";
                        break;
                    case 5:
                        hari = "KAMIS";
                        break;
                    case 6:
                        hari = "JUMAT";
                        break;
                    case 7:
                        hari = "SABTU";
                        break;
                    default:
                        break;
                }

                ps = koneksi.prepareStatement("select jam_mulai, jam_selesai, kuota from jadwal where hari_kerja = ? and kd_poli = ? and kd_dokter = ?");
                try {
                    ps.setString(1, hari);
                    ps.setString(2, kodepolireg);
                    ps.setString(3, kodedokterreg);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        jammulai = rs.getString("jam_mulai");
                        jamselesai = rs.getString("jam_selesai");
                        kuota = rs.getInt("kuota");
                        datajam = query.cariIsiSmc("select date_add(concat(?, ' ', ?), interval ? minute)", valid.SetTgl(tglSEP.getSelectedItem().toString()), jammulai, String.valueOf(Integer.parseInt(NoReg.getText()) * 10));
                        parsedDate = dateFormat.parse(datajam);
                    } else {
                        System.out.println("Jadwal tidak ditemukan...!");
                    }
                } catch (Exception e) {
                    System.out.println("Notif jadwal: " + e);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }

                if (!noSuratKontrol.getText().equals("")) {
                    try {
                        utc = String.valueOf(api.GetUTCdatetimeAsString());
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                        headers.add("x-timestamp", utc);
                        headers.add("x-signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());

                        requestJson = "{"
                            + "\"kodebooking\": \"" + TNoRw.getText() + "\","
                            + "\"jenispasien\": \"JKN\","
                            + "\"nomorkartu\": \"" + noKartu.getText() + "\","
                            + "\"nik\": \"" + nik.getText() + "\","
                            + "\"nohp\": \"" + noTelp.getText() + "\","
                            + "\"kodepoli\": \"" + kodePoli.getText() + "\","
                            + "\"namapoli\": \"" + namaPoli.getText() + "\","
                            + "\"pasienbaru\": 0,"
                            + "\"norm\": \"" + noRM.getText() + "\","
                            + "\"tanggalperiksa\": \"" + valid.SetTgl(tglSEP.getSelectedItem() + "") + "\","
                            + "\"kodedokter\": " + kodeDokter.getText() + ","
                            + "\"namadokter\": \"" + namaDokter.getText() + "\","
                            + "\"jampraktek\": \"" + jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5) + "\","
                            + "\"jeniskunjungan\": " + jeniskunjungan + ","
                            + "\"nomorreferensi\": \"" + noSuratKontrol.getText() + "\","
                            + "\"nomorantrean\": \"" + NoReg.getText() + "\","
                            + "\"angkaantrean\": " + Integer.parseInt(NoReg.getText()) + ","
                            + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                            + "\"sisakuotajkn\": " + (kuota - Integer.parseInt(NoReg.getText())) + ","
                            + "\"kuotajkn\": " + kuota + ","
                            + "\"sisakuotanonjkn\": " + (kuota - Integer.parseInt(NoReg.getText())) + ","
                            + "\"kuotanonjkn\": " + kuota + ","
                            + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi. Estimasi pelayanan 10 menit per pasien\""
                        + "}";
                        requestEntity = new HttpEntity(requestJson, headers);
                        URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                        System.out.println("URL : " + URL);
                        System.out.println(requestEntity);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                        nameNode = root.path("metadata");
                        System.out.println("respon WS BPJS Kirim Pakai SKDP : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                    } catch (Exception e) {
                        System.out.println("Notif SKDP : " + e);
                    }
                }

                if (!noRujukan.getText().equals("")) {
                    try {
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                        utc = String.valueOf(api.GetUTCdatetimeAsString());
                        headers.add("x-timestamp", utc);
                        headers.add("x-signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                        requestJson = "{"
                            + "\"kodebooking\": \"" + TNoRw.getText() + "\","
                            + "\"jenispasien\": \"JKN\","
                            + "\"nomorkartu\": \"" + noKartu.getText() + "\","
                            + "\"nik\": \"" + nik.getText() + "\","
                            + "\"nohp\": \"" + noTelp.getText() + "\","
                            + "\"kodepoli\": \"" + kodePoli.getText() + "\","
                            + "\"namapoli\": \"" + namaPoli.getText() + "\","
                            + "\"pasienbaru\": 0,"
                            + "\"norm\": \"" + noRM.getText() + "\","
                            + "\"tanggalperiksa\": \"" + valid.SetTgl(tglSEP.getSelectedItem() + "") + "\","
                            + "\"kodedokter\": " + kodeDokter.getText() + ","
                            + "\"namadokter\": \"" + namaDokter.getText() + "\","
                            + "\"jampraktek\": \"" + jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5) + "\","
                            + "\"jeniskunjungan\": " + jeniskunjungan + ","
                            + "\"nomorreferensi\": \"" + noRujukan.getText() + "\","
                            + "\"nomorantrean\": \"" + NoReg.getText() + "\","
                            + "\"angkaantrean\": " + Integer.parseInt(NoReg.getText()) + ","
                            + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                            + "\"sisakuotajkn\": " + (kuota - Integer.parseInt(NoReg.getText())) + ","
                            + "\"kuotajkn\": " + kuota + ","
                            + "\"sisakuotanonjkn\": " + (kuota - Integer.parseInt(NoReg.getText())) + ","
                            + "\"kuotanonjkn\": " + kuota + ","
                            + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                        + "}";
                        System.out.println("JSON : " + requestJson + "\n");
                        requestEntity = new HttpEntity(requestJson, headers);
                        URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                        System.out.println("URL Kirim Pakai No.Rujuk : " + URL);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                        nameNode = root.path("metadata");
                        System.out.println("respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                    } catch (Exception e) {
                        System.out.println("Notif No.Rujuk : " + e);
                    }
                }

            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }

    private void emptTeks() {
        namaPasien.setText("");
        tglSEP.setDate(new Date());
        tglRujuk.setDate(new Date());
        tglLahir.setText("");
        noKartu.setText("");
        jenisPeserta.setText("");
        statusPeserta.setText("");
        jk.setText("");
        noRujukan.setText("");
        kodePPK.setText("");
        namaPPK.setText("");
        jenisPelayanan.setSelectedIndex(1);
        catatan.setText("");
        kodeDiagnosa.setText("");
        namaDiagnosa.setText("");
        kodePoli.setText("");
        namaPoli.setText("");
        kelas.setSelectedIndex(2);
        lakaLantas.setSelectedIndex(0);
        noRM.setText("");
        kodeDokter.setText("");
        namaDokter.setText("");
        keterangan.setText("");
        noSEPSuplesi.setText("");
        kodeProvKLL.setText("");
        namaProvKLL.setText("");
        kodeKabKLL.setText("");
        namaKabKLL.setText("");
        kodeKecKLL.setText("");
        namaKecKLL.setText("");
        katarak.setSelectedIndex(0);
        suplesi.setSelectedIndex(0);
        tglKLL.setDate(new Date());
        tglKLL.setEnabled(false);
        keterangan.setEditable(false);
        tujuanKunjungan.setSelectedIndex(0);
        flagProsedur.setSelectedIndex(0);
        flagProsedur.setEnabled(false);
        penunjang.setSelectedIndex(0);
        penunjang.setEnabled(false);
        asesmenPelayanan.setSelectedIndex(0);
        asesmenPelayanan.setEnabled(true);
        kodeDokterLayanan.setText("");
        namaDokterLayanan.setText("");
        pilihDokterTujuan.setEnabled(true);
        noRujukan.requestFocus();
        kodepolireg = "";
        kodedokterreg = "";
        KdPoliTerapi.setText("");
        NmPoliTerapi.setText("");
        KodeDokterTerapi.setText("");
        NmDokterTerapi.setText("");
    }

    private void isPoli() {
        try {
            ps = koneksi.prepareStatement("select registrasi, registrasilama from poliklinik where kd_poli = ? order by nm_poli");
            try {
                ps.setString(1, kodepolireg);
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (statuspasien.equals("Lama")) {
                        TBiaya.setText(rs.getString("registrasilama"));
                    } else {
                        TBiaya.setText(rs.getString("registrasi"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }

                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif Cari Poli : " + e);
        }
    }
    
    private void bukaAplikasiFingerprint() {
        if (noKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "No. kartu peserta tidak ada..!!");

            return;
        }

        toFront();

        try {
            aplikasiAktif = false;
            User32 u32 = User32.INSTANCE;
            
            u32.EnumWindows((WinDef.HWND hwnd, Pointer pntr) -> {
                char[] windowText = new char[512];
                u32.GetWindowText(hwnd, windowText, 512);
                String wText = Native.toString(windowText);
                
                if (wText.isEmpty()) {
                    return true;
                }
                
                if (wText.contains("Registrasi Sidik Jari")) {
                    DlgPengaturanAPM.this.aplikasiAktif = true;
                    u32.SetForegroundWindow(hwnd);
                }
                
                return true;
            }, Pointer.NULL);
            
            Robot r = new Robot();
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection ss;
            
            if (aplikasiAktif) {
                Thread.sleep(1000);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_A);
                r.keyRelease(KeyEvent.VK_A);
                r.keyRelease(KeyEvent.VK_CONTROL);
                Thread.sleep(500);
                
                ss = new StringSelection(noKartu.getText().trim());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
            } else {
                Runtime.getRuntime().exec(URLAPLIKASIFINGERPRINTBPJS);
                Thread.sleep(2000);
                ss = new StringSelection(USERFINGERPRINTBPJS);
                c.setContents(ss, ss);

                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                Thread.sleep(1000);

                ss = new StringSelection(PASSFINGERPRINTBPJS);
                c.setContents(ss, ss);

                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_ENTER);
                r.keyRelease(KeyEvent.VK_ENTER);
                Thread.sleep(1000);
                
                ss = new StringSelection(noKartu.getText().trim());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void updateSuratKontrol(String noSKDP, String noSEP, String tglKontrol, String noKartuPeserta) {
        if (noSKDP.trim().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, data surat kontrol tidak ditemukan...!!\nSilahkan hubungi administrasi...!!");
            
            return;
        }
        
        String kodePoliKontrol = query.cariIsiSmc("select kd_poli_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP),
               namaPoliKontrol = query.cariIsiSmc("select nm_poli_bpjs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoliKontrol),
               kodeDokterKontrol = query.cariIsiSmc("select kd_dokter_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP),
               namaDokterKontrol = query.cariIsiSmc("select nm_dokter_bpjs from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokterKontrol),
               tanggalSKDP = query.cariIsiSmc("select tgl_surat from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP);
        
        try {
            utc = String.valueOf(api.GetUTCdatetimeAsString());

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());

            URL = URLAPIBPJS + "/RencanaKontrol/Update";

            requestJson = "{"
                + "\"request\": {"
                    + "\"noSuratKontrol\":\"" + noSKDP + "\","
                    + "\"noSEP\":\"" + noSEP + "\","
                    + "\"kodeDokter\":\"" + kodeDokterKontrol + "\","
                    + "\"poliKontrol\":\"" + kodePoliKontrol + "\","
                    + "\"tglRencanaKontrol\":\"" + tglKontrol + "\","
                    + "\"user\":\"" + noKartuPeserta + "\""
                + "}"
            + "}";

            System.out.println("JSON : " + requestJson);

            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.PUT, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());

            if (nameNode.path("code").asText().equals("200")) {
                System.out.println("Respon BPJS : " + nameNode.path("message").asText());

                query.mengupdateSmc("bridging_surat_kontrol_bpjs",
                    "tgl_surat = ?, tgl_rencana = ?, kd_dokter_bpjs = ?, nm_dokter_bpjs = ?, kd_poli_bpjs = ?, nm_poli_bpjs = ?",
                    "no_surat = ?",
                    tanggalSKDP, tglKontrol, kodeDokterKontrol, namaDokterKontrol, kodePoliKontrol, namaPoliKontrol,
                    noSKDP
                );
            } else {
                JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    public void tampilRujukanRS(String nokartu) {
        try {
            URL = URLAPIBPJS + "/Rujukan/RS/Peserta/" + nokartu;
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            peserta = "";
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                noRujukan.setText(response.path("noKunjungan").asText());
                if (response.path("peserta").path("hakKelas").path("kode").asText().equals("1")) {
                    kelas.setSelectedIndex(0);
                } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("2")) {
                    kelas.setSelectedIndex(1);
                } else if (response.path("peserta").path("hakKelas").path("kode").asText().equals("3")) {
                    kelas.setSelectedIndex(2);
                }
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                namaPasien.setText(response.path("peserta").path("nama").asText());
                noKartu.setText(response.path("peserta").path("noKartu").asText());
                noRM.setText(query.cariIsi("select pasien.no_rkm_medis from pasien where pasien.no_peserta='" + noKartu.getText() + "'"));
                nik.setText(query.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis='" + noRM.getText() + "'"));
                jk.setText(response.path("peserta").path("sex").asText());
                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kdpoli.setText(query.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText()));
                kodepolireg = query.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = query.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", kodeDokter.getText());
                noTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (noTelp.getText().equals("null") || noTelp.getText().isBlank()) {
                    noTelp.setText(query.cariIsi("select pasien.no_tlp from pasien where pasien.no_rkm_medis='" + noRM.getText() + "'"));
                }
                kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                valid.SetTgl(tglRujuk, response.path("tglKunjungan").asText());
                asalRujukan.setSelectedIndex(1);
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");

                KdPoliTerapi.setText("");
                NmPoliTerapi.setText("");
                KodeDokterTerapi.setText("");
                NmDokterTerapi.setText("");
                KdPoliTerapi.setVisible(false);
                NmPoliTerapi.setVisible(false);
                KodeDokterTerapi.setVisible(false);
                NmDokterTerapi.setVisible(false);
                pilihPoliTerapi.setVisible(false);
                pilihDokterTerapi.setVisible(false);
                lblTerapi.setVisible(false);

                ps = koneksi.prepareStatement(
                        "select maping_dokter_dpjpvclaim.kd_dokter,maping_dokter_dpjpvclaim.kd_dokter_bpjs,maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal "
                        + "on maping_dokter_dpjpvclaim.kd_dokter=jadwal.kd_dokter where jadwal.kd_poli=? and jadwal.hari_kerja=?");
                try {
                    if (day == 1) {
                        hari = "AKHAD";
                    } else if (day == 2) {
                        hari = "SENIN";
                    } else if (day == 3) {
                        hari = "SELASA";
                    } else if (day == 4) {
                        hari = "RABU";
                    } else if (day == 5) {
                        hari = "KAMIS";
                    } else if (day == 6) {
                        hari = "JUMAT";
                    } else if (day == 7) {
                        hari = "SABTU";
                    }

                    ps.setString(1, kdpoli.getText());
                    ps.setString(2, hari);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        kodeDokter.setText(rs.getString("kd_dokter_bpjs"));
                        namaDokter.setText(rs.getString("nm_dokter_bpjs"));
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
            } else {
                emptTeks();
//                dispose();
                JOptionPane.showMessageDialog(rootPane, "Pesan Pencarian Rujukan FKRTL : " + nameNode.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }
    
    private boolean registerPasien() {
        int coba = 0, maxCoba = 5;
        
        System.out.println("Mencoba mendaftarkan pasien dengan no. rawat: " + TNoRw.getText());
             
        while (coba < maxCoba && (
            ! query.menyimpantfSmc("reg_periksa", null,
                NoReg.getText(), TNoRw.getText(), valid.SetTgl(tglSEP.getSelectedItem().toString()),
                query.cariIsi("select current_time()"), kodedokterreg, noRM.getText(), kodepolireg,
                TPngJwb.getText(), TAlmt.getText(), THbngn.getText(), TBiaya.getText(), "Belum",
                statuspasien, "Ralan", Kdpnj.getText(), umur, sttsumur, "Belum Bayar", status)
        )) {
            isNumber();
            System.out.println("Mencoba mendaftarkan pasien dengan no. rawat: " + TNoRw.getText());
            
            coba++;
        }
        
        String isNoRawat = query.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ?", valid.SetTgl(tglSEP.getSelectedItem().toString()), noRM.getText(), kodepolireg, kodedokterreg);
                
        if (coba == maxCoba && (isNoRawat == null || ! isNoRawat.equals(TNoRw.getText()))) {
            System.out.println("======================================================");
            System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
            System.out.println("No. Rawat: " + TNoRw.getText());
            System.out.println("Tgl. Registrasi: " + valid.SetTgl(tglSEP.getSelectedItem().toString()));
            System.out.println("No. Antrian: " + NoReg.getText() + " (Ditemukan: " + query.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
            System.out.println("No. RM: " + noRM.getText() + " (Ditemukan: " + query.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
            System.out.println("Kode Dokter: " + kodedokterreg + " (Ditemukan: " + query.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
            System.out.println("Kode Poli: " + kodepolireg  + " (Ditemukan: " + query.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
            System.out.println("======================================================");

            return false;
        }
        
        updateUmurPasien();
        
        return true;
    }
    
    private boolean simpanRujukan() {
        int coba = 0, maxCoba = 5;
        
        NoRujukMasuk.setText(query.cariIsiSmc("select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                valid.SetTgl(tglSEP.getSelectedItem().toString()), valid.SetTgl(tglSEP.getSelectedItem().toString())
            )
        );
        
        System.out.println("Mencoba memproses rujukan masuk pasien dengan no. surat: " + NoRujukMasuk.getText());
        
        while (coba < maxCoba && (
            !query.menyimpantfSmc("rujuk_masuk", null,
                TNoRw.getText(), namaPPK.getText(), "-", noRujukan.getText(),
                "0", namaPPK.getText(), kodeDiagnosa.getText(), "-", "-", NoRujukMasuk.getText()
            )
        )) {
            NoRujukMasuk.setText(query.cariIsiSmc("select concat('BR/', date_format(?, '%Y/%m/%d/'), lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                    valid.SetTgl(tglSEP.getSelectedItem().toString()), valid.SetTgl(tglSEP.getSelectedItem().toString())
                )
            );
            
            System.out.println("Mencoba memproses rujukan masuk pasien dengan no. surat balasan: " + NoRujukMasuk.getText());
            
            coba++;
        }
        
        String isNoRujukMasuk = query.cariIsiSmc("select rujuk_masuk.no_balasan from rujuk_masuk where rujuk_masuk.no_rawat = ?", TNoRw.getText());
        
        if (coba == maxCoba && (isNoRujukMasuk == null || (! isNoRujukMasuk.equals(NoRujukMasuk.getText())))) {
            System.out.println("======================================================");
            System.out.println("Tidak dapat memproses rujukan masuk dengan detail berikut:");
            System.out.println("No. Surat: " + NoRujukMasuk.getText());
            System.out.println("No. Rawat: " + TNoRw.getText());
            System.out.println("======================================================");
            
            return false;
        }
        
        return true;
    }
    
    private void updateUmurPasien() {
        query.mengupdateSmc("pasien",
            "no_tlp = ?, umur = concat(concat(concat(timestampdiff(year, tgl_lahir, curdate()), ' Th '), concat(timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12), ' Bl ')), concat(timestampdiff(day, date_add(date_add(tgl_lahir, interval timestampdiff(year, tgl_lahir, curdate()) year), interval timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12) month), curdate()), ' Hr'))",
            "no_rkm_medis = ?",
            noTelp.getText(), noRM.getText()
        );
    }
    
    public void tampilRujukanPertama(String input) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JsonNode rujukan = cekRujukan(input, false);
        asalRujukan.setSelectedIndex(0);
        if (rujukan == null) {
            rujukan = cekRujukan(input, true);
            asalRujukan.setSelectedIndex(1);
        }

        if (rujukan == null) {
            JOptionPane.showMessageDialog(rootPane, "Rujukan tidak ditemukan...!!!");
            this.setCursor(Cursor.getDefaultCursor());
            return;
        }

        noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", input));
        namaPasien.setText(rujukan.path("pasien").path("nama").asText());
        tglLahir.setText(valid.SetTgl(rujukan.path("pasien").path("tglLahir").asText()));
        statusPeserta.setText(rujukan.path("pasien").path("statusPeserta").path("kode").asText() + " " + rujukan.path("pasien").path("statusPeserta").path("keterangan").asText());
        noRujukan.setText(rujukan.path("noKunjungan").asText());
        kodePPK.setText(rujukan.path("provPerujuk").path("kode").asText());
        namaPPK.setText(rujukan.path("provPerujuk").path("nama").asText());
        kodeDiagnosa.setText(rujukan.path("diagnosa").path("kode").asText());
        namaDiagnosa.setText(rujukan.path("diagnosa").path("nama").asText());
        kodePoli.setText(rujukan.path("poliRujukan").path("kode").asText());
        namaPoli.setText(rujukan.path("poliRujukan").path("nama").asText());
        jenisPelayanan.setSelectedIndex(0);
        switch (rujukan.path("hakKelas").path("kode").asText()) {
            case "1": kelas.setSelectedIndex(0); break;
            case "2": kelas.setSelectedIndex(1); break;
            case "3": kelas.setSelectedIndex(2); break;
        }
        kodeDPJPLayanan.setVisible(false);
        namaDPJPLayanan.setVisible(false);
        LabelPoli7.setVisible(false);
        jenisPeserta.setText(rujukan.path("pasien").path("jenisPeserta").path("keterangan").asText());
        jk.setText(rujukan.path("pasien").path("sex").asText());
        
        String _nik = rujukan.path("pasien").path("nik").asText().replace("null,", "");
        if (_nik.isBlank()) _nik = query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText());
        nik.setText(_nik);
        
        noKartu.setText(rujukan.path("pasien").path("noKartu").asText());
        tglSEP.setDate(new Date());
        tglRujuk.setSelectedItem(valid.SetTgl(rujukan.path("tglKunjungan").asText()));
        
        String _noTelp = rujukan.path("pasien").path("mr").path("noTelepon").asText().replace("null", "");
        if (_noTelp.isBlank()) _noTelp = query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText());
        noTelp.setText(_noTelp);

        this.setCursor(Cursor.getDefaultCursor());
    }
    
    public void tampilRujukanBedaPoli(String input) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        kodeDPJPLayanan.setVisible(false);
        namaDPJPLayanan.setVisible(false);
        LabelPoli7.setVisible(false);
        
        this.setCursor(Cursor.getDefaultCursor());
    }

    public void tampilRujukanKontrol(String input) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        kodeDPJPLayanan.setVisible(true);
        namaDPJPLayanan.setVisible(true);
        LabelPoli8.setVisible(true);
        
        //
        
        this.setCursor(Cursor.getDefaultCursor());
    }
    
    private JsonNode cekRujukan(String noKartu, boolean fktrl) {
        String url = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + noKartu;
        if (fktrl) {
            url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + noKartu;
        }

        try {
            String utc = api.getUTCDateTime();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());

            HttpEntity entities = new HttpEntity(headers);
            JsonNode root = objMap.readTree(api.getRest().exchange(url, HttpMethod.GET, entities, String.class).getBody());
            JsonNode metadata = root.path("metaData");
            JsonNode response = objMap.readTree(api.Decrypt(root.path("response").asText(), utc));
            JsonNode rujukan = response.path("rujukan");

            if (!metadata.path("code").asText().equals("200")) {
                System.out.println("Respon BPJS : " + metadata.path("message").asText());
            }

            return rujukan;
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus!");
            }
            return null;
        }
    }

    private void autonomor() {
        switch (koneksiDB.URUTNOREG()) {
            case "dokter":
                noReg = query.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and tgl_registrasi = ?",
                    kodeDokterRS, valid.SetTgl(tglSEP.getSelectedItem().toString())
                );
                break;
            case "poli":
                noReg = query.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and tgl_registrasi = ?",
                    kodePoliRS, valid.SetTgl(tglSEP.getSelectedItem().toString())
                );
                break;
            case "dokter + poli":
                noReg = query.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and kd_poli = ? and tgl_registrasi = ?",
                    kodeDokterRS, kodePoliRS, valid.SetTgl(tglSEP.getSelectedItem().toString())
                );
                break;
            default:
                noReg = query.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and kd_poli = ? and tgl_registrasi = ?",
                    kodeDokterRS, kodePoliRS, valid.SetTgl(tglSEP.getSelectedItem().toString())
                );
                break;
        }
        noRawat = query.cariIsiSmc(
            "select concat(date_format(tgl_registrasi, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_rawat, 6), signed)), 0) + 1, 6, '0')) from reg_periksa where no_rawat = concat(date_format(?, '%Y/%m/%d'), '/%')",
            valid.SetTgl(tglSEP.getSelectedItem().toString())
        );
    }
    
    private void cekStatusPasien() {
        String sql = "select pasien.nm_pasien, concat_ws(', ' pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as asal, " +
            "pasien.namakeluarga, pasien.keluarga, pasien.kd_pj, penjab.png_jawab, if (tgl_daftar = ?, 'baru', 'lama') as status_daftar, " +
            "pasien.no_ktp, pasien.no_tlp, pasien.no_peserta, timestampdiff(year, pasien.tgl_lahir, curdate()) as tahun, " +
            "timestampdiff(month, pasien.tgl_lahir, curdate()) - ((timestampdiff(month, pasien.tgl_lahir, curdate()) div 12) * 12) as bulan, " +
            "timestampdiff(day, date_add(date_add(pasien.tgl_lahir, interval timestampdiff(year, pasien.tgl_lahir, curdate()) year), interval timestampdiff(month, pasien.tgl_lahir, curdate()) - ((timestampdiff(month, pasien.tgl_lahir, curdate()) div 12) * 12) month), curdate()) as hari " +
            "from pasien " +
            "join kelurahan on pasien.kd_kel = kelurahan.kd_kel " +
            "join kecamatan on pasien.kd_kec = kecamatan.kd_kec " +
            "join kabupaten on pasien.kd_kab = kabupaten.kd_kab " +
            "join penjab on pasien.kd_pj = penjab.kd_pj " +
            "where pasien.no_rkm_medis = ?";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setDate(1, (java.sql.Date) tglSEP.getDate());
            ps.setString(2, noRM.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    alamatPj = rs.getString("asal");
                    namaPj = rs.getString("namakeluarga");
                    hubunganPj = rs.getString("keluarga");
                    statusDaftar = rs.getString("status_daftar");
                    umurDaftar = "0";
                    statusUmur = "Th";
                    if (rs.getInt("tahun") > 0) {
                        umurDaftar = rs.getString("tahun");
                        statusUmur = "Th";
                    } else if (rs.getInt("tahun") == 0 && rs.getInt("bulan") > 0) {
                        umurDaftar = rs.getString("bulan");
                        statusUmur = "Bl";
                    } else {
                        umurDaftar = rs.getString("hari");
                        statusUmur = "Hr";
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private void cekPoli() {
        try (PreparedStatement ps = koneksi.prepareStatement("select registrasi, registrasilama from poliklinik where kd_poli = ? order by nm_poli")) {
            ps.setString(1, kodePoliRS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (statusPoli.equals("Lama")) {
                        biayaReg = rs.getString("registrasilama");
                    } else {
                        biayaReg = rs.getString("registrasi");
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private boolean validasiInput() {
        if (noRM.getText().isBlank() || namaPasien.getText().isBlank()) {
            valid.textKosong(noRM, "Pasien");
        } else if (noKartu.getText().isBlank()) {
            valid.textKosong(noKartu, "Nomor Kartu");
        } else if (query.cariBooleanSmc("select * from pasien where no_rkm_medis = ?", noRM.getText())) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. RM Pasien tidak sesuai");
        } else if (kodePPK.getText().isBlank() || namaPPK.getText().isBlank()) {
            valid.textKosong(kodePPK, "PPK Rujukan");
        } else if (kodePPKPelayanan.getText().isBlank() || namaPPKPelayanan.getText().isBlank()) {
            valid.textKosong(kodePPKPelayanan, "PPK Pelayanan");
        } else if (kodeDiagnosa.getText().isBlank() || namaDiagnosa.getText().isBlank()) {
            valid.textKosong(kodeDiagnosa, "Diagnosa");
        } else if (catatan.getText().isBlank()) {
            valid.textKosong(catatan, "Catatan");
        } else if ((jenisPelayanan.getSelectedIndex() == 1) && (kodePoli.getText().isBlank() || namaPoli.getText().isBlank())) {
            valid.textKosong(kodePoli, "Poli Tujuan");
        } else if ((lakaLantas.getSelectedIndex() == 1) && keterangan.getText().isBlank()) {
            valid.textKosong(keterangan, "Keterangan");
        } else if (kodeDokter.getText().isBlank() || namaDokter.getText().isBlank()) {
            valid.textKosong(kodeDokter, "DPJP");
        } else if (! statusfinger && query.cariIntegerSmc("select timestampdiff(year, ?, current_date())", tglLahir.getText()) >= 17 && jenisPelayanan.getSelectedIndex() != 0 && !kodePoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Pasien belum melakukan Fingerprint");
            bukaAplikasiFingerprint();
        } else {
            return true;
        }
        return false;
    }
    
    private boolean simpanRegistrasi() {
        int coba = 0, maxCoba = 5;
        autonomor();
        boolean sukses = query.menyimpantfSmc("reg_periksa", null,
            noReg, noRawat, valid.setTglSmc(tglSEP), tf.format(Calendar.getInstance().getTime()), kodeDokterRS, noRM.getText(),
            kodePoliRS, namaPj, alamatPj, hubunganPj, biayaReg, "Belum", statusPoli, "Ralan", "BPJ", umurDaftar, statusUmur,
            "Belum Bayar", "Lama"
        );
        
        while (coba < maxCoba && ! sukses) {
            autonomor();
            sukses = query.menyimpantfSmc("reg_periksa", null,
                noReg, noRawat, valid.setTglSmc(tglSEP), tf.format(Calendar.getInstance().getTime()), kodeDokterRS, noRM.getText(),
                kodePoliRS, namaPj, alamatPj, hubunganPj, biayaReg, "Belum", statusPoli, "Ralan", "BPJ", umurDaftar, statusUmur,
                "Belum Bayar", statusDaftar
            );
        }
        
        return sukses;
    }
    
    private void simpanDataPasien() {
        //
    }
    
    private void simpanAntrianOnsite() {
        //
    }

    private void simpanSEP() {
        //
    }
    
    private void printRegistrasi() {
        // 
    }

    private void updateSKDP() {
        //
    }

    private void resetInput() {
        noRM.setText("");
        namaPasien.setText("");
        tglLahir.setText("");
        statusPeserta.setText("");
        noSuratKontrol.setText("");
        noRujukan.setText("");
        kodePPK.setText("");
        namaPPK.setText("");
        kodeDiagnosa.setText("");
        namaDiagnosa.setText("");
        kodePoli.setText("");
        namaPoli.setText("");
        kodeDokter.setText("");
        namaDokter.setText("");
        jenisPelayanan.setSelectedIndex(0);
        kelas.setSelectedIndex(2);
        tujuanKunjungan.setSelectedIndex(0);
        flagProsedur.setSelectedIndex(0);
        penunjang.setSelectedIndex(0);
        asesmenPelayanan.setSelectedIndex(0);
        jenisPeserta.setText("");
        jk.setText("");
        nik.setText("");
        noKartu.setText("");
        asalRujukan.setSelectedIndex(0);
        tglSEP.setDate(new Date());
        tglRujuk.setDate(new Date());
        noTelp.setText("");
        katarak.setSelectedIndex(0);
        lakaLantas.setSelectedIndex(0);
        tglKLL.setDate(new Date());
        keterangan.setText("");
        suplesi.setSelectedIndex(0);
        noSEPSuplesi.setText("");
        kodeProvKLL.setText("");
        namaProvKLL.setText("");
        kodeKabKLL.setText("");
        namaKabKLL.setText("");
        kodeKecKLL.setText("");
        namaKecKLL.setText("");
    }
}
