package org.bohan.rpc.server.worker

class ThreadUtil {

    companion object {
        fun calculateThreadCountForIo(waitTime: Int, calculateTime: Int): Int {
            val cpuCores = Runtime.getRuntime().availableProcessors()
            return (cpuCores * (1 + waitTime.toDouble() / calculateTime)).toInt()
        }
    }

}