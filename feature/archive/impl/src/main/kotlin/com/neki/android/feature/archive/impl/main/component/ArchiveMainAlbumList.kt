package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.backgroundHazeBlur
import com.neki.android.core.designsystem.modifier.cardShadow
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.AlbumPreview
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private const val VIEWPORT_W = 124f
private const val VIEWPORT_H = 65f

private val AlbumFolderShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val scaleX = size.width / VIEWPORT_W
        val scaleY = size.height / VIEWPORT_H

        val path = Path().apply {
            moveTo(124f * scaleX, 57f * scaleY)
            cubicTo(124f * scaleX, 61.42f * scaleY, 120.42f * scaleX, 65f * scaleY, 116f * scaleX, 65f * scaleY)
            lineTo(8f * scaleX, 65f * scaleY)
            cubicTo(
                3.58f * scaleX,
                65f * scaleY,
                0f,
                61.42f * scaleY,
                0f,
                57f * scaleY,
            )
            lineTo(0f, 8f * scaleY)
            cubicTo(
                0f,
                3.58f * scaleY,
                3.58f * scaleX,
                0f,
                8f * scaleX,
                0f,
            )
            lineTo(58.54f * scaleX, 0f)
            cubicTo(
                61.07f * scaleX,
                0f,
                63.45f * scaleX,
                1.2f * scaleY,
                64.96f * scaleX,
                3.23f * scaleY,
            )
            lineTo(69.2f * scaleX, 8.93f * scaleY)
            cubicTo(
                70.7f * scaleX,
                10.95f * scaleY,
                73.06f * scaleX,
                12.15f * scaleY,
                75.58f * scaleX,
                12.16f * scaleY,
            )
            lineTo(116.04f * scaleX, 12.35f * scaleY)
            cubicTo(
                120.44f * scaleX,
                12.36f * scaleY,
                124f * scaleX,
                15.94f * scaleY,
                124f * scaleX,
                20.34f * scaleY,
            )
            lineTo(124f * scaleX, 57f * scaleY)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
internal fun ArchiveMainAlbumList(
    favoriteAlbum: AlbumPreview,
    modifier: Modifier = Modifier,
    albumList: ImmutableList<AlbumPreview> = persistentListOf(),
    onClickFavoriteAlbum: () -> Unit = {},
    onClickAlbumItem: (AlbumPreview) -> Unit = {},
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "favorite_album") {
            ArchiveAlbumItem(
                title = favoriteAlbum.title,
                photoCount = favoriteAlbum.photoCount,
                thumbnailImage = favoriteAlbum.thumbnailUrl,
                isFavorite = true,
                onClick = onClickFavoriteAlbum,
            )
        }
        items(
            items = albumList,
            key = { album -> album.id },
        ) { album ->
            ArchiveAlbumItem(
                title = album.title,
                photoCount = album.photoCount,
                thumbnailImage = album.thumbnailUrl,
                onClick = { onClickAlbumItem(album) },
            )
        }
    }
}

@Composable
private fun ArchiveAlbumItem(
    title: String,
    photoCount: Int,
    modifier: Modifier = Modifier,
    thumbnailImage: String? = null,
    isFavorite: Boolean = false,
    onClick: () -> Unit = {},
) {
    val hazeState = rememberHazeState()

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
                .hazeSource(hazeState)
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
            hazeState = hazeState,
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
    hazeState: HazeState = rememberHazeState(),
    isFavorite: Boolean = false,
) {
    AlbumFolderLayout(
        modifier = modifier.width(124.dp),
        hazeState = hazeState,
        color = if (isFavorite) NekiTheme.colorScheme.favoriteAlbumCover
        else NekiTheme.colorScheme.defaultAlbumCover,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 10.dp,
                    start = 10.dp,
                    bottom = 8.dp,
                    end = 8.dp,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.widthIn(max = 86.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "${photoCount}장",
                    style = NekiTheme.typography.body14Medium,
                    color = NekiTheme.colorScheme.white.copy(alpha = 0.7f),
                )
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = NekiTheme.typography.body16SemiBold,
                    color = NekiTheme.colorScheme.white,
                )
            }

            if (isFavorite) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                        .align(Alignment.Bottom)
                        .clip(CircleShape)
                        .background(
                            color = NekiTheme.colorScheme.gray25.copy(alpha = 0.19f),
                            shape = CircleShape,
                        )
                        .padding(4.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(12.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_heart_filled),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.white,
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumFolderLayout(
    modifier: Modifier = Modifier,
    hazeState: HazeState = rememberHazeState(),
    color: Color = Color(0xFFFF5647),
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(AlbumFolderShape)
            .backgroundHazeBlur(
                hazeState = hazeState,
                color = color,
                blurRadius = 4.dp,
                alpha = 0.92f,
            ),
        content = content,
    )
}

@ComponentPreview
@Composable
private fun FavoriteAlbumItemPreview() {
    NekiTheme {
        Box(modifier = Modifier.padding(8.dp)) {
            ArchiveAlbumItem(
                isFavorite = true,
                title = "네키 화이팅화이팅",
                photoCount = 10,
            )
        }
    }
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
private fun ArchiveMainAlbumListPreview() {
    NekiTheme {
        ArchiveMainAlbumList(
            favoriteAlbum = AlbumPreview(),
            albumList = List(3) { AlbumPreview(id = it.toLong()) }.toImmutableList(),
        )
    }
}
