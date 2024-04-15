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
import fungsi.BatasInput;
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
public class DlgRegistrasiSEPPertama extends javax.swing.JDialog {
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel query = new sekuel();
    private final validasi valid = new validasi();
    private final ApiBPJS api = new ApiBPJS();
    private final ObjectMapper mapper = new ObjectMapper();
    private final SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private BPJSCekReferensiDokterDPJP1 dokter = new BPJSCekReferensiDokterDPJP1(null, true);
    private BPJSCekReferensiPenyakit cariDiagnosa = new BPJSCekReferensiPenyakit(null, true);
    private DlgCariPoliBPJS poli = new DlgCariPoliBPJS(null, true);
    private DlgCariPoli polimapping = new DlgCariPoli(null, true);
    private DlgCariDokter2 doktermapping = new DlgCariDokter2(null, true);
    private BPJSCekRiwayatRujukanTerakhir cariRujukan = new BPJSCekRiwayatRujukanTerakhir(null, true);
    private BPJSCekRiwayatPelayanan historiPelayanan = new BPJSCekRiwayatPelayanan(null, true);
    private DlgCariPoli poli2 = new DlgCariPoli(null, true);
    private String noRawat = "",
                   noReg = "",
                   kodeDokterRS = "",
                   kodePoliRS = "",
                   namaPJ = "-",
                   hubunganPJ = "DIRI SENDIRI",
                   alamat = "",
                   biaya = "0",
                   umur = "0",
                   statusUmur = "Th",
                   statusRegistrasi = "-",
                   statusPoli = "Baru",
                   hari = "",
                   jamMulai = "",
                   jamSelesai = "",
                   noSEP = "",
                   url = "",
                   utc = "",
                   json = "{}",
                   prb = "",
                   jenisKunjungan = "",
                   aksiFP = "";
    private int kuota = 0;
    private boolean statusFP = false, appFPAktif = false;
    private JsonNode root, metaData, response;
    private HttpHeaders headers;
    private HttpEntity entity;

