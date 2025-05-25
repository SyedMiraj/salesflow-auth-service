package com.dsol.salesflow.auth.mapper;

import com.dsol.salesflow.asset.ResultMapper;
import com.dsol.salesflow.auth.domain.UserEntity;
import com.dsol.salesflow.auth.jpa.UserRepository;
import com.dsol.salesflow.auth.model.User;
import com.dsol.salesflow.auth.util.ImageUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;

    public ResultMapper<UserEntity, User> toModel(boolean passwordRequired, boolean imageRequired) {
        return entity -> new User()
                .setId(entity.getId())
                .setEmail(entity.getEmail())
                .setFirstName(entity.getFirstName())
                .setLastName(entity.getLastName())
                .setRole(entity.getRole())
                .setPassword(passwordRequired ? entity.getPassword() : null)
                .setProfilePicture(imageRequired && entity.getProfilePicture() != null ? ImageUtils.decompressImage(entity.getProfilePicture()) : null);
    }

    public ResultMapper<User, UserEntity> toEntity(){
        return domain -> userRepository.findByEmail(domain.getEmail())
                .orElseGet(UserEntity::new)
                .setEmail(domain.getEmail())
                .setFirstName(domain.getFirstName())
                .setLastName(domain.getLastName())
                .setRole(domain.getRole());
    }
}

