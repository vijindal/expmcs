/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phase.mc.FCC;

import io.prnt;
import java.io.IOException;
import phase.mc.PHASEMCBINCE;

/**
 *
 * @author metallurgy
 */
public abstract class FCCMCBINCE extends PHASEMCBINCE {

    private int coordNum = 12;//coordination number of lattice
    private int tcdis = 10; //No of total clusters in disordred phase
    private int nxcdis = 1; //No of clusters realted to point cluters in disordred phase
    private int ncdis = 9;
    private double mdis[] = {6.0, 3.0, 8.0, 12.0, 2.0, 12.0, 3.0, 6.0, 1.0, 1.0};//Multiplicities for each cluster
    private int rcdis[] = {2, 2, 3, 3, 4, 4, 4, 5, 6, 1};//No of sites for each cluster

    public FCCMCBINCE() throws IOException {
        prnt.writeln("FCCMCBINCE constructor method called");
        setCoordNum(coordNum);
        setTcdis(tcdis);
        setNxcdis(nxcdis);
        setNcdis(ncdis);
        setMdis(mdis);
        setRcdis(rcdis);
        setUab(tcdis, rcdis);//Calculate and set uA and uB arrays
        prnt.writeln("FCCMCBINCE constructor method ended");
    }

    //Setter Methods
    private void setUab(int tcdis_In, int[] rcdis_In) {
        double uA[] = new double[tcdis_In];
        double uB[] = new double[tcdis_In];
        for (int itc = 0; itc < tcdis_In; itc++) {
            uA[itc] = Math.pow(-1.0, rcdis_In[itc]);
            uB[itc] = Math.pow(1.0, rcdis_In[itc]);
        }
        setUAB(uA, uB);
    }
    //Getter Methods
    //public methods

    //private methods
    //overridden methods
    @Override
    public int calNLP(int latticeSizeIn) {
        return (((latticeSizeIn * latticeSizeIn * latticeSizeIn) / 2));
    }

    @Override
    public int[][] coordstore(int latticeSizeIn) throws IOException {//vj-2012-12-25-Storing coordinates of sites in px,py,pz for ramdom site selection alogorithm
        prnt.writeln("FCCMCBINCE.coordstore() called");
        int nlp = calNLP(latticeSizeIn);
        int p[][] = new int[nlp][3];
        int l = 0;
        for (int i = 0; i < latticeSizeIn; i = i + 1) {
            for (int j = 0; j < latticeSizeIn; j = j + 1) {
                for (int k = 0; k < latticeSizeIn; k = k + 1) {
                    if ((i + j + k) % 2 == 0) {
                        p[l][0] = i;
                        p[l][1] = j;
                        p[l][2] = k;
                        //System.out.println("siteIndex:" + l + ", siteCoord:" + p[l][0] + "," + p[l][1] + "," + p[l][2]);
                        //prnt.list(siteIndexTositeCoordinate(latticeSizeIn,l),"coord:");
                        l = l + 1;
                    }
                }
            }
        }
        System.out.println("No of coordinates stored=" + l);
        prnt.writeln("FCCMCBINCE.coordstore() ended");
        return (p);
    }

    @Override
    public int[] siteIndexTositeCoordinate(int size, int siteIndex) {
        int[] siteCoordinate = new int[3];
        int divisor1 = (size * size) / 2;
        int factor1 = siteIndex / divisor1;
        int remainder1 = siteIndex % divisor1;
        int divisor2 = size;
        int factor2 = (2 * remainder1) / divisor2;
        int remainder2 = (2 * remainder1) % divisor2;
        siteCoordinate[0] = factor1;
        siteCoordinate[1] = factor2;
        siteCoordinate[2] = (((factor1 + factor2 + remainder2) % 2 == 0) ? remainder2 : (remainder2 + 1));
        return (siteCoordinate);
    }

