package hirata.shigeyoshi.techacademy.autoslideshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler
import java.util.*
import android.database.Cursor
import android.view.View


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0

    private var mHandler = Handler()

    var cursor: Cursor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Andriod 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
                rwd_button.setOnClickListener(this)
                fwd_button.setOnClickListener(this)
                play_button.setOnClickListener(this)

            } else {
                // 許可されてないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }

        // Android 5系以下の場合
        } else {
            getContentsInfo()
            rwd_button.setOnClickListener(this)
            fwd_button.setOnClickListener(this)
            play_button.setOnClickListener(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                    rwd_button.setOnClickListener(this)
                    fwd_button.setOnClickListener(this)
                    play_button.setOnClickListener(this)
                }
        }
    }


    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        this.cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null,   // 項目null = 全項目)
            null,   // フィルタ条件(null = フィルタなし
            null,   // フィルタ用パラメータ
            null    // ソート (null なし)
        )
        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id =cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            Log.d("ANDROID", "URI : " + imageUri.toString())
            imageView.setImageURI(imageUri)

        } else {
            // cursor.close()
        }

    }




    override fun onClick(v: View) {
        if (v.id == R.id.rwd_button) {
                if (cursor!!.moveToPrevious()) {
                    cursor!!.moveToPrevious()
                } else {
                    cursor!!.moveToLast()
                }

                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id =cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                Log.d("ANDROID", "URI : " + imageUri.toString())
                imageView.setImageURI(imageUri)
        } else if (v.id == R.id.fwd_button) {
            if (cursor!!.moveToNext()) {
                cursor!!.moveToNext()
            } else {
                cursor!!.moveToFirst()
            }
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id =cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)

        } else if (v.id == R.id.play_button) {
                if (mTimer == null) {
                    play_button.text = "停止"
                    rwd_button.setEnabled(false)
                    fwd_button.setEnabled(false)
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask()  {
                        override fun run() {
                            mTimerSec += 2.0
                            mHandler.post {
                                if (cursor!!.moveToNext()) {
                                    cursor!!.moveToNext()
                                } else {
                                    cursor!!.moveToFirst()
                                }
                                // indexからIDを取得し、そのIDから画像のURIを取得する
                                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                val id =cursor!!.getLong(fieldIndex)
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                imageView.setImageURI(imageUri)
                            }
                        }
                    }, 2000, 2000)
                } else {
                    mTimer!!.cancel()
                    mTimer = null
                    play_button.text = "再生"
                    rwd_button.setEnabled(true)
                    fwd_button.setEnabled(true)
                }

        }
    }

    override fun onDestroy() {
        cursor!!.close()
        super.onDestroy()
    }
}
