package pl.applover.android.mvvmtest.vvm.example.next_example.example_list

import android.arch.lifecycle.LiveData
import pl.applover.android.mvvmtest.util.architecture.SingleEvent

/**
 * Created by Janusz Hain on 2018-06-11.
 */
interface ExampleListFragmentNavigator {
    fun fragmentClickedLiveData(): LiveData<SingleEvent<String>>
}