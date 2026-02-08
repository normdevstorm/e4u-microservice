package com.renting.authentication_service.mapper.user;
import com.renting.authentication_service.dto.user.UserRequestDto;
import com.renting.authentication_service.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring")
public interface UserRequestMapper {
    UserRequestMapper INSTANCE = Mappers.getMapper( UserRequestMapper.class );
    @Mapping(target = "phoneNumber", source = "userRequestDto.phoneNumber",qualifiedBy = FormatPhoneNumber.class)
    User toUser(UserRequestDto userRequestDto);

    @Mapping(target = "phoneNumber", source = "user.phoneNumber",qualifiedBy = DeformatPhoneNumber.class)
    UserRequestDto toUserDto(User user);

    @FormatPhoneNumber
    //method will get the sources as param
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


    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "user.phoneNumber", source = "userRequestDto.phoneNumber",qualifiedBy = FormatPhoneNumber.class)
    User partialUpdate(UserRequestDto userRequestDto, @MappingTarget User user);

}

