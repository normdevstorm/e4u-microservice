package com.renting.authentication_service.mapper.user;
import com.renting.authentication_service.dto.auth.validate_token.ValidateJwtTokenSuccessResponse;
import com.renting.authentication_service.dto.user.UserResponseDto;
import com.renting.authentication_service.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    @Mapping(target = "phoneNumber", source = "userResponseDto.phoneNumber",qualifiedBy = FormatPhoneNumber.class)
    User toUser(UserResponseDto userResponseDto);

    @Mapping(target = "phoneNumber", source = "user.phoneNumber",qualifiedBy = DeformatPhoneNumber.class)
    UserResponseDto toUserDto(User user);

    @Mapping(target = "phoneNumber", source = "user.phoneNumber",qualifiedBy = DeformatPhoneNumber.class)
    @Mapping(target = "authorities", expression = "java(user.getRole().getAuthorities())")
    ValidateJwtTokenSuccessResponse toValidateJwtTokenSuccessResponseDto(User user);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void updateUserFromDto(UserResponseDto userResponseDto, @MappingTarget User user);

    @FormatPhoneNumber
    public static  String formatPhoneNumber(String unformattedPhoneNumber){
        String firstCluster = unformattedPhoneNumber.substring(1, 3);
        String secondCluster = unformattedPhoneNumber.substring(3, 6);
        String thirdCluster = unformattedPhoneNumber.substring(6);
        String formattedPhoneNumber = "+84" + "-" + firstCluster + "-" + secondCluster + "-" + thirdCluster;
        return formattedPhoneNumber;
    }

    @DeformatPhoneNumber
    public static   String deformatPhoneNumber(String formattedPhoneNumber){
        String firstCluster = formattedPhoneNumber.substring(4, 6);
        String secondCluster = formattedPhoneNumber.substring(7, 10);
        String thirdCluster = formattedPhoneNumber.substring(11);
        String unformattedPhoneNumber = firstCluster + secondCluster + thirdCluster;
        return unformattedPhoneNumber;
    }

}
