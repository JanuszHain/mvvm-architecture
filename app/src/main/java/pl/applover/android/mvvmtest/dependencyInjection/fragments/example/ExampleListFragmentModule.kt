package pl.applover.android.mvvmtest.dependencyInjection.fragments.example

import dagger.Module
import dagger.Provides
import pl.applover.android.mvvmtest.vvm.example.nextExample.exampleList.ExampleListFragmentRouter
import pl.applover.android.mvvmtest.vvm.example.nextExample.exampleList.ExampleListViewModelFactory

/**
 * Created by Janusz Hain on 2018-06-06.
 */
@Module
class ExampleListFragmentModule {
    @Provides
    fun provideViewModelFactory(exampleListFragmentRouter: ExampleListFragmentRouter) = ExampleListViewModelFactory(exampleListFragmentRouter)
}