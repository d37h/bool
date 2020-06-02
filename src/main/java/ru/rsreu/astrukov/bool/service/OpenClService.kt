package ru.rsreu.astrukov.bool.service

import com.aparapi.Kernel


class OpenClService {


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

        val a = 1;

    }
}
