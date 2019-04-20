/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phase.mc;

import io.prnt;
import java.io.IOException;
import phase.PHASEBINCE;

/**
 *
 * @author metallurgy
 */
public abstract class PHASEMCBINCE implements PHASEBINCE {
    //Highest symmetry phase information

    private int coordNum;//coordination number of lattice
    private int tcdis; //No of total clusters
    private int nxcdis; //No of clusters realted to point cluters
    private int ncdis;
    private double mgdis[];//Multiplicities for each cluster
    private int rcdis[];//No of sites for each cluster
    double uA[], uB[];// Value of correlation functions for pure A and pure B resp.
    // Phase specific information
    private String phaseTag;
    private int lc[];//List of clusters in case of broken symemtry (Ordered phases)per highest symmetry (disordred phase) cluster*
    private int tc;//total no of clusters: sum of nc
    private int nxc;//total no of cluster related to point clusters
    private int nc;
    private int lcf[];//No of correlation functions for each disordered cluster
    private int tcf;//No of total correlation functions; Sum of lcf
    private int nxcf;//No of total correlation functions realted to point cluters
    private int ncf;
    private double m[][];//Normalised multiplicities in case of broken symmetry;
    // Parameters
    private double R = 1.0;
    private double ecdis[];//Eci for each correlation function
    private double T;//  Temperature
    private double xB;//  Composition of one component(i.e. B)
    private double MU;//  Average Chemical potential
    private double u[]; //  Correlation functions (flattened)
    private double ut[][];//  Correlation function
    // MC Specific parameters
    private String mcMethod;
    private int latticeSize;
    private int NLP;
    //private int WARMMCSS = 1000;// No of MC Steps for warm up
    //private int MCSS = 4000;// No of MC Steps per site
    //private int NDP = 2000;// no of data points
    private int[][][] config;//3-Dimensional array to store configuration of lattice
    private boolean isequil = false;

    public PHASEMCBINCE() throws IOException {
        prnt.writeln("-------------Creating phase object--------------------");
        prnt.writeln("PHASEMCBINCE constructor method called");
        prnt.writeln("PHASEMCBINCE constructor method ended");
    }

    // Setter Methods
    //Highest symmetry phase information
    public void setCoordNum(int coordNum_In) {
        this.coordNum = coordNum_In;
    }

    public void setTcdis(int tcdis_In) {
        this.tcdis = tcdis_In;
    }

    public void setNxcdis(int nxcdis_In) {
        this.nxcdis = nxcdis_In;
    }

    public void setNcdis(int ncdis_In) {
        this.ncdis = ncdis_In;
    }

    public void setMdis(double[] mdis_In) {
        this.mgdis = mdis_In;
    }

    public void setRcdis(int[] rcdis_In) {
        this.rcdis = rcdis_In;
    }

    public void setUAB(double uA_In[], double uB_In[]) {//vj-2012-12-23-added
        this.uA = uA_In;
        this.uB = uB_In;
    }
    // Phase specific information

    public void setPhaseTag(String phaseTag_In) {
        this.phaseTag = phaseTag_In;
    }

    public void setLc(int[] lc_In) {
        this.lc = lc_In;
    }

    public void setTc(int tc_In) {
        this.tc = tc_In;
    }

    public void setNxc(int nxc_In) {
        this.nxc = nxc_In;
    }

    public void setNc(int nc_In) {
        this.nc = nc_In;
    }

    public void setLcf(int[] lcf_In) {
        this.lcf = lcf_In;
    }

    public void setTcf(int tcf_In) {
        this.tcf = tcf_In;
    }

    public void setNxcf(int nxcf_In) {
        this.nxcf = nxcf_In;
    }

    public void setNcf(int ncf_In) {
        this.ncf = ncf_In;
    }

    public void setM(double[][] m_In) {
        this.m = m_In;
    }
    // Parameters

    public void setR(double R_In) {
        this.R = R_In;
    }

    @Override
    public void setEdis(double edis_In[]) {
        this.ecdis = edis_In;
    }

    @Override
    public void setT(double T_In) {
        this.T = T_In;
    }

    @Override
    public void setX(double xB_In) {
        this.xB = xB_In;
    }

