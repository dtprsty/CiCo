package id.dtprsty.cico.features.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import id.dtprsty.cico.R
import id.dtprsty.cico.app.App
import id.dtprsty.cico.features.main.MainActivity
import id.dtprsty.cico.session.SharedPref
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {
        val manager: SharedPref? = App.mInstance.getPrefManager()
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(LoginViewModel::class.java)

        if (manager?.getKey() != null) {
            startActivity<MainActivity>()
        }

        btnLogin.setOnClickListener {
            showLoading(true)
            viewModel.login(this, tvUsername.text.toString(), tvPassword.text.toString())
        }

        viewModel.getLoginStatus().observe(this, Observer { isLoginSuccess ->
            if (isLoginSuccess) {
                startActivity<MainActivity>()
                finish()
                showLoading(false)
            } else {
                showLoading(false)
            }
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
            btnLogin.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
        }
    }
}
