package com.example.zshackathon

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zshackathon.databinding.ActivityDiagnosticCenterAccessBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DiagnosticCenterAccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiagnosticCenterAccessBinding
    private val TAG = "DiagnosticCenterAccess"
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnosticCenterAccessBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val uri: Uri? = intent.data

        if (uri != null) {
            val parameters: List<String> = uri.getPathSegments()

            val phoneNumber = parameters[parameters.size - 1]
            Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show()

            val etInfo = binding.etAddToPatientHistory.text
            binding.btnAddRecord.setOnClickListener {
                db.collection("Info")
                    .whereEqualTo("phoneNumber", phoneNumber.trim())
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            var info = document.data["Info"].toString()
                            Toast.makeText(this, info+etInfo, Toast.LENGTH_SHORT).show()
                            db.collection("Info").document(document.id)
                                .update("Info", info + etInfo)
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                                    Toast.makeText(this, "Information updated successfully", Toast.LENGTH_SHORT).show()
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