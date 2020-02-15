package id.dtprsty.cico.features.main

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
import id.dtprsty.cico.models.Job
import id.dtprsty.cico.utils.ApiEndpoint
import org.json.JSONException
import org.json.JSONObject

class MainViewModel : ViewModel() {
    private val TAG = "MainVM"

    private val jobData = MutableLiveData<Job>()

    private val isRequestSuccess = MutableLiveData<Boolean>()

    internal fun getData(context: Context) {
        AndroidNetworking.get(ApiEndpoint.URL_GET_JOB)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val jobObj = Job()
                        jobObj.apply {
                            positionName = response.getJSONObject("position").getString("name")
                            clientName = response.getJSONObject("client").getString("name")
                            streetName = response.getJSONObject("location").getJSONObject("address")
                                .getString("street_1")
                            managerName = "Yai Thong-oon"
                            managerPhone = "+66-955-5695-65"
                        }
                        jobData.postValue(jobObj)
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

                    isRequestSuccess.postValue(false)
                    StyleableToast.makeText(
                        context,
                        context.getString(R.string.error_network),
                        Toast.LENGTH_LONG,
                        R.style.error_toast
                    ).show()
                }
            })
    }

    internal fun getJob(): LiveData<Job> {
        return jobData
    }

    internal fun getRequestStatus(): LiveData<Boolean> {
        return isRequestSuccess
    }
}