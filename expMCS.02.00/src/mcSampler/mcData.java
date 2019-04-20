/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mcSampler;

import io.prnt;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import phase.mc.PHASEMCBINCE;

/**
 *
 * @author metallurgy
 */
public class mcData {
    //Highest symmetry phase information
    // Phase specific information

    private PHASEMCBINCE m_phasemcbince;
    private String m_phaseTag;
    private int m_tcf;//Total number of correlation functions
    // Parameters
    private double m_R;
    private double[] m_ECI;
    private double m_T;
    private double m_xB;
    private double m_Mu;
    // MC Specific parameters
    private int m_latticeSize;
    private int m_latticeSites;
    //Sampler Specific parameters
    private int m_EQMCSS;
    private int m_MCSS;
    private int m_NDP;// no of data points
    private double m_runtime;
    //mcData Specific Parameters
    private double[][] m_corrFuncArray;//Array containing values of correlation functions for number of equilibrium MC runs
    private double[] m_avgU;
    private double[] m_avgSqU;//average of squared values of correlation functions
    private double[] m_statError;
    private double[] m_EArray;
    private double m_avgE;
    private double m_avgE2;
    private double m_avgE4;
    private double m_CV;
    public static String fileName = "", newtime;
    private static DateFormat df = new SimpleDateFormat("yyMMdd");
    private static DateFormat tf = new SimpleDateFormat("HHmmss");
    private DecimalFormat df1 = new DecimalFormat("0.000");

    public mcData() {
        System.out.println("Default mcData constructor method called ");
    }

    public mcData(int DataPoints) {
        this.m_NDP = DataPoints;
        this.m_corrFuncArray = new double[DataPoints][];
    }

    public mcData(int DataPoints, int tcf) {
        this.m_NDP = DataPoints;
        this.m_tcf = tcf;
        this.m_corrFuncArray = new double[DataPoints][tcf];
        this.m_avgU = new double[tcf];
    }

    public mcData(int DataPoints, PHASEMCBINCE lattice) throws IOException {//vj-2012-12-26
        prnt.writeln("mcData constructor method called");
        //Phase Specific parameters
        this.m_phasemcbince = lattice;
        this.m_phaseTag = lattice.getPhaseTag();
        this.m_tcf = lattice.getTcf();
        //Parameters
        this.m_R = lattice.getR();
        this.m_ECI = lattice.getEcdis();
        this.m_T = lattice.getT();
        this.m_xB = lattice.getX();
        this.m_Mu = lattice.getMU();
        //MC specific Parameters
        this.m_latticeSize = lattice.getLatticeSize();
        this.m_latticeSites = lattice.getNLP();
        //mcData specific parameters
        this.m_NDP = DataPoints;
        this.m_corrFuncArray = new double[m_NDP][m_tcf];
        this.m_avgU = new double[m_tcf];
        this.m_avgSqU = new double[m_tcf];
        this.m_statError = new double[m_tcf];
        this.m_EArray = new double[m_NDP];
        prnt.writeln("mcData constructor method ended");
    }

