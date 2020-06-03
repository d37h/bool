package ru.rsreu.astrukov.bool.service;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;
import javafx.util.Pair;

public class OpenClServiceJava {

    public Pair<boolean[][], boolean[][]> calcWeight(
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

        Kernel kernel = new Kernel() {

            final int setIndex = getGlobalId() / (setsCount);
            final int groupIndex = getGlobalId() % (groupLength);

            public void run() {
                boolean[] group = boolVars[groupIndex];
                boolean[] containGroup = containVars[groupIndex];
//                int a = 0;
//                for (int i = 0; i < 10; i++) {
//                    a++;
//                }

//                boolean gotFalsy = false;
//
//                for (int varIndex = 0; varIndex < setLength; varIndex++) {
//                    if (!gotFalsy && containGroup[varIndex]) {
//                        boolean conjuncResult = group[varIndex] && truthySets[setIndex][varIndex];
//
//                        gotFalsy = !conjuncResult;
//                    }
//                }
                boolean gotFalsy = false;
                int varIndex = 0;

                while (!gotFalsy || varIndex < setLength) {
                    if (containGroup[varIndex]) {
                        boolean conjuncResult = group[varIndex] && truthySets[setIndex][varIndex];

                        gotFalsy = !conjuncResult;
                    }
                    varIndex++;
                }

                gotFalsy = false;
                varIndex = 0;

                while (!gotFalsy || varIndex < setLength) {
                    if (containGroup[varIndex]) {
                        boolean conjuncResult = group[varIndex] && falsySets[setIndex][varIndex];

                        gotFalsy = !conjuncResult;
                    }
                    varIndex++;
                }

//                cycle(group, containGroup, truthySets[setIndex], truthyResult);
//                cycle(group, containGroup, falsySets[setIndex], falsyResult);
            }

            void cycle(boolean[] group, boolean[] containGroup, boolean[] set, boolean[][] result) {
                boolean gotFalsy = false;
                int varIndex = 0;
                boolean cg = containGroup[varIndex];
                boolean conjuncResult = false;
                //!gotFalsy ||
//                for (int varIndex = 0; varIndex < setLength; varIndex++) {
//                    if (!gotFalsy && containGroup[varIndex]) {
//                        boolean conjuncResult = group[varIndex] && set[varIndex];
//
//                        gotFalsy = !conjuncResult;
//                    }
//                }


//                while (!gotFalsy || varIndex < setLength) {
//                    if (containGroup[varIndex]) {
//                        conjuncResult = group[varIndex] && set[varIndex];
//
//                        gotFalsy = !conjuncResult;
//                    }
//                    varIndex++;
//                }

                result[setIndex][groupIndex] = !gotFalsy;
            }
        };

        Device kmDevice = KernelManager.instance().bestDevice();
        Range range = Range.create(kmDevice, resultSize);

        kernel.execute(range);
//        val et = kernel.executionTime
//        val sb = StringBuilder()
//        KernelManager.instance().reportDeviceUsage(sb, false)
//        val report = sb.toString()
//        val a = 1;

        return new Pair<boolean[][], boolean[][]>(truthyResult, falsyResult);

    }

}
