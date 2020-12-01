/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage.sample.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLookupFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUWobbleFilter
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools.FilterAdjuster
import jp.co.cyberagent.android.gpuimage.sample.R
import kotlinx.android.synthetic.main.activity_gallery.*

class GalleryActivity : AppCompatActivity() {

    private var filterAdjuster: FilterAdjuster? = null
    private var isFilter: GPUImageFilter? = null
    private var listFilter: ArrayList<GPUImageFilter> = arrayListOf()
    private val gpuImageView: GPUImageView by lazy {
        findViewById<GPUImageView>(R.id.gpuimage)
    }
    private val seekBar: SeekBar by lazy { findViewById<SeekBar>(R.id.seekBar) }
    private var selectFilter = false
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        //  gpuImageView.setRatio(1f/1)

            Handler().postDelayed(Runnable {
                Log.d("namtime", "timeFilter: ${System.currentTimeMillis()}")
                if (isFilter!=null&&isFilter is GPUWobbleFilter) {
                    Log.d("namtime", "timeFilter: ${System.currentTimeMillis()}")
                    (isFilter as GPUWobbleFilter).getOff()
                    gpuImageView.requestRender()
                }
            }, 20)


        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                filterAdjuster?.adjust(progress)
                gpuImageView.requestRender()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBar2.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isFilter is jp.co.cyberagent.android.gpuimage.filter.GPUImageOverlayBlendFilter) {
                    (isFilter as jp.co.cyberagent.android.gpuimage.filter.GPUImageOverlayBlendFilter).setAlpha(progress.toFloat() / 100)
                    gpuImageView.requestRender()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBar3.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isFilter is jp.co.cyberagent.android.gpuimage.filter.GPUImageOverlayBlendFilter) {
                    (isFilter as jp.co.cyberagent.android.gpuimage.filter.GPUImageOverlayBlendFilter).setMultiply(progress.toFloat() / 100)
                    gpuImageView.requestRender()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        findViewById<View>(R.id.button_choose_filter).setOnClickListener {
            GPUImageFilterTools.showDialog(this) { gpuImageFilter: GPUImageFilter, filterType: GPUImageFilterTools.FilterType ->
                switchFilterTo(gpuImageFilter, filterType)
                gpuImageView.requestRender()
            }
        }
        findViewById<View>(R.id.button_save).setOnClickListener { saveImage() }

        startPhotoPicker()
        custom_filter.setOnClickListener {
            selectFilter = true
            startPhotoPicker()
        }
        GPUImageFilterTools.setRecyclerView(this, rv_filter) { filter ->
            switchFilterTo(filter)
            gpuImageView.requestRender()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_IMAGE -> if (resultCode == RESULT_OK) {
                if (selectFilter) {

                    try {
                        val imageUri = data!!.data;
                        val bitmap1 = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri);
                        val filter = GPUImageLookupFilter().apply {
                            bitmap = bitmap1
                        }
                        switchFilterTo(filter)
                        gpuImageView.requestRender()
                    } catch (e: Exception) {
                        Toast.makeText(applicationContext, "không sử dụng được filter này", Toast.LENGTH_LONG).show()
                    }
                } else {
                    gpuImageView.setImage(data!!.data)
                }

            } else {
                finish()
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startPhotoPicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE)
    }

    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        gpuImageView.saveToPictures("GPUImage", fileName) { uri ->
            Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchFilterTo(filter: GPUImageFilter, filterType: GPUImageFilterTools.FilterType? = null) {
        var time = System.currentTimeMillis()
//        var bitmap = gpuImageView.capture()
//        gpuImageView.setImage(bitmap)
        isFilter = filter
        listFilter.add(filter)
        Log.d("namtime", "timeGetSetImage: ${System.currentTimeMillis() - time}")
        if (gpuImageView.filter == null || gpuImageView.filter.javaClass != filter.javaClass) {
            gpuImageView.filter = filter
            //GPUImageFilterGroup(listFilter)
            filterAdjuster = FilterAdjuster(filter)
            if (filterAdjuster!!.canAdjust()) {
                seekBar.visibility = View.VISIBLE
                filterAdjuster!!.adjust(seekBar.progress)
            } else {
                seekBar.visibility = View.GONE
            }
        }


    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }
}
