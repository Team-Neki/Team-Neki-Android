package com.neki.android.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.AlbumPreview

@Composable
fun FavoriteAlbumRowComponent(
    album: AlbumPreview,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .noRippleClickable(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FavoriteAlbumThumbnail(
            isEmpty = album.photoCount == 0,
            thumbnailUrl = album.thumbnailUrl,
        )

        AlbumInfo(
            title = "즐겨찾기",
            photoCount = album.photoCount,
        )
    }
}

@Composable
fun AlbumRowComponent(
    album: AlbumPreview,
    modifier: Modifier = Modifier,
    isSelectable: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .noRippleClickable(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AlbumThumbnail(
            isEmpty = album.photoCount == 0,
            thumbnailUrl = album.thumbnailUrl,
        )

        AlbumInfo(
            modifier = Modifier.weight(1f),
            title = album.title,
            photoCount = album.photoCount,
        )

        if (isSelectable) {
            SelectionCheckbox(
                isSelected = isSelected,
            )
        }
    }
}

@Composable
private fun FavoriteAlbumThumbnail(
    thumbnailUrl: String?,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (isEmpty || thumbnailUrl == null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(NekiTheme.colorScheme.gray50),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_empty_gallery_thumbnail),
                    tint = NekiTheme.colorScheme.gray100,
                    contentDescription = null,
                )
            }
        } else {
            AsyncImage(
                modifier = Modifier
                    .background(color = Color.Black.copy(alpha = 0.04f))
                    .matchParentSize(),
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(NekiTheme.colorScheme.favoriteAlbumCover.copy(alpha = 0.5f)),
            )

            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_heart_filled),
                contentDescription = null,
                tint = NekiTheme.colorScheme.white,
            )
        }
    }
}

@Composable
private fun AlbumThumbnail(
    thumbnailUrl: String?,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isEmpty) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(NekiTheme.colorScheme.gray50),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_empty_gallery_thumbnail),
                tint = NekiTheme.colorScheme.gray100,
                contentDescription = null,
            )
        }
    } else {
        AsyncImage(
            modifier = modifier
                .size(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = NekiTheme.colorScheme.gray50,
                    shape = RoundedCornerShape(8.dp),
                ),
            model = thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun AlbumInfo(
    title: String,
    photoCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = NekiTheme.typography.body16SemiBold,
            color = NekiTheme.colorScheme.gray900,
        )

        Text(
            text = "${photoCount}장",
            style = NekiTheme.typography.caption12Medium,
            color = NekiTheme.colorScheme.gray500,
        )
    }
}

@ComponentPreview
@Composable
private fun FavoriteAlbumRowComponentPreview() {
    NekiTheme {
        Column {
            FavoriteAlbumRowComponent(
                album = AlbumPreview(
                    id = 0,
                    title = "즐겨찾기",
                    photoCount = 0,
                ),
            )
            FavoriteAlbumRowComponent(
                album = AlbumPreview(
                    id = 0,
                    title = "즐겨찾기",
                    thumbnailUrl = "https://example.com/photo.jpg",
                    photoCount = 12,
                ),
            )
        }
    }
}

@ComponentPreview
@Composable
private fun AlbumRowComponentPreview() {
    NekiTheme {
        Column {
            AlbumRowComponent(
                album = AlbumPreview(
                    id = 1,
                    title = "빈 앨범",
                    photoCount = 0,
                ),
            )
            AlbumRowComponent(
                album = AlbumPreview(
                    id = 2,
                    title = "일반앨범제목",
                    thumbnailUrl = "https://example.com/photo.jpg",
                    photoCount = 5,
                ),
            )
        }
    }
}

@ComponentPreview
@Composable
private fun AlbumRowComponentSelectablePreview() {
    NekiTheme {
        Column {
            AlbumRowComponent(
                album = AlbumPreview(
                    id = 1,
                    title = "선택되지 않은 빈 앨범",
                    photoCount = 0,
                ),
                isSelectable = true,
                isSelected = false,
            )
            AlbumRowComponent(
                album = AlbumPreview(
                    id = 2,
                    title = "선택된 앨범",
                    thumbnailUrl = "https://example.com/photo.jpg",
                    photoCount = 8,
                ),
                isSelectable = true,
                isSelected = true,
            )
        }
    }
}
