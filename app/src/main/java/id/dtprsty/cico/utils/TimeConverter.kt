package id.dtprsty.cico.utils

import com.google.gson.internal.bind.util.ISO8601Utils
import java.text.ParsePosition
import java.text.SimpleDateFormat

object TimeConverter {

    fun dateFromIso8601(dateTime: String): String? {
        val date = ISO8601Utils.parse(dateTime, ParsePosition(0))
        val format = SimpleDateFormat("hh:mm aa")
        return format.format(date)
    }
}