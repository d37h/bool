package ru.rsreu.astrukov.bool.service;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;
import javafx.util.Pair;

public class OpenClServiceJava {

//    public Pair<boolean[][], boolean[][]> calcWeight(
//            boolean[][] boolVars,
//            boolean[][] containVars,
//            boolean[][] truthySets,
//            boolean[][] falsySets
//    ) {
//        int groupsCount = boolVars.length;//j2
//        int setsCount = truthySets.length;
//        int setLength = truthySets[0].length;
//
//        int groupLength = boolVars[0].length;
//        int resultSize = setsCount * groupsCount;
//
//        boolean[][] truthyResult = new boolean[setsCount][groupsCount];
//        boolean[][] falsyResult = new boolean[setsCount][groupsCount];
//
//        Kernel kernel = new Kernel() {
//            public void run() {
//                final int setIndex = getGlobalId() / (setsCount);
//                final int groupIndex = getGlobalId() % (groupsCount);
//
//                solveCl(setIndex, groupIndex, true);
//                solveCl(setIndex, groupIndex, false);
////                boolean[] group = boolVars[groupIndex];
////                boolean[] containGroup = containVars[groupIndex];
////
////                boolean gotFalsy = false;
////                int varIndex = 0;
////
////                while (!gotFalsy || varIndex < setLength) {
////                    if (containGroup[varIndex]) {
////                        boolean variableReplaceResult = (group[varIndex] == truthySets[setIndex][varIndex]);
////
////                        gotFalsy = !variableReplaceResult;
////                    }
////                    varIndex++;
////                }
////
////                truthyResult[setIndex][groupIndex] = !gotFalsy;
////
////                gotFalsy = false;
////                varIndex = 0;
////
////                while (!gotFalsy || varIndex < setLength) {
////                    if (containGroup[varIndex]) {
////                        boolean variableReplaceResult = (group[varIndex] == truthySets[setIndex][varIndex]);
////
////                        gotFalsy = !variableReplaceResult;
////                    }
////                    varIndex++;
////                }
////
////                falsyResult[setIndex][groupIndex] = !gotFalsy;
//            }
//
//            public void solveCl(int setIndex, int groupIndex, boolean isTruthy) {
//                boolean[] group = boolVars[groupIndex];
//                boolean[] containGroup = containVars[groupIndex];
//                boolean[] set = isTruthy ? truthySets[setIndex] : falsySets[setIndex];
//                boolean gotFalsy = false;
//                int varIndex = 0;
//
//                while (!gotFalsy || varIndex < setLength) {
//                    if (containGroup[varIndex]) {
//                        boolean variableReplaceResult = (group[varIndex] == set[varIndex]);
//
//                        gotFalsy = !variableReplaceResult;
//                    }
//                    varIndex++;
//                }
//
//                if (isTruthy) {
//                    truthyResult[setIndex][groupIndex] = !gotFalsy;
//                } else {
//                    falsyResult[setIndex][groupIndex] = !gotFalsy;
//                }
//            }
//
//
//        };
//
//        Device kmDevice = KernelManager.instance().bestDevice();
//        Range range = Range.create(kmDevice, resultSize);
//                //Device.firstCPU().createRange(resultSize);
//        kernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.GPU);
//        kernel.execute(range);
////        val et = kernel.executionTime
////        val sb = StringBuilder()
////        KernelManager.instance().reportDeviceUsage(sb, false)
////        val report = sb.toString()
////        val a = 1;
//
//        return new Pair<boolean[][], boolean[][]>(truthyResult, falsyResult);
//
//    }


 public Pair<boolean[][], boolean[][]> calcWeightDbg(
            boolean[][] boolVars,
            boolean[][] containVars,
            boolean[][] truthySets,
            boolean[][] falsySets
    ) {
        int groupsCount = boolVars.length;//j2
        int setsCount = truthySets.length;
        int setLength = truthySets[0].length;

        int groupLength = boolVars[0].length;
        int resultSize = setsCount * groupsCount;

        boolean[][] truthyResult = new boolean[setsCount][groupsCount];
        boolean[][] falsyResult = new boolean[setsCount][groupsCount];

     //считаем результаты подставления группы переменных в сет
     DbgKernel kernel = new DbgKernel() {
         public void runCl(int finalKernelCounter) {
                final int setIndex = finalKernelCounter / (groupsCount);
                final int groupIndex = finalKernelCounter % (groupsCount);

                solveCl(setIndex, groupIndex, true);
                solveCl(setIndex, groupIndex, false);
            }

            public void solveCl(int setIndex, int groupIndex, boolean isTruthy) {
                boolean[] group = boolVars[groupIndex];
                boolean[] set = isTruthy ? truthySets[setIndex] : falsySets[setIndex];
                boolean[] containGroup = containVars[groupIndex];
                boolean gotFalsy = false;
                int varIndex = 0;

                if (setIndex == 3 && groupIndex == 1) {
                    int a =0;
                }


                while (!gotFalsy && (varIndex < (setLength))) {
                    if (containGroup[varIndex]) {
                        boolean variableReplaceResult = (group[varIndex] == set[varIndex]);

                        gotFalsy = !variableReplaceResult;
                    }
                    varIndex++;
                }

                if (isTruthy) {
                    truthyResult[setIndex][groupIndex] = !gotFalsy;
                } else {
                    falsyResult[setIndex][groupIndex] = !gotFalsy;
                }
            }


        };

     for (int i = 0; i < resultSize; i++) {
         kernel.runCl(i);
     }
//        val et = kernel.executionTime
//        val sb = StringBuilder()
//        KernelManager.instance().reportDeviceUsage(sb, false)
//        val report = sb.toString()
//        val a = 1;

        return new Pair<boolean[][], boolean[][]>(truthyResult, falsyResult);

    }

}

abstract class DbgKernel {
    public void runCl(int finalKernelCounter) {}
}
