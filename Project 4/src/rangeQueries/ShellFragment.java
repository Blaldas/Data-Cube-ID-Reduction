package rangeQueries;

import java.sql.SQLOutput;

public class ShellFragment {

    DIntArray[] matrix;
    int lower;
    int upper;


    /**
     * @param upper biggest value to store
     * @param lower lowest value yo store
     *              <p>
     *              used to create subCubes!
     */
    ShellFragment(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
        matrix = new DIntArray[upper - lower + 1];
    }

    /**
     * @param tid      the tid to add - what to add
     * @param tidValue the valye to add - where to add
     */
    public void addTuple(int tid, int tidValue) {
        if (matrix[tidValue - lower] == null) {
            matrix[tidValue - lower] = new DIntArray();
        }
        matrix[tidValue - lower].addTid(tid);
    }

    /**
     * prones all necessary arrays of this shellfragment
     */
    public void proneShellFragment() {
        for (DIntArray d : matrix) {
            if (d == null)
                continue;
            d.proneDIntArray();
        }
    }

    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public DIntArray getTidsListFromValue(int value) {
        if (value > upper || value < lower || matrix[value - lower] == null)
            return new DIntArray();

        return matrix[value - lower];
    }

    /**
     * @return all the values being stored
     */
    public int[] getAllValues() {
        int[] returnable = new int[matrix.length];

        for (int i = 0; i < returnable.length; returnable[i] = lower + i++) {
        }
        return returnable;
    }

    /**
     * @return the biggest tid stored int the shellfragment
     */
    public int getBiggestTid() {
        int max = -1;                                                                           //coloca um valor inicial nunca returnavel em max

        //para cada dimens??o
        for (DIntArray dIntArray : matrix) {
            if (dIntArray == null)
                continue;
            int a = dIntArray.getBigestTid();
            if (a > max)
                max = a;
        }
        return max;                                                             //devolve max
    }

    /**
     * @return returns an array with all the tids
     * <p>This function should actually be ignored, the method getBiggestTid() should be used instead
     */
    public int[] getAllTids() {
        int b = getBiggestTid();
        int[] returnable = new int[b + 1];

        for (int i = 0; i < returnable.length; i++)
            returnable[i] = i;

        return returnable;
    }

    public DIntArray getTidsListFromListValue(int[] attrvalues) {
        DIntArray[] tidsList = new DIntArray[attrvalues.length];

        for (int i = 0; i < attrvalues.length; i++) {
            tidsList[i] = getTidsListFromValue(attrvalues[i]);
        }
        DIntArray returnable = tidsList[0];

        for (int i = 1; i < tidsList.length; i++)
            returnable = joinDIntArrays(returnable, tidsList[i]);

        return returnable;
    }

