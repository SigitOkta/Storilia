package com.dwarfkit.storilia.pkg.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dwarfkit.storilia.R
import com.dwarfkit.storilia.data.Resource
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.data.local.entity.UserEntity
import com.dwarfkit.storilia.databinding.ActivityLoginBinding
import com.dwarfkit.storilia.pkg.main.MainActivity
import com.dwarfkit.storilia.pkg.signup.SignUpActivity
import com.dwarfkit.storilia.utils.LoadingDialog

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "token")
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val loginViewModel: LoginViewModel by viewModels {
        LoginModelFactory.getInstance(
            UserPreferences.getInstance(dataStore)
        )
    }
    private lateinit var user: UserEntity
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        getUser()
        playAnimation()
        setMyButtonEnable()
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.myButton.setOnClickListener(this)
        binding.tvSignUp.setOnClickListener(this)
    }

    private fun setMyButtonEnable() {
        val resultEmail = binding.etEmail.text
        val resultPass = binding.etPassword.text
        binding.myButton.isEnabled =
            resultEmail != null && resultEmail.toString()
                .isNotEmpty() && isEmailValid(resultEmail.toString())
                    && resultPass != null && resultPass.toString()
                .isNotEmpty() && resultPass.toString().length >= 6
    }

    private fun isEmailValid(s: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(s).matches()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivStoryImg, View.TRANSLATION_X,-30f,30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.llTitle, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val edtPassword = ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.myButton, View.ALPHA, 1f).setDuration(500)
        val tvOrSignup = ObjectAnimator.ofFloat(binding.tvOrSign, View.ALPHA, 1f).setDuration(500)
        val tvSignup = ObjectAnimator.ofFloat(binding.tvSignUp, View.ALPHA, 1f).setDuration(500)

        val together =  AnimatorSet().apply {
            playTogether(tvOrSignup,tvSignup)
        }

        AnimatorSet().apply {
            playSequentially(title,tvEmail,edtEmail,tvPassword,edtPassword,btnLogin,together)
            start()
        }
    }

    private fun getUser() {
        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.my_button -> {
                val resultEmail = binding.etEmail.text
                val resultPass = binding.etPassword.text
                val result = loginViewModel.loginUser(resultEmail.toString(), resultPass.toString())
                result.observe(this) {
                    when (it) {
                        is Resource.Loading -> {
                            LoadingDialog.startLoading(this)
                        }
                        is Resource.Error -> {
                            LoadingDialog.hideLoading()
                            val data = it.error
                            Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Success -> {
                            LoadingDialog.hideLoading()
                            val data = it.data
                            loginViewModel.saveUser(UserEntity(data.loginResult.userId, data.loginResult.name, data.loginResult.token))
                            loginViewModel.login(data.loginResult.token)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
            R.id.tv_sign_up -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}