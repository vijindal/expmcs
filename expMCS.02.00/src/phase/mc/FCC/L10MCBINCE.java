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
public class L10MCBINCE extends FCCMCBINCE {
    //phase specific parameters

    private String phaseTag_local = "L10MCBINCE";
    private int lc_local[] = {3, 2, 2, 4, 1, 4, 3, 4, 2, 2};
    private int tc_local = 27;
    private int nxc_local = 1;
    private int nc_local = 26;
    private int lcf_local[] = {3, 2, 2, 4, 1, 4, 3, 4, 2, 2};
    private int tcf_local = 27;
    private int nxcf_local = 1;
    private int ncf_local = 26;
    private int rcf_local[] = {2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 1, 1};
    private int rcfaLocal[] = {1, 2, 0, 2, 0, 2, 1, 3, 2, 1, 0, 2, 3, 2, 1, 2, 4, 0, 2, 1, 2, 4, 3, 2, 4, 1, 0};//No of site operators of alfa type
    private int rcfbLocal[] = {1, 0, 2, 0, 2, 1, 2, 0, 1, 2, 3, 2, 1, 2, 3, 2, 0, 4, 2, 4, 3, 1, 2, 4, 2, 0, 1};//No of site operators of beta type
    private double m_local[][] = {{0.666667, 0.166667, 0.166667},
        {0.5, 0.5},
        {0.5, 0.5},
        {0.166667, 0.333333, 0.333333, 0.166667},
        {1.},
        {0.333333, 0.166667, 0.333333, 0.166667},
        {0.166667, 0.166667, 0.666667},
        {0.166667, 0.333333, 0.166667, 0.333333},
        {0.5, 0.5},
        {0.5, 0.5}};
    private String mcMethod_local = "exchange";

    public L10MCBINCE(double[] eci, double T, double xB, int latticeSize, int latticeType) throws IOException {
        prnt.writeln("L10MCBINCE constructor method called");
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
        prnt.writeln("L10MCBINCE constructor method ended");
        prnt.writeln("-------------Phase object created--------------------");
    }