    /**
     * Creates new form DlgAdmin
     *
     * @param parent
     * @param id
     */
    public DlgRegistrasiSEPPertama(java.awt.Frame parent, boolean id) {
        super(parent, id);
        initComponents();
        jumlahBarcode.setDocument(new BatasInput(1).getOnlyAngka(jumlahBarcode));

        dokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    kodeDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    namaDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    if (jenisPelayanan.getSelectedIndex() == 1) {
                        kodeDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                        namaDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    }
                    kodeDPJP.requestFocus();

                }
            }
        });

        poli.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (poli.getTable().getSelectedRow() != -1) {
                    kodePoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 0).toString());
                    namaPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 1).toString());
                    kodeDPJP.requestFocus();
                }
            }
        });

        polimapping.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (polimapping.getTable().getSelectedRow() != -1) {
                    kodePoliTerapi.setText(polimapping.getTable().getValueAt(polimapping.getTable().getSelectedRow(), 0).toString());
                    namaPoliTerapi.setText(polimapping.getTable().getValueAt(polimapping.getTable().getSelectedRow(), 1).toString());
                    kodeDokterTerapi.requestFocus();

                }
            }
        });

        doktermapping.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (doktermapping.getTable().getSelectedRow() != -1) {
                    kodeDokterTerapi.setText(doktermapping.getTable().getValueAt(doktermapping.getTable().getSelectedRow(), 0).toString());
                    namaDokterTerapi.setText(doktermapping.getTable().getValueAt(doktermapping.getTable().getSelectedRow(), 1).toString());
                    kodeDokterTerapi.requestFocus();

                }
            }
        });

        cariDiagnosa.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cariDiagnosa.getTable().getSelectedRow() != -1) {

                    kodeDiagnosa.setText(cariDiagnosa.getTable().getValueAt(cariDiagnosa.getTable().getSelectedRow(), 1).toString());
                    namaDiagnosa.setText(cariDiagnosa.getTable().getValueAt(cariDiagnosa.getTable().getSelectedRow(), 2).toString());
                    kodeDiagnosa.requestFocus();

                }
            }
        });

        cariRujukan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cariRujukan.getTable().getSelectedRow() != -1) {
                    kodeDiagnosa.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 0).toString());
                    namaDiagnosa.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 1).toString());
                    noRujukan.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 2).toString());
                    kodePoli.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 3).toString());
                    namaPoli.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 4).toString());
                    kodePPK.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 6).toString());
                    namaPPK.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 7).toString());
                    valid.SetTgl(tglRujukan, cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 5).toString());
                    catatan.requestFocus();
                }
            }
        });

        historiPelayanan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (historiPelayanan.getTable().getSelectedRow() != -1) {
                    if ((historiPelayanan.getTable().getSelectedColumn() == 6) || (historiPelayanan.getTable().getSelectedColumn() == 7)) {
                        noRujukan.setText(historiPelayanan.getTable().getValueAt(historiPelayanan.getTable().getSelectedRow(), historiPelayanan.getTable().getSelectedColumn()).toString());
                    }
                }
                noRujukan.requestFocus();
            }
        });

        kodePPKPelayanan.setText(query.cariIsiSmc("select kode_ppk from setting"));
        namaPPKPelayanan.setText(query.cariIsiSmc("select nama_instansi from setting"));
        catatan.setText("Anjungan Pasien Mandiri " + kodePPKPelayanan.getText());
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
        labelTglSEP = new widget.Label();
        tglSEP = new widget.Tanggal();
        labelTglRujukan = new widget.Label();
        tglRujukan = new widget.Tanggal();
        labelNoSKDP = new widget.Label();
        noRujukan = new widget.TextBox();
        labelPPKPelayanan = new widget.Label();
        kodePPKPelayanan = new widget.TextBox();
        namaPPKPelayanan = new widget.TextBox();
        labelPPK = new widget.Label();
        kodePPK = new widget.TextBox();
        namaPPK = new widget.TextBox();
        labelDiagnosa = new widget.Label();
        kodeDiagnosa = new widget.TextBox();
        namaDiagnosa = new widget.TextBox();
        namaPoli = new widget.TextBox();
        kodePoli = new widget.TextBox();
        labelPoli = new widget.Label();
        labelJenisPelayanan = new widget.Label();
        labelCatatan = new widget.Label();
        catatan = new widget.TextBox();
        jenisPelayanan = new widget.ComboBox();
        labelKelas = new widget.Label();
        kelas = new widget.ComboBox();
        lakaLantas = new widget.ComboBox();
        labelDataPasien = new widget.Label();
        tglLahir = new widget.TextBox();
        labekJK = new widget.Label();
        jk = new widget.TextBox();
        labelJenisPeserta = new widget.Label();
        jenisPeserta = new widget.TextBox();
        labelStatusPeserta = new widget.Label();
        statusPeserta = new widget.TextBox();
        labelAsalRujukan = new widget.Label();
        asalRujukan = new widget.ComboBox();
        noTelpon = new widget.TextBox();
        katarak = new widget.ComboBox();
        labelKatarak = new widget.Label();
        labelTglKLL = new widget.Label();
        tglKLL = new widget.Tanggal();
        labelDPJP = new widget.Label();
        kodeDPJP = new widget.TextBox();
        namaDPJP = new widget.TextBox();
        labelKeterangan = new widget.Label();
        keterangan = new widget.TextBox();
        labelSuplesi = new widget.Label();
        suplesi = new widget.ComboBox();
        noSEPSuplesi = new widget.TextBox();
        labelNoSEPSuplesi = new widget.Label();
        labelProvKLL = new widget.Label();
        kodeProvKLL = new widget.TextBox();
        namaProvKLL = new widget.TextBox();
        labelKabKLL = new widget.Label();
        kodeKabKLL = new widget.TextBox();
        namaKabKLL = new widget.TextBox();
        labelKecKLL = new widget.Label();
        kodeKecKLL = new widget.TextBox();
        namaKecKLL = new widget.TextBox();
        labelTujuanKunjungan = new widget.Label();
        tujuanKunjungan = new widget.ComboBox();
        flagProsedur = new widget.ComboBox();
        labelFlagProsedur = new widget.Label();
        labelPenunjang = new widget.Label();
        penunjang = new widget.ComboBox();
        labelAsesmenPelayanan = new widget.Label();
        asesmenPelayanan = new widget.ComboBox();
        labelTerapi = new widget.Label();
        kodeDPJPLayanan = new widget.TextBox();
        namaDPJPLayanan = new widget.TextBox();
        pilihDPJP = new widget.Button();
        labelLakaLantas = new widget.Label();
        labelNoTelpon = new widget.Label();
        labelTglLahir = new widget.Label();
        labelNIK = new widget.Label();
        noSKDP = new widget.TextBox();
        labelRujukan = new widget.Label();
        nik = new widget.TextBox();
        labelNoKartu = new widget.Label();
        pilihPoli = new widget.Button();
        pilihDiagnosa = new widget.Button();
        pilihRujukan = new widget.Button();
        lihatRiwayatPelayananBPJS = new widget.Button();
        kodeDokterTerapi = new widget.TextBox();
        kodePoliTerapi = new widget.TextBox();
        namaPoliTerapi = new widget.TextBox();
        namaDokterTerapi = new widget.TextBox();
        pilihDokterTerapi = new widget.Button();
        pilihPoliTerapi = new widget.Button();
        labelDokterTerapi = new widget.Label();
        approvalFP = new widget.Button();
        pengajuanFP = new widget.Button();
        labelDPJPLayanan = new widget.Label();
        labelPoliTerapi = new widget.Label();
        labelJumlahBarcode = new widget.Label();
        jumlahBarcode = new widget.TextBox();
        jPanel3 = new javax.swing.JPanel();
        simpan = new component.Button();
        fingerprint = new component.Button();
        batal = new component.Button();

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
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(238, 238, 255), 1, true), "DATA ELIGIBILITAS PESERTA JKN", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 24), new java.awt.Color(0, 131, 62))); // NOI18N
        jPanel1.setMinimumSize(new java.awt.Dimension(543, 106));
        jPanel1.setPreferredSize(new java.awt.Dimension(543, 106));
        jPanel1.setLayout(new java.awt.BorderLayout(0, 1));

        jPanel2.setBackground(new java.awt.Color(238, 238, 255));
        jPanel2.setForeground(new java.awt.Color(0, 131, 62));
        jPanel2.setPreferredSize(new java.awt.Dimension(390, 120));
        jPanel2.setLayout(null);

        namaPasien.setEditable(false);
        namaPasien.setBackground(new java.awt.Color(255, 255, 153));
        namaPasien.setHighlighter(null);
        jPanel2.add(namaPasien);
        namaPasien.setBounds(340, 10, 230, 30);

        noRM.setHighlighter(null);
        jPanel2.add(noRM);
        noRM.setBounds(230, 10, 110, 30);

        noKartu.setEditable(false);
        noKartu.setBackground(new java.awt.Color(255, 255, 153));
        noKartu.setHighlighter(null);
        jPanel2.add(noKartu);
        noKartu.setBounds(730, 70, 300, 30);

        labelTglSEP.setForeground(new java.awt.Color(0, 131, 62));
        labelTglSEP.setText("Tgl. SEP :");
        labelTglSEP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelTglSEP.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelTglSEP);
        labelTglSEP.setBounds(620, 130, 105, 30);

        tglSEP.setEditable(false);
        tglSEP.setForeground(new java.awt.Color(50, 70, 50));
        tglSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "15-04-2024" }));
        tglSEP.setDisplayFormat("dd-MM-yyyy");
        tglSEP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        tglSEP.setOpaque(false);
        tglSEP.setPreferredSize(new java.awt.Dimension(95, 25));
        jPanel2.add(tglSEP);
        tglSEP.setBounds(730, 130, 170, 30);

        labelTglRujukan.setForeground(new java.awt.Color(0, 131, 62));
        labelTglRujukan.setText("Tgl. Rujukan :");
        labelTglRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelTglRujukan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelTglRujukan);
        labelTglRujukan.setBounds(620, 160, 105, 30);

        tglRujukan.setEditable(false);
        tglRujukan.setForeground(new java.awt.Color(50, 70, 50));
        tglRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "15-04-2024" }));
        tglRujukan.setDisplayFormat("dd-MM-yyyy");
        tglRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        tglRujukan.setOpaque(false);
        tglRujukan.setPreferredSize(new java.awt.Dimension(95, 23));
        jPanel2.add(tglRujukan);
        tglRujukan.setBounds(730, 160, 170, 30);

        labelNoSKDP.setForeground(new java.awt.Color(0, 131, 62));
        labelNoSKDP.setText("No. SKDP / Surat Kontrol :");
        labelNoSKDP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelNoSKDP.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelNoSKDP);
        labelNoSKDP.setBounds(75, 70, 150, 30);

        noRujukan.setEditable(false);
        noRujukan.setBackground(new java.awt.Color(255, 255, 153));
        noRujukan.setHighlighter(null);
        jPanel2.add(noRujukan);
        noRujukan.setBounds(230, 100, 340, 30);

        labelPPKPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        labelPPKPelayanan.setText("PPK Pelayanan :");
        labelPPKPelayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelPPKPelayanan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelPPKPelayanan);
        labelPPKPelayanan.setBounds(75, 250, 150, 30);

        kodePPKPelayanan.setEditable(false);
        kodePPKPelayanan.setBackground(new java.awt.Color(255, 255, 153));
        kodePPKPelayanan.setHighlighter(null);
        jPanel2.add(kodePPKPelayanan);
        kodePPKPelayanan.setBounds(230, 250, 80, 30);

        namaPPKPelayanan.setEditable(false);
        namaPPKPelayanan.setBackground(new java.awt.Color(255, 255, 153));
        namaPPKPelayanan.setHighlighter(null);
        jPanel2.add(namaPPKPelayanan);
        namaPPKPelayanan.setBounds(310, 250, 260, 30);

        labelPPK.setForeground(new java.awt.Color(0, 131, 62));
        labelPPK.setText("PPK Rujukan :");
        labelPPK.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelPPK.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelPPK);
        labelPPK.setBounds(75, 130, 150, 30);

        kodePPK.setEditable(false);
        kodePPK.setBackground(new java.awt.Color(255, 255, 153));
        kodePPK.setHighlighter(null);
        jPanel2.add(kodePPK);
        kodePPK.setBounds(230, 130, 80, 30);

        namaPPK.setEditable(false);
        namaPPK.setBackground(new java.awt.Color(255, 255, 153));
        namaPPK.setHighlighter(null);
        jPanel2.add(namaPPK);
        namaPPK.setBounds(310, 130, 260, 30);

        labelDiagnosa.setForeground(new java.awt.Color(0, 131, 62));
        labelDiagnosa.setText("Diagnosa Awal :");
        labelDiagnosa.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelDiagnosa.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelDiagnosa);
        labelDiagnosa.setBounds(75, 160, 150, 30);

        kodeDiagnosa.setEditable(false);
        kodeDiagnosa.setBackground(new java.awt.Color(255, 255, 153));
        kodeDiagnosa.setHighlighter(null);
        jPanel2.add(kodeDiagnosa);
        kodeDiagnosa.setBounds(230, 160, 80, 30);

        namaDiagnosa.setEditable(false);
        namaDiagnosa.setBackground(new java.awt.Color(255, 255, 153));
        namaDiagnosa.setHighlighter(null);
        jPanel2.add(namaDiagnosa);
        namaDiagnosa.setBounds(310, 160, 260, 30);

        namaPoli.setEditable(false);
        namaPoli.setBackground(new java.awt.Color(255, 255, 153));
        namaPoli.setHighlighter(null);
        jPanel2.add(namaPoli);
        namaPoli.setBounds(310, 190, 260, 30);

        kodePoli.setEditable(false);
        kodePoli.setBackground(new java.awt.Color(255, 255, 153));
        kodePoli.setHighlighter(null);
        jPanel2.add(kodePoli);
        kodePoli.setBounds(230, 190, 80, 30);

        labelPoli.setForeground(new java.awt.Color(0, 131, 62));
        labelPoli.setText("Poli Tujuan :");
        labelPoli.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelPoli.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelPoli);
        labelPoli.setBounds(75, 190, 150, 30);

        labelJenisPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        labelJenisPelayanan.setText("Jenis Pelayanan :");
        labelJenisPelayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelJenisPelayanan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelJenisPelayanan);
        labelJenisPelayanan.setBounds(75, 280, 150, 30);

        labelCatatan.setForeground(new java.awt.Color(0, 131, 62));
        labelCatatan.setText("Catatan :");
        labelCatatan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelCatatan);
        labelCatatan.setBounds(620, 460, 105, 30);

        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        catatan.setHighlighter(null);
        jPanel2.add(catatan);
        catatan.setBounds(730, 460, 300, 30);

        jenisPelayanan.setBackground(new java.awt.Color(255, 255, 153));
        jenisPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        jenisPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Ranap", "2. Ralan" }));
        jenisPelayanan.setSelectedIndex(1);
        jenisPelayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jenisPelayanan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jenisPelayananItemStateChanged(evt);
            }
        });
        jPanel2.add(jenisPelayanan);
        jenisPelayanan.setBounds(230, 280, 110, 30);

        labelKelas.setForeground(new java.awt.Color(0, 131, 62));
        labelKelas.setText("Kelas :");
        labelKelas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelKelas);
        labelKelas.setBounds(355, 280, 40, 30);

        kelas.setForeground(new java.awt.Color(0, 131, 62));
        kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Kelas 1", "2. Kelas 2", "3. Kelas 3" }));
        kelas.setSelectedIndex(2);
        kelas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(kelas);
        kelas.setBounds(400, 280, 100, 30);

        lakaLantas.setForeground(new java.awt.Color(0, 131, 62));
        lakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        lakaLantas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        lakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        lakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lakaLantasItemStateChanged(evt);
            }
        });
        jPanel2.add(lakaLantas);
        lakaLantas.setBounds(730, 250, 170, 30);

        labelDataPasien.setForeground(new java.awt.Color(0, 131, 62));
        labelDataPasien.setText("Data Pasien : ");
        labelDataPasien.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelDataPasien.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelDataPasien);
        labelDataPasien.setBounds(75, 10, 150, 30);

        tglLahir.setEditable(false);
        tglLahir.setBackground(new java.awt.Color(255, 255, 153));
        tglLahir.setHighlighter(null);
        jPanel2.add(tglLahir);
        tglLahir.setBounds(230, 40, 110, 30);

        labekJK.setForeground(new java.awt.Color(0, 131, 62));
        labekJK.setText("J. K. :");
        labekJK.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labekJK);
        labekJK.setBounds(905, 10, 30, 30);

        jk.setEditable(false);
        jk.setBackground(new java.awt.Color(255, 255, 153));
        jk.setHighlighter(null);
        jPanel2.add(jk);
        jk.setBounds(940, 10, 90, 30);

        labelJenisPeserta.setForeground(new java.awt.Color(0, 131, 62));
        labelJenisPeserta.setText("Peserta :");
        labelJenisPeserta.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelJenisPeserta.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelJenisPeserta);
        labelJenisPeserta.setBounds(620, 10, 105, 30);

        jenisPeserta.setEditable(false);
        jenisPeserta.setBackground(new java.awt.Color(255, 255, 153));
        jenisPeserta.setHighlighter(null);
        jPanel2.add(jenisPeserta);
        jenisPeserta.setBounds(730, 10, 165, 30);

        labelStatusPeserta.setForeground(new java.awt.Color(0, 131, 62));
        labelStatusPeserta.setText("Status :");
        labelStatusPeserta.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelStatusPeserta.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelStatusPeserta);
        labelStatusPeserta.setBounds(370, 40, 45, 30);

        statusPeserta.setEditable(false);
        statusPeserta.setBackground(new java.awt.Color(255, 255, 153));
        statusPeserta.setHighlighter(null);
        jPanel2.add(statusPeserta);
        statusPeserta.setBounds(420, 40, 150, 30);

        labelAsalRujukan.setForeground(new java.awt.Color(0, 131, 62));
        labelAsalRujukan.setText("Asal Rujukan :");
        labelAsalRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelAsalRujukan);
        labelAsalRujukan.setBounds(620, 100, 105, 30);

        asalRujukan.setForeground(new java.awt.Color(0, 131, 62));
        asalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        asalRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(asalRujukan);
        asalRujukan.setBounds(730, 100, 170, 30);

        noTelpon.setHighlighter(null);
        jPanel2.add(noTelpon);
        noTelpon.setBounds(730, 190, 170, 30);

        katarak.setForeground(new java.awt.Color(0, 131, 62));
        katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        katarak.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        jPanel2.add(katarak);
        katarak.setBounds(730, 220, 170, 30);

        labelKatarak.setForeground(new java.awt.Color(0, 131, 62));
        labelKatarak.setText("Katarak :");
        labelKatarak.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelKatarak);
        labelKatarak.setBounds(620, 220, 105, 30);

        labelTglKLL.setForeground(new java.awt.Color(0, 131, 62));
        labelTglKLL.setText("Tgl. KLL :");
        labelTglKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelTglKLL.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelTglKLL);
        labelTglKLL.setBounds(620, 280, 105, 30);

        tglKLL.setEditable(false);
        tglKLL.setForeground(new java.awt.Color(50, 70, 50));
        tglKLL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "15-04-2024" }));
        tglKLL.setDisplayFormat("dd-MM-yyyy");
        tglKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        tglKLL.setOpaque(false);
        tglKLL.setPreferredSize(new java.awt.Dimension(64, 25));
        jPanel2.add(tglKLL);
        tglKLL.setBounds(730, 280, 170, 30);

        labelDPJP.setForeground(new java.awt.Color(0, 131, 62));
        labelDPJP.setText("Dokter DPJP :");
        labelDPJP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelDPJP.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelDPJP);
        labelDPJP.setBounds(75, 220, 150, 30);

        kodeDPJP.setEditable(false);
        kodeDPJP.setBackground(new java.awt.Color(255, 255, 153));
        kodeDPJP.setHighlighter(null);
        jPanel2.add(kodeDPJP);
        kodeDPJP.setBounds(230, 220, 80, 30);

        namaDPJP.setEditable(false);
        namaDPJP.setBackground(new java.awt.Color(255, 255, 153));
        namaDPJP.setHighlighter(null);
        jPanel2.add(namaDPJP);
        namaDPJP.setBounds(310, 220, 260, 30);

        labelKeterangan.setForeground(new java.awt.Color(0, 131, 62));
        labelKeterangan.setText("Keterangan :");
        labelKeterangan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelKeterangan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelKeterangan);
        labelKeterangan.setBounds(620, 310, 105, 30);

        keterangan.setEditable(false);
        keterangan.setHighlighter(null);
        jPanel2.add(keterangan);
        keterangan.setBounds(730, 310, 300, 30);

        labelSuplesi.setForeground(new java.awt.Color(0, 131, 62));
        labelSuplesi.setText("Suplesi :");
        labelSuplesi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelSuplesi);
        labelSuplesi.setBounds(620, 340, 105, 30);

        suplesi.setForeground(new java.awt.Color(0, 131, 62));
        suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        suplesi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        suplesi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                suplesiItemStateChanged(evt);
            }
        });
        jPanel2.add(suplesi);
        suplesi.setBounds(730, 340, 85, 30);

        noSEPSuplesi.setHighlighter(null);
        jPanel2.add(noSEPSuplesi);
        noSEPSuplesi.setBounds(880, 340, 150, 30);

        labelNoSEPSuplesi.setForeground(new java.awt.Color(0, 131, 62));
        labelNoSEPSuplesi.setText("No. SEP :");
        labelNoSEPSuplesi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelNoSEPSuplesi.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelNoSEPSuplesi);
        labelNoSEPSuplesi.setBounds(815, 340, 60, 30);

        labelProvKLL.setForeground(new java.awt.Color(0, 131, 62));
        labelProvKLL.setText("Propinsi KLL :");
        labelProvKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelProvKLL);
        labelProvKLL.setBounds(620, 370, 105, 30);

        kodeProvKLL.setEditable(false);
        kodeProvKLL.setBackground(new java.awt.Color(255, 255, 153));
        kodeProvKLL.setHighlighter(null);
        jPanel2.add(kodeProvKLL);
        kodeProvKLL.setBounds(730, 370, 60, 30);

        namaProvKLL.setEditable(false);
        namaProvKLL.setBackground(new java.awt.Color(255, 255, 153));
        namaProvKLL.setHighlighter(null);
        jPanel2.add(namaProvKLL);
        namaProvKLL.setBounds(790, 370, 240, 30);

        labelKabKLL.setForeground(new java.awt.Color(0, 131, 62));
        labelKabKLL.setText("Kabupaten KLL :");
        labelKabKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelKabKLL);
        labelKabKLL.setBounds(620, 400, 105, 30);

        kodeKabKLL.setEditable(false);
        kodeKabKLL.setBackground(new java.awt.Color(255, 255, 153));
        kodeKabKLL.setHighlighter(null);
        jPanel2.add(kodeKabKLL);
        kodeKabKLL.setBounds(730, 400, 60, 30);

        namaKabKLL.setEditable(false);
        namaKabKLL.setBackground(new java.awt.Color(255, 255, 153));
        namaKabKLL.setHighlighter(null);
        jPanel2.add(namaKabKLL);
        namaKabKLL.setBounds(790, 400, 240, 30);

        labelKecKLL.setForeground(new java.awt.Color(0, 131, 62));
        labelKecKLL.setText("Kecamatan KLL :");
        labelKecKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelKecKLL);
        labelKecKLL.setBounds(620, 430, 105, 30);

        kodeKecKLL.setEditable(false);
        kodeKecKLL.setBackground(new java.awt.Color(255, 255, 153));
        kodeKecKLL.setHighlighter(null);
        jPanel2.add(kodeKecKLL);
        kodeKecKLL.setBounds(730, 430, 60, 30);

        namaKecKLL.setEditable(false);
        namaKecKLL.setBackground(new java.awt.Color(255, 255, 153));
        namaKecKLL.setHighlighter(null);
        jPanel2.add(namaKecKLL);
        namaKecKLL.setBounds(790, 430, 240, 30);

        labelTujuanKunjungan.setForeground(new java.awt.Color(0, 131, 62));
        labelTujuanKunjungan.setText("Tujuan Kunjungan :");
        labelTujuanKunjungan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelTujuanKunjungan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelTujuanKunjungan);
        labelTujuanKunjungan.setBounds(75, 310, 150, 30);

        tujuanKunjungan.setForeground(new java.awt.Color(0, 131, 62));
        tujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Normal", "1. Prosedur", "2. Konsul Dokter" }));
        tujuanKunjungan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        tujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tujuanKunjunganItemStateChanged(evt);
            }
        });
        jPanel2.add(tujuanKunjungan);
        tujuanKunjungan.setBounds(230, 310, 340, 30);

        flagProsedur.setForeground(new java.awt.Color(0, 131, 62));
        flagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        flagProsedur.setEnabled(false);
        flagProsedur.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(flagProsedur);
        flagProsedur.setBounds(230, 340, 340, 30);

        labelFlagProsedur.setForeground(new java.awt.Color(0, 131, 62));
        labelFlagProsedur.setText("Flag Prosedur :");
        labelFlagProsedur.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelFlagProsedur.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelFlagProsedur);
        labelFlagProsedur.setBounds(75, 340, 150, 30);

        labelPenunjang.setForeground(new java.awt.Color(0, 131, 62));
        labelPenunjang.setText("Penunjang :");
        labelPenunjang.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelPenunjang.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelPenunjang);
        labelPenunjang.setBounds(75, 370, 150, 30);

        penunjang.setForeground(new java.awt.Color(0, 131, 62));
        penunjang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Radioterapi", "2. Kemoterapi", "3. Rehabilitasi Medik", "4. Rehabilitasi Psikososial", "5. Transfusi Darah", "6. Pelayanan Gigi", "7. Laboratorium", "8. USG", "9. Farmasi", "10. Lain-Lain", "11. MRI", "12. HEMODIALISA" }));
        penunjang.setEnabled(false);
        penunjang.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(penunjang);
        penunjang.setBounds(230, 370, 340, 30);

        labelAsesmenPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        labelAsesmenPelayanan.setText("Asesmen Pelayanan :");
        labelAsesmenPelayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelAsesmenPelayanan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelAsesmenPelayanan);
        labelAsesmenPelayanan.setBounds(75, 400, 150, 30);

        asesmenPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        asesmenPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        asesmenPelayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(asesmenPelayanan);
        asesmenPelayanan.setBounds(230, 400, 340, 30);

        labelTerapi.setForeground(new java.awt.Color(0, 131, 62));
        labelTerapi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTerapi.setText("Terapi / Rehabilitasi Medik");
        labelTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelTerapi.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelTerapi);
        labelTerapi.setBounds(230, 470, 340, 20);

        kodeDPJPLayanan.setEditable(false);
        kodeDPJPLayanan.setBackground(new java.awt.Color(255, 255, 153));
        kodeDPJPLayanan.setHighlighter(null);
        jPanel2.add(kodeDPJPLayanan);
        kodeDPJPLayanan.setBounds(230, 430, 80, 30);

        namaDPJPLayanan.setEditable(false);
        namaDPJPLayanan.setBackground(new java.awt.Color(255, 255, 153));
        namaDPJPLayanan.setHighlighter(null);
        jPanel2.add(namaDPJPLayanan);
        namaDPJPLayanan.setBounds(310, 430, 260, 30);

        pilihDPJP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        pilihDPJP.setMnemonic('X');
        pilihDPJP.setToolTipText("Alt+X");
        pilihDPJP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pilihDPJP.setGlassColor(new java.awt.Color(238, 238, 255));
        pilihDPJP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihDPJPActionPerformed(evt);
            }
        });
        jPanel2.add(pilihDPJP);
        pilihDPJP.setBounds(570, 220, 40, 30);

        labelLakaLantas.setForeground(new java.awt.Color(0, 131, 62));
        labelLakaLantas.setText("Laka Lantas :");
        labelLakaLantas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelLakaLantas);
        labelLakaLantas.setBounds(620, 250, 105, 30);

        labelNoTelpon.setForeground(new java.awt.Color(0, 131, 62));
        labelNoTelpon.setText("No. Telpon :");
        labelNoTelpon.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelNoTelpon.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelNoTelpon);
        labelNoTelpon.setBounds(620, 190, 105, 30);

        labelTglLahir.setForeground(new java.awt.Color(0, 131, 62));
        labelTglLahir.setText("Tgl. Lahir :");
        labelTglLahir.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelTglLahir.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelTglLahir);
        labelTglLahir.setBounds(75, 40, 150, 30);

        labelNIK.setForeground(new java.awt.Color(0, 131, 62));
        labelNIK.setText("NIK :");
        labelNIK.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelNIK);
        labelNIK.setBounds(620, 40, 105, 30);

        noSKDP.setEditable(false);
        noSKDP.setBackground(new java.awt.Color(255, 255, 153));
        noSKDP.setHighlighter(null);
        jPanel2.add(noSKDP);
        noSKDP.setBounds(230, 70, 340, 30);

        labelRujukan.setForeground(new java.awt.Color(0, 131, 62));
        labelRujukan.setText("No. Rujukan :");
        labelRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelRujukan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelRujukan);
        labelRujukan.setBounds(75, 100, 150, 30);

        nik.setEditable(false);
        nik.setBackground(new java.awt.Color(255, 255, 153));
        nik.setHighlighter(null);
        jPanel2.add(nik);
        nik.setBounds(730, 40, 300, 30);

        labelNoKartu.setForeground(new java.awt.Color(0, 131, 62));
        labelNoKartu.setText("No. Kartu :");
        labelNoKartu.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelNoKartu);
        labelNoKartu.setBounds(620, 70, 105, 30);

        pilihPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        pilihPoli.setMnemonic('X');
        pilihPoli.setToolTipText("Alt+X");
        pilihPoli.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pilihPoli.setGlassColor(new java.awt.Color(238, 238, 255));
        pilihPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihPoliActionPerformed(evt);
            }
        });
        jPanel2.add(pilihPoli);
        pilihPoli.setBounds(570, 190, 40, 30);

        pilihDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        pilihDiagnosa.setMnemonic('X');
        pilihDiagnosa.setToolTipText("Alt+X");
        pilihDiagnosa.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pilihDiagnosa.setGlassColor(new java.awt.Color(238, 238, 255));
        pilihDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihDiagnosaActionPerformed(evt);
            }
        });
        jPanel2.add(pilihDiagnosa);
        pilihDiagnosa.setBounds(570, 160, 40, 30);

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
        jPanel2.add(pilihRujukan);
        pilihRujukan.setBounds(570, 100, 40, 30);

        lihatRiwayatPelayananBPJS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        lihatRiwayatPelayananBPJS.setMnemonic('X');
        lihatRiwayatPelayananBPJS.setText("Riwayat Layanan BPJS");
        lihatRiwayatPelayananBPJS.setToolTipText("Alt+X");
        lihatRiwayatPelayananBPJS.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        lihatRiwayatPelayananBPJS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lihatRiwayatPelayananBPJS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lihatRiwayatPelayananBPJSActionPerformed(evt);
            }
        });
        jPanel2.add(lihatRiwayatPelayananBPJS);
        lihatRiwayatPelayananBPJS.setBounds(1040, 130, 220, 30);

        kodeDokterTerapi.setEditable(false);
        kodeDokterTerapi.setBackground(new java.awt.Color(255, 255, 153));
        kodeDokterTerapi.setHighlighter(null);
        jPanel2.add(kodeDokterTerapi);
        kodeDokterTerapi.setBounds(230, 520, 80, 30);

        kodePoliTerapi.setEditable(false);
        kodePoliTerapi.setBackground(new java.awt.Color(255, 255, 153));
        kodePoliTerapi.setHighlighter(null);
        jPanel2.add(kodePoliTerapi);
        kodePoliTerapi.setBounds(230, 490, 80, 30);

        namaPoliTerapi.setEditable(false);
        namaPoliTerapi.setBackground(new java.awt.Color(255, 255, 153));
        namaPoliTerapi.setHighlighter(null);
        jPanel2.add(namaPoliTerapi);
        namaPoliTerapi.setBounds(310, 490, 260, 30);

        namaDokterTerapi.setEditable(false);
        namaDokterTerapi.setBackground(new java.awt.Color(255, 255, 153));
        namaDokterTerapi.setHighlighter(null);
        jPanel2.add(namaDokterTerapi);
        namaDokterTerapi.setBounds(310, 520, 260, 30);

        pilihDokterTerapi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        pilihDokterTerapi.setMnemonic('X');
        pilihDokterTerapi.setToolTipText("Alt+X");
        pilihDokterTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pilihDokterTerapi.setGlassColor(new java.awt.Color(238, 238, 255));
        pilihDokterTerapi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihDokterTerapiActionPerformed(evt);
            }
        });
        jPanel2.add(pilihDokterTerapi);
        pilihDokterTerapi.setBounds(570, 520, 40, 30);

        pilihPoliTerapi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        pilihPoliTerapi.setMnemonic('X');
        pilihPoliTerapi.setToolTipText("Alt+X");
        pilihPoliTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pilihPoliTerapi.setGlassColor(new java.awt.Color(238, 238, 255));
        pilihPoliTerapi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihPoliTerapiActionPerformed(evt);
            }
        });
        jPanel2.add(pilihPoliTerapi);
        pilihPoliTerapi.setBounds(570, 490, 40, 30);

        labelDokterTerapi.setForeground(new java.awt.Color(0, 131, 62));
        labelDokterTerapi.setText("Dokter Terapi :");
        labelDokterTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelDokterTerapi.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelDokterTerapi);
        labelDokterTerapi.setBounds(75, 520, 150, 30);

        approvalFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/approvalfp.png"))); // NOI18N
        approvalFP.setMnemonic('X');
        approvalFP.setText("Approval FP BPJS");
        approvalFP.setToolTipText("Alt+X");
        approvalFP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        approvalFP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        approvalFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approvalFPActionPerformed(evt);
            }
        });
        jPanel2.add(approvalFP);
        approvalFP.setBounds(1040, 250, 190, 60);

        pengajuanFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pengajuan.png"))); // NOI18N
        pengajuanFP.setMnemonic('X');
        pengajuanFP.setText("Pengajuan FP BPJS");
        pengajuanFP.setToolTipText("Alt+X");
        pengajuanFP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pengajuanFP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pengajuanFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pengajuanFPActionPerformed(evt);
            }
        });
        jPanel2.add(pengajuanFP);
        pengajuanFP.setBounds(1040, 190, 190, 60);

        labelDPJPLayanan.setForeground(new java.awt.Color(0, 131, 62));
        labelDPJPLayanan.setText("DPJP Layanan :");
        labelDPJPLayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelDPJPLayanan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelDPJPLayanan);
        labelDPJPLayanan.setBounds(75, 430, 150, 30);

        labelPoliTerapi.setForeground(new java.awt.Color(0, 131, 62));
        labelPoliTerapi.setText("Poli Terapi :");
        labelPoliTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelPoliTerapi.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelPoliTerapi);
        labelPoliTerapi.setBounds(75, 490, 150, 30);

        labelJumlahBarcode.setForeground(new java.awt.Color(0, 131, 62));
        labelJumlahBarcode.setText("Jumlah Barcode :");
        labelJumlahBarcode.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelJumlahBarcode);
        labelJumlahBarcode.setBounds(1040, 70, 105, 30);

        jumlahBarcode.setText("3");
        jumlahBarcode.setHighlighter(null);
        jPanel2.add(jumlahBarcode);
        jumlahBarcode.setBounds(1150, 70, 40, 30);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(238, 238, 255));
        jPanel3.setMinimumSize(new java.awt.Dimension(533, 120));
        jPanel3.setPreferredSize(new java.awt.Dimension(533, 120));

        simpan.setForeground(new java.awt.Color(0, 131, 62));
        simpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        simpan.setMnemonic('S');
        simpan.setText("Konfirmasi");
        simpan.setToolTipText("Alt+S");
        simpan.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        simpan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        simpan.setIconTextGap(5);
        simpan.setPreferredSize(new java.awt.Dimension(300, 45));
        simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanActionPerformed(evt);
            }
        });
        simpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                simpanKeyPressed(evt);
            }
        });
        jPanel3.add(simpan);

        fingerprint.setForeground(new java.awt.Color(0, 131, 62));
        fingerprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        fingerprint.setMnemonic('K');
        fingerprint.setText("FINGERPRINT");
        fingerprint.setToolTipText("Alt+K");
        fingerprint.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        fingerprint.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        fingerprint.setIconTextGap(5);
        fingerprint.setPreferredSize(new java.awt.Dimension(300, 45));
        fingerprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fingerprintActionPerformed(evt);
            }
        });
        fingerprint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fingerprintKeyPressed(evt);
            }
        });
        jPanel3.add(fingerprint);

        batal.setForeground(new java.awt.Color(0, 131, 62));
        batal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/reset.png"))); // NOI18N
        batal.setMnemonic('K');
        batal.setText("Batal");
        batal.setToolTipText("Alt+K");
        batal.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        batal.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        batal.setIconTextGap(5);
        batal.setPreferredSize(new java.awt.Dimension(300, 45));
        batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batalActionPerformed(evt);
            }
        });
        batal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                batalKeyPressed(evt);
            }
        });
        jPanel3.add(batal);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void batalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_batalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            batalActionPerformed(null);
        }
    }//GEN-LAST:event_batalKeyPressed

    private void batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batalActionPerformed
        dispose();
    }//GEN-LAST:event_batalActionPerformed

    private void simpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_simpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            simpanActionPerformed(null);
        }
    }//GEN-LAST:event_simpanKeyPressed

    private void simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cekStatusFP();
        if (noRawat.trim().isBlank() || namaPasien.getText().trim().isBlank()) {
            valid.textKosong(noRM, "Pasien");
        } else if (noKartu.getText().trim().isBlank()) {
            valid.textKosong(noKartu, "Nomor Kartu");
        } else if (! query.cariBooleanSmc("select * from pasien where no_rkm_medis = ?", noRM.getText())) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, no. RM tidak ditemukan!");
        } else if (kodePPK.getText().trim().isBlank() || namaPPK.getText().trim().isBlank()) {
            valid.textKosong(kodePPK, "PPK Rujukan");
        } else if (kodePPKPelayanan.getText().trim().isBlank() || namaPPKPelayanan.getText().trim().isBlank()) {
            valid.textKosong(kodePPKPelayanan, "PPK Pelayanan");
        } else if (kodeDiagnosa.getText().trim().isBlank() || namaDiagnosa.getText().trim().isBlank()) {
            valid.textKosong(kodeDiagnosa, "Diagnosa");
        } else if (catatan.getText().trim().isBlank()) {
            valid.textKosong(catatan, "Catatan");
        } else if ((jenisPelayanan.getSelectedIndex() == 1) && (kodePoli.getText().trim().isBlank() || namaPoli.getText().trim().isBlank())) {
            valid.textKosong(kodePoli, "Poli Tujuan");
        } else if ((lakaLantas.getSelectedIndex() == 1) && keterangan.getText().isBlank()) {
            valid.textKosong(keterangan, "Keterangan");
        } else if (kodeDPJP.getText().trim().isBlank() || namaDPJP.getText().trim().isBlank()) {
            valid.textKosong(kodeDPJP, "DPJP");
        } else if (! statusFP
            && query.cariIntegerSmc("select timestampdiff(year, ?, CURRENT_DATE())", tglLahir.getText()) >= 17
            && jenisPelayanan.getSelectedIndex() != 0
            && ! kodePoli.getText().equals("IGD")
        ) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Pasien belum melakukan fingerprint");
            bukaAplikasiFingerprint();
        } else {
            kodePoliRS = (kodePoliTerapi.getText().isBlank()
                ? query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText())
                : kodePoliTerapi.getText());
            kodeDokterRS = (kodeDokterTerapi.getText().isBlank()
                ? query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDPJP.getText())
                : kodeDokterTerapi.getText());
            cekBiayaRegistrasi();
            cekStatusPasien();
            autoNomorRegistrasi();
            if (query.cariIntegerSmc("select count(*) from reg_periksa where no_rkm_medis = ? and tgl_registrasi = ? and kd_poli = ? and kd_dokter = ? and kd_pj = ?", noRM.getText(), valid.setTglSmc(tglSEP), kodePoliRS, kodeDokterRS, query.cariIsiSmc("select kd_pj from password_asuransi")) > 0) {
                JOptionPane.showMessageDialog(rootPane, "Maaf, Telah terdaftar pemeriksaan hari ini!\nMohon konfirmasi ke Bagian Admisi!");
                resetForm();
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }
            if (! registerPasien()) {
                JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan pada saat pendaftaran pasien!");
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }
            if (jenisPelayanan.getSelectedIndex() == 0) {
                insertSEP();
            } else if (jenisPelayanan.getSelectedIndex() == 1) {
                if (namaPoli.getText().toLowerCase().contains("darurat") && query.cariIntegerSmc(
                    "select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan like '%darurat%'",
                    noKartu.getText(), jenisPelayanan.getSelectedItem().toString().substring(0, 1), valid.setTglSmc(tglSEP)
                ) >= 3) {
                    JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                } else if (! namaPoli.getText().toLowerCase().contains("darurat") && query.cariIntegerSmc(
                    "select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan not like '%darurat%'",
                    noKartu.getText(), jenisPelayanan.getSelectedItem().toString().substring(0, 1), valid.setTglSmc(tglSEP)
                ) >= 1) {
                    JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                } else {
                    simpanAntrianOnSite();
                    insertSEP();
                }
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_simpanActionPerformed

    private void pilihDPJPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihDPJPActionPerformed
        dokter.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        dokter.setLocationRelativeTo(jPanel1);
        dokter.carinamadokter(kodePoli.getText(), namaPoli.getText());
        dokter.setVisible(true);
    }//GEN-LAST:event_pilihDPJPActionPerformed

    private void tujuanKunjunganItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tujuanKunjunganItemStateChanged
        if (tujuanKunjungan.getSelectedIndex() == 0) {
            flagProsedur.setEnabled(false);
            flagProsedur.setSelectedIndex(0);
            penunjang.setEnabled(false);
            penunjang.setSelectedIndex(0);
            asesmenPelayanan.setEnabled(true);
        } else {
            if (tujuanKunjungan.getSelectedIndex() == 1) {
                asesmenPelayanan.setSelectedIndex(0);
                asesmenPelayanan.setEnabled(false);
            } else {
                asesmenPelayanan.setEnabled(true);
            }
            if (flagProsedur.getSelectedIndex() == 0) {
                flagProsedur.setSelectedIndex(2);
            }
            flagProsedur.setEnabled(true);
            if (penunjang.getSelectedIndex() == 0) {
                penunjang.setSelectedIndex(10);
            }
            penunjang.setEnabled(true);
        }
    }//GEN-LAST:event_tujuanKunjunganItemStateChanged

    private void lakaLantasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lakaLantasItemStateChanged
        if (lakaLantas.getSelectedIndex() == 0) {
            labelTglKLL.setVisible(false);
            tglKLL.setDate(new Date());
            tglKLL.setVisible(false);
            labelKeterangan.setVisible(false);
            keterangan.setText("");
            keterangan.setVisible(false);
            labelSuplesi.setVisible(false);
            suplesi.setSelectedIndex(0);
            suplesi.setVisible(false);
            labelNoSEPSuplesi.setVisible(false);
            noSEPSuplesi.setText("");
            noSEPSuplesi.setVisible(false);
            labelProvKLL.setVisible(false);
            kodeProvKLL.setText("");
            kodeProvKLL.setVisible(false);
            namaProvKLL.setText("");
            namaProvKLL.setVisible(false);
            labelKabKLL.setVisible(false);
            kodeKabKLL.setText("");
            kodeKabKLL.setVisible(false);
            namaKabKLL.setText("");
            namaKabKLL.setVisible(false);
            labelKecKLL.setVisible(false);
            kodeKecKLL.setText("");
            kodeKecKLL.setVisible(false);
            namaKecKLL.setText("");
            namaKecKLL.setVisible(false);
            labelCatatan.setLocation(620, 280);
            catatan.setLocation(730, 280);
        } else {
            labelTglKLL.setVisible(true);
            tglKLL.setVisible(true);
            labelKeterangan.setVisible(true);
            keterangan.setVisible(true);
            labelSuplesi.setVisible(true);
            suplesi.setVisible(true);
            labelNoSEPSuplesi.setVisible(true);
            noSEPSuplesi.setVisible(true);
            labelProvKLL.setVisible(true);
            kodeProvKLL.setVisible(true);
            namaProvKLL.setVisible(true);
            labelKabKLL.setVisible(true);
            kodeKabKLL.setVisible(true);
            namaKabKLL.setVisible(true);
            labelKecKLL.setVisible(true);
            kodeKecKLL.setVisible(true);
            namaKecKLL.setVisible(true);
            labelCatatan.setLocation(620, 460);
            catatan.setLocation(730, 460);
        }
    }//GEN-LAST:event_lakaLantasItemStateChanged

    private void jenisPelayananItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jenisPelayananItemStateChanged
        if (jenisPelayanan.getSelectedIndex() == 0) {
            labelPoli.setVisible(false);
            kodePoli.setText("");
            kodePoli.setVisible(false);
            namaPoli.setText("");
            namaPoli.setVisible(false);
            kodeDPJPLayanan.setText("");
            namaDPJPLayanan.setText("");
            pilihPoli.setVisible(false);
        } else if (jenisPelayanan.getSelectedIndex() == 1) {
            labelPoli.setVisible(true);
            kodePoli.setVisible(true);
            namaPoli.setVisible(true);
            pilihPoli.setVisible(true);
        }
    }//GEN-LAST:event_jenisPelayananItemStateChanged

    private void fingerprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fingerprintActionPerformed
        bukaAplikasiFingerprint();
    }//GEN-LAST:event_fingerprintActionPerformed

    private void fingerprintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fingerprintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            bukaAplikasiFingerprint();
        }
    }//GEN-LAST:event_fingerprintKeyPressed

    private void pilihPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihPoliActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        poli.setSize(jPanel1.getWidth() - 100, jPanel1.getHeight() - 100);
        poli.tampil();
        poli.setLocationRelativeTo(jPanel1);
        poli.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_pilihPoliActionPerformed

    private void pilihDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihDiagnosaActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cariDiagnosa.setSize(jPanel1.getWidth() - 100, jPanel1.getHeight() - 100);
        cariDiagnosa.setLocationRelativeTo(jPanel1);
        cariDiagnosa.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_pilihDiagnosaActionPerformed

    private void pilihRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihRujukanActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (noKartu.getText().trim().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "No. Kartu masih kosong...!!");
        } else {
            cariRujukan.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
            cariRujukan.setLocationRelativeTo(jPanel1);
            cariRujukan.tampil(noKartu.getText(), namaPasien.getText());
            cariRujukan.setVisible(true);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_pilihRujukanActionPerformed

    private void lihatRiwayatPelayananBPJSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lihatRiwayatPelayananBPJSActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        historiPelayanan.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
        historiPelayanan.setLocationRelativeTo(jPanel1);
        historiPelayanan.setKartu(noKartu.getText());
        historiPelayanan.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_lihatRiwayatPelayananBPJSActionPerformed

    private void pilihDokterTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihDokterTerapiActionPerformed
        doktermapping.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        doktermapping.tampilDokterTerapi(kodeDPJPLayanan.getText());
        doktermapping.setLocationRelativeTo(jPanel1);
        doktermapping.setVisible(true);
    }//GEN-LAST:event_pilihDokterTerapiActionPerformed

    private void pilihPoliTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihPoliTerapiActionPerformed
        polimapping.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        polimapping.tampilPoliMapping(kodePoli.getText());
        polimapping.setLocationRelativeTo(jPanel1);
        polimapping.setVisible(true);
    }//GEN-LAST:event_pilihPoliTerapiActionPerformed

    private void approvalFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approvalFPActionPerformed
        pwUserId.setText("");
        pwPass.setText("");
        WindowAksi.setSize(400, 300);
        WindowAksi.setLocationRelativeTo(null);
        WindowAksi.setVisible(true);
        if (! noKartu.getText().isBlank()) {
            aksiFP = "approval";
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_approvalFPActionPerformed

    private void pengajuanFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pengajuanFPActionPerformed
        pwUserId.setText("");
        pwPass.setText("");
        WindowAksi.setSize(400, 300);
        WindowAksi.setLocationRelativeTo(null);
        WindowAksi.setVisible(true);
        if (!noKartu.getText().isBlank()) {
            aksiFP = "pengajuan";
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_pengajuanFPActionPerformed

    private void btnAksiKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiKonfirmasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (! noKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
            this.setCursor(Cursor.getDefaultCursor());
            return;
        }
        try (PreparedStatement ps = koneksi.prepareStatement("select id_user from user where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi') limit 1")) {
            ps.setString(1, new String(pwUserId.getPassword()));
            ps.setString(2, new String(pwPass.getPassword()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (aksiFP.equals("pengajuan")) {
                        try {
                            utc = api.getUTCTimestamp();
                            url = koneksiDB.URLAPIBPJS() + "/Sep/pengajuanSEP";
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            json = " {"
                                + "\"request\": {"
                                + "\"t_sep\": {"
                                + "\"noKartu\": \"" + noKartu.getText() + "\","
                                + "\"tglSep\": \"" + valid.setTglSmc(tglSEP) + "\","
                                + "\"jnsPelayanan\": \"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                                + "\"jnsPengajuan\": \"2\","
                                + "\"keterangan\": \"Pengajuan SEP Finger oleh Anjungan Pasien Mandiri " + namaPPKPelayanan.getText() + "\","
                                + "\"user\": \"RM:" + noRM.getText() + "\""
                                + "}"
                                + "}"
                                + "}";
                            entity = new HttpEntity(json, headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                            metaData = root.path("metaData");
                            if (metaData.path("code").asText().equals("200")) {
                                JOptionPane.showMessageDialog(rootPane, "Pengajuan Berhasil");
                            } else {
                                JOptionPane.showMessageDialog(rootPane, metaData.path("message").asText());
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                            if (e.toString().contains("UnknownHostException")) {
                                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!!!");
                            }
                        }
                    } else if (aksiFP.equals("approval")) {
                        try {
                            utc = api.getUTCTimestamp();
                            url = koneksiDB.URLAPIBPJS() + "/Sep/aprovalSEP";
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            json = " {"
                                + "\"request\": {"
                                + "\"t_sep\": {"
                                + "\"noKartu\": \"" + noKartu.getText() + "\","
                                + "\"tglSep\": \"" + valid.setTglSmc(tglSEP) + "\","
                                + "\"jnsPelayanan\": \"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                                + "\"jnsPengajuan\": \"2\","
                                + "\"keterangan\": \"Approval Fingerprint karena Gagal FP melalui Anjungan Pasien Mandiri " + namaPPKPelayanan.getText() + "\","
                                + "\"user\": \"RM:" + noRM.getText() + "\""
                                + "}"
                                + "}"
                                + "}";
                            entity = new HttpEntity(json, headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                            metaData = root.path("metaData");
                            if (metaData.path("code").asText().equals("200")) {
                                JOptionPane.showMessageDialog(rootPane, "Approval berhasil!");
                            } else {
                                JOptionPane.showMessageDialog(rootPane, metaData.path("message").asText());
                            }
                        } catch (Exception ex) {
                            System.out.println("Notif : " + ex);
                            if (ex.toString().contains("UnknownHostException")) {
                                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Anda tidak diizinkan untuk melakukan aksi ini...!!!");
                    }
                } else {
                    JOptionPane.showMessageDialog(rootPane, "User ID atau password salah!");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
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

    private void suplesiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_suplesiItemStateChanged
        if (suplesi.getSelectedIndex() == 1) {
            noSEPSuplesi.setEditable(true);
        } else {
            noSEPSuplesi.setEditable(false);
            noSEPSuplesi.setText("");
        }
    }//GEN-LAST:event_suplesiItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgRegistrasiSEPPertama dialog = new DlgRegistrasiSEPPertama(new javax.swing.JFrame(), true);
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
    private widget.Button approvalFP;
    private widget.ComboBox asalRujukan;
    private widget.ComboBox asesmenPelayanan;
    private component.Button batal;
    private widget.Button btnAksiBatal;
    private widget.Button btnAksiKonfirmasi;
    private widget.TextBox catatan;
    private component.Button fingerprint;
    private widget.ComboBox flagProsedur;
    private widget.InternalFrame internalFrame1;
    private component.Panel jPanel1;
    private component.Panel jPanel2;
    private javax.swing.JPanel jPanel3;
    private widget.ComboBox jenisPelayanan;
    private widget.TextBox jenisPeserta;
    private widget.TextBox jk;
    private widget.TextBox jumlahBarcode;
    private widget.ComboBox katarak;
    private widget.ComboBox kelas;
    private widget.TextBox keterangan;
    private widget.TextBox kodeDPJP;
    private widget.TextBox kodeDPJPLayanan;
    private widget.TextBox kodeDiagnosa;
    private widget.TextBox kodeDokterTerapi;
    private widget.TextBox kodeKabKLL;
    private widget.TextBox kodeKecKLL;
    private widget.TextBox kodePPK;
    private widget.TextBox kodePPKPelayanan;
    private widget.TextBox kodePoli;
    private widget.TextBox kodePoliTerapi;
    private widget.TextBox kodeProvKLL;
    private widget.Label labekJK;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label labelAsalRujukan;
    private widget.Label labelAsesmenPelayanan;
    private widget.Label labelCatatan;
    private widget.Label labelDPJP;
    private widget.Label labelDPJPLayanan;
    private widget.Label labelDataPasien;
    private widget.Label labelDiagnosa;
    private widget.Label labelDokterTerapi;
    private widget.Label labelFlagProsedur;
    private widget.Label labelJenisPelayanan;
    private widget.Label labelJenisPeserta;
    private widget.Label labelJumlahBarcode;
    private widget.Label labelKabKLL;
    private widget.Label labelKatarak;
    private widget.Label labelKecKLL;
    private widget.Label labelKelas;
    private widget.Label labelKeterangan;
    private widget.Label labelLakaLantas;
    private widget.Label labelNIK;
    private widget.Label labelNoKartu;
    private widget.Label labelNoSEPSuplesi;
    private widget.Label labelNoSKDP;
    private widget.Label labelNoTelpon;
    private widget.Label labelPPK;
    private widget.Label labelPPKPelayanan;
    private widget.Label labelPenunjang;
    private widget.Label labelPoli;
    private widget.Label labelPoliTerapi;
    private widget.Label labelProvKLL;
    private widget.Label labelRujukan;
    private widget.Label labelStatusPeserta;
    private widget.Label labelSuplesi;
    private widget.Label labelTerapi;
    private widget.Label labelTglKLL;
    private widget.Label labelTglLahir;
    private widget.Label labelTglRujukan;
    private widget.Label labelTglSEP;
    private widget.Label labelTujuanKunjungan;
    private widget.ComboBox lakaLantas;
    private widget.Button lihatRiwayatPelayananBPJS;
    private widget.TextBox namaDPJP;
    private widget.TextBox namaDPJPLayanan;
    private widget.TextBox namaDiagnosa;
    private widget.TextBox namaDokterTerapi;
    private widget.TextBox namaKabKLL;
    private widget.TextBox namaKecKLL;
    private widget.TextBox namaPPK;
    private widget.TextBox namaPPKPelayanan;
    private widget.TextBox namaPasien;
    private widget.TextBox namaPoli;
    private widget.TextBox namaPoliTerapi;
    private widget.TextBox namaProvKLL;
    private widget.TextBox nik;
    private widget.TextBox noKartu;
    private widget.TextBox noRM;
    private widget.TextBox noRujukan;
    private widget.TextBox noSEPSuplesi;
    private widget.TextBox noSKDP;
    private widget.TextBox noTelpon;
    private widget.Button pengajuanFP;
    private widget.ComboBox penunjang;
    private widget.Button pilihDPJP;
    private widget.Button pilihDiagnosa;
    private widget.Button pilihDokterTerapi;
    private widget.Button pilihPoli;
    private widget.Button pilihPoliTerapi;
    private widget.Button pilihRujukan;
    private widget.PasswordBox pwPass;
    private widget.PasswordBox pwUserId;
    private component.Button simpan;
    private widget.TextBox statusPeserta;
    private widget.ComboBox suplesi;
    private widget.Tanggal tglKLL;
    private widget.TextBox tglLahir;
    private widget.Tanggal tglRujukan;
    private widget.Tanggal tglSEP;
    private widget.ComboBox tujuanKunjungan;
    // End of variables declaration//GEN-END:variables

    private void autoNomorRegistrasi() {
        switch (koneksiDB.URUTNOREG()) {
            case "poli":
                noReg = query.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and tgl_registrasi = ?",
                    kodePoliRS, valid.setTglSmc(tglSEP)
                );
                break;
            case "dokter":
                noReg = query.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and tgl_registrasi = ?",
                    kodeDokterRS, valid.setTglSmc(tglSEP)
                );
                break;
            case "dokter + poli":
                noReg = query.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                    kodePoliRS, kodeDokterRS, valid.setTglSmc(tglSEP)
                );
                break;
            default:
                noReg = query.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                    kodePoliRS, kodeDokterRS, valid.setTglSmc(tglSEP)
                );
                break;
        }

        noRawat = query.cariIsiSmc("select concat(date_format(tgl_registrasi, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_rawat, 6), signed)), 0) + 1, 6, '0')) from reg_periksa where tgl_registrasi = ?", valid.setTglSmc(tglSEP));
    }

    private void cekStatusPasien() {
        String sql = "select nm_pasien, namakeluarga, keluarga, concat_ws(', ', alamat, nm_kel, nm_kec, nm_kab) asal, if (tgl_daftar = ?, 'baru', 'lama') as daftar, " +
            "timestampdiff(year, tgl_lahir, curdate()) as tahun, (timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12)) as bulan, " +
            "timestampdiff(day, date_add(date_add(tgl_lahir, interval timestampdiff(year, tgl_lahir, curdate()) year), interval timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12) month), curdate()) as hari " +
            "from pasien join kelurahan on pasien.kd_kel = kelurahan.kd_kel join kecamatan on pasien.kd_kec = kecamatan.kd_kec join kabupaten on pasien.kd_kab = kabupaten.kd_kab where no_rkm_medis = ?";
        
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setDate(1, (java.sql.Date) tglSEP.getDate());
            ps.setString(2, noRM.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    alamat = rs.getString("asal");
                    namaPJ = rs.getString("namakeluarga");
                    hubunganPJ = rs.getString("keluarga");
                    umur = "0";
                    statusUmur = "Th";
                    statusRegistrasi = rs.getString("daftar");
                    if (rs.getInt("tahun") > 0) {
                        umur = rs.getString("tahun");
                        statusUmur = "Th";
                    } else if (rs.getInt("tahun") == 0 && rs.getInt("bulan") > 0) {
                        umur = rs.getString("bulan");
                        statusUmur = "Bl";
                    } else {
                        umur = rs.getString("hari");
                        statusUmur = "Hr";
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        
        statusPoli = "Baru";
        if (query.cariBooleanSmc("select * from reg_periksa where no_rkm_medis = ? and kd_poli = ?", noRM.getText(), kodePoliRS)) {
            statusPoli = "Lama";
        }
    }

    private void cetakRegistrasi(String noSEP) {
        Map<String, Object> param = new HashMap<>();
        param.put("norawat", noRawat);
        param.put("parameter", noSEP);
        param.put("namars", query.cariIsiSmc("select nama_instansi from setting"));
        param.put("kotars", query.cariIsiSmc("select kabupaten from setting"));
        
        if (jenisPelayanan.getSelectedIndex() == 0) {
            valid.printReport("rptBridgingSEPAPM1.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            valid.MyReport("rptBridgingSEPAPM1.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        } else {
            valid.printReport("rptBridgingSEPAPM2.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            valid.MyReport("rptBridgingSEPAPM2.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        }
        
        valid.printReport("rptBarcodeRawatAPM.jasper", koneksiDB.PRINTER_BARCODE(), "::[ Barcode Perawatan ]::", Integer.parseInt(jumlahBarcode.getText()), param);
        valid.MyReport("rptBarcodeRawatAPM.jasper", "report", "::[ Barcode Perawatan ]::", param);
    }

    private void insertSEP() {
        String tglLakaLantas = "";
        try {
            if (lakaLantas.getSelectedIndex() > 0) {
                tglLakaLantas = valid.SetTgl(tglKLL.getSelectedItem() + "");
            }
            utc = api.getUTCTimestamp();
            url = koneksiDB.URLAPIBPJS() + "/SEP/2.0/insert";
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            json = "{"
                + "\"request\":{"
                + "\"t_sep\":{"
                + "\"noKartu\":\"" + noKartu.getText() + "\","
                + "\"tglSep\":\"" + valid.setTglSmc(tglSEP) + "\","
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
                + "\"tglRujukan\":\"" + valid.setTglSmc(tglRujukan) + "\","
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
                + "\"tglKejadian\": \"" + tglLakaLantas + "\","
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
                + "\"kdPenunjang\": \"" + (penunjang.getSelectedIndex() > 0 ? penunjang.getSelectedItem().toString().substring(0, 1) : "") + "\","
                + "\"assesmentPel\": \"" + (asesmenPelayanan.getSelectedIndex() > 0 ? asesmenPelayanan.getSelectedItem().toString().substring(0, 1) : "") + "\","
                + "\"skdp\": {"
                + "\"noSurat\": \"" + noSKDP.getText() + "\","
                + "\"kodeDPJP\": \"" + kodeDPJP.getText() + "\""
                + "},"
                + "\"dpjpLayan\": \"" + (kodeDPJPLayanan.getText().isBlank() ? "" : kodeDPJPLayanan.getText()) + "\","
                + "\"noTelp\": \"" + noTelpon.getText() + "\","
                + "\"user\":\"" + noKartu.getText() + "\""
                + "}"
                + "}"
                + "}";
            entity = new HttpEntity(json, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
            metaData = root.path("metaData");
            JOptionPane.showMessageDialog(rootPane, metaData.path("message").asText());
            if (metaData.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep");
                System.out.println("No. SEP : " + response.asText());
                
                String isNoRawat = query.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ?", valid.setTglSmc(tglSEP), noRM.getText(), kodePoliRS, kodeDokterRS);
                
                if (isNoRawat == null || (! isNoRawat.equals(noRawat))) {
                    System.out.println("======================================================");
                    System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
                    System.out.println("No. Rawat: " + noRawat);
                    System.out.println("Tgl. Registrasi: " + valid.setTglSmc(tglSEP));
                    System.out.println("No. Antrian: " + noReg + " (Ditemukan: " + query.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", noRawat) + ")");
                    System.out.println("No. RM: " + noRM.getText() + " (Ditemukan: " + query.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", noRawat) + ")");
                    System.out.println("Kode Dokter: " + kodeDokterRS + " (Ditemukan: " + query.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", noRawat) + ")");
                    System.out.println("Kode Poli: " + kodePoliRS  + " (Ditemukan: " + query.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", noRawat) + ")");
                    System.out.println("======================================================");

                    return;
                }
                
                query.menyimpanSmc("bridging_sep", null,
                    response.asText(),
                    noRawat,
                    valid.setTglSmc(tglSEP),
                    valid.SetTgl(tglRujukan.getSelectedItem().toString()),
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
                    noTelpon.getText(),
                    katarak.getSelectedItem().toString(),
                    tglLakaLantas,
                    keterangan.getText(),
                    suplesi.getSelectedItem().toString(),
                    noSEPSuplesi.getText(),
                    kodeProvKLL.getText(),
                    namaProvKLL.getText(),
                    kodeKabKLL.getText(),
                    namaKabKLL.getText(),
                    kodeKecKLL.getText(),
                    namaKecKLL.getText(),
                    noSKDP.getText(),
                    kodeDPJP.getText(),
                    namaDPJP.getText(),
                    tujuanKunjungan.getSelectedItem().toString().substring(0, 1),
                    (flagProsedur.getSelectedIndex() > 0 ? flagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
                    (penunjang.getSelectedIndex() > 0 ? penunjang.getSelectedItem().toString().substring(0, 1) : ""),
                    (asesmenPelayanan.getSelectedIndex() > 0 ? asesmenPelayanan.getSelectedItem().toString().substring(0, 1) : ""),
                    kodeDPJPLayanan.getText(),
                    namaDPJPLayanan.getText()
                );
                
                if (! simpanRujukan()) {
                    System.out.println("Terjadi kesalahan pada saat proses rujukan masuk pasien!");
                }
                
                
                if (jenisPelayanan.getSelectedIndex() == 1) {
                    query.mengupdateSmc("bridging_sep", "tglpulang = ?", "no_sep = ?", valid.setTglSmc(tglSEP), response.asText());
                }

                if (! prb.isBlank()) {
                    query.menyimpanSmc("bpjs_prb", null, response.asText(), prb);
                    
                    prb = "";
                }
                
                if (query.cariIntegerSmc("select count(*) from booking_registrasi where no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ? and status != 'Terdaftar'",
                    noRM.getText(), valid.setTglSmc(tglSEP), kodeDokterRS, kodePoliRS
                ) == 1) {
                    query.mengupdateSmc("booking_registrasi", "status = 'Terdaftar', waktu_kunjungan = now()", "no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ?", noRM.getText(), valid.setTglSmc(tglSEP), kodeDokterRS, kodePoliRS);
                }

                cetakRegistrasi(response.asText());

                resetForm();
                dispose();
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private void cekStatusFP() {
        statusFP = false;
        if (! noKartu.getText().isBlank()) {
            try {
                utc = api.getUTCTimestamp();
                url = koneksiDB.URLAPIBPJS() + "/SEP/FingerPrint/Peserta/" + noKartu.getText() + "/TglPelayanan/" + valid.setTglSmc(tglSEP);
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                entity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                metaData = root.path("metaData");
                if (metaData.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                    statusFP = response.path("kode").asText().equals("1") && response.path("status").asText().contains(valid.setTglSmc(tglSEP));
                    if (! statusFP) JOptionPane.showMessageDialog(rootPane, response.path("status").asText());
                } else {
                    JOptionPane.showMessageDialog(rootPane, response.path("status").asText());
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                if (e.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih data peserta!");
        }
    }

    public void tampilKunjunganPertama(String input) {
        try {
            url = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + input;
            utc = api.getUTCTimestamp();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
            metaData = root.path("metaData");
            System.out.println("URL : " + url);
            if (metaData.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                noRM.setText(query.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", response.path("peserta").path("noKartu").asText()));
                namaPasien.setText(response.path("peserta").path("nama").asText());
                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                noSKDP.setText("");
                noRujukan.setText(response.path("noKunjungan").asText());
                kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                kodePoliRS = query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodePoli.getText());
                cariJadwalPoliDokter();
                jenisPelayanan.setSelectedIndex(1);
                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                    case "1": kelas.setSelectedIndex(0); break;
                    case "2": kelas.setSelectedIndex(1); break;
                    case "3": kelas.setSelectedIndex(2); break;
                    default: kelas.setSelectedIndex(2); break;
                }
                tujuanKunjungan.setSelectedIndex(0);
                flagProsedur.setSelectedIndex(0);
                penunjang.setSelectedIndex(0);
                asesmenPelayanan.setSelectedIndex(0);
                labelTerapi.setVisible(false);
                labelPoliTerapi.setVisible(false);
                kodePoliTerapi.setText("");
                kodePoliTerapi.setVisible(false);
                namaPoliTerapi.setText("");
                namaPoliTerapi.setVisible(false);
                pilihPoliTerapi.setVisible(false);
                labelDokterTerapi.setVisible(false);
                kodeDokterTerapi.setText("");
                kodeDokterTerapi.setVisible(false);
                namaDokterTerapi.setText("");
                namaDokterTerapi.setVisible(false);
                pilihDokterTerapi.setVisible(false);
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                jk.setText(response.path("peserta").path("sex").asText());
                nik.setText(response.path("peserta").path("nik").asText());
                if (response.path("peserta").path("nik").asText().contains("null") || response.path("peserta").path("nik").asText().isBlank()) {
                    nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                }
                noKartu.setText(response.path("peserta").path("noKartu").asText());
                asalRujukan.setSelectedIndex(0);
                tglSEP.setDate(new Date());
                tglRujukan.setSelectedItem(response.path("tglKunjungan").asText());
                noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (response.path("peserta").path("mr").path("noTelepon").asText().contains("null") || response.path("peserta").path("mr").path("noTelepon").asText().isBlank()) {
                    noTelpon.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                }
                katarak.setSelectedIndex(0);
                lakaLantas.setSelectedIndex(0);
                prb = (response.path("peserta").path("informasi").path("prolanisPRB").asText().contains("null")
                    ? response.path("peserta").path("informasi").path("prolanisPRB").asText() : "");
            } else {
                System.out.println("Respon : " + metaData.path("code").asText() + " " + metaData.path("status").asText());
                System.out.println("Pesan pencarian rujukan FKTP : " + metaData.path("message").asText());
                try {
                    url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + input;
                    utc = api.getUTCTimestamp();
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    entity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                    metaData = root.path("metaData");
                    System.out.println("URL : " + url);
                    if (metaData.path("code").asText().equals("200")) {
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                        noRM.setText(query.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", response.path("peserta").path("noKartu").asText()));
                        namaPasien.setText(response.path("peserta").path("nama").asText());
                        tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                        statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                        noSKDP.setText("");
                        noRujukan.setText(response.path("noKunjungan").asText());
                        kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                        namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                        kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                        namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                        kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                        namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                        kodePoliRS = query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodePoli.getText());
                        cariJadwalPoliDokter();
                        jenisPelayanan.setSelectedIndex(1);
                        switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                            case "1": kelas.setSelectedIndex(0); break;
                            case "2": kelas.setSelectedIndex(1); break;
                            case "3": kelas.setSelectedIndex(2); break;
                            default: kelas.setSelectedIndex(2); break;
                        }
                        tujuanKunjungan.setSelectedIndex(0);
                        flagProsedur.setSelectedIndex(0);
                        penunjang.setSelectedIndex(0);
                        asesmenPelayanan.setSelectedIndex(0);
                        labelTerapi.setVisible(false);
                        labelPoliTerapi.setVisible(false);
                        kodePoliTerapi.setText("");
                        kodePoliTerapi.setVisible(false);
                        namaPoliTerapi.setText("");
                        namaPoliTerapi.setVisible(false);
                        pilihPoliTerapi.setVisible(false);
                        labelDokterTerapi.setVisible(false);
                        kodeDokterTerapi.setText("");
                        kodeDokterTerapi.setVisible(false);
                        namaDokterTerapi.setText("");
                        namaDokterTerapi.setVisible(false);
                        pilihDokterTerapi.setVisible(false);
                        jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                        jk.setText(response.path("peserta").path("sex").asText());
                        nik.setText(response.path("peserta").path("nik").asText());
                        if (response.path("peserta").path("nik").asText().contains("null") || response.path("peserta").path("nik").asText().isBlank()) {
                            nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                        }
                        noKartu.setText(response.path("peserta").path("noKartu").asText());
                        asalRujukan.setSelectedIndex(1);
                        tglSEP.setDate(new Date());
                        tglRujukan.setSelectedItem(response.path("tglKunjungan").asText());
                        noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                        if (response.path("peserta").path("mr").path("noTelepon").asText().contains("null") || response.path("peserta").path("mr").path("noTelepon").asText().isBlank()) {
                            noTelpon.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                        }
                        katarak.setSelectedIndex(0);
                        lakaLantas.setSelectedIndex(0);
                        prb = (response.path("peserta").path("informasi").path("prolanisPRB").asText().contains("null")
                            ? response.path("peserta").path("informasi").path("prolanisPRB").asText()
                            : "");
                    } else {
                        resetForm();
                        System.out.println("Respon : " + metaData.path("code").asText() + " " + metaData.path("status").asText());
                        System.out.println("Pesan pencarian rujukan FKTL : " + metaData.path("message").asText());
                        JOptionPane.showMessageDialog(rootPane, "Rujukan pasien tidak ditemukan...!!!");
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                    if (e.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                    }
                    resetForm();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
            resetForm();
        }
    }

    public void tampilKunjunganBedaPoli(String input) {
        try {
            url = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + input;
            utc = api.getUTCTimestamp();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
            metaData = root.path("metaData");
            System.out.println("URL : " + url);
            if (metaData.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                noRM.setText(query.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", response.path("peserta").path("noKartu").asText()));
                namaPasien.setText(response.path("peserta").path("nama").asText());
                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                noSKDP.setText("");
                noRujukan.setText(response.path("noKunjungan").asText());
                kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                kodePoliRS = query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodePoli.getText());
                cariJadwalPoliDokter();
                jenisPelayanan.setSelectedIndex(1);
                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                    case "1": kelas.setSelectedIndex(0); break;
                    case "2": kelas.setSelectedIndex(1); break;
                    case "3": kelas.setSelectedIndex(2); break;
                    default: kelas.setSelectedIndex(2); break;
                }
                tujuanKunjungan.setSelectedIndex(0);
                flagProsedur.setSelectedIndex(0);
                penunjang.setSelectedIndex(0);
                asesmenPelayanan.setSelectedIndex(0);
                labelTerapi.setVisible(true);
                labelPoliTerapi.setVisible(true);
                kodePoliTerapi.setText("");
                kodePoliTerapi.setVisible(true);
                namaPoliTerapi.setText("");
                namaPoliTerapi.setVisible(true);
                pilihPoliTerapi.setVisible(true);
                labelDokterTerapi.setVisible(true);
                kodeDokterTerapi.setText("");
                kodeDokterTerapi.setVisible(true);
                namaDokterTerapi.setText("");
                namaDokterTerapi.setVisible(true);
                pilihDokterTerapi.setVisible(true);
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                jk.setText(response.path("peserta").path("sex").asText());
                nik.setText(response.path("peserta").path("nik").asText());
                if (response.path("peserta").path("nik").asText().contains("null") || response.path("peserta").path("nik").asText().isBlank()) {
                    nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                }
                noKartu.setText(response.path("peserta").path("noKartu").asText());
                asalRujukan.setSelectedIndex(0);
                tglSEP.setDate(new Date());
                tglRujukan.setSelectedItem(response.path("tglKunjungan").asText());
                noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (response.path("peserta").path("mr").path("noTelepon").asText().contains("null") || response.path("peserta").path("mr").path("noTelepon").asText().isBlank()) {
                    noTelpon.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                }
                katarak.setSelectedIndex(0);
                lakaLantas.setSelectedIndex(0);
                prb = (response.path("peserta").path("informasi").path("prolanisPRB").asText().contains("null")
                    ? response.path("peserta").path("informasi").path("prolanisPRB").asText()
                    : "");
            } else {
                System.out.println("Respon : " + metaData.path("code").asText() + " " + metaData.path("status").asText());
                System.out.println("Pesan pencarian rujukan FKTP : " + metaData.path("message").asText());
                try {
                    url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + input;
                    utc = api.getUTCTimestamp();
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    entity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                    metaData = root.path("metaData");
                    System.out.println("URL : " + url);
                    if (metaData.path("code").asText().equals("200")) {
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                        noRM.setText(query.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", response.path("peserta").path("noKartu").asText()));
                        namaPasien.setText(response.path("peserta").path("nama").asText());
                        tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                        statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                        noSKDP.setText("");
                        noRujukan.setText(response.path("noKunjungan").asText());
                        kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                        namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                        kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                        namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                        kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                        namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                        kodePoliRS = query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodePoli.getText());
                        cariJadwalPoliDokter();
                        jenisPelayanan.setSelectedIndex(1);
                        switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                            case "1": kelas.setSelectedIndex(0); break;
                            case "2": kelas.setSelectedIndex(1); break;
                            case "3": kelas.setSelectedIndex(2); break;
                            default: kelas.setSelectedIndex(2); break;
                        }
                        tujuanKunjungan.setSelectedIndex(0);
                        flagProsedur.setSelectedIndex(0);
                        penunjang.setSelectedIndex(0);
                        asesmenPelayanan.setSelectedIndex(0);
                        labelTerapi.setVisible(true);
                        labelPoliTerapi.setVisible(true);
                        kodePoliTerapi.setText("");
                        kodePoliTerapi.setVisible(true);
                        namaPoliTerapi.setText("");
                        namaPoliTerapi.setVisible(true);
                        pilihPoliTerapi.setVisible(true);
                        labelDokterTerapi.setVisible(true);
                        kodeDokterTerapi.setText("");
                        kodeDokterTerapi.setVisible(true);
                        namaDokterTerapi.setText("");
                        namaDokterTerapi.setVisible(true);
                        pilihDokterTerapi.setVisible(true);
                        jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                        jk.setText(response.path("peserta").path("sex").asText());
                        nik.setText(response.path("peserta").path("nik").asText());
                        if (response.path("peserta").path("nik").asText().contains("null") || response.path("peserta").path("nik").asText().isBlank()) {
                            nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                        }
                        noKartu.setText(response.path("peserta").path("noKartu").asText());
                        asalRujukan.setSelectedIndex(1);
                        tglSEP.setDate(new Date());
                        tglRujukan.setSelectedItem(response.path("tglKunjungan").asText());
                        noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                        if (response.path("peserta").path("mr").path("noTelepon").asText().contains("null") || response.path("peserta").path("mr").path("noTelepon").asText().isBlank()) {
                            noTelpon.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                        }
                        katarak.setSelectedIndex(0);
                        lakaLantas.setSelectedIndex(0);
                        prb = (response.path("peserta").path("informasi").path("prolanisPRB").asText().contains("null")
                            ? response.path("peserta").path("informasi").path("prolanisPRB").asText()
                            : "");
                    } else {
                        resetForm();
                        System.out.println("Respon : " + metaData.path("code").asText() + " " + metaData.path("status").asText());
                        System.out.println("Pesan pencarian rujukan FKTL : " + metaData.path("message").asText());
                        JOptionPane.showMessageDialog(rootPane, "Rujukan pasien tidak ditemukan...!!!");
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                    if (e.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                    }
                    resetForm();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
            resetForm();
        }
    }
    
    public void tampilKontrol(String input) {
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select bridging_surat_kontrol_bpjs.*, bridging_sep.no_sep, bridging_sep.no_kartu, upper(bridging_sep.no_rujukan) as no_rujukan, " +
            "bridging_sep.klsrawat, left(bridging_sep.asal_rujukan, 1) as asal_rujukan, bridging_sep.jnspelayanan, bridging_sep.kdppkrujukan, " +
            "bridging_sep.nomr, bridging_sep.notelep, bridging_sep.nmppkrujukan, maping_dokter_dpjpvclaim.kd_dokter, maping_poli_bpjs.kd_poli_rs " +
            "from bridging_surat_kontrol_bpjs join bridging_sep on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep " +
            "left join maping_dokter_dpjpvclaim on bridging_surat_kontrol_bpjs.kd_dokter_bpjs = maping_dokter_dpjpvclaim.kd_dokter_bpjs " +
            "left join maping_poli_bpjs on bridging_surat_kontrol_bpjs.kd_poli_bpjs = maping_poli_bpjs.kd_poli_bpjs " +
            "where bridging_surat_kontrol_bpjs.no_surat = ?"
        )) {
            ps.setString(1, input);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (! rs.getString("tgl_rencana").equals(valid.setTglSmc(tglSEP))) updateSuratKontrol(rs.getString("no_surat"));
                    if (rs.getString("jnspelayanan").equals("1")) {
                        utc = api.getUTCTimestamp();
                        url = koneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rs.getString("no_kartu") + "/tglSEP/" + valid.setTglSmc(tglSEP);
                        try {
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            entity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                            metaData = root.path("metaData");
                            System.out.println("URL : " + url);
                            if (metaData.path("code").asText().equals("200")) {
                                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("peserta");
                                noRM.setText(rs.getString("bridging_sep.nomr"));
                                namaPasien.setText(response.path("nama").asText());
                                tglLahir.setText(response.path("tglLahir").asText());
                                noSKDP.setText(rs.getString("no_surat"));
                                noRujukan.setText(rs.getString("no_sep"));
                                kodePPK.setText(kodePPKPelayanan.getText());
                                namaPPK.setText(namaPPKPelayanan.getText());
                                kodeDiagnosa.setText("Z09.8");
                                namaDiagnosa.setText("Follow-up examination after other treatment for other conditions");
                                kodePoli.setText(rs.getString("kd_poli_bpjs"));
                                namaPoli.setText(rs.getString("nm_poli_bpjs"));
                                kodePoliRS = rs.getString("kd_poli_rs");
                                kodeDPJP.setText(rs.getString("kd_dokter_bpjs"));
                                namaDPJP.setText(rs.getString("nm_dokter_bpjs"));
                                kodeDokterRS = rs.getString("kd_dokter");
                                cariJadwalDokter();
                                jenisPelayanan.setSelectedIndex(1);
                                switch (rs.getString("klsrawat")) {
                                    case "1": kelas.setSelectedIndex(0); break;
                                    case "2": kelas.setSelectedIndex(1); break;
                                    case "3": kelas.setSelectedIndex(2); break;
                                    default: kelas.setSelectedIndex(2); break;
                                }
                                tujuanKunjungan.setSelectedIndex(0);
                                flagProsedur.setSelectedIndex(0);
                                penunjang.setSelectedIndex(0);
                                asesmenPelayanan.setSelectedIndex(0);
                                kodeDPJPLayanan.setText(rs.getString("kd_dokter_bpjs"));
                                namaDPJPLayanan.setText(rs.getString("nm_dokter_bpjs"));
                                labelTerapi.setVisible(false);
                                labelPoliTerapi.setVisible(false);
                                kodePoliTerapi.setText("");
                                kodePoliTerapi.setVisible(false);
                                namaPoliTerapi.setText("");
                                namaPoliTerapi.setVisible(false);
                                pilihPoliTerapi.setVisible(false);
                                labelDokterTerapi.setVisible(false);
                                kodeDokterTerapi.setText("");
                                kodeDokterTerapi.setVisible(false);
                                namaDokterTerapi.setText("");
                                namaDokterTerapi.setVisible(false);
                                pilihDokterTerapi.setVisible(false);
                                jenisPeserta.setText(response.path("jenisPeserta").path("keterangan").asText());
                                jk.setText(response.path("sex").asText());
                                nik.setText(response.path("nik").asText());
                                if (response.path("mr").path("nik").asText().contains("null") || response.path("mr").path("nik").asText().isBlank()) {
                                    nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                                }
                                noKartu.setText(response.path("noKartu").asText());
                                asalRujukan.setSelectedIndex(1);
                                tglSEP.setDate(new Date());
                                tglRujukan.setDate(new Date());
                                noTelpon.setText(response.path("mr").path("noTelepon").asText());
                                if (response.path("mr").path("noTelepon").asText().contains("null") || response.path("mr").path("noTelepon").asText().isBlank()) {
                                    noTelpon.setText(rs.getString("notelep"));
                                }
                                katarak.setSelectedIndex(0);
                                lakaLantas.setSelectedIndex(0);
                                prb = (response.path("informasi").path("prolanisPRB").asText().contains("null")
                                    ? response.path("informasi").path("prolanisPRB").asText()
                                    : "");
                            } else {
                                resetForm();
                                System.out.println("Respon : " + metaData.path("code").asText() + " " + metaData.path("status").asText());
                                System.out.println("Pesan pengecekan data pasien : " + metaData.path("message").asText());
                                JOptionPane.showMessageDialog(rootPane, "Surat kontrol pasca ranap pasien tidak ditemukan...!!!");
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                            if (e.toString().contains("UnknownHostException")) {
                                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!!!");
                            }
                        }
                    } else if (rs.getString("jnspelayanan").equals("2")) {
                        if (rs.getString("asal_rujukan").equals("1")) {
                            try {
                                url = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rs.getString("no_rujukan");
                                utc = api.getUTCTimestamp();
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                headers.add("X-Timestamp", utc);
                                headers.add("X-Signature", api.getHmac(utc));
                                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                entity = new HttpEntity(headers);
                                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                                metaData = root.path("metaData");
                                System.out.println("URL : " + url);
                                if (metaData.path("code").asText().equals("200")) {
                                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                    noRM.setText(query.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", response.path("peserta").path("noKartu").asText()));
                                    namaPasien.setText(response.path("peserta").path("nama").asText());
                                    tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                                    statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                                    noSKDP.setText(rs.getString("no_surat"));
                                    noRujukan.setText(response.path("noKunjungan").asText());
                                    kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                                    namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                                    kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                    namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                    kodePoli.setText(rs.getString("kd_poli_bpjs"));
                                    namaPoli.setText(rs.getString("nm_poli_bpjs"));
                                    kodePoliRS = rs.getString("kd_poli_rs");
                                    kodeDPJP.setText(rs.getString("kd_dokter_bpjs"));
                                    namaDPJP.setText(rs.getString("nm_dokter_bpjs"));
                                    kodeDokterRS = rs.getString("kd_dokter");
                                    cariJadwalDokter();
                                    jenisPelayanan.setSelectedIndex(1);
                                    switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                                        case "1": kelas.setSelectedIndex(0); break;
                                        case "2": kelas.setSelectedIndex(1); break;
                                        case "3": kelas.setSelectedIndex(2); break;
                                        default: kelas.setSelectedIndex(2); break;
                                    }
                                    tujuanKunjungan.setSelectedIndex(2);
                                    flagProsedur.setSelectedIndex(0);
                                    penunjang.setSelectedIndex(0);
                                    asesmenPelayanan.setSelectedIndex(5);
                                    kodeDPJPLayanan.setText(rs.getString("kd_dokter_bpjs"));
                                    namaDPJPLayanan.setText(rs.getString("nm_dokter_bpjs"));
                                    labelTerapi.setVisible(false);
                                    labelPoliTerapi.setVisible(false);
                                    kodePoliTerapi.setText("");
                                    kodePoliTerapi.setVisible(false);
                                    namaPoliTerapi.setText("");
                                    namaPoliTerapi.setVisible(false);
                                    pilihPoliTerapi.setVisible(false);
                                    labelDokterTerapi.setVisible(false);
                                    kodeDokterTerapi.setText("");
                                    kodeDokterTerapi.setVisible(false);
                                    namaDokterTerapi.setText("");
                                    namaDokterTerapi.setVisible(false);
                                    pilihDokterTerapi.setVisible(false);
                                    jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                                    jk.setText(response.path("peserta").path("sex").asText());
                                    nik.setText(response.path("peserta").path("nik").asText());
                                    if (response.path("peserta").path("nik").asText().contains("null") || response.path("peserta").path("nik").asText().isBlank()) {
                                        nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                                    }
                                    noKartu.setText(response.path("peserta").path("noKartu").asText());
                                    asalRujukan.setSelectedIndex(0);
                                    tglSEP.setDate(new Date());
                                    tglRujukan.setSelectedItem(response.path("tglKunjungan").asText());
                                    noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                                    if (response.path("peserta").path("noTelepon").asText().contains("null") || response.path("peserta").path("noTelepon").asText().isBlank()) {
                                        noTelpon.setText(rs.getString("notelep"));
                                    }
                                    katarak.setSelectedIndex(0);
                                    lakaLantas.setSelectedIndex(0);
                                    prb = (response.path("peserta").path("informasi").path("prolanisPRB").asText().contains("null")
                                        ? response.path("peserta").path("informasi").path("prolanisPRB").asText()
                                        : "");
                                } else {
                                    System.out.println("Respon : " + metaData.path("code").asText() + " " + metaData.path("status").asText());
                                    System.out.println("Pesan pencarian rujukan FKTP : " + metaData.path("message").asText());
                                    JOptionPane.showMessageDialog(rootPane, "Tidak dapat menemukan rujukan awal rencana kontrol pasien...!!!");
                                    resetForm();
                                }
                            } catch (Exception e) {
                                System.out.println("Notif : " + e);
                                if (e.toString().contains("UnknownHostException")) {
                                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                }
                            }
                        } else if (rs.getString("asal_rujukan").equals("2")) {
                            try {
                                url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rs.getString("no_rujukan");
                                utc = api.getUTCTimestamp();
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                headers.add("X-Timestamp", utc);
                                headers.add("X-Signature", api.getHmac(utc));
                                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                entity = new HttpEntity(headers);
                                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                                metaData = root.path("metaData");
                                System.out.println("URL : " + url);
                                if (metaData.path("code").asText().equals("200")) {
                                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                    noRM.setText(query.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", response.path("peserta").path("noKartu").asText()));
                                    namaPasien.setText(response.path("peserta").path("nama").asText());
                                    tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                                    statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                                    noSKDP.setText(rs.getString("no_surat"));
                                    noRujukan.setText(response.path("noKunjungan").asText());
                                    kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                                    namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                                    kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                    namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                    kodePoli.setText(rs.getString("kd_poli_bpjs"));
                                    namaPoli.setText(rs.getString("nm_poli_bpjs"));
                                    kodePoliRS = rs.getString("kd_poli_rs");
                                    kodeDPJP.setText(rs.getString("kd_dokter_bpjs"));
                                    namaDPJP.setText(rs.getString("nm_dokter_bpjs"));
                                    kodeDokterRS = rs.getString("kd_dokter");
                                    cariJadwalDokter();
                                    jenisPelayanan.setSelectedIndex(1);
                                    switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                                        case "1": kelas.setSelectedIndex(0); break;
                                        case "2": kelas.setSelectedIndex(1); break;
                                        case "3": kelas.setSelectedIndex(2); break;
                                        default: kelas.setSelectedIndex(2); break;
                                    }
                                    tujuanKunjungan.setSelectedIndex(0);
                                    flagProsedur.setSelectedIndex(0);
                                    penunjang.setSelectedIndex(0);
                                    asesmenPelayanan.setSelectedIndex(0);
                                    labelTerapi.setVisible(false);
                                    labelPoliTerapi.setVisible(false);
                                    kodePoliTerapi.setText("");
                                    kodePoliTerapi.setVisible(false);
                                    namaPoliTerapi.setText("");
                                    namaPoliTerapi.setVisible(false);
                                    pilihPoliTerapi.setVisible(false);
                                    labelDokterTerapi.setVisible(false);
                                    kodeDokterTerapi.setText("");
                                    kodeDokterTerapi.setVisible(false);
                                    namaDokterTerapi.setText("");
                                    namaDokterTerapi.setVisible(false);
                                    pilihDokterTerapi.setVisible(false);
                                    jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                                    jk.setText(response.path("peserta").path("sex").asText());
                                    nik.setText(response.path("peserta").path("nik").asText());
                                    if (response.path("peserta").path("nik").asText().contains("null") || response.path("peserta").path("nik").asText().isBlank()) {
                                        nik.setText(query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                                    }
                                    noKartu.setText(response.path("peserta").path("noKartu").asText());
                                    asalRujukan.setSelectedIndex(1);
                                    tglSEP.setDate(new Date());
                                    tglRujukan.setSelectedItem(response.path("tglKunjungan").asText());
                                    noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                                    if (response.path("peserta").path("noTelepon").asText().contains("null") || response.path("peserta").path("noTelepon").asText().isBlank()) {
                                        noTelpon.setText(rs.getString("notelep"));
                                    }
                                    katarak.setSelectedIndex(0);
                                    lakaLantas.setSelectedIndex(0);
                                    prb = (response.path("peserta").path("informasi").path("prolanisPRB").asText().contains("null")
                                        ? response.path("peserta").path("informasi").path("prolanisPRB").asText()
                                        : "");
                                } else {
                                    resetForm();
                                    System.out.println("Respon : " + metaData.path("code").asText() + " " + metaData.path("status").asText());
                                    System.out.println("Pesan pencarian rujukan FKTL : " + metaData.path("message").asText());
                                    JOptionPane.showMessageDialog(rootPane, "Tidak dapat menemukan rujukan awal rencana kontrol pasien...!!!");
                                }
                            } catch (Exception e) {
                                System.out.println("Notif : " + e);
                                if (e.toString().contains("UnknownHostException")) {
                                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Surat kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Surat kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                    }
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Surat kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(rootPane, "Surat kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
        }
    }

    public void simpanAntrianOnSite() {
        if (query.cariBooleanSmc("select * from referensi_mobilejkn_bpjs where no_rawat = ?", noRawat)) {
            System.out.println("Notif : pasien sudah menggunakan antrian online MobileJKN!");
            return;
        }
        if (kodePoliRS.isBlank() || kodeDokterRS.isBlank()) {
            System.out.println("Notif : Poli / Dokter belum dimapping!");
            return;
        }
        if (noRujukan.getText().isBlank() || noSKDP.getText().isBlank()) {
            System.out.println("Notif : No. rujukan / No. SKDP pasien tidak ada!");
            return;
        }
        if (tujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().isBlank()) {
            if (asalRujukan.getSelectedIndex() == 0) {
                jenisKunjungan = "1";
            } else {
                jenisKunjungan = "4";
            }
        } else if (tujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
            jenisKunjungan = "3";
        } else if (tujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().equals("4. Atas Instruksi RS")) {
            jenisKunjungan = "2";
        } else if (tujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().equals("1. Poli spesialis tidak tersedia pada hari sebelumnya")) {
            jenisKunjungan = "2";
        } else {
            if (tujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && asesmenPelayanan.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
                jenisKunjungan = "3";
            } else {
                jenisKunjungan = "2";
            }
        }
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        try (PreparedStatement ps = koneksi.prepareStatement("select jam_mulai, jam_selesai, kuota from jadwal where hari_kerja = ? and kd_poli = ? and kd_dokter = ?")) {
            cal.setTime(tglSEP.getDate());
            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case 1: hari = "AKHAD"; break;
                case 2: hari = "SENIN"; break;
                case 3: hari = "SELASA"; break;
                case 4: hari = "RABU"; break;
                case 5: hari = "KAMIS"; break;
                case 6: hari = "JUMAT"; break;
                case 7: hari = "SABTU"; break;
            }
            ps.setString(1, hari);
            ps.setString(2, kodePoliRS);
            ps.setString(3, kodeDokterRS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    jamMulai = rs.getString("jam_mulai");
                    jamSelesai = rs.getString("jam_selesai");
                    kuota = rs.getInt("kuota");
                    date = dtf.parse(query.cariIsiSmc("select date_add(concat(?, ' ', ?), interval ? minute)", valid.setTglSmc(tglSEP), jamMulai, String.valueOf(Integer.parseInt(noReg) * 10)));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        if (! noSKDP.getText().isBlank()) {
            try {
                utc = api.getUTCTimestamp();
                url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                headers.add("x-timestamp", utc);
                headers.add("x-signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                json = "{"
                    + "\"kodebooking\": \"" + noRawat + "\","
                    + "\"jenispasien\": \"JKN\","
                    + "\"nomorkartu\": \"" + noKartu.getText() + "\","
                    + "\"nik\": \"" + nik.getText() + "\","
                    + "\"nohp\": \"" + noTelpon.getText() + "\","
                    + "\"kodepoli\": \"" + kodePoli.getText() + "\","
                    + "\"namapoli\": \"" + namaPoli.getText() + "\","
                    + "\"pasienbaru\": 0,"
                    + "\"norm\": \"" + noRM.getText() + "\","
                    + "\"tanggalperiksa\": \"" + valid.setTglSmc(tglSEP) + "\","
                    + "\"kodedokter\": " + kodeDPJP.getText() + ","
                    + "\"namadokter\": \"" + namaDPJP.getText() + "\","
                    + "\"jampraktek\": \"" + jamMulai.substring(0, 5) + "-" + jamSelesai.substring(0, 5) + "\","
                    + "\"jeniskunjungan\": " + jenisKunjungan + ","
                    + "\"nomorreferensi\": \"" + noSKDP.getText() + "\","
                    + "\"nomorantrean\": \"" + noReg + "\","
                    + "\"angkaantrean\": " + Integer.parseInt(noReg) + ","
                    + "\"estimasidilayani\": " + date.getTime() + ","
                    + "\"sisakuotajkn\": " + (kuota - Integer.parseInt(noReg)) + ","
                    + "\"kuotajkn\": " + kuota + ","
                    + "\"sisakuotanonjkn\": " + (kuota - Integer.parseInt(noReg)) + ","
                    + "\"kuotanonjkn\": " + kuota + ","
                    + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi. Estimasi pelayanan 10 menit per pasien\""
                + "}";
                entity = new HttpEntity(json, headers);
                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                metaData = root.path("metadata");
                System.out.println("Respon WS Add Antrean : " + noSKDP.getText() + " " + metaData.path("code").asText() + " " + metaData.path("message").asText());
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
        if (! noRujukan.getText().isBlank()) {
            try {
                utc = api.getUTCTimestamp();
                url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                headers.add("x-timestamp", utc);
                headers.add("x-signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                json = "{"
                    + "\"kodebooking\": \"" + noRawat + "\","
                    + "\"jenispasien\": \"JKN\","
                    + "\"nomorkartu\": \"" + noKartu.getText() + "\","
                    + "\"nik\": \"" + nik.getText() + "\","
                    + "\"nohp\": \"" + noTelpon.getText() + "\","
                    + "\"kodepoli\": \"" + kodePoli.getText() + "\","
                    + "\"namapoli\": \"" + namaPoli.getText() + "\","
                    + "\"pasienbaru\": 0,"
                    + "\"norm\": \"" + noRM.getText() + "\","
                    + "\"tanggalperiksa\": \"" + valid.setTglSmc(tglSEP) + "\","
                    + "\"kodedokter\": " + kodeDPJP.getText() + ","
                    + "\"namadokter\": \"" + namaDPJP.getText() + "\","
                    + "\"jampraktek\": \"" + jamMulai.substring(0, 5) + "-" + jamSelesai.substring(0, 5) + "\","
                    + "\"jeniskunjungan\": " + jenisKunjungan + ","
                    + "\"nomorreferensi\": \"" + noRujukan.getText() + "\","
                    + "\"nomorantrean\": \"" + noReg + "\","
                    + "\"angkaantrean\": " + Integer.parseInt(noReg) + ","
                    + "\"estimasidilayani\": " + date.getTime() + ","
                    + "\"sisakuotajkn\": " + (kuota - Integer.parseInt(noReg)) + ","
                    + "\"kuotajkn\": " + kuota + ","
                    + "\"sisakuotanonjkn\": " + (kuota - Integer.parseInt(noReg)) + ","
                    + "\"kuotanonjkn\": " + kuota + ","
                    + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                + "}";
                entity = new HttpEntity(json, headers);
                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                metaData = root.path("metadata");
                System.out.println("Respon WS Add Antrean : " + noRujukan.getText() + " " + metaData.path("code").asText() + " " + metaData.path("message").asText());
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }

    private void resetForm() {
        noRM.setText("");
        namaPasien.setText("");
        tglLahir.setText("");
        statusPeserta.setText("");
        noSKDP.setText("");
        noRujukan.setText("");
        kodePPK.setText("");
        namaPPK.setText("");
        kodeDiagnosa.setText("");
        namaDiagnosa.setText("");
        labelPoli.setVisible(true);
        kodePoli.setText("");
        kodePoli.setVisible(true);
        namaPoli.setText("");
        namaPoli.setVisible(true);
        kodeDPJP.setText("");
        namaDPJP.setText("");
        kodePPKPelayanan.setText(query.cariIsiSmc("select kode_ppk from setting"));
        namaPPKPelayanan.setText(query.cariIsiSmc("select nama_instansi from setting"));
        jenisPelayanan.setSelectedIndex(1);
        kelas.setSelectedIndex(2);
        tujuanKunjungan.setSelectedIndex(0);
        flagProsedur.setEnabled(false);
        flagProsedur.setSelectedIndex(0);
        penunjang.setEnabled(false);
        penunjang.setSelectedIndex(0);
        asesmenPelayanan.setSelectedIndex(0);
        kodeDPJPLayanan.setText("");
        namaDPJPLayanan.setText("");
        labelTerapi.setVisible(true);
        labelPoliTerapi.setVisible(true);
        kodePoliTerapi.setText("");
        kodePoliTerapi.setVisible(true);
        namaPoliTerapi.setText("");
        namaPoliTerapi.setVisible(true);
        pilihPoliTerapi.setVisible(true);
        labelDokterTerapi.setVisible(true);
        kodeDokterTerapi.setText("");
        kodeDokterTerapi.setVisible(true);
        namaDokterTerapi.setText("");
        namaDokterTerapi.setVisible(true);
        pilihDokterTerapi.setVisible(true);
        jenisPeserta.setText("");
        jk.setText("");
        nik.setText("");
        noKartu.setText("");
        asalRujukan.setSelectedIndex(0);
        tglSEP.setDate(new Date());
        tglRujukan.setDate(new Date());
        noTelpon.setText("");
        katarak.setSelectedIndex(0);
        lakaLantas.setSelectedIndex(0);
        labelTglKLL.setVisible(true);
        tglKLL.setDate(new Date());
        tglKLL.setVisible(true);
        labelKeterangan.setVisible(true);
        keterangan.setVisible(true);
        keterangan.setText("");
        labelSuplesi.setVisible(true);
        suplesi.setVisible(true);
        suplesi.setSelectedIndex(0);
        labelNoSEPSuplesi.setVisible(true);
        noSEPSuplesi.setVisible(true);
        noSEPSuplesi.setText("");
        labelProvKLL.setVisible(true);
        kodeProvKLL.setVisible(true);
        kodeProvKLL.setText("");
        namaProvKLL.setVisible(true);
        namaProvKLL.setText("");
        labelKabKLL.setVisible(true);
        kodeKabKLL.setVisible(true);
        kodeKabKLL.setText("");
        namaKabKLL.setVisible(true);
        namaKabKLL.setText("");
        labelKecKLL.setVisible(true);
        kodeKecKLL.setVisible(true);
        kodeKecKLL.setText("");
        namaKecKLL.setVisible(true);
        namaKecKLL.setText("");
        labelCatatan.setLocation(620, 460);
        catatan.setText("Anjungan Pasien Mandiri " + namaPPKPelayanan.getText());
        catatan.setLocation(730, 460);
    }

    private void cekBiayaRegistrasi() {
        try (PreparedStatement ps = koneksi.prepareStatement("select registrasi, registrasilama from poliklinik where kd_poli = ?")) {
            ps.setString(1, kodePoliRS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    biaya = (statusRegistrasi.equalsIgnoreCase("lama")
                        ? rs.getString("registrasilama")
                        : rs.getString("registrasi")
                    );
                } else {
                    biaya = "0";
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private void bukaAplikasiFingerprint() {
        if (noKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "No. kartu peserta tidak ada..!!");
            return;
        }
        this.toFront();
        try {
            appFPAktif = false;
            User32 u32 = User32.INSTANCE;
            u32.EnumWindows((WinDef.HWND hwnd, Pointer pntr) -> {
                char[] windowText = new char[512];
                u32.GetWindowText(hwnd, windowText, 512);
                String wText = Native.toString(windowText);
                if (wText.isEmpty()) return true;
                if (wText.contains("Registrasi Sidik Jari")) {
                    DlgRegistrasiSEPPertama.this.appFPAktif = true;
                    u32.SetForegroundWindow(hwnd);
                }
                return true;
            }, Pointer.NULL);
            Robot r = new Robot();
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection ss;
            if (appFPAktif) {
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
                Runtime.getRuntime().exec(koneksiDB.URLAPLIKASIFINGERPRINTBPJS());
                Thread.sleep(2000);
                ss = new StringSelection(koneksiDB.USERFINGERPRINTBPJS());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                Thread.sleep(1000);
                ss = new StringSelection(koneksiDB.PASSFINGERPRINTBPJS());
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
    
    private void updateSuratKontrol(String noSuratKontrol) {
        try {
            utc = api.getUTCTimestamp();
            url = koneksiDB.URLAPIBPJS() + "/RencanaKontrol/Update";
            System.out.println("URL : " + url);
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            json = "{"
                + "\"request\": {"
                + "\"noSuratKontrol\":\"" + noSKDP.getText() + "\","
                + "\"noSEP\":\"" + noSEP + "\","
                + "\"kodeDokter\":\"" + kodeDPJP.getText() + "\","
                + "\"poliKontrol\":\"" + kodePoli.getText() + "\","
                + "\"tglRencanaKontrol\":\"" + valid.setTglSmc(tglSEP) + "\","
                + "\"user\":\"RM:" + noRM.getText() + "\""
                + "}"
                + "}";
            entity = new HttpEntity(json, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.PUT, entity, String.class).getBody());
            metaData = root.path("metaData");
            if (metaData.path("code").asText().equals("200")) {
                query.mengupdateSmc("bridging_surat_kontrol_bpjs",
                    "tgl_rencana = ?, kd_dokter_bpjs = ?, nm_dokter_bpjs = ?, kd_poli_bpjs = ?, nm_poli_bpjs = ?", "no_surat = ?",
                    valid.setTglSmc(tglSEP), kodeDPJP.getText(), namaDPJP.getText(), kodePoli.getText(), namaPoli.getText(), noSKDP.getText()
                );
            } else {
                System.out.println("Notif : " + metaData.path("code").asText() + " " + metaData.path("message").asText());
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!!!");
            }
        }
    }

    private boolean registerPasien() {
        int coba = 0, maxCoba = 5;
        autoNomorRegistrasi();
        boolean sukses = query.menyimpantfSmc("reg_periksa", null,
            noReg, noRawat, valid.setTglSmc(tglSEP), query.cariIsiSmc("select current_time()"), kodeDokterRS,
            noRM.getText(), kodePoliRS, namaPJ, alamat, hubunganPJ, biaya, "Belum", statusRegistrasi, "Ralan",
            query.cariIsiSmc("select kd_pj from password_asuransi"), umur, statusUmur, "Belum Bayar", statusPoli
        );
        while (coba < maxCoba && ! sukses) {
            autoNomorRegistrasi();
            sukses = query.menyimpantfSmc("reg_periksa", null,
                noReg, noRawat, valid.setTglSmc(tglSEP), query.cariIsiSmc("select current_time()"), kodeDokterRS,
                noRM.getText(), kodePoliRS, namaPJ, alamat, hubunganPJ, biaya, "Belum", statusRegistrasi, "Ralan",
                query.cariIsiSmc("select kd_pj from password_asuransi"), umur, statusUmur, "Belum Bayar", statusPoli
            );
            coba++;
        }
        return sukses;
    }
    
    private boolean simpanRujukan() {
        int coba = 0, maxCoba = 5;
        String noRujukMasuk = query.cariIsiSmc(
            "select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
            valid.setTglSmc(tglSEP), valid.setTglSmc(tglSEP)
        );
        boolean sukses = query.menyimpantfSmc("rujuk_masuk", null,
            noRawat, namaPPK.getText(), "-", noRujukan.getText(), "0",
            namaPPK.getText(), kodeDiagnosa.getText(), "-", "-", noRujukMasuk
        );
        while (coba < maxCoba && ! sukses) {
            noRujukMasuk = query.cariIsiSmc(
                "select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                valid.setTglSmc(tglSEP), valid.setTglSmc(tglSEP)
            );
            sukses = query.menyimpantfSmc("rujuk_masuk", null,
                noRawat, namaPPK.getText(), "-", noRujukan.getText(), "0",
                namaPPK.getText(), kodeDiagnosa.getText(), "-", "-", noRujukMasuk
            );
            coba++;
        }
        return sukses;
    }
    
    private void updateDataPasien() {
        query.mengupdateSmc("pasien",
            "no_tlp = ?, umur = concat(concat(concat(timestampdiff(year, tgl_lahir, curdate()), ' Th '), concat(timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12), ' Bl ')), concat(timestampdiff(day, date_add(date_add(tgl_lahir, interval timestampdiff(year, tgl_lahir, curdate()) year), interval timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12) month), curdate()), ' Hr'))",
            "no_rkm_medis = ?",
            noTelpon.getText(), noRM.getText()
        );
    }
    
    private void cariJadwalPoliDokter() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(tglSEP.getDate());
        try (PreparedStatement ps = koneksi.prepareStatement("select jadwal.*, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim join jadwal on maping_dokter_dpjpvclaim.kd_dokter = jadwal.kd_dokter where jadwal.hari_kerja = ? and jadwal.kd_poli = ?")) {
            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case 1: hari = "AKHAD"; break;
                case 2: hari = "SENIN"; break;
                case 3: hari = "SELASA"; break;
                case 4: hari = "RABU"; break;
                case 5: hari = "KAMIS"; break;
                case 6: hari = "JUMAT"; break;
                case 7: hari = "SABTU"; break;
            }
            ps.setString(1, hari);
            ps.setString(2, kodePoliRS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kodeDPJP.setText(rs.getString("kd_dokter_bpjs"));
                    namaDPJP.setText(rs.getString("nm_dokter_bpjs"));
                    kodeDokterRS = rs.getString("kd_dokter");
                    jamMulai = rs.getString("jam_mulai");
                    jamSelesai = rs.getString("jam_selesai");
                    kuota = rs.getInt("kuota");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private void cariJadwalDokter() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(tglSEP.getDate());
        try (PreparedStatement ps = koneksi.prepareStatement("select * from jadwal where hari_kerja = ? and kd_dokter = ? and kd_poli = ?")) {
            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case 1: hari = "AKHAD"; break;
                case 2: hari = "SENIN"; break;
                case 3: hari = "SELASA"; break;
                case 4: hari = "RABU"; break;
                case 5: hari = "KAMIS"; break;
                case 6: hari = "JUMAT"; break;
                case 7: hari = "SABTU"; break;
            }
            ps.setString(1, hari);
            ps.setString(2, kodeDokterRS);
            ps.setString(3, kodePoliRS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    jamMulai = rs.getString("jam_mulai");
                    jamSelesai = rs.getString("jam_selesai");
                    kuota = rs.getInt("kuota");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
}
