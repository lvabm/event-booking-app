package com.eventbooking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.eventbooking.common.constant.Role;
import com.eventbooking.dto.auth.LoginRequest;
import com.eventbooking.dto.auth.RegisterRequest;
import com.eventbooking.dto.auth.LoginResponse;
import com.eventbooking.dto.auth.RegisterResponse;
import com.eventbooking.entity.User;
import com.eventbooking.exception.ConflictException;
import com.eventbooking.mapper.UserMapper;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.impl.AuthServiceImpl;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  // Lớp đang được kiểm thử (System Under Test)
  @InjectMocks private AuthServiceImpl authService;

  // Các Dependency được giả lập
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserMapper userMapper;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtService jwtService;

  private RegisterRequest validRegisterRequest;
  private LoginRequest validLoginRequest;
  private User mockUser;
  private User userToSave;

  @BeforeEach
  void setUp() {
    // 1. Dữ liệu giả định
    validRegisterRequest = new RegisterRequest("Test User", "test@example.com", "SecurePwd123");
    validLoginRequest = new LoginRequest("test@example.com", "SecurePwd123");

    // Đối tượng User đã được lưu (có ID và password đã được mã hóa)
    mockUser =
        new User(
            "Test User",
            "test@example.com",
            "encodedPassword",
            null,
            Role.USER,
            Collections.emptyList(),
            null);
    mockUser.setId(1L);

    // Đối tượng User chưa được lưu (dùng trong bước Arrange của Register)
    userToSave =
        new User(
            "Test User",
            "test@example.com",
            validRegisterRequest.password(),
            null,
            Role.USER,
            Collections.emptyList(),
            null);

    // 2. KHẮC PHỤC LỖI NPE: Khởi tạo thủ công AuthServiceImpl
    // Đảm bảo các @Mock được tiêm đúng vào constructor của AuthServiceImpl
    authService =
        new AuthServiceImpl(
            userRepository, passwordEncoder, userMapper, authenticationManager, jwtService);
  }

  // --- REGISTER TESTS ---

  @Test
  @DisplayName("Register_Success_Happy_Case")
  void register_Should_Return_RegisterResponse_When_EmailNotExists() {
    // A (Arrange):
    // 1. Giả lập: Email chưa tồn tại -> FALSE
    when(userRepository.existsByEmail(validRegisterRequest.email())).thenReturn(false);

    // 2. Giả lập: Mapper chuyển DTO thành Entity
    when(userMapper.toEntity(validRegisterRequest)).thenReturn(userToSave);

    // 3. Giả lập: Mã hóa mật khẩu
    String encodedPassword = "newEncodedPassword";
    when(passwordEncoder.encode(validRegisterRequest.password())).thenReturn(encodedPassword);

    // 4. Giả lập: Lưu Entity vào Repository
    User savedUser =
        new User(
            "Test User",
            "test@example.com",
            encodedPassword,
            null,
            Role.USER,
            Collections.emptyList(),
            null);
    savedUser.setId(10L);

    // Sử dụng argThat để kiểm tra nội dung của đối tượng được truyền vào hàm save
    when(userRepository.save(
            argThat(
                user ->
                    user.getRole().equals(Role.USER)
                        && user.getPassword().equals(encodedPassword))))
        .thenReturn(savedUser);

    // 5. Giả lập: Mapper chuyển Entity đã lưu thành DTO Response
    RegisterResponse expectedResponse = new RegisterResponse(10L, "Test User", "test@example.com");
    when(userMapper.toDTO(savedUser)).thenReturn(expectedResponse);

    // A (Act): Thực thi phương thức
    RegisterResponse actualResponse = authService.register(validRegisterRequest);

    // A (Assert): Khẳng định kết quả
    assertNotNull(actualResponse);
    assertEquals(10L, actualResponse.userId());
    assertEquals("test@example.com", actualResponse.email());

    // V (Verify): Xác minh tương tác
    verify(userRepository, times(1)).existsByEmail(validRegisterRequest.email());
    verify(passwordEncoder, times(1)).encode(validRegisterRequest.password());
    verify(userRepository, times(1)).save(any(User.class));
    verify(userMapper, times(1)).toDTO(savedUser);
  }

  @Test
  @DisplayName("Register_Failure_Email_Already_Exists")
  void register_Should_Throw_ConflictException_When_EmailExists() {
    // A (Arrange):
    when(userRepository.existsByEmail(validRegisterRequest.email())).thenReturn(true);

    // A (Act) & A (Assert):
    ConflictException thrown =
        assertThrows(ConflictException.class, () -> authService.register(validRegisterRequest));

    assertTrue(thrown.getMessage().contains("Email is already registered"));

    // V (Verify):
    verify(userRepository, times(1)).existsByEmail(validRegisterRequest.email());
    verify(userRepository, never()).save(any(User.class));
    verify(passwordEncoder, never()).encode(anyString());
    verify(userMapper, never()).toEntity(any(RegisterRequest.class));
  }

  // --- LOGIN TESTS ---

  @Test
  @DisplayName("Login_Success_Happy_Case")
  void login_Should_Return_LoginResponse_When_CredentialsAreValid() {
    // Giả lập đối tượng Authentication đã xác thực (Mock)
    Authentication mockAuthentication = mock(Authentication.class);

    // A (Arrange):
    // 1. Giả lập: AuthenticationManager xác thực thành công
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(mockAuthentication);

    // 2. Giả lập: Lấy Principal (User object)
    when(mockAuthentication.getPrincipal()).thenReturn(mockUser);

    // 3. Giả lập: Tạo JWT Token và Expiry
    String expectedToken = "mock.jwt.token";
    Long expectedExpiry = 3600L;
    when(jwtService.generateToken(any(User.class))).thenReturn(expectedToken);
    when(jwtService.getExpirationTimeInSeconds()).thenReturn(expectedExpiry);

    // A (Act):
    LoginResponse actualResponse = authService.login(validLoginRequest);

    // A (Assert):
    assertNotNull(actualResponse);
    assertEquals(expectedToken, actualResponse.accessToken());
    assertEquals(expectedExpiry, actualResponse.expire());

    // V (Verify):
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService, times(1)).generateToken(mockUser);
    verify(jwtService, times(1)).getExpirationTimeInSeconds();
  }

  @Test
  @DisplayName("Login_Failure_Invalid_Credentials")
  void login_Should_Throw_Exception_When_CredentialsAreInvalid() {
    // A (Arrange):
    // Giả lập: AuthenticationManager ném ra BadCredentialsException (Sai mật khẩu/Username)
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    // A (Act) & A (Assert):
    // Khẳng định phương thức ném ra Exception
    assertThrows(
        BadCredentialsException.class, // Hoặc Exception tương ứng
        () -> authService.login(validLoginRequest));

    // V (Verify):
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService, never()).generateToken(any(User.class));
  }
}
