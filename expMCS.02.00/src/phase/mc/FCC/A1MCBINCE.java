/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phase.mc.FCC;

import io.prnt;
import java.io.IOException;

/**
 *
 * @author metallurgy
 */
public class A1MCBINCE extends FCCMCBINCE {
    //phase specific parameters

    private String phaseTag_local = "A1MCBINCE";
    private int lc_local[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private int tc_local = 10;
    private int nxc_local = 1;
    private int nc_local = 9;
    private int lcf_local[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private int tcf_local = 10;
    private int nxcf_local = 1;
    private int ncf_local = 9;
    private int rcf_local[] = {2, 2, 3, 3, 4, 4, 4, 5, 6, 1};
    private double m_local[][] = {{1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}};
    private String mcMethod_local = "exchange";

    public A1MCBINCE(double[] eci, double T, double xB, int latticeSize, int latticeType) throws IOException {
        prnt.writeln("A1MCBINCE constructor method called");
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
        prnt.writeln("A1MCBINCE constructor method ended");
        prnt.writeln("-------------Phase object created--------------------");
    }
//Getter Methods
//Setter  Methods
//public methods   
//private methods

    private int[][][] genConfig(double X, int latticeSizeIn, int latticeType) throws IOException {
        prnt.writeln("  PHASEMCBINCE.genConfig() called");
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
            System.out.println("    X=" + X + ", filled B Atoms=" + bs);
        }
        if (latticeType == 2) {
            for (int i = 0; i < nlp; i++) {
                //System.out.println(coordArray[i][0] + "," + coordArray[i][1] + "," + coordArray[i][2]);
                iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = -1;
                if (((coordArray[i][2] % 2 == 1)) && (bs < no_B)) {
                    iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
                    bs = bs + 1;
                }
            }
//            while (bs < no_B) {
//                iconfig[coordArray[nlp - 1 - bs][0]][coordArray[nlp - 1 - bs][1]][coordArray[nlp - 1 - bs][2]] = 1;
//                bs = bs + 1;
//            }
            System.out.println("    X=" + X + ", filled B Atoms=" + bs);
        }
        prnt.writeln("  PHASEMCBINCE.genConfig() ended");
        return (iconfig);
    }

    private void setInitU() throws IOException {//Phase specific method
        double[] u_In = calU();
        setU(u_In);
    }
//abstract methods
//Overriden methods

    @Override
    public double[] calU() throws IOException {//vj-2012-12-26-to be replaced
        prnt.writeln("A1MCBINCE.calU() method called", 1);
        int[][][] config = getConfig();
        int size = getLatticeSize();
        int nlp = calNLP(size);
        int tcf = getTcf();
        double[] u = new double[tcf];
        int nprod[] = new int[tcf];//for storing products of site operators for each correlation function
        for (int temp = 0; temp < tcf; temp++) {
            nprod[temp] = 0;
        }
        int n[] = new int[8];//list of site operators in the orbit

        for (int i = 0; i < size; i = i + 1) {
            for (int j = 0; j < size; j = j + 1) {
                for (int k = 0; k < size; k = k + 1) {
                    if ((i + j + k) % 2 == 0) {//Visiting all FCC sites
                        n[0] = config[i][j][k];
                        n[1] = config[(i + 1) % size][(j + 1) % size][k];
                        n[2] = config[(i + 1) % size][j][(k + 1) % size];
                        n[3] = config[i][(j + 1) % size][(k + 1) % size];
                        n[4] = config[(i + 1) % size][(j + 2) % size][(k + 1) % size];
                        n[5] = config[(i + 2) % size][(j + 1) % size][(k + 1) % size];
                        n[6] = config[(i + 1) % size][(j + 1) % size][(k + 2) % size];
                        //n[7] = config[(i + 2) % size][(j + 2) % size][(k + 2) % size];
                        nprod[0] = nprod[0] + (n[0] + n[1]) * (n[2] + n[3]) + (n[0] * n[1]) + (n[2] * n[3]);//6
                        nprod[1] = nprod[1] + n[1] * n[6] + n[2] * n[4] + n[3] * n[5];//3
                        nprod[2] = nprod[2] + n[0] * n[1] * (n[2] + n[3]) + n[2] * n[3] * (n[0] + n[6]) + n[1] * n[2] * (n[3] + n[5]) + n[4] * (n[5] * n[6] + n[1] * n[3]);//8
                        nprod[3] = nprod[3] + n[1] * n[6] * (n[2] + n[3] + n[4] + n[5]) + n[2] * n[4] * (n[1] + n[3] + n[5] + n[6]) + n[3] * n[5] * (n[1] + n[2] + n[4] + n[6]);//12
                        nprod[4] = nprod[4] + n[1] * n[2] * n[3] * n[0] + n[5] * n[6] * n[7] * n[4];//2
                        nprod[5] = nprod[5] + n[1] * n[6] * (n[2] * n[3] + n[3] * n[4] + n[4] * n[5] + n[5] * n[2]) + n[2] * n[4] * (n[1] * n[3] + n[3] * n[5] + n[5] * n[6] + n[6] * n[1]) + n[3] * n[5] * (n[1] * n[2] + n[2] * n[4] + n[4] * n[6] + n[6] * n[1]);//12
                        nprod[6] = nprod[6] + (n[1] * n[6]) * (n[2] * n[4]) + (n[1] * n[6]) * (n[3] * n[5]) + (n[2] * n[4]) * (n[3] * n[5]);//3
                        nprod[7] = nprod[7] + (n[1] * n[6]) * (n[2] * n[4]) * (n[3] + n[5]) + (n[1] * n[6]) * (n[3] * n[5]) * (n[2] + n[4]) + (n[2] * n[4]) * (n[3] * n[5]) * (n[1] + n[6]);//6
                        nprod[8] = nprod[8] + (n[1] * n[6]) * (n[2] * n[4]) * (n[3] * n[5]);//1
                        nprod[9] = nprod[9] + n[0];//1
                    }
                }
            }
        }
        for (int i = 0; i < tcf; i++) {
            u[i] = (1.0 * nprod[i]) / (1.0 * getMgdis()[i] * nlp);
            //System.out.println("nprod[" + i + "]=" + nprod[i]);
            //System.out.println("u[" + i + "]=" + u[i]);
        }
        prnt.writeln("A1MCBINCE.calU() method ended", 1);
        return (u);
    }
}
