package com.zhanyan.aicodehelper.ai;

import com.zhanyan.aicodehelper.ai.guardrail.SafeInputGuardrail;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import java.util.List;

//@AiService
@InputGuardrails({SafeInputGuardrail.class})
public interface AiCodeHelperService {

    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String message);

    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String message);

    // 学习报告
    record Report(String name, List<String> suggesttionList){}

    // 返回封装后的结果
    @SystemMessage(fromResource = "system-prompt.txt")
    Result<String> chatWithResult(String message);

    Flux<String> chatWithStream(@MemoryId int memoryId, @UserMessage String userMessage);
}
