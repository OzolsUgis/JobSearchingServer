package com.ugisozols.util

sealed class ValidationState {
    object ErrorFieldEmpty : ValidationState()
    object ErrorPasswordToShort : ValidationState()
    object ErrorPasswordsAreNotEqual : ValidationState()
    object ErrorEmailIsNotContainingChars  : ValidationState()
    object Success : ValidationState()
}
