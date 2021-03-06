package com.dbhong.ch05

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val imageUriList : MutableList<Uri> = mutableListOf()

    val addPhothButton : Button by lazy {
        findViewById(R.id.addPhothButton)
    }

    val startPhotoFrameButton : Button by lazy {
        findViewById(R.id.startPhotoFrameModeButton)
    }

    private val imageViewList : List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            findViewById<ImageView>(R.id.imageView11)
            findViewById<ImageView>(R.id.imageView12)
            findViewById<ImageView>(R.id.imageView13)
            findViewById<ImageView>(R.id.imageView21)
            findViewById<ImageView>(R.id.imageView22)
            findViewById<ImageView>(R.id.imageView23)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhothButton()
        initStartPhothFrameButton()
    }

    private fun initAddPhothButton(){
        addPhothButton.setOnClickListener{
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    //TODO 갤러리에서 사진 선택
                    navigatePhotos()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //TODO true면 교육용 팝업(permission이 왜 필요한지) 확인 후 권한 팝업 띄우기
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    navigatePhotos()
                    //TODO 권한 부여됨
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            //Cancle 등 확인이 아닌 경우
            return
            Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
        }

        when(requestCode) {
            2000 -> {
                val selectedImageUri : Uri? = data?.data

                if(selectedImageUri != null) {

                    if(imageUriList.size == 6) {
                        Toast.makeText(this, "사진이 꽉 찼습니다.", Toast.LENGTH_SHORT).show()
                    }

                    //NUll 체크가 되어있으므로? 가 필요없음
                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri) //lazy 선언문 초기화
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
                }
            } else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT) //SAF 기능을 사용하여 데이터를 얻어옴 Storage Access Framework
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }

    private fun initStartPhothFrameButton() {
        startPhotoFrameButton.setOnClickListener {

        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다")
            .setMessage("전자액자 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기", { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            })
            .setNegativeButton("거부하기", { _, _ ->

            })
            .create()
            .show()
    }
}
