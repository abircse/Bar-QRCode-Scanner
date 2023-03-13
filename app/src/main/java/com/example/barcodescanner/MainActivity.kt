package com.example.barcodescanner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.example.barcodescanner.databinding.ActivityMainBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner
    var isBackCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        /** Initialize Scanner **/
        codeScanner = CodeScanner(this, binding.scannerView)

        /** Open scanner if camera permission allowed **/
        if (hasCameraPermission()) {
            codeScanner.decodeCallback = DecodeCallback { scannerResult ->
                runOnUiThread {
                    binding.tvScanResult.text = scannerResult.text.toString()
                }
            }
        } else {
            requestCameraPermission()
        }

        /** ReScan enable button **/
        binding.iconRescan.setOnClickListener {
            binding.tvScanResult.text = "-------"
            codeScanner.startPreview()
        }

        /** Camera switcher **/
        binding.iconCameraSwitch.setOnClickListener {
            if (isBackCamera){
                codeScanner.camera = CodeScanner.CAMERA_BACK
                isBackCamera = false
            }
            else{
                codeScanner.camera = CodeScanner.CAMERA_FRONT // or CAMERA_FRONT or specific camera id
                isBackCamera = true
            }
        }
    }

    /**
     * Check Camera Permission
     * @sample Boolean check
     * **/
    private fun hasCameraPermission() =
        EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)

    /**
     * Request for Camera permission using Easy Permission
     *
     * **/
    private fun requestCameraPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This Scanner features can't work without camera permission",
            101,
            android.Manifest.permission.CAMERA
        )
    }

    /**
     * On Request Permission result
     *
     * **/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * On Permission for Camera If granted callback by Easy Permission
     *
     * **/
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "Camera Access Permission Granted", Toast.LENGTH_SHORT).show()
    }

    /**
     * On Permission for Camera If denied callback by Easy Permission
     *
     * **/
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionDenied(this, perms.first())) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestCameraPermission()
        }
    }

    /**
     * Handle Scanner with LifeCycle
     *
     * **/
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

}