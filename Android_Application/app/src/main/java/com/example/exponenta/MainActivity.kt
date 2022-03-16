package com.example.exponenta

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.exponenta.databinding.ActivityMainBinding
import com.example.exponenta.ml.Model
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.security.Permission
import java.util.jar.Manifest
import kotlin.reflect.typeOf

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var bitmap: Bitmap

    lateinit var bindingClass: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        bindingClass.btPredict.visibility = View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()

        val filename = "labels.txt"
        val inputString = application.assets.open(filename).bufferedReader().use { it.readText() }
        var ListOfLabels = inputString.split("\n")


        bindingClass.btCamera.setOnClickListener {
            cameraCheckPermission()
        }

        bindingClass.btDownload.setOnClickListener {
            galleryCheckPermission()
        }

        bindingClass.imageView.setOnClickListener {
            val pictureDialogWindow = AlertDialog.Builder(this)
            pictureDialogWindow.setTitle(resources.getString(R.string.activity_selector))
            val pictureDialogItem = arrayOf(
                resources.getString(R.string.select_1),
                resources.getString(R.string.select_2)
                )
            pictureDialogWindow.setItems(pictureDialogItem) {dialog, which ->

                when(which) {
                    0 -> gallery()
                    1 -> camera()
                }
            }
            pictureDialogWindow.show()
        }

        bindingClass.btPredict.setOnClickListener {

                // changing size of image
                var resized_bitmap: Bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    Constant.X_SIZE, Constant.Y_SIZE, true
                )

                // download model from assets
                val model = Model.newInstance(this)
                //

                // setting up fixed image size
                val inputFeatureTensor = TensorBuffer.createFixedSize(
                    intArrayOf(Constant.BATCH, Constant.X_SIZE, Constant.Y_SIZE, Constant.CHANNEL),
                    DataType.UINT8
                )
                //

                // transform Bitmap to tensor before sending to Model
                // and after to bitBuffer
                var tensorBuffer = TensorImage.fromBitmap(resized_bitmap)
                var byteBuffer = tensorBuffer.buffer
                inputFeatureTensor.loadBuffer(byteBuffer)
                //

                //Evaluate
                val output = model.process(inputFeatureTensor)
                val outputFeature = output.outputFeature0AsTensorBuffer.floatArray
                //

                var maxIndexAndValueInOutput = MaxIndex()
                var predictedLabel = maxIndexAndValueInOutput.findMaxIndex(outputFeature)
                //Show prediction, where we get value with index = 10
                var textInfo = "Value: ${ListOfLabels[predictedLabel]}"
                bindingClass.tvMessage.text = textInfo

                model.close()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {

            when(requestCode) {

                Constant.REQUESTED_NUMBER_1 -> {

                    var uri: Uri = data?.data!!
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    bindingClass.imageView.load(data?.data) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(RoundedCornersTransformation(10f))
                    }
                    bindingClass.btPredict.visibility = View.VISIBLE
                }

                Constant.CAMERA_REQUEST_CODE -> {

                    bitmap = data?.extras?.get("data") as Bitmap
                    bindingClass.imageView.load(bitmap){
                        crossfade(true)
                        crossfade(1000)
                        transformations(RoundedCornersTransformation(10f))
                    }
                    bindingClass.btPredict.visibility = View.VISIBLE
                }

                else -> {
                    bindingClass.tvMessage.text = resources.getString(R.string.not_found_image)
                }
            }
        }
    }
    private fun galleryCheckPermission() {
        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.denied_permission_storage),
                    Toast.LENGTH_SHORT
                ).show()
                showRationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun cameraCheckPermission() {

        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA).withListener(

                object: MultiplePermissionsListener {

                    override fun onPermissionsChecked(
                        report: MultiplePermissionsReport?) {

                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?) {
                            showRationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
    }

    private fun camera() {
        var intent_camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent_camera, Constant.CAMERA_REQUEST_CODE)

    }

    private  fun gallery() {
        var intent_storage = Intent(Intent.ACTION_PICK)
        intent_storage.type = "image/*"
        startActivityForResult(intent_storage, Constant.REQUESTED_NUMBER_1)
    }

    private  fun showRationalDialogForPermission() {
        AlertDialog.Builder(this).
            setMessage(resources.getString(R.string.alert_permission))
            .setPositiveButton(resources.getString(R.string.positive_button)) {_,_ ->
                try {
                    val intent_action = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent_action.data = uri
                    startActivity(intent_action)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(resources.getString(R.string.negative_button)){dialog, _->
                dialog.dismiss()
            }.show()
    }
}