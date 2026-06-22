package tech.unrealistic.cineflix.data.remote
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import tech.unrealistic.cineflix.BuildConfig

object RetrofitClient {
    const val  BASE_URL= "https://api.themoviedb.org/3/"
    // ignore unwanted responses
    private val json = Json {ignoreUnknownKeys= true}

    private val apiKeyInterceptor= Interceptor {chain ->
        val originalRequest =  chain.request()
        val originalUrl = originalRequest.url
        val newUrl = originalUrl.newBuilder().
        addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
            .build()
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        return@Interceptor chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Shows the URL, headers, and full JSON!

    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()
    val tmdbService: TmdbService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
            .create(TmdbService::class.java)
    }
}