package com.toscano.test2.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.biometric.BiometricManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toscano.test2.R
import com.toscano.test2.data.network.entities.UsersDB
import com.toscano.test2.logic.network.usercasses.CreateNewUserWithEmailAndPassword
import com.toscano.test2.logic.network.usercasses.SaveUserInDBUserCase
import com.toscano.test2.logic.network.usercasses.SignInUserWithEmailAndPasswordUserCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val user get() = _user
    private val _user = MutableLiveData<UsersDB>()

    val error get() = _error
    private val _error = MutableLiveData<String>()

    //---------------Biometrico------------------------

    val resultCheckBiometric = MutableLiveData<Int>()
    fun checkBiometric(context: Context){

        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {

            BiometricManager.BIOMETRIC_SUCCESS ->{

                resultCheckBiometric.postValue(BiometricManager.BIOMETRIC_SUCCESS)
            }


            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{

                resultCheckBiometric.postValue(BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE)
            }


            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->{

                resultCheckBiometric.postValue(BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE)
            }


            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {

                resultCheckBiometric.postValue(BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED)
            }
        }
    }

    fun signInUserWithEmailAndPassword(email: String, password: String){

        viewModelScope.launch (Dispatchers.IO){
            val users = SignInUserWithEmailAndPasswordUserCase().invoke(email, password)

            if (users != null){
                _user.postValue(users!!)
            }
            else{
                _error.postValue("Ocurrio un error")
            }
        }
    }

    fun createNewInUserWithEmailAndPassword(email: String, password: String){

        viewModelScope.launch (Dispatchers.IO){
            val users = CreateNewUserWithEmailAndPassword().invoke(email, password)

            if (users != null){
                val newUs = SaveUserInDBUserCase().invoke(users.id, users.email, "usNamr")
                _user.postValue(newUs!!)
            }
            else{
                _error.postValue("Ocurrio un error")
            }
        }
    }
}