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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.toscano.test2.databinding.ActivityMainBinding
import com.toscano.test2.viewmodels.MainViewModel
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    //Declarar variable FireBase
    private lateinit var auth: FirebaseAuth

    //Declara la variable Binding
    private lateinit var binding : ActivityMainBinding

    //Vriables Ejecutor en Segundo Plano en Hilos
    private lateinit var executor: Executor
    //Muestra los eventos del Biometrico
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    //Creamos la instancia del ViewModel
    private val mainViewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth


        initListeners()
        initObservables()
        authenticationDialog()
        mainViewModel.checkBiometric(this)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {

            //Acceso con Huella
            binding.etxUser.visibility = View.GONE
            binding.etxPassword.visibility = View.GONE
            binding.imgFinger.visibility = View.VISIBLE
            binding.txtInfo.text = getString(R.string.no_user)

            //Acceso con Autenticacion (usuario - contrasenia)
            //startActivity(Intent(this,MainActivity2::class.java))
        }
        else{
            binding.imgFinger.visibility = View.GONE
            binding.txtInfo.text = getString(R.string.no_user)
        }
    }


    private fun initListeners(){

        //Accion de dar click a la huella
        binding.imgFinger.setOnClickListener {

            biometricPrompt.authenticate(promptInfo)
        }

        //Damos la accion de crear un usuario
        binding.btnSafeUser.setOnClickListener {
            createNewUsers(binding.etxUser.text.toString(), binding.etxPassword.text.toString())
        }

        //Damos la accion de ingresar con un usuario
        binding.btnSignIn.setOnClickListener {
            createdUsers(binding.etxUser.text.toString(), binding.etxPassword.text.toString())
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
                    binding.imgFinger.visibility = View.VISIBLE
                    binding.txtInfo.text = getString(R.string.biometric_succes)

                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {

                    binding.txtInfo.text = getString(R.string.biometric_no_hardware)
                    Log.e("MY_APP_TAG", "No biometric features available on this device.")
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {

                    binding.txtInfo.text = getString(R.string.biometric_erroe)
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

    private fun createNewUsers(user: String, password: String){
        //Ingreso de Nuevos Usuarios
        auth.createUserWithEmailAndPassword(binding.etxUser.text.toString(), binding.etxPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")

                    val user = auth.currentUser

                    Snackbar.make(this, binding.etxUser, "createUserWithEmail:success", Snackbar.LENGTH_LONG).show()

                    binding.etxUser.text.clear()
                    binding.etxPassword.text.clear()

                } else {
                    // If sign in fails, display a message to the user.
                    Snackbar.make(this, binding.etxUser, task.exception!!.message.toString(), Snackbar.LENGTH_LONG).show()

                }
            }
    }

    private fun createdUsers(email: String, password: String){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")

                    val user = auth.currentUser
                    startActivity(Intent(this, MainActivity2::class.java))
                }

                else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)

                    Snackbar.make(this,
                        binding.etxUser,
                        "signInWithEmail:failure",
                        Snackbar.LENGTH_LONG,
                    ).show()
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