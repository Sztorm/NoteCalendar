package com.sztorm.notecalendar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nulabinc.zxcvbn.Zxcvbn

sealed class PasswordStrength(val value: Float) {
    object VeryWeak : PasswordStrength(0f)
    object Weak : PasswordStrength(0.25f)
    object Moderate : PasswordStrength(0.5f)
    object Strong : PasswordStrength(0.75f)
    object VeryStrong : PasswordStrength(1f)
}

data class PasswordStrengthTexts(
    val weak: String,
    val moderate: String,
    val strong: String,
) {
    companion object {
        fun english() = PasswordStrengthTexts(
            weak = "Weak",
            moderate = "Moderate",
            strong = "Strong"
        )
    }
}

fun PasswordStrength.name(texts: PasswordStrengthTexts) = when (this) {
    PasswordStrength.VeryWeak -> texts.weak
    PasswordStrength.Weak -> texts.weak
    PasswordStrength.Moderate -> texts.moderate
    PasswordStrength.Strong -> texts.strong
    PasswordStrength.VeryStrong -> texts.strong
}

val PasswordStrength.color
    get() = when (this) {
        PasswordStrength.VeryWeak -> Color(0xFFB00020)
        PasswordStrength.Weak -> Color(0xFFD85010)
        PasswordStrength.Moderate -> Color(0xFFFFA000)
        PasswordStrength.Strong -> Color(0xFF808D1A)
        PasswordStrength.VeryStrong -> Color(0xFF007A33)
    }

private val PasswordStrengthMeasurer = Zxcvbn()

fun measurePasswordStrength(password: CharSequence): PasswordStrength {
    val score = PasswordStrengthMeasurer.measure(password).score

    return when (score) {
        0 -> PasswordStrength.VeryWeak
        1 -> PasswordStrength.Weak
        2 -> PasswordStrength.Moderate
        3 -> PasswordStrength.Strong
        else -> PasswordStrength.VeryStrong
    }
}

@Composable
fun PasswordStrengthIndicator(
    strength: PasswordStrength,
    modifier: Modifier = Modifier,
    texts: PasswordStrengthTexts = PasswordStrengthTexts.english()
) {
    val strengthColor = strength.color

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                progress = { strength.value },
                color = strengthColor,
                modifier = modifier.padding(vertical = 4.dp)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = strength.name(texts),
                color = strengthColor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}