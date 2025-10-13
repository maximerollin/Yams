package io.github.maximerollin.yams.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons

@Suppress("UnusedReceiverParameter")
public val YamsIcons.FamilyStar: ImageVector
    get() {
        if (_FamilyStar != null) {
            return _FamilyStar!!
        }
        _FamilyStar = ImageVector.Builder(
            name = "FamilyStar",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, 640f)
                quadToRelative(48f, 0f, 85.5f, -28.5f)
                reflectiveQuadTo(620f, 538f)
                lineTo(340f, 538f)
                quadToRelative(17f, 45f, 54.5f, 73.5f)
                reflectiveQuadTo(480f, 640f)
                close()
                moveTo(380f, 480f)
                quadToRelative(25f, 0f, 42.5f, -17.5f)
                reflectiveQuadTo(440f, 420f)
                quadToRelative(0f, -25f, -17.5f, -42.5f)
                reflectiveQuadTo(380f, 360f)
                quadToRelative(-25f, 0f, -42.5f, 17.5f)
                reflectiveQuadTo(320f, 420f)
                quadToRelative(0f, 25f, 17.5f, 42.5f)
                reflectiveQuadTo(380f, 480f)
                close()
                moveTo(580f, 480f)
                quadToRelative(25f, 0f, 42.5f, -17.5f)
                reflectiveQuadTo(640f, 420f)
                quadToRelative(0f, -25f, -17.5f, -42.5f)
                reflectiveQuadTo(580f, 360f)
                quadToRelative(-25f, 0f, -42.5f, 17.5f)
                reflectiveQuadTo(520f, 420f)
                quadToRelative(0f, 25f, 17.5f, 42.5f)
                reflectiveQuadTo(580f, 480f)
                close()
                moveTo(305f, 256f)
                lineToRelative(112f, -145f)
                quadToRelative(12f, -16f, 28.5f, -23.5f)
                reflectiveQuadTo(480f, 80f)
                quadToRelative(18f, 0f, 34.5f, 7.5f)
                reflectiveQuadTo(543f, 111f)
                lineToRelative(112f, 145f)
                lineToRelative(170f, 57f)
                quadToRelative(26f, 8f, 41f, 29.5f)
                reflectiveQuadToRelative(15f, 47.5f)
                quadToRelative(0f, 12f, -3.5f, 24f)
                reflectiveQuadTo(866f, 437f)
                lineTo(756f, 593f)
                lineToRelative(4f, 164f)
                quadToRelative(1f, 35f, -23f, 59f)
                reflectiveQuadToRelative(-56f, 24f)
                quadToRelative(-2f, 0f, -22f, -3f)
                lineToRelative(-179f, -50f)
                lineToRelative(-179f, 50f)
                quadToRelative(-5f, 2f, -11f, 2.5f)
                reflectiveQuadToRelative(-11f, 0.5f)
                quadToRelative(-32f, 0f, -56f, -24f)
                reflectiveQuadToRelative(-23f, -59f)
                lineToRelative(4f, -165f)
                lineTo(95f, 437f)
                quadToRelative(-8f, -11f, -11.5f, -23f)
                reflectiveQuadTo(80f, 390f)
                quadToRelative(0f, -25f, 14.5f, -46.5f)
                reflectiveQuadTo(135f, 313f)
                lineToRelative(170f, -57f)
                close()
                moveTo(354f, 325f)
                lineTo(160f, 389f)
                lineTo(284f, 568f)
                lineTo(280f, 759f)
                lineTo(480f, 704f)
                lineTo(680f, 760f)
                lineTo(676f, 568f)
                lineTo(800f, 391f)
                lineTo(606f, 325f)
                lineTo(480f, 160f)
                lineTo(354f, 325f)
                close()
                moveTo(480f, 460f)
                close()
            }
        }.build()

        return _FamilyStar!!
    }

@Suppress("ObjectPropertyName")
private var _FamilyStar: ImageVector? = null
