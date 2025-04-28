package com.baranova.cat_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.baranova.cat_service.dto.CatDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatCache {
    private List<CatDTO> catDto;
    private Long lastAccessed;


}