    @Override
    public int[] firstNeighbourSites(int[][][] config, int size, int i, int j, int k) throws IOException {
        prnt.writeln("FCCMCBINCE.firstNeighbourSites() method called", 1);
        int[] nnatoms = new int[12];
        int iPlus = (i + 1) % size;
        int jPlus = (j + 1) % size;
        int kPlus = (k + 1) % size;
        int iMinus = (i - 1 + size) % size;
        int jMinus = (j - 1 + size) % size;
        int kMinus = (k - 1 + size) % size;
        nnatoms[0] = config[i][jPlus][kPlus];
        nnatoms[1] = config[i][jPlus][kMinus];
        nnatoms[2] = config[i][jMinus][kPlus];
        nnatoms[3] = config[i][jMinus][kMinus];
        nnatoms[4] = config[iPlus][j][kPlus];
        nnatoms[5] = config[iPlus][j][kMinus];
        nnatoms[6] = config[iMinus][j][kPlus];
        nnatoms[7] = config[iMinus][j][kMinus];
        nnatoms[8] = config[iPlus][jPlus][k];
        nnatoms[9] = config[iPlus][jMinus][k];
        nnatoms[10] = config[iMinus][jPlus][k];
        nnatoms[11] = config[iMinus][jMinus][k];
        prnt.writeln("FCCMCBINCE.firstNeighbourSites() method ended", 1);
        return (nnatoms);
    }

    @Override
    public int[] getfirstNeighbourCoord(int size, int[] siteCoordinates, int nnIndex) {
        int[] nncoord = new int[3];
        int i = siteCoordinates[0];
        int j = siteCoordinates[1];
        int k = siteCoordinates[2];
        int iPlus = (siteCoordinates[0] + 1) % size;
        int jPlus = (siteCoordinates[1] + 1) % size;
        int kPlus = (siteCoordinates[2] + 1) % size;
        int iMinus = (siteCoordinates[0] - 1 + size) % size;
        int jMinus = (siteCoordinates[1] - 1 + size) % size;
        int kMinus = (siteCoordinates[2] - 1 + size) % size;
        switch (nnIndex) {
            case 0:
                nncoord[0] = i;
                nncoord[1] = jPlus;
                nncoord[2] = kPlus;
                break;
            case 1:
                nncoord[0] = i;
                nncoord[1] = jPlus;
                nncoord[2] = kMinus;
                break;
            case 2:
                nncoord[0] = i;
                nncoord[1] = jMinus;
                nncoord[2] = kPlus;
                break;
            case 3:
                nncoord[0] = i;
                nncoord[1] = jMinus;
                nncoord[2] = kMinus;
                break;
            case 4:
                nncoord[0] = iPlus;
                nncoord[1] = j;
                nncoord[2] = kPlus;
                break;
            case 5:
                nncoord[0] = iPlus;
                nncoord[1] = j;
                nncoord[2] = kMinus;
                break;
            case 6:
                nncoord[0] = iMinus;
                nncoord[1] = j;
                nncoord[2] = kPlus;
                break;
            case 7:
                nncoord[0] = iMinus;
                nncoord[1] = j;
                nncoord[2] = kMinus;
                break;
            case 8:
                nncoord[0] = iPlus;
                nncoord[1] = jPlus;
                nncoord[2] = k;
                break;
            case 9:
                nncoord[0] = iPlus;
                nncoord[1] = jMinus;
                nncoord[2] = k;
                break;
            case 10:
                nncoord[0] = iMinus;
                nncoord[1] = jPlus;
                nncoord[2] = k;
                break;
            case 11:
                nncoord[0] = iMinus;
                nncoord[1] = jMinus;
                nncoord[2] = k;
                break;
            default:
                System.out.println("Error");
        }
        return (nncoord);
    }

    @Override
    public int[] secondNeighbourSites(int[][][] config, int size, int i1, int j1, int k1) throws IOException {
        prnt.writeln("FCCMCBINCE.secondNeighbourSites() method called", 1);
        int[] natoms = new int[6];
        natoms[0] = config[(i1 + 2) % size][j1][k1];
        natoms[1] = config[(i1 - 2 + size) % size][j1][k1];
        natoms[2] = config[i1][(j1 + 2) % size][k1];
        natoms[3] = config[i1][(j1 - 2 + size) % size][k1];
        natoms[4] = config[i1][j1][(k1 + 2) % size];
        natoms[5] = config[i1][j1][(k1 - 2 + size) % size];
        prnt.writeln("FCCMCBINCE.secondNeighbourSites() method called", 1);
        return (natoms);
    }

