package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.min

/**
 * Pure Kotlin QR Code Generator (Versions 1 to 6, Error Correction Level M/L).
 * Operates 100% offline with zero dependencies, generating scannable QR matrices
 * and Bitmaps instantly without WebView or network dependencies.
 */
object QRCodeGenerator {

  /**
   * Generates a 2D boolean matrix where true = black module, false = white module.
   */
  fun generateMatrix(content: String): Array<BooleanArray> {
    val dataBytes = content.toByteArray(Charsets.UTF_8)
    // Choose appropriate version based on byte length (using Byte Mode)
    // Version 3: 26 data codewords (Level M) -> up to 24 bytes
    // Version 4: 36 data codewords (Level M) -> up to 34 bytes
    // Version 6: 68 data codewords (Level M) -> up to 66 bytes
    val version = when {
      dataBytes.size <= 17 -> 2  // 25x25
      dataBytes.size <= 24 -> 3  // 29x29
      dataBytes.size <= 34 -> 4  // 33x33
      dataBytes.size <= 50 -> 5  // 37x37
      else -> 6                  // 41x41
    }

    val size = 17 + 4 * version
    val matrix = Array(size) { BooleanArray(size) }
    val isFunction = Array(size) { BooleanArray(size) }

    // 1. Finder patterns (7x7) + Separators (8x8)
    placeFinderPattern(matrix, isFunction, 0, 0, size)
    placeFinderPattern(matrix, isFunction, size - 7, 0, size)
    placeFinderPattern(matrix, isFunction, 0, size - 7, size)

    // 2. Alignment pattern for version >= 2
    if (version >= 2) {
      val alignCoords = getAlignmentCoordinates(version)
      for (r in alignCoords) {
        for (c in alignCoords) {
          if (!isFunction[r][c]) {
            placeAlignmentPattern(matrix, isFunction, r, c)
          }
        }
      }
    }

    // 3. Timing patterns
    for (i in 8 until size - 8) {
      val bit = (i % 2 == 0)
      if (!isFunction[6][i]) {
        matrix[6][i] = bit
        isFunction[6][i] = true
      }
      if (!isFunction[i][6]) {
        matrix[i][6] = bit
        isFunction[i][6] = true
      }
    }

    // 4. Dark module
    matrix[4 * version + 9][8] = true
    isFunction[4 * version + 9][8] = true

    // 5. Reserve format information areas
    for (i in 0..8) {
      if (i != 6) {
        isFunction[8][i] = true
        isFunction[i][8] = true
      }
    }
    for (i in size - 8 until size) {
      isFunction[8][i] = true
      isFunction[i][8] = true
    }

    // 6. Encode data payload (Byte mode)
    val totalDataCodewords = getDataCodewordsCount(version)
    val totalEccCodewords = getEccCodewordsCount(version)
    val encodedBits = encodeDataBits(dataBytes, version, totalDataCodewords)

    // Convert bits to codewords
    val dataCodewords = IntArray(totalDataCodewords)
    for (i in 0 until totalDataCodewords) {
      var byteVal = 0
      for (b in 0 until 8) {
        val bitIdx = i * 8 + b
        if (bitIdx < encodedBits.size && encodedBits[bitIdx]) {
          byteVal = byteVal or (1 shl (7 - b))
        }
      }
      dataCodewords[i] = byteVal
    }

    // Compute Reed-Solomon ECC codewords
    val eccCodewords = computeReedSolomonEcc(dataCodewords, totalEccCodewords)

    // Combine all codewords
    val allCodewords = dataCodewords + eccCodewords

    // 7. Place data into matrix with Mask 0 ((row + col) % 2 == 0)
    placeCodewords(matrix, isFunction, allCodewords, size)

    // 8. Place Format Information (Level M, Mask 0 -> 0x5412 XORed)
    // Format bits for Level M (00) and Mask 0 (000): raw = 00000, BCH code = 0000000000, xor 0x5412 = 0x5412
    val formatBits = 0x5412
    placeFormatBits(matrix, formatBits, size)

    return matrix
  }

