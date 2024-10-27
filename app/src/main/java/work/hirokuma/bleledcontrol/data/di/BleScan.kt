package work.hirokuma.bleledcontrol.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import work.hirokuma.bleledcontrol.data.BleLedControlRepository
import work.hirokuma.bleledcontrol.data.LedControlRepository
import work.hirokuma.bleledcontrol.data.ble.BleScan
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LedControlRepositoryModule {
    @Singleton
    @Provides
    fun provideLedControlRepository(bleScan: BleScan): LedControlRepository {
        return BleLedControlRepository(bleScan)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object BleScanModule {
    @Singleton
    @Provides
    fun provideBleScan(
        @ApplicationContext context: Context
    ): BleScan {
        return BleScan(context)
    }
}
