package work.hirokuma.bleledcontrol.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import work.hirokuma.bleledcontrol.data.BleLbsControlRepository
import work.hirokuma.bleledcontrol.data.LbsControlRepository
import work.hirokuma.bleledcontrol.data.ble.LbsControl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LbsControlRepositoryModule {
    @Singleton
    @Provides
    fun provideLbsControlRepository(lbsControl: LbsControl): LbsControlRepository {
        return BleLbsControlRepository(lbsControl)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object LbsControlModule {
    @Singleton
    @Provides
    fun provideLbsControl(
        @ApplicationContext context: Context
    ): LbsControl {
        return LbsControl(context)
    }
}
