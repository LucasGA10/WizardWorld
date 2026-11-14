package com.example.wizardworld.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val Plum = Color(0xFF2C1848)
private val Gold = Color(0xFF9A7420)
private val LightGold = Color(0xFFE8C97A)
private val StarWhite = Color(0xFFFFF6D4)

@Composable
fun AppLogo(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier =
            modifier
                .size(40.dp)
                .semantics { this.contentDescription = contentDescription },
    ) {
        val scaleFactor = size.width / 108f
        scale(scaleFactor, scaleFactor, pivot = Offset.Zero) {
            scale(1.35f, 1.35f, pivot = Offset(54f, 54f)) {
                drawPath(
                    path =
                        Path().apply {
                            moveTo(30f, 78f)
                            lineTo(30f, 54f)
                            lineTo(42f, 54f)
                            lineTo(42f, 66f)
                            lineTo(46f, 66f)
                            lineTo(46f, 46f)
                            lineTo(62f, 46f)
                            lineTo(62f, 62f)
                            lineTo(68f, 62f)
                            lineTo(68f, 58f)
                            lineTo(78f, 58f)
                            lineTo(78f, 78f)
                            close()
                        },
                    color = Plum,
                )
                drawPath(
                    path =
                        Path().apply {
                            moveTo(30f, 54f)
                            lineTo(36f, 40f)
                            lineTo(42f, 54f)
                            close()
                        },
                    color = Gold,
                )
                drawPath(
                    path =
                        Path().apply {
                            moveTo(46f, 46f)
                            lineTo(54f, 28f)
                            lineTo(62f, 46f)
                            close()
                        },
                    color = Gold,
                )
                drawPath(
                    path =
                        Path().apply {
                            moveTo(68f, 58f)
                            lineTo(73f, 46f)
                            lineTo(78f, 58f)
                            close()
                        },
                    color = Gold,
                )
                drawRect(LightGold, topLeft = Offset(34f, 60f), size = androidx.compose.ui.geometry.Size(4f, 8f))
                drawRect(LightGold, topLeft = Offset(51f, 54f), size = androidx.compose.ui.geometry.Size(4f, 10f))
                drawRect(LightGold, topLeft = Offset(71f, 64f), size = androidx.compose.ui.geometry.Size(4f, 8f))
                drawRect(LightGold, topLeft = Offset(50f, 70f), size = androidx.compose.ui.geometry.Size(8f, 8f))

                rotate(degrees = -36f, pivot = Offset(54f, 60f)) {
                    drawCircle(color = Gold, radius = 5f, center = Offset(40f, 60f))
                    drawRect(
                        color = LightGold,
                        topLeft = Offset(44f, 57.4f),
                        size = androidx.compose.ui.geometry.Size(27f, 5.2f),
                    )
                    drawPath(
                        path =
                            Path().apply {
                                moveTo(69f, 57.4f)
                                lineTo(75f, 60f)
                                lineTo(69f, 62.6f)
                                close()
                            },
                        color = LightGold,
                    )
                    drawPath(
                        path =
                            Path().apply {
                                moveTo(76f, 54.5f)
                                lineTo(77.6f, 58.2f)
                                lineTo(81.5f, 60f)
                                lineTo(77.6f, 61.8f)
                                lineTo(76f, 65.5f)
                                lineTo(74.4f, 61.8f)
                                lineTo(70.5f, 60f)
                                lineTo(74.4f, 58.2f)
                                close()
                            },
                        color = StarWhite,
                    )
                }
            }
        }
    }
}
