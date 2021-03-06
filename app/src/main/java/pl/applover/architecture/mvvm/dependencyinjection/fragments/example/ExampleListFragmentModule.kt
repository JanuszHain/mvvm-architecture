package pl.applover.architecture.mvvm.dependencyinjection.fragments.example

import dagger.Module
import dagger.Provides
import pl.applover.architecture.mvvm.data.example.repositories.ExampleCitiesRepository
import pl.applover.architecture.mvvm.util.other.SchedulerProvider
import pl.applover.architecture.mvvm.vvm.example.nextexample.examplelist.ExampleListFragmentRouter
import pl.applover.architecture.mvvm.vvm.example.nextexample.examplelist.ExampleListViewModelFactory

/**
 * Created by Janusz Hain on 2018-06-06.
 */
@Module
class ExampleListFragmentModule {
    @Provides
    fun provideViewModelFactory(router: ExampleListFragmentRouter,
                                schedulerProvider: SchedulerProvider,
                                repository: ExampleCitiesRepository) =
            ExampleListViewModelFactory(router, schedulerProvider, repository)
}