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

public class DlgRegistrasiSEPPertama extends javax.swing.JDialog {
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel query = new sekuel();
    private final validasi valid = new validasi();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ApiBPJS api = new ApiBPJS();
    private final SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss"),
                                   dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private BPJSCekReferensiDokterDPJP1 cariDokterBPJS = new BPJSCekReferensiDokterDPJP1(null, true);
    private BPJSCekReferensiPenyakit cariDiagnosaBPJS = new BPJSCekReferensiPenyakit(null, true);
    private DlgCariPoliBPJS cariPoliBPJS = new DlgCariPoliBPJS(null, true);
    private DlgCariPoli cariPoli = new DlgCariPoli(null, true);
    private DlgCariDokter2 cariDokter = new DlgCariDokter2(null, true);
    private BPJSCekRiwayatRujukanTerakhir cekRujukanPasien = new BPJSCekRiwayatRujukanTerakhir(null, true);
    private BPJSCekRiwayatPelayanan riwayatPelayananPasien = new BPJSCekRiwayatPelayanan(null, true);
    private String _nik = "",
                   _noTelp = "",
                   aksiFP = "",
                   noRawat = "",
                   noReg = "",
                   kodePoliRS = "",
                   kodeDokterRS = "",
                   namaPj = "",
                   alamatPj = "",
                   hubunganPj = "DIRI SENDIRI",
                   biayaReg = "",
                   statusPoli = "Lama",
                   umurDaftar = "0",
                   statusUmur = "Th",
                   statusDaftar = "Lama",
                   jenisKunjungan = "",
                   hari = "AKHAD",
                   jadwalMulai = "",
                   jadwalSelesai = "",
                   prb = "",
                   utc = "",
                   url = "",
                   json = "";
    private int kuota = 0, noRegInt = 0;

    private JsonNode root, metaData, response;
    private Calendar cal = Calendar.getInstance();
    private boolean statusFP = false, aplikasiAktif = false;
    private HttpHeaders headers;
    private HttpEntity entity;
    private JsonNode nameNode;
    private int day = cal.get(Calendar.DAY_OF_WEEK);

