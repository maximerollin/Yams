package io.github.maximerollin.yams.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons

@Suppress("UnusedReceiverParameter")
public val YamsIcons.Trophy: ImageVector
    get() {
        if (_TrophyIcon != null) {
            return _TrophyIcon!!
        }
        _TrophyIcon = ImageVector.Builder(
            name = "TrophyIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(440f, 760f)
                lineTo(440f, 636f)
                quadTo(391f, 625f, 352.5f, 594.5f)
                quadTo(314f, 564f, 296f, 518f)
                quadTo(221f, 509f, 170.5f, 452.5f)
                quadTo(120f, 396f, 120f, 320f)
                lineTo(120f, 280f)
                quadTo(120f, 247f, 143.5f, 223.5f)
                quadTo(167f, 200f, 200f, 200f)
                lineTo(280f, 200f)
                lineTo(280f, 200f)
                quadTo(280f, 167f, 303.5f, 143.5f)
                quadTo(327f, 120f, 360f, 120f)
                lineTo(600f, 120f)
                quadTo(633f, 120f, 656.5f, 143.5f)
                quadTo(680f, 167f, 680f, 200f)
                lineTo(680f, 200f)
                lineTo(760f, 200f)
                quadTo(793f, 200f, 816.5f, 223.5f)
                quadTo(840f, 247f, 840f, 280f)
                lineTo(840f, 320f)
                quadTo(840f, 396f, 789.5f, 452.5f)
                quadTo(739f, 509f, 664f, 518f)
                quadTo(646f, 564f, 607.5f, 594.5f)
                quadTo(569f, 625f, 520f, 636f)
                lineTo(520f, 760f)
                lineTo(640f, 760f)
                quadTo(657f, 760f, 668.5f, 771.5f)
                quadTo(680f, 783f, 680f, 800f)
                quadTo(680f, 817f, 668.5f, 828.5f)
                quadTo(657f, 840f, 640f, 840f)
                lineTo(320f, 840f)
                quadTo(303f, 840f, 291.5f, 828.5f)
                quadTo(280f, 817f, 280f, 800f)
                quadTo(280f, 783f, 291.5f, 771.5f)
                quadTo(303f, 760f, 320f, 760f)
                lineTo(440f, 760f)
                close()
                moveTo(280f, 432f)
                lineTo(280f, 280f)
                lineTo(200f, 280f)
                quadTo(200f, 280f, 200f, 280f)
                quadTo(200f, 280f, 200f, 280f)
                lineTo(200f, 320f)
                quadTo(200f, 358f, 222f, 388.5f)
                quadTo(244f, 419f, 280f, 432f)
                close()
                moveTo(480f, 560f)
                quadTo(530f, 560f, 565f, 525f)
                quadTo(600f, 490f, 600f, 440f)
                lineTo(600f, 200f)
                quadTo(600f, 200f, 600f, 200f)
                quadTo(600f, 200f, 600f, 200f)
                lineTo(360f, 200f)
                quadTo(360f, 200f, 360f, 200f)
                quadTo(360f, 200f, 360f, 200f)
                lineTo(360f, 440f)
                quadTo(360f, 490f, 395f, 525f)
                quadTo(430f, 560f, 480f, 560f)
                close()
                moveTo(680f, 432f)
                quadTo(716f, 419f, 738f, 388.5f)
                quadTo(760f, 358f, 760f, 320f)
                lineTo(760f, 280f)
                quadTo(760f, 280f, 760f, 280f)
                quadTo(760f, 280f, 760f, 280f)
                lineTo(680f, 280f)
                lineTo(680f, 432f)
                close()
                moveTo(480f, 380f)
                quadTo(480f, 380f, 480f, 380f)
                quadTo(480f, 380f, 480f, 380f)
                lineTo(480f, 380f)
                quadTo(480f, 380f, 480f, 380f)
                quadTo(480f, 380f, 480f, 380f)
                lineTo(480f, 380f)
                quadTo(480f, 380f, 480f, 380f)
                quadTo(480f, 380f, 480f, 380f)
                lineTo(480f, 380f)
                quadTo(480f, 380f, 480f, 380f)
                quadTo(480f, 380f, 480f, 380f)
                close()
            }
        }.build()

        return _TrophyIcon!!
    }

@Suppress("ObjectPropertyName")
private var _TrophyIcon: ImageVector? = null
