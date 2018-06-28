package pl.applover.android.mvvmtest.dependencyInjection.other

import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.applover.android.mvvmtest.util.other.SchedulerProvider
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
class SchedulerProviderModule {

    @Provides
    @Singleton
    internal fun provideSchedulerProvider(): SchedulerProvider {
        return SchedulerProvider(Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())),
                AndroidSchedulers.mainThread())
    }
}