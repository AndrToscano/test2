package com.toscano.test2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.toscano.test2.viewmodels.MainViewModel
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    //Declarar variable ImageView
    private lateinit var btnFinger : ImageView

    //Declarar variable TextView
    private lateinit var txtInfo : TextView


    //Vriables Ejecutor en Segundo Plano en Hilos
    private lateinit var executor: Executor
    //Muestra los eventos del Biometrico
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    //Creamos la instancia del ViewModel
    private val mainViewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListeners()
        initObservables()
        authenticationDialog()
        mainViewModel.checkBiometric(this)
    }



    private fun initListeners(){

        //Creamos un boton para la huella
        btnFinger = findViewById<ImageView>(R.id.imgFinger)


        txtInfo = findViewById(R.id.txtInfo)

        //Accion de dar click a la huella
        btnFinger.setOnClickListener {

            biometricPrompt.authenticate(promptInfo)
        }
    }

    //Dialogo de Autenticacion
    private fun authenticationDialog(){

        //Se ejecuta en un hilo
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                //La aplicacion es correcta
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    //Log.d("MY_APP_TAG", "Authentication Success.")
                    startActivity(Intent(this@MainActivity, MainActivity2::class.java))
                }

                //La aplicacion tiene un erroe
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.d("MY_APP_TAG", "Authentication Error.")
                }

                //La aplicacion falla
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.d("MY_APP_TAG", "Authentication Failed.")
                }
            })

        //Dialogo de Autenticacion
        /*
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()
        */

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancel")
            .build()


    }

    private fun initObservables(){

        mainViewModel.resultCheckBiometric.observe(this){
            when(it){
                BiometricManager.BIOMETRIC_SUCCESS -> {

                    //Aparicion de los elementos
                    btnFinger.visibility = View.VISIBLE
                    txtInfo.text = getString(R.string.biometric_succes)

                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {

                    txtInfo.text = getString(R.string.biometric_no_hardware)
                    Log.e("MY_APP_TAG", "No biometric features available on this device.")
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {

                    txtInfo.text = getString(R.string.biometric_erroe)
                    Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {

                    // Prompts the user to create credentials that your app accepts.
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                        )
                    }
                    startActivityForResult(enrollIntent, 100)
                }
            }
        }
    }

    /*
    private fun checkBiometric(context: Context){

        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {

            BiometricManager.BIOMETRIC_SUCCESS ->{

                //Aparicion de los elementos
                btnFinger.visibility = View.VISIBLE
                txtInfo.text = getString(R.string.biometric_succes)

                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
            }


            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{

                txtInfo.text = getString(R.string.biometric_no_hardware)
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
            }


            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->{

                txtInfo.text = getString(R.string.biometric_erroe)
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            }


            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {

                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                startActivityForResult(enrollIntent, 100)
            }
        }
    }
     */
}