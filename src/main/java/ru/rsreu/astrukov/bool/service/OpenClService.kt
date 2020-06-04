package ru.rsreu.astrukov.bool.service

import com.aparapi.Kernel
import com.aparapi.Range
import com.aparapi.device.Device
import com.aparapi.internal.kernel.KernelManager


class OpenClService {

    val openClServiceJava = OpenClServiceJava()

    fun run() {
        val inA = FloatArray(100)// get a float array of data from somewhere
        val inB = FloatArray(100)// get a float array of data from somewhere

        for (i in inA.indices) {
            inA[i] = i.toFloat()
            inB[i] = (i * 2).toFloat()
        }


        val result = FloatArray(inA.size)

        val kernel = object : Kernel() {
            override fun run() {
                val i = globalId
                result[i] = inA[i] + inB[i]
            }
        }

        var device1 = Device.best()
        val kmDevice = KernelManager.instance().bestDevice()
        val range = Range.create(kmDevice, result.size)

        kernel.execute(range)
        val et = kernel.executionTime
        val sb = StringBuilder()
        KernelManager.instance().reportDeviceUsage(sb, false)
        val report = sb.toString()
        val a = 1;

    }

    fun calcWeight(boolVars: Array<Array<Boolean?>>, truthySets: Array<Array<Boolean>>, falsySets: Array<Array<Boolean>>) : Int {
        val groupsCount = boolVars.size //j2
        val setsCount = truthySets.size
        val setLength = truthySets[0].size

        val groupLength = boolVars[0].size
        val resultSize = setsCount * groupsCount

        val kernel = object : Kernel() {

            val setIndex = globalId.div(setsCount)
            val groupIndex = globalId.rem(groupLength)

            val truthyResult = Array<Array<Boolean>>(setsCount) { Array(groupsCount) {false} }
            val falsyResult = Array<Array<Boolean>>(setsCount) { Array(groupsCount) {false} }

            override fun run() {
                val gId = globalId
                val group = boolVars[groupIndex]
                cycle(group, truthySets[setIndex],truthyResult)
                cycle(group, falsySets[setIndex], falsyResult)
                val a = 1
            }

            fun cycle(group: Array<Boolean?>, set: Array<Boolean>, result:Array<Array<Boolean>>) {
                var gotFalsy = false
                var varIndex = 0

                while (!gotFalsy || varIndex < setLength) {
                    val conjuncResult = if (group[varIndex] != null) {
                        group[varIndex]!! && set[varIndex]
                    } else null

                    if (conjuncResult == false) gotFalsy = true
                    varIndex++
                }

                result[setIndex][groupIndex] = !gotFalsy
            }
        }

        val kmDevice = KernelManager.instance().bestDevice()
        val range = Range.create(kmDevice, resultSize)
//        val range = Range.create(resultSize)

        kernel.execute(range)
        val et = kernel.executionTime
        val sb = StringBuilder()
        KernelManager.instance().reportDeviceUsage(sb, false)
        val report = sb.toString()
        val a = 1;

        val t = truthySets.map { it.contains(true) }
        val f = falsySets.map { it.contains(true) }

        val resultBySets = t.zip(f)

        val weight = resultBySets.count {
            it.first xor it.second
        }

        return weight

    }

    fun calcWeightJava(boolVars: Array<BooleanArray>, containMask:  Array<BooleanArray>, truthySets: Array<Array<Boolean>>, falsySets: Array<Array<Boolean>>) : Int {
        val res = openClServiceJava.calcWeightDbg(
                boolVars,
                containMask,
                truthySets.map { it.toBooleanArray() }.toTypedArray(),
                falsySets.map { it.toBooleanArray() }.toTypedArray()
        )

        val t = res.key.map { it.contains(true) }
        val f = res.value.map { it.contains(true) }

        val resultBySets = t.zip(f)

        val weight = resultBySets.count {
            it.first xor it.second
        }

        return weight

    }

    data class boolVariable(
            val value: Boolean,
            val isPresent: Boolean
    )
}
