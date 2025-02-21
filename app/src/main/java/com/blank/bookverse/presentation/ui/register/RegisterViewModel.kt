package com.blank.bookverse.presentation.ui.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.model.RegisterModel
import com.blank.bookverse.data.repository.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    // 회원가입 필드 상태 관리
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    // 회원가입 아이디 상태 관리
    private val _registerUserId = MutableStateFlow("")
    val registerUserId = _registerUserId.asStateFlow()

    // 회원가입 비밀번호 상태 관리
    private val _registerUserPw = MutableStateFlow("")
    val registerUserPw = _registerUserPw.asStateFlow()

    // 회원가입 비밀번호 확인 상태 관리
    private val _registerUserPwCheck = MutableStateFlow("")
    val registerUserPwCheck = _registerUserPwCheck.asStateFlow()

    // 회원가입 이름 상태 관리
    private val _registerUserNameCheck = MutableStateFlow("")
    val registerUserNameCheck = _registerUserNameCheck.asStateFlow()

    // 회원가입 전화번호 상태 관리
    private val _registerUserPhoneNumber = MutableStateFlow("")
    val registerUserPhoneNumber = _registerUserPhoneNumber.asStateFlow()

    // 회원가입 닉네임 상태 관리
    private val _registerUserNickName = MutableStateFlow(generateNickname())
    val registerUserNickName = _registerUserNickName.asStateFlow()

    // 회원가입 상태 관리
    private val _registrationStatus = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationStatus: StateFlow<RegistrationState> get() = _registrationStatus

    // Register Effect 상태 관리
    private val _registerEffect = MutableSharedFlow<RegisterEffect>()
    val registerEffect: SharedFlow<RegisterEffect> get() = _registerEffect

    // 아이디 중복확인 Effect 상태 관리
    private val _checkIdEffect = MutableSharedFlow<CheckIdEffect>()
    val checkIdEffect: SharedFlow<CheckIdEffect> get() = _checkIdEffect

    // 아이디 중복확인 상태 관리
    private val _checkIdStatus = MutableStateFlow<IdCheckState>(IdCheckState.Idle)
    val checkIdStatus: StateFlow<IdCheckState> get() = _checkIdStatus

    // 아이디 중복확인 다이얼로그 제어 변수
    val showDialogCheckIdState = mutableStateOf(false)

    // 아이디 사용허용 체크 변수
    val isEnableCheckIdState = mutableStateOf(true)

    // 아이디 에러 상태 관리
    val isUserIdError = mutableStateOf<Boolean>(false)

    // 비밀번호 에러 상태 관리
    val isUserPwError = mutableStateOf(false)

    // 비밀번호 확인 에러 상태 관리
    val isUserPwCheckError = mutableStateOf(false)

    // 이름 에러 상태 관리
    val isUserNameError = mutableStateOf(false)

    // 전화번호 에러 상태 관리
    val isUserNumberError = mutableStateOf(false)

    // 닉네임 에러 상태 관리
    val isUserNicknameError = mutableStateOf(false)

    init {
        generateNickname()
    }

    // 회원가입 필드 업데이트 (중복 제거)
    fun updateRegisterField(field: RegisterField, value: String) {
        _registerState.value = when (field) {
            RegisterField.USER_ID -> _registerState.value.copy(userId = value)
            RegisterField.PASSWORD -> _registerState.value.copy(userPw = value)
            RegisterField.PASSWORD_CHECK -> _registerState.value.copy(userPwCheck = value)
            RegisterField.USER_NAME -> _registerState.value.copy(userName = value)
            RegisterField.PHONE_NUMBER -> _registerState.value.copy(userPhoneNumber = value)
            RegisterField.NICKNAME -> _registerState.value.copy(userNickName = value)
        }
    }

    // 랜덤 닉네임 생성기
    fun generateNickname(): String {
        var nickname = ""

        runBlocking {
            registerRepository.getRandomNickName()
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            nickname = it
                        },
                        onFailure = {
                            nickname = ""
                        }
                    )
                }
        }
        return nickname
    }


    // 회원가입 유효성 검사
    fun checkIsRegister(): Boolean {
        val errors = listOf(
            isUserIdError.value && _checkIdStatus.value == IdCheckState.Available,
            isUserPwError.value,
            isUserPwCheckError.value,
            isUserNameError.value,
            isUserNumberError.value,
            isUserNicknameError.value
        )

        return errors.all { it }
    }

    // 회원가입
    fun registerUser(
        userId: String,
        userName: String,
        userPw: String,
        userPhoneNumber: String,
        userNickName: String
    ) = viewModelScope.launch {

        // RegisterModel 생성
        val registerModel =
            RegisterModel(
                memberDocId = "",
                memberName = userName,
                memberProfileImage = "",
                memberId = userId,
                memberPassword = userPw,
                memberPhoneNumber = userPhoneNumber,
                memberNickName = userNickName,
                LoginType = "일반",
                createAt = System.currentTimeMillis(),
                isDelete = false
            )

        // Flow 수집
        registerRepository.createUserData(registerModel)
            .onStart { _registrationStatus.value = RegistrationState.Loading }

            .onCompletion { cause ->
                // onCompletion에서 예외가 발생했는지 확인
                cause?.let {
                    _registrationStatus.value = RegistrationState.Error("Error: ${it.message}")
                    _registerEffect.emit(RegisterEffect.ShowMessage("회원가입에 실패하였습니다. 잠시 후 다시 시도해주세요."))
                }
            }

            .collectLatest { result ->
                result.fold(
                    // fold -> 성공과 실패를 나눠 처리하는 함수
                    onSuccess = {
                        _registrationStatus.value = RegistrationState.Success
                        _registerEffect.emit(RegisterEffect.ShowMessage("회원가입에 성공하였습니다."))
                    },
                    onFailure = { e ->
                        _registrationStatus.value = RegistrationState.Error("Error: ${e.message}")
                        _registerEffect.emit(RegisterEffect.ShowMessage("회원가입에 실패하였습니다. 잠시 후 다시 시도해주세요."))
                    }
                )
            }
    }

    // 아이디 중복 확인
    fun validateUserId(userId: String) = viewModelScope.launch {
        registerRepository.checkUserIdDuplicate(userId)

            .onStart {
                _checkIdStatus.value = IdCheckState.Loading
            }

            .onCompletion { cause ->
                // Flow가 완료되었을 때 발생한 오류 처리
                cause?.let {
                    _checkIdStatus.value = IdCheckState.Error("Error: ${cause.message}")
                    _checkIdEffect.emit(CheckIdEffect.ShowMessage("아이디 중복 검사에 실패하였습니다."))
                }
            }

            .collectLatest { result ->
                // `result`는 `Result<Unit>` 타입
                result.fold(
                    onSuccess = { isValid ->
                        if (isValid) {
                            _checkIdStatus.value = IdCheckState.Available
                            _checkIdEffect.emit(CheckIdEffect.ShowMessage("사용가능한 아이디 입니다. 사용하시겠습니까?"))
                            showDialogCheckIdState.value = true
                        } else {
                            _checkIdStatus.value = IdCheckState.Duplicate
                            _checkIdEffect.emit(CheckIdEffect.ShowMessage("아이디가 중복되었습니다. 다른 아이디를 사용해 주세요."))
                            showDialogCheckIdState.value = true
                        }
                    },
                    onFailure = { e ->
                        _checkIdStatus.value = IdCheckState.Error("Error: ${e.message}")
                        _checkIdEffect.emit(CheckIdEffect.ShowMessage("아이디 유효성 검사에 실패하였습니다."))
                    }
                )
            }

    }

    // 등록 상태 정의
    sealed class RegistrationState {
        data object Idle : RegistrationState() // 초기 상태
        data object Loading : RegistrationState() // 로딩 중
        data object Success : RegistrationState() // 성공
        data class Error(val message: String) : RegistrationState() // 실패
    }

    // 아이디 중복 확인 상태 정의
    sealed class IdCheckState {
        data object Idle : IdCheckState() // 초기 상태
        data object Loading : IdCheckState() // 로딩 중
        data object Available : IdCheckState() // 사용 가능한 아이디
        data object Duplicate : IdCheckState() // 중복된 아이디
        data class Error(val message: String) : IdCheckState() // 오류 메시지
    }


    // 사이드 이펙트 정의
    sealed class RegisterEffect {
        data class ShowMessage(val message: String) : RegisterEffect()
    }

    sealed class CheckIdEffect {
        data class ShowMessage(val message: String) : CheckIdEffect()
    }

    // 필드 enum 정의
    enum class RegisterField {
        USER_ID, PASSWORD, PASSWORD_CHECK, USER_NAME, PHONE_NUMBER, NICKNAME
    }

    // 상태 데이터 클래스
    data class RegisterState(
        val userId: String = "",
        val userPw: String = "",
        val userPwCheck: String = "",
        val userName: String = "",
        val userPhoneNumber: String = "",
        val userNickName: String = generateNickname()
    ) {
        companion object {
            fun generateNickname(): String {
                val animals = listOf("토끼", "고양이", "강아지", "곰", "새", "호랑이", "사자", "거북이", "여우", "사슴")
                val adjectives = listOf("행복한", "사랑스러운", "귀여운", "용감한", "파란", "따뜻한", "조용한", "반짝이는")
                return "${adjectives.random()} ${animals.random()}"
            }
        }
    }
}