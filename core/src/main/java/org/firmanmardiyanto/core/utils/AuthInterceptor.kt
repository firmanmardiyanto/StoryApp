package org.firmanmardiyanto.core.utils

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.firmanmardiyanto.core.domain.repository.IDataSourceRepository

class AuthInterceptor(private val dataSourceRepository: IDataSourceRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        runBlocking {
            dataSourceRepository.userPreference().first().let { user ->
                user?.let {
                    requestBuilder.addHeader("Authorization", "Bearer ${user.token}")
                }
            }
        }

        val response: Response = chain.proceed(requestBuilder.build())
        if (response.code == 401) {
            runBlocking {
                dataSourceRepository.updateUserPreference(null)
            }
        }
        return response
    }
}