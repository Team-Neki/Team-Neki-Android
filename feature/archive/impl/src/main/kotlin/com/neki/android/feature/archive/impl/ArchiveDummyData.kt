package com.neki.android.feature.archive.impl

import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
import kotlinx.collections.immutable.persistentListOf

val dummyPhotos = persistentListOf(
    Photo(id = 1, imageUrl = "https://picsum.photos/id/1/200/320", isFavorite = true),
    Photo(id = 2, imageUrl = "https://picsum.photos/id/2/200/250", isFavorite = false),
    Photo(id = 3, imageUrl = "https://picsum.photos/id/3/200/450", isFavorite = true),
    Photo(id = 4, imageUrl = "https://picsum.photos/id/4/200/280", isFavorite = false),
    Photo(id = 5, imageUrl = "https://picsum.photos/id/5/200/300", isFavorite = true),
    Photo(id = 6, imageUrl = "https://picsum.photos/id/6/200/500", isFavorite = false),
    Photo(id = 7, imageUrl = "https://picsum.photos/id/7/200/380", isFavorite = true),
    Photo(id = 8, imageUrl = "https://picsum.photos/id/8/200/260", isFavorite = false),
    Photo(id = 9, imageUrl = "https://picsum.photos/id/9/200/310", isFavorite = true),
    Photo(id = 10, imageUrl = "https://picsum.photos/id/10/200/420", isFavorite = false),
    Photo(id = 11, imageUrl = "https://picsum.photos/id/11/200/240", isFavorite = true),
    Photo(id = 12, imageUrl = "https://picsum.photos/id/12/200/350", isFavorite = false),
    Photo(id = 13, imageUrl = "https://picsum.photos/id/13/200/480", isFavorite = true),
    Photo(id = 14, imageUrl = "https://picsum.photos/id/14/200/290", isFavorite = false),
    Photo(id = 15, imageUrl = "https://picsum.photos/id/15/200/330", isFavorite = true),
    Photo(id = 16, imageUrl = "https://picsum.photos/id/16/200/520", isFavorite = false),
    Photo(id = 17, imageUrl = "https://picsum.photos/id/17/200/370", isFavorite = true),
    Photo(id = 18, imageUrl = "https://picsum.photos/id/18/200/260", isFavorite = false),
    Photo(id = 19, imageUrl = "https://picsum.photos/id/19/200/400", isFavorite = true),
    Photo(id = 20, imageUrl = "https://picsum.photos/id/20/200/310", isFavorite = false),
)

val dummyAlbums = persistentListOf(
    Album(id = 1, title = "즐겨찾는 앨범", thumbnailUrl = "https://picsum.photos/id/21/200/280", photoList = dummyPhotos),
    Album(title = "빈 앨범"),
    Album(id = 2, title = "여행", thumbnailUrl = "https://picsum.photos/id/22/200/420", photoList = dummyPhotos),
    Album(id = 3, title = "친구", thumbnailUrl = "https://picsum.photos/id/23/200/290", photoList = dummyPhotos),
    Album(id = 4, title = "가족", thumbnailUrl = "https://picsum.photos/id/24/200/450", photoList = dummyPhotos),
    Album(id = 5, title = "맛집", thumbnailUrl = "https://picsum.photos/id/25/200/270", photoList = dummyPhotos),
    Album(id = 6, title = "카페", thumbnailUrl = "https://picsum.photos/id/26/200/380", photoList = dummyPhotos),
    Album(id = 7, title = "운동", thumbnailUrl = "https://picsum.photos/id/27/200/310", photoList = dummyPhotos),
    Album(id = 8, title = "공부", thumbnailUrl = "https://picsum.photos/id/28/200/480", photoList = dummyPhotos),
    Album(id = 9, title = "반려동물", thumbnailUrl = "https://picsum.photos/id/29/200/330", photoList = dummyPhotos),
)

val dummyFavoriteAlbum = Album(
    id = 0,
    title = "즐겨찾는 사진",
    thumbnailUrl = "https://picsum.photos/id/31/200/300",
)
