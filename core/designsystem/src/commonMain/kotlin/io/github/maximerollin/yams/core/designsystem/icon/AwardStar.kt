package io.github.maximerollin.yams.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons

@Suppress("UnusedReceiverParameter")
public val YamsIcons.AwardStar: ImageVector
    get() {
        if (_AwardStar != null) {
            return _AwardStar!!
        }
        _AwardStar = ImageVector.Builder(
            name = "AwardStar",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveToRelative(480f, 579f)
                lineToRelative(76f, 46f)
                quadToRelative(11f, 7f, 22f, -0.5f)
                reflectiveQuadToRelative(8f, -20.5f)
                lineToRelative(-20f, -87f)
                lineToRelative(68f, -59f)
                quadToRelative(10f, -9f, 6f, -21.5f)
                reflectiveQuadTo(622f, 423f)
                lineToRelative(-89f, -7f)
                lineToRelative(-35f, -82f)
                quadToRelative(-5f, -12f, -18f, -12f)
                reflectiveQuadToRelative(-18f, 12f)
                lineToRelative(-35f, 82f)
                lineToRelative(-89f, 7f)
                quadToRelative(-14f, 1f, -18f, 13.5f)
                reflectiveQuadToRelative(6f, 21.5f)
                lineToRelative(68f, 59f)
                lineToRelative(-20f, 87f)
                quadToRelative(-3f, 13f, 8f, 20.5f)
                reflectiveQuadToRelative(22f, 0.5f)
                lineToRelative(76f, -46f)
                close()
                moveTo(346f, 800f)
                lineTo(240f, 800f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(160f, 720f)
                verticalLineToRelative(-106f)
                lineToRelative(-77f, -78f)
                quadToRelative(-11f, -12f, -17f, -26.5f)
                reflectiveQuadTo(60f, 480f)
                quadToRelative(0f, -15f, 6f, -29.5f)
                reflectiveQuadTo(83f, 424f)
                lineToRelative(77f, -78f)
                verticalLineToRelative(-106f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(240f, 160f)
                horizontalLineToRelative(106f)
                lineToRelative(78f, -77f)
                quadToRelative(12f, -11f, 26.5f, -17f)
                reflectiveQuadToRelative(29.5f, -6f)
                quadToRelative(15f, 0f, 29.5f, 6f)
                reflectiveQuadToRelative(26.5f, 17f)
                lineToRelative(78f, 77f)
                horizontalLineToRelative(106f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(800f, 240f)
                verticalLineToRelative(106f)
                lineToRelative(77f, 78f)
                quadToRelative(11f, 12f, 17f, 26.5f)
                reflectiveQuadToRelative(6f, 29.5f)
                quadToRelative(0f, 15f, -6f, 29.5f)
                reflectiveQuadTo(877f, 536f)
                lineToRelative(-77f, 78f)
                verticalLineToRelative(106f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(720f, 800f)
                lineTo(614f, 800f)
                lineToRelative(-78f, 77f)
                quadToRelative(-12f, 11f, -26.5f, 17f)
                reflectiveQuadTo(480f, 900f)
                quadToRelative(-15f, 0f, -29.5f, -6f)
                reflectiveQuadTo(424f, 877f)
                lineToRelative(-78f, -77f)
                close()
                moveTo(380f, 720f)
                lineTo(480f, 820f)
                lineTo(580f, 720f)
                horizontalLineToRelative(140f)
                verticalLineToRelative(-140f)
                lineToRelative(100f, -100f)
                lineToRelative(-100f, -100f)
                verticalLineToRelative(-140f)
                lineTo(580f, 240f)
                lineTo(480f, 140f)
                lineTo(380f, 240f)
                lineTo(240f, 240f)
                verticalLineToRelative(140f)
                lineTo(140f, 480f)
                lineToRelative(100f, 100f)
                verticalLineToRelative(140f)
                horizontalLineToRelative(140f)
                close()
                moveTo(480f, 480f)
                close()
            }
        }.build()

        return _AwardStar!!
    }

@Suppress("ObjectPropertyName")
private var _AwardStar: ImageVector? = null
