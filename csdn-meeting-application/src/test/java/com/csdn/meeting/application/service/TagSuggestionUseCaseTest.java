package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.TagSuggestionDTO;
import com.csdn.meeting.domain.port.NLPTagPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TagSuggestionUseCase: suggestTags, graceful degradation")
class TagSuggestionUseCaseTest {

    @Mock
    private NLPTagPort nlpTagPort;

    private TagSuggestionUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new TagSuggestionUseCase(nlpTagPort);
    }

    @Test
    @DisplayName("suggestTags returns tags from NLP port (3-5 items)")
    void suggestTags_returnsTagsFromPort() {
        List<String> portTags = Arrays.asList("Java", "后端", "微服务");
        when(nlpTagPort.suggestTags(anyString(), anyString())).thenReturn(portTags);

        TagSuggestionDTO result = useCase.suggestTags("技术大会", "年度开发者大会");

        assertNotNull(result);
        assertNotNull(result.getTags());
        assertEquals(3, result.getTags().size());
        assertEquals(Arrays.asList("Java", "后端", "微服务"), result.getTags());
        verify(nlpTagPort).suggestTags("技术大会", "年度开发者大会");
    }

    @Test
    @DisplayName("suggestTags: NLP unavailable returns fallback tags (graceful degradation)")
    void suggestTags_nlpThrows_returnsFallback() {
        when(nlpTagPort.suggestTags(anyString(), anyString()))
                .thenThrow(new RuntimeException("NLP service unavailable"));

        TagSuggestionDTO result = useCase.suggestTags("技术大会", null);

        assertNotNull(result);
        assertNotNull(result.getTags());
        assertEquals(3, result.getTags().size());
        assertTrue(result.getTags().contains("Java"));
        assertTrue(result.getTags().contains("后端"));
        assertTrue(result.getTags().contains("微服务"));
    }

    @Test
    @DisplayName("suggestTags: empty result returns fallback")
    void suggestTags_emptyResult_returnsFallback() {
        when(nlpTagPort.suggestTags(anyString(), anyString())).thenReturn(Collections.emptyList());

        TagSuggestionDTO result = useCase.suggestTags("标题", "");

        assertNotNull(result);
        assertNotNull(result.getTags());
        assertEquals(3, result.getTags().size());
        assertTrue(result.getTags().contains("Java"));
    }

    @Test
    @DisplayName("suggestTags: null title and description handled")
    void suggestTags_nullInput_handled() {
        when(nlpTagPort.suggestTags("", "")).thenReturn(Arrays.asList("AI", "大模型"));

        TagSuggestionDTO result = useCase.suggestTags(null, null);

        assertNotNull(result);
        assertNotNull(result.getTags());
        assertFalse(result.getTags().isEmpty());
    }
}
