package fr.event.eventify.utils

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.github.dhaval2404.imagepicker.ImagePicker



object ImageDialog {
    fun takePicture(launcher: ActivityResultLauncher<Intent>, context: Activity, maxSize: Int = 640, maxMo: Float = 0.5f, ratioX: Float = 16f, ratioY: Float = 16f) {
        ImagePicker.with(context)
            .crop(ratioX, ratioY)
            .compress((maxMo * 1024).toInt())
            .maxResultSize(maxSize, maxSize)
            .createIntent { intent ->
                launcher.launch(intent)
            }
    }

}