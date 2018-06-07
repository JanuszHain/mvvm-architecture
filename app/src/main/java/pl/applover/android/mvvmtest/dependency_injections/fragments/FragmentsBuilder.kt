package pl.applover.android.mvvmtest.dependency_injections.fragments

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.applover.android.mvvmtest.dependency_injections.fragments.modules.ExampleListFragmentModule
import pl.applover.android.mvvmtest.vvm.example.next_example.list.ExampleListFragment

/**
 * Created by Janusz Hain on 2018-06-06.
 */
@Module
abstract class FragmentsBuilder {

    @ContributesAndroidInjector(modules = arrayOf(ExampleListFragmentModule::class))
    abstract fun bindExampleListFragment(): ExampleListFragment
}