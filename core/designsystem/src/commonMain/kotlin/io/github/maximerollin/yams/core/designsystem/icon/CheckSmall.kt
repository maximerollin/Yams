package io.github.maximerollin.yams.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons

@Suppress("UnusedReceiverParameter")
public val YamsIcons.CheckSmall: ImageVector
    get() {
        if (_CheckSmallIcon != null) {
            return _CheckSmallIcon!!
        }
        _CheckSmallIcon = ImageVector.Builder(
            name = "CheckSmallIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveToRelative(400f, 544f)
                lineToRelative(236f, -236f)
                quadToRelative(11f, -11f, 28f, -11f)
                reflectiveQuadToRelative(28f, 11f)
                quadToRelative(11f, 11f, 11f, 28f)
                reflectiveQuadToRelative(-11f, 28f)
                lineTo(428f, 628f)
                quadToRelative(-12f, 12f, -28f, 12f)
                reflectiveQuadToRelative(-28f, -12f)
                lineTo(268f, 524f)
                quadToRelative(-11f, -11f, -11f, -28f)
                reflectiveQuadToRelative(11f, -28f)
                quadToRelative(11f, -11f, 28f, -11f)
                reflectiveQuadToRelative(28f, 11f)
                lineToRelative(76f, 76f)
                close()
            }
        }.build()

        return _CheckSmallIcon!!
    }

@Suppress("ObjectPropertyName")
private var _CheckSmallIcon: ImageVector? = null
