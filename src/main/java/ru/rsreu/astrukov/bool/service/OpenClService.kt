package ru.rsreu.astrukov.bool.service

import com.aparapi.Kernel
import com.aparapi.Range
import com.aparapi.internal.kernel.KernelManager
import java.lang.RuntimeException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.pow


class OpenClService {


    init {
        val l1 = 31.getBinaryLength()
        val l2 = 32.getBinaryLength()
        val l3 = 33.getBinaryLength()

        val t1 = generate(31, 5)
        val t2 = generate(32, 6)
        val t3 = generate(33, 6)
        val t34 = generate(33, 6)
    }

    fun calcWeight(
            boolVars: Array<BooleanArray>,
            containMask: Array<BooleanArray>
    ): Int {
        val res = calcWeightOnGpu(
                boolVars,
                containMask
        )

        val resChunked = res.toList().chunked(res.size / (boolVars[0].size)/2)


        val tt = resChunked.filterIndexed { index, _ -> index % 2 == 0 }
                .map { list-> list.map{it.contains(true)} }

        val ff = resChunked.filterIndexed { index, _ -> index % 2 == 1 }
                .map { list-> list.map{it.contains(true)} }

        val resultList = tt.mapIndexed { index, list -> list.zip(ff[index]).count {
            it.first xor it.second
        }}

        //fixme: remove rev???
        return resultList.indexOf(resultList.max())

//        val t = resChunked[0].map { it.contains(true) }
//        val f = resChunked[1].map { it.contains(true) }
//
//        val resultBySets = t.zip(f)
//
//        return resultBySets.count {
//            it.first xor it.second
//        }

    }

    private fun calcWeightOnGpu(
            boolVars: Array<BooleanArray>,
            containVars: Array<BooleanArray>
    ): Array<BooleanArray> {
        val groupsCount = boolVars.size
        val setLength = boolVars[0].size// (boolVars[0].size+1).getBinaryLength()
        val singleVariableSetRange = 2.0.pow(setLength).toInt()
        val setsCount = (singleVariableSetRange * boolVars[0].size)

        val sets = Array(setsCount) { BooleanArray(setLength) }

        val resultSize = setsCount * groupsCount
        val result = Array(setsCount) { BooleanArray(groupsCount) }

//        считаем результаты подставления группы переменных в сет
//        wk.apply {
//            this.boolVars = boolVars
//            this.containVars = containVars
//            this.sets = sets
//            this.result = result
//            this.size = groupsCount
//            this.setLength = setLength
//        }

        val kernel = object {
            fun run(globalId: Int) {
                if (globalId == 64) {
                    val a =1
                }
                val setIndex = globalId / groupsCount
                val groupIndex = globalId % groupsCount

                val variableIndex = globalId / (singleVariableSetRange* groupsCount)
                val setGroupNumber = globalId % singleVariableSetRange
                val setGenerationIndex = setIndex % (singleVariableSetRange/2)

                //fixme:
                val groupR = boolVars[groupIndex]
                val set = generate(setGenerationIndex, setLength)
                val sameLockedVariableSetsCount = (singleVariableSetRange*groupsCount/2)
                set[variableIndex] = (globalId / sameLockedVariableSetsCount) % 2 == 0

                solveCl(setIndex, groupIndex, set)
                val calcResult = groupR.mapIndexed {index: Int, b: Boolean -> !containVars[groupIndex][index] || (containVars[groupIndex][index] && (b == set[index]))  }
                        .all { it }
                if (result[setIndex][groupIndex] != calcResult) {
                    val a = 1
                }
                val a = "Group($groupIndex):"+ groupR.map { if(it) "1" else "0" }+
                        " Set($setIndex):"+ set.map { if(it) "1" else "0" }+
                        " Var: $variableIndex Result: ${result[setIndex][groupIndex]}"
                val b= ""
            }

            fun solveCl(setIndex: Int, groupIndex: Int, set : BooleanArray) {
                val group = boolVars[groupIndex]
//                val set = group
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

            fun generate(range: Int, size: Int) : BooleanArray{
                var number = range

                val array = BooleanArray(size)
                var varIndex =  0

                while (varIndex < size) {
                    array[varIndex] = number % 2 != 0
                    number /= 2
                    varIndex++
                }

                return array
            }

        }

        val gpuDevice = KernelManager.instance().bestDevice()
        val range = Range.create(gpuDevice, resultSize)
//        kernel.execute(range)
        for (i in 0 until resultSize) {
            try {
                kernel.run(i)
            } catch (e: RuntimeException) {
                val a = 1
            }
        }
        //wk.execute(range)
        //        val et = kernel.executionTime
        val sb = StringBuilder()
        KernelManager.instance().reportDeviceUsage(sb, true)
        val report = sb.toString()
        print(report)
        return result
    }

    fun generate(range: Int, size: Int) : BooleanArray{
        var number = range

        val array = BooleanArray(size)
        var varIndex =  0

        while (varIndex < size) {
            array[varIndex] = number % 2 != 0
            number /= 2
            varIndex++
        }

        return array
    }

    fun Int.getBinaryLength(): Int {
        var setSize = log2((this/2.0))+1
        if (ceil(setSize) == floor(setSize)) {
            setSize += 1
        }
        return ceil(setSize).toInt()
    }

}

//
//class WeightKernel : Kernel() {
//
//    var boolVars: Array<BooleanArray> = Array(1) { BooleanArray(1) }
//    var containVars: Array<BooleanArray> = Array(1) { BooleanArray(1) }
//    var sets: Array<BooleanArray> = Array(1) { BooleanArray(1) }
//    var result: Array<BooleanArray> = Array(1) { BooleanArray(1) }
//    var size: Int = 1
//    var setLength: Int = 1
//
//    override fun run() {
//        val setIndex = globalId / size
//        val groupIndex = globalId % size
//        solveCl(setIndex, groupIndex)
//    }
//
//    fun solveCl(setIndex: Int, groupIndex: Int) {
//        val group = boolVars[groupIndex]
//        val set = sets[setIndex]
//        val containGroup = containVars[groupIndex]
//        var gotFalsy = false
//        var varIndex = 0
//        while (!gotFalsy && varIndex < setLength) {
//            if (containGroup[varIndex]) {
//                val variableReplaceResult = group[varIndex] == set[varIndex]
//                gotFalsy = !variableReplaceResult
//            }
//            varIndex++
//        }
//        result[setIndex][groupIndex] = !gotFalsy
//    }
//
//}