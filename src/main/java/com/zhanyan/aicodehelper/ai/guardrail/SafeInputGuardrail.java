package com.zhanyan.aicodehelper.ai.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;

import java.util.Set;

/**
 * 输入安全策略
 */
public class SafeInputGuardrail implements InputGuardrail {

    private static final Set<String> FORBIDDEN_WORDS = Set.of("kill", "evil");

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        // 获取用户输入并转化为小写确保大小写敏感
        String inputText = userMessage.singleText().toLowerCase();
        // 使用正则表达式分割输入文本为单词
        String[] words = inputText.split("\\W+");
        // 遍历所有单词，检查是否有敏感词
        for (String word : words) {
            if (FORBIDDEN_WORDS.contains(word)){
                return fatal("Input contains forbidden words" +  word);
            }
        }
        return success();
    }
}
