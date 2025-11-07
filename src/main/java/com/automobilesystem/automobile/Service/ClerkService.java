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
        this.clerkClient = Clerk.builder()
                .bearerAuth(System.getenv().getOrDefault("BEARER_AUTH", ""))
                .build();
    }

    public List<ClerkUserDto> getAllUsers() throws ClerkErrors, Exception {
        GetUserListRequest req = GetUserListRequest.builder().build();

        GetUserListResponse res = clerkClient.users().list()
                .request(req)
                .call();

        // Return the list (if present)
        return res.userList().orElse(List.of())
                .stream()
                .map( user ->  new ClerkUserDto(
                        user.id(),
                        user.firstName().orElse(null),
                        user.lastName().orElse(null),
                        user.emailAddresses().get(0).emailAddress(),
                        (String) user.publicMetadata().get("role")

                )).toList();

    }



}
