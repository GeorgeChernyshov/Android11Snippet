package com.example.post30.ui.screen.security

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.post30.R
import com.example.post30.databinding.ActivitySecurityBinding // Import the generated binding class
import java.util.concurrent.Executor

class SecurityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecurityBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityBinding.inflate(layoutInflater) // Inflate the layout
        setContentView(binding.root) // Set the root view

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.d(TAG, "Authentication error: $errString ($errorCode)")
                    binding.statusText.text = getString(
                        R.string.security_authentication_error,
                        errString,
                        errorCode
                    )
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "Authentication succeeded!")

                    val authType = result.authenticationType
                    val authTypeString = when (authType) {
                        BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> getString(
                            R.string.security_auth_type_biometric
                        )

                        BiometricPrompt.AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL -> getString(
                            R.string.security_auth_type_device_credential
                        )

                        else -> getString(R.string.security_auth_type_unknown)
                    }

                    Log.d(TAG, "Authentication type used: $authTypeString ($authType)")
                    binding.statusText.text = getString(
                        R.string.security_authentication_succeeded,
                        authTypeString
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.d(TAG, "Authentication failed.")
                    binding.statusText.text =
                        getString(R.string.security_authentication_failed)
                }
            }
        )

        val biometricManager = BiometricManager.from(this)
        updateBiometricStatus(biometricManager)

        if (biometricManager.supportsType(
                type = BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            binding.authenticateWeakBiometricButton.isVisible = true
            binding.authenticateWeakBiometricButton.setOnClickListener {
                biometricPrompt.authenticate(
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle(getString(R.string.security_prompt_weak_title))
                        .setSubtitle(getString(R.string.security_prompt_weak_subtitle))
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                        .setNegativeButtonText(getString(R.string.security_prompt_cancel))
                        .build()
                )
            }
        } else {
            binding.authenticateWeakBiometricButton.isGone = true
        }

        if (biometricManager.supportsType(
                type = BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            binding.authenticateDeviceCredentialButton.isVisible = true
            binding.authenticateDeviceCredentialButton.setOnClickListener {
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.security_prompt_device_title))
                    .setSubtitle(getString(R.string.security_prompt_device_subtitle))
                    .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    .build()
            }
        } else {
            binding.authenticateDeviceCredentialButton.isGone = true
        }

        if (biometricManager.supportsType(
                type = BiometricManager.Authenticators.BIOMETRIC_STRONG
        )) {
            binding.authenticateStrongOnlyButton.isVisible = true
            binding.authenticateStrongOnlyButton.setOnClickListener {
                biometricPrompt.authenticate(
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle(getString(R.string.security_prompt_strong_title))
                        .setSubtitle(getString(R.string.security_prompt_strong_subtitle))
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                        .setNegativeButtonText(getString(R.string.security_prompt_cancel))
                        .build()
                )
            }
        } else {
            binding.authenticateStrongOnlyButton.isGone = true
        }
    }

    private fun updateBiometricStatus(biometricManager: BiometricManager) {
        val statusStringBuilder = StringBuilder()
        statusStringBuilder.append("canAuthenticate(WEAK): ")
            .append(biometricManager.supportsType(
                type = BiometricManager.Authenticators.BIOMETRIC_WEAK
            ))
            .append("\ncanAuthenticate(DEVICE_CRED): ")
            .append(biometricManager.supportsType(
                type = BiometricManager.Authenticators.DEVICE_CREDENTIAL
            ))
            .append("\ncanAuthenticate(STRONG): ")
            .append(biometricManager.supportsType(
                type = BiometricManager.Authenticators.BIOMETRIC_STRONG
            ))

        binding.statusText.text = statusStringBuilder.toString()
        Log.d(TAG, "Biometric Status: $statusStringBuilder")
    }

    private fun BiometricManager.supportsType(type: Int) =
        canAuthenticate(type) == BiometricManager.BIOMETRIC_SUCCESS

    companion object {
        private const val TAG = "SecurityActivity"
    }
}