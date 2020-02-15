package id.dtprsty.cico.features.loading

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import id.dtprsty.cico.R
import id.dtprsty.cico.app.App
import id.dtprsty.cico.features.main.MainActivity
import id.dtprsty.cico.models.ClockTime
import kotlinx.android.synthetic.main.activity_loading.*

class LoadingActivity : AppCompatActivity() {

    private lateinit var viewModel: ClockViewModel
    private lateinit var clockTimeObj: ClockTime
    private val mHandler = Handler()
    private lateinit var runnable: Runnable

    companion object {
        val REQUESTCODE: Int = 101
    }

    private val manager = App.mInstance.getPrefManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ClockViewModel::class.java)

        if (manager.getIsClockedIn() == true) {
            tvClock.text = "Clocking out"
        } else {
            tvClock.text = "Clocking in"
        }

        runProgressDeterminate()

        viewModel.getRequestStatus().observe(this, Observer { isRequestSuccess ->
            if (isRequestSuccess) {
                viewModel.getClockTime().observe(this, Observer {
                    clockTimeObj = it
                    val intent = Intent()
                    intent.putExtra(MainActivity.CLOCKEDINTIME, clockTimeObj.clockInTime)
                    intent.putExtra(MainActivity.CLOCKEDOUTTIME, clockTimeObj.clockOutTime)
                    setResult(Activity.RESULT_OK, intent)
                })
            }
            finish()

            mHandler.removeCallbacks(runnable)
        })

        tvCancel.setOnClickListener {
            finish()
            mHandler.removeCallbacks(runnable)
        }
    }

    private fun runProgressDeterminate() {
        runnable = object : Runnable {
            override fun run() {
                val progress = progressBar.progress + 10
                progressBar.progress = progress
                if (progress > 100) {
                    progressBar.progress = 0
                    viewModel.getData(applicationContext)
                }

                mHandler.postDelayed(this, 1000)
            }
        }
        mHandler.post(runnable)
    }

}
