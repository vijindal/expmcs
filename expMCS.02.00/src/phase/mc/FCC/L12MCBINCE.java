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
public class L12MCBINCE extends FCCMCBINCE {
    //phase specific parameters

    private String phaseTag_local = "L12MCBINCE";
    private int lc_local[] = {2, 2, 2, 3, 1, 3, 2, 3, 2, 2};
    private int tc_local = 22;
    private int nxc_local = 1;
    private int nc_local = 21;
    private int lcf_local[] = {2, 2, 2, 3, 1, 3, 2, 3, 2, 2};
    private int tcf_local = 22;
    private int nxcf_local = 1;
    private int ncf_local = 21;
    private int rcf_local[] = {2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 6, 6, 1, 1};
    private int rcfaLocal[] = {2, 1, 2, 0, 3, 2, 3, 2, 1, 3, 4, 3, 2, 4, 2, 5, 4, 3, 6, 4, 1, 0};//No of site operators of alfa type
    private int rcfbLocal[] = {0, 1, 0, 2, 0, 1, 0, 1, 2, 1, 0, 1, 2, 0, 2, 0, 1, 2, 0, 2, 0, 1};//No of site operators of beta type
    private double m_local[][] = {{0.5, 0.5}, {0.75, 0.25}, {0.25, 0.75}, {0.5, 0.25, 0.25}, {1.}, {0.25, 0.5, 0.25}, {0.5, 0.5}, {0.25, 0.25, 0.5}, {0.25, 0.75}, {0.75, 0.25}};
    private String mcMethod_local = "exchange";

    public L12MCBINCE(double[] eci, double T, double xB, int latticeSize, int latticeType) throws IOException {
        prnt.writeln("L12MCBINCE constructor method called");
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
        prnt.writeln("L12MCBINCE constructor method ended");
        prnt.writeln("-------------Phase object created--------------------");
    }

    private int[][][] genConfig(double X, int latticeSizeIn, int latticeType) throws IOException {//vj-2012-12-23-to be modified
        prnt.writeln("PHASEMCBINCE.genConfig() called");
        int nlp = calNLP(latticeSizeIn);
        int[][][] iconfig = new int[latticeSizeIn][latticeSizeIn][latticeSizeIn];
        int[][] coordArray = coordstore(latticeSizeIn);
        int as = 0;
        int bs = 0;
        int no_B = (int) (X * nlp);//no of B atoms.
        int no_A = nlp - no_B;//no of A atoms
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
                if (((coordArray[i][0] % 2 == 0) && (coordArray[i][1] % 2 == 0) && (coordArray[i][2] % 2 == 0))) {//delta sites
                    if (bs < no_B) {
                        iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
                        bs = bs + 1;
                    } else {
                        iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = -1;
                        as = as + 1;
                    }
                }
            }
            for (int i = 0; i < nlp; i++) {
                if (((coordArray[i][0] % 2 == 0) && (coordArray[i][1] % 2 == 1) && (coordArray[i][2] % 2 == 1))) {//alfa sites
                    if (bs < no_B) {
                        iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
                        bs = bs + 1;
                    } else {
                        iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = -1;
                        as = as + 1;
                    }
                }
            }
            for (int i = 0; i < nlp; i++) {
                if (((coordArray[i][0] % 2 == 1) && (coordArray[i][1] % 2 == 0) && (coordArray[i][2] % 2 == 1))) {//beta sites
                    if (bs < no_B) {
                        iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
                        bs = bs + 1;
                    } else {
                        iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = -1;
                        as = as + 1;
                    }
                }
            }
            for (int i = 0; i < nlp; i++) {
                if (((coordArray[i][0] % 2 == 1) && (coordArray[i][1] % 2 == 1) && (coordArray[i][2] % 2 == 0))) {//gama sites
                    if (bs < no_B) {
                        iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
                        bs = bs + 1;
                    } else {
                        iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = -1;
                        as = as + 1;
                    }
                }
            }
            //System.out.println(coordArray[i][0] + "," + coordArray[i][1] + "," + coordArray[i][2]);
//            iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = -1;
//            if ((i < (no_B / 4)) && (bs < no_B)) {
//                if (((coordArray[i][0] % 2 == 0) && (coordArray[i][1] % 2 == 0) && (coordArray[i][2] % 2 == 0))) {
//                    iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
//                    bs = bs + 1;
//                }
//            }
//            if ((i >= (no_B / 4)) && (i < (no_B / 2)) && (bs < no_B)) {
//                if (((coordArray[i][0] % 2 == 0) && (coordArray[i][1] % 2 == 1) && (coordArray[i][2] % 2 == 1))) {
//                    iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
//                    bs = bs + 1;
//                }
//            }

