package com.project.webshopproject.like;

import com.project.webshopproject.like.dto.LikeRequestDto;
import com.project.webshopproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    @PostMapping("/likes")
    public void addLike( @AuthenticationPrincipal final UserDetailsImpl userDetails,
                         @RequestBody LikeRequestDto likeRequestDto){
        likeService.toggleLike(userDetails.getUser(),likeRequestDto);
    }
}
