/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mcSampler;

import io.prnt;
import java.io.IOException;
import phase.mc.PHASEMCBINCE;

/**
 *
 * @author metallurgy
 */
public class mcSampler {
    //
    //Highest symmetry phase information

    private int coordNum_local;//coordination number of lattice
    private double mdis[];//Multiplicities for each cluster
    // Phase specific information
    private PHASEMCBINCE phasemcbince_local;
    private int nc[];//List of clusters in case of broken symemtry (Ordered phases)per highest symmetry (disordred phase) cluster*
    private int lcf[];//No of correlation functions for each disordered cluster
    private int tcf;//No of total correlation functions; Sum of lcf
    private int nxcf;//No of total correlation functions realted to point cluters
    private double m[][];//Normalised multiplicities in case of broken symmetry;
    // Parameters
    private double R_local;
    private double edis_local[];//Eci for each correlation function
    private double T_local;//  Temperature
    private double X_local;//  Composition of one component(i.e. B)
    private double MU_local;//  Average Chemical potential
    private double u[]; //  Correlation functions (flattened)
    private double ut[][];//  Correlation function
    // MC Specific parameters
    private String mcMethod;
    private int latticeSize_local;
    private int mc_NLP;
    //Sampler Specific paramters
    private int mc_WARMMCSS = 1000;// No of MC Steps for warm up
    private int mc_MCSS = 4000;// No of MC Steps per site
    private int mc_NDP;// no of data points
    private int mc_samplingInterval = 2;
    private double startTime, endTime, runTime;// Time to start and finish experiment
    private double[][] mc_u_array;

    public mcSampler(int MCSS, int EQMCSS, PHASEMCBINCE phasemcbince) throws IOException {
        prnt.writeln("mcSampler constructor method called");
        //Highest symmetry phase information
        this.coordNum_local = phasemcbince.getCoordNum();
        // Phase specific information
        this.tcf = phasemcbince.getTcf();
        // Parameters
        this.R_local = phasemcbince.getR();
        this.edis_local = phasemcbince.getEcdis();
        this.T_local = phasemcbince.getT();
        this.X_local = phasemcbince.getX();
        // MC Specific parameters
        this.mcMethod = phasemcbince.getMcMethod();
        this.latticeSize_local = phasemcbince.getLatticeSize();//lattice Size
        this.mc_NLP = phasemcbince.getNLP();
        this.mc_WARMMCSS = EQMCSS;
        this.mc_MCSS = MCSS;
        this.mc_NDP = (int) (MCSS / mc_samplingInterval);
        this.phasemcbince_local = phasemcbince;
        //Sampler Specific paramters
        this.mc_u_array = new double[mc_NDP][tcf];
        prnt.writeln("mcSampler constructor method ended");

    }

    public mcSampler(PHASEMCBINCE phasemcbince) throws IOException {
        prnt.writeln("mcSampler constructor method called");
        //Highest symmetry phase information
        this.coordNum_local = phasemcbince.getCoordNum();
        // Phase specific information
        this.tcf = phasemcbince.getTcf();
        // Parameters
        this.R_local = phasemcbince.getR();
        this.edis_local = phasemcbince.getEcdis();
        this.T_local = phasemcbince.getT();
        this.X_local = phasemcbince.getX();
        // MC Specific parameters
        this.mcMethod = phasemcbince.getMcMethod();
        this.latticeSize_local = phasemcbince.getLatticeSize();//lattice Size
        this.mc_NLP = phasemcbince.getNLP();
        this.mc_NDP = (int) (mc_MCSS / mc_samplingInterval);
        this.phasemcbince_local = phasemcbince;
        //Sampler Specific paramters
        this.mc_u_array = new double[mc_NDP][tcf];
        prnt.writeln("mcSampler constructor method ended");
    }
    // Setter Methods
    // Getter Methods

    public double[] getECI() {
        return edis_local;
    }

    public double getT() {
        return T_local;
    }

    public double getXB() {
        return X_local;
    }

    public double getSize() {
        return latticeSize_local;
    }

    public double getSites() {
        return mc_NLP;
    }

