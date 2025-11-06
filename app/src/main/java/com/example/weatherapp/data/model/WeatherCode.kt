package com.example.weatherapp.data.model

/**
 * 天氣代碼對應的描述文字
 */
object WeatherCode {
    fun getDescription(code: Int): String {
        return when (code) {
            0 -> "晴朗"
            1, 2, 3 -> "多雲"
            45, 48 -> "霧"
            51, 53, 55 -> "毛毛雨"
            56, 57 -> "凍毛毛雨"
            61, 63, 65 -> "雨"
            66, 67 -> "凍雨"
            71, 73, 75 -> "雪"
            77 -> "雪粒"
            80, 81, 82 -> "驟雨"
            85, 86 -> "雪暴"
            95 -> "雷暴"
            96, 99 -> "雷暴伴冰雹"
            else -> "未知"
        }
    }
}

