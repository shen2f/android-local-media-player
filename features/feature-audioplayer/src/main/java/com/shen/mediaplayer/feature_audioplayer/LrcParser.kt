package com.shen.mediaplayer.feature_audioplayer

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

data class LrcLine(
    val time: Long,
    val text: String
)

class LrcParser {

    private val pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\]")

    fun parse(file: File): List<LrcLine> {
        val lines = mutableListOf<LrcLine>()
        try {
            val reader = BufferedReader(FileReader(file))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                line?.let { parseLine(it, lines) }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        lines.sortBy { it.time }
        return lines
    }

    private fun parseLine(line: String, lines: MutableList<LrcLine>) {
        val matcher = pattern.matcher(line)
        var currentLine = line
        var time: Long = 0

        while (matcher.find()) {
            val minutes = matcher.group(1)?.toIntOrNull() ?: 0
            val seconds = matcher.group(2)?.toIntOrNull() ?: 0
            val milliseconds = matcher.group(3)?.let {
                if (it.length == 2) it.toIntOrNull()?.times(10) else it.toIntOrNull()
            } ?: 0
            time = (minutes * 60 * 1000 + seconds * 1000 + milliseconds).toLong()
            currentLine = currentLine.substring(matcher.end())
        }

        if (currentLine.isNotEmpty() && time > 0) {
            lines.add(LrcLine(time, currentLine))
        }
    }

    fun getCurrentLineIndex(lines: List<LrcLine>, currentPosition: Long): Int {
        for (i in lines.size - 1 downTo 0) {
            if (currentPosition >= lines[i].time) {
                return i
            }
        }
        return -1
    }
}
