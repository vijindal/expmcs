/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phase.mc.BCC;

import io.prnt;
import java.io.IOException;

/**
 *
 * @author metallurgy
 */
public class A2MCBINCE extends BCCMCBINCE {
    //phase specific parameters

    private String phaseTag_local = "A2MCBINCE";
    private int lc_local[] = {1, 1, 1, 1, 1};
    private int tc_local = 5;
    private int nxc_local = 1;
    private int nc_local = 4;
    private int lcf_local[] = {1, 1, 1, 1, 1};
    private int tcf_local = 5;
    private int nxcf_local = 1;
    private int ncf_local = 4;
    private int rcf_local[] = {2, 2, 3, 4, 1};
    private double m_local[][] = {{1}, {1}, {1}, {1}, {1}};
    private String mcMethod_local = "exchange";

    public A2MCBINCE(double[] eci, double T, double xB, int latticeSize, int latticeType) throws IOException {
        //phase specific parameters
        prnt.writeln("  A2MCBINCE constructor method called");
        setPhaseTag(phaseTag_local);
        setLc(lc_local);
        setTc(tc_local);
        setNxc(nxc_local);
        setNc(nc_local);
        setLcf(lcf_local);
        setTcf(tcf_local);
        setNxcf(nxcf_local);
        setNcf(ncf_local);
        setM(m_local);
        //macroscopic parameters
        setR(((Math.abs(eci[0]) == 1) ? 1 : 8.3144)); //  Universal gas constant
        setEdis(eci);
        setT(T);
        setX(xB);
        //MC specfic parameters
        setMcMethod(mcMethod_local);
        setlatticeSize(latticeSize);
        setNLP(calNLP(latticeSize));
        setConfig(genConfig(xB, latticeSize, latticeType));
        setInitU();// set u
        prnt.writeln("A2MCBINCE constructor method ended");
        prnt.writeln("-------------Phase object created--------------------");
    }
//Getter Methods
//Setter  Methods
//public methods
//private methods

    private int[][][] genConfig(double X, int latticeSizeIn, int latticeType) throws IOException {
        prnt.writeln("A2MCBINCE.genConfig() called");
        int nlp = calNLP(latticeSizeIn);
        int[][][] iconfig = new int[latticeSizeIn][latticeSizeIn][latticeSizeIn];
        int[][] coordArray = coordstore(latticeSizeIn);
        int as = 0;
        int bs = 0;
        int no_B = (int) (X * nlp);//no of B element.
        if (latticeType == 1) {
            //randomly call bc and corner sites and assign them A or B atoms
            for (int l1 = 1; l1 <= nlp; l1++) {
                int rand = (int) (Math.random() * (nlp - l1));
                if (l1 <= no_B) {
                    iconfig[coordArray[rand][0]][coordArray[rand][1]][coordArray[rand][2]] = +1;
                    bs++;
                } else {
                    iconfig[coordArray[rand][0]][coordArray[rand][1]][coordArray[rand][2]] = -1;
                    as++;
                }
                coordArray[rand][0] = coordArray[nlp - l1][0];
                coordArray[rand][1] = coordArray[nlp - l1][1];
                coordArray[rand][2] = coordArray[nlp - l1][2];
            }
            System.out.println("X=" + X + ", filled B Atoms=" + bs);
        }
        if (latticeType == 2) {
            for (int i = 0; i < nlp; i++) {
                //System.out.println(coordArray[i][0] + "," + coordArray[i][1] + "," + coordArray[i][2]);
                iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = -1;
                if (((coordArray[i][0] % 2 == 1) && (coordArray[i][1] % 2 == 1) && (coordArray[i][2] % 2 == 1)) && (bs < no_B)) {
                    iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
                    bs = bs + 1;
                }
            }
            System.out.println("X=" + X + ", filled B Atoms=" + bs);
        }
        prnt.writeln("A2MCBINCE.genConfig() ended");
        return (iconfig);
    }

    private void setInitU() throws IOException {//Phase specific method
        double[] u_In = calU();
        setU(u_In);
    }

