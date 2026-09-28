package com.recruitment.service;

import com.recruitment.dto.UserDTO;
import com.recruitment.entity.User;
import com.recruitment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        
        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }

    public UserDTO createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    /**
     * 注册新账号
     * @param role 角色：admin=管理员，readonly=只读
     */
    public UserDTO register(String username, String password, String realName, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        if (userRepository.existsByUsername(username.trim())) {
            throw new RuntimeException("用户名已存在");
        }
        // 校验角色：只允许注册 管理员 / 只读 两种等级
        String finalRole = role;
        if (finalRole == null || finalRole.isEmpty()) {
            finalRole = "readonly";
        }
        if (!"admin".equals(finalRole) && !"readonly".equals(finalRole)) {
            throw new RuntimeException("注册等级仅支持：管理员(admin) 或 只读(readonly)");
        }
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setRealName(realName == null || realName.trim().isEmpty() ? username.trim() : realName.trim());
        user.setRole(finalRole);
        user.setStatus(1);
        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    public UserDTO updateUser(Long id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        existing.setUsername(user.getUsername());
        existing.setRealName(user.getRealName());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        existing.setRole(user.getRole());
        existing.setStatus(user.getStatus());
        
        User updated = userRepository.save(existing);
        return convertToDTO(updated);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        return dto;
    }
}
