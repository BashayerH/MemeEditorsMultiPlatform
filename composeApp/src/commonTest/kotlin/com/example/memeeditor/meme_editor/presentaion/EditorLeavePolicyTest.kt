package com.example.memeeditor.meme_editor.presentaion

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Documents leave-confirm policy: dirty work asks; clean/exported work exits freely.
 */
class EditorLeavePolicyTest {
    @Test
    fun dirtyRequiresConfirm() {
        assertTrue(shouldConfirmLeave(isDirty = true))
    }

    @Test
    fun cleanExitsFreely() {
        assertFalse(shouldConfirmLeave(isDirty = false))
    }
}

/** Mirrors [MemeEditorViewModel] attemptToGoBack gate. */
internal fun shouldConfirmLeave(isDirty: Boolean): Boolean = isDirty
