package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.cardShadow
import com.neki.android.core.designsystem.extension.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Album
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private const val VIEWPORT_W = 124f
private const val VIEWPORT_H = 65f

@Composable
internal fun ArchiveMainAlbumList(
    favoriteAlbum: Album,
    modifier: Modifier = Modifier,
    albumList: ImmutableList<Album> = persistentListOf(),
    onFavoriteAlbumClick: () -> Unit = {},
    onAlbumItemClick: (Album) -> Unit = {},
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ArchiveAlbumItem(
                title = favoriteAlbum.title,
                photoCount = favoriteAlbum.photoList.size,
                thumbnailImage = favoriteAlbum.photoList.firstOrNull()?.imageUrl,
                isFavorite = true,
                onClick = onFavoriteAlbumClick,
            )
        }
        items(albumList) { album ->
            ArchiveAlbumItem(
                title = album.title,
                photoCount = album.photoList.size,
                thumbnailImage = album.photoList.firstOrNull()?.imageUrl,
                onClick = { onAlbumItemClick(album) },
            )
        }
    }
}

@Composable
private fun ArchiveAlbumItem(
    title: String,
    photoCount: Int,
    thumbnailImage: String? = null,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .height(166.dp)
            .clip(RoundedCornerShape(8.dp))
            .noRippleClickable(onClick = onClick),
    ) {
        AsyncImage(
            modifier = Modifier
                .cardShadow(shape = RoundedCornerShape(8.dp))
                .matchParentSize()
                .then(
                    if (!thumbnailImage.isNullOrBlank()) Modifier
                    else Modifier.background(color = NekiTheme.colorScheme.gray50),
                ),
            model = thumbnailImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        AlbumFolder(
            modifier = Modifier.align(Alignment.BottomCenter),
            title = title,
            photoCount = photoCount,
            isFavorite = isFavorite,
        )
    }
}

@Composable
private fun AlbumFolder(
    title: String,
    photoCount: Int,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
) {
    AlbumFolderLayout(
        modifier = modifier.width(124.dp),
        color = if (isFavorite) NekiTheme.colorScheme.favoriteAlbumCover.copy(alpha = 0.9f) else NekiTheme.colorScheme.defaultAlbumCover.copy(alpha = 0.92f),
    ) {
        Row(
            modifier = Modifier
                .padding(
                    top = 15.dp,
                    bottom = 10.dp,
                    start = 10.dp,
                    end = 8.dp,
                ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(
                modifier = Modifier.width(80.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "${photoCount}장",
                    style = NekiTheme.typography.caption12Medium,
                    color = NekiTheme.colorScheme.white.copy(alpha = 0.7f),
                )
                Text(
                    modifier = Modifier.widthIn(max = 80.dp),
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = NekiTheme.typography.body14SemiBold,
                    color = NekiTheme.colorScheme.white,
                )
            }

            if (isFavorite) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .align(Alignment.Bottom)
                        .clip(CircleShape)
                        .background(
                            color = NekiTheme.colorScheme.gray25.copy(alpha = 0.19f),
                            shape = CircleShape,
                        )
                        .padding(4.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(8.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_heart_white_20),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumFolderLayout(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFF5647),
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .drawBehind {
                val scaleX = size.width / VIEWPORT_W
                val scaleY = size.height / VIEWPORT_H

                scale(scaleX, scaleY, pivot = Offset.Zero) {
                    val path = Path().apply {
                        // 오른쪽 하단에서 시작 (코너 8만큼 위)
                        moveTo(124f, 57f)
                        // 오른쪽 하단 코너
                        cubicTo(124f, 61.42f, 120.42f, 65f, 116f, 65f)
                        // 하단 직선
                        lineTo(8f, 65f)
                        // 왼쪽 하단 코너
                        cubicTo(3.58f, 65f, 0f, 61.42f, 0f, 57f)
                        // 왼쪽 직선
                        lineTo(0f, 8f)
                        // 왼쪽 상단 코너
                        cubicTo(0f, 3.58f, 3.58f, 0f, 8f, 0f)
                        // 상단 탭 모양
                        lineTo(58.54f, 0f)
                        cubicTo(61.07f, 0f, 63.45f, 1.2f, 64.96f, 3.23f)
                        lineTo(69.2f, 8.93f)
                        cubicTo(70.7f, 10.95f, 73.06f, 12.15f, 75.58f, 12.16f)
                        lineTo(116.04f, 12.35f)
                        cubicTo(120.44f, 12.36f, 124f, 15.94f, 124f, 20.34f)
                        // 오른쪽 직선
                        lineTo(124f, 57f)
                        close()
                    }
                    drawPath(path, color, style = Fill)
                }
            },
        content = content,
    )
}

@ComponentPreview
@Composable
private fun ArchiveAlbumItemPreview() {
    NekiTheme {
        Box(modifier = Modifier.padding(8.dp)) {
            ArchiveAlbumItem(
                title = "네키 화이팅화이팅",
                photoCount = 10,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun FavoriteAlbumFolderPreview() {
    NekiTheme {
        AlbumFolder(
            isFavorite = true,
            title = "즐겨찾는 사진",
            photoCount = 12,
        )
    }
}

@ComponentPreview
@Composable
private fun DefaultAlbumFolderPreview() {
    NekiTheme {
        AlbumFolder(
            isFavorite = false,
            title = "네키 화이팅화이팅",
            photoCount = 10,
        )
    }
}

@Preview
@Composable
private fun ArchiveMainAlbumListPreview() {
    NekiTheme {
        ArchiveMainAlbumList(
            favoriteAlbum = Album(),
        )
    }
}