    private DIntArray joinDIntArrays(DIntArray A, DIntArray B) {
        int rA = 0;
        int nA = 0;
        int rB = 0;
        int nB = 0;

        DIntArray c = new DIntArray();

        //Enquanto n??o estiverem todos passados
        while (rA < A.sizeReduced || nA < A.sizeNonReduced || rB < B.sizeReduced || nB < B.sizeNonReduced) {
            //Se A n??o tiver
            if (rA == A.sizeReduced && nA == A.sizeNonReduced) {
                //Se B n??o tem reduzido
                if (rB == B.sizeReduced) {
                    //Adiciona n??o reduzido
                    c.addTid(B.noReductionArray[nB]);
                    ++nB;
                    //Se B n??o tem n??o reduzido
                } else if (nB == B.sizeNonReduced) {// ( nB == B.sizesizeNonReduced || B.noReductionArray[nB] > B.reducedPos1[rB])
                    //Adiciona reduzido
                    c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                    ++rB;
                    //Se B reduzido < B n??o reduzid
                } else if (B.reducedPos1[rB] < B.noReductionArray[nB]) {
                    //Adiciona reduzido
                    c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                    ++rB;
                } else {
                    //Adiciona n??o reduzido
                    c.addTid(B.noReductionArray[nB]);
                    ++nB;
                }
                //Se A n??o tem reduzido
            } else if (rA == A.sizeReduced) {
                //Se B n??o tiver
                if (rB == B.sizeReduced && nB == B.sizeNonReduced) {
                    //Adiciona A n??o reduzido
                    c.addTid(A.noReductionArray[nA]);
                    ++nA;
                    //Se B n??o tem n??o reduzido
                } else if (nB == B.sizeNonReduced) {// ( nB == B.sizesizeNonReduced || B.noReductionArray[nB] > B.reducedPos1[rB])
                    //Se B reduzido < A n??o reduzido
                    if (B.reducedPos1[rB] < A.noReductionArray[nA]) {
                        //Adiciona B reduzido
                        c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                        ++rB;
                        //sen??o
                    } else {
                        //Adiciona A n??o reduzido
                        c.addTid(A.noReductionArray[nA]);
                        ++nA;
                    }
                    //Se B n??o tem reduzido
                } else if (rB == B.sizeReduced) {
                    //Se B n??o reduzido < A n??o reduzido
                    if (B.noReductionArray[nB] < A.noReductionArray[nA]) {
                        //Adiciona b n??o reduzido
                        c.addTid(B.noReductionArray[nB]);
                        ++nB;
                        //sen??o
                    } else {
                        //Adiciona A n??o reduzido
                        c.addTid(A.noReductionArray[nA]);
                        ++nA;
                    }
                    //Se B reduzido < B n??o reduzido
                } else if (B.reducedPos1[rB] < B.noReductionArray[nB]) {
                    //Se B reduzido < A n??o reduzido
                    if (B.reducedPos1[rB] < A.noReductionArray[nA]) {
                        //Adiciona B reduzido
                        c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                        ++rB;
                        //Sen??o
                    } else {
                        //Adiciona A n??o reduzido
                        c.addTid(A.noReductionArray[nA]);
                        ++nA;
                    }
                    //Sen??o
                } else {
                    //Se B n??o reduzido < A n??o reduzido
                    if (B.noReductionArray[nB] < A.noReductionArray[nA]) {
                        //Adiciona B n??o reduzido
                        c.addTid(B.noReductionArray[nB]);
                        ++nB;
                        //Sen??o
                    } else {
                        //Adiciona A nao reduzido
                        c.addTid(A.noReductionArray[nA]);
                        ++nA;
                    }
                }
                //Se A n??o tem n??o reduzido
            } else if (nA == A.sizeNonReduced) {// ( nA == A.sizeNonReduced || A.noReductionArray[nA] > A.reducedPos1[rA])
                //Se B n??o tiver
                if (rB == B.sizeReduced && nB == B.sizeNonReduced) {
                    //Adiciona A reduzido
                    c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                    ++rA;
                    //Se B n??o tem n??o reduzido
                } else if (nB == B.sizeNonReduced) {// ( nB == B.sizesizeNonReduced || B.noReductionArray[nB] > B.reducedPos1[rB])
                    //Se B reduzido < A reduzido
                    if (A.reducedPos1[rA] < B.reducedPos1[rB]) {
                        c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                        ++rA;
                    } else {
                        c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                        ++rB;
                    }
                    //Se B n??o tem reduzido
                } else if (rB == B.sizeReduced) {
                    //Se B n??o reduzido < A reduzido
                    if (B.noReductionArray[nB] < A.reducedPos1[rA]) {
                        //Adiciona B n??o reduzido
                        c.addTid(B.noReductionArray[nB]);
                        ++nB;
                        //Sen??o
                    } else {
                        //Adiciona A reduzido
                        c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                        ++rA;
                    }
                    //Se B reduzido < B n??o reduzido
                } else if (B.reducedPos1[rB] < B.noReductionArray[nB]) {
                    //Se B reduzido < A reduzido
                    if (B.reducedPos1[nB] < A.reducedPos1[rA]) {
                        //Adiciona B reduzido
                        c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                        ++rB;
                        //Sen??o
                    } else {
                        //Adiciona A reduzido
                        c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                        ++rA;
                    }
                    //Sen??o
                } else {
                    //Se B n??o reduzido < A reduzido
                    if (B.noReductionArray[nB] < A.reducedPos1[rA]) {
                        //Adiciona B n??o reduzido
                        c.addTid(B.noReductionArray[nB]);
                        ++nB;
                        //Sen??o
                    } else {
                        //Adiciona A reduzido
                        c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                        ++rA;
                    }
                }
                //Se A n??o reduzido < A reduzido
            } else if (A.noReductionArray[nA] < A.reducedPos1[rA]) {
                //Se B n??o tiver
                if (rB == B.sizeReduced && nB == B.sizeNonReduced) {
                    //Adiciona A n??o reduzido
                    c.addTid(A.noReductionArray[nA]);
                    ++nA;
                    //Se B n??o tem n??o reduzido
                } else if (nB == B.sizeNonReduced) {// ( nB == B.sizesizeNonReduced || B.noReductionArray[nB] > B.reducedPos1[rB])
                    //Se B reduzido < A n??o reduzido
                    if (B.reducedPos1[rB] < A.noReductionArray[nA]) {
                        //Adiciona B reduzido
                        c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                        ++rB;
                        //sen??o
                    } else {
                        //Adiciona A n??o reduzido
                        c.addTid(A.noReductionArray[nA]);
                        ++nA;
                    }
                    //Se B n??o tem reduzido
                } else if (rB == B.sizeReduced) {
                    //Se B n??o reduzido < A n??o reduzido
                    if (B.noReductionArray[nB] < A.noReductionArray[nA]) {
                        //Adiciona b n??o reduzido
                        c.addTid(B.noReductionArray[nB]);
                        ++nB;
                        //sen??o
                    } else {
                        //Adiciona A n??o reduzido
                        c.addTid(A.noReductionArray[nA]);
                        ++nA;
                    }
                    //Se B reduzido < B n??o reduzido
                } else if (B.reducedPos1[rB] < B.noReductionArray[nB]) {
                    //Se B reduzido < A n??o reduzido
                    if (B.reducedPos1[rB] < A.noReductionArray[nA]) {
                        //Adiciona B reduzido
                        c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                        ++rB;
                        //Sen??o
                    } else {
                        //Adiciona A n??o reduzido
                        c.addTid(A.noReductionArray[nA]);
                        ++nA;
                    }
                    //Sen??o
                } else {
                    //Se B n??o reduzido < A n??o reduzido
                    if (B.noReductionArray[nB] < A.noReductionArray[nA]) {
                        //Adiciona B n??o reduzido
                        c.addTid(B.noReductionArray[nB]);
                        ++nB;
                        //Sen??o
                    } else {
                        //Adiciona A nao reduzido
                        c.addTid(A.noReductionArray[nA]);
                        ++nA;
                    }
                }
            } else {
                //Se B n??o tiver
                //Se B n??o tiver
                if (rB == B.sizeReduced && nB == B.sizeNonReduced) {
                    //Adiciona A reduzido
                    c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                    ++rA;
                    //Se B n??o tem n??o reduzido
                } else if (nB == B.sizeNonReduced) {// ( nB == B.sizesizeNonReduced || B.noReductionArray[nB] > B.reducedPos1[rB])
                    //Se B reduzido < A reduzido
                    if (A.reducedPos1[rA] < B.reducedPos1[rB]) {
                        c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                        ++rA;
                    } else {
                        c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                        ++rB;
                    }
                    //Se B n??o tem reduzido
                } else if (rB == B.sizeReduced) {
                    //Se B n??o reduzido < A reduzido
                    if (B.noReductionArray[nB] < A.reducedPos1[rA]) {
                        //Adiciona B n??o reduzido
                        c.addTid(B.noReductionArray[nB]);
                        ++nB;
                        //Sen??o
                    } else {
                        //Adiciona A reduzido
                        c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                        ++rA;
                    }
                    //Se B reduzido < B n??o reduzido
                } else if (B.reducedPos1[rB] < B.noReductionArray[nB]) {
                    //Se B reduzido < A reduzido
                    if (B.reducedPos1[rB] < A.reducedPos1[rA]) {
                        //Adiciona B reduzido
                        c.addTidInterval(B.reducedPos1[rB], B.reducedPos2[rB]);
                        ++rB;
                        //Sen??o
                    } else {
                        //Adiciona A reduzido
                        c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                        ++rA;
                    }
                    //Sen??o
                } else {
                    //Se B n??o reduzido < A reduzido
                    if (B.noReductionArray[nB] < A.reducedPos1[rA]) {
                        //Adiciona B n??o reduzido
                        c.addTid(B.noReductionArray[nB]);
                        ++nB;
                        //Sen??o
                    } else {
                        //Adiciona A reduzido
                        c.addTidInterval(A.reducedPos1[rA], A.reducedPos2[rA]);
                        ++rA;
                    }
                }
            }

        }
        c.proneDIntArray();
        return c;
    }
}