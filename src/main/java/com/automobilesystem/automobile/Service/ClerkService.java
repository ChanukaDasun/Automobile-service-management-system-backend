package com.automobilesystem.automobile.Service;

import com.automobilesystem.automobile.Dto.ClerkUserDto;
import com.clerk.backend_api.models.operations.GetUserListResponse;
import com.clerk.backend_api.models.operations.GetUserListRequest;
import com.clerk.backend_api.Clerk;
import com.clerk.backend_api.models.errors.ClerkErrors;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClerkService {

    private final Clerk clerkClient;

    public ClerkService() {
        // Use CLERK_SECRET_KEY environment variable or fallback to a test key
        String secretKey = System.getenv("BEARER_AUTH");
        if (secretKey == null || secretKey.isEmpty()) {
            // For development - replace with your actual Clerk secret key
            secretKey = "sk_test_your_secret_key_here";
            System.out.println("WARNING: Using default Clerk secret key. Please set CLERK_SECRET_KEY environment variable.");
        }
        
        this.clerkClient = Clerk.builder()
                .bearerAuth(secretKey)
                .build();
    }

    public List<ClerkUserDto> getAllUsers() throws ClerkErrors, Exception {
        try {
            GetUserListRequest req = GetUserListRequest.builder()
                    .limit(100L) // Get up to 100 users
                    .build();

            GetUserListResponse res = clerkClient.users().list()
                    .request(req)
                    .call();

            // Return the list (if present)
            return res.userList().orElse(List.of())
                    .stream()
                    .map(user -> new ClerkUserDto(
                            user.id(),
                            user.firstName().orElse("Unknown"),
                            user.lastName().orElse("User"),
                            user.emailAddresses().isEmpty() ? "no-email" : user.emailAddresses().get(0).emailAddress(),
                            (String) user.publicMetadata().getOrDefault("role", "client")
                    )).toList();

        } catch (Exception e) {
            System.err.println("Error fetching users from Clerk: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get users by role for admin dashboard
     */
    public List<ClerkUserDto> getUsersByRole(String role) throws ClerkErrors, Exception {
        return getAllUsers().stream()
                .filter(user -> role.equals(user.role()))
                .toList();
    }

    /**
     * Get client statistics for admin dashboard
     */
    public Map<String, Object> getClientStats() throws ClerkErrors, Exception {
        List<ClerkUserDto> allUsers = getAllUsers();
        
        long totalClients = allUsers.stream()
                .filter(user -> "client".equals(user.role()) || user.role() == null)
                .count();
        
        long totalEmployees = allUsers.stream()
                .filter(user -> "employee".equals(user.role()))
                .count();
        
        long totalAdmins = allUsers.stream()
                .filter(user -> "admin".equals(user.role()))
                .count();

        return Map.of(
                "totalUsers", allUsers.size(),
                "totalClients", totalClients,
                "totalEmployees", totalEmployees,
                "totalAdmins", totalAdmins
        );
    }



}
