package com.dwarfkit.storilia.pkg.login

import androidx.lifecycle.*
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.data.local.entity.UserEntity
import com.dwarfkit.storilia.data.repository.UserRepository
import com.dwarfkit.storilia.di.Injection
import kotlinx.coroutines.launch

class LoginViewModel(
    private var userRepository: UserRepository,
    private var userPreferences: UserPreferences
) : ViewModel() {

    fun loginUser(email: String, password: String) = userRepository.login(email, password)

    fun saveUser(user: UserEntity) {
        viewModelScope.launch {
            userPreferences.saveUser(user)
        }
    }

    fun getUser(): LiveData<UserEntity> {
        return userPreferences.getUser().asLiveData()
    }

    fun login(token: String) {
        viewModelScope.launch {
            userPreferences.login(token)
        }
    }

}

class LoginModelFactory private constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: LoginModelFactory? = null

        fun getInstance(
            userPreferences: UserPreferences
        ): LoginModelFactory = instance ?: synchronized(this) {
            instance ?: LoginModelFactory(Injection.provideUserRepository(), userPreferences)
        }
    }
}