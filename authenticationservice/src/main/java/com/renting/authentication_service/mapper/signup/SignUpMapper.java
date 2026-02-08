package com.renting.authentication_service.mapper.signup;

import com.renting.authentication_service.dto.auth.signup.SignUpResponseDto;
import com.renting.authentication_service.entity.User;
import org.mapstruct.Mapping;

;

@org.mapstruct.Mapper(componentModel = "spring")
public interface SignUpMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "accessToken", source= "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    public SignUpResponseDto toSignUpResponseDto(User user, String accessToken, String refreshToken);

}
