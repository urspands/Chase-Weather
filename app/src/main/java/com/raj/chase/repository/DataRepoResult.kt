package com.raj.chase.repository

sealed class DataRepoResult<out R> {
    data class Success<out S>(val data: S) : DataRepoResult<S>()
    data class Error(val exception: Exception) : DataRepoResult<Nothing>()
}