    public mcData runMC() throws IOException {
        prnt.writeln("mcSampler.runMC() method called");
        mcData mcdata = new mcData(mc_NDP, phasemcbince_local);
        startTime = System.currentTimeMillis();
        if (mcMethod.equals("exchange")) {
            //System.out.println("exchange method");
            if (isNormalLogging()) {
                System.out.println("exchange method");
            }
            for (int counter = 0; counter < (mc_WARMMCSS); counter++) {//Equilibrium MCSS
                int MCS = 0;// Begining of One mc_MCSS
                for (int i = 0; i < mc_NLP; i++) {
                    MCS = MCS + MCSTEP_exchange();// Completion of one mc_MCSS }
                }
//                while (MCS < mc_NLP) {//Counting only performed MCS
//                    MCS = MCS + MCSTEP_exchange();// Completion of one mc_MCSS
//                }
                // System.out.println("Warm MCSS no.=" + counter + ", Performed Steps=" + MCS);
                if (isVerboseLogging()) {
                    System.out.println("Warm MCSS no.=" + counter + ", Performed Steps=" + MCS);
                }
            }
            for (int counter = 0; counter < (mc_MCSS); counter++) {
                int MCS = 0;// Begining of One mc_MCSS
                for (int i = 0; i < mc_NLP; i++) {
                    MCS = MCS + MCSTEP_exchange();// Completion of one mc_MCSS }
                }
//                while (MCS < mc_NLP) {//Counting only performed MCS
//                    MCS = MCS + MCSTEP_exchange();// Completion of one mc_MCSS
//                }
                if ((counter % mc_samplingInterval == 0)) {//data output step
                    int counter2 = (int) ((counter) / mc_samplingInterval);
                    mc_u_array[counter2] = phasemcbince_local.calU();
                    // System.out.println("MCSS no.=" + counter + ", Performed Steps=" + MCS);
                    if (isNormalLogging()) {
                        System.out.println("MCSS no.=" + counter + ", Performed Steps=" + MCS);
                    }
                    //prnt.list(mc_u_array[counter2], "u");
                }//END of if loop for data output
            }
        }

        if (mcMethod.equals("flip")) {
            // System.out.println("flip method");
            if (isNormalLogging()) {
                System.out.println("flip method");
            }
            for (int counter = 0; counter < (mc_WARMMCSS + mc_MCSS); counter++) {
                int MCS = 0;// Begining of One mc_MCSS
                for (int i = 0; i < mc_NLP; i++) {
                    MCS = MCS + MCSTEP_flip();// Completion of one mc_MCSS
                }
                if (counter < mc_WARMMCSS) {
                    // System.out.println("mc_MCSS no.=" + counter);
                    if (isVerboseLogging()) {
                        System.out.println("mc_MCSS no.=" + counter);
                    }
                } else if ((counter % mc_samplingInterval == 0)) {//data output step
                    int counter2 = (int) ((counter - mc_WARMMCSS) / mc_samplingInterval);
                    mc_u_array[counter2] = phasemcbince_local.calU();
                    // System.out.println("mc_MCSS no.=" + counter + ", Performed Steps=" + MCS);
                    if (isNormalLogging()) {
                        System.out.println("mc_MCSS no.=" + counter + ", Performed Steps=" + MCS);
                    }
                }//END of if loop for data output
            }
        }

        endTime = System.currentTimeMillis();
        runTime = endTime - startTime;
        mcdata.setMCSS(mc_MCSS);
        mcdata.setEQMCSS(mc_WARMMCSS);
        mcdata.setRunTime(runTime);
        mcdata.setCorrFuncArray(mc_u_array);
        prnt.writeln("mcSampler.runMC() method ended");
        return (mcdata);
    }

