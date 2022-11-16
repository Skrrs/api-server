package com.mask.api.domain.user.dto.favorite;

import lombok.Getter;

import java.util.List;

@Getter
public class FavoriteRequestDto
{
    private List<Integer> problem;
}
