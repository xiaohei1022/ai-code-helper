package com.zhanyan.aicodehelper;

import com.zhanyan.aicodehelper.ai.AiCodeHelper;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeHelperApplicationTests {

	@Resource
    private AiCodeHelper aiCodeHelper;

    @Test
    void chat() {
        String chat = aiCodeHelper.chat("你好！我是程序员展岩。");
        System.out.println(chat);
    }

    @Test
    void chatWithMessage() {
        UserMessage userMessage = UserMessage.from(
                TextContent.from("描述图片"),
                ImageContent.from("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png")
        );
        aiCodeHelper.chatWithMessage(userMessage);
    }
}