    int MCSTEP_exchange() throws IOException {
        int random1;//random Site 1
        int random2;//random neighbour site of the first random site
        int[] siteCoord1 = new int[3];//Coordinates of the first random site
        int[] siteCoord2 = new int[3];//Coordinates of the neighbour site of first random site
        double dE;
        int performed = 0;

        //Begining of MCS
        //STEP 1: Random site selection and its neighbour
        random1 = (int) (Math.random() * mc_NLP);
        siteCoord1 = phasemcbince_local.siteIndexTositeCoordinate(latticeSize_local, random1);
        int site1 = phasemcbince_local.getSiteOperator(siteCoord1);
        random2 = (int) (Math.random() * coordNum_local);
        siteCoord2 = phasemcbince_local.getfirstNeighbourCoord(latticeSize_local, siteCoord1, random2);
        int site2 = phasemcbince_local.getSiteOperator(siteCoord2);
        //System.out.println("Site1 Coordinate:" + siteCoord1[0] + "," + siteCoord1[1] + "," + siteCoord1[2] + "," + site1);
        //System.out.println("Site2 Coordinate:" + siteCoord2[0] + "," + siteCoord2[1] + "," + siteCoord2[2] + "," + site2);

        //------STEP 2: CALCULATION OF dE=E(*)-E(i) -----------------------------
        if (site1 != site2) {
            double siteEnergy11 = phasemcbince_local.calLHc(siteCoord1);
            double siteEnergy12 = phasemcbince_local.calLHc(siteCoord2);
            //mc_lattice.firstNeighbourSites(siteCoord1[0], siteCoord1[1], siteCoord1[2], true);
            //mc_lattice.firstNeighbourSites(siteCoord2[0], siteCoord2[1], siteCoord2[2], true);
            double E1 = siteEnergy11 + siteEnergy12;
            phasemcbince_local.exchange(siteCoord1, siteCoord2);
            double siteEnergy21 = phasemcbince_local.calLHc(siteCoord1);
            double siteEnergy22 = phasemcbince_local.calLHc(siteCoord2);
            //mc_lattice.firstNeighbourSites(siteCoord1[0], siteCoord1[1], siteCoord1[2], true);
            //mc_lattice.firstNeighbourSites(siteCoord2[0], siteCoord2[1], siteCoord2[2], true);
            double E2 = siteEnergy21 + siteEnergy22;
            dE = E2 - E1;
            //System.out.println("SE11:" + siteEnergy11 + ", E12:" + siteEnergy12 + ", SE21:" + siteEnergy21 + ", E22:" + siteEnergy22 + ", dE:" + dE);
            //STEP 3: ACCEPTANCE OF NEW STATE(*)
            if (dE <= 0) {
                performed = 1;// Already exchanged with the selected neighbour
                //System.out.println("Accepted with dE:" + dE);
            } else if (Math.exp(-(dE / (R_local * T_local))) >= Math.random()) {
                performed = 1;// Already exchanged with the selected neighbour
            } else {
                phasemcbince_local.exchange(siteCoord1, siteCoord2);//not accept exchange
                performed = 0;
            }
        }
        //printf("check");
        return (performed);
    }

    int MCSTEP_flip() throws IOException {
        int random1;//random Site 1
        int[] siteCoord1 = new int[3];//Coordinates of the first random site
        double dE;
        int performed = 0;
        //Begining of MCS
        //STEP 1: Random site selection
        random1 = (int) (Math.random() * mc_NLP);
        siteCoord1 = phasemcbince_local.siteIndexTositeCoordinate(phasemcbince_local.getLatticeSize(), random1);
        //int site1 = phasemcbince_local.getSiteOperator(siteCoord1);
        //------STEP 2: CALCULATION OF dE=E(*)-E(i) -----------------------------

        double siteEnergy11 = phasemcbince_local.calLHc(siteCoord1);
        //mc_lattice.firstNeighbourSites(siteCoord1[0], siteCoord1[1], siteCoord1[2], true);
        double E1 = siteEnergy11;
        phasemcbince_local.flip(siteCoord1);
        double siteEnergy21 = phasemcbince_local.calLHc(siteCoord1);
        //mc_lattice.firstNeighbourSites(siteCoord1[0], siteCoord1[1], siteCoord1[2], true);
        double E2 = siteEnergy21;
        dE = E2 - E1;
        //System.out.println("SE11:" + siteEnergy11 + ", E12:" + siteEnergy12 + ", SE21:" + siteEnergy21 + ", E22:" + siteEnergy22 + ", dE:" + dE);
        //STEP 3: ACCEPTANCE OF NEW STATE(*)
        if (dE <= 0) {
            performed = 1;// Already exchanged with the selected neighbour
        } else if (Math.exp(-(dE / (R_local * T_local))) >= Math.random()) {
            performed = 1;// Already exchanged with the selected neighbour
        } else {
            phasemcbince_local.flip(siteCoord1);//not accept exchange
            performed = 0;
        }

        //printf("check");
        return (performed);
    }

    private boolean isNormalLogging() {
        return prnt.getLogLevel() >= prnt.LOG_NORMAL;
    }

    private boolean isVerboseLogging() {
        return prnt.getLogLevel() >= prnt.LOG_VERBOSE;
    }

    public void printSamplerInfo() {
        if (!isNormalLogging()) {
            return;
        }
        System.out.println("-------------mcSampler Parameters--------------------");
        System.out.println("           warm mcss: " + mc_WARMMCSS);
        System.out.println("                mcss: " + mc_MCSS);
        System.out.println("   No of data points: " + mc_NDP);
        System.out.println("   Sampling Interval: " + mc_samplingInterval);
        System.out.println("-----------------------------------------------------");
    }
}
