package com.example.zshackathon.phoneNumberLogin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zshackathon.MainActivity
import com.example.zshackathon.databinding.ActivityOtpVerifyBinding
import com.example.zshackathon.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OtpVerifyActivity : AppCompatActivity() {

    private var binding: ActivityOtpVerifyBinding? = null
    private var verificationId: String? = null
    private val TAG = "OtpVerifyActivity"
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())

        editTextInput()

        binding!!.tvMobile.text = String.format(
            "+91-%s", intent.getStringExtra("phone")
        )

        verificationId = intent.getStringExtra("verificationId")

        binding!!.tvResendBtn.setOnClickListener {
            Toast.makeText(
                this@OtpVerifyActivity,
                "OTP Send Successfully.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding!!.btnVerify.setOnClickListener {
            binding!!.progressBarVerify.visibility = View.VISIBLE
            binding!!.btnVerify.visibility = View.INVISIBLE
            if (binding!!.etC1.text.toString().trim().isEmpty() ||
                binding!!.etC2.text.toString().trim().isEmpty() ||
                binding!!.etC3.text.toString().trim().isEmpty() ||
                binding!!.etC4.text.toString().trim().isEmpty() ||
                binding!!.etC5.text.toString().trim().isEmpty() ||
                binding!!.etC6.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(this@OtpVerifyActivity, "OTP is not Valid!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (verificationId != null) {
                    val code = binding!!.etC1.text.toString().trim() +
                            binding!!.etC2.text.toString().trim() +
                            binding!!.etC3.text.toString().trim() +
                            binding!!.etC4.text.toString().trim() +
                            binding!!.etC5.text.toString().trim() +
                            binding!!.etC6.text.toString().trim()
                    val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                    FirebaseAuth
                        .getInstance()
                        .signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                binding!!.progressBarVerify.visibility = View.VISIBLE
                                binding!!.btnVerify.visibility = View.INVISIBLE
                                val user = User("+91"+intent.getStringExtra("phone"))
                                db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding document", e)
                                    }



                                Toast.makeText(this@OtpVerifyActivity, "Welcome...", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@OtpVerifyActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)

                            } else {
                                binding!!.progressBarVerify.visibility = View.GONE
                                binding!!.btnVerify.visibility = View.VISIBLE
                                Toast.makeText(
                                    this@OtpVerifyActivity,
                                    "OTP is not Valid!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun editTextInput() {
        binding!!.etC1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding!!.etC2.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.etC2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding!!.etC3.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.etC3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding!!.etC4.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.etC4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding!!.etC5.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.etC5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding!!.etC6.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
}