package com.example.seguritas.utils

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import com.example.seguritas.R
import com.example.seguritas.constant.CONTRACT_TEXT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PdfUtils {
    fun generateAndSavePdf(context: Context) {
        val pdfDocument = PdfDocument()
        val pageWidth = 600
        val pageHeight = 1000
        val margin = 50f
        val textYStart = 220f
        val availableWidth = pageWidth - 2 * margin
        val imageSize = 100
        val logoSize = 60
        val imageMargin = 10f

        val titlePaint = createTitlePaint()
        val bodyPaint = createBodyPaint()

        val logoBitmap = decodeSampledBitmapFromResource(
            context.resources,
            R.drawable.logo,
            logoSize,
            logoSize
        )
        val scaledLogo =
            logoBitmap?.let { Bitmap.createScaledBitmap(it, logoSize, logoSize, true) }

        //62.8
        val imageList = listOf(
            R.drawable.img10,
            R.drawable.img10,
            R.drawable.img10,
            R.drawable.img1,
            R.drawable.img1,
            R.drawable.img1,
            R.drawable.img1,
            R.drawable.img1,
            R.drawable.img12,
            R.drawable.img12,
        )

        GlobalScope.launch(Dispatchers.IO) {
            val images = imageList.map { resId ->
                decodeSampledBitmapFromResource(context.resources, resId, imageSize, imageSize)
            }

            withContext(Dispatchers.Main) {
                createPdfDocument(
                    context,
                    pdfDocument,
                    pageWidth,
                    pageHeight,
                    margin,
                    textYStart,
                    availableWidth,
                    imageSize,
                    imageMargin,
                    scaledLogo,
                    images
                )
            }
        }
    }

    private fun createPdfDocument(
        context: Context,
        pdfDocument: PdfDocument,
        pageWidth: Int,
        pageHeight: Int,
        margin: Float,
        textYStart: Float,
        availableWidth: Float,
        imageSize: Int,
        imageMargin: Float,
        scaledLogo: Bitmap?,
        imageList: List<Bitmap?>
    ) {
        var currentPageNumber = 1
        var currentPage = createPdfPage(pdfDocument, pageWidth, pageHeight, currentPageNumber)
        var canvas = currentPage.canvas

        var isTitleDrawn = false

        scaledLogo?.let { drawLogo(canvas, it, pageWidth, margin) }
        if (!isTitleDrawn) {
            drawTitle(canvas, createTitlePaint(), pageWidth)
            isTitleDrawn = true
        }

        var currentTextY = textYStart
        val textLayout = createTextLayout(CONTRACT_TEXT, createBodyPaint(), availableWidth)

        for (i in 0 until textLayout.lineCount) {
            if (isPageFull(currentTextY, createBodyPaint(), pageHeight, margin)) {
                pdfDocument.finishPage(currentPage)
                currentPageNumber++
                currentPage = createPdfPage(pdfDocument, pageWidth, pageHeight, currentPageNumber)
                canvas = currentPage.canvas
                scaledLogo?.let { drawLogo(canvas, it, pageWidth, margin) }
                if (!isTitleDrawn) {
                    drawTitle(canvas, createTitlePaint(), pageWidth)
                    isTitleDrawn = true
                }
                currentTextY = margin
            }

            drawTextLine(
                canvas,
                CONTRACT_TEXT,
                textLayout,
                i,
                createBodyPaint(),
                margin,
                currentTextY
            )
            currentTextY += createBodyPaint().descent() - createBodyPaint().ascent()
        }

        var currentImageY = currentTextY + 20f
        var currentImageX = margin

        for (image in imageList) {
            if (image != null) {
                if (currentImageX + imageSize > availableWidth) {
                    currentImageX = margin
                    currentImageY += imageSize + imageMargin
                    if (currentImageY + imageSize > pageHeight - margin) {
                        pdfDocument.finishPage(currentPage)
                        currentPageNumber++
                        currentPage = createPdfPage(pdfDocument, pageWidth, pageHeight, currentPageNumber)
                        canvas = currentPage.canvas
                        scaledLogo?.let { drawLogo(canvas, it, pageWidth, margin) }
                        if (!isTitleDrawn) {
                            drawTitle(canvas, createTitlePaint(), pageWidth)
                            isTitleDrawn = true
                        }
                        currentImageY = margin
                    }
                }
                val scaledImage = Bitmap.createScaledBitmap(image, imageSize, imageSize, true)
                canvas.drawBitmap(scaledImage, currentImageX, currentImageY, null)
                currentImageX += imageSize + imageMargin
            }
        }

        pdfDocument.finishPage(currentPage)
        savePdfDocument(context, pdfDocument, "contract.pdf")
    }


    private fun decodeSampledBitmapFromResource(
        res: Resources,
        resId: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeResource(res, resId, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun createTitlePaint(): Paint {
        return Paint().apply {
            color = Color.BLACK
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }
    }

    private fun createBodyPaint(): TextPaint {
        return TextPaint().apply {
            color = Color.BLACK
            textSize = 18f
        }
    }

    private fun createTextLayout(text: String, paint: TextPaint, availableWidth: Float): StaticLayout {
        return StaticLayout.Builder.obtain(
            text,
            0,
            text.length,
            paint,
            availableWidth.toInt()
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(1.0f, 1.0f)
            .build()
    }

    private fun drawLogo(canvas: Canvas, logoBitmap: Bitmap, pageWidth: Int, margin: Float) {
        val logoXPosition = pageWidth - margin - 30f
        val logoYPosition = margin - 30f
        canvas.drawBitmap(logoBitmap, logoXPosition, logoYPosition, null)
    }

    private fun drawTitle(canvas: Canvas, paint: Paint, pageWidth: Int) {
        val titleText = "CONTRATO DE SERVICIOS"
        val titleTextWidth = paint.measureText(titleText)
        val titleXPosition = (pageWidth - titleTextWidth) / 2
        canvas.drawText(titleText, titleXPosition, 120f, paint)
    }

    private fun drawTextLine(canvas: Canvas, text: String, textLayout: StaticLayout, lineIndex: Int, paint: TextPaint, margin: Float, currentTextY: Float) {
        canvas.drawText(
            text.substring(textLayout.getLineStart(lineIndex), textLayout.getLineEnd(lineIndex)),
            margin,
            currentTextY,
            paint
        )
    }

    private fun isPageFull(currentTextY: Float, paint: TextPaint, pageHeight: Int, margin: Float): Boolean {
        return currentTextY + paint.descent() - paint.ascent() > pageHeight - margin
    }

    private fun createPdfPage(pdfDocument: PdfDocument, width: Int, height: Int, pageNumber: Int): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
        return pdfDocument.startPage(pageInfo)
    }

    private fun savePdfDocument(context: Context, pdfDocument: PdfDocument, fileName: String) {
        try {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            }
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)

            uri?.let {
                resolver.openOutputStream(it).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                pdfDocument.close()
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "PDF saved successfully", Toast.LENGTH_SHORT).show()
                }
                Log.d("PdfScreen", "PDF saved to ${uri.path}")
            } ?: run {
                Log.e("PdfScreen", "Failed to create PDF URI")
            }
        } catch (e: Exception) {
            Log.e("PdfScreen", e.toString())
        }
    }
}
