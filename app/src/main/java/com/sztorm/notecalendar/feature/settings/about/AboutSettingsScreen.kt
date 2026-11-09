package com.sztorm.notecalendar.feature.settings.about

import android.content.ClipData
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults.outlinedIconButtonColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.ui.components.preferences.Preference
import com.sztorm.notecalendar.ui.components.preferences.SubpreferenceScreen
import kotlinx.coroutines.launch

@Composable
private fun Link(
    text: String,
    url: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    fontSize: TextUnit = 16.sp,
    color: Color = MaterialTheme.colorScheme.secondary
) = Text(
    text = buildAnnotatedString {
        withLink(
            LinkAnnotation.Url(
                url = url,
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = color,
                        textDecoration = TextDecoration.Underline
                    )
                )
            )
        ) {
            append(text)
        }
    },
    style = style,
    fontSize = fontSize,
    modifier = modifier
)

@Composable
private fun CopyButton(
    onClick: () -> Unit,
    iconColor: Color,
    borderColor: Color,
    pressedBackgroundColor: Color,
) = OutlinedIconButton(
    onClick = onClick,
    border = BorderStroke(width = 1.dp, color = borderColor),
    colors = outlinedIconButtonColors(
        contentColor = pressedBackgroundColor,
    ),
    modifier = Modifier
        .padding(horizontal = 4.dp)
        .size(24.dp)
) {
    Icon(
        imageVector = ImageVector
            .vectorResource(R.drawable.icon_outline_content_copy),
        contentDescription = "copy",
        tint = iconColor,
        modifier = Modifier.size(16.dp)
    )
}

@Composable
private fun AboutPreference(
    title: String,
    summary: @Composable () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Row {
            Text(
                text = title,
                fontSize = 20.sp
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            summary()
            Spacer(Modifier.weight(1f))
            CopyButton(
                onClick = onCopy,
                iconColor = MaterialTheme.colorScheme.onBackground,
                borderColor = MaterialTheme.colorScheme.onBackground,
                pressedBackgroundColor = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun AboutPreference(
    title: String,
    summary: String,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) = AboutPreference(
    title = title,
    summary = {
        Text(
            text = summary,
            fontSize = 16.sp
        )
    },
    onCopy = onCopy,
    modifier = modifier,
)

@Composable
fun AboutSettingsScreen(
    appViewModel: AppViewModel,
    onBackButtonClick: () -> Unit
) {
    val themeColors = appViewModel.state.themeColors
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    SubpreferenceScreen(
        title = stringResource(R.string.Settings_About),
        backButtonIcon = painterResource(R.drawable.icon_rounded_arrow_back),
        iconTint = themeColors.textColor,
        onBackButtonClick = onBackButtonClick
    ) {
        AboutPreference(
            title = stringResource(R.string.Settings_About_Version),
            summary = AppInfo.VERSION,
            onCopy = {
                coroutineScope.launch {
                    clipboardManager.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "version", AppInfo.VERSION
                            )
                        )
                    )
                }
            }
        )
        AboutPreference(
            title = stringResource(R.string.Settings_About_Contact),
            summary = AppInfo.CONTACT_EMAIL,
            onCopy = {
                coroutineScope.launch {
                    clipboardManager.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "contact email", AppInfo.CONTACT_EMAIL
                            )
                        )
                    )
                }
            }
        )
        AboutPreference(
            title = stringResource(R.string.Settings_About_SourceCode),
            summary = {
                Link(
                    text = AppInfo.SOURCE_CODE_GITHUB,
                    url = AppInfo.SOURCE_CODE_GITHUB_URL
                )
            },
            onCopy = {
                coroutineScope.launch {
                    clipboardManager.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "source code url",
                                AppInfo.SOURCE_CODE_GITHUB_URL
                            )
                        )
                    )
                }
            }
        )
        AboutPreference(
            title = stringResource(R.string.Settings_About_License),
            summary = {
                Link(
                    text = AppInfo.LICENSE,
                    url = AppInfo.LICENSE_URL
                )
            },
            onCopy = {
                coroutineScope.launch {
                    clipboardManager.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "license url", AppInfo.LICENSE_URL
                            )
                        )
                    )
                }
            }
        )
        AboutPreference(
            title = stringResource(R.string.Settings_About_PrivacyPolicy),
            summary = {
                Link(
                    text = AppInfo.SOURCE_CODE_GITHUB,
                    url = AppInfo.PRIVACY_POLICY_URL
                )
            },
            onCopy = {
                coroutineScope.launch {
                    clipboardManager.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "privacy policy url",
                                AppInfo.PRIVACY_POLICY_URL
                            )
                        )
                    )
                }
            }
        )
        val title = stringResource(R.string.Settings_About_LibraryLicenses)

        Preference(
            title = title,
            titleColor = themeColors.textColor,
            icon = painterResource(R.drawable.icon_outline_rounded_license),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onClick = {
                context.startActivity(
                    LibsBuilder()
                        .withActivityTitle(title)
                        .withEdgeToEdge(true)
                        .withSearchEnabled(true)
                        .intent(context)
                )
            }
        )
    }
}