package pl.applover.android.mvvmtest.vm.fragments.example

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import pl.applover.android.mvvmtest.data.example.repositories.ExampleCitiesRepository
import pl.applover.android.mvvmtest.lambdaMock
import pl.applover.android.mvvmtest.models.example.ExampleCityModel
import pl.applover.android.mvvmtest.util.architecture.liveData.Event
import pl.applover.android.mvvmtest.util.architecture.rx.EmptyEvent
import pl.applover.android.mvvmtest.util.other.SchedulerProvider
import pl.applover.android.mvvmtest.vvm.example.nextExample.exampleList.ExampleListFragmentRouter
import pl.applover.android.mvvmtest.vvm.example.nextExample.exampleList.ExampleListViewModel
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean

class ExampleListViewModelUnitTest {

    /**
     * A JUnit Test Rule that swaps the background executor used by the Architecture Components with a
     * different one which executes each task synchronously.
     * <p>
     * You can use this rule for your host side tests that use Architecture Components.
     */
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRepository: ExampleCitiesRepository

    private lateinit var exampleListViewModel: ExampleListViewModel

    @Spy
    private val spiedRouter: ExampleListFragmentRouter = ExampleListFragmentRouter()

    @Mock
    private lateinit var mockedSender: ExampleListFragmentRouter.Sender

    @Mock
    private lateinit var mockedFragmentClickedSubject: PublishSubject<EmptyEvent>

    private val schedulerProvider = SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        setUpMocksForRouter()

        exampleListViewModel = ExampleListViewModel(spiedRouter, schedulerProvider, mockRepository)
    }

    private fun setUpMocksForRouter() {
        whenever(spiedRouter.sender).thenReturn(mockedSender)
        whenever(mockedSender.fragmentClicked).thenReturn(mockedFragmentClickedSubject)
    }

    @Test
    fun testFragmentClicked() {
        exampleListViewModel.fragmentClicked()
        verify(spiedRouter.sender.fragmentClicked, times(1)).onNext(any())
    }

    @Test
    fun testShowSomeToast() {
        exampleListViewModel.showSomeToast()
        assertEquals(Event("mldSomeToast"), exampleListViewModel.mldSomeToast.value)
    }

    /**
     * Test for LiveData event for paused Fragment
     */
    @Test
    fun testShowSomeToastForFragmentPaused() {
        val lifecycle = LifecycleRegistry(mock<LifecycleOwner>())
        val observer = lambdaMock<(Event<String>?) -> Unit>()

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        exampleListViewModel.showSomeToast()

        //Assert that sent Event wasn't handled
        assertEquals(false, exampleListViewModel.mldSomeToast.value?.hasBeenHandled)

        //Verify that no liveData event is sent when Fragment is created
        verify(observer, times(0)).invoke(any())

        //Verify that liveData event is sent after Fragment can manipulate UI
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

        exampleListViewModel.mldSomeToast.observe({ lifecycle }) {
            observer(it)
        }

        verify(observer, times(1)).invoke(any())

        //Assert that sent Event was handled
        assertEquals(true, exampleListViewModel.mldSomeToast.value?.hasBeenHandled)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

        //Verify that no new event wasn't sent after onStop in Fragment
        verify(observer, times(1)).invoke(any())

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        //Verify that new event wasn't sent again after onResume Fragment
        verify(observer, times(1)).invoke(any())
    }


    /**
     * Test for LiveData event for recreated Fragment
     */
    @Test
    fun testShowSomeToastForFragmentRecreated() {
        val lifecycle = LifecycleRegistry(mock<LifecycleOwner>())
        val liveDataUnit = lambdaMock<(Event<String>?) -> Unit>()

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        exampleListViewModel.showSomeToast()

        //Verify that no liveData event is sent when Fragment is created
        verify(liveDataUnit, times(0)).invoke(any())

        //Verify that liveData event is sent after Fragment can manipulate UI
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

        exampleListViewModel.mldSomeToast.observe({ lifecycle }) {
            liveDataUnit(it)

            //Assert that event is not null and content wasn't handled
            assertNotNull(it?.getContentIfNotHandled())
        }

        verify(liveDataUnit, times(1)).invoke(any())

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

        //Verify that no new event wasn't sent after onStop in Fragment
        verify(liveDataUnit, times(1)).invoke(any())

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

        //Verify that new event wasn't sent again after onStop Fragment
        verify(liveDataUnit, times(1)).invoke(any())

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

        //Verify that new event wasn't sent again after onStart in Fragment without subscribing again
        verify(liveDataUnit, times(1)).invoke(any())

        exampleListViewModel.mldSomeToast.observe({ lifecycle }) {
            liveDataUnit(it)

            //Assert that event is not null
            assertNotNull(it)
            //Assert content was handled already
            assertNull(it!!.getContentIfNotHandled())
        }

        //Verify that new event was sent again after onStart in Fragment after subscribing livedata again
        verify(liveDataUnit, times(2)).invoke(any())
    }

    @Test
    fun testLoadCities() {
        whenever(mockRepository.citiesFromNetwork()).thenReturn(Single.just(Response.success(
                listOf(
                        ExampleCityModel(id = "02mstvd091", name = "Warsaw", country = "Poland", lat = 52.22977, lng = 21.01178),
                        ExampleCityModel(id = "0dg2ykpttl", name = "Lodz", country = "Poland", lat = 51.75, lng = 19.46667)
                ))))

        val isInitialValue = AtomicBoolean(true)

        exampleListViewModel.mldCitiesLiveData.observeForever {
            if (!isInitialValue.get()) {
                assertEquals(2, it?.size)
            }
            isInitialValue.set(false)
        }

        exampleListViewModel.loadCities()
    }
}
