package com.neki.android.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FavoriteAlbumRowComponent(
    album: Album,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FavoriteAlbumThumbnail(
            thumbnailUrl = album.photoList.firstOrNull()?.imageUrl,
        )

        AlbumInfo(
            title = "즐겨찾는 사진",
            photoCount = album.photoList.size,
        )
    }
}

@Composable
fun AlbumRowComponent(
    album: Album,
    modifier: Modifier = Modifier,
    isSelectable: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AlbumThumbnail(
            thumbnailUrl = album.thumbnailUrl,
        )

        AlbumInfo(
            modifier = Modifier.weight(1f),
            title = album.title,
            photoCount = album.photoList.size,
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
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier.matchParentSize(),
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
            imageVector = ImageVector.vectorResource(R.drawable.icon_heart),
            contentDescription = null,
            tint = NekiTheme.colorScheme.white,
        )
    }
}

@Composable
private fun AlbumThumbnail(
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
) {
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
        FavoriteAlbumRowComponent(
            album = Album(
                id = 0,
                title = "즐겨찾는 사진",
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun AlbumRowComponentPreview() {
    NekiTheme {
        AlbumRowComponent(
            album = Album(
                id = 1,
                title = "일반앨범제목",
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun AlbumRowComponentSelectablePreview() {
    NekiTheme {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            AlbumRowComponent(
                album = Album(
                    id = 1,
                    title = "선택되지 않은 앨범",
                    photoList = persistentListOf(
                        Photo(),
                        Photo(),
                    ),
                ),
                isSelectable = true,
                isSelected = false,
            )

            AlbumRowComponent(
                album = Album(
                    id = 2,
                    title = "선택된 앨범",
                ),
                isSelectable = true,
                isSelected = true,
            )
        }
    }
}