    @Override
    public double calLHc(int[] siteCoord) throws IOException {//vj-2012-12-25-to be modified for other CFs
        int tcf = getTcf();
        int[][][] config = getConfig();
        int size = getLatticeSize();
        double[] edis = getEcdis();
        int[] SiteCF = new int[tcf];// site correlation function for BCC disordred phase for Cluster expansion
        int site = config[siteCoord[0]][siteCoord[1]][siteCoord[2]];
        int[] nnsites = firstNeighbourSites(config, size, siteCoord[0], siteCoord[1], siteCoord[2]);
        int[] nsites = secondNeighbourSites(config, size, siteCoord[0], siteCoord[1], siteCoord[2]);
        SiteCF[0] = site * (nnsites[0] + nnsites[1] + nnsites[2] + nnsites[3] + nnsites[4] + nnsites[5] + nnsites[6] + nnsites[7] + nnsites[8] + nnsites[9] + nnsites[10] + nnsites[11]);
        SiteCF[1] = site * (nsites[0] + nsites[1] + nsites[2] + nsites[3] + nsites[4] + nsites[5]);
        double SiteEnthalpy = (edis[0] * SiteCF[0] + edis[1] * SiteCF[1]);
        return SiteEnthalpy;
    }
//    private double[] correlationFunctions(int lattice[][][]) {
//        int no_images[] = new int[65];
//        int total_images[] = new int[65];	//Array to strore instances of occurance and total no of crystallographic equivalent cluster
//        double[] culFunc = new double[65];
//
//        total_images[0] = latticeSites / 4; // no of alfa point clusters
//        total_images[1] = latticeSites / 4; // no of beta point clusters
//        total_images[2] = latticeSites / 4; // no of gama point clusters
//        total_images[3] = latticeSites / 4; // no of delta point clusters
//
//        total_images[4] = latticeSites; // no of alfa-gama pair clusters
//        total_images[5] = latticeSites; // no of alfa-delta pair clusters
//        total_images[6] = latticeSites; // no of beta-gama pair clusters
//        total_images[7] = latticeSites; // no of beta-delta pair clusters
//        total_images[8] = latticeSites; // no of alfa-beta pair clusters
//        total_images[9] = latticeSites; // no of gama-delta pair clusters
//
//        total_images[10] = (3 * latticeSites) / 4; // no of alfa-beta-gama triangle clusters
//        total_images[11] = (3 * latticeSites) / 4;  // no of alfa-beta-delta triangle clusters
//        total_images[12] = (3 * latticeSites) / 4;  // no of alfa-gama-delta triangle clusters
//        total_images[13] = (3 * latticeSites) / 4;  // no of beta-gama-delta triangle clusters
//
//        total_images[31] = (latticeSites); //
//        total_images[32] = (latticeSites); //
//        total_images[33] = (latticeSites); //
//        total_images[34] = (latticeSites); //
//        total_images[35] = (latticeSites); //
//        total_images[36] = (latticeSites); //
//        total_images[37] = (latticeSites); //
//        total_images[38] = (latticeSites); //
//        total_images[39] = (latticeSites); //
//        total_images[40] = (latticeSites); //
//        total_images[41] = (latticeSites); //
//        total_images[42] = (latticeSites); //
//
//        total_images[43] = (latticeSites) / 2; //
//        total_images[44] = (latticeSites) / 2; //
//        total_images[45] = (latticeSites) / 2; //
//        total_images[46] = (latticeSites) / 2; //
//        total_images[47] = (latticeSites) / 2; //
//        total_images[48] = (latticeSites) / 2; //
//
//        total_images[49] = (latticeSites) / 2; //
//        total_images[50] = (latticeSites) / 2; //
//        total_images[51] = (latticeSites) / 2; //
//        total_images[52] = (latticeSites) / 2; //
//        total_images[53] = (latticeSites) / 2; //
//        total_images[54] = (latticeSites) / 2; //
//        total_images[55] = (latticeSites) / 2; //
//        total_images[56] = (latticeSites) / 2; //
//        total_images[57] = (latticeSites) / 2; //
//        total_images[58] = (latticeSites) / 2; //
//        total_images[59] = (latticeSites) / 2; //
//        total_images[60] = (latticeSites) / 2; //
//
//        total_images[61] = (latticeSites) / 4; //
//        total_images[62] = (latticeSites) / 4; //
//        total_images[63] = (latticeSites) / 4; //
//        total_images[64] = (latticeSites) / 4; //
//
//        for (int temp = 0; temp < 15; temp++) {
//            no_images[temp] = 0;
//        }
//
//        for (int i = 0; i < latticeSize; i = i + 2) {
//            for (int j = 0; j < latticeSize; j = j + 2) {
//                for (int k = 0; k < latticeSize; k = k + 2) {//Visiting alfa sites
//
//                    int s10 = lattice[i][j][k];
//                    int s11 = lattice[(i + 2) % latticeSize][j][k];
//                    int s12 = lattice[i][(j + 2) % latticeSize][k];
//                    int s13 = lattice[i][j][(k + 2) % latticeSize];
//
//                    int s20 = lattice[(i + 1) % latticeSize][(j + 1) % latticeSize][(k)];
//                    int s21 = lattice[(i - 1 + latticeSize) % latticeSize][(j + 1) % latticeSize][(k)];
//                    int s22 = lattice[(i + 1) % latticeSize][(j - 1 + latticeSize) % latticeSize][(k)];
//                    int s23 = lattice[(i + 1) % latticeSize][(j + 1) % latticeSize][(k - 2 + latticeSize) % latticeSize];
//                    int s24 = lattice[(i - 1 + latticeSize) % latticeSize][(j - 1 + latticeSize) % latticeSize][(k)];
//
//                    int s30 = lattice[(i)][(j + 1) % latticeSize][(k + 1) % latticeSize];
//                    int s31 = lattice[(i + 2) % latticeSize][(j + 1) % latticeSize][(k + 1) % latticeSize];
//                    int s32 = lattice[(i)][(j - 1 + latticeSize) % latticeSize][(k + 1) % latticeSize];
//                    int s33 = lattice[(i)][(j + 1) % latticeSize][(k - 1 + latticeSize) % latticeSize];
//                    int s34 = lattice[(i)][(j - 1 + latticeSize) % latticeSize][(k - 1 + latticeSize) % latticeSize];
//                    int s35 = lattice[(i + 2) % latticeSize][(j + 1) % latticeSize][(k - 1 + latticeSize) % latticeSize];
//
//                    int s40 = lattice[(i + 1) % latticeSize][(j)][(k + 1) % latticeSize];
//                    int s41 = lattice[(i - 1 + latticeSize) % latticeSize][(j)][(k + 1) % latticeSize];
//                    int s42 = lattice[(i + 1) % latticeSize][(j + 2) % latticeSize][(k + 1) % latticeSize];
//                    int s43 = lattice[(i + 1) % latticeSize][(j)][(k - 1 + latticeSize) % latticeSize];
//                    int s44 = lattice[(i - 1 + latticeSize) % latticeSize][(j)][(k - 1 + latticeSize) % latticeSize];
//                    int s45 = lattice[(i + 1) % latticeSize][(j + 2) % latticeSize][(k - 1 + latticeSize) % latticeSize];
//                    int s46 = lattice[(i - 1 + latticeSize) % latticeSize][(j + 2) % latticeSize][(k + 1) % latticeSize];
//
//                    no_images[0] = no_images[0] + s10;
//                    no_images[1] = no_images[1] + s20;
//                    no_images[2] = no_images[2] + s30;
//                    no_images[3] = no_images[3] + s40;
//
//                    no_images[4] = no_images[4] + s10 * (s20 + s21 + s22 + s24);
//                    no_images[5] = no_images[5] + s10 * (s30 + s32 + s33 + s34);
//                    no_images[6] = no_images[6] + s10 * (s40 + s41 + s43 + s44);
//                    no_images[7] = no_images[7] + s20 * (s30 + s31 + s33 + s35);
//                    no_images[8] = no_images[8] + s20 * (s40 + s42 + s43 + s45);
//                    no_images[9] = no_images[9] + s30 * (s40 + s41 + s42 + s46);
//
//                    no_images[10] = no_images[10] + s10 * (s11 + s12 + s13);
//                    no_images[11] = no_images[11] + s20 * (s21 + s22 + s23);
//                    no_images[12] = no_images[12] + s30 * (s31 + s32 + s33);
//                    no_images[13] = no_images[13] + s40 * (s41 + s42 + s43);
//
//                    no_images[14] = no_images[14] + (s20 + s21) * (s10 + s12) * (s30 + s33);
//                    no_images[15] = no_images[15] + (s30 + s31) * (s40 + s42) * (s20 + s23);
//                    no_images[16] = no_images[16] + (s40 + s41) * (s30 + s32) * (s10 + s13);
//                    no_images[17] = no_images[17] + (s10 + s11) * (s20 + s22) * (s40 + s43);
//
//                    no_images[18] = no_images[18] + (s10 * s12) * (s20 + s21) + (s10 * s11) * (s20 + s22);
//                    no_images[19] = no_images[19] + (s10 * s12) * (s30 + s33) + (s30 + s32) * (s10 * s13);
//                    no_images[20] = no_images[20] + (s10 * s13) * (s40 + s41) + (s10 * s11) * (s40 + s43);
//                    no_images[21] = no_images[21] + (s20 * s21) * (s10 + s12) + (s20 * s22) * (s10 + s11);
//                    no_images[22] = no_images[22] + (s20 * s21) * (s30 + s33) + (s20 * s23) * (s30 + s31);
//                    no_images[23] = no_images[23] + (s20 * s23) * (s40 + s42) + (s20 * s22) * (s40 + s43);
//                    no_images[24] = no_images[24] + (s30 * s33) * (s10 + s12) + (s30 * s32) * (s10 + s13);
//                    no_images[25] = no_images[25] + (s20 + s21) * (s30 * s33) + (s20 + s23) * (s30 * s31);
//                    no_images[26] = no_images[26] + (s30 * s31) * (s40 + s42) + (s40 + s41) * (s30 * s32);
//                    no_images[27] = no_images[27] + (s10 + s13) * (s40 * s41) + (s10 + s11) * (s40 * s43);
//                    no_images[28] = no_images[28] + (s20 + s23) * (s40 * s42) + (s20 + s22) * (s40 * s43);
//                    no_images[29] = no_images[29] + (s30 + s31) * (s40 * s42) + (s40 * s41) * (s30 + s32);
//
//                    no_images[30] = no_images[30] + (s10 * s20 * s30 * s40);//imcomplite
//
//                    no_images[31] = no_images[31] + (s10 * s12) * (s20 + s21) * (s30 + s33);
//                    no_images[32] = no_images[32] + (s10 * s11) * (s20 + s22) * (s40 + s43);
//                    no_images[33] = no_images[33] + (s10 * s13) * (s30 + s32) * (s40 + s41);
//                    no_images[34] = no_images[34] + (s10 + s12) * (s20 * s21) * (s30 + s33);
//                    no_images[35] = no_images[35] + (s10 + s11) * (s20 * s22) * (s40 + s43);
//                    no_images[36] = no_images[36] + (s20 * s23) * (s30 + s31) * (s40 + s42);
//                    no_images[37] = no_images[37] + (s10 + s12) * (s20 + s21) * (s30 * s33);
//                    no_images[38] = no_images[38] + (s10 + s13) * (s30 * s32) * (s40 + s41);
//                    no_images[39] = no_images[39] + (s20 + s23) * (s30 * s31) * (s40 + s42);
//                    no_images[40] = no_images[40] + (s10 + s11) * (s20 + s22) * (s40 * s43);
//                    no_images[41] = no_images[41] + (s10 + s13) * (s30 + s32) * (s40 * s41);
//                    no_images[42] = no_images[42] + (s20 + s23) * (s30 + s31) * (s40 * s42);
//
//                    no_images[43] = no_images[43] + ((s20 * s21) * (s10 * s12) + (s10 * s11) * (s20 * s22));
//                    no_images[44] = no_images[44] + ((s10 * s12) * (s30 * s33) + (s30 * s32) * (s10 * s13));
//                    no_images[45] = no_images[45] + ((s40 * s41) * (s10 * s13) + (s10 * s11) * (s40 * s43));
//                    no_images[46] = no_images[46] + ((s20 * s21) * (s30 * s33) + (s30 * s31) * (s20 * s23));
//                    no_images[47] = no_images[47] + ((s40 * s42) * (s20 * s23) + (s20 * s22) * (s40 * s43));
//                    no_images[48] = no_images[48] + ((s30 * s31) * (s40 * s42) + (s40 * s41) * (s30 * s32));
//
//                    no_images[49] = no_images[49] + (s20 + s21) * (s10 * s12) * (s30 * s33);
//                    no_images[50] = no_images[50] + (s20 * s21) * (s10 + s12) * (s30 * s33);
//                    no_images[51] = no_images[51] + (s20 * s21) * (s10 * s12) * (s30 + s33);
//                    no_images[52] = no_images[52] + (s30 + s31) * (s40 * s42) * (s20 * s23);
//                    no_images[53] = no_images[53] + (s30 * s31) * (s40 + s42) * (s20 * s23);
//                    no_images[54] = no_images[54] + (s30 * s31) * (s40 * s42) * (s20 + s23);
//                    no_images[55] = no_images[55] + (s40 + s41) * (s30 * s32) * (s10 * s13);
//                    no_images[56] = no_images[56] + (s40 * s41) * (s30 + s32) * (s10 * s13);
//                    no_images[57] = no_images[57] + (s40 * s41) * (s30 * s32) * (s10 + s13);
//                    no_images[58] = no_images[58] + (s10 + s11) * (s20 * s22) * (s40 * s43);
//                    no_images[59] = no_images[59] + (s10 * s11) * (s20 + s22) * (s40 * s43);
//                    no_images[60] = no_images[60] + (s10 * s11) * (s20 * s22) * (s40 + s43);
//
//
//                    no_images[61] = no_images[61] + (s20 * s21) * (s10 * s12) * (s30 * s33);
//                    no_images[62] = no_images[62] + (s30 * s31) * (s40 * s42) * (s20 * s23);
//                    no_images[63] = no_images[63] + (s40 * s41) * (s30 * s32) * (s10 * s13);
//                    no_images[64] = no_images[64] + (s10 * s11) * (s20 * s22) * (s40 * s43);
//
//
//                }
//            }
//        }
//        for (int i = 0; i < 14; i++) {
//            culFunc[i] = (1.0 * no_images[i]) / (1.0 * total_images[i]);
//            System.out.println("total_images[" + i + "]=" + total_images[i] + ", no_images[" + i + "]=" + no_images[i]);
//            System.out.println("culfunc[" + i + "]=" + culFunc[i]);
//        }
//        for (int i = 31; i < 65; i++) {
//            culFunc[i] = (1.0 * no_images[i]) / (1.0 * total_images[i]);
//            System.out.println("total_images[" + i + "]=" + total_images[i] + ", no_images[" + i + "]=" + no_images[i]);
//            System.out.println("culfunc[" + i + "]=" + culFunc[i]);
//        }
//        return (culFunc);
//    }
//    private double[] correlationFunctionsL12() {
//        int no_images[] = new int[23];
//        int total_images[] = new int[23];	//Array to strore instances of occurance and total no of crystallographic equivalent cluster
//        double[] culFunc = new double[23];
//
//        total_images[1] = (3 * latticeSites); //a-a
//        total_images[2] = (3 * latticeSites); //a-b
//
//        total_images[3] = (9 * latticeSites) / 4; // a-a
//        total_images[4] = (3 * latticeSites) / 4; // b-b
//
//        total_images[5] = (2 * latticeSites); //a-a-a
//        total_images[6] = (6 * latticeSites); //a-b-b
//
//        total_images[7] = (6 * latticeSites); //a-a-a
//        total_images[8] = (3 * latticeSites); //a-a-b
//        total_images[9] = (3 * latticeSites); //a-b-b
//
//        total_images[10] = (2 * latticeSites); //a-a-a-b
//
//        total_images[11] = (3 * latticeSites); //a-a-a-a
//        total_images[12] = (6 * latticeSites); //a-a-a-b
//        total_images[13] = (3 * latticeSites); //a-a-b-b
//
//        total_images[14] = (3 * latticeSites) / 2; //a-a-a-a
//        total_images[15] = (3 * latticeSites) / 2; //a-a-b-b
//
//        total_images[16] = (3 * latticeSites) / 2; //a-a-a-a-a
//        total_images[17] = (3 * latticeSites) / 2; //a-a-a-a-b
//        total_images[18] = (3 * latticeSites); //a-a-a-b-b
//
//        total_images[19] = (1 * latticeSites) / 4; //a-a-a-a-a-a
//        total_images[20] = (3 * latticeSites) / 4; //a-a-a-a-b-b
//
//        total_images[21] = (9 * latticeSites) / 4; //a
//        total_images[22] = (3 * latticeSites) / 4; //b
//
//        for (int temp = 0; temp < 23; temp++) {
//            no_images[temp] = 0;
//        }
//
//        for (int i = 0; i < latticeSize; i = i + 2) {
//            for (int j = 0; j < latticeSize; j = j + 2) {
//                for (int k = 0; k < latticeSize; k = k + 2) {//Visiting alfa sites
//
//                    int[] s = new int[13];
//
//                    s[11] = lattice[i][j][k];
//                    s[9] = lattice[(i + 2) % latticeSize][(j + 2) % latticeSize][k];
//                    s[6] = lattice[(i + 2) % latticeSize][(j + 2) % latticeSize][(k + 2) % latticeSize];
//
//                    s[1] = lattice[(i + 1) % latticeSize][(j)][(k + 1) % latticeSize];
//                    s[4] = lattice[(i)][(j + 1) % latticeSize][(k + 1) % latticeSize];
//                    s[5] = lattice[(i + 1) % latticeSize][(j + 1) % latticeSize][(k)];
//                    s[3] = lattice[(i + 2) % latticeSize][(j + 1) % latticeSize][(k + 1) % latticeSize];
//                    s[2] = lattice[(i + 1) % latticeSize][(j + 1) % latticeSize][(k + 2) % latticeSize];
//                    s[8] = lattice[(i + 1) % latticeSize][(j + 2) % latticeSize][(k + 1) % latticeSize];
//                    s[7] = lattice[(i + 3) % latticeSize][(j + 2) % latticeSize][(k + 1) % latticeSize];
//                    s[10] = lattice[(i + 2) % latticeSize][(j + 3) % latticeSize][(k + 1) % latticeSize];
//                    s[12] = lattice[(i + 3) % latticeSize][(j + 3) % latticeSize][(k + 2) % latticeSize];
//
//
//                    no_images[1] = no_images[1]
//                            + (s[1] * s[4] + s[4] * s[5] + s[5] * s[1])
//                            + (s[2] * s[3] + s[3] * s[8] + s[8] * s[2])
//                            + (s[7] * s[10] + s[10] * s[12] + s[12] * s[7])
//                            + (s[3] * s[5] + s[5] * s[8] + s[8] * s[3]);//a-a
//                    no_images[2] = no_images[2]
//                            + (s[1] + s[4] + s[5]) * s[11]
//                            + (s[2] + s[3] + s[8]) * s[6]
//                            + (s[7] + s[10] + s[12]) * s[6]
//                            + (s[3] + s[5] + s[8]) * s[9];//b-b
//
//                    no_images[3] = no_images[3] + (s[3] * s[4]) + (s[1] * s[8]) + (s[2] * s[5])
//                            + 3 * (s[7] * s[8]) + 3 * (s[3] * s[10]);//a-a
//                    no_images[4] = no_images[4] + 3 * (s[6] * s[9]);//b-b
//
//                    no_images[5] = no_images[5]
//                            + 2 * (s[1] * s[4] * s[5]
//                            + s[2] * s[3] * s[8]
//                            + s[7] * s[10] * s[12]
//                            + s[3] * s[5] * s[8]);//a-a-a
//                    no_images[6] = no_images[6]
//                            + 2 * ((s[1] * s[4] + s[4] * s[5] + s[5] * s[1]) * s[11]
//                            + (s[2] * s[3] + s[3] * s[8] + s[8] * s[2]) * s[6]
//                            + (s[7] * s[10] + s[10] * s[12] + s[12] * s[7]) * s[6]
//                            + (s[3] * s[5] + s[5] * s[8] + s[8] * s[3]) * s[9]);//a-b-b
//
//                    no_images[7] = no_images[7]
//                            + (s[3] * s[4]) * ((s[1] + s[8]) + (s[2] + s[5]))
//                            + (s[1] * s[8]) * ((s[3] + s[4]) + (s[2] + s[5]))
//                            + (s[2] * s[5]) * ((s[3] + s[4]) + (s[1] + s[8]))
//                            + 3 * (s[7] * s[8]) * ((s[3] + s[10]))
//                            + 3 * (s[3] * s[10]) * ((s[7] + s[8]));//a-a-a
//                    no_images[8] = no_images[8]
//                            + 3 * (s[7] * s[8]) * ((s[6] + s[9]))
//                            + 3 * (s[3] * s[10]) * ((s[6] + s[9]));//a-a-b
//                    no_images[9] = no_images[9]
//                            + 3 * (s[6] * s[9]) * ((s[7] + s[8]) + (s[3] + s[10]));//a-b-b
//
//                    no_images[10] = no_images[10]
//                            + 2 * s[1] * s[4] * s[5] * s[11]
//                            + 2 * s[2] * s[3] * s[8] * s[6]
//                            + 2 * s[7] * s[10] * s[12] * s[6]
//                            + 2 * s[3] * s[5] * s[8] * s[9];//a-a-a-b
//
//                    no_images[11] = no_images[11]
//                            + (s[3] * s[4]) * (s[1] + s[8]) * (s[2] + s[5])
//                            + (s[3] + s[4]) * (s[1] * s[8]) * (s[2] + s[5])
//                            + (s[3] + s[4]) * (s[1] + s[8]) * (s[2] * s[5]); //a-a-a-a
//                    no_images[12] = no_images[12]
//                            + 3 * (s[7] * s[8]) * (s[3] + s[10]) * (s[6] + s[9])
//                            + 3 * (s[7] + s[8]) * (s[3] * s[10]) * (s[6] + s[9]);//a-a-a-b
//                    no_images[13] = no_images[13]
//                            + 3 * (s[7] + s[8]) * (s[3] + s[10]) * (s[6] * s[9]);//a-a-b-b
//
//                    no_images[14] = no_images[14]
//                            + (s[3] * s[4]) * (s[1] * s[8])
//                            + (s[3] * s[4]) * (s[2] * s[5])
//                            + (s[1] * s[8]) * (s[2] * s[5])
//                            + 3 * (s[7] * s[8]) * (s[3] * s[10]);//a-a-a-a
//                    no_images[15] = no_images[15]
//                            + 3 * (s[7] * s[8]) * (s[6] * s[9])
//                            + 3 * (s[3] * s[10]) * (s[6] * s[9]);//a-a-b-b
//
//                    no_images[16] = no_images[16]
//                            + (s[3] * s[4]) * (s[1] * s[8]) * (s[2] + s[5])
//                            + (s[3] * s[4]) * (s[1] + s[8]) * (s[2] * s[5])
//                            + (s[3] + s[4]) * (s[1] * s[8]) * (s[2] * s[5]);//a-a-a-a-a
//                    no_images[17] = no_images[17]
//                            + 3 * (s[7] * s[8]) * (s[3] * s[10]) * (s[6] + s[9]);//a-a-a-a-b
//                    no_images[18] = no_images[18]
//                            + 3 * (s[7] * s[8]) * (s[3] + s[10]) * (s[6] * s[9])
//                            + 3 * (s[7] + s[8]) * (s[3] * s[10]) * (s[6] * s[9]);//a-a-a-b-b
//
//                    no_images[19] = no_images[19] + (s[3] * s[4]) * (s[1] * s[8]) * (s[2] * s[5]);//a-a-a-a-a-a
//                    no_images[20] = no_images[20] + 3 * (s[7] * s[8]) * (s[3] * s[10]) * (s[6] * s[9]);//a-a-a-a-b-b
//
//                    no_images[21] = no_images[21] + ((s[3] + s[4]) + (s[1] + s[8]) + (s[2] + s[5]) + (s[7] + s[10]) + s[12]);//a
//                    no_images[22] = no_images[22] + (s[6] + s[9] + s[11]);//b
//
//                }
//            }
//        }
//        for (int i = 1; i < 23; i++) {
//            culFunc[i] = (1.0 * no_images[i]) / (1.0 * total_images[i]);
//            //System.out.println("total_images[" + i + "]=" + total_images[i] + ", no_images[" + i + "]=" + no_images[i]);
//            //System.out.print("u[" + i + "]->" + culFunc[i]+",");
//        }
//        return (culFunc);
//    }
//overriden methods
}
