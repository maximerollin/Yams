package io.github.maximerollin.yams.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val YamsIcons.PencilSparkles: ImageVector
    get() {
        if (_PencilSparkles != null) {
            return _PencilSparkles!!
        }
        _PencilSparkles = ImageVector.Builder(
            name = "PencilSparkles",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(10f, 3f)
                horizontalLineTo(8f)
                moveTo(15.007f, 5.008f)
                lineTo(18.994f, 8.994f)
                moveTo(20f, 15f)
                verticalLineTo(19f)
                moveTo(21.174f, 6.813f)
                curveTo(22.275f, 5.711f, 22.275f, 3.927f, 21.174f, 2.826f)
                curveTo(20.073f, 1.725f, 18.289f, 1.725f, 17.188f, 2.826f)
                lineTo(3.842f, 16.175f)
                curveTo(3.608f, 16.409f, 3.436f, 16.694f, 3.342f, 17.005f)
                lineTo(2.021f, 21.357f)
                curveTo(1.911f, 21.721f, 2.258f, 22.068f, 2.644f, 21.979f)
                lineTo(6.997f, 20.659f)
                curveTo(7.308f, 20.565f, 7.593f, 20.393f, 7.827f, 20.162f)
                close()
                moveTo(22f, 17f)
                horizontalLineTo(18f)
                moveTo(4f, 5f)
                verticalLineTo(9f)
                moveTo(6f, 7f)
                horizontalLineTo(2f)
                moveTo(9f, 2f)
                verticalLineTo(4f)
            }
        }.build()

        return _PencilSparkles!!
    }

private var _PencilSparkles: ImageVector? = null
