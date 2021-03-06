package pl.applover.architecture.mvvm.vvm.example.nextexample.examplelist


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.example_fragment_list.*
import kotlinx.android.synthetic.main.example_item_network_state.*
import pl.applover.architecture.mvvm.App
import pl.applover.architecture.mvvm.R
import pl.applover.architecture.mvvm.adapters.cities.ExampleCityAdapter
import pl.applover.architecture.mvvm.util.architecture.network.NetworkState
import pl.applover.architecture.mvvm.util.extensions.showToast
import pl.applover.architecture.mvvm.util.ui.hide
import pl.applover.architecture.mvvm.util.ui.show
import javax.inject.Inject

class ExampleListFragment : DaggerFragment() {

    @Inject
    internal lateinit var viewModelFactory: ExampleListViewModelFactory
    private lateinit var viewModel: ExampleListViewModel

    private val adapter = ExampleCityAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ExampleListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setViewModelListeners()
        return inflater.inflate(R.layout.example_fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewCities.adapter = adapter
        setViewListeners()
        setViewModelObservers()
    }

    private fun setViewModelObservers() {
        viewModel.mldNetworkState.observe(this, Observer {
            it?.let {
                manageNetworkStateView(it)
            }
        })

        viewModel.mldCitiesLiveData.observe(this, Observer {
            it?.let {
                adapter.replaceCities(it)
            }
        })
    }

    private fun manageNetworkStateView(networkState: NetworkState) {
        textViewNetworkState.text = networkState.networkStatus.name

        when (networkState.networkStatus) {
            NetworkState.State.FAILED -> {
                layoutExampleItemNetworkState.show()
                progressBarNetwork.hide()
                buttonRetry.show()
            }
            NetworkState.State.RUNNING -> {
                layoutExampleItemNetworkState.show()
                progressBarNetwork.show()
                buttonRetry.hide()
            }
            NetworkState.State.SUCCESS -> {
                layoutExampleItemNetworkState.hide()
            }
        }
    }

    private fun setViewModelListeners() {
        viewModel.mldSomeToast.observe(this, Observer {
            it?.getContentIfNotHandled(this)?.let {
                showToast(it)
            }
        })

        viewModel.mldCitiesFromLocal.observe(this, Observer {
            if (it == true) {
                buttonDataFromDb.text = "Show online data"
                buttonDataFromDb.setOnClickListener {
                    viewModel.loadCities()
                }
            } else {
                buttonDataFromDb.text = "Show db data"
                buttonDataFromDb.setOnClickListener {
                    viewModel.loadCitiesFromDb()
                }
            }
        })
    }

    private fun setViewListeners() {
        buttonNavigatorTest.setOnClickListener {
            viewModel.fragmentClicked()
            viewModel.saveCitiesToDb()
        }

        buttonRetry.setOnClickListener {
            viewModel.loadCities()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.showSomeToast()
        if (viewModel.mldCitiesLiveData.value!!.size == 0)
            viewModel.loadCities()
    }

    override fun onDestroy() {
        super.onDestroy()
        watchLeaks()
    }

    private fun watchLeaks() {
        App.refWatcher.watch(this)
        App.refWatcher.watch(viewModelFactory)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExampleListFragment()
    }
}