    public void calStat() throws IOException {
        m_avgE = 0;
        m_avgE2 = 0;
        m_avgE4 = 0;
        for (int j = 0; j < m_tcf; j++) {
            m_avgU[j] = 0;
            m_avgSqU[j] = 0;
        }
        for (int i = 0; i < m_NDP; i++) {
            //m_EArray[i] = m_phasemcbince.calHmc(m_corrFuncArray[i]);
            m_phasemcbince.setU(m_corrFuncArray[i]);//vj-2013-01-13
            m_EArray[i] = m_phasemcbince.calHmc();//vj-2012-12-23-modified
            m_avgE = m_avgE + m_EArray[i];
            m_avgE2 = m_avgE2 + m_EArray[i] * m_EArray[i];
            //m_avgE4 = m_avgE4 + Math.pow(m_EArray[i], 4);//vj-2013-01-13
            //m_avgE4 = m_avgE4 + Math.pow(m_corrFuncArray[i][4], 4);//vj-2013-01-13
            for (int j = 0; j < m_tcf; j++) {
                m_avgU[j] = m_avgU[j] + m_corrFuncArray[i][j];
                m_avgSqU[j] = m_avgSqU[j] + m_corrFuncArray[i][j] * m_corrFuncArray[i][j];
                //System.out.println(m_avgU[j]);
            }
        }
        m_avgE = m_avgE / m_NDP;
        m_avgE2 = m_avgE2 / m_NDP;
        //m_avgE4 = m_avgE4 / m_NDP;//vj-2013-01-13
        m_CV = ((m_avgE2 - m_avgE * m_avgE) * m_latticeSites) / (m_R * m_T * m_T);
        //m_U4 = 1 - (m_avgE4 / (3 * m_avgSqU[4] * m_avgSqU[4]));
        for (int j = 0; j < m_tcf; j++) {
            m_avgU[j] = m_avgU[j] / m_NDP;
            m_avgSqU[j] = m_avgSqU[j] / m_NDP;
            m_statError[j] = Math.sqrt(((m_avgSqU[j] - (m_avgU[j] * m_avgU[j])) / (m_NDP - 1)));//vj-2013-01-13
        }

//        for (int j = 0; j < m_tcf; j++) {
//            m_statError[j] = Math.sqrt(((m_avgSqU[j] - (m_avgU[j] * m_avgU[j])) / (m_NDP - 1)));
//        }//vj-2013-01-13
    }
    // Setter Methods

    public void setRunTime(double runtime) {
        m_runtime = runtime;
    }

    public void setCorrFuncArray(double[][] corrFuncArray) {
        m_corrFuncArray = corrFuncArray;
    }

    public void setMCSS(int MCSS) {
        m_MCSS = MCSS;
    }

    public void setEQMCSS(int EQMCSS) {
        m_EQMCSS = EQMCSS;
    }
    // Getter Methods

    public double[] getAvgCorrFunc() {
        return (m_avgU);
    }

    public double[] getStatError() {
        return (m_statError);
    }

