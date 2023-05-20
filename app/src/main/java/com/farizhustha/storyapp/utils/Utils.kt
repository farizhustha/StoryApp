package com.farizhustha.storyapp.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.view.inputmethod.EditorInfo
import com.farizhustha.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class Utils {
    companion object {
        private const val FILENAME_FORMAT = "dd-MMM-yyyy"
        private const val MAXIMAL_SIZE = 1000000

        private val timeStamp: String = SimpleDateFormat(
            FILENAME_FORMAT, Locale.US
        ).format(System.currentTimeMillis())

        private fun createCustomTempFile(context: Context): File {
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(timeStamp, ".jpg", storageDir)
        }

        fun createFile(application: Application): File {
            val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
                File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
            }

            val outputDirectory =
                if (mediaDir != null && mediaDir.exists()) mediaDir else application.filesDir

            return File(outputDirectory, "$timeStamp.jpg")
        }

        fun rotateFile(file: File, isBackCamera: Boolean = false) {
            val matrix = Matrix()
            val bitmap = BitmapFactory.decodeFile(file.path)
            val rotation = if (isBackCamera) 90f else -90f
            matrix.postRotate(rotation)
            if (!isBackCamera) {
                matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            }
            val result =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
        }

        fun uriToFile(selectedImg: Uri, context: Context): File {
            val contentResolver: ContentResolver = context.contentResolver
            val myFile = createCustomTempFile(context)

            val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
            val outputStream: OutputStream = FileOutputStream(myFile)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()

            return myFile
        }

        fun keyboardClearFocus(editText: TextInputEditText) {
            editText.setOnEditorActionListener { _, actionId: Int, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editText.clearFocus()
                }
                false
            }
        }

        fun formatDate(currentDate: String): String? {
            val currentFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val targetFormat = "dd MMM yyyy | HH:mm z"
            val timezone = "GMT"
            val currentDf: DateFormat = SimpleDateFormat(currentFormat, Locale.getDefault())
            currentDf.timeZone = TimeZone.getTimeZone(timezone)
            val targetDf: DateFormat = SimpleDateFormat(targetFormat, Locale.getDefault())
            var targetDate: String? = null
            try {
                val date = currentDf.parse(currentDate)
                if (date != null) {
                    targetDate = targetDf.format(date)
                }
            } catch (ex: ParseException) {
                ex.printStackTrace()
            }
            return targetDate
        }

        fun reduceFileImage(file: File): File {
            val bitmap = BitmapFactory.decodeFile(file.path)
            var compressQuality = 100
            var streamLength: Int
            do {
                val bmpStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                compressQuality -= 5
            } while (streamLength > MAXIMAL_SIZE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
            return file
        }

        suspend fun downloadImage(url: String): Bitmap? =
            withContext(Dispatchers.IO) {
                var bmp: Bitmap? = null
                try {
                    val stream = URL(url).openStream()
                    bmp = BitmapFactory.decodeStream(stream)
                    stream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                bmp
            }
    }
}