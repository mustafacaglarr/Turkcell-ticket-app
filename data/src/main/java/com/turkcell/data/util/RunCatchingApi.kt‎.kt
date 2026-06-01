package com.turkcell.data.util

import com.turkcell.data.network.ApiException
import com.turkcell.data.network.NetworkException
import retrofit2.HttpException
import java.io.IOException

// Todo: İlerde tekrar konuşalım.
suspend inline fun <T> runCatchingApi(crossinline block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch(e: HttpException)
{
    val errorMessage = e.response()?.errorBody()?.string()?.takeIf { it.isNotBlank() } ?: e.message()
    Result.failure(ApiException(code = e.code(), errorMessage = errorMessage, cause=e))
} catch(e: IOException)
{
    Result.failure(NetworkException(e))
} catch(e: Exception)
{
    Result.failure(e)
}
