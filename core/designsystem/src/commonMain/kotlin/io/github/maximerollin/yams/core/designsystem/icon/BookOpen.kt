package io.github.maximerollin.yams.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val YamsIcons.BookOpen: ImageVector
    get() {
        if (_BookOpen != null) {
            return _BookOpen!!
        }
        _BookOpen = ImageVector.Builder(
            name = "BookOpen",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, 800f)
                quadToRelative(-66f, -44f, -143f, -66f)
                reflectiveQuadToRelative(-157f, -22f)
                quadToRelative(-25f, 0f, -42.5f, -17.5f)
                reflectiveQuadTo(120f, 652f)
                verticalLineTo(220f)
                quadToRelative(0f, -25f, 17.5f, -42.5f)
                reflectiveQuadTo(180f, 160f)
                quadToRelative(78f, 0f, 153.5f, 22f)
                reflectiveQuadTo(480f, 248f)
                quadToRelative(71f, -44f, 146.5f, -66f)
                reflectiveQuadTo(780f, 160f)
                quadToRelative(25f, 0f, 42.5f, 17.5f)
                reflectiveQuadTo(840f, 220f)
                verticalLineToRelative(432f)
                quadToRelative(0f, 25f, -17.5f, 42.5f)
                reflectiveQuadTo(780f, 712f)
                quadToRelative(-80f, 0f, -157f, 22f)
                reflectiveQuadTo(480f, 800f)
                close()
                moveTo(520f, 694f)
                quadToRelative(57f, -31f, 122f, -47f)
                reflectiveQuadToRelative(118f, -17f)
                verticalLineTo(242f)
                quadToRelative(-61f, 3f, -121.5f, 24.5f)
                reflectiveQuadTo(520f, 332f)
                verticalLineToRelative(362f)
                close()
                moveTo(440f, 694f)
                verticalLineTo(332f)
                quadToRelative(-58f, -44f, -118.5f, -65.5f)
                reflectiveQuadTo(200f, 242f)
                verticalLineToRelative(388f)
                quadToRelative(53f, 1f, 118f, 17f)
                reflectiveQuadToRelative(122f, 47f)
                close()
            }
        }.build()

        return _BookOpen!!
    }

@Suppress("ObjectPropertyName")
private var _BookOpen: ImageVector? = null
