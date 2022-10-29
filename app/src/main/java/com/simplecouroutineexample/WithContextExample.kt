package gyanani.harish.multithreadingusecases.coroutine.level1

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import gyanani.harish.multithreadingusecases.ApiUtils
import gyanani.harish.multithreadingusecases.Constants
import gyanani.harish.multithreadingusecases.R
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*

Reference video - https://www.youtube.com/watch?v=71NrkkRNXG4

 fun startExample(){
        lifecycleScope.launch(Dispatchers.Main){
            Log.d(Constants.LOG_TAG, "Thread in scope while calling api"+Thread.currentThread())
            val result = ApiUtils.apiCalling(1)
            findViewById<TextView>(R.id.txt_api_state).text = result
            Log.d(Constants.LOG_TAG, "Thread in scope while changing ui "+Thread.currentThread())
        }
}
       Output -
       19:50:58.218  D  Thread in scope while calling apiThread[main,5,main]
19:50:58.219  D  ApiCalling 1Thread[main,5,main]
19:50:58.249  D  ApiCalling 1 inputStream Thread[main,5,main]
19:50:58.259  D  ApiCalling 1 android.os.NetworkOnMainThreadException
                 	at android.os.StrictMode$AndroidBlockGuardPolicy.onNetwork(StrictMode.java:1565)
                 	at java.net.Inet6AddressImpl.lookupHostByName(Inet6AddressImpl.java:115)
                 	at
19:50:58.259  D  Thread in scope while changing ui Thread[main,5,main]

        with above code we get android.os.NetworkOnMainThreadException
 ---------------------------------------------------------------------
 fun startExample(){
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.d(Constants.LOG_TAG, "CoroutineExceptionHandler got $exception")
        }
        lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler){
            Log.d(Constants.LOG_TAG, "Thread in scope while calling api"+Thread.currentThread())
            val result = ApiUtils.apiCalling(1)
            findViewById<TextView>(R.id.txt_api_state).text = result
            Log.d(Constants.LOG_TAG, "Thread in scope while changing ui "+Thread.currentThread())
        }
    }
 Output -
19:55:48.410  D  Thread in scope while calling apiThread[DefaultDispatcher-worker-2,5,main]
19:55:48.410  D  ApiCalling 1Thread[DefaultDispatcher-worker-2,5,main]
19:55:48.421  D  ApiCalling 1 inputStream Thread[DefaultDispatcher-worker-2,5,main]
19:55:49.181  D  ApiCalling 1  readStream Thread[DefaultDispatcher-worker-2,5,main]
19:55:49.182  D  ApiCalling 1  reader Thread[DefaultDispatcher-worker-2,5,main]
19:55:49.183  D  ApiCalling 1 try reader.close()
19:55:49.186  D  CoroutineExceptionHandler got android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.

        with above code we get - Only the original thread that created a view hierarchy can touch its views.
 ---------------------------------------------------------------------
fun startExample(){
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.d(Constants.LOG_TAG, "CoroutineExceptionHandler got $exception")
        }
        lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler){
            Log.d(Constants.LOG_TAG, "Thread in scope while calling api"+Thread.currentThread())
            val result = ApiUtils.apiCalling(1)
            lifecycleScope.launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.txt_api_state).text = result
                Log.d(
                    Constants.LOG_TAG,
                    "Thread in scope while changing ui " + Thread.currentThread()
                )
            }
            Log.d(
                Constants.LOG_TAG,
                "Thread in scope after changing ui " + Thread.currentThread()
            )
        }
 we need solution for both - lets start main coroutine inside IO
 Output -
 19:59:31.394  D  Thread in scope while calling apiThread[DefaultDispatcher-worker-1,5,main]
19:59:31.394  D  ApiCalling 1Thread[DefaultDispatcher-worker-1,5,main]
19:59:31.405  D  ApiCalling 1 inputStream Thread[DefaultDispatcher-worker-1,5,main]
19:59:32.252  D  ApiCalling 1  readStream Thread[DefaultDispatcher-worker-1,5,main]
19:59:32.252  D  ApiCalling 1  reader Thread[DefaultDispatcher-worker-1,5,main]
19:59:32.253  D  ApiCalling 1 try reader.close()
19:59:32.257  D  Thread in scope after changing ui Thread[DefaultDispatcher-worker-1,5,main]
19:59:32.257  D  Thread in scope while changing ui Thread[main,5,main]

but problem is it does not work in sequential manner
withContext can solve this problem-
fun startExample(){
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.d(Constants.LOG_TAG, "CoroutineExceptionHandler got $exception")
        }
        lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler){
            Log.d(Constants.LOG_TAG, "Thread in scope while calling api"+Thread.currentThread())
            val result = ApiUtils.apiCalling(1)
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.txt_api_state).text = result
                Log.d(
                    Constants.LOG_TAG,
                    "Thread in scope while changing ui " + Thread.currentThread()
                )
            }
            Log.d(
                Constants.LOG_TAG,
                "Thread in scope after changing ui " + Thread.currentThread()
            )
        }
    }

    Output -
    20:01:54.266  D  Thread in scope while calling apiThread[DefaultDispatcher-worker-1,5,main]
20:01:54.267  D  ApiCalling 1Thread[DefaultDispatcher-worker-1,5,main]
20:01:54.279  D  ApiCalling 1 inputStream Thread[DefaultDispatcher-worker-1,5,main]
20:01:54.945  D  ApiCalling 1  readStream Thread[DefaultDispatcher-worker-1,5,main]
20:01:54.945  D  ApiCalling 1  reader Thread[DefaultDispatcher-worker-1,5,main]
20:01:54.946  D  ApiCalling 1 try reader.close()
20:01:54.953  D  Thread in scope while changing ui Thread[main,5,main]
20:01:54.954  D  Thread in scope after changing ui Thread[DefaultDispatcher-worker-1,5,main]

 */
class WithContextExample: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startExample()
    }
    fun startExample(){
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.d(Constants.LOG_TAG, "CoroutineExceptionHandler got $exception")
        }
        lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler){
            Log.d(Constants.LOG_TAG, "Thread in scope while calling api"+Thread.currentThread())
            val result = ApiUtils.apiCalling(1)
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.txt_api_state).text = result
                Log.d(
                    Constants.LOG_TAG,
                    "Thread in scope while changing ui " + Thread.currentThread()
                )
            }
            Log.d(
                Constants.LOG_TAG,
                "Thread in scope after changing ui " + Thread.currentThread()
            )
        }
    }
}