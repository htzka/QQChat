package org.example.qqchat.service;

import org.example.qqchat.pojo.ReplyMsgFormat;

public interface Reply {
    /**
     *
     * @param replyMsgFormat 回复消息的格式 包括语音 文本 表情等等
     * @param privateId 回复消息的对象id
     */
    void replyToprivate(ReplyMsgFormat replyMsgFormat, String privateId);

    /**
     *
     * @param replyMsgFormat
     * @param groupId 群聊id
     * @param waiTime 消息缓存时间毫秒 可以修改为@秒处理 默认xx毫秒 或者根据对方是否与你交谈等判断进行修改时间
     */
    void replyToGroup(ReplyMsgFormat replyMsgFormat, String groupId,Long waiTime);
}
