package com.dsol.salesflow.auth.serice;

import com.dsol.salesflow.asset.ListResultBuilder;
import com.dsol.salesflow.asset.PagedResult;
import com.dsol.salesflow.asset.PagedResultBuilder;
import com.dsol.salesflow.auth.UserSpecification;
import com.dsol.salesflow.auth.domain.UserEntity;
import com.dsol.salesflow.auth.jpa.UserRepository;
import com.dsol.salesflow.auth.mapper.UserMapper;
import com.dsol.salesflow.auth.model.User;
import com.dsol.salesflow.auth.model.UserSaveRequest;
import com.dsol.salesflow.auth.util.ImageUtils;
import com.dsol.salesflow.exception.CommonException;
import com.dsol.salesflow.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Long saveUser(UserSaveRequest request){
        logger.info("Request to save request. email={}", request.getEmail());
        UserEntity entity = userMapper.toEntity().map(request);
        return userRepository.save(entity).getId();
    }

    public User findByEmail(String email) {
        logger.info("Request to fetch user for auth purpose. email={}", email);
        UserEntity entity = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not exist"));
        return userMapper.toModel(true, true).map(entity);
    }

    public Boolean exist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public PagedResult<User> findAllUsers(Pageable pageable, String firstName, String lastName, String email, Role role) {
        Page<UserEntity> users = userRepository.findAll(UserSpecification.findUsers(firstName, lastName, email, role), pageable);
        return PagedResultBuilder.build(users, userMapper.toModel(false, false));
    }

    public List<User> findAllUsers(String firstName, String lastName, String email, Role role) {
        List<UserEntity> users = userRepository.findAll(UserSpecification.findUsers(firstName, lastName, email, role));
        return ListResultBuilder.build(users, userMapper.toModel(false, false));
    }

    public User findById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new CommonException("00", "User not found"));
        return userMapper.toModel(false, true).map(userEntity);
    }

    public boolean isExist(Long id){
        return userRepository.findById(id).isPresent();
    }

    public void uploadProfilePicture(Long id, MultipartFile file) {
        validateMultipartFile(file);
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new CommonException("00", "User not found"));
        try {
            byte[] image = ImageUtils.compressImage(file.getBytes());
            user.setProfilePicture(image);
            userRepository.save(user);
        } catch (IOException e) {
            throw new CommonException("11", "Exception occurred in uploading profile picture");
        }
    }

    private void validateMultipartFile(MultipartFile file) {
        long maxSize = 256 * 256;  // 256 KB
        if (file.getSize() > maxSize) {
            throw new CommonException("09", "File size exceeds limit of 256 KB");
        }

        String contentType = file.getContentType();
        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png");
        if (!allowedTypes.contains(contentType)) {
            throw new CommonException("10", "Only JPG and PNG images are allowed");
        }
    }


}
