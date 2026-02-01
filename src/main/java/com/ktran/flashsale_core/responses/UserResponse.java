package com.ktran.flashsale_core.responses;

import com.ktran.flashsale_core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class UserResponse {

    private String id;
    private String username;
    private String email;

    // --- Hàm mapping từ Entity sang Response ---
    public static UserResponse fromUser(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUserName()) // Giả sử trong Entity là getUsername()
                .email(user.getEmail())
                .build();
    }
}