package ru.rsreu.astrukov.bool.service

import com.aparapi.Kernel
import com.aparapi.Range
import com.aparapi.internal.kernel.KernelManager


class OpenClService {

    val wk = WeightKernel()

    init {
        warmup()
        print("WARMUP ENDED, READY TO ROLL")
    }

    fun calcWeight(
            boolVars: Array<BooleanArray>,
            containMask: Array<BooleanArray>,
            truthySets: Array<BooleanArray>,
            falsySets: Array<BooleanArray>
    ): Int {
        val res = calcWeightOnGpu(
                boolVars,
                containMask,
                truthySets.plus(falsySets)
        )

        val resChunked = res.toList().chunked(res.size / 2)

        val t = resChunked[0].map { it.contains(true) }
        val f = resChunked[1].map { it.contains(true) }

        val resultBySets = t.zip(f)

        return resultBySets.count {
            it.first xor it.second
        }

    }

    private fun warmup() {
        calcWeightOnGpu(
                Array(1) { BooleanArray(1) },
                Array(1) { BooleanArray(1) },
                Array(1) { BooleanArray(1) }
        )
    }

    private fun calcWeightOnGpu(
            boolVars: Array<BooleanArray>,
            containVars: Array<BooleanArray>,
            sets: Array<BooleanArray>
    ): Array<BooleanArray> {
        val groupsCount = boolVars.size
        val setsCount = sets.size
        val setLength = sets[0].size
        val resultSize = setsCount * groupsCount
        val result = Array(setsCount) { BooleanArray(groupsCount) }

        //считаем результаты подставления группы переменных в сет
        wk.apply {
            this.boolVars = boolVars
            this.containVars = containVars
            this.sets = sets
            this.result = result
            this.size = groupsCount
            this.setLength = setLength
        }

        val kernel: Kernel = object : Kernel() {
            override fun run() {
                val setIndex = globalId / groupsCount
                val groupIndex = globalId % groupsCount
                solveCl(setIndex, groupIndex)
            }

            fun solveCl(setIndex: Int, groupIndex: Int) {
                val group = boolVars[groupIndex]
                val set = sets[setIndex]
                val containGroup = containVars[groupIndex]
                var gotFalsy = false
                var varIndex = 0
                while (!gotFalsy && varIndex < setLength) {
                    if (containGroup[varIndex]) {
                        val variableReplaceResult = group[varIndex] == set[varIndex]
                        gotFalsy = !variableReplaceResult
                    }
                    varIndex++
                }
                result[setIndex][groupIndex] = !gotFalsy
            }
        }

        val gpuDevice = KernelManager.instance().bestDevice()
        val range = Range.create(gpuDevice, resultSize)
        kernel.execute(range)
        //wk.execute(range)
        //        val et = kernel.executionTime
//        val sb = StringBuilder()
//        KernelManager.instance().reportDeviceUsage(sb, true)
//        val report = sb.toString()
//        print(report)
        return result
    }
}


class WeightKernel : Kernel() {

    var boolVars: Array<BooleanArray> = Array(1) { BooleanArray(1) }
    var containVars: Array<BooleanArray> = Array(1) { BooleanArray(1) }
    var sets: Array<BooleanArray> = Array(1) { BooleanArray(1) }
    var result: Array<BooleanArray> = Array(1) { BooleanArray(1) }
    var size: Int = 1
    var setLength: Int = 1

    override fun run() {
        val setIndex = globalId / size
        val groupIndex = globalId % size
        solveCl(setIndex, groupIndex)
    }

    fun solveCl(setIndex: Int, groupIndex: Int) {
        val group = boolVars[groupIndex]
        val set = sets[setIndex]
        val containGroup = containVars[groupIndex]
        var gotFalsy = false
        var varIndex = 0
        while (!gotFalsy && varIndex < setLength) {
            if (containGroup[varIndex]) {
                val variableReplaceResult = group[varIndex] == set[varIndex]
                gotFalsy = !variableReplaceResult
            }
            varIndex++
        }
        result[setIndex][groupIndex] = !gotFalsy
    }

}