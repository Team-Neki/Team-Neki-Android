package com.neki.android.feature.map.impl.region.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PhotoBooth
import com.neki.android.feature.map.impl.component.DistanceInfo
import com.neki.android.feature.map.impl.component.HorizontalBrandItem
import com.neki.android.feature.map.impl.const.MapConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchResultPhotoBoothBottomSheet(
    photoBooths: ImmutableList<PhotoBooth>,
    showDistance: Boolean,
    modifier: Modifier = Modifier,
    filterLabel: String = "브랜드",
    isFilterSelected: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onBoothClick: (PhotoBooth) -> Unit = {},
    onFavoriteClick: (PhotoBooth) -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = NekiTheme.colorScheme.white,
        dragHandle = { BottomSheetDragHandle(color = NekiTheme.colorScheme.gray100) },
    ) {
        SearchResultPhotoBoothBottomSheetContent(
            photoBooths = photoBooths,
            showDistance = showDistance,
            modifier = Modifier.requiredHeight(244.dp),
            filterLabel = filterLabel,
            isFilterSelected = isFilterSelected,
            onFilterClick = onFilterClick,
            onBoothClick = onBoothClick,
            onFavoriteClick = onFavoriteClick,
        )
    }
}

@Composable
private fun SearchResultPhotoBoothBottomSheetContent(
    photoBooths: ImmutableList<PhotoBooth>,
    showDistance: Boolean,
    modifier: Modifier = Modifier,
    filterLabel: String = "브랜드",
    isFilterSelected: Boolean = false,
    onFilterClick: () -> Unit = {},
    onBoothClick: (PhotoBooth) -> Unit = {},
    onFavoriteClick: (PhotoBooth) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    NekiTheme.typography.body14SemiBold
                        .toSpanStyle()
                        .copy(color = NekiTheme.colorScheme.gray600),
                ) {
                    append(photoBooths.size.toString())
                }
                append("곳의 포토부스를 찾았어요.")
            },
            style = NekiTheme.typography.body14Medium,
            color = NekiTheme.colorScheme.gray400,
        )
        BrandFilterChip(
            text = filterLabel,
            selected = isFilterSelected,
            onClick = onFilterClick,
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                bottom = MapConst.BOTTOM_NAVIGATION_BAR_HEIGHT.dp +
                    WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding(),
            ),
        ) {
            items(photoBooths, key = { it.id }) { booth ->
                HorizontalBrandItem(
                    photoBooth = booth,
                    modifier = Modifier.padding(vertical = 8.dp),
                    titleStyle = NekiTheme.typography.title18SemiBold,
                    onClickItem = { onBoothClick(booth) },
                    onClickFavorite = { onFavoriteClick(booth) },
                    extraInfo = if (showDistance) {
                        { DistanceInfo(booth.distance) }
                    } else {
                        null
                    },
                )
            }
        }
    }
}

@Composable
private fun BrandFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit = {},
) {
    val color = if (selected) NekiTheme.colorScheme.primary400 else NekiTheme.colorScheme.gray600
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(CircleShape)
            .clickableSingle(onClick = onClick)
            .border(
                width = 1.2.dp,
                color = if (selected) color else NekiTheme.colorScheme.gray50,
                shape = CircleShape,
            )
            .padding(start = 12.dp, end = 9.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = buildAnnotatedString {
                append(text)
                Regex(" 외 \\d+개$").find(text)?.let { suffix ->
                    addStyle(
                        style = SpanStyle(color = NekiTheme.colorScheme.gray600),
                        start = suffix.range.first,
                        end = text.length,
                    )
                }
            },
            style = NekiTheme.typography.body14SemiBold,
            color = color,
        )
        Icon(
            painter = painterResource(R.drawable.icon_chip_down),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = NekiTheme.colorScheme.gray400,
        )
    }
}

@ComponentPreview
@Composable
private fun SearchResultPhotoBoothPreview() {
    NekiTheme {
        SearchResultPhotoBoothBottomSheetContent(
            photoBooths = persistentListOf(
                PhotoBooth(
                    id = 1,
                    brandName = "인생네컷",
                    branchName = "강남역점",
                    distance = 300,
                ),
            ),
            showDistance = true,
            modifier = Modifier.size(width = 375.dp, height = 420.dp),
        )
    }
}

@ComponentPreview
@Composable
private fun SearchResultPhotoBoothSingleBrandSelectedPreview() {
    NekiTheme {
        SearchResultPhotoBoothBottomSheetContent(
            photoBooths = persistentListOf(
                PhotoBooth(
                    id = 1,
                    brandName = "하루필름",
                    branchName = "강남점",
                ),
            ),
            showDistance = false,
            modifier = Modifier.size(width = 375.dp, height = 420.dp),
            filterLabel = "하루필름",
            isFilterSelected = true,
        )
    }
}

@ComponentPreview
@Composable
private fun SearchResultPhotoBoothMultipleBrandsSelectedPreview() {
    NekiTheme {
        SearchResultPhotoBoothBottomSheetContent(
            photoBooths = persistentListOf(
                PhotoBooth(
                    id = 1,
                    brandName = "포토시그니처",
                    branchName = "강남점",
                ),
                PhotoBooth(
                    id = 2,
                    brandName = "하루필름",
                    branchName = "강남점",
                ),
            ),
            showDistance = false,
            modifier = Modifier.size(width = 375.dp, height = 420.dp),
            filterLabel = "포토시그니처 외 1개",
            isFilterSelected = true,
        )
    }
}