    private int[][][] genConfig(double X, int latticeSizeIn, int latticeType) throws IOException {
        prnt.writeln("L10MCBINCE.genConfig() called with latticeType:" + latticeType);
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
                iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = -1;
            }//fill entire system with A atoms
            for (int i = 0; i < nlp; i++) {
                //System.out.println(coordArray[i][0] + "," + coordArray[i][1] + "," + coordArray[i][2]);
                if (((coordArray[i][1] % 2 == 0))) {//vj-2013-01-14
                    iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
                    bs = bs + 1;
                }
                if (bs == no_B) {
                    break;
                }
            }//fill beta sites with B ataoms
            for (int i = 0; i < nlp; i++) {
                //System.out.println(coordArray[i][0] + "," + coordArray[i][1] + "," + coordArray[i][2]);
                if (((coordArray[i][1] % 2 == 1))) {//vj-2013-01-14
                    iconfig[coordArray[i][0]][coordArray[i][1]][coordArray[i][2]] = 1;
                    bs = bs + 1;
                }
                if (bs == no_B) {
                    break;
                }
            }//fill alpha sites with B atoms
//            while (bs < no_B) {
//                iconfig[coordArray[nlp - 1 - bs][0]][coordArray[nlp - 1 - bs][1]][coordArray[nlp - 1 - bs][2]] = 1;
//                bs = bs + 1;
//            }
            System.out.println("xB:" + X + ", no of B atoms:" + no_B + ", filled B Atoms=" + bs);
        }
        prnt.writeln("L10MCBINCE.genConfig() ended");
        return (iconfig);
    }

    private void setInitU() throws IOException {//Phase specific method
        double[] u_In = calU();
        setU(u_In);
    }

    @Override
    public double[] calU() throws IOException {//vj-2012-12-26-to be updated
        prnt.writeln("L10MCBINCE.calU() method called", 1);
        int[][][] config = getConfig();
        int size = getLatticeSize();
        int nlp = calNLP(size);
        int tcf = getTcf();
        double[] u = new double[tcf];
        int s[] = new int[8];//site-operators
        double total_images[] = {4, 1, 1, 1.5, 1.5, 4, 4, 2, 4, 4, 2, 2, 4, 2, 4, 2, 0.5, 0.5, 2, 1, 2, 1, 2, 0.5, 0.5, 0.5, 0.5};
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
        u[0] = (sp[0][0] + sp[0][2] + sp[0][3] + sp[0][5]) / (1.0 * total_images[0] * nlp);
        u[1] = (sp[0][4]) / (1.0 * total_images[1] * nlp);
        u[2] = (sp[0][1]) / (1.0 * total_images[2] * nlp);

        u[3] = (sp[1][1] + sp[1][3]) / (1.0 * total_images[3] * nlp);
        u[4] = (sp[1][0] + sp[1][2]) / (1.0 * total_images[4] * nlp);

        u[5] = (sp[2][1] + sp[2][3]) / (1.0 * total_images[5] * nlp);
        u[6] = (sp[2][0] + sp[2][2]) / (1.0 * total_images[6] * nlp);

        u[7] = (sp[3][5] + sp[3][10]) / (1.0 * total_images[7] * nlp);
        u[8] = (sp[3][3] + sp[3][4] + sp[3][9] + sp[3][11]) / (1.0 * total_images[8] * nlp);
        u[9] = (sp[3][0] + sp[3][2] + sp[3][7] + sp[3][8]) / (1.0 * total_images[9] * nlp);
        u[10] = (sp[3][1] + sp[3][6]) / (1.0 * total_images[10] * nlp);

        u[11] = (sp[4][0]) / (1.0 * total_images[11] * nlp);

        u[12] = (sp[5][0] + sp[5][2] + sp[5][6] + sp[5][8]) / (1.0 * total_images[12] * nlp);
        u[13] = (sp[5][4] + sp[5][10]) / (1.0 * total_images[13] * nlp);
        u[14] = (sp[5][3] + sp[5][5] + sp[5][9] + sp[5][11]) / (1.0 * total_images[14] * nlp);
        u[15] = (sp[5][1] + sp[5][7]) / (1.0 * total_images[15] * nlp);

        u[16] = (sp[6][4]) / (1.0 * total_images[16] * nlp);
        u[17] = (sp[6][1]) / (1.0 * total_images[17] * nlp);
        u[18] = (sp[6][0] + sp[6][2] + sp[6][3] + sp[6][5]) / (1.0 * total_images[18] * nlp);

        u[19] = (sp[7][4] + sp[7][10]) / (1.0 * total_images[19] * nlp);
        u[20] = (sp[7][3] + sp[7][5] + sp[7][9] + sp[7][11]) / (1.0 * total_images[20] * nlp);
        u[21] = (sp[7][1] + sp[7][7]) / (1.0 * total_images[21] * nlp);
        u[22] = (sp[7][0] + sp[7][2] + sp[7][6] + sp[7][8]) / (1.0 * total_images[22] * nlp);

        u[23] = (sp[8][1] + sp[8][3]) / (1.0 * total_images[23] * nlp);
        u[24] = (sp[8][0] + sp[8][2]) / (1.0 * total_images[24] * nlp);

        u[25] = (sp[9][1] + sp[9][3]) / (1.0 * total_images[25] * nlp);
        u[26] = (sp[9][0] + sp[9][2]) / (1.0 * total_images[26] * nlp);
        double LRO = (u[26] - u[25]) / 2;
        double x = (2 + u[25] + u[26]) / 4;
        u[25] = LRO;
        u[26] = x;

        prnt.writeln("L10MCBINCE.calU() method ended", 1);
        return (u);
    }
