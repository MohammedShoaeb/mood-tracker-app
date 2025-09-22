package com.shadow.moodtracker.viewmodel


import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel: ViewModel() {

    private val auth:FirebaseAuth=FirebaseAuth.getInstance()
    private val _authState= MutableStateFlow<AuthState>(AuthState.LoadingState)

    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthStatus()
    }
    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.UnAuthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }







    fun login(email:String,password:String){

        if (email.isEmpty()||password.isEmpty()){
            _authState.value= AuthState.Error("Email or password can't be empty!")
            return
        }
        _authState.value= AuthState.LoadingState
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                    task->
                if (task.isSuccessful){
                    _authState.value= AuthState.Authenticated
                }else {
                    _authState.value=
                        AuthState.Error(task.exception?.message ?: "Something went wrong!")
                }
            }
    }


    fun signup(email:String,password:String){

        if (email.isEmpty()||password.isEmpty()){
            _authState.value= AuthState.Error("Email or password can't be empty!")
            return
        }

        _authState.value= AuthState.LoadingState
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                    task->
                if (task.isSuccessful){
                    _authState.value= AuthState.Authenticated
                }else {
                    _authState.value=
                        AuthState.Error(task.exception?.message ?: "Something went wrong!")
                }
            }
    }
    fun signOut(){
        _authState.value= AuthState.LoadingState
        auth.signOut()
        _authState.value= AuthState.UnAuthenticated
    }

    fun loadingRevoke(){
        _authState.value= AuthState.LoadingState
    }
}






sealed class AuthState{
    object Authenticated: AuthState()
    object UnAuthenticated: AuthState()
    object LoadingState: AuthState()
    data class Error(val message:String): AuthState()

}