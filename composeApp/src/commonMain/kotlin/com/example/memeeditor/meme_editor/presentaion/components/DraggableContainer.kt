package com.example.memeeditor.meme_editor.presentaion.components


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.memeeditor.meme_editor.presentaion.MemeText
import com.example.memeeditor.meme_editor.presentaion.TextBoxInteractionState
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DraggableContainer(
    children: List<MemeText>,
    textBoxInteractionState: TextBoxInteractionState,
    onChildTransformChanged: (id: String, offset: Offset, rotation: Float, scale: Float) -> Unit,
    onChildClick: (String) -> Unit,
    onChildDoubleClick: (String) -> Unit,
    onChildTextChange: (id: String, text: String) -> Unit,
    onChildDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier) {
        val parentWidth = constraints.maxWidth
        val parentHeight = constraints.maxHeight

        children.forEach { child ->
            var childWidth by remember(child.id) {
                mutableStateOf(0)
            }
            var childHeight by remember(child.id) {
                mutableStateOf(0)
            }

            val isSelected = textBoxInteractionState is TextBoxInteractionState.Selected &&
                textBoxInteractionState.textBoxId == child.id

            val transformableState = rememberTransformableState { scaleChange, panChange, rotationChange ->
                val newRotation = child.rotation + rotationChange

                val angle = newRotation * PI.toFloat() / 180f
                val cosA = cos(angle)
                val sinA = sin(angle)

                val rotatedPanX = panChange.x * cosA - panChange.y * sinA
                val rotatedPanY = panChange.x * sinA + panChange.y * cosA

                val newScale = (child.scale * scaleChange).coerceIn(0.5f, 2f)

                val scaledWidth = childWidth * child.scale
                val scaledHeight = childHeight * child.scale

                val visualWidth = abs(scaledWidth * cosA) + abs(scaledHeight * sinA)
                val visualHeight = abs(scaledWidth * sinA) + abs(scaledHeight * cosA)

                val scaleOffsetX = (scaledWidth - childWidth) / 2
                val scaleOffsetY = (scaledHeight - childHeight) / 2

                val rotationOffsetX = (visualWidth - scaledWidth) / 2
                val rotationOffsetY = (visualHeight - scaledHeight) / 2

                val minX = scaleOffsetX + rotationOffsetX
                val maxX = parentWidth - childWidth - scaleOffsetX - rotationOffsetX
                val minY = scaleOffsetY + rotationOffsetY
                val maxY = parentHeight - childHeight - scaleOffsetY - rotationOffsetY

                val newOffset = Offset(
                    x = (child.offsetRatioX * parentWidth + child.scale * rotatedPanX).coerceIn(
                        minimumValue = minOf(minX, maxX),
                        maximumValue = maxOf(minX, maxX)
                    ),
                    y = (child.offsetRatioY * parentHeight + child.scale * rotatedPanY).coerceIn(
                        minimumValue = minOf(minY, maxY),
                        maximumValue = maxOf(minY, maxY)
                    ),
                )

                onChildTransformChanged(
                    child.id,
                    newOffset,
                    newRotation,
                    newScale
                )
            }

            Box(
                modifier = Modifier
                    .onSizeChanged {
                        childWidth = it.width
                        childHeight = it.height
                    }
                    .graphicsLayer {
                        translationX = child.offsetRatioX * parentWidth
                        translationY = child.offsetRatioY * parentHeight
                        rotationZ = child.rotation
                        scaleX = child.scale
                        scaleY = child.scale
                    }
                    .transformable(transformableState)
            ) {
                MemeTextBox(
                    memeText = child,
                    textBoxInteractionState = textBoxInteractionState,
                    maxWidth = with(density) {
                        (parentWidth / child.scale).toDp()
                    },
                    maxHeight = with(density) {
                        (parentHeight / child.scale).toDp()
                    },
                    onClick = {
                        onChildClick(child.id)
                    },
                    onDoubleClick = {
                        onChildDoubleClick(child.id)
                    },
                    onTextChange = {
                        onChildTextChange(child.id, it)
                    },
                    onDeleteClick = {
                        onChildDeleteClick(child.id)
                    }
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.35f), CircleShape)
                            .pointerInput(child.id, child.scale, parentWidth, parentHeight) {
                                detectDragGestures { change, dragAmount ->
                                    val factor = 1f - dragAmount.y * 0.004f
                                    val newScale = (child.scale * factor).coerceIn(0.5f, 2f)
                                    onChildTransformChanged(
                                        child.id,
                                        Offset(
                                            child.offsetRatioX * parentWidth,
                                            child.offsetRatioY * parentHeight
                                        ),
                                        child.rotation,
                                        newScale
                                    )
                                    change.consume()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Resize text",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(22.dp)
                                .rotate(45f)
                        )
                    }
                }
            }
        }
    }
}