    @Override
    public double[] calU() throws IOException {
        prnt.writeln("A2MCBINCE.calU() method called", 1);
        int[][][] config = getConfig();
        int size = getLatticeSize();
        int nlp = calNLP(size);
        int tcf = getTcf();
        int siteOperators[] = new int[tcf];
        double[] corrFunc = new double[tcf];
        for (int temp = 0; temp < tcf; temp++) {
            siteOperators[temp] = 0;
        }
        for (int i = 0; i < size; i = i + 1) {
            for (int j = 0; j < size; j = j + 1) {
                for (int k = 0; k < size; k = k + 1) {
                    if (((i % 2 == 0) && (j % 2 == 0) && (k % 2 == 0)) || ((i % 2 == 1) && (j % 2 == 1) && (k % 2 == 1))) {//Visiting all BCC sites
                        int site = config[i][j][k];
                        int[] nnsites = firstNeighbourSites(config, size, i, j, k);
                        int[] nsites = secondNeighbourSites(config, size, i, j, k);
                        //prnt.list(nnsites, "nnsites");
                        siteOperators[0] = siteOperators[0] + site * (nnsites[0] + nnsites[1] + nnsites[2] + nnsites[3] + nnsites[4] + nnsites[5] + nnsites[6] + nnsites[7]);
                        siteOperators[1] = siteOperators[1] + site * (nsites[0] + nsites[1] + nsites[2] + nsites[3] + nsites[4] + nsites[5]);
                        siteOperators[2] = siteOperators[2] + site * ((nnsites[0] * nnsites[1] + nnsites[1] * nnsites[3] + nnsites[3] * nnsites[2] + nnsites[2] * nnsites[0])
                                + (nnsites[4] * nnsites[5] + nnsites[5] * nnsites[7] + nnsites[7] * nnsites[6] + nnsites[6] * nnsites[4])
                                + (nnsites[1] * nnsites[5] + nnsites[4] * nnsites[0])
                                + (nnsites[3] * nnsites[7] + nnsites[6] * nnsites[2]))
                                + site * (nsites[0] * (nnsites[0] + nnsites[1] + nnsites[3] + nnsites[2])
                                + nsites[1] * (nnsites[4] + nnsites[5] + nnsites[7] + nnsites[6])
                                + nsites[2] * (nnsites[0] + nnsites[1] + nnsites[5] + nnsites[4])
                                + nsites[3] * (nnsites[2] + nnsites[3] + nnsites[7] + nnsites[6])
                                + nsites[4] * (nnsites[0] + nnsites[2] + nnsites[6] + nnsites[4])
                                + nsites[5] * (nnsites[1] + nnsites[3] + nnsites[7] + nnsites[5]));
                        siteOperators[3] = siteOperators[3] + site * (nsites[0] * (nnsites[0] * nnsites[1] + nnsites[1] * nnsites[3] + nnsites[3] * nnsites[2] + nnsites[2] * nnsites[0])
                                + nsites[1] * (nnsites[4] * nnsites[5] + nnsites[5] * nnsites[7] + nnsites[7] * nnsites[6] + nnsites[6] * nnsites[4])
                                + nsites[2] * (nnsites[0] * nnsites[1] + nnsites[1] * nnsites[5] + nnsites[5] * nnsites[4] + nnsites[4] * nnsites[0])
                                + nsites[3] * (nnsites[2] * nnsites[3] + nnsites[3] * nnsites[7] + nnsites[7] * nnsites[6] + nnsites[6] * nnsites[2])
                                + nsites[4] * (nnsites[0] * nnsites[2] + nnsites[2] * nnsites[6] + nnsites[6] * nnsites[4] + nnsites[4] * nnsites[0])
                                + nsites[5] * (nnsites[1] * nnsites[3] + nnsites[3] * nnsites[7] + nnsites[7] * nnsites[5] + nnsites[5] * nnsites[1]));
                        siteOperators[4] = siteOperators[4] + site;
                    }
                }
            }
        }
        for (int i = 0; i < tcf; i++) {
            corrFunc[i] = (1.0 * siteOperators[i]) / (1.0 * getRcdis()[i] * getMgdis()[i] * nlp);
            //System.out.println("siteOperators[" + i + "]=" + siteOperators[i]);
            //System.out.println("culfunc[" + i + "]=" + corrFunc[i]);
        }
        prnt.writeln("A2MCBINCE.calU() method ended", 1);
        return (corrFunc);
    }
}
