package io.github.maximerollin.yams.core.designsystem.preview

import androidx.compose.ui.tooling.preview.Preview

private const val PhoneDevice = "spec:width=411dp,height=891dp,dpi=420"
private const val TabletDevice = "spec:width=800dp,height=1280dp,dpi=240"

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "phone/fr", group = "phone/fr", locale = "fr", device = PhoneDevice, showSystemUi = true)
@Preview(name = "phone/en", group = "phone/en", locale = "en", device = PhoneDevice, showSystemUi = true)
@Preview(name = "phone/es", group = "phone/es", locale = "es", device = PhoneDevice, showSystemUi = true)
@Preview(name = "phone/de", group = "phone/de", locale = "de", device = PhoneDevice, showSystemUi = true)
@Preview(name = "phone/it", group = "phone/it", locale = "it", device = PhoneDevice, showSystemUi = true)
public annotation class YamsPhoneStoreScreenshotPreviews

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "tablet/fr", group = "tablet/fr", locale = "fr", device = TabletDevice, showSystemUi = true)
@Preview(name = "tablet/en", group = "tablet/en", locale = "en", device = TabletDevice, showSystemUi = true)
@Preview(name = "tablet/es", group = "tablet/es", locale = "es", device = TabletDevice, showSystemUi = true)
@Preview(name = "tablet/de", group = "tablet/de", locale = "de", device = TabletDevice, showSystemUi = true)
@Preview(name = "tablet/it", group = "tablet/it", locale = "it", device = TabletDevice, showSystemUi = true)
public annotation class YamsTabletStoreScreenshotPreviews

@YamsPhoneStoreScreenshotPreviews
@YamsTabletStoreScreenshotPreviews
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
public annotation class YamsStoreScreenshotPreviews
