package com.example.zshackathon.phoneNumberLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zshackathon.R
import com.example.zshackathon.databinding.ActivityOtpSendBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class OtpSendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpSendBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCallbacks: OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpSendBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()
        Log.d(this.toString(), "OtpSendActivity loaded")

        binding.btnSend.setOnClickListener {
            if (binding.etPhone.text.toString().trim().isEmpty()) {
                Toast.makeText(this@OtpSendActivity, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
            } else if (binding.etPhone.getText().toString().trim().length != 10){
                Toast.makeText(this@OtpSendActivity, "Type valid Phone Number", Toast.LENGTH_SHORT).show()
            } else {
                otpSend()
            }
        }
    }


    private fun otpSend() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSend.visibility = View.INVISIBLE

        mCallbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.visibility = View.GONE
                binding.btnSend.visibility = View.VISIBLE
                Toast.makeText(this@OtpSendActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                binding.progressBar.visibility = View.GONE
                binding.btnSend.visibility = View.VISIBLE
                //Toast.makeText( this@OtpSendActivity, "OTP is successfully send.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@OtpSendActivity, OtpVerifyActivity::class.java)
                intent.putExtra("phone", binding.etPhone.text.toString().trim())
                intent.putExtra("verificationId", verificationId)
                startActivity(intent)
            }
        }

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91" + binding.etPhone.text.toString().trim())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}