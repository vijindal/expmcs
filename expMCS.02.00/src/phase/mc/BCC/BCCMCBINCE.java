/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phase.mc.BCC;

import io.prnt;
import java.io.IOException;
import phase.mc.PHASEMCBINCE;

/**
 *
 * @author metallurgy
 */
public abstract class BCCMCBINCE extends PHASEMCBINCE {
    //Highest symmetry phase information

    private int coordNum = 8;//coordination number of lattice
    private int tcdis = 5; //No of total clusters in disordred phase
    private int nxcdis = 1; //No of clusters realted to point cluters in disordred phase
    private int ncdis = 4;
    private double mdis[] = {4, 3, 12, 6, 1};//Multiplicities for each cluster
    private int rcdis[] = {2, 2, 3, 4, 1};//No of sites for each cluster

    public BCCMCBINCE() throws IOException {
        prnt.writeln("  BCCMCBINCE constructor method called");
        setCoordNum(coordNum);
        setTcdis(tcdis);
        setNxcdis(nxcdis);
        setNcdis(ncdis);
        setMdis(mdis);
        setRcdis(rcdis);
        setUab(tcdis, rcdis);//Calculate and set uA and uB arrays
        prnt.writeln("BCCMCBINCE constructor method ended");
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
    //private methods
    //overriden methods

    @Override
    public int calNLP(int latticeSizeIn) {
        return ((latticeSizeIn * latticeSizeIn * latticeSizeIn) / 4);
    }

    @Override
    public int[][] coordstore(int latticeSizeIn) throws IOException {
        prnt.writeln("BCCMCBINCE.coordstore() called");
        int nlp = calNLP(latticeSizeIn);// No of lattice points
        int[][] p = new int[nlp][3];
        // Storing coordinates of sites in px,py,pz for random site selection alogorithm
        int l1 = 0;
        for (int i1 = 0; i1 < latticeSizeIn; i1 = i1 + 1) {
            for (int j1 = 0; j1 < latticeSizeIn; j1 = j1 + 1) {
                for (int k1 = 0; k1 < latticeSizeIn; k1 = k1 + 1) {
                    if (((i1 % 2 == 0) && (j1 % 2 == 0) && (k1 % 2 == 0)) || ((i1 % 2 == 1) && (j1 % 2 == 1) && (k1 % 2 == 1))) {
                        p[l1][0] = i1;
                        p[l1][1] = j1;
                        p[l1][2] = k1;
                        l1 = l1 + 1;
                        //System.out.println(l1+": ("+i1+","+j1+","+k1+")");
                    }
                }
            } //Coordinates stored
        }
        System.out.println("No of Coordinate stored=" + l1);
        prnt.writeln("BCCMCBINCE.coordstore() ended");
        return (p);
    }

    @Override
    public int[] siteIndexTositeCoordinate(int size, int siteIndex) {
        int[] siteCoordinate = new int[3];
        int divisor1 = (size * size) / 4;
        int factor1 = siteIndex / divisor1;
        int remainder1 = siteIndex % divisor1;
        int divisor2 = size / 2;
        int factor2 = remainder1 / divisor2;
        int remainder2 = remainder1 % divisor2;
        siteCoordinate[0] = factor1;
        siteCoordinate[1] = (2 * factor2) + (factor1 % 2);
        siteCoordinate[2] = (2 * remainder2) + (factor1 % 2);
        return (siteCoordinate);
    }

    @Override
    public int[] firstNeighbourSites(int[][][] config, int size, int i1, int j1, int k1) throws IOException {//nearest neighbor atoms
        prnt.writeln("BCCMCBINCE.firstNeighbourSites() method called", 1);
        int[] nnatoms = new int[8];
        int iPlus = (i1 + 1) % size;
        int jPlus = (j1 + 1) % size;
        int kPlus = (k1 + 1) % size;
        int iMinus = (i1 - 1 + size) % size;
        int jMinus = (j1 - 1 + size) % size;
        int kMinus = (k1 - 1 + size) % size;
        nnatoms[0] = config[iPlus][jPlus][kPlus];
        nnatoms[1] = config[iPlus][jPlus][kMinus];
        nnatoms[2] = config[iPlus][jMinus][kPlus];
        nnatoms[3] = config[iPlus][jMinus][kMinus];
        nnatoms[4] = config[iMinus][jPlus][kPlus];
        nnatoms[5] = config[iMinus][jPlus][kMinus];
        nnatoms[6] = config[iMinus][jMinus][kPlus];
        nnatoms[7] = config[iMinus][jMinus][kMinus];
        prnt.writeln("BCCMCBINCE.firstNeighbourSites() method ended", 1);
        return (nnatoms);
    }

    @Override
    public int[] getfirstNeighbourCoord(int size, int[] siteCoordinates, int nnIndex) {//nearest neighbor coord
        int[] nncoord = new int[3];
        int iPlus = (siteCoordinates[0] + 1) % size;
        int jPlus = (siteCoordinates[1] + 1) % size;
        int kPlus = (siteCoordinates[2] + 1) % size;
        int iMinus = (siteCoordinates[0] - 1 + size) % size;
        int jMinus = (siteCoordinates[1] - 1 + size) % size;
        int kMinus = (siteCoordinates[2] - 1 + size) % size;
        switch (nnIndex) {
            case 0:
                nncoord[0] = iPlus;
                nncoord[1] = jPlus;
                nncoord[2] = kPlus;
                break;
            case 1:
                nncoord[0] = iPlus;
                nncoord[1] = jPlus;
                nncoord[2] = kMinus;
                break;
            case 2:
                nncoord[0] = iPlus;
                nncoord[1] = jMinus;
                nncoord[2] = kPlus;
                break;
            case 3:
                nncoord[0] = iPlus;
                nncoord[1] = jMinus;
                nncoord[2] = kMinus;
                break;
            case 4:
                nncoord[0] = iMinus;
                nncoord[1] = jPlus;
                nncoord[2] = kPlus;
                break;
            case 5:
                nncoord[0] = iMinus;
                nncoord[1] = jPlus;
                nncoord[2] = kMinus;
                break;
            case 6:
                nncoord[0] = iMinus;
                nncoord[1] = jMinus;
                nncoord[2] = kPlus;
                break;
            case 7:
                nncoord[0] = iMinus;
                nncoord[1] = jMinus;
                nncoord[2] = kMinus;
                break;
            default:
                System.out.println("Error");
        }
        return (nncoord);
    }

    @Override
    public int[] secondNeighbourSites(int[][][] config, int size, int i1, int j1, int k1) throws IOException {
        prnt.writeln("BCCMCBINCE.secondNeighbourSites() method called", 1);
        int[] natoms = new int[6];
        natoms[0] = config[(i1 + 2) % size][j1][k1];
        natoms[1] = config[(i1 - 2 + size) % size][j1][k1];
        natoms[2] = config[i1][(j1 + 2) % size][k1];
        natoms[3] = config[i1][(j1 - 2 + size) % size][k1];
        natoms[4] = config[i1][j1][(k1 + 2) % size];
        natoms[5] = config[i1][j1][(k1 - 2 + size) % size];
        prnt.writeln("BCCMCBINCE.secondNeighbourSites() method called", 1);
        return (natoms);
    }

    @Override
    public double calLHc(int[] siteCoord) throws IOException {
        // This method calculates energy using cluster expansion related to site(i,j,k) or part of the total energy that will be altered if
        // this site is altered
        int[][][] config = getConfig();
        int size = getLatticeSize();
        double[] edis = getEcdis();
        int[] SiteCF = new int[4];// site correlation function for BCC disordred phase for Cluster expansion
        int site = config[siteCoord[0]][siteCoord[1]][siteCoord[2]];
        int[] nnsites = firstNeighbourSites(config, size, siteCoord[0], siteCoord[1], siteCoord[2]);
        int[] nsites = secondNeighbourSites(config, size, siteCoord[0], siteCoord[1], siteCoord[2]);
        SiteCF[0] = site * (nnsites[0] + nnsites[1] + nnsites[2] + nnsites[3] + nnsites[4] + nnsites[5] + nnsites[6] + nnsites[7]);
        SiteCF[1] = site * (nsites[0] + nsites[1] + nsites[2] + nsites[3] + nsites[4] + nsites[5]);
        SiteCF[2] = site * ((nnsites[0] * nnsites[1] + nnsites[1] * nnsites[3] + nnsites[3] * nnsites[2] + nnsites[2] * nnsites[0])
                + (nnsites[4] * nnsites[5] + nnsites[5] * nnsites[7] + nnsites[7] * nnsites[6] + nnsites[6] * nnsites[4])
                + (nnsites[1] * nnsites[5] + nnsites[4] * nnsites[0])
                + (nnsites[3] * nnsites[7] + nnsites[6] * nnsites[2]))
                + site * (nsites[0] * (nnsites[0] + nnsites[1] + nnsites[3] + nnsites[2])
                + nsites[1] * (nnsites[4] + nnsites[5] + nnsites[7] + nnsites[6])
                + nsites[2] * (nnsites[0] + nnsites[1] + nnsites[5] + nnsites[4])
                + nsites[3] * (nnsites[2] + nnsites[3] + nnsites[7] + nnsites[6])
                + nsites[4] * (nnsites[0] + nnsites[2] + nnsites[6] + nnsites[4])
                + nsites[5] * (nnsites[1] + nnsites[3] + nnsites[7] + nnsites[5]));
        SiteCF[3] = site * (nsites[0] * (nnsites[0] * nnsites[1] + nnsites[1] * nnsites[3] + nnsites[3] * nnsites[2] + nnsites[2] * nnsites[0])
                + nsites[1] * (nnsites[4] * nnsites[5] + nnsites[5] * nnsites[7] + nnsites[7] * nnsites[6] + nnsites[6] * nnsites[4])
                + nsites[2] * (nnsites[0] * nnsites[1] + nnsites[1] * nnsites[5] + nnsites[5] * nnsites[4] + nnsites[4] * nnsites[0])
                + nsites[3] * (nnsites[2] * nnsites[3] + nnsites[3] * nnsites[7] + nnsites[7] * nnsites[6] + nnsites[6] * nnsites[2])
                + nsites[4] * (nnsites[0] * nnsites[2] + nnsites[2] * nnsites[6] + nnsites[6] * nnsites[4] + nnsites[4] * nnsites[0])
                + nsites[5] * (nnsites[1] * nnsites[3] + nnsites[3] * nnsites[7] + nnsites[7] * nnsites[5] + nnsites[5] * nnsites[1]));
        //SiteCF[4] = site; // coordArray CF
        double SiteEnthalpy = (edis[0] * SiteCF[0] + edis[1] * SiteCF[1] + edis[2] * SiteCF[2] + edis[3] * SiteCF[3]);
        return SiteEnthalpy;
    }

//   
}
