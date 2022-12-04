package com.example.zshackathon

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.zshackathon.databinding.ActivityMainBinding
import com.example.zshackathon.phoneNumberLogin.OtpSendActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mAuth = FirebaseAuth.getInstance()
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val user = mAuth.currentUser
        if (user == null) {
            val intent = Intent(this, OtpSendActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this@MainActivity, OtpSendActivity::class.java)
            startActivity(intent)
        }

        binding.btnGenerateQR.setOnClickListener { getQRCodeOptionsAlertDialog() }
        binding.btnScanQR.setOnClickListener { scanQRCode() }
    }

    fun getQRCodeOptionsAlertDialog() {
        val getTwoChoiceDialogView = LayoutInflater.from(this).inflate(R.layout.share_profile_option_btns,null)
        getTwoChoiceDialogView.findViewById<Button>(R.id.btn1).text = "Doctor"
        getTwoChoiceDialogView.findViewById<Button>(R.id.btn2).text = "Diagnostic Center"
        val builder = AlertDialog.Builder(this).setView(getTwoChoiceDialogView).setTitle("Show QR code to: ")
        val alertDialog = builder.show()
        getTwoChoiceDialogView.findViewById<Button>(R.id.btn1).setOnClickListener {
            generateQRCode("Doctor")
            alertDialog.dismiss()
        }
        getTwoChoiceDialogView.findViewById<Button>(R.id.btn2).setOnClickListener {
            generateQRCode("Diagnostic Center")
            alertDialog.dismiss()
        }
    }

    fun generateQRCode(useCase: String) {
        //Get the phone number of the user logged in
        val user = mAuth.currentUser
        val phoneNumber: String
        user.let {
            phoneNumber = user?.phoneNumber.toString()
        }

        var Url: String = ""
        if (useCase == "Doctor")
            Url = "https://www.zshealthdoctor.com/" + phoneNumber.trim()
        if (useCase == "Diagnostic Center")
            Url = "https://www.zshealthdiagnosticcenter.com/" + phoneNumber.trim()

        //Generate QR image using the above url as input text:
        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(Url, BarcodeFormat.QR_CODE, 400, 400)

        //Dialog box to display QR code
        val getQRCodeDialogView = LayoutInflater.from(this).inflate(R.layout.qrcode_image_view, null)
        val builder = AlertDialog.Builder(this).setView(getQRCodeDialogView).setTitle("Scan below QR code:")
        getQRCodeDialogView.findViewById<ImageView>(R.id.iv1).setImageBitmap(bitmap)
        val alertDialog = builder.show()
    }

    fun askPermissions() {
        val list = listOf<String>(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this,list,PermissionsRequestCode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            managePermissions.checkPermissions()
    }

    fun scanQRCode() {
        askPermissions()
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setDesiredBarcodeFormats(listOf(IntentIntegrator.QR_CODE))
        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val res = IntentIntegrator.parseActivityResult(resultCode,data)
        if (res != null) {
            AlertDialog.Builder(this).setMessage("Would you like to go to ${res.contents}?")
                .setPositiveButton("Accept", DialogInterface.OnClickListener{ _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.contents))
                    startActivity(intent)
                })
                .setNegativeButton("Deny", DialogInterface.OnClickListener{ dialogInterface, i ->  })
                .create()
                .show()
        }
    }
}
