package com.sf.guildanalytics.controller;

import com.sf.guildanalytics.entity.User;
import com.sf.guildanalytics.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User createGuildLeader(@RequestBody User user) {
        // Force the role to GUILD_LEAD if an admin is creating
        user.setRole("GUILD_LEAD");
        return userService.createUser(user);
    }

    @PostMapping("/link-player/{playerId}")
    public User linkPlayerToCurrentUser(@PathVariable UUID playerId, java.security.Principal principal) {
        return userService.linkPlayer(principal.getName(), playerId);
    }
}
