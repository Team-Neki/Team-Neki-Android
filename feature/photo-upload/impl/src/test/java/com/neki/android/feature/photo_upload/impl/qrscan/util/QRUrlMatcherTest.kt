package com.neki.android.feature.photo_upload.impl.qrscan.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QRUrlMatcherTest {

    @Test
    fun `픽픽링크 T0231 QR URL을 지원한다`() {
        val url = "https://t.pixpixlink.com/g4?d=T0231&i=QIAk1K6i"

        assertTrue(QRUrlMatcher.isSupportedBrand(url))
        assertFalse(QRUrlMatcher.isFirstDownloadRequired(url))
    }

    @Test
    fun `픽픽링크 T0231 JPG 이미지 URL을 감지한다`() {
        val url = "https://t.pixpixlink.com/t/T0231/QIAk1K6i.jpg"

        assertEquals(QRImageProvider.PIXPIXLINK, QRUrlMatcher.detectImageProvider(url))
    }

    @Test
    fun `픽픽링크가 아닌 호스트의 JPG URL은 감지하지 않는다`() {
        val url = "https://example.com/t/T0231/QIAk1K6i.jpg"

        assertFalse(QRUrlMatcher.isSupportedBrand(url))
        assertEquals(null, QRUrlMatcher.detectImageProvider(url))
    }
}
