package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.TagDTO;
import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.domain.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 热门标签 UseCase
 * issue001-9
 */
@Service
public class HotTagsUseCase {

    private final TagRepository tagRepository;

    public HotTagsUseCase(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDTO> getHotTags(int limit) {
        int cappedLimit = Math.min(Math.max(limit, 1), 50);
        List<Tag> tags = tagRepository.findHotTags(cappedLimit);
        return tags.stream()
                .map(t -> new TagDTO(t.getId(), t.getTagName(),
                        t.getTagCategory() != null ? t.getTagCategory().name().toLowerCase() : null))
                .collect(Collectors.toList());
    }
}