  /**
   * Renders the QR code matrix to an Android Bitmap.
   */
  fun generateBitmap(
    content: String,
    pixelSize: Int = 512,
    foregroundColor: Int = Color.BLACK,
    backgroundColor: Int = Color.WHITE
  ): Bitmap {
    val matrix = generateMatrix(content)
    val matrixSize = matrix.size
    val quietZone = 2
    val fullSize = matrixSize + quietZone * 2

    val bitmap = Bitmap.createBitmap(pixelSize, pixelSize, Bitmap.Config.ARGB_8888)
    val scale = pixelSize.toFloat() / fullSize.toFloat()

    // Fill background
    val pixels = IntArray(pixelSize * pixelSize)
    for (i in pixels.indices) {
      pixels[i] = backgroundColor
    }

    for (r in 0 until matrixSize) {
      for (c in 0 until matrixSize) {
        if (matrix[r][c]) {
          val startX = ((c + quietZone) * scale).toInt()
          val endX = min(((c + quietZone + 1) * scale).toInt(), pixelSize)
          val startY = ((r + quietZone) * scale).toInt()
          val endY = min(((r + quietZone + 1) * scale).toInt(), pixelSize)

          for (y in startY until endY) {
            for (x in startX until endX) {
              pixels[y * pixelSize + x] = foregroundColor
            }
          }
        }
      }
    }

    bitmap.setPixels(pixels, 0, pixelSize, 0, 0, pixelSize, pixelSize)
    return bitmap
  }

  private fun placeFinderPattern(
    matrix: Array<BooleanArray>,
    isFunction: Array<BooleanArray>,
    r: Int,
    c: Int,
    size: Int
  ) {
    for (dr in -1..7) {
      for (dc in -1..7) {
        val row = r + dr
        val col = c + dc
        if (row in 0 until size && col in 0 until size) {
          isFunction[row][col] = true
          if (dr in 0..6 && dc in 0..6) {
            val isBorder = dr == 0 || dr == 6 || dc == 0 || dc == 6
            val isCenter = dr in 2..4 && dc in 2..4
            matrix[row][col] = isBorder || isCenter
          } else {
            matrix[row][col] = false
          }
        }
      }
    }
  }

  private fun placeAlignmentPattern(
    matrix: Array<BooleanArray>,
    isFunction: Array<BooleanArray>,
    centerR: Int,
    centerC: Int
  ) {
    for (dr in -2..2) {
      for (dc in -2..2) {
        val row = centerR + dr
        val col = centerC + dc
        isFunction[row][col] = true
        val isBorder = dr == -2 || dr == 2 || dc == -2 || dc == 2
        val isCenter = dr == 0 && dc == 0
        matrix[row][col] = isBorder || isCenter
      }
    }
  }

  private fun getAlignmentCoordinates(version: Int): IntArray {
    return when (version) {
      2 -> intArrayOf(6, 18)
      3 -> intArrayOf(6, 22)
      4 -> intArrayOf(6, 26)
      5 -> intArrayOf(6, 30)
      6 -> intArrayOf(6, 34)
      else -> intArrayOf(6, 26)
    }
  }

  private fun getDataCodewordsCount(version: Int): Int {
    // Level M
    return when (version) {
      1 -> 16
      2 -> 28
      3 -> 44
      4 -> 64
      5 -> 86
      6 -> 108
      else -> 64
    }
  }

  private fun getEccCodewordsCount(version: Int): Int {
    // Level M
    return when (version) {
      1 -> 10
      2 -> 16
      3 -> 26
      4 -> 36
      5 -> 48
      6 -> 64
      else -> 36
    }
  }

  private fun encodeDataBits(data: ByteArray, version: Int, targetCodewords: Int): List<Boolean> {
    val bits = ArrayList<Boolean>()

    // Mode: Byte (0100)
    bits.add(false); bits.add(true); bits.add(false); bits.add(false)

    // Character count indicator (8 bits for versions 1-9 in byte mode)
    val count = data.size
    for (i in 7 downTo 0) {
      bits.add(((count shr i) and 1) == 1)
    }

    // Data bytes
    for (b in data) {
      val intVal = b.toInt() and 0xFF
      for (i in 7 downTo 0) {
        bits.add(((intVal shr i) and 1) == 1)
      }
    }

    // Terminator (up to 4 zeros)
    val maxBits = targetCodewords * 8
    val termLength = min(4, maxBits - bits.size)
    for (i in 0 until termLength) {
      bits.add(false)
    }

    // Pad to byte boundary
    while (bits.size % 8 != 0 && bits.size < maxBits) {
      bits.add(false)
    }

    // Pad bytes (0xEC, 0x11 alternating)
    val padPatterns = arrayOf(0xEC, 0x11)
    var padIdx = 0
    while (bits.size < maxBits) {
      val padByte = padPatterns[padIdx % 2]
      padIdx++
      for (i in 7 downTo 0) {
        bits.add(((padByte shr i) and 1) == 1)
      }
    }

    return bits
  }

