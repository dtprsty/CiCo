package id.dtprsty.cico.features.main

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import id.dtprsty.cico.R
import id.dtprsty.cico.app.App
import id.dtprsty.cico.features.loading.LoadingActivity
import id.dtprsty.cico.models.Job
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivityForResult


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var jobObj: Job
    private val manager = App.mInstance.getPrefManager()

    companion object {
        val CLOCKEDINTIME: String = "clocked_in_time"
        val CLOCKEDOUTTIME: String = "clocked_out_time"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        showLoading(true)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        viewModel.getData(this)

        viewModel.getRequestStatus().observe(this, Observer { isRequestSuccess ->
            if (isRequestSuccess) {
                viewModel.getJob().observe(this, Observer {
                    jobObj = it
                    initData()
                })
            } else {
                content.visibility = View.GONE
            }
            showLoading(false)
        })

        if (manager.getIsClockedIn() == true) {
            ivClock.setImageResource(R.drawable.clock_out)
        } else {
            ivClock.setImageResource(R.drawable.clock_in)
        }

        ivClock.setOnClickListener {
            startActivityForResult<LoadingActivity>(LoadingActivity.REQUESTCODE)
        }

        tvManagerPhone.paintFlags = tvManagerPhone.paintFlags or Paint.UNDERLINE_TEXT_FLAG

    }

    private fun initData() {
        tvPositionName.text = jobObj.positionName
        tvClientName.text = jobObj.clientName
        tvStreetName.text = jobObj.streetName
        tvManagerName.text = jobObj.managerName
        tvManagerPhone.text = jobObj.managerPhone
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            spinKit.visibility = View.VISIBLE
            content.alpha = 0.2f
        } else {
            spinKit.visibility = View.GONE
            content.alpha = 1.0f
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoadingActivity.REQUESTCODE && resultCode == RESULT_OK) {
            if (data?.getStringExtra(CLOCKEDINTIME) != null) {
                tvClockIn.text = data?.getStringExtra(CLOCKEDINTIME)
            }
            if (data?.getStringExtra(CLOCKEDOUTTIME) != null) {
                tvClockOut.text = data?.getStringExtra(CLOCKEDOUTTIME)
            }

            if (manager.getIsClockedIn() == true) {
                ivClock.setImageResource(R.drawable.clock_out)
            } else {
                ivClock.setImageResource(R.drawable.clock_in)
            }

            if (data?.getStringExtra(CLOCKEDINTIME) != null && data?.getStringExtra(CLOCKEDOUTTIME) != null) {
                ivClock.visibility = View.GONE
            }
        }
    }
}
