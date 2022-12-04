package com.example.zshackathon

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.zshackathon.databinding.ActivityDoctorAccessBinding
import com.example.zshackathon.phoneNumberLogin.OtpSendActivity
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DoctorAccessActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityDoctorAccessBinding
    private val TAG = "DoctorAccessActivity"
    private val db = Firebase.firestore
    private var data = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorAccessBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val uri: Uri? = intent.data

        if (uri != null) {
            val parameters: List<String> = uri.getPathSegments()

            val phoneNumber = parameters[parameters.size - 1]

            db.collection("Info")
                .whereEqualTo("phoneNumber", phoneNumber.trim())
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    for (doc in value!!) {
                        doc.getString("Info")?.let {
                            data.add(it)
                            Log.d(TAG, it)
                        }
                    }
                    Log.d(TAG, "Info of user $phoneNumber: $data")
                    binding.tvPatientHistory.setText(data[data.size - 1].toString())
                }

            val etInfo = binding.etAdd2PatientHistory.text
            binding.btnAddInfo.setOnClickListener {
                db.collection("Info")
                    .whereEqualTo("phoneNumber", phoneNumber.trim())
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            var info = document.data["Info"].toString()
                            db.collection("Info").document(document.id)
                                .update("Info", info + etInfo)
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error updating document", e)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            }
        }
    }
}