package com.dwarfkit.storilia.pkg.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dwarfkit.storilia.data.repository.UserRepository
import com.dwarfkit.storilia.di.Injection

class SignupViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    fun signUpUser(name: String, email: String, password: String) = userRepository.signup(name, email, password)
}

class SignupViewModelFactory private constructor(private val userRepository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(userRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: SignupViewModelFactory? = null

        fun getInstance(): SignupViewModelFactory = instance ?: synchronized(this) {
            instance ?: SignupViewModelFactory(Injection.provideUserRepository())
        }
    }
}