    public void setMU(double mu_In) {
        this.MU = mu_In;
    }

    public void setU(double[] u_In) throws IOException {//vj-2012-12-23-Also set ut 
        u = new double[tcf];//Initialization of u, tcf is already initialized
        for (int i = 0; i < u_In.length; i++) {
            if ((u_In[i] < (-1.0)) || (u_In[i] > 1.0)) {
                throw new ArithmeticException("Value of correlation function is out of range");
            } else if (Math.abs(u_In[i]) < 1.0E-15) {
                u_In[i] = 0.0;
            } else {
                this.u[i] = u_In[i];
            }
        }
        updateUt();
    }

    private void updateUt() {//vj-2012-12-23-convert flattened correlation function list to two dimensional Cluster wise list
        int counter = 0;
        double ut_In[][] = new double[tcdis][];
        for (int itc = 0; itc < (tcdis); itc++) {
            ut_In[itc] = new double[lcf[itc]];
            for (int ilcf = 0; ilcf < lcf[itc]; ilcf++) {
                ut_In[itc][ilcf] = u[counter];
                //System.out.println(counter + ":" + itc + ":" + ilcf + ":" + ut_In[itc][ilcf]);
                counter = counter + 1;
            }
        }
        ut = ut_In;
    }

    // MC Specific parameters
    public void setMcMethod(String mcmethod_In) {
        this.mcMethod = mcmethod_In;
    }

    public void setlatticeSize(int latticeSize_In) {
        this.latticeSize = latticeSize_In;
    }

    public void setNLP(int latticeSites_In) {
        this.NLP = latticeSites_In;
    }

    public void setConfig(int[][][] config_In) {
        this.config = config_In;
    }
    // Getter Methods

    public int getCoordNum() {
        return (coordNum);
    }

    public int getTcdis() {
        return (tcdis);
    }

    public int getNxcdis() {
        return (nxcdis);
    }

    public int getNcdis() {//vj-2012-12-26
        return (ncdis);
    }

    public double[] getMgdis() {
        return (mgdis);
    }

    public int[] getRcdis() {
        return (rcdis);
    }

    public double[] getUA() {
        return (uA);
    }

    public double[] getUB() {
        return (uB);
    }
//Phase specific getter methods

    @Override
    public String getPhaseTag() {
        return (phaseTag);
    }

    public int[] getLc() {
        return lc;
    }

    public int getTc() {
        return tc;
    }

    public int getNxc() {
        return nxc;
    }

    public int getNc() {//vj-2012-12-26
        return nc;
    }

    public int[] getLcf() {
        return lcf;
    }

    public int getTcf() {
        return tcf;
    }

    public int getNxcf() {
        return nxcf;
    }

    public int getNcf() {//vj-2012-12-26
        return ncf;
    }

    public double[][] getM() {
        return m;
    }
    //Parameters

    public double getR() {
        return R;
    }

    public double[] getEcdis() {
        return ecdis;
    }

    public double getT() {
        return T;
    }

    public double getX() {
        return xB;
    }

    public double getMU() {
        return MU;
    }

    public double[] getU() {
        return u;
    }

    public double[][] getUt() {
        return ut;
    }
//MC-Specific getter methods

    public String getMcMethod() {
        return (mcMethod);
    }

    public int getLatticeSize() {
        return latticeSize;
    }

    public int getNLP() {
        return NLP;
    }

    public int[][][] getConfig() {
        return config;
    }

    //Public Methods
    public int getSiteOperator(int[] siteCoordinates) {
        return (config[siteCoordinates[0]][siteCoordinates[1]][siteCoordinates[2]]);
    }

    public void exchange(int[] siteCoord1, int[] siteCoord2) {
        int temp;
        temp = config[siteCoord1[0]][siteCoord1[1]][siteCoord1[2]];
        config[siteCoord1[0]][siteCoord1[1]][siteCoord1[2]] = config[siteCoord2[0]][siteCoord2[1]][siteCoord2[2]];
        config[siteCoord2[0]][siteCoord2[1]][siteCoord2[2]] = temp;
    }

    public void flip(int[] siteCoord1) {
        config[siteCoord1[0]][siteCoord1[1]][siteCoord1[2]] = -config[siteCoord1[0]][siteCoord1[1]][siteCoord1[2]];
    }

