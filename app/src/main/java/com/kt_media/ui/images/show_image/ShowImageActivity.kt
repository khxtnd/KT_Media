package com.kt_media.ui.images.show_image

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.databinding.ActivityShowImageBinding
import com.kt_media.domain.constant.CHILD_CATEGORY_IMAGE
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_LIST_IMAGE
import com.kt_media.domain.constant.CHILD_NAME
import com.kt_media.domain.constant.NAME_INTENT_CATEGORY_IMAGE_ID
import com.kt_media.domain.constant.TITLE_DOWNLOAD_SUCCESS
import com.kt_media.domain.constant.VAL_REQUEST_CODE
import com.mymusic.ui.adapters.ImageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.UUID


class ShowImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowImageBinding
    private lateinit var adapter: ImageAdapter
    private var imageList = arrayListOf<String>()

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions(activity: Activity, link:String) {
        val permission =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }else{
            downloadImage(link)
        }

    }

    fun downloadImage(url: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream: InputStream = connection.inputStream
                        BitmapFactory.decodeStream(inputStream)
                    } else {
                        null
                    }
                }
                if(bitmap!=null){
                    withContext(Dispatchers.Main) {
                        saveImageToDownload(bitmap)
                        Toast.makeText(this@ShowImageActivity, TITLE_DOWNLOAD_SUCCESS, Toast.LENGTH_SHORT).show()
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun saveImageToDownload(bitmap: Bitmap) {
        val randomFileName = UUID.randomUUID().toString() + ".jpg"

        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val filePath = File(directory, randomFileName).absolutePath

        val file = File(filePath)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idCategoryImage=intent.getIntExtra(NAME_INTENT_CATEGORY_IMAGE_ID, 0)
        adapter = ImageAdapter()
        getAllImageItem(idCategoryImage)
        binding.viewPager2Sia.adapter = adapter
        binding.ivBackSia.setOnClickListener {
            finish()
        }
        binding.ivDownloadSia.setOnClickListener {
            val position=binding.viewPager2Sia.currentItem
            val link=imageList[position]
            verifyStoragePermissions(this,link)
        }
    }

    private fun getAllImageItem(idCategoryImage:Int) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(CHILD_CATEGORY_IMAGE)
        val query =
            databaseReference.orderByChild(CHILD_ID).equalTo(idCategoryImage.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imageList.clear()
                for (data in dataSnapshot.children) {
                    val categoryImage = data.child(CHILD_LIST_IMAGE).children
                    for (imageData in categoryImage) {
                        val imageUrl = imageData.value as String
                        imageList.add(imageUrl)
                    }
                    if(imageList.isNotEmpty()){
                        adapter.submit(imageList)
                    }
                    val categoryName = data.child(CHILD_NAME).value as String
                    binding.tvCategoryImageSia.text=categoryName
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

}