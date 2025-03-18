package gg.happy.dglab.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitArray
import com.google.zxing.common.BitMatrix
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

object QRCodeUtil
{
    fun generateQRCode(text: String, width: Int = 256, height: Int = 256): BufferedImage
    {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        return MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints).toBufferedImage()
    }

    fun generateQRCodeThenOpen(text: String, width: Int = 256, height: Int = 256)
    {
        val image = generateQRCode(text, width, height)
        val qrCodeFile = File("DG-LAB-QRCode.png")
        ImageIO.write(image, "png", qrCodeFile)
        val process = ProcessBuilder("cmd", "/c", "start", "", "\"" + qrCodeFile.absolutePath + "\"")
        process.start()
    }

    private fun BitMatrix.toBufferedImage(): BufferedImage
    {
        val width: Int = this.width
        val height: Int = this.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)
        val onColor = -0x1000000
        val offColor = -0x1
        val rowPixels = IntArray(width)
        var row = BitArray(width)
        for (y in 0 until height)
        {
            row = this.getRow(y, row)
            for (x in 0 until width)
                rowPixels[x] = if (row.get(x)) onColor else offColor
            image.setRGB(0, y, width, 1, rowPixels, 0, width)
        }
        return image
    }
}