    public double calHc(double[][] ut) {
        double Hc = 0;
        for (int itc = 0; itc < (tcdis); itc++) {
            double Hcin = 0;
            for (int ilcf = 0; ilcf < lcf[itc]; ilcf++) {
                Hcin = Hcin + m[itc][ilcf] * (ut[itc][ilcf]);
            }
            Hc = Hc + mgdis[itc] * ecdis[itc] * (Hcin);
        }
        return (Hc);
    }

    public double calHc() throws IOException, ArithmeticException {//2012-03-28(VJ): Added
        double Hc = 0;
        for (int itc = 0; itc < (tcdis); itc++) {
            double Hcin = 0;
            for (int ilcf = 0; ilcf < lcf[itc]; ilcf++) {
                Hcin = Hcin + m[itc][ilcf] * (ut[itc][ilcf]);
            }
            Hc = Hc + mgdis[itc] * ecdis[itc] * (Hcin);
        }
        return (Hc);
    }

    @Override
    public double calHmc() throws IOException, ArithmeticException {//vj-2012-12-23//Enthalpy of mixing
        //double[] mgdis = {4.0, 3.0, 12.0, 6.0, 1.0};//vj-2012-06-23
        double Hc = 0;
        for (int itc = 0; itc < (tcdis - nxcdis); itc++) {
            double Hcin = 0;
            for (int ilcf = 0; ilcf < lcf[itc]; ilcf++) {
                Hcin = Hcin + m[itc][ilcf] * (ut[itc][ilcf]);
            }
            Hc = Hc + mgdis[itc] * ecdis[itc] * (Hcin - (1 - xB) * uA[itc] - (xB) * uB[itc]);//vj-2012-06-23
        }
        return (Hc);
    }

    @Override
    public void printPhaseInfo() throws IOException {
        prnt.writeln("-------------Phase object parameters--------------------");
        prnt.list(phaseTag, "Phase");
        prnt.list(coordNum, "coordNum");
        prnt.list(tcdis, "tcdis");
        prnt.list(nxcdis, "nxcdis");
        prnt.list(ncdis, "ncdis");
        prnt.list(mgdis, "mgdis");
        prnt.list(rcdis, "rcdis");
        prnt.list(uA, "uA");
        prnt.list(uB, "uB");
        prnt.list(lc, "lc");
        prnt.list(tc, "tc");
        prnt.list(nxc, "nxc");
        prnt.list(nc, "nc");
        prnt.list(lcf, "lcf");
        prnt.list(tcf, "tcf");
        prnt.list(nxcf, "nxcf");
        prnt.list(ncf, "ncf");
        prnt.list(m, "m");
        prnt.list(R, "R");
        prnt.list(ecdis, "edis");
        prnt.list(T, "T");
        prnt.list(xB, "X");
        prnt.list(u, "u");
        prnt.list(ut, "ut");
        prnt.list(mcMethod, "MC method");
        prnt.list(latticeSize, "Lattice size");
        prnt.list(NLP, "No of lattice points");
        prnt.writeln("-----------------------------------------------------");
    }
    //Abstract Methods

    public abstract int calNLP(int latticeSizeIn);

    public abstract int[][] coordstore(int latticeSizeIn) throws IOException;

    public abstract int[] siteIndexTositeCoordinate(int size, int siteIndex);

    public abstract int[] firstNeighbourSites(int[][][] config, int size, int i1, int j1, int k1) throws IOException;

    public abstract int[] getfirstNeighbourCoord(int size, int[] siteCoordinates, int nnIndex) throws IOException;

    public abstract int[] secondNeighbourSites(int[][][] config, int size, int i1, int j1, int k1) throws IOException;

    public abstract double calLHc(int[] siteCoord) throws IOException;

