package org.firmanmardiyanto.core.di

import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.firmanmardiyanto.core.data.AuthRepository
import org.firmanmardiyanto.core.data.DataSourceRepository
import org.firmanmardiyanto.core.data.StoryRepository
import org.firmanmardiyanto.core.data.source.local.LocalDataSource
import org.firmanmardiyanto.core.data.source.local.room.StoriesDatabase
import org.firmanmardiyanto.core.data.source.remote.RemoteDataSource
import org.firmanmardiyanto.core.data.source.remote.network.ApiService
import org.firmanmardiyanto.core.domain.repository.IAuthRepository
import org.firmanmardiyanto.core.domain.repository.IDataSourceRepository
import org.firmanmardiyanto.core.domain.repository.IStoryRepository
import org.firmanmardiyanto.core.utils.AuthInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(AuthInterceptor(get()))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }
    single {
        get<Retrofit>().create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { RemoteDataSource(get(), get()) }
    single { LocalDataSource(get(),get()) }
    single<IStoryRepository> {
        StoryRepository(
            get(), get(), androidContext()
        )
    }
    single<IDataSourceRepository> {
        DataSourceRepository(
            androidContext()
        )
    }
    single<IAuthRepository> {
        AuthRepository(
            get(), androidContext()
        )
    }
}

val databaseModule = module {
    factory { get<StoriesDatabase>().storiesDao() }
    factory { get<StoriesDatabase>().remoteKeysDao() }
    single {
        val passphrase: ByteArray = SQLiteDatabase.getBytes("storyapp".toCharArray())
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(
            androidContext(),
            StoriesDatabase::class.java, "stories.db"
        ).fallbackToDestructiveMigration()
            .openHelperFactory(factory)
            .build()
    }
}