            System.out.println("X=" + X + ", filled A atoms=" + as + ", filled B Atoms=" + bs);
        }
        prnt.writeln("PHASEMCBINCE.genConfig() ended");
        return (iconfig);
    }

    private void setInitU() throws IOException {//Phase specific method
        double[] u_In = calU();
        setU(u_In);
    }

    @Override
    public double[] calU() throws IOException {//vj-2012-12-26-to be updated
        prnt.writeln("L12MCBINCE.calU() method called", 1);
        int[][][] config = getConfig();
        int size = getLatticeSize();
        int nlp = calNLP(size);
        int tcf = getTcf();
        double[] u = new double[tcf];
        int s[] = new int[8];//site-operators
        double total_images[] = {3.0, 3.0, 2.25, 0.75, 2.0, 6.0, 6.0, 3.0, 3.0, 2.0, 3.0, 6.0, 3.0, 1.5, 1.5, 1.5, 1.5, 3.0, 0.25, 0.75, 0.75, 0.25};
        int[][] sp = new int[10][];//sum of products of site-operators
        sp[0] = new int[6];//I-n pair
        sp[1] = new int[4];//II-n pair
        sp[2] = new int[4];//triangle
        sp[3] = new int[12];//irr-triangle
        sp[4] = new int[1];//tetrahedron
        sp[5] = new int[12];//irr. tetrahedron
        sp[6] = new int[6];//sqaure
        sp[7] = new int[12];//pyramid
        sp[8] = new int[4];//octahedron
        sp[9] = new int[4];//point
        for (int i = 0; i < size; i = i + 1) {
            for (int j = 0; j < size; j = j + 1) {
                for (int k = 0; k < size; k = k + 1) {
                    if (((i + j + k) % 2 == 0)) {
                        s[0] = config[i][j][k];
                        s[4] = config[(i + 2) % size][(j + 2) % size][(k + 2) % size];
                        s[1] = config[i][(j + 1) % size][(k + 1) % size];
                        s[5] = config[(i + 2) % size][(j + 1) % size][(k + 1) % size];
                        s[2] = config[(i + 1) % size][j][(k + 1) % size];
                        s[6] = config[(i + 1) % size][(j + 2) % size][(k + 1) % size];
                        s[3] = config[(i + 1) % size][(j + 1) % size][k];
                        s[7] = config[(i + 1) % size][(j + 1) % size][(k + 2) % size];
                        //prnt.list(s, "s");

                        if ((i % 2 == 0) && (j % 2 == 0) && (k % 2 == 0)) {//Visiting all FCC-delta sites
                            sp[0][3] = sp[0][3] + ((s[1] * s[2]) + (s[5] * s[6]));//ab
                            sp[0][4] = sp[0][4] + ((s[1] * s[3]) + (s[5] * s[7]));//ag
                            sp[0][5] = sp[0][5] + ((s[2] * s[3]) + (s[6] * s[7]));//bg//6

                            sp[1][1] = sp[1][1] + (s[1] * s[5]);//aa
                            sp[1][2] = sp[1][2] + (s[2] * s[6]);//bb
                            sp[1][3] = sp[1][3] + (s[3] * s[7]);//gg//3

                            sp[2][0] = sp[2][0] + ((s[0] * s[1] * s[2]) + (s[4] * s[5] * s[6]));//dab
                            sp[2][1] = sp[2][1] + ((s[0] * s[1] * s[3]) + (s[4] * s[5] * s[7]));//dag
                            sp[2][2] = sp[2][2] + ((s[0] * s[2] * s[3]) + (s[4] * s[6] * s[7]));//dbg
                            sp[2][3] = sp[2][3] + ((s[1] * s[2] * s[3]) + (s[5] * s[6] * s[7]));//abg//8

                            sp[3][4] = sp[3][4] + (s[1] * s[5]) * (s[2] + s[6]);//aab
                            sp[3][5] = sp[3][5] + (s[1] * s[5]) * (s[3] + s[7]);//aag
                            sp[3][7] = sp[3][7] + (s[2] * s[6]) * (s[1] + s[5]);//abb
                            sp[3][8] = sp[3][8] + (s[2] * s[6]) * (s[3] + s[7]);//bbg
                            sp[3][10] = sp[3][10] + (s[3] * s[7]) * (s[1] + s[5]);//agg
                            sp[3][11] = sp[3][11] + (s[3] * s[7]) * (s[2] + s[6]);//bgg//12

                            sp[4][0] = sp[4][0] + ((s[0] * s[1] * s[2] * s[3]) + (s[4] * s[5] * s[6] * s[7]));//2

                            sp[5][0] = sp[5][0] + ((s[1] * s[5]) * (s[2] + s[6]) * (s[3] + s[7]));//aabg
                            sp[5][1] = sp[5][1] + ((s[1] + s[5]) * (s[2] * s[6]) * (s[3] + s[7]));//abbg
                            sp[5][2] = sp[5][2] + ((s[1] + s[5]) * (s[2] + s[6]) * (s[3] * s[7]));//abgg//12

                            sp[6][5] = sp[6][5] + ((s[2] * s[6]) * (s[3] * s[7]));//bbgg
                            sp[6][4] = sp[6][4] + ((s[1] * s[5]) * (s[3] * s[7]));//aagg
                            sp[6][3] = sp[6][3] + ((s[1] * s[5]) * (s[2] * s[6]));//aabb//3

                            sp[7][0] = sp[7][0] + ((s[1] + s[5]) * (s[2] * s[6]) * (s[3] * s[7]));//abbgg
                            sp[7][1] = sp[7][1] + ((s[1] * s[5]) * (s[2] + s[6]) * (s[3] * s[7]));//aabgg
                            sp[7][2] = sp[7][2] + ((s[1] * s[5]) * (s[2] * s[6]) * (s[3] + s[7]));//aabbg//6

                            sp[8][0] = sp[8][0] + (s[3] * s[7]) * (s[2] * s[6]) * (s[1] * s[5]);//aabbgg//1

                            sp[9][0] = sp[9][0] + s[0];//d//1
                        }
                        if ((i % 2 == 0) && (j % 2 == 1) && (k % 2 == 1)) {//Visiting all FCC-alpha sites
                            sp[0][2] = sp[0][2] + ((s[1] * s[2]) + (s[5] * s[6]));//dg
                            sp[0][1] = sp[0][1] + ((s[1] * s[3]) + (s[5] * s[7]));//db
                            sp[0][5] = sp[0][5] + ((s[2] * s[3]) + (s[6] * s[7]));//bg//6

                            sp[1][0] = sp[1][0] + (s[1] * s[5]);//dd
                            sp[1][3] = sp[1][3] + (s[2] * s[6]);//gg
                            sp[1][2] = sp[1][2] + (s[3] * s[7]);//bb//3

                            sp[2][1] = sp[2][1] + ((s[0] * s[1] * s[2]) + (s[4] * s[5] * s[6]));//dag
                            sp[2][0] = sp[2][0] + ((s[0] * s[1] * s[3]) + (s[4] * s[5] * s[7]));//dab
                            sp[2][3] = sp[2][3] + ((s[0] * s[2] * s[3]) + (s[4] * s[6] * s[7]));//abg
                            sp[2][2] = sp[2][2] + ((s[1] * s[2] * s[3]) + (s[5] * s[6] * s[7]));//dbg//8

                            sp[3][2] = sp[3][2] + (s[1] * s[5]) * (s[2] + s[6]);//ddg
                            sp[3][1] = sp[3][1] + (s[1] * s[5]) * (s[3] + s[7]);//ddb
                            sp[3][9] = sp[3][9] + (s[2] * s[6]) * (s[1] + s[5]);//dgg
                            sp[3][11] = sp[3][11] + (s[2] * s[6]) * (s[3] + s[7]);//bgg
                            sp[3][6] = sp[3][6] + (s[3] * s[7]) * (s[1] + s[5]);//dbb
                            sp[3][8] = sp[3][8] + (s[3] * s[7]) * (s[2] + s[6]);//bbg//12

                            sp[4][0] = sp[4][0] + ((s[0] * s[1] * s[2] * s[3]) + (s[4] * s[5] * s[6] * s[7]));//2

                            sp[5][3] = sp[5][3] + ((s[1] * s[5]) * (s[2] + s[6]) * (s[3] + s[7]));//ddgb
                            sp[5][4] = sp[5][4] + ((s[1] + s[5]) * (s[2] * s[6]) * (s[3] + s[7]));//dggb
                            sp[5][5] = sp[5][5] + ((s[1] + s[5]) * (s[2] + s[6]) * (s[3] * s[7]));//dgbb

                            sp[6][5] = sp[6][5] + ((s[2] * s[6]) * (s[3] * s[7]));//ggbb
                            sp[6][1] = sp[6][1] + ((s[1] * s[5]) * (s[3] * s[7]));//ddbb
                            sp[6][2] = sp[6][2] + ((s[1] * s[5]) * (s[2] * s[6]));//ddgg

                            sp[7][3] = sp[7][3] + ((s[1] + s[5]) * (s[2] * s[6]) * (s[3] * s[7]));//dggbb
                            sp[7][4] = sp[7][4] + ((s[1] * s[5]) * (s[2] + s[6]) * (s[3] * s[7]));//ddgbb
                            sp[7][5] = sp[7][5] + ((s[1] * s[5]) * (s[2] * s[6]) * (s[3] + s[7]));//ddggb

                            sp[8][1] = sp[8][1] + (s[3] * s[7]) * (s[2] * s[6]) * (s[1] * s[5]);//ddbbgg//1

                            sp[9][1] = sp[9][1] + s[0];//a//1
                        }
                        if ((i % 2 == 1) && (j % 2 == 0) && (k % 2 == 1)) {//Visiting all FCC-beta sites
                            sp[0][2] = sp[0][2] + ((s[1] * s[2]) + (s[5] * s[6]));//dg
                            sp[0][4] = sp[0][4] + ((s[1] * s[3]) + (s[5] * s[7]));//ag
                            sp[0][0] = sp[0][0] + ((s[2] * s[3]) + (s[6] * s[7]));//da//6

                            sp[1][3] = sp[1][3] + (s[1] * s[5]);//gg
                            sp[1][0] = sp[1][0] + (s[2] * s[6]);//dd
                            sp[1][1] = sp[1][1] + (s[3] * s[7]);//aa//3

                            sp[2][2] = sp[2][2] + ((s[0] * s[1] * s[2]) + (s[4] * s[5] * s[6]));//dbg
                            sp[2][3] = sp[2][3] + ((s[0] * s[1] * s[3]) + (s[4] * s[5] * s[7]));//abg
                            sp[2][0] = sp[2][0] + ((s[0] * s[2] * s[3]) + (s[4] * s[6] * s[7]));//dab
                            sp[2][1] = sp[2][1] + ((s[1] * s[2] * s[3]) + (s[5] * s[6] * s[7]));//dag//8

                            sp[3][9] = sp[3][9] + (s[1] * s[5]) * (s[2] + s[6]);//dgg
                            sp[3][10] = sp[3][10] + (s[1] * s[5]) * (s[3] + s[7]);//agg
                            sp[3][2] = sp[3][2] + (s[2] * s[6]) * (s[1] + s[5]);//ddg
                            sp[3][0] = sp[3][0] + (s[2] * s[6]) * (s[3] + s[7]);//dda
                            sp[3][5] = sp[3][5] + (s[3] * s[7]) * (s[1] + s[5]);//aag
                            sp[3][3] = sp[3][3] + (s[3] * s[7]) * (s[2] + s[6]);//daa//12

                            sp[4][0] = sp[4][0] + ((s[0] * s[1] * s[2] * s[3]) + (s[4] * s[5] * s[6] * s[7]));//2

                            sp[5][6] = sp[5][6] + ((s[1] * s[5]) * (s[2] + s[6]) * (s[3] + s[7]));//ggda
                            sp[5][7] = sp[5][7] + ((s[1] + s[5]) * (s[2] * s[6]) * (s[3] + s[7]));//gdda
                            sp[5][8] = sp[5][8] + ((s[1] + s[5]) * (s[2] + s[6]) * (s[3] * s[7]));//gdaa

                            sp[6][0] = sp[6][0] + ((s[2] * s[6]) * (s[3] * s[7]));//ddaa
                            sp[6][4] = sp[6][4] + ((s[1] * s[5]) * (s[3] * s[7]));//ggaa
                            sp[6][2] = sp[6][2] + ((s[1] * s[5]) * (s[2] * s[6]));//ggdd

                            sp[7][6] = sp[7][6] + ((s[1] + s[5]) * (s[2] * s[6]) * (s[3] * s[7]));//gddaa
                            sp[7][7] = sp[7][7] + ((s[1] * s[5]) * (s[2] + s[6]) * (s[3] * s[7]));//ggdaa
                            sp[7][8] = sp[7][8] + ((s[1] * s[5]) * (s[2] * s[6]) * (s[3] + s[7]));//ggdda

                            sp[8][2] = sp[8][2] + (s[3] * s[7]) * (s[2] * s[6]) * (s[1] * s[5]);//ddaagg//1

                            sp[9][2] = sp[9][2] + s[0];//b//1
                        }
                        if ((i % 2 == 1) && (j % 2 == 1) && (k % 2 == 0)) {//Visiting all FCC-gamma sites
                            sp[0][3] = sp[0][3] + ((s[1] * s[2]) + (s[5] * s[6]));//ab
                            sp[0][1] = sp[0][1] + ((s[1] * s[3]) + (s[5] * s[7]));//db
                            sp[0][0] = sp[0][0] + ((s[2] * s[3]) + (s[6] * s[7]));//da//6

                            sp[1][2] = sp[1][2] + (s[1] * s[5]);//bb
                            sp[1][1] = sp[1][1] + (s[2] * s[6]);//aa
                            sp[1][0] = sp[1][0] + (s[3] * s[7]);//dd//3

                            sp[2][3] = sp[2][3] + ((s[0] * s[1] * s[2]) + (s[4] * s[5] * s[6]));//abg
                            sp[2][2] = sp[2][2] + ((s[0] * s[1] * s[3]) + (s[4] * s[5] * s[7]));//dbg
                            sp[2][1] = sp[2][1] + ((s[0] * s[2] * s[3]) + (s[4] * s[6] * s[7]));//dag
                            sp[2][0] = sp[2][0] + ((s[1] * s[2] * s[3]) + (s[5] * s[6] * s[7]));//dab//8

                            sp[3][7] = sp[3][7] + (s[1] * s[5]) * (s[2] + s[6]);//abb
                            sp[3][6] = sp[3][6] + (s[1] * s[5]) * (s[3] + s[7]);//dbb
                            sp[3][4] = sp[3][4] + (s[2] * s[6]) * (s[1] + s[5]);//aab
                            sp[3][3] = sp[3][3] + (s[2] * s[6]) * (s[3] + s[7]);//daa
                            sp[3][1] = sp[3][1] + (s[3] * s[7]) * (s[1] + s[5]);//ddb
                            sp[3][0] = sp[3][0] + (s[3] * s[7]) * (s[2] + s[6]);//dda//12

                            sp[4][0] = sp[4][0] + ((s[0] * s[1] * s[2] * s[3]) + (s[4] * s[5] * s[6] * s[7]));//dabg//2

                            sp[5][9] = sp[5][9] + ((s[1] * s[5]) * (s[2] + s[6]) * (s[3] + s[7]));//bbad
                            sp[5][10] = sp[5][10] + ((s[1] + s[5]) * (s[2] * s[6]) * (s[3] + s[7]));//baad
                            sp[5][11] = sp[5][11] + ((s[1] + s[5]) * (s[2] + s[6]) * (s[3] * s[7]));//badd

                            sp[6][0] = sp[6][0] + ((s[2] * s[6]) * (s[3] * s[7]));//aadd
                            sp[6][1] = sp[6][1] + ((s[1] * s[5]) * (s[3] * s[7]));//bbdd
                            sp[6][3] = sp[6][3] + ((s[1] * s[5]) * (s[2] * s[6]));//bbaa

                            sp[7][9] = sp[7][9] + ((s[1] + s[5]) * (s[2] * s[6]) * (s[3] * s[7]));//baadd
                            sp[7][10] = sp[7][10] + ((s[1] * s[5]) * (s[2] + s[6]) * (s[3] * s[7]));//bbadd
                            sp[7][11] = sp[7][11] + ((s[1] * s[5]) * (s[2] * s[6]) * (s[3] + s[7]));//bbaad

                            sp[8][3] = sp[8][3] + (s[3] * s[7]) * (s[2] * s[6]) * (s[1] * s[5]);//ddaabb//1

                            sp[9][3] = sp[9][3] + s[0];//g//1
                        }
                    }
                }
            }
        }
        //prnt.list(sp, "sp");
        //L10: delta=beta & alpha=gama
        u[0] = (sp[0][3] + sp[0][4] + sp[0][5]) / (1.0 * total_images[0] * nlp);//aa
        u[1] = (sp[0][0] + sp[0][1] + sp[0][2]) / (1.0 * total_images[1] * nlp);//ab

        u[2] = (sp[1][1] + sp[1][2] + sp[1][3]) / (1.0 * total_images[2] * nlp);//aa
        u[3] = (sp[1][0]) / (1.0 * total_images[3] * nlp);//bb

        u[4] = (sp[2][3]) / (1.0 * total_images[4] * nlp);//aaa
        u[5] = (sp[2][0] + sp[2][1] + sp[2][2]) / (1.0 * total_images[5] * nlp);//aab

        u[6] = (sp[3][4] + sp[3][5] + sp[3][7] + sp[3][8] + sp[3][10] + sp[3][11]) / (1.0 * total_images[6] * nlp);
        u[7] = (sp[3][3] + sp[3][6] + sp[3][9]) / (1.0 * total_images[7] * nlp);
        u[8] = (sp[3][0] + sp[3][1] + sp[3][2]) / (1.0 * total_images[8] * nlp);

        u[9] = (sp[4][0]) / (1.0 * total_images[9] * nlp);

        u[10] = (sp[5][0] + sp[5][1] + sp[5][2]) / (1.0 * total_images[10] * nlp);
        u[11] = (sp[5][4] + sp[5][5] + sp[5][6] + sp[5][8] + sp[5][9] + sp[5][10]) / (1.0 * total_images[11] * nlp);
        u[12] = (sp[5][3] + sp[5][7] + sp[5][11]) / (1.0 * total_images[12] * nlp);


        u[13] = (sp[6][3] + sp[6][4] + sp[6][5]) / (1.0 * total_images[13] * nlp);
        u[14] = (sp[6][0] + sp[6][1] + sp[6][2]) / (1.0 * total_images[14] * nlp);

        u[15] = (sp[7][0] + sp[7][1] + sp[7][2]) / (1.0 * total_images[15] * nlp);
        u[16] = (sp[7][3] + sp[7][7] + sp[7][11]) / (1.0 * total_images[16] * nlp);
        u[17] = (sp[7][4] + sp[7][5] + sp[7][6] + sp[7][8] + sp[7][9] + sp[7][10]) / (1.0 * total_images[17] * nlp);

        u[18] = (sp[8][0]) / (1.0 * total_images[18] * nlp);
        u[19] = (sp[8][1] + sp[8][2] + sp[8][3]) / (1.0 * total_images[19] * nlp);

        u[20] = (sp[9][1] + sp[9][2] + sp[9][3]) / (1.0 * total_images[20] * nlp);
        u[21] = (sp[9][0]) / (1.0 * total_images[21] * nlp);
        double LRO = (u[21] - u[20]) / 2;
        double x = (4 + 3 * u[20] + u[21]) / 8;
        u[20] = LRO;
        u[21] = x;

        prnt.writeln("L10MCBINCE.calU() method ended", 1);
        return (u);
    }
}