    public abstract double[] calU() throws IOException;//calculates correlation functions at a given configuration
    //    public void caleqCF() throws IOException {
//        double[][] corrFuncArray = new double[NDP][tcf];
//        double[] avgCorrFunc = new double[tcf];
//        double[] statError = new double[tcf];
//        runMC(corrFuncArray);//update corrFuncArray
//        calStat(corrFuncArray, avgCorrFunc, statError);//Pass corrFuncArray and update avgCorrFunc and statError
//        prnt.list(avgCorrFunc, "avgCorrFunc");
//        prnt.list(statError, "statError");
//        printStat(avgCorrFunc, statError);
//        isequil = true;
//    }
    //private methods
//    private void runMC(double[][] corrFuncArray) throws IOException {
//        int mc_samplingInterval = 2;
//        double[][] mc_corrFuncArray = new double[NDP][tcf];
//        double startTime = System.currentTimeMillis();
//        System.out.println("Using exchange sites algorithms");
//        for (int counter = 0; counter < (WARMMCSS + MCSS); counter++) {
//            int MCS = 0;// Begining of One MCSS
//            for (int i = 0; i < NLP; i++) {
//                MCS = MCS + exchangeSitesAlgorithm();// Completion of one MCSS 
//            }
////            while (MCS < NLP) {//Counting only performed MCS
////                MCS = MCS + exchangeSitesAlgorithm();// Completion of one MCSS
////            }
//            if (counter < WARMMCSS) {
//                System.out.println("mc_MCSS no.=" + counter);
//            } else if ((counter % mc_samplingInterval == 0)) {//data output step
//                int counter2 = (int) ((counter - WARMMCSS) / mc_samplingInterval);
//                mc_corrFuncArray[counter2] = calU();
//                //prnt.list(mc_corrFuncArray[counter2], " mc_corrFuncArray[counter2]");
//                System.out.println("mc_MCSS no.=" + counter + ", Performed Steps=" + MCS);
//            }//END of if loop for data output
//        }
//        double endTime = System.currentTimeMillis();
//        double runTime = endTime - startTime;
//        for (int i = 0; i < NDP; i++) {
//            System.arraycopy(mc_corrFuncArray[i], 0, corrFuncArray[i], 0, tcf);
//        }
//    }
//    private int exchangeSitesAlgorithm() throws IOException {
//        int performed = 0;
//        //Begining of MCS
//        //STEP 1: Random site selection and its neighbour
//        int random1 = (int) (Math.random() * NLP);//first random Site
//        int[] siteCoord1 = siteIndexTositeCoordinate(latticeSize, random1);//Coordinates of the first random site
//        int site1 = getSiteOperator(siteCoord1);
//        int random2 = (int) (Math.random() * coordNum);
//        int[] siteCoord2 = getfirstNeighbourCoord(latticeSize, siteCoord1, random2);//Coordinates of the neighbour site of first random site
//        int site2 = getSiteOperator(siteCoord2);//random neighbour site of the first random site
//        //System.out.println("Site1 Coordinate:" + site1[0] + "," + site1[1] + "," + site1[2] + "," + config[site1[0]][site1[1]][site1[2]]);
//        //System.out.println("Site2 Coordinate:" + site2[0] + "," + site2[1] + "," + site2[2] + "," + config[site2[0]][site2[1]][site2[2]]);
//        //------STEP 2: CALCULATION OF dE=E(*)-E(i) -----------------------------
//        if (site1 != site2) {
//            double siteEnergy11 = calLHc(siteCoord1);
//            double siteEnergy12 = calLHc(siteCoord2);
//            //mc_lattice.firstNeighbourSites(siteCoord1[0], siteCoord1[1], siteCoord1[2], true);
//            //mc_lattice.firstNeighbourSites(siteCoord2[0], siteCoord2[1], siteCoord2[2], true);
//            double E1 = siteEnergy11 + siteEnergy12;
//            exchange(siteCoord1, siteCoord2);
//            double siteEnergy21 = calLHc(siteCoord1);
//            double siteEnergy22 = calLHc(siteCoord2);
//            //mc_lattice.firstNeighbourSites(siteCoord1[0], siteCoord1[1], siteCoord1[2], true);
//            //mc_lattice.firstNeighbourSites(siteCoord2[0], siteCoord2[1], siteCoord2[2], true);
//            double E2 = siteEnergy21 + siteEnergy22;
//            double dE = E2 - E1;
//            //System.out.println("SE11:" + siteEnergy11 + ", E12:" + siteEnergy12 + ", SE21:" + siteEnergy21 + ", E22:" + siteEnergy22 + ", dE:" + dE);
//            //STEP 3: ACCEPTANCE OF NEW STATE(*)
//            if (dE <= 0) {
//                performed = 1;// Already exchanged with the selected neighbour
//            } else if (Math.exp(-(dE / (R * T))) >= Math.random()) {
//                performed = 1;// Already exchanged with the selected neighbour
//            } else {
//                exchange(siteCoord1, siteCoord2);//not accept exchange
//                performed = 0;
//            }
//        }
//        return (performed);
//    }
//    private void calStat(double[][] m_corrFuncArray, double[] avgCorrFunc, double[] statError) {
//        double m_avgE = 0;
//        double m_avgE2 = 0;
//        //double m_avgE4 = 0;
//        double[] m_avgCorrFunc = new double[tcf];
//        double[] m_avgCorrFunc2 = new double[tcf];
//        double[] m_statError = new double[tcf];
//        double[] m_EArray = new double[NDP];
//        for (int j = 0; j < tcf; j++) {
//            m_avgCorrFunc[j] = 0;
//            m_avgCorrFunc2[j] = 0;
//        }
//        for (int i = 0; i < NDP; i++) {
//            m_EArray[i] = calHc(m_corrFuncArray[i]);
//            m_avgE = m_avgE + m_EArray[i];
//            m_avgE2 = m_avgE2 + m_EArray[i] * m_EArray[i];
//            //m_avgE4 = m_avgE4 + Math.pow(m_corrFuncArray[i][4], 4);
//            for (int j = 0; j < tcf; j++) {
//                m_avgCorrFunc[j] = m_avgCorrFunc[j] + m_corrFuncArray[i][j];
//                m_avgCorrFunc2[j] = m_avgCorrFunc2[j] + m_corrFuncArray[i][j] * m_corrFuncArray[i][j];
//            }
//        }
//        m_avgE = m_avgE / NDP;
//        m_avgE2 = m_avgE2 / NDP;
//        //m_avgE4 = m_avgE4 / NDP;
//        for (int j = 0; j < tcf; j++) {
//            m_avgCorrFunc[j] = m_avgCorrFunc[j] / NDP;
//            m_avgCorrFunc2[j] = m_avgCorrFunc2[j] / NDP;
//        }
//        double m_CV = ((m_avgE2 - m_avgE * m_avgE) * NLP) / (R * T * T);
//        //m_U4 = 1 - (m_avgE4 / (3 * m_avgCorrFunc2[4] * m_avgCorrFunc2[4]));
//        for (int j = 0; j < tcf; j++) {
//            m_statError[j] = Math.sqrt(((m_avgCorrFunc2[j] - (m_avgCorrFunc[j] * m_avgCorrFunc[j])) / (NDP - 1)));
//        }
//        System.arraycopy(m_avgCorrFunc, 0, avgCorrFunc, 0, tcf);
//        System.arraycopy(m_statError, 0, statError, 0, tcf);
//    }
//    public void printStat(double[] avgCorrFunc, double[] statError) {
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        DateFormat tf = new SimpleDateFormat("HH:mm:ss");
//        df.setTimeZone(TimeZone.getTimeZone("IST"));
//        tf.setTimeZone(TimeZone.getTimeZone("IST"));
//        String newtime = df.format(new Date()) + "," + tf.format(new Date());
//        try {
//            try (FileWriter writer1 = new FileWriter("mcResults.csv", true)) {
//                writer1.append(newtime + "," + phaseTag + "," + NLP + "," + WARMMCSS + "," + MCSS + ",");
//                for (int i = 0; i < tcf; i++) {
//                    writer1.append(ecdis[i] + ",");
//                }
//                writer1.append(T + "," + xB + ",");
//                for (int i1 = 0; i1 < tcf; i1++) {
//                    writer1.append(avgCorrFunc[i1] + ",");
//                }
//                for (int i1 = 0; i1 < tcf; i1++) {
//                    writer1.append(statError[i1] + ",");
//                }
//                writer1.append("\n");
//                writer1.flush();
//            }
//        } catch (IOException e) {
//            System.err.println("Unable to write to file");
//            System.exit(-1);
//        }
//    }
}
