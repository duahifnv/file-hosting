package org.duahifnv.filehosting.mobile.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val NeomorphismLight = Color(0xFFE0E0E0)
val NeomorphismDark = Color(0xFFD0D0D0)
val NeomorphismShadowLight = Color(0xFFFFFFFF)
val NeomorphismShadowDark = Color(0xFFB0B0B0)

@Composable
fun NeomorphicCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NeomorphismLight)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = NeomorphismShadowDark,
                ambientColor = NeomorphismShadowDark
            )
            .shadow(
                elevation = -8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = NeomorphismShadowLight,
                ambientColor = NeomorphismShadowLight
            )
    ) {
        content()
    }
}

@Composable
fun NeomorphicButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(if (enabled) NeomorphismLight else NeomorphismDark)
            .shadow(
                elevation = if (enabled) 6.dp else 2.dp,
                shape = RoundedCornerShape(15.dp),
                spotColor = NeomorphismShadowDark,
                ambientColor = NeomorphismShadowDark
            )
            .shadow(
                elevation = if (enabled) -6.dp else -2.dp,
                shape = RoundedCornerShape(15.dp),
                spotColor = NeomorphismShadowLight,
                ambientColor = NeomorphismShadowLight
            )
    ) {
        androidx.compose.material3.Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.matchParentSize(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(15.dp),
            content = { content() }
        )
    }
}

@Composable
fun NeomorphicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) NeomorphismLight else NeomorphismDark)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = NeomorphismShadowDark,
                ambientColor = NeomorphismShadowDark
            )
            .shadow(
                elevation = -4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = NeomorphismShadowLight,
                ambientColor = NeomorphismShadowLight
            )
    ) {
        androidx.compose.material3.TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.matchParentSize(),
            placeholder = placeholder?.let { { androidx.compose.material3.Text(it) } },
            enabled = enabled,
            singleLine = singleLine,
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
