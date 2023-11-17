package com.kt_media.ui.images.show_image

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.kt_media.databinding.ActivityShowImageBinding
import com.kt_media.domain.constant.NAME_INTENT_ALBUM_ID
import com.kt_media.domain.constant.TITLE_DOWNLOAD_SUCCESS
import com.mymusic.ui.adapters.ImageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID


class ShowImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowImageBinding
    private lateinit var imageAdapter: ImageAdapter

    private val showImageViewModel: ShowImageViewModel by viewModel()

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

    private fun downloadImage(url: String) {
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
    private fun saveImageToDownload(bitmap: Bitmap) {
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

        val albumId=intent.getIntExtra(NAME_INTENT_ALBUM_ID, 0)
        imageAdapter = ImageAdapter()
        getNameAlbumAndImageList(albumId)
        binding.viewPager2Sia.adapter = imageAdapter
        binding.ivBackSia.setOnClickListener {
            finish()
        }
        binding.ivDownloadSia.setOnClickListener {
            val position=binding.viewPager2Sia.currentItem
            val link= showImageViewModel.imageList.value?.get(position)
            if (link != null) {
                verifyStoragePermissions(this,link)
            }
        }
    }

    private fun getNameAlbumAndImageList(albumId:Int) {
        showImageViewModel.getNameAlbumAndImageList(albumId)
        showImageViewModel.imageList.observe(this, Observer { imageList ->
            if (imageList?.isNotEmpty() == true) {
                imageAdapter.submit(imageList)
            }
        })
        showImageViewModel.nameAlbum.observe(this,Observer{nameAlbum->
            binding.tvNameAlbumSia.text=nameAlbum
        })
    }

}