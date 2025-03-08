package gg.happy.dglab.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitArray
import com.google.zxing.common.BitMatrix
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import javax.imageio.ImageIO

object QRCodeUtil
{
    @Throws(IOException::class, WriterException::class)
    fun generateQRCode(text: String?, width: Int = 256, height: Int = 256): InputStream
    {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val bitMatrix =
            MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints)
        val image = toBufferedImage(bitMatrix)
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "PNG", outputStream)
        return ByteArrayInputStream(outputStream.toByteArray())
    }

    @Throws(IOException::class, WriterException::class)
    fun generateQRCodeAndOpen(text: String?, width: Int = 256, height: Int = 256)
    {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val bitMatrix =
            MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints)
        val image = toBufferedImage(bitMatrix)
        val qrCodeFile = File("DG-LAB-QRCode.png")
        ImageIO.write(image, "png", qrCodeFile)
        val process = ProcessBuilder("cmd", "/c", "start", "", "\"" + qrCodeFile.absolutePath + "\"")
        process.start()
    }

    private fun toBufferedImage(matrix: BitMatrix): BufferedImage
    {
        val width: Int = matrix.width
        val height: Int = matrix.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)
        val onColor = -0x1000000
        val offColor = -0x1
        val rowPixels = IntArray(width)
        var row = BitArray(width)
        for (y in 0 until height)
        {
            row = matrix.getRow(y, row)
            for (x in 0 until width)
            {
                rowPixels[x] = if (row.get(x)) onColor else offColor
            }
            image.setRGB(0, y, width, 1, rowPixels, 0, width)
        }
        return image
    }
}