    public DlgRegistrasiSEPPertama(java.awt.Frame parent, boolean id) {
        super(parent, id);
        initComponents();

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
                    kodePoliTerapi.setText(cariPoli.getTable().getValueAt(cariPoli.getTable().getSelectedRow(), 0).toString());
                    namaPoliTerapi.setText(cariPoli.getTable().getValueAt(cariPoli.getTable().getSelectedRow(), 1).toString());
                    kodeDokterTerapi.requestFocus();

                }
            }
        });

        cariDokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cariDokter.getTable().getSelectedRow() != -1) {
                    kodeDokterTerapi.setText(cariDokter.getTable().getValueAt(cariDokter.getTable().getSelectedRow(), 0).toString());
                    namaDokterTerapi.setText(cariDokter.getTable().getValueAt(cariDokter.getTable().getSelectedRow(), 1).toString());
                    kodeDokterTerapi.requestFocus();

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
        
        kodePPKPelayanan.setText(query.cariIsi("select setting.kode_ppk from setting"));
        namaPPKPelayanan.setText(query.cariIsi("select setting.nama_instansi from setting"));
        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
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
        labelTglRujuk = new widget.Label();
        tglRujuk = new widget.Tanggal();
        labelNoSuratKontrol = new widget.Label();
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
        labelPasien = new widget.Label();
        tglLahir = new widget.TextBox();
        labelJK = new widget.Label();
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
        labelDokter = new widget.Label();
        kodeDokter = new widget.TextBox();
        namaDokter = new widget.TextBox();
        labelKeterangan = new widget.Label();
        keterangan = new widget.TextBox();
        labelSuplesi = new widget.Label();
        suplesi = new widget.ComboBox();
        noSEPSuplesi = new widget.TextBox();
        labelSEPSuplesi = new widget.Label();
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
        labelPoliTerapi = new widget.Label();
        asesmenPelayanan = new widget.ComboBox();
        labelTerapi = new widget.Label();
        kodeDokterLayanan = new widget.TextBox();
        namaDokterLayanan = new widget.TextBox();
        pilihDokterTujuan = new widget.Button();
        labelLakaLantas = new widget.Label();
        labelNoTelpon = new widget.Label();
        labelTglLahir = new widget.Label();
        labelNIK = new widget.Label();
        noSuratKontrol = new widget.TextBox();
        labelNoRujukan = new widget.Label();
        nik = new widget.TextBox();
        labelNoKartu = new widget.Label();
        pilihPoliTujuan = new widget.Button();
        pilihDiagnosaAwal = new widget.Button();
        pilihRujukan = new widget.Button();
        riwayatPelayananBPJS = new widget.Button();
        kodeDokterTerapi = new widget.TextBox();
        kodePoliTerapi = new widget.TextBox();
        namaPoliTerapi = new widget.TextBox();
        namaDokterTerapi = new widget.TextBox();
        pilihDokterTerapi = new widget.Button();
        pilihPoliTerapi = new widget.Button();
        labelDokterTerapi = new widget.Label();
        approvalFP = new widget.Button();
        pengajuanFP = new widget.Button();
        labelAsesmenPelayanan = new widget.Label();
        labelDokterLayanan = new widget.Label();
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
        labelTglSEP.setBounds(625, 130, 100, 30);

        tglSEP.setEditable(false);
        tglSEP.setForeground(new java.awt.Color(50, 70, 50));
        tglSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-04-2024" }));
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

        labelTglRujuk.setForeground(new java.awt.Color(0, 131, 62));
        labelTglRujuk.setText("Tgl. Rujuk :");
        labelTglRujuk.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelTglRujuk.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelTglRujuk);
        labelTglRujuk.setBounds(625, 160, 100, 30);

        tglRujuk.setEditable(false);
        tglRujuk.setForeground(new java.awt.Color(50, 70, 50));
        tglRujuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-04-2024" }));
        tglRujuk.setDisplayFormat("dd-MM-yyyy");
        tglRujuk.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        tglRujuk.setOpaque(false);
        tglRujuk.setPreferredSize(new java.awt.Dimension(95, 23));
        tglRujuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglRujukKeyPressed(evt);
            }
        });
        jPanel2.add(tglRujuk);
        tglRujuk.setBounds(730, 160, 170, 30);

        labelNoSuratKontrol.setForeground(new java.awt.Color(0, 131, 62));
        labelNoSuratKontrol.setText("No. SKDP / Surat Kontrol :");
        labelNoSuratKontrol.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelNoSuratKontrol.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelNoSuratKontrol);
        labelNoSuratKontrol.setBounds(75, 70, 150, 30);

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
        labelCatatan.setBounds(625, 460, 100, 30);

        catatan.setEditable(false);
        catatan.setBackground(new java.awt.Color(255, 255, 153));
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
        jenisPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jenisPelayananKeyPressed(evt);
            }
        });
        jPanel2.add(jenisPelayanan);
        jenisPelayanan.setBounds(230, 280, 110, 30);

        labelKelas.setForeground(new java.awt.Color(0, 131, 62));
        labelKelas.setText("Kelas :");
        labelKelas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelKelas);
        labelKelas.setBounds(355, 280, 50, 30);

        kelas.setForeground(new java.awt.Color(0, 131, 62));
        kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Kelas 1", "2. Kelas 2", "3. Kelas 3" }));
        kelas.setSelectedIndex(2);
        kelas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(kelas);
        kelas.setBounds(410, 280, 100, 30);

        lakaLantas.setForeground(new java.awt.Color(0, 131, 62));
        lakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        lakaLantas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        lakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        lakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lakaLantasItemStateChanged(evt);
            }
        });
        lakaLantas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lakaLantasKeyPressed(evt);
            }
        });
        jPanel2.add(lakaLantas);
        lakaLantas.setBounds(730, 250, 170, 30);

        labelPasien.setForeground(new java.awt.Color(0, 131, 62));
        labelPasien.setText("Data Pasien : ");
        labelPasien.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelPasien.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelPasien);
        labelPasien.setBounds(75, 10, 150, 30);

        tglLahir.setEditable(false);
        tglLahir.setBackground(new java.awt.Color(255, 255, 153));
        tglLahir.setHighlighter(null);
        jPanel2.add(tglLahir);
        tglLahir.setBounds(230, 40, 110, 30);

        labelJK.setForeground(new java.awt.Color(0, 131, 62));
        labelJK.setText("J. K. :");
        labelJK.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelJK);
        labelJK.setBounds(900, 10, 35, 30);

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
        labelJenisPeserta.setBounds(625, 10, 100, 30);

        jenisPeserta.setEditable(false);
        jenisPeserta.setBackground(new java.awt.Color(255, 255, 153));
        jenisPeserta.setHighlighter(null);
        jPanel2.add(jenisPeserta);
        jenisPeserta.setBounds(730, 10, 170, 30);

        labelStatusPeserta.setForeground(new java.awt.Color(0, 131, 62));
        labelStatusPeserta.setText("Status :");
        labelStatusPeserta.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelStatusPeserta.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelStatusPeserta);
        labelStatusPeserta.setBounds(370, 40, 50, 30);

        statusPeserta.setEditable(false);
        statusPeserta.setBackground(new java.awt.Color(255, 255, 153));
        statusPeserta.setHighlighter(null);
        jPanel2.add(statusPeserta);
        statusPeserta.setBounds(420, 40, 150, 30);

        labelAsalRujukan.setForeground(new java.awt.Color(0, 131, 62));
        labelAsalRujukan.setText("Asal Rujukan :");
        labelAsalRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelAsalRujukan);
        labelAsalRujukan.setBounds(625, 100, 100, 30);

        asalRujukan.setForeground(new java.awt.Color(0, 131, 62));
        asalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        asalRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(asalRujukan);
        asalRujukan.setBounds(730, 100, 170, 30);

        noTelpon.setHighlighter(null);
        noTelpon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noTelponKeyPressed(evt);
            }
        });
        jPanel2.add(noTelpon);
        noTelpon.setBounds(730, 190, 170, 30);

        katarak.setForeground(new java.awt.Color(0, 131, 62));
        katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        katarak.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        katarak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                katarakKeyPressed(evt);
            }
        });
        jPanel2.add(katarak);
        katarak.setBounds(730, 220, 170, 30);

        labelKatarak.setForeground(new java.awt.Color(0, 131, 62));
        labelKatarak.setText("Katarak :");
        labelKatarak.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelKatarak);
        labelKatarak.setBounds(625, 220, 100, 30);

        labelTglKLL.setForeground(new java.awt.Color(0, 131, 62));
        labelTglKLL.setText("Tgl. KLL :");
        labelTglKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelTglKLL.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelTglKLL);
        labelTglKLL.setBounds(625, 280, 100, 30);

        tglKLL.setEditable(false);
        tglKLL.setForeground(new java.awt.Color(50, 70, 50));
        tglKLL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-04-2024" }));
        tglKLL.setDisplayFormat("dd-MM-yyyy");
        tglKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        tglKLL.setOpaque(false);
        tglKLL.setPreferredSize(new java.awt.Dimension(64, 25));
        tglKLL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglKLLKeyPressed(evt);
            }
        });
        jPanel2.add(tglKLL);
        tglKLL.setBounds(730, 280, 170, 30);

        labelDokter.setForeground(new java.awt.Color(0, 131, 62));
        labelDokter.setText("Dokter DPJP :");
        labelDokter.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelDokter.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelDokter);
        labelDokter.setBounds(75, 220, 150, 30);

        kodeDokter.setEditable(false);
        kodeDokter.setBackground(new java.awt.Color(255, 255, 153));
        kodeDokter.setHighlighter(null);
        jPanel2.add(kodeDokter);
        kodeDokter.setBounds(230, 220, 80, 30);

        namaDokter.setEditable(false);
        namaDokter.setBackground(new java.awt.Color(255, 255, 153));
        namaDokter.setHighlighter(null);
        jPanel2.add(namaDokter);
        namaDokter.setBounds(310, 220, 260, 30);

        labelKeterangan.setForeground(new java.awt.Color(0, 131, 62));
        labelKeterangan.setText("Keterangan :");
        labelKeterangan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelKeterangan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelKeterangan);
        labelKeterangan.setBounds(625, 310, 100, 30);

        keterangan.setEditable(false);
        keterangan.setBackground(new java.awt.Color(255, 255, 255));
        keterangan.setHighlighter(null);
        keterangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keteranganKeyPressed(evt);
            }
        });
        jPanel2.add(keterangan);
        keterangan.setBounds(730, 310, 300, 30);

        labelSuplesi.setForeground(new java.awt.Color(0, 131, 62));
        labelSuplesi.setText("Suplesi :");
        labelSuplesi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelSuplesi);
        labelSuplesi.setBounds(625, 340, 100, 30);

        suplesi.setForeground(new java.awt.Color(0, 131, 62));
        suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        suplesi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        suplesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                suplesiKeyPressed(evt);
            }
        });
        jPanel2.add(suplesi);
        suplesi.setBounds(730, 340, 90, 30);

        noSEPSuplesi.setHighlighter(null);
        noSEPSuplesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noSEPSuplesiKeyPressed(evt);
            }
        });
        jPanel2.add(noSEPSuplesi);
        noSEPSuplesi.setBounds(860, 340, 170, 30);

        labelSEPSuplesi.setForeground(new java.awt.Color(0, 131, 62));
        labelSEPSuplesi.setText("SEP :");
        labelSEPSuplesi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelSEPSuplesi.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelSEPSuplesi);
        labelSEPSuplesi.setBounds(820, 340, 35, 30);

        labelProvKLL.setForeground(new java.awt.Color(0, 131, 62));
        labelProvKLL.setText("Propinsi KLL :");
        labelProvKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelProvKLL);
        labelProvKLL.setBounds(625, 370, 100, 30);

        kodeProvKLL.setEditable(false);
        kodeProvKLL.setBackground(new java.awt.Color(255, 255, 153));
        kodeProvKLL.setHighlighter(null);
        jPanel2.add(kodeProvKLL);
        kodeProvKLL.setBounds(730, 370, 80, 30);

        namaProvKLL.setEditable(false);
        namaProvKLL.setBackground(new java.awt.Color(255, 255, 153));
        namaProvKLL.setHighlighter(null);
        jPanel2.add(namaProvKLL);
        namaProvKLL.setBounds(810, 370, 220, 30);

        labelKabKLL.setForeground(new java.awt.Color(0, 131, 62));
        labelKabKLL.setText("Kabupaten KLL :");
        labelKabKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelKabKLL);
        labelKabKLL.setBounds(625, 400, 100, 30);

        kodeKabKLL.setEditable(false);
        kodeKabKLL.setBackground(new java.awt.Color(255, 255, 153));
        kodeKabKLL.setHighlighter(null);
        jPanel2.add(kodeKabKLL);
        kodeKabKLL.setBounds(730, 400, 80, 30);

        namaKabKLL.setEditable(false);
        namaKabKLL.setBackground(new java.awt.Color(255, 255, 153));
        namaKabKLL.setHighlighter(null);
        jPanel2.add(namaKabKLL);
        namaKabKLL.setBounds(810, 400, 220, 30);

        labelKecKLL.setForeground(new java.awt.Color(0, 131, 62));
        labelKecKLL.setText("Kecamatan KLL :");
        labelKecKLL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelKecKLL);
        labelKecKLL.setBounds(625, 430, 100, 30);

        kodeKecKLL.setEditable(false);
        kodeKecKLL.setBackground(new java.awt.Color(255, 255, 153));
        kodeKecKLL.setHighlighter(null);
        jPanel2.add(kodeKecKLL);
        kodeKecKLL.setBounds(730, 430, 80, 30);

        namaKecKLL.setEditable(false);
        namaKecKLL.setBackground(new java.awt.Color(255, 255, 153));
        namaKecKLL.setHighlighter(null);
        jPanel2.add(namaKecKLL);
        namaKecKLL.setBounds(810, 430, 220, 30);

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
        tujuanKunjungan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tujuanKunjunganKeyPressed(evt);
            }
        });
        jPanel2.add(tujuanKunjungan);
        tujuanKunjungan.setBounds(230, 310, 340, 30);

        flagProsedur.setForeground(new java.awt.Color(0, 131, 62));
        flagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        flagProsedur.setEnabled(false);
        flagProsedur.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        flagProsedur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                flagProsedurKeyPressed(evt);
            }
        });
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
        penunjang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                penunjangKeyPressed(evt);
            }
        });
        jPanel2.add(penunjang);
        penunjang.setBounds(230, 370, 340, 30);

        labelPoliTerapi.setForeground(new java.awt.Color(0, 131, 62));
        labelPoliTerapi.setText("Poli Terapi :");
        labelPoliTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelPoliTerapi.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelPoliTerapi);
        labelPoliTerapi.setBounds(75, 490, 150, 30);

        asesmenPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        asesmenPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        asesmenPelayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        asesmenPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                asesmenPelayananKeyPressed(evt);
            }
        });
        jPanel2.add(asesmenPelayanan);
        asesmenPelayanan.setBounds(230, 400, 340, 30);

        labelTerapi.setForeground(new java.awt.Color(0, 131, 62));
        labelTerapi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTerapi.setText("Terapi / Rehabilitasi Medik");
        labelTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelTerapi.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelTerapi);
        labelTerapi.setBounds(230, 470, 340, 20);

        kodeDokterLayanan.setEditable(false);
        kodeDokterLayanan.setBackground(new java.awt.Color(255, 255, 153));
        kodeDokterLayanan.setHighlighter(null);
        jPanel2.add(kodeDokterLayanan);
        kodeDokterLayanan.setBounds(230, 430, 80, 30);

        namaDokterLayanan.setEditable(false);
        namaDokterLayanan.setBackground(new java.awt.Color(255, 255, 153));
        namaDokterLayanan.setHighlighter(null);
        jPanel2.add(namaDokterLayanan);
        namaDokterLayanan.setBounds(310, 430, 260, 30);

        pilihDokterTujuan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        pilihDokterTujuan.setMnemonic('X');
        pilihDokterTujuan.setToolTipText("Alt+X");
        pilihDokterTujuan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pilihDokterTujuan.setGlassColor(new java.awt.Color(238, 238, 255));
        pilihDokterTujuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihDokterTujuanActionPerformed(evt);
            }
        });
        pilihDokterTujuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pilihDokterTujuanKeyPressed(evt);
            }
        });
        jPanel2.add(pilihDokterTujuan);
        pilihDokterTujuan.setBounds(570, 220, 40, 30);

        labelLakaLantas.setForeground(new java.awt.Color(0, 131, 62));
        labelLakaLantas.setText("Laka Lantas :");
        labelLakaLantas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelLakaLantas);
        labelLakaLantas.setBounds(625, 250, 100, 30);

        labelNoTelpon.setForeground(new java.awt.Color(0, 131, 62));
        labelNoTelpon.setText("No. Telpon :");
        labelNoTelpon.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelNoTelpon.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelNoTelpon);
        labelNoTelpon.setBounds(625, 190, 100, 30);

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
        labelNIK.setBounds(625, 40, 100, 30);

        noSuratKontrol.setEditable(false);
        noSuratKontrol.setBackground(new java.awt.Color(255, 255, 153));
        noSuratKontrol.setHighlighter(null);
        jPanel2.add(noSuratKontrol);
        noSuratKontrol.setBounds(230, 70, 340, 30);

        labelNoRujukan.setForeground(new java.awt.Color(0, 131, 62));
        labelNoRujukan.setText("No. Rujukan :");
        labelNoRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelNoRujukan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelNoRujukan);
        labelNoRujukan.setBounds(75, 100, 150, 30);

        nik.setEditable(false);
        nik.setBackground(new java.awt.Color(255, 255, 153));
        nik.setHighlighter(null);
        jPanel2.add(nik);
        nik.setBounds(730, 40, 300, 30);

        labelNoKartu.setForeground(new java.awt.Color(0, 131, 62));
        labelNoKartu.setText("No. Kartu :");
        labelNoKartu.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelNoKartu);
        labelNoKartu.setBounds(625, 70, 100, 30);

        pilihPoliTujuan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        pilihPoliTujuan.setMnemonic('X');
        pilihPoliTujuan.setToolTipText("Alt+X");
        pilihPoliTujuan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pilihPoliTujuan.setGlassColor(new java.awt.Color(238, 238, 255));
        pilihPoliTujuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihPoliTujuanActionPerformed(evt);
            }
        });
        pilihPoliTujuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pilihPoliTujuanKeyPressed(evt);
            }
        });
        jPanel2.add(pilihPoliTujuan);
        pilihPoliTujuan.setBounds(570, 190, 40, 30);

        pilihDiagnosaAwal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        pilihDiagnosaAwal.setMnemonic('X');
        pilihDiagnosaAwal.setToolTipText("Alt+X");
        pilihDiagnosaAwal.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        pilihDiagnosaAwal.setGlassColor(new java.awt.Color(238, 238, 255));
        pilihDiagnosaAwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihDiagnosaAwalActionPerformed(evt);
            }
        });
        pilihDiagnosaAwal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pilihDiagnosaAwalKeyPressed(evt);
            }
        });
        jPanel2.add(pilihDiagnosaAwal);
        pilihDiagnosaAwal.setBounds(570, 160, 40, 30);

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

        riwayatPelayananBPJS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        riwayatPelayananBPJS.setMnemonic('X');
        riwayatPelayananBPJS.setText("Riwayat Layanan BPJS");
        riwayatPelayananBPJS.setToolTipText("Alt+X");
        riwayatPelayananBPJS.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        riwayatPelayananBPJS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        riwayatPelayananBPJS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                riwayatPelayananBPJSActionPerformed(evt);
            }
        });
        riwayatPelayananBPJS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                riwayatPelayananBPJSKeyPressed(evt);
            }
        });
        jPanel2.add(riwayatPelayananBPJS);
        riwayatPelayananBPJS.setBounds(1040, 150, 220, 30);

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
        pilihDokterTerapi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pilihDokterTerapiKeyPressed(evt);
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
        pilihPoliTerapi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pilihPoliTerapiKeyPressed(evt);
            }
        });
        jPanel2.add(pilihPoliTerapi);
        pilihPoliTerapi.setBounds(570, 490, 40, 30);

        labelDokterTerapi.setForeground(new java.awt.Color(0, 131, 62));
        labelDokterTerapi.setText("DPJP Terapi :");
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
        approvalFP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                approvalFPKeyPressed(evt);
            }
        });
        jPanel2.add(approvalFP);
        approvalFP.setBounds(1040, 260, 190, 50);

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
        pengajuanFP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pengajuanFPKeyPressed(evt);
            }
        });
        jPanel2.add(pengajuanFP);
        pengajuanFP.setBounds(1040, 200, 190, 50);

        labelAsesmenPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        labelAsesmenPelayanan.setText("Asesmen Pelayanan :");
        labelAsesmenPelayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelAsesmenPelayanan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelAsesmenPelayanan);
        labelAsesmenPelayanan.setBounds(75, 400, 150, 30);

        labelDokterLayanan.setForeground(new java.awt.Color(0, 131, 62));
        labelDokterLayanan.setText("DPJP Layanan :");
        labelDokterLayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        labelDokterLayanan.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(labelDokterLayanan);
        labelDokterLayanan.setBounds(75, 430, 150, 30);

        labelJumlahBarcode.setForeground(new java.awt.Color(0, 131, 62));
        labelJumlahBarcode.setText("Jumlah Barcode :");
        labelJumlahBarcode.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(labelJumlahBarcode);
        labelJumlahBarcode.setBounds(1040, 70, 100, 30);

        jumlahBarcode.setHighlighter(null);
        jPanel2.add(jumlahBarcode);
        jumlahBarcode.setBounds(1145, 70, 60, 30);

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
        fingerprint.setText("FINGERPRINT BPJS");
        fingerprint.setToolTipText("Alt+K");
        fingerprint.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        fingerprint.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
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
        
        cekStatusFingerprint();
        
        if (! validasiInput()) {
            resetInput();
            this.setCursor(Cursor.getDefaultCursor());
            return;
        }
        /*
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
        } else if (! statusFP && query.cariIntegerSmc("select timestampdiff(year, ?, CURRENT_DATE())", tglLahir.getText()) >= 17 && jenisPelayanan.getSelectedIndex() != 0 && !kodePoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Pasien belum melakukan Fingerprint");
            bukaAplikasiFingerprint();
        } else {
            if (!kodePoliTerapi.getText().equals("")) {
                kodepolireg = kodePoliTerapi.getText();
            } else {
                kodepolireg = query.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText());
            }

            if (!kodeDokterTerapi.getText().equals("")) {
                kodedokterreg = kodeDokterTerapi.getText();
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
        }
        */
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_simpanActionPerformed

    private void pilihDokterTujuanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pilihDokterTujuanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pilihDokterTujuanKeyPressed

    private void pilihDokterTujuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihDokterTujuanActionPerformed
        cariDokterBPJS.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        cariDokterBPJS.setLocationRelativeTo(jPanel1);
        cariDokterBPJS.carinamadokter(kodePoli.getText(), namaPoli.getText());
        cariDokterBPJS.setVisible(true);
    }//GEN-LAST:event_pilihDokterTujuanActionPerformed

    private void asesmenPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_asesmenPelayananKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_asesmenPelayananKeyPressed

    private void penunjangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_penunjangKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_penunjangKeyPressed

    private void flagProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_flagProsedurKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_flagProsedurKeyPressed

    private void tujuanKunjunganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tujuanKunjunganKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tujuanKunjunganKeyPressed

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

    private void noSEPSuplesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noSEPSuplesiKeyPressed

    }//GEN-LAST:event_noSEPSuplesiKeyPressed

    private void suplesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_suplesiKeyPressed
        valid.pindah(evt, keterangan, noSEPSuplesi);
    }//GEN-LAST:event_suplesiKeyPressed

    private void keteranganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keteranganKeyPressed
        valid.pindah(evt, tglKLL, suplesi);
    }//GEN-LAST:event_keteranganKeyPressed

    private void tglKLLKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglKLLKeyPressed
        valid.pindah(evt, lakaLantas, keterangan);
    }//GEN-LAST:event_tglKLLKeyPressed

    private void katarakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_katarakKeyPressed
        valid.pindah(evt, catatan, noTelpon);
    }//GEN-LAST:event_katarakKeyPressed

    private void noTelponKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noTelponKeyPressed
        valid.pindah(evt, katarak, lakaLantas);
    }//GEN-LAST:event_noTelponKeyPressed

    private void lakaLantasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lakaLantasKeyPressed
        valid.pindah(evt, noTelpon, tglKLL);
    }//GEN-LAST:event_lakaLantasKeyPressed

    private void lakaLantasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lakaLantasItemStateChanged
        if (lakaLantas.getSelectedIndex() == 0) {
            labelTglKLL.setVisible(false);
            tglKLL.setVisible(false);
            labelKeterangan.setVisible(false);
            keterangan.setText("");
            keterangan.setVisible(false);
            labelSuplesi.setVisible(false);
            suplesi.setSelectedIndex(0);
            suplesi.setVisible(false);
            labelSEPSuplesi.setVisible(false);
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
            labelCatatan.setLocation(625, 280);
            catatan.setLocation(730, 280);
        } else {
            labelTglKLL.setVisible(true);
            tglKLL.setVisible(true);
            labelKeterangan.setVisible(true);
            keterangan.setVisible(true);
            labelSuplesi.setVisible(true);
            suplesi.setSelectedIndex(0);
            suplesi.setVisible(true);
            labelSEPSuplesi.setVisible(true);
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
            labelCatatan.setLocation(625, 460);
            catatan.setLocation(730, 460);
        }
    }//GEN-LAST:event_lakaLantasItemStateChanged

    private void jenisPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jenisPelayananKeyPressed

    }//GEN-LAST:event_jenisPelayananKeyPressed

    private void jenisPelayananItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jenisPelayananItemStateChanged
        if (jenisPelayanan.getSelectedIndex() == 0) {
            kodePoli.setText("");
            namaPoli.setText("");
            labelPoli.setVisible(false);
            kodePoli.setVisible(false);
            namaPoli.setVisible(false);
            kodeDokterLayanan.setText("");
            namaDokterLayanan.setText("");
            pilihDokterTujuan.setEnabled(false);
        } else if (jenisPelayanan.getSelectedIndex() == 1) {
            labelPoli.setVisible(true);
            kodePoli.setVisible(true);
            namaPoli.setVisible(true);
            pilihDokterTujuan.setEnabled(true);
        }
    }//GEN-LAST:event_jenisPelayananItemStateChanged

    private void tglRujukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglRujukKeyPressed
        valid.pindah(evt, noRujukan, tglSEP);
    }//GEN-LAST:event_tglRujukKeyPressed

    private void tglSEPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglSEPKeyPressed
        valid.pindah(evt, tglRujuk, asalRujukan);
    }//GEN-LAST:event_tglSEPKeyPressed

    private void fingerprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fingerprintActionPerformed
        bukaAplikasiFingerprint();
    }//GEN-LAST:event_fingerprintActionPerformed

    private void fingerprintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fingerprintKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fingerprintKeyPressed

    private void pilihPoliTujuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihPoliTujuanActionPerformed
        cariPoliBPJS.setSize(jPanel1.getWidth() - 100, jPanel1.getHeight() - 100);
        cariPoliBPJS.tampil();
        cariPoliBPJS.setLocationRelativeTo(jPanel1);
        cariPoliBPJS.setVisible(true);
    }//GEN-LAST:event_pilihPoliTujuanActionPerformed

    private void pilihPoliTujuanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pilihPoliTujuanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pilihPoliTujuanKeyPressed

    private void pilihDiagnosaAwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihDiagnosaAwalActionPerformed
        cariDiagnosaBPJS.setSize(jPanel1.getWidth() - 100, jPanel1.getHeight() - 100);
        cariDiagnosaBPJS.setLocationRelativeTo(jPanel1);
        cariDiagnosaBPJS.setVisible(true);
    }//GEN-LAST:event_pilihDiagnosaAwalActionPerformed

    private void pilihDiagnosaAwalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pilihDiagnosaAwalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pilihDiagnosaAwalKeyPressed

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

    private void riwayatPelayananBPJSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_riwayatPelayananBPJSActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        riwayatPelayananPasien.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
        riwayatPelayananPasien.setLocationRelativeTo(jPanel1);
        riwayatPelayananPasien.setKartu(noKartu.getText());
        riwayatPelayananPasien.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_riwayatPelayananBPJSActionPerformed

    private void riwayatPelayananBPJSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_riwayatPelayananBPJSKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_riwayatPelayananBPJSKeyPressed

    private void pilihDokterTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihDokterTerapiActionPerformed
        cariDokter.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        cariDokter.tampilDokterTerapi(kodeDokterLayanan.getText());
        cariDokter.setLocationRelativeTo(jPanel1);
        cariDokter.setVisible(true);
    }//GEN-LAST:event_pilihDokterTerapiActionPerformed

    private void pilihDokterTerapiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pilihDokterTerapiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pilihDokterTerapiKeyPressed

    private void pilihPoliTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihPoliTerapiActionPerformed
        cariPoli.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        cariPoli.tampilPoliMapping(kodePoli.getText());
        cariPoli.setLocationRelativeTo(jPanel1);
        cariPoli.setVisible(true);
    }//GEN-LAST:event_pilihPoliTerapiActionPerformed

    private void pilihPoliTerapiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pilihPoliTerapiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pilihPoliTerapiKeyPressed

    private void approvalFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approvalFPActionPerformed
        pwUserId.setText("");
        pwPass.setText("");
        WindowAksi.setSize(400, 300);
        WindowAksi.setLocationRelativeTo(null);
        WindowAksi.setVisible(true);
        if (!noKartu.getText().equals("")) {
            aksiFP = "Approval";
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_approvalFPActionPerformed

    private void approvalFPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_approvalFPKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnDiagnosaAwal3ActionPerformed(null);
        }
    }//GEN-LAST:event_approvalFPKeyPressed

    private void pengajuanFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pengajuanFPActionPerformed
        pwUserId.setText("");
        pwPass.setText("");
        WindowAksi.setSize(400, 300);
        WindowAksi.setLocationRelativeTo(null);
        WindowAksi.setVisible(true);
        if (!noKartu.getText().equals("")) {
            aksiFP = "Pengajuan";
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_pengajuanFPActionPerformed

    private void pengajuanFPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pengajuanFPKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnDiagnosaAwal4ActionPerformed(null);
        }
    }//GEN-LAST:event_pengajuanFPKeyPressed

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
                        if (aksiFP.equals("Pengajuan")) {
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                utc = String.valueOf(api.GetUTCdatetimeAsString());
                                headers.add("X-Timestamp", utc);
                                headers.add("X-Signature", api.getHmac());
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
                                entity = new HttpEntity(requestJson, headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, entity, String.class).getBody());
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
                        } else if (aksiFP.equals("Approval")) {
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                utc = String.valueOf(api.GetUTCdatetimeAsString());
                                headers.add("X-Timestamp", utc);
                                headers.add("X-Signature", api.getHmac());
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
                                entity = new HttpEntity(requestJson, headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, entity, String.class).getBody());
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
    private widget.TextBox kodeDiagnosa;
    private widget.TextBox kodeDokter;
    private widget.TextBox kodeDokterLayanan;
    private widget.TextBox kodeDokterTerapi;
    private widget.TextBox kodeKabKLL;
    private widget.TextBox kodeKecKLL;
    private widget.TextBox kodePPK;
    private widget.TextBox kodePPKPelayanan;
    private widget.TextBox kodePoli;
    private widget.TextBox kodePoliTerapi;
    private widget.TextBox kodeProvKLL;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label labelAsalRujukan;
    private widget.Label labelAsesmenPelayanan;
    private widget.Label labelCatatan;
    private widget.Label labelDiagnosa;
    private widget.Label labelDokter;
    private widget.Label labelDokterLayanan;
    private widget.Label labelDokterTerapi;
    private widget.Label labelFlagProsedur;
    private widget.Label labelJK;
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
    private widget.Label labelNoRujukan;
    private widget.Label labelNoSuratKontrol;
    private widget.Label labelNoTelpon;
    private widget.Label labelPPK;
    private widget.Label labelPPKPelayanan;
    private widget.Label labelPasien;
    private widget.Label labelPenunjang;
    private widget.Label labelPoli;
    private widget.Label labelPoliTerapi;
    private widget.Label labelProvKLL;
    private widget.Label labelSEPSuplesi;
    private widget.Label labelStatusPeserta;
    private widget.Label labelSuplesi;
    private widget.Label labelTerapi;
    private widget.Label labelTglKLL;
    private widget.Label labelTglLahir;
    private widget.Label labelTglRujuk;
    private widget.Label labelTglSEP;
    private widget.Label labelTujuanKunjungan;
    private widget.ComboBox lakaLantas;
    private widget.TextBox namaDiagnosa;
    private widget.TextBox namaDokter;
    private widget.TextBox namaDokterLayanan;
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
    private widget.TextBox noSuratKontrol;
    private widget.TextBox noTelpon;
    private widget.Button pengajuanFP;
    private widget.ComboBox penunjang;
    private widget.Button pilihDiagnosaAwal;
    private widget.Button pilihDokterTerapi;
    private widget.Button pilihDokterTujuan;
    private widget.Button pilihPoliTerapi;
    private widget.Button pilihPoliTujuan;
    private widget.Button pilihRujukan;
    private widget.PasswordBox pwPass;
    private widget.PasswordBox pwUserId;
    private widget.Button riwayatPelayananBPJS;
    private component.Button simpan;
    private widget.TextBox statusPeserta;
    private widget.ComboBox suplesi;
    private widget.Tanggal tglKLL;
    private widget.TextBox tglLahir;
    private widget.Tanggal tglRujuk;
    private widget.Tanggal tglSEP;
    private widget.ComboBox tujuanKunjungan;
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
            headers.add("X-Signature", api.getHmac());
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
                        + "\"noTelp\": \"" + noTelpon.getText() + "\","
                        + "\"user\":\"" + noKartu.getText() + "\""
                    + "}"
                + "}"
            + "}";
            
            entity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, entity, String.class).getBody());
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
                    noTelpon.getText(),
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

    public void tampilKunjunganPertama(String noRujukan) {
        try {
            URL = URLAPIBPJS + "/Rujukan/Peserta/" + noRujukan;
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac());
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, entity, String.class).getBody());
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
                noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (noTelpon.getText().equals("null") || noTelpon.getText().isBlank()) {
                    noTelpon.setText(query.cariIsiSmc("select pasien.no_tlp from pasien where pasien.no_rkm_medis = ?", noRM.getText()));
                }

                kodePoliTerapi.setText("");
                namaPoliTerapi.setText("");
                kodeDokterTerapi.setText("");
                namaDokterTerapi.setText("");
                kodePoliTerapi.setVisible(false);
                namaPoliTerapi.setVisible(false);
                kodeDokterTerapi.setVisible(false);
                namaDokterTerapi.setVisible(false);
                pilihPoliTerapi.setVisible(false);
                pilihDokterTerapi.setVisible(false);
                labelTerapi.setVisible(false);

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
            headers.add("X-Signature", api.getHmac());
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, entity, String.class).getBody());
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
                noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (noTelpon.getText().equals("null") || noTelpon.getText().isBlank()) {
                    noTelpon.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                }

                kodePoliTerapi.setText("");
                namaPoliTerapi.setText("");
                kodeDokterTerapi.setText("");
                namaDokterTerapi.setText("");
                kodePoliTerapi.setVisible(true);
                namaPoliTerapi.setVisible(true);
                kodeDokterTerapi.setVisible(true);
                namaDokterTerapi.setVisible(true);
                pilihPoliTerapi.setVisible(true);
                pilihDokterTerapi.setVisible(true);
                labelTerapi.setVisible(true);

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

    public void tampilKontrolLama(String noSKDP) {
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
                headers.add("X-Signature", api.getHmac());
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                
                entity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, entity, String.class).getBody());
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
                    noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                    if (noTelpon.getText().equals("null") || noTelpon.getText().isBlank()) {
                        noTelpon.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                    }
                    kodePoliTerapi.setText("");
                    namaPoliTerapi.setText("");
                    kodeDokterTerapi.setText("");
                    namaDokterTerapi.setText("");
                    kodePoliTerapi.setVisible(false);
                    namaPoliTerapi.setVisible(false);
                    kodeDokterTerapi.setVisible(false);
                    namaDokterTerapi.setVisible(false);
                    pilihPoliTerapi.setVisible(false);
                    pilihDokterTerapi.setVisible(false);
                    labelTerapi.setVisible(false);
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
                headers.add("X-Signature", api.getHmac());
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                entity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, entity, String.class).getBody());
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
                    noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                    if (noTelpon.getText().equals("null") || noTelpon.getText().isBlank()) {
                        noTelpon.setText(query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
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
        kodePoliTerapi.setText("");
        namaPoliTerapi.setText("");
        kodeDokterTerapi.setText("");
        namaDokterTerapi.setText("");
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
                    DlgRegistrasiSEPPertama.this.aplikasiAktif = true;
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
            headers.add("X-Signature", api.getHmac());
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

            entity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.PUT, entity, String.class).getBody());
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
            headers.add("X-Signature", api.getHmac());
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, entity, String.class).getBody());
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
                noTelpon.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (noTelpon.getText().equals("null") || noTelpon.getText().isBlank()) {
                    noTelpon.setText(query.cariIsi("select pasien.no_tlp from pasien where pasien.no_rkm_medis='" + noRM.getText() + "'"));
                }
                kodePPK.setText(response.path("provPerujuk").path("kode").asText());
                namaPPK.setText(response.path("provPerujuk").path("nama").asText());
                valid.SetTgl(tglRujuk, response.path("tglKunjungan").asText());
                asalRujukan.setSelectedIndex(1);
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");

                kodePoliTerapi.setText("");
                namaPoliTerapi.setText("");
                kodeDokterTerapi.setText("");
                namaDokterTerapi.setText("");
                kodePoliTerapi.setVisible(false);
                namaPoliTerapi.setVisible(false);
                kodeDokterTerapi.setVisible(false);
                namaDokterTerapi.setVisible(false);
                pilihPoliTerapi.setVisible(false);
                pilihDokterTerapi.setVisible(false);
                labelTerapi.setVisible(false);

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
    
    public void tampilRujukanPertama(String input) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        utc = api.getUTCDateTime();
        url = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + input;

        // Rujukan FKTP
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac());
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
            metaData = root.path("metaData");
            if (metaData.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                asalRujukan.setSelectedIndex(0);
                jenisPelayanan.setSelectedIndex(1);
                noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", input));
                namaPasien.setText(response.path("rujukan").path("pasien").path("nama").asText());
                tglLahir.setText(valid.SetTgl(response.path("rujukan").path("pasien").path("tglLahir").asText()));
                statusPeserta.setText(response.path("rujukan").path("pasien").path("statusPeserta").path("kode").asText() + " " + response.path("rujukan").path("pasien").path("statusPeserta").path("keterangan").asText());
                noRujukan.setText(response.path("rujukan").path("noKunjungan").asText());
                kodePPK.setText(response.path("rujukan").path("provPerujuk").path("kode").asText());
                namaPPK.setText(response.path("rujukan").path("provPerujuk").path("nama").asText());
                kodeDiagnosa.setText(response.path("rujukan").path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("rujukan").path("diagnosa").path("nama").asText());
                kodePoli.setText(response.path("rujukan").path("poliRujukan").path("kode").asText());
                namaPoli.setText(response.path("rujukan").path("poliRujukan").path("nama").asText());
                kodePoliRS = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_rs = ?", kodePoli.getText());
                switch (response.path("rujukan").path("hakKelas").path("kode").asText()) {
                    case "1": kelas.setSelectedIndex(0); break;
                    case "2": kelas.setSelectedIndex(1); break;
                    case "3": kelas.setSelectedIndex(2); break;
                }
                jenisPeserta.setText(response.path("rujukan").path("pasien").path("jenisPeserta").path("keterangan").asText());
                jk.setText(response.path("rujukan").path("pasien").path("sex").asText());
                _nik = response.path("rujukan").path("pasien").path("nik").asText().replace("null,", "");
                if (_nik.isBlank()) _nik = query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText());
                nik.setText(_nik);
                noKartu.setText(response.path("rujukan").path("pasien").path("noKartu").asText());
                tglSEP.setDate(new Date());
                tglRujuk.setSelectedItem(valid.SetTgl(response.path("rujukan").path("tglKunjungan").asText()));
                _noTelp = response.path("rujukan").path("pasien").path("mr").path("noTelepon").asText().replace("null", "");
                if (_noTelp.isBlank()) _noTelp = query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText());
                noTelpon.setText(_noTelp);
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
            } else {
                System.out.println("Respon pencarian rujukan FKTP BPJS : " + metaData.path("code").asText() + " " + metaData.path("message").asText());
                
                // Rujukan FKTL
                utc = api.getUTCDateTime();
                url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + input;
                
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac());
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                entity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                metaData = root.path("metaData");
                if (metaData.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                    asalRujukan.setSelectedIndex(0);
                    jenisPelayanan.setSelectedIndex(1);
                    noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", input));
                    namaPasien.setText(response.path("rujukan").path("pasien").path("nama").asText());
                    tglLahir.setText(valid.SetTgl(response.path("rujukan").path("pasien").path("tglLahir").asText()));
                    statusPeserta.setText(response.path("rujukan").path("pasien").path("statusPeserta").path("kode").asText() + " " + response.path("rujukan").path("pasien").path("statusPeserta").path("keterangan").asText());
                    noRujukan.setText(response.path("rujukan").path("noKunjungan").asText());
                    kodePPK.setText(response.path("rujukan").path("provPerujuk").path("kode").asText());
                    namaPPK.setText(response.path("rujukan").path("provPerujuk").path("nama").asText());
                    kodeDiagnosa.setText(response.path("rujukan").path("diagnosa").path("kode").asText());
                    namaDiagnosa.setText(response.path("rujukan").path("diagnosa").path("nama").asText());
                    kodePoli.setText(response.path("rujukan").path("poliRujukan").path("kode").asText());
                    namaPoli.setText(response.path("rujukan").path("poliRujukan").path("nama").asText());
                    kodePoliRS = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_rs = ?", kodePoli.getText());
                    switch (response.path("rujukan").path("hakKelas").path("kode").asText()) {
                        case "1": kelas.setSelectedIndex(0); break;
                        case "2": kelas.setSelectedIndex(1); break;
                        case "3": kelas.setSelectedIndex(2); break;
                    }
                    jenisPeserta.setText(response.path("rujukan").path("pasien").path("jenisPeserta").path("keterangan").asText());
                    jk.setText(response.path("rujukan").path("pasien").path("sex").asText());
                    _nik = response.path("rujukan").path("pasien").path("nik").asText().replace("null,", "");
                    if (_nik.isBlank()) _nik = query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText());
                    nik.setText(_nik);
                    noKartu.setText(response.path("rujukan").path("pasien").path("noKartu").asText());
                    tglSEP.setDate(new Date());
                    tglRujuk.setSelectedItem(valid.SetTgl(response.path("rujukan").path("tglKunjungan").asText()));
                    _noTelp = response.path("rujukan").path("pasien").path("mr").path("noTelepon").asText().replace("null", "");
                    if (_noTelp.isBlank()) _noTelp = query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText());
                    noTelpon.setText(_noTelp);
                    prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                } else {
                    System.out.println("Respon Pencarian Rujukan FKTL BPJS : " + metaData.path("code").asText() + " " + metaData.path("message").asText());
                    JOptionPane.showMessageDialog(rootPane, "Rujukan pasien tidak ditemukan...!!!");
                    resetInput();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus!");
            }
        }
        
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select jadwal.*, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs " +
            "from jadwal left join maping_dokter_dpjpvclaim on jadwal.kd_dokter = maping_dokter_dpjpvclaim.kd_dokter_rs " +
            "where jadwal.kd_poli = ? and jadwal.hari_kerja = ?"
        )) {
            switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                case 1: hari = "AKHAD"; break;
                case 2: hari = "SENIN"; break;
                case 3: hari = "SELASA"; break;
                case 4: hari = "RABU"; break;
                case 5: hari = "KAMIS"; break;
                case 6: hari = "JUMAT"; break;
                case 7: hari = "SABTU"; break;
                default: break;
            }
            ps.setString(1, kodePoliRS);
            ps.setString(2, hari);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kodeDokter.setText(rs.getString("kd_dokter_bpjs"));
                    namaDokter.setText(rs.getString("nm_dokter_bpjs"));
                    kodeDokterRS = rs.getString("kd_dokter");
                    jadwalMulai = rs.getString("jam_mulai");
                    jadwalSelesai = rs.getString("jam_selesai");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        
        labelTerapi.setVisible(false);
        labelPoliTerapi.setVisible(false);
        kodePoliTerapi.setVisible(false);
        namaPoliTerapi.setVisible(false);
        pilihPoliTerapi.setVisible(false);
        labelDokterTerapi.setVisible(false);
        kodeDokterTerapi.setVisible(false);
        namaDokterTerapi.setVisible(false);
        pilihDokterTerapi.setVisible(false);
        
        this.setCursor(Cursor.getDefaultCursor());
    }
    
    public void tampilRujukanBedaPoli(String input) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        utc = api.getUTCDateTime();
        url = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + input;

        // Rujukan FKTP
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac());
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
            metaData = root.path("metaData");
            if (metaData.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                asalRujukan.setSelectedIndex(1);
                jenisPelayanan.setSelectedIndex(1);
                noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", input));
                namaPasien.setText(response.path("rujukan").path("pasien").path("nama").asText());
                tglLahir.setText(valid.SetTgl(response.path("rujukan").path("pasien").path("tglLahir").asText()));
                statusPeserta.setText(response.path("rujukan").path("pasien").path("statusPeserta").path("kode").asText() + " " + response.path("rujukan").path("pasien").path("statusPeserta").path("keterangan").asText());
                noRujukan.setText(response.path("rujukan").path("noKunjungan").asText());
                kodePPK.setText(response.path("rujukan").path("provPerujuk").path("kode").asText());
                namaPPK.setText(response.path("rujukan").path("provPerujuk").path("nama").asText());
                kodeDiagnosa.setText(response.path("rujukan").path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("rujukan").path("diagnosa").path("nama").asText());
                kodePoli.setText(response.path("rujukan").path("poliRujukan").path("kode").asText());
                namaPoli.setText(response.path("rujukan").path("poliRujukan").path("nama").asText());
                kodePoliRS = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_rs = ?", kodePoli.getText());
                switch (response.path("rujukan").path("hakKelas").path("kode").asText()) {
                    case "1": kelas.setSelectedIndex(0); break;
                    case "2": kelas.setSelectedIndex(1); break;
                    case "3": kelas.setSelectedIndex(2); break;
                }
                jenisPeserta.setText(response.path("rujukan").path("pasien").path("jenisPeserta").path("keterangan").asText());
                jk.setText(response.path("rujukan").path("pasien").path("sex").asText());
                _nik = response.path("rujukan").path("pasien").path("nik").asText().replace("null,", "");
                if (_nik.isBlank()) _nik = query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText());
                nik.setText(_nik);
                noKartu.setText(response.path("rujukan").path("pasien").path("noKartu").asText());
                tglSEP.setDate(new Date());
                tglRujuk.setSelectedItem(valid.SetTgl(response.path("rujukan").path("tglKunjungan").asText()));
                _noTelp = response.path("rujukan").path("pasien").path("mr").path("noTelepon").asText().replace("null", "");
                if (_noTelp.isBlank()) _noTelp = query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText());
                noTelpon.setText(_noTelp);
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
            } else {
                System.out.println("Respon Pencarian Rujukan FKTP BPJS : " + metaData.path("code").asText() + " " + metaData.path("message").asText());
                
                // Rujukan FKTL
                utc = api.getUTCDateTime();
                url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + input;
                
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac());
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                entity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                metaData = root.path("metaData");
                if (metaData.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                    asalRujukan.setSelectedIndex(0);
                    jenisPelayanan.setSelectedIndex(1);
                    noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", input));
                    namaPasien.setText(response.path("rujukan").path("pasien").path("nama").asText());
                    tglLahir.setText(valid.SetTgl(response.path("rujukan").path("pasien").path("tglLahir").asText()));
                    statusPeserta.setText(response.path("rujukan").path("pasien").path("statusPeserta").path("kode").asText() + " " + response.path("rujukan").path("pasien").path("statusPeserta").path("keterangan").asText());
                    noRujukan.setText(response.path("rujukan").path("noKunjungan").asText());
                    kodePPK.setText(response.path("rujukan").path("provPerujuk").path("kode").asText());
                    namaPPK.setText(response.path("rujukan").path("provPerujuk").path("nama").asText());
                    kodeDiagnosa.setText(response.path("rujukan").path("diagnosa").path("kode").asText());
                    namaDiagnosa.setText(response.path("rujukan").path("diagnosa").path("nama").asText());
                    kodePoli.setText(response.path("rujukan").path("poliRujukan").path("kode").asText());
                    namaPoli.setText(response.path("rujukan").path("poliRujukan").path("nama").asText());
                    kodePoliRS = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_rs = ?", kodePoli.getText());
                    switch (response.path("rujukan").path("hakKelas").path("kode").asText()) {
                        case "1": kelas.setSelectedIndex(0); break;
                        case "2": kelas.setSelectedIndex(1); break;
                        case "3": kelas.setSelectedIndex(2); break;
                    }
                    jenisPeserta.setText(response.path("rujukan").path("pasien").path("jenisPeserta").path("keterangan").asText());
                    jk.setText(response.path("rujukan").path("pasien").path("sex").asText());
                    _nik = response.path("rujukan").path("pasien").path("nik").asText().replace("null,", "");
                    if (_nik.isBlank()) _nik = query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText());
                    nik.setText(_nik);
                    noKartu.setText(response.path("rujukan").path("pasien").path("noKartu").asText());
                    tglSEP.setDate(new Date());
                    tglRujuk.setSelectedItem(valid.SetTgl(response.path("rujukan").path("tglKunjungan").asText()));
                    _noTelp = response.path("rujukan").path("pasien").path("mr").path("noTelepon").asText().replace("null", "");
                    if (_noTelp.isBlank()) _noTelp = query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText());
                    noTelpon.setText(_noTelp);
                    prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                } else {
                    System.out.println("Respon Pencarian Rujukan FKTL BPJS : " + metaData.path("code").asText() + " " + metaData.path("message").asText());
                    JOptionPane.showMessageDialog(rootPane, "Rujukan pasien tidak ditemukan...!!!");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus!");
            }
        }
        
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select jadwal.*, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs " +
            "from jadwal left join maping_dokter_dpjpvclaim on jadwal.kd_dokter = maping_dokter_dpjpvclaim.kd_dokter_rs " +
            "where jadwal.kd_poli = ? and jadwal.hari_kerja = ?"
        )) {
            switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                case 1: hari = "AKHAD"; break;
                case 2: hari = "SENIN"; break;
                case 3: hari = "SELASA"; break;
                case 4: hari = "RABU"; break;
                case 5: hari = "KAMIS"; break;
                case 6: hari = "JUMAT"; break;
                case 7: hari = "SABTU"; break;
                default: break;
            }
            ps.setString(1, kodePoliRS);
            ps.setString(2, hari);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kodeDokter.setText(rs.getString("kd_dokter_bpjs"));
                    namaDokter.setText(rs.getString("nm_dokter_bpjs"));
                    kodeDokterRS = rs.getString("kd_dokter");
                    jadwalMulai = rs.getString("jam_mulai");
                    jadwalSelesai = rs.getString("jam_selesai");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        
        labelTerapi.setVisible(true);
        labelPoliTerapi.setVisible(true);
        kodePoliTerapi.setVisible(true);
        namaPoliTerapi.setVisible(true);
        pilihPoliTerapi.setVisible(true);
        labelDokterTerapi.setVisible(true);
        kodeDokterTerapi.setVisible(true);
        namaDokterTerapi.setVisible(true);
        pilihDokterTerapi.setVisible(true);
        
        this.setCursor(Cursor.getDefaultCursor());
    }

    public void tampilKontrol(String input) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        String sql = "select bridging_surat_kontrol_bpjs.*, bridging_sep.no_rujukan, bridging_sep.no_kartu, bridging_sep.jnspelayanan, " +
            "left(bridging_sep.asal_rujukan, 1) as asal_rujukan, bridging_sep.kdppkrujukan, bridging_sep.nmppkrujukan, bridging_sep.klsrawat " +
            "from bridging_surat_kontrol_bpjs join bridging_sep on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep " +
            "where bridging_surat_kontrol_bpjs.no_surat = ?";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, input);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Kontrol Post Ranap
                    // Gunakan pencarian kartu peserta BPJS pasien, data rujukan SKDP dari sisi RS
                    if (rs.getString("jnspelayanan").equals("1")) {
                        utc = api.getUTCDateTime();
                        url = koneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rs.getString("no_kartu") + "/tglSEP/" + valid.setTglSmc(tglSEP);
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                        headers.add("X-Timestamp", utc);
                        headers.add("X-Signature", api.getHmac());
                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                        entity = new HttpEntity(headers);
                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                        metaData = root.path("metaData");
                        if (metaData.path("code").asText().equals("200")) {
                            asalRujukan.setSelectedIndex(1);
                            jenisPelayanan.setSelectedIndex(1);
                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                            noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", rs.getString("no_kartu")));
                            namaPasien.setText(response.path("peserta").path("nama").asText());
                            tglLahir.setText(valid.SetTgl(response.path("peserta").path("tglLahir").asText()));
                            statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + ". " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                            noSuratKontrol.setText(rs.getString("no_surat"));
                            noRujukan.setText(rs.getString("no_sep"));
                            kodePPK.setText(kodePPKPelayanan.getText());
                            namaPPK.setText(namaPPKPelayanan.getText());
                            kodePoli.setText(rs.getString("kd_poli_bpjs"));
                            namaPoli.setText(rs.getString("nm_poli_bpjs"));
                            kodePoliRS = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", rs.getString("kd_poli_bpjs"));
                            kodeDokter.setText(rs.getString("kd_dokter_bpjs"));
                            namaDokter.setText(rs.getString("nm_dokter_bpjs"));
                            kodeDokterRS = query.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_rs = ?", rs.getString("kd_dokter_bpjs"));
                            switch (rs.getString("klsrawat")) {
                                case "1": kelas.setSelectedIndex(0); break;
                                case "2": kelas.setSelectedIndex(1); break;
                                case "3": kelas.setSelectedIndex(2); break;
                            }
                            kodeDokterLayanan.setText(kodeDokter.getText());
                            namaDokterLayanan.setText(namaDokter.getText());
                            jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                            jk.setText(response.path("peserta").path("sex").asText());
                            _nik = response.path("peserta").path("nik").asText().replace("null,", "");
                            if (_nik.isBlank()) _nik = query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText());
                            nik.setText(_nik);
                            noKartu.setText(response.path("peserta").path("noKartu").asText());
                            tglSEP.setDate(new Date());
                            tglRujuk.setDate(new Date());
                            _noTelp = response.path("peserta").path("mr").path("noTelepon").asText().replace("null", "");
                            if (_noTelp.isBlank()) _noTelp = query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText());
                            noTelpon.setText(_noTelp);
                            prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                        }
                    } else if (rs.getString("jnspelaynan").equals("2")) {
                        // Kontrol rawat jalan
                        // Gunakan pencarian data berdasarkan no. rujukan dari SEP kontrol
                        // Pencarian rujukan FKTP
                        utc = api.getUTCDateTime();
                        url = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rs.getString("no_rujukan");
                        
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                        headers.add("X-Timestamp", utc);
                        headers.add("X-Signature", api.getHmac());
                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                        entity = new HttpEntity(headers);
                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                        metaData = root.path("metaData");
                        if (metaData.path("code").asText().equals("200")) {
                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                            asalRujukan.setSelectedIndex(0);
                            jenisPelayanan.setSelectedIndex(1);
                            noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", rs.getString("no_kartu")));
                            namaPasien.setText(response.path("rujukan").path("pasien").path("nama").asText());
                            tglLahir.setText(valid.SetTgl(response.path("rujukan").path("pasien").path("tglLahir").asText()));
                            statusPeserta.setText(response.path("rujukan").path("pasien").path("statusPeserta").path("kode").asText() + " " + response.path("rujukan").path("pasien").path("statusPeserta").path("keterangan").asText());
                            noRujukan.setText(response.path("rujukan").path("noKunjungan").asText());
                            kodePPK.setText(response.path("rujukan").path("provPerujuk").path("kode").asText());
                            namaPPK.setText(response.path("rujukan").path("provPerujuk").path("nama").asText());
                            kodeDiagnosa.setText(response.path("rujukan").path("diagnosa").path("kode").asText());
                            namaDiagnosa.setText(response.path("rujukan").path("diagnosa").path("nama").asText());
                            kodePoli.setText(response.path("rujukan").path("poliRujukan").path("kode").asText());
                            namaPoli.setText(response.path("rujukan").path("poliRujukan").path("nama").asText());
                            kodePoliRS = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_rs = ?", kodePoli.getText());
                            switch (response.path("rujukan").path("hakKelas").path("kode").asText()) {
                                case "1": kelas.setSelectedIndex(0); break;
                                case "2": kelas.setSelectedIndex(1); break;
                                case "3": kelas.setSelectedIndex(2); break;
                            }
                            jenisPeserta.setText(response.path("rujukan").path("pasien").path("jenisPeserta").path("keterangan").asText());
                            jk.setText(response.path("rujukan").path("pasien").path("sex").asText());
                            _nik = response.path("rujukan").path("pasien").path("nik").asText().replace("null,", "");
                            if (_nik.isBlank()) _nik = query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText());
                            nik.setText(_nik);
                            noKartu.setText(response.path("rujukan").path("pasien").path("noKartu").asText());
                            tglSEP.setDate(new Date());
                            tglRujuk.setSelectedItem(valid.SetTgl(response.path("rujukan").path("tglKunjungan").asText()));
                            _noTelp = response.path("rujukan").path("pasien").path("mr").path("noTelepon").asText().replace("null", "");
                            if (_noTelp.isBlank()) _noTelp = query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText());
                            noTelpon.setText(_noTelp);
                            prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                        } else {
                            System.out.println("Respon pencarian rujukan FKTP BPJS : " + metaData.path("code").asText() + " " + metaData.path("message").asText());

                            // Pencarian rujukan FKTL
                            utc = api.getUTCDateTime();
                            url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rs.getString("no_rujukan");

                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac());
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            entity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                            metaData = root.path("metaData");
                            if (metaData.path("code").asText().equals("200")) {
                                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                                asalRujukan.setSelectedIndex(0);
                                jenisPelayanan.setSelectedIndex(1);
                                noRM.setText(query.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", rs.getString("no_kartu")));
                                namaPasien.setText(response.path("rujukan").path("pasien").path("nama").asText());
                                tglLahir.setText(valid.SetTgl(response.path("rujukan").path("pasien").path("tglLahir").asText()));
                                statusPeserta.setText(response.path("rujukan").path("pasien").path("statusPeserta").path("kode").asText() + " " + response.path("rujukan").path("pasien").path("statusPeserta").path("keterangan").asText());
                                noRujukan.setText(response.path("rujukan").path("noKunjungan").asText());
                                kodePPK.setText(response.path("rujukan").path("provPerujuk").path("kode").asText());
                                namaPPK.setText(response.path("rujukan").path("provPerujuk").path("nama").asText());
                                kodeDiagnosa.setText(response.path("rujukan").path("diagnosa").path("kode").asText());
                                namaDiagnosa.setText(response.path("rujukan").path("diagnosa").path("nama").asText());
                                kodePoli.setText(response.path("rujukan").path("poliRujukan").path("kode").asText());
                                namaPoli.setText(response.path("rujukan").path("poliRujukan").path("nama").asText());
                                kodePoliRS = query.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_rs = ?", kodePoli.getText());
                                switch (response.path("rujukan").path("hakKelas").path("kode").asText()) {
                                    case "1": kelas.setSelectedIndex(0); break;
                                    case "2": kelas.setSelectedIndex(1); break;
                                    case "3": kelas.setSelectedIndex(2); break;
                                }
                                jenisPeserta.setText(response.path("rujukan").path("pasien").path("jenisPeserta").path("keterangan").asText());
                                jk.setText(response.path("rujukan").path("pasien").path("sex").asText());
                                _nik = response.path("rujukan").path("pasien").path("nik").asText().replace("null,", "");
                                if (_nik.isBlank()) _nik = query.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText());
                                nik.setText(_nik);
                                noKartu.setText(response.path("rujukan").path("pasien").path("noKartu").asText());
                                tglSEP.setDate(new Date());
                                tglRujuk.setSelectedItem(valid.SetTgl(response.path("rujukan").path("tglKunjungan").asText()));
                                _noTelp = response.path("rujukan").path("pasien").path("mr").path("noTelepon").asText().replace("null", "");
                                if (_noTelp.isBlank()) _noTelp = query.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText());
                                noTelpon.setText(_noTelp);
                                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                            } else {
                                System.out.println("Respon pPencarian rujukan FKTL BPJS : " + metaData.path("code").asText() + " " + metaData.path("message").asText());
                                JOptionPane.showMessageDialog(rootPane, "Rujukan pasien tidak ditemukan...!!!");
                            }
                        }
                    }
                    kodeDiagnosa.setText("Z09.8");
                    namaDiagnosa.setText("Z09.8 - Follow-up examination after other treatment for other conditions");
                    tujuanKunjungan.setSelectedIndex(2);
                    flagProsedur.setSelectedIndex(0);
                    penunjang.setSelectedIndex(0);
                    asesmenPelayanan.setSelectedIndex(5);
                    labelTerapi.setVisible(false);
                    labelPoliTerapi.setVisible(false);
                    kodePoliTerapi.setVisible(false);
                    namaPoliTerapi.setVisible(false);
                    pilihPoliTerapi.setVisible(false);
                    labelDokterTerapi.setVisible(false);
                    kodeDokterTerapi.setVisible(false);
                    namaDokterTerapi.setVisible(false);
                    pilihDokterTerapi.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(rootPane, "No. SKDP tidak ditemukan!\nSilahkan hubungi administrasi.");
                    resetInput();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan pada saat mencari SKDP Pasien!\nSilahkan hubungi administrasi.");
            resetInput();
        }
        
        this.setCursor(Cursor.getDefaultCursor());
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
        noRegInt = Integer.parseInt(noReg);
        noRawat = query.cariIsiSmc(
            "select concat(date_format(tgl_registrasi, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_rawat, 6), signed)), 0) + 1, 6, '0')) from reg_periksa where no_rawat = concat(date_format(?, '%Y/%m/%d'), '/%')",
            valid.SetTgl(tglSEP.getSelectedItem().toString())
        );
    }
    
    private void cekStatusPasien() {
        statusPoli = (query.cariBooleanSmc("select * from reg_periksa where no_rkm_medis = ? and kd_poli = ?", noRM.getText(), kodePoliRS) ? "Lama" : "Baru");
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
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private void cekStatusFingerprint() {
        statusFP = false;
        
        if (noKartu.getText().isBlank()) {
            valid.textKosong(noKartu, "No. Kartu BPJS");
            return;
        }

        try {
            utc = String.valueOf(api.getUTCDateTime());
            url = koneksiDB.URLAPIBPJS() + "/SEP/FingerPrint/Peserta/" + noKartu.getText() + "/TglPelayanan/" + valid.setTglSmc(tglSEP);
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac());
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());            
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
            metaData = root.path("metaData");
            if (metaData.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                if (response.path("kode").asText().equals("1")) {
                    statusFP = response.path("status").asText().contains(valid.setTglSmc(tglSEP));
                    if (! statusFP) {
                        JOptionPane.showMessageDialog(rootPane, response.path("status").asText());
                    }
                }
            } else {
                System.out.println("Respon status FP : " + metaData.path("code").asText() + " " + metaData.path("message").asText());
                JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan pada saat pengecekan status FP pasien!\nSilahkan hubungi administrasi!");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }
    
    private boolean validasiInput() {
        if (noRM.getText().isBlank() || namaPasien.getText().isBlank()) {
            valid.textKosong(noRM, "Pasien");
        } else if (noKartu.getText().isBlank()) {
            valid.textKosong(noKartu, "Nomor Kartu");
        } else if (query.cariBooleanSmc("select * from pasien where no_rkm_medis = ?", noRM.getText())) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. RM Pasien tidak sesuai..!!");
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
        } else if (! statusFP && query.cariIntegerSmc("select timestampdiff(year, ?, current_date())", tglLahir.getText()) >= 17 && jenisPelayanan.getSelectedIndex() != 0 && !kodePoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Pasien belum melakukan Fingerprint..!!");
            bukaAplikasiFingerprint();
        } else if (query.cariBooleanSmc("select * from reg_periksa where no_rkm_medis = ? and tgl_registrasi = ? and kd_poli = ? and kd_dokter = ? and kd_pj = 'BPJ'", noRM.getText(), valid.setTglSmc(tglSEP), kodePoliRS)) {
            JOptionPane.showMessageDialog(rootPane, "Pasien sudah terdaftar pada hari ini dengan tujuan poli, dokter, dan jaminan yang sama!");
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
    
    private void updateDataPasien() {
        query.mengupdateSmc("pasien",
            "no_tlp = ?, no_ktp = ?, umur = concat(concat(concat(timestampdiff(year, tgl_lahir, curdate()), ' Th '), concat(timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12), ' Bl ')), concat(timestampdiff(day, date_add(date_add(tgl_lahir, interval timestampdiff(year, tgl_lahir, curdate()) year), interval timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12) month), curdate()), ' Hr'))",
            "no_rkm_medis = ?",
            noTelpon.getText(), noRM.getText()
        );
    }
    
    private void simpanAntrianOnsite() {
        if (noRujukan.getText().isBlank() || noSuratKontrol.getText().isBlank()) {
            System.out.println("No. rujukan tidak boleh kosong...!!!");
            return;
        }
        
        if (query.cariBooleanSmc("select * from referensi_mobilejkn_bpjs where no_rawat = ?", noRawat)) {
            System.out.println("Pasien sudah mengambil antrian di MobileJKN...!!!");
            return;
        }
        
        if (tujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && flagProsedur.getSelectedItem().toString().equals("") && penunjang.getSelectedItem().toString().equals("") && asesmenPelayanan.getSelectedItem().toString().equals("")) {
            if (asalRujukan.getSelectedIndex() == 0) {
                jenisKunjungan = "1";
            } else {
                jenisKunjungan = "4";
            }
        } else if (tujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && flagProsedur.getSelectedItem().toString().equals("") && penunjang.getSelectedItem().toString().equals("") && asesmenPelayanan.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
            jenisKunjungan = "3";
        } else if (tujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && flagProsedur.getSelectedItem().toString().equals("") && penunjang.getSelectedItem().toString().equals("") && asesmenPelayanan.getSelectedItem().toString().equals("4. Atas Instruksi RS")) {
            jenisKunjungan = "2";
        } else if (tujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && flagProsedur.getSelectedItem().toString().equals("") && penunjang.getSelectedItem().toString().equals("") && asesmenPelayanan.getSelectedItem().toString().equals("1. Poli spesialis tidak tersedia pada hari sebelumnya")) {
            jenisKunjungan = "2";
        } else {
            if (tujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && asesmenPelayanan.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
                jenisKunjungan = "3";
            } else {
                jenisKunjungan = "2";
            }
        }

        try {
            switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                case 1: hari = "AKHAD"; break;
                case 2: hari = "SENIN"; break;
                case 3: hari = "SELASA"; break;
                case 4: hari = "RABU"; break;
                case 5: hari = "KAMIS"; break;
                case 6: hari = "JUMAT"; break;
                case 7: hari = "SABTU"; break;
                default: break;
            }
            
            String queryJamMulaiSelesai = "";
            Date date = null;
            int sisaKuota = 0;
            
            if (! jadwalMulai.isBlank() && ! jadwalSelesai.isBlank()) {
                queryJamMulaiSelesai = " and jam_mulai = ? and jam_selesai = ?";
            }
            
            try (PreparedStatement ps = koneksi.prepareStatement("select jam_mulai, jam_selesai, kuota from jadwal where hari_kerja = ? and kd_poli = ? and kd_dokter = ?" + queryJamMulaiSelesai)) {
                ps.setString(1, hari);
                ps.setString(2, kodePoliRS);
                ps.setString(3, kodeDokterRS);
                if (! jadwalMulai.isBlank() && ! jadwalSelesai.isBlank()) {
                    ps.setString(4, jadwalMulai);
                    ps.setString(5, jadwalSelesai);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        jadwalMulai = rs.getString("jam_mulai");
                        jadwalSelesai = rs.getString("jam_selesai");
                        kuota = rs.getInt("kuota");
                        date = dtf.parse(query.cariIsiSmc(
                            "select date_add(concat(?, ' ', ?), interval ? minute)",
                            valid.setTglSmc(tglSEP), jadwalMulai, String.valueOf(noRegInt * 10)
                        ));
                        sisaKuota = kuota - noRegInt;
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Jadwal Praktek Dokter tidak ada...!!!");
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            
            if (date == null) {
                System.out.println("Terjadi kesalahan pada saat mencoba tambah antrian pasien...!!!");
                return;
            }
            
            if (! noSuratKontrol.getText().isBlank()) {
                try {
                    utc = api.getUTCDateTime();
                    url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                    
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                    headers.add("x-timestamp", utc);
                    headers.add("x-signature", api.getHmac());
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
                        + "\"kodedokter\": " + kodeDokter.getText() + ","
                        + "\"namadokter\": \"" + namaDokter.getText() + "\","
                        + "\"jampraktek\": \"" + jadwalMulai.substring(0, 5) + "-" + jadwalSelesai.substring(0, 5) + "\","
                        + "\"jeniskunjungan\": " + jenisKunjungan + ","
                        + "\"nomorreferensi\": \"" + noSuratKontrol.getText() + "\","
                        + "\"nomorantrean\": \"" + noReg + "\","
                        + "\"angkaantrean\": " + noRegInt + ","
                        + "\"estimasidilayani\": " + date.getTime() + ","
                        + "\"kuotajkn\": " + kuota + ","
                        + "\"sisakuotajkn\": " + sisaKuota + ","
                        + "\"kuotanonjkn\": " + kuota + ","
                        + "\"sisakuotanonjkn\": " + sisaKuota + ","
                        + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi. Estimasi pelayanan 10 menit per pasien\""
                    + "}";
                    entity = new HttpEntity(json, headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                    System.out.println("Respon WS BPJS : " + root.path("metadata").path("code").asText() + " " + root.path("metadata").path("message").asText());
                } catch (Exception e) {
                    System.out.println("Notif " + e);
                    if (e.toString().contains("UnknownHostException")) {
                        System.out.println("Notif : Koneksi ke server BPJS terputus...!!!");
                    }
                }
            }
            
            if (! noRujukan.getText().isBlank()) {
                try {
                    utc = api.getUTCDateTime();
                    url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                    headers.add("x-timestamp", utc);
                    headers.add("x-signature", api.getHmac());
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
                        + "\"kodedokter\": " + kodeDokter.getText() + ","
                        + "\"namadokter\": \"" + namaDokter.getText() + "\","
                        + "\"jampraktek\": \"" + jadwalMulai.substring(0, 5) + "-" + jadwalSelesai.substring(0, 5) + "\","
                        + "\"jeniskunjungan\": " + jenisKunjungan + ","
                        + "\"nomorreferensi\": \"" + noRujukan.getText() + "\","
                        + "\"nomorantrean\": \"" + noReg + "\","
                        + "\"angkaantrean\": " + noRegInt + ","
                        + "\"estimasidilayani\": " + date.getTime() + ","
                        + "\"kuotajkn\": " + kuota + ","
                        + "\"sisakuotajkn\": " + sisaKuota + ","
                        + "\"kuotanonjkn\": " + kuota + ","
                        + "\"sisakuotanonjkn\": " + sisaKuota + ","
                        + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi. Estimasi pelayanan 10 menit per pasien\""
                    + "}";
                    entity = new HttpEntity(json, headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                    System.out.println("Respon WS BPJS : " + root.path("metadata").path("code").asText() + " " + root.path("metadata").path("message").asText());
                } catch (Exception e) {
                    System.out.println("Notif " + e);
                    if (e.toString().contains("UnknownHostException")) {
                        System.out.println("Notif : Koneksi ke server BPJS terputus...!!!");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void simpanSEP() {
        String tanggalKLL = "0000-00-00";
        String noSEP = "";
        
        try {
            if (lakaLantas.getSelectedIndex() > 0) {
                tanggalKLL = valid.setTglSmc(tglKLL);
            }
            
            utc = api.getUTCDateTime();
            url = koneksiDB.URLAPIBPJS() + "/SEP/2.0/insert";
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac());
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            json = "{"
                + "\"request\":{"
                + "\"t_sep\":{"
                + "\"noKartu\":\"" + noKartu.getText() + "\","
                + "\"tglSep\":\"" + valid.SetTglSmc(tglSEP) + "\","
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
                + "\"tglRujukan\":\"" + valid.setTglSmc(tglRujuk) + "\","
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
                + "\"tglKejadian\": \"" + tanggalKLL.replaceAll("0000-00-00", "") + "\","
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
                + "\"dpjpLayan\": \"" + (kodeDokterLayanan.getText().isBlank() ? "" : kodeDokterLayanan.getText()) + "\","
                + "\"noTelp\": \"" + noTelpon.getText() + "\","
                + "\"user\":\"" + noKartu.getText() + "\""
                + "}"
                + "}"
                + "}";
            entity = new HttpEntity(json, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
            metaData = root.path("metaData");
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                noSEP = response.path("sep").path("noSep").asText();
                
                query.menyimpanSmc("bridging_sep", null,
                    response.asText(),
                    noRawat,
                    valid.setTglSmc(tglSEP),
                    valid.setTglSmc(tglRujuk),
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
                    (jenisPelayanan.getSelectedIndex() == 1 ? valid.setTglSmc(tglSEP) : "0000-00-00") + " 00:00:00",
                    asalRujukan.getSelectedItem().toString(),
                    "0. Tidak",
                    "0. Tidak",
                    noTelpon.getText(),
                    katarak.getSelectedItem().toString(),
                    tanggalKLL,
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
                    (flagProsedur.getSelectedIndex() > 0 ? String.valueOf(flagProsedur.getSelectedIndex()) : ""),
                    (penunjang.getSelectedIndex() > 0 ? String.valueOf(penunjang.getSelectedIndex()) : ""),
                    (asesmenPelayanan.getSelectedIndex() > 0 ? String.valueOf(asesmenPelayanan.getSelectedIndex()) : ""),
                    kodeDokterLayanan.getText(),
                    namaDokterLayanan.getText()
                );
                
                if (! simpanRujukan()) {
                    System.out.println("Terjadi kesalahan pada saat proses rujukan masuk pasien!");
                }

                if (! prb.equals("")) {
                    query.menyimpanSmc("bpjs_prb", null, noSEP, prb);
                }
                
                if (query.cariBooleanSmc(
                    "select * from booking_registrasi where no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ? and status != 'Terdaftar'",
                    noRM.getText(), valid.setTglSmc(tglSEP), kodeDokterRS, kodePoliRS
                )) {
                    query.mengupdateSmc("booking_registrasi",
                        "status = 'Terdaftar', waktu_kunjungan = now()", "no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ?",
                        noRM.getText(), valid.setTglSmc(tglSEP), kodeDokterRS, kodePoliRS
                    );
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
    
    private boolean simpanRujukan() {
        int coba = 0, maxCoba = 5;
        String noRujukRS = query.cariIsiSmc(
            "select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
            valid.setTglSmc(tglSEP), valid.setTglSmc(tglSEP)
        );
        boolean sukses = query.menyimpantfSmc("rujuk_masuk", null, noRawat, namaPPK.getText(), "-", noRujukan.getText(), "0", namaPPK.getText(), kodeDiagnosa.getText(), "-", "-", noRujukRS);
        
        while (coba < maxCoba && ! sukses) {
            noRujukRS = query.cariIsiSmc(
                "select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                valid.setTglSmc(tglSEP), valid.setTglSmc(tglSEP)
            );
            sukses = query.menyimpantfSmc("rujuk_masuk", null, noRawat, namaPPK.getText(), "-", noRujukan.getText(), "0", namaPPK.getText(), kodeDiagnosa.getText(), "-", "-", noRujukRS);
        }
        
        return sukses;
    }
    
    private void printRegistrasi() {
        Map<String, Object> param = new HashMap<>();
        param.put("norawat", noRawat);
        param.put("parameter", noSEP);
        param.put("namars", namaPPKPelayanan.getText());
        param.put("kotars", query.cariIsi("select setting.kabupaten from setting limit 1"));
        
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

    private void updateSKDP() {
        if (noSuratKontrol.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. SKDP kosong...!!!");
            return;
        }
        
        try (PreparedStatement ps = koneksi.prepareStatement("select * from bridging_surat_kontrol_bpjs where no_surat = ?")) {
            ps.setString(1, noSuratKontrol.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    json = "{"
                        + "\"request\": {"
                        + "\"noSuratKontrol\":\"" + rs.getString("no_surat") + "\","
                        + "\"noSEP\":\"" + rs.getString("no_sep") + "\","
                        + "\"kodeDokter\":\"" + rs.getString("kd_dokter_bpjs") + "\","
                        + "\"poliKontrol\":\"" + rs.getString("kd_poli_bpjs") + "\","
                        + "\"tglRencanaKontrol\":\"" + valid.setTglSmc(tglSEP) + "\","
                        + "\"user\":\"" + noKartu.getText() + "\""
                        + "}"
                        + "}";
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        
        try {
            utc = api.getUTCDateTime();
            url = koneksiDB.URLAPIBPJS() + "/RencanaKontrol/Update";
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac());
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            entity = new HttpEntity(json, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.PUT, entity, String.class).getBody());
            metaData = root.path("metaData");
            if (metaData.path("code").asText().equals("200")) {
                // TODO: konfirmasi apabila perubahan SKDP juga merubah poli dan dokternya
                query.mengupdateSmc("bridging_surat_kontrol_bpjs",
                    "tgl_rencana = ?", "no_surat = ?",
                    valid.setTglSmc(tglSEP), noSuratKontrol.getText()
                );
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
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
        kodeDokterLayanan.setText("");
        namaDokterLayanan.setText("");
        kodePoliTerapi.setVisible(true);
        namaPoliTerapi.setVisible(true);
        kodeDokterTerapi.setVisible(true);
        namaDokterTerapi.setVisible(true);
        labelTerapi.setVisible(true);
        labelPoliTerapi.setVisible(true);
        labelDokterTerapi.setVisible(true);
        pilihPoliTerapi.setVisible(true);
        pilihDokterTerapi.setVisible(true);
        jenisPeserta.setText("");
        jk.setText("");
        nik.setText("");
        noKartu.setText("");
        asalRujukan.setSelectedIndex(0);
        tglSEP.setDate(new Date());
        tglRujuk.setDate(new Date());
        noTelpon.setText("");
        katarak.setSelectedIndex(0);
        lakaLantas.setSelectedIndex(0);
        labelTglKLL.setVisible(false);
        tglKLL.setDate(new Date());
        tglKLL.setVisible(false);
        labelKeterangan.setVisible(false);
        keterangan.setText("");
        keterangan.setVisible(false);
        labelSuplesi.setVisible(false);
        suplesi.setSelectedIndex(0);
        suplesi.setVisible(false);
        labelSEPSuplesi.setVisible(false);
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
        labelCatatan.setLocation(625, 280);
        catatan.setLocation(730, 280);
        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        jumlahBarcode.setText("3");
    }
}
