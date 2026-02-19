package com.neki.android.feature.map.impl.cluster

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey
import com.neki.android.core.model.PhotoBooth

/**
 * PhotoBooth를 클러스터링 가능한 아이템으로 래핑하는 클래스
 */
internal data class PhotoBoothClusterItem(
    val photoBooth: PhotoBooth,
): ClusteringKey {
    override fun getPosition(): LatLng = LatLng(
        photoBooth.latitude,
        photoBooth.longitude
    )
}