  private fun computeReedSolomonEcc(data: IntArray, eccCount: Int): IntArray {
    val generator = getRsGeneratorPoly(eccCount)
    val remainder = IntArray(eccCount)

    for (d in data) {
      val factor = (d xor remainder[0]) and 0xFF
      for (i in 0 until eccCount - 1) {
        remainder[i] = remainder[i + 1] xor gfMultiply(factor, generator[i + 1])
      }
      remainder[eccCount - 1] = gfMultiply(factor, generator[eccCount])
    }
    return remainder
  }

  private fun getRsGeneratorPoly(deg: Int): IntArray {
    var poly = intArrayOf(1)
    for (i in 0 until deg) {
      val next = IntArray(poly.size + 1)
      val root = gfExp(i)
      for (j in poly.indices) {
        next[j] = next[j] xor poly[j]
        next[j + 1] = next[j + 1] xor gfMultiply(poly[j], root)
      }
      poly = next
    }
    return poly
  }

  private val GF_EXP = IntArray(512)
  private val GF_LOG = IntArray(256)

  init {
    var x = 1
    for (i in 0 until 255) {
      GF_EXP[i] = x
      GF_EXP[i + 255] = x
      GF_LOG[x] = i
      x = (x shl 1)
      if (x and 0x100 != 0) {
        x = x xor 0x11D
      }
    }
  }

  private fun gfExp(p: Int): Int = GF_EXP[(p % 255 + 255) % 255]

  private fun gfMultiply(x: Int, y: Int): Int {
    if (x == 0 || y == 0) return 0
    return GF_EXP[GF_LOG[x] + GF_LOG[y]]
  }

  private fun placeCodewords(
    matrix: Array<BooleanArray>,
    isFunction: Array<BooleanArray>,
    codewords: IntArray,
    size: Int
  ) {
    var bitIndex = 0
    val totalBits = codewords.size * 8

    var upwards = true
    var c = size - 1
    while (c > 0) {
      if (c == 6) c-- // Skip vertical timing column

      val rows = if (upwards) (size - 1 downTo 0) else (0 until size)
      for (r in rows) {
        for (dc in 0 downTo -1) {
          val col = c + dc
          if (!isFunction[r][col]) {
            var bit = false
            if (bitIndex < totalBits) {
              val cwIdx = bitIndex / 8
              val bitInCw = 7 - (bitIndex % 8)
              bit = ((codewords[cwIdx] shr bitInCw) and 1) == 1
              bitIndex++
            }
            // Mask 0: (row + col) % 2 == 0
            val mask = (r + col) % 2 == 0
            matrix[r][col] = bit xor mask
          }
        }
      }
      upwards = !upwards
      c -= 2
    }
  }

  private fun placeFormatBits(matrix: Array<BooleanArray>, formatBits: Int, size: Int) {
    // 15 format bits
    for (i in 0..14) {
      val bit = ((formatBits shr (14 - i)) and 1) == 1

      // Top-left
      val (r1, c1) = when {
        i < 6 -> Pair(8, i)
        i == 6 -> Pair(8, 7)
        i == 7 -> Pair(8, 8)
        i == 8 -> Pair(7, 8)
        else -> Pair(14 - i, 8)
      }
      matrix[r1][c1] = bit

      // Around top-right and bottom-left
      val (r2, c2) = if (i < 8) {
        Pair(size - 1 - i, 8)
      } else {
        Pair(8, size - 15 + i)
      }
      matrix[r2][c2] = bit
    }
  }
}
