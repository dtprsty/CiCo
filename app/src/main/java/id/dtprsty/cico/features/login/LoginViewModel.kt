package id.dtprsty.cico.features.login

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
import id.dtprsty.cico.session.SharedPref
import id.dtprsty.cico.utils.ApiEndpoint
import org.json.JSONException
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val TAG = "LoginVM"

    private val isLoginSuccess = MutableLiveData<Boolean>()

    internal fun login(context: Context, username: String, password: String) {
        AndroidNetworking.post(ApiEndpoint.URL_LOGIN)
            .addBodyParameter("username", username)
            .addBodyParameter("password", password)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val manager: SharedPref? = App.mInstance.getPrefManager()
                        manager?.setKey(response.getString("key"))
                        isLoginSuccess.postValue(true)
                    } catch (e: JSONException) {
                        StyleableToast.makeText(
                            context,
                            context.getString(R.string.failed),
                            Toast.LENGTH_LONG,
                            R.style.error_toast
                        ).show()
                        isLoginSuccess.postValue(false)
                    }
                }

                override fun onError(error: ANError) { // handle error
                    if (error.errorCode != 0) {
                        Log.d(TAG, "onError errorCode: " + error.errorCode)
                        Log.d(TAG, "onError errorBody: " + error.errorBody)
                    } else {
                        Log.d(TAG, "onError errorDetail: " + error.errorDetail)
                    }

                    StyleableToast.makeText(
                        context,
                        context.getString(R.string.error_network),
                        Toast.LENGTH_LONG,
                        R.style.error_toast
                    ).show()
                    isLoginSuccess.postValue(false)
                }

            })
    }

    internal fun getLoginStatus(): LiveData<Boolean> {
        return isLoginSuccess
    }
}