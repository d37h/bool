package ru.rsreu.astrukov.bool.service;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;
import javafx.util.Pair;

public class OpenClServiceJava {

    public boolean[][] calcWeight(
            boolean[][] boolVars,
            boolean[][] containVars,
            boolean[][] sets
    ) {
        int groupsCount = boolVars.length;
        int setsCount = sets.length;
        int setLength = sets[0].length;

        int resultSize = setsCount * groupsCount;

        boolean[][] result = new boolean[setsCount][groupsCount];

        //считаем результаты подставления группы переменных в сет
        Kernel kernel = new Kernel() {
            public void run() {
                final int setIndex = getGlobalId() / (groupsCount);
                final int groupIndex = getGlobalId() % (groupsCount);

                solveCl(setIndex, groupIndex);
            }

            public void solveCl(int setIndex, int groupIndex) {
                boolean[] group = boolVars[groupIndex];
                boolean[] set = sets[setIndex];
                boolean[] containGroup = containVars[groupIndex];
                boolean gotFalsy = false;
                int varIndex = 0;

                while (!gotFalsy && (varIndex < (setLength))) {
                    if (containGroup[varIndex]) {
                        boolean variableReplaceResult = (group[varIndex] == set[varIndex]);

                        gotFalsy = !variableReplaceResult;
                    }
                    varIndex++;
                }

                result[setIndex][groupIndex] = !gotFalsy;

            }
        };

        Device gpuDevice = KernelManager.instance().bestDevice();
        Range range = Range.create(gpuDevice, resultSize);
        kernel.execute(range);
//        val et = kernel.executionTime
//        val sb = StringBuilder()
//        KernelManager.instance().reportDeviceUsage(sb, false)
//        val report = sb.toString()
//        val a = 1;

        return result;
    }
}
