package com.example.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * VisualTransformation untuk format nomor telepon berjarak/spasi otomatis:
 * - Jika diawali +62 / 62: "+62 812 3456 7890"
 * - Jika diawali 08: "0812 3456 7890"
 */
class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val out = StringBuilder()
        // Map transformed offset to original offset
        val transformedToOriginal = mutableListOf<Int>()
        // Map original offset to transformed offset
        val originalToTransformed = IntArray(raw.length + 1)

        val digits = raw.filter { it.isDigit() }
        val is62Prefix = raw.startsWith("+62") || raw.startsWith("62")

        if (is62Prefix) {
            // Kita format sebagai: +62 8xx xxxx xxxx
            var digitCount = 0
            for (i in raw.indices) {
                originalToTransformed[i] = out.length
                val c = raw[i]
                if (c.isDigit()) {
                    digitCount++
                    out.append(c)
                    transformedToOriginal.add(i)
                    // Masukkan spasi setelah 2 digit (62), lalu 3 digit berikutnya (8xx), lalu 4 digit berikutnya (xxxx)
                    if (digitCount == 2 || digitCount == 5 || digitCount == 9) {
                        out.append(' ')
                        transformedToOriginal.add(i + 1)
                    }
                } else if (c == '+') {
                    out.append(c)
                    transformedToOriginal.add(i)
                }
            }
        } else {
            // Standard format 08xx xxxx xxxx
            var digitCount = 0
            for (i in raw.indices) {
                originalToTransformed[i] = out.length
                val c = raw[i]
                if (c.isDigit()) {
                    digitCount++
                    out.append(c)
                    transformedToOriginal.add(i)
                    // Masukkan spasi setelah 4 digit (08xx), lalu 4 digit berikutnya (xxxx)
                    if (digitCount == 4 || digitCount == 8) {
                        out.append(' ')
                        transformedToOriginal.add(i + 1)
                    }
                } else {
                    out.append(c)
                    transformedToOriginal.add(i)
                }
            }
        }

        originalToTransformed[raw.length] = out.length
        transformedToOriginal.add(raw.length)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, raw.length)
                return originalToTransformed[clamped].coerceIn(0, out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, transformedToOriginal.size - 1)
                return transformedToOriginal[clamped].coerceIn(0, raw.length)
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}