    public void printStat(Boolean isPrintToFile) throws IOException {
        prnt.list(m_avgU, "u");
        prnt.list(m_statError, "u_error");
        System.out.println();
        //System.out.println("Specific Heat:" + m_CV);
        //System.out.println("U4:" + m_U4);
        if (isPrintToFile) {
            df.setTimeZone(TimeZone.getTimeZone("IST"));
            tf.setTimeZone(TimeZone.getTimeZone("IST"));
            //fileName = fileName + df.format(new Date());
            //fileName = fileName + "-" + tf.format(new Date());
            fileName = fileName + "mc-" + m_phaseTag + "-" + df1.format(m_ECI[0] / (m_R * m_T)) + "-" + df1.format(m_xB) + "-" + df.format(new Date()) + "-" + tf.format(new Date()) + ".txt";
            prnt.drawLine(fileName);
            prnt.writeln("-------------Job/System Properties--------------------", fileName);
            prnt.writeln("  started on: " + (new Date()).toString(), fileName);
            prnt.writeln("    hostname: " + InetAddress.getLocalHost().getHostName(), fileName);
            prnt.writeln("executing on: " + System.getProperty("os.name").toString(), fileName);
            prnt.writeln("        arch: " + System.getProperty("os.arch"), fileName);
            prnt.writeln("      kernel: " + System.getProperty("os.version"), fileName);
            prnt.writeln(" JVM-version: " + System.getProperty("java.vm.version"), fileName);
            prnt.writeln("  JVM-vender: " + System.getProperty("java.vm.vender"), fileName);
            prnt.writeln("    JVM-name: " + System.getProperty("java.vm.name"), fileName);
            //prnt.writeln("-----------------------------------------------------", fileName);
            prnt.writeln("-------------Phase object parameters------------------", fileName);
            prnt.list(m_phasemcbince.getPhaseTag(), "Phase", fileName);
            prnt.list(m_phasemcbince.getCoordNum(), "coordNum", fileName);
            prnt.list(m_phasemcbince.getTcdis(), "tcdis", fileName);
            prnt.list(m_phasemcbince.getNxcdis(), "nxcdis", fileName);
            prnt.list(m_phasemcbince.getNcdis(), "ncdis", fileName);
            prnt.list(m_phasemcbince.getMgdis(), "mgdis", fileName);
            prnt.list(m_phasemcbince.getRcdis(), "rcdis", fileName);
            prnt.list(m_phasemcbince.getUA(), "uA", fileName);
            prnt.list(m_phasemcbince.getUB(), "uB", fileName);
            prnt.list(m_phasemcbince.getLc(), "lc", fileName);
            prnt.list(m_phasemcbince.getTc(), "tc", fileName);
            prnt.list(m_phasemcbince.getNxc(), "nxc", fileName);
            prnt.list(m_phasemcbince.getNc(), "nc", fileName);
            prnt.list(m_phasemcbince.getLcf(), "lcf", fileName);
            prnt.list(m_phasemcbince.getTcf(), "tcf", fileName);
            prnt.list(m_phasemcbince.getNxcf(), "nxcf", fileName);
            prnt.list(m_phasemcbince.getNcf(), "ncf", fileName);
            prnt.list(m_phasemcbince.getM(), "m", fileName);
            prnt.list(m_phasemcbince.getR(), "R", fileName);
            prnt.list(m_phasemcbince.getEcdis(), "edis", fileName);
            prnt.list(m_phasemcbince.getT(), "T", fileName);
            prnt.list(m_phasemcbince.getX(), "X", fileName);
            prnt.list(m_phasemcbince.getU(), "u", fileName);
            prnt.list(m_phasemcbince.getUt(), "ut", fileName);
            prnt.list(m_phasemcbince.getMcMethod(), "MC method", fileName);
            prnt.list(m_phasemcbince.getLatticeSize(), "Lattice size", fileName);
            prnt.list(m_phasemcbince.getNLP(), "No of lattice points", fileName);
            //prnt.writeln("-----------------------------------------------------", fileName);
            prnt.writeln("-------------mcSampler Parameters---------------------", fileName);
            prnt.writeln("           warm mcss: " + m_EQMCSS, fileName);
            prnt.writeln("                mcss: " + m_MCSS, fileName);
            prnt.writeln("   No of data points: " + m_NDP, fileName);
            prnt.writeln("------------------MC Results--------------------------", fileName);
            prnt.list(m_avgU, "u", fileName);
            prnt.list(m_statError, "error(u)", fileName);
            prnt.list(m_avgE, "Enthalpy", fileName);
            prnt.list(m_CV, "CV", fileName);
        }
//        try {
//            try (FileWriter writer1 = new FileWriter("mcA2.csv", true)) {
//                writer1.append(m_latticeSites + "," + m_EQMCSS + "," + m_MCSS + ",");
//                for (int i = 0; i < m_tcf; i++) {
//                    writer1.append(m_ECI[i] + ",");
//                }
//                writer1.append(m_T + "," + m_xB + "," + m_Mu + ",");
//                for (int i1 = 0; i1 < m_tcf; i1++) {
//                    writer1.append(m_avgU[i1] + ",");
//                }
//                for (int i1 = 0; i1 < m_tcf; i1++) {
//                    writer1.append(m_statError[i1] + ",");
//                }
//                writer1.append(m_avgE + "," + m_CV + "," + (m_runtime / 1000) + ",");
//                writer1.append("\n");
//                writer1.flush();
//            }
//        } catch (IOException e) {
//            System.err.println("Unable to write to file");
//            System.exit(-1);
//        }
//        if (isPrintToFile == true) {
//            String m_outfileNameString = outfileNameString + ".T" + m_T + ".xB" + m_xB + ".Sites" + m_latticeSites + ".v" + System.currentTimeMillis() + ".csv";
//            System.out.println(m_outfileNameString);
//            FileOutputStream fout1;
//            try {
//                fout1 = new FileOutputStream(m_outfileNameString);
//                try (PrintStream pt1 = new PrintStream(fout1)) {
//                    pt1.print("Iteration No" + ",");
//                    for (int i = 0; i < m_tcf; i++) {
//                        pt1.print("CF" + i + ",");
//                    }
//                    pt1.println();
//                    for (int i = 0; (i < m_NDP); i++) {
//                        pt1.print((i + 1) + ",");
//                        for (int j = 0; j < m_tcf; j++) {
//                            pt1.print(m_corrFuncArray[i][j] + ",");
//                        }
//                        pt1.println();
//                    }
//                }
//            } catch (IOException e) {
//                System.err.println("Unable to write to file");
//                System.exit(-1);
//            }
//        }
        fileName = "";
    }
}
