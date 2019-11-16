package com.kinvey.sample.testdrive

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kinvey.android.Client
import com.kinvey.android.Client.Builder
import com.kinvey.android.callback.KinveyDeleteCallback
import com.kinvey.android.callback.KinveyReadCallback
import com.kinvey.android.callback.KinveyUserCallback
import com.kinvey.android.model.User
import com.kinvey.android.store.DataStore
import com.kinvey.android.store.UserStore
import com.kinvey.java.AbstractClient
import com.kinvey.java.Query
import com.kinvey.java.core.KinveyClientCallback
import com.kinvey.java.dto.BaseUser
import com.kinvey.java.model.KinveyReadResponse
import com.kinvey.java.store.StoreType
import com.kinvey.sample.testdrive.Constants.COLLECTION_NAME
import com.kinvey.sample.testdrive.Constants.ENTITY_ID
import com.kinvey.sample.testdrive.Constants.USER_NAME
import com.kinvey.sample.testdrive.Constants.USER_PASSWORD
import com.kinvey.sample.testdrive.Constants._ID
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var kinveyClient: Client<User>? = null
    private var dataStore: DataStore<Entity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshProgress?.isIndeterminate = true
        kinveyClient = App.instance?.kinveyClient
        dataStore = DataStore.collection(COLLECTION_NAME, Entity::class.java, StoreType.NETWORK, kinveyClient)
        initLogin()
    }

    private fun initLogin() {
        val isLogin = kinveyClient?.isUserLoggedIn ?: false
        loginBtn.text = if (isLogin) "Logout" else "Login"
        UiUtils.enableLayout(buttonsPanel, isLogin)
        loginBtn.setOnClickListener {
            if (kinveyClient?.isUserLoggedIn == false) { login() }
            else logout()
        }
    }

    private fun login() {
        showProgress(true)
        if (kinveyClient?.isUserLoggedIn == false) {
            try {
                UserStore.login(USER_NAME, USER_PASSWORD, kinveyClient as Client<User>,
                object : KinveyUserCallback<User> {
                    override fun onSuccess(result: User) {
                        showProgress(false)
                        Timber.i("Logged in successfully as ${result.id}")
                        showToast("New implicit user logged in successfully as ${result.id}")
                        initLogin()
                    }
                    override fun onFailure(error: Throwable) {
                        showProgress(false)
                        Timber.e("Login Failure: $error")
                        showToast("Login error: ${error.message}")
                    }
                })
            } catch (e: IOException) {
                Timber.e(e)
            }
        } else {
            showToast("Using cached implicit user ${kinveyClient?.activeUser?.id}")
        }
    }

    private fun logout() {
        showProgress(true)
        UserStore.logout(kinveyClient as AbstractClient<BaseUser>,
        object: KinveyClientCallback<Void?> {
            override fun onSuccess(result: Void?) {
                showProgress(false)
                Timber.i("Logged out successfully")
                showToast("Logged out successfully")
                initLogin()
            }
            override fun onFailure(error: Throwable) {
                showProgress(false)
                Timber.e("Login Failure: $error")
                showToast("Login error: ${error.message}")
            }
        })
    }

    fun onLoadClick(view: View?) {
        showProgress(true)
        dataStore?.find(ENTITY_ID, object : KinveyClientCallback<Entity> {
            override fun onSuccess(result: Entity) {
                showProgress(false)
                showToast("Save Worked!\nTitle: ${result.title}\nDescription: ${result.description}")
            }
            override fun onFailure(error: Throwable) {
                showProgress(false)
                Timber.e("AppData.getEntity Failure $error")
                showToast("Save Failed!\n: ${error.message}")
            }
        })
    }

    private fun showProgress(show: Boolean) {
        val state = if(show) View.VISIBLE else View.GONE
        refreshProgress?.visibility = state
    }

    private fun showToast(msg: String) = Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()

    fun onQueryClick(view: View?) {
        showProgress(true)
        val myQuery = kinveyClient?.query()
        myQuery?.equals(_ID, ENTITY_ID)
        dataStore?.find(myQuery as Query, object : KinveyReadCallback<Entity> {
            override fun onSuccess(result: KinveyReadResponse<Entity>?) {
                showProgress(false)
                showToast("query Worked!\n Got: ${result?.result}")
            }
            override fun onFailure(error: Throwable) {
                showProgress(false)
                Timber.e("AppData.get by Query Failure $error")
                showToast("query Failed!\n ${error.message}")
            }
        }, null)
    }

    fun onLoadAllClick(view: View?) {
        showProgress(true)
        dataStore?.find(object : KinveyReadCallback<Entity> {
            override fun onSuccess(result: KinveyReadResponse<Entity>?) {
                showProgress(false)
                showToast("Get All Worked!\n Got: ${result?.result}")
            }
            override fun onFailure(error: Throwable) {
                showProgress(false)
                Timber.e("AppData.get all Failure $error")
                showToast("Get All error: ${error.message}")
            }
        }, null)
    }

    fun onSaveClick(view: View?) {
        showProgress(true)
        val entity = Entity(ENTITY_ID)
        entity.description = "This is a description of a dynamically-added Entity property."
        dataStore?.save(entity, object : KinveyClientCallback<Entity> {
            override fun onSuccess(result: Entity) {
                showProgress(false)
                showToast("Entity Saved\nTitle: ${result.title}\nDescription: ${result.description}")
            }

            override fun onFailure(error: Throwable) {
                showProgress(false)
                Timber.e("AppData.save Failure $error")
                showToast("Save error: ${error.message}")
            }
        })
    }

    fun onDeleteClick(view: View?) {
        showProgress(true)
        dataStore?.delete(ENTITY_ID, object : KinveyDeleteCallback {
            override fun onSuccess(result: Int?) {
                showProgress(false)
                showToast("Number of Entities Deleted: $result")
            }
            override fun onFailure(error: Throwable) {
                showProgress(false)
                Timber.e("AppData.delete Failure, $error")
                showToast("Delete error: ${error.message}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}