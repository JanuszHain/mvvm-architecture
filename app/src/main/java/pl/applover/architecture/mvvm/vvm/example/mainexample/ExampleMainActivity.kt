package pl.applover.architecture.mvvm.vvm.example.mainexample

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.example_activity_main.*
import pl.applover.architecture.mvvm.App
import pl.applover.architecture.mvvm.R
import pl.applover.architecture.mvvm.util.extensions.goToActivity
import pl.applover.architecture.mvvm.vvm.example.nextexample.NextExampleActivity
import javax.inject.Inject


class ExampleMainActivity : DaggerAppCompatActivity() {


    @Inject
    internal lateinit var viewModelFactory: ExampleMainViewModelFactory
    private lateinit var viewModel: ExampleMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_activity_main)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ExampleMainViewModel::class.java)
        viewModel.someEvent.observe(this, Observer { event -> println(event?.getContentIfNotHandled(this)) })
        viewModel.title.observe(this, Observer { title -> textViewHelloWorld.text = title })

        buttonGoToNextExampleActivity.setOnClickListener {
            println("Go to next Activity")
            goToNextActivity()
        }
    }

    private fun goToNextActivity() {
        goToActivity(NextExampleActivity::class.java, true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.activityOnResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        watchLeaks()
    }

    private fun watchLeaks() {
        App.refWatcher.watch(this)
        App.refWatcher.watch(viewModelFactory)
    }

}
