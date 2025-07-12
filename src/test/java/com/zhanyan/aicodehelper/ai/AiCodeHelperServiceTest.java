package com.zhanyan.aicodehelper.ai;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;


    @Test
    void chat() {
        String result = aiCodeHelperService.chat("你好！我是程序员展岩。");
        System.out.println(result);
    }

    @Test
    void chatWithMemory() {
        String result = aiCodeHelperService.chat("你好！我是程序员展岩。");
        System.out.println(result);
        result = aiCodeHelperService.chat("你好！我是谁来着？");
        System.out.println(result);
    }

    @Test
    void chatForReport() {
        String userMessage = "你好！我是程序员展岩，学习编程6年，请帮我给出一些学习建议？";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(userMessage);
        System.out.println(report);
    }

    @Test
    void chatWithRag() {
        String result = aiCodeHelperService.chat("怎么学习Java? 有哪些常见的面试题？");
        System.out.println(result);
    }

    @Test
    void chatWithRagResult() {
        Result<String> stringResult = aiCodeHelperService.chatWithResult("怎么学习Java? 有哪些常见的面试题？");
        System.out.println(stringResult.sources());
        System.out.println(stringResult.content());
    }

    @Test
    void chatWithTools() {
        String result = aiCodeHelperService.chat("有哪些常见的计算机网络面试题？");
        System.out.println(result);
    }

    @Test
    void chatWithMcp() {
        String result = aiCodeHelperService.chat("什么是鱼皮的编程导航？");
        System.out.println(result);
    }

    @Test
    void chatWithGuardrail() {
        String result = aiCodeHelperService.chat("kill the game");
        System.out.println(result);
    }
}