package com.example.wizardworld.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import wizardworld.sharedui.generated.resources.Res
import wizardworld.sharedui.generated.resources.ic_app_logo

@Composable
fun WizardTopBar(
    title: @Composable () -> Unit,
    actions: (@Composable () -> Unit)? = null,
) {
    TopAppBar(
        title = title,
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        actions = { if (actions != null) actions() },
        modifier = Modifier.shadow(elevation = 4.dp),
    )
}

@Composable
fun WizardTopBar(
    title: String,
    appLogoDescription: String? = "",
    actions: (@Composable () -> Unit)? = null,
) {
    WizardTopBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_app_logo),
                    contentDescription = appLogoDescription,
                    modifier = Modifier.size(40.dp),
                )
                Text(text = title)
            }
        },
        actions = actions,
    )
}