//    double[] MCSS_run() {
//
//        int counter, counter2;
//        int MCS, attempt;
//        int lattice[][][];//Array to store spins/atoms
//        int coord[][];    //Array to store Coordinates
//        double CulFunc_tmp[] = new double[26];//Temperory values of cluster functions
//        double CulFunc_avg[] = new double[26];//Average values of cluster functions
//        double CulFunc_array[][] = new double[DataPoints][26];//Array to strore Cluster functions
//        int no_B = (int) (xB * SITES);//no of B elements.
//
//        coord = coordstore();//setting up coordinate array
//        lattice = initiate_L10(no_B, coord);//Setting up a lattice
//        CulFunc_tmp = calU(lattice);
//        System.out.println(CulFunc_tmp[25]);
//        System.out.println("Starting Simulation.... for xB=" + xB + ",Temp=" + T);
//
//        counter = 0;	//Initiating counter for no of MCS
//        System.out.println("Warming up system....");
//        while (counter < WARMMCSS) {//WARM UP STAGE
//            System.out.println("WARMMCSS no.=" + counter);
//            MCS = 0;// Begining of One MCSS
//            while (MCS < SITES) {
//                MCS = MCS + MCSTEP(SIZE, lattice, coord, J, T, no_B);
//            }// Completion of one MCSS
//            counter = counter + 1;
//        }//Completion of while loop of warming step ....
//        System.out.println("Warming completed....");
//
//        counter = 0; //counter reset for MC Simulations
//        while (counter < MCSS) {//EXPERIMAENT STAGE
//            MCS = 0;// Begining of One MCSS
//            attempt = 0;
//            while (MCS < SITES) {
//                MCS = MCS + MCSTEP(SIZE, lattice, coord, J, T, no_B);// Completion of one MCSS
//                //attempt=attempt+1;
//                //System.out.println("No of attempt:"+attempt+", MCS:"+MCS);
//            }
//            counter = counter + 1;
//            if (counter % 2 == 0) {//data output step
//                MCS = 0;
//                counter2 = (int) (counter / 2);
//                while (MCS < 1) {
//                    MCS = MCS + MCSTEP(SIZE, lattice, coord, J, T, no_B);//One performed MCS
//                }
//                CulFunc_tmp = calU(lattice);
//                System.out.println("Counter:" + counter + " ," + CulFunc_tmp[25] + "," + CulFunc_tmp[0]);
//                //for(int i=0;i<26;i++)CulFunc_array[counter2][i]=CulFunc_tmp[i];
//                //System.out.println("MCSS no.="+counter2+", L.r.o="+CulFunc_array[counter2][25]);
//            }//END of if loop for data output
//        }//Completion while loop of Simulation step
//        //CulFunc_avg=Stats(CulFunc_array);
//        return (CulFunc_avg);
//    }
//
//    int[][][] initiate_A1(int noB, int point[][]) {
//        int matrix[][][] = new int[SIZE][SIZE][SIZE];
//        int i1, j1, k1, rand;//dummy variable
//        int as = 0;
//        int bs = 0;
//
//        for (int l1 = 0; l1 < SITES; l1++) {
//            rand = (int) (Math.random() * (SITES - l1));
//            i1 = point[rand][0];
//            j1 = point[rand][1];
//            k1 = point[rand][2];
//            if (l1 < noB) {
//                matrix[i1][j1][k1] = +1;
//                bs++;
//            } else {
//                matrix[i1][j1][k1] = -1;
//                as++;
//            }
//            point[rand][0] = point[SITES - l1 - 1][0];
//            point[rand][1] = point[SITES - l1 - 1][1];
//            point[rand][2] = point[SITES - l1 - 1][2];
//        }
//        System.out.println("Setting FCC lattice(A1).... for xB=" + xB + ", A atoms:" + as + ", B Atoms:" + bs);
//        return (matrix);
//    }
//
//    int[][][] initiate_L10(int noB, int point[][]) {
//        int matrix[][][] = new int[SIZE][SIZE][SIZE];
//        int i, j, k = 0;
//        for (i = 0; i < noB; i++) {
//            matrix[point[i][0]][point[i][1]][point[i][2]] = 1;//B atoms
//        }
//        for (j = noB; j < SITES; j++) {
//            matrix[point[j][0]][point[j][1]][point[j][2]] = -1;//A atoms
//            k++;
//        }
//        System.out.println("Setting FCC lattice(L10).... for xB=" + xB + ", A atoms:" + k + ", B Atoms:" + i);
//        return (matrix);
//    }
//
//    int[][] coordstore() {
//        int i1, j1, k1, l1;
//        int p[][] = new int[SITES][3];
//        /*
//         * Storing coordinates of sites in px,py,pz for ramdom site selection
//         * alogorithm
//         */
//        l1 = 0;
//        for (i1 = 0; i1 < SIZE; i1 = i1 + 1) {
//            for (j1 = 0; j1 < SIZE; j1 = j1 + 1) {
//                for (k1 = 0; k1 < SIZE; k1 = k1 + 1) {
//                    if ((i1 + k1) % 2 == 1) {
//                        p[l1][0] = i1;
//                        p[l1][1] = j1;
//                        p[l1][2] = k1;
//                        l1 = l1 + 1;
//                    }
//
//                }
//            }
//        }
//        //System.out.println("no of alfa coordinates stored:"+l1);
//        for (i1 = 0; i1 < SIZE; i1 = i1 + 1) {
//            for (j1 = 0; j1 < SIZE; j1 = j1 + 1) {
//                for (k1 = 0; k1 < SIZE; k1 = k1 + 1) {
//                    if ((i1 + k1) % 2 == 0) {
//                        p[l1][0] = i1;
//                        p[l1][1] = j1;
//                        p[l1][2] = k1;
//                        l1 = l1 + 1;
//                    }
//                }
//            }
//        }/*
//         * Coordinates stored
//         */
//        System.out.println("Storing coordinates....total FCC sites stored:" + l1);
//        return (p);
//    }
//
//    int MCSTEP(int L, int matrix[][][], int points[][], int J, double T, int noB) {
//        int randA, randB;
//        int xA = 0, yA = 0, zA = 0, xB = 0, yB = 0, zB = 0;
//
//        int dnnpairs;
//        int nntemp1 = 0;
//        int nntemp2 = 0;
//        int nncoord1[][] = new int[12][3];
//        double dE;
//        int performed = 0;
//
//        //Begining of MCS
//        randB = (int) (Math.random() * noB); // selecting a B atom site
//        xB = points[randB][0];  //B atom site selection and its neighbour
//        yB = points[randB][1];
//        zB = points[randB][2];
//        randA = noB + (int) (Math.random() * (SITES - noB)); // selecting an A atom site
//        xA = points[randA][0];  //A atom site selection and its neighbour
//        yA = points[randA][1];
//        zA = points[randA][2];
//        //System.out.println("randA:"+randA+", SiteA:"+xA+","+yA+","+zA+","+ matrix[xA][yA][zA]+", randB:"+randB+", SiteB:"+xB+","+yB+","+zB+","+matrix[xB][yB][zB]);
//        //STEP 2: CALCULATION OF dE=E(*)-E(i)
//        dE = Math.exp(-((DeltaE(xA, yA, zA, xB, yB, zB, L, matrix) * J) / T));
//        //System.out.println("DE:"+dE);
//        if (dE >= Math.random()) { //STEP 3: ACCEPTANCE OF NEW STATE(*)
//            //System.out.println("DE:"+dE);
//            matrix[xB][yB][zB] = -1;
//            matrix[xA][yA][zA] = 1;
//            points[randA][0] = xB;
//            points[randA][1] = yB;
//            points[randA][2] = zB;
//            points[randB][0] = xA;
//            points[randB][1] = yA;
//            points[randB][2] = zA;
//            performed = 1;
//        }
//        return (performed);
//    }
//
//    double DeltaE(int ox, int oy, int oz, int nx, int ny, int nz, int L, int matrix[][][]) {
//        int nntemp1, nntemp2;
//        double DE;
//        nntemp1 = nn_atoms(ox, oy, oz, matrix);
//        nntemp2 = nn_atoms(nx, ny, nz, matrix);
//        DE = -2 * (matrix[ox][oy][oz] * (nntemp1) + matrix[nx][ny][nz] * (nntemp2));
//        //System.out.println("de:"+DE);
//        return (DE);
//    }
//
//    static void exchange(int matrix[][][], int points[][], int i1, int j1, int k1, int i2, int j2, int k2) {
//        int temp;
//        temp = matrix[i1][j1][k1];
//        matrix[i1][j1][k1] = matrix[i2][j2][k2];
//        matrix[i2][j2][k2] = temp;
//    }
//
//    int nn_atoms(int i1, int j1, int k1, int matrix[][][])//nearest neighbor atoms
//    {
//        int sum_nn = 0;
//        int nn_coord[][];
//
//        return (matrix[(i1 + 1) % SIZE][j1][k1]
//                + matrix[(i1 - 1 + SIZE) % SIZE][j1][k1]
//                + matrix[i1][j1][(k1 + 1) % SIZE]
//                + matrix[i1][j1][(k1 - 1 + SIZE) % SIZE]
//                + matrix[i1][(j1 + 1) % SIZE][k1]
//                + matrix[i1][(j1 - 1 + SIZE) % SIZE][k1]
//                + matrix[(i1 + 1) % SIZE][(j1 + 1) % SIZE][k1]
//                + matrix[(i1 - 1 + SIZE) % SIZE][(j1 - 1 + SIZE) % SIZE][k1]
//                + matrix[(i1 + 1) % SIZE][j1][(k1 + 1) % SIZE]
//                + matrix[(i1 - 1 + SIZE) % SIZE][j1][(k1 - 1 + SIZE) % SIZE]
//                + matrix[i1][(j1 + 1) % SIZE][(k1 + 1) % SIZE]
//                + matrix[i1][(j1 - 1 + SIZE) % SIZE][(k1 - 1 + SIZE) % SIZE]);
//    }
    /*
     * double[] Stats(double CulFunc[][]) { int i1,j1; double
     * Cv,Free_Energy,Energy,Entropy; double culfunc[]=new double[7]; double
     * error_culfunc[]=new double[7]; double avEnergysq=0; double avEnergy=0; B2
     * b2;
     *
     * for(j1=0;j1<7;j1++){ culfunc[j1]=0; error_culfunc[j1]=0; for
     * (i1=0;i1<(DataPoints);i1++) culfunc[j1]=culfunc[j1]+CulFunc[i1][j1];
     * culfunc[j1]=culfunc[j1]/ DataPoints; for (i1=0;i1<(DataPoints);i1++)
     * error_culfunc[j1]=error_culfunc[j1]+Math.pow((CulFunc[i1][j1]-culfunc[j1]),2);
     * error_culfunc[j1]=Math.sqrt(error_culfunc[j1]/(DataPoints*(DataPoints-1)));
     * System.out.println("u["+j1+1+"]="+culfunc[j1]+",
     * error="+error_culfunc[j1]); } for (i1=0;i1<(DataPoints);i1++) {
     * avEnergysq=avEnergysq+Math.pow(CulFunc[i1][0],2);
     * avEnergy=avEnergy+CulFunc[i1][0]; }
     * avEnergysq=16*J*J*(avEnergysq)/DataPoints;
     * avEnergy=4*J*(avEnergy)/DataPoints;
     * Cv=(SITES*(avEnergysq-avEnergy*avEnergy))/(T*T); b2=new B2(T,xB,1.0,
     * culfunc,TCF); Entropy=b2.S(); Energy=b2.E();
     * Free_Energy=Energy-T*Entropy; System.out.println("Cv="+Cv+",
     * G="+Free_Energy+", H="+Energy+", S="+Entropy); try { FileWriter
     * writer1=new FileWriter("mc_BCC.94.11.csv",true);
     * writer1.append(SITES+","+T+","+xB+","+Free_Energy+","+Energy+","+Entropy+","+Cv+",");
     * for (i1=0;i1<7;i1++) { writer1.append(culfunc[i1]+","); } for
     * (i1=0;i1<7;i1++) { writer1.append(error_culfunc[i1]+","); }
     * writer1.append("\n"); writer1.flush(); writer1.close(); }
     * catch(IOException e) { e.printStackTrace(); } try { FileWriter
     * writer2=new FileWriter("mc_BCC.94.12.csv",true); for
     * (j1=0;j1<(DataPoints);j1++){ writer2.append(T+","+xB+","+(j1+1)); for
     * (i1=0;i1<7;i1++) { writer2.append(","+CulFunc[j1][i1]); }
     * writer2.append("\n"); } writer2.flush(); writer2.close(); }
     * catch(IOException e) { e.printStackTrace(); } return(culfunc); }
     */
//    public static void main(String args[]) {
//        double culfunc[];
//        //System.out.println("Starting....");
//        L10MCBINCE FCC = new L10MCBINCE(0.5, 1.3, 1, 16, 1000, 0);
//        FCC.MCSS_run();
//    }
}