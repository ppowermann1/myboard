package com.board.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "아이디는 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,30}$", message = "아이디는 6-30자의 영문 대소문자 및 숫자만 가능합니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    private String password;

    @NotBlank(message = "별명은 필수입니다")
    @Size(min = 2, max = 10, message = "별명은 2-10자 사이여야 합니다")
    private String nickname;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @Pattern(regexp = "^(010-\\d{4}-\\d{4})?$", message = "핸드폰번호는 010-XXXX-XXXX 형식이어야 합니다")
    private String phoneNumber;
}
