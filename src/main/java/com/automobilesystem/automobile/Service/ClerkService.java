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
        String secretKey = System.getenv("CLERK_SECRET_KEY");
        if (secretKey == null || secretKey.isEmpty()) {
            // Try alternative environment variable name
            secretKey = System.getenv("BEARER_AUTH");
        }
        if (secretKey == null || secretKey.isEmpty()) {
            // For development - this will cause the error but makes it clear what's needed
            secretKey = "sk_test_please_set_your_clerk_secret_key";
            System.out.println("WARNING: No valid Clerk secret key found. Please set CLERK_SECRET_KEY environment variable.");
            System.out.println("Current CLERK_SECRET_KEY: " + System.getenv("CLERK_SECRET_KEY"));
            System.out.println("Current BEARER_AUTH: " + System.getenv("BEARER_AUTH"));
        }
        
        this.clerkClient = Clerk.builder()
                .bearerAuth(secretKey)
                .build();
    }

    public List<ClerkUserDto> getAllUsers() {
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
            System.err.println("Returning empty user list. Please configure Clerk properly for production.");
            // Return empty list instead of throwing exception to prevent application crash
            return List.of();
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
