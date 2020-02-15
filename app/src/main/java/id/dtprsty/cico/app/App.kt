package id.dtprsty.cico.app

import android.app.Application
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jacksonandroidnetworking.JacksonParserFactory
import id.dtprsty.cico.session.SharedPref
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class App : Application() {

    private lateinit var pref: SharedPref

    companion object {
        lateinit var mInstance: App
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(StethoInterceptor())
            .build()

        AndroidNetworking.setParserFactory(JacksonParserFactory())
        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY)
        AndroidNetworking.setConnectionQualityChangeListener { currentConnectionQuality, currentBandwidth ->
            Log.d(
                "App",
                "onChange: currentConnectionQuality : \$currentConnectionQuality currentBandwidth : \$currentBandwidth"
            )
        }
        AndroidNetworking.initialize(applicationContext, okHttpClient)
    }

    fun getPrefManager(): SharedPref {
        pref = SharedPref(this)
        return pref
    }
}