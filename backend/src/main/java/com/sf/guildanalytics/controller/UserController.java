package com.sf.guildanalytics.controller;

import com.sf.guildanalytics.dto.UserDTO;
import com.sf.guildanalytics.entity.User;
import com.sf.guildanalytics.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "GUILD_LEADER");

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getRole(), u.getPlayerId()))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO createUser(@RequestBody User user) {
        if (!ALLOWED_ROLES.contains(user.getRole())) {
            user.setRole("MEMBER");
        }
        User created = userService.createUser(user);
        return new UserDTO(created.getId(), created.getUsername(), created.getRole(), created.getPlayerId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/link-player/{playerId}")
    public User linkPlayerToCurrentUser(@PathVariable UUID playerId, java.security.Principal principal) {
        return userService.linkPlayer(principal.getName(), playerId);
    }
}
