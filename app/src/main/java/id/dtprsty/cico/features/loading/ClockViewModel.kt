package id.dtprsty.cico.features.loading

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.muddzdev.styleabletoast.StyleableToast
import id.dtprsty.cico.R
import id.dtprsty.cico.app.App
import id.dtprsty.cico.models.ClockTime
import id.dtprsty.cico.session.SharedPref
import id.dtprsty.cico.utils.ApiEndpoint
import id.dtprsty.cico.utils.TimeConverter
import org.json.JSONException
import org.json.JSONObject

class ClockViewModel : ViewModel() {

    private val TAG = "clockVM"
    private val isRequestSuccess = MutableLiveData<Boolean>()
    private val clockTime = MutableLiveData<ClockTime>()

    private val manager = App.mInstance.getPrefManager()

    internal fun getData(context: Context) {
        var url =
            if (manager?.getIsClockedIn() == false) ApiEndpoint.URL_CLOCKED_IN else ApiEndpoint.URL_CLOCKED_OUT
        val manager: SharedPref? = App.mInstance.getPrefManager()
        AndroidNetworking.post(url)
            .setPriority(Priority.LOW)
            .addBodyParameter("latitude", "-6.2446691")
            .addBodyParameter("longitude", "106.8779625")
            .addHeaders("Authorization", "token ${manager?.getKey()}")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) { // do anything with response
                    try {
                        val clockTimeObj = ClockTime()

                        if (url == ApiEndpoint.URL_CLOCKED_IN) {
                            manager?.setIsClockedIn(true)
                            clockTimeObj.apply {
                                clockInTime =
                                    TimeConverter.dateFromIso8601(response.getString("clock_in_time"))
                                        .toString()
                            }
                        } else {
                            manager?.setIsClockedIn(false)
                            clockTimeObj.apply {
                                clockInTime = TimeConverter.dateFromIso8601(
                                    response.getJSONObject("timesheet").getString("clock_in_time")
                                ).toString()
                                clockOutTime = TimeConverter.dateFromIso8601(
                                    response.getJSONObject("timesheet").getString("clock_out_time")
                                ).toString()
                            }
                        }

                        clockTime.postValue(clockTimeObj)
                        isRequestSuccess.postValue(true)
                    } catch (e: JSONException) {
                        Log.d(TAG, "error: ${e.message}")
                        StyleableToast.makeText(
                            context,
                            context.getString(R.string.failed),
                            Toast.LENGTH_LONG,
                            R.style.error_toast
                        ).show()
                        isRequestSuccess.postValue(false)
                    }
                }

                override fun onError(error: ANError) { // handle error
                    if (error.errorCode != 0) {
                        Log.d(TAG, "onError errorCode: " + error.errorCode)
                        Log.d(TAG, "onError errorBody: " + error.errorBody)
                    } else {
                        Log.d(TAG, "onError errorDetail: " + error.errorDetail)
                    }
                    val jsonObj = JSONObject(error.errorBody)
                    if (error.errorCode != 400) {
                        StyleableToast.makeText(
                            context,
                            context.getString(R.string.error_network),
                            Toast.LENGTH_LONG,
                            R.style.error_toast
                        ).show()
                    } else {
                        if (manager?.getIsClockedIn() == false) {  // means partner already clocked in
                            manager?.setIsClockedIn(true)
                        } else if (manager?.getIsClockedIn() == true) { //means partner already clocked out
                            manager?.setIsClockedIn(false)
                        }
                        StyleableToast.makeText(
                            context,
                            jsonObj.getString("detail"),
                            Toast.LENGTH_LONG,
                            R.style.error_toast
                        ).show()
                    }

                    isRequestSuccess.postValue(false)
                }
            })
    }

    internal fun getClockTime(): LiveData<ClockTime> {
        return clockTime
    }

    internal fun getRequestStatus(): LiveData<Boolean> {
        return isRequestSuccess
    }
}