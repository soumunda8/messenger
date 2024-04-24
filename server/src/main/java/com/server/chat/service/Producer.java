package com.server.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
@Slf4j
public class Producer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, int enterId, String message, String messageType, String fid) {

        Date date = new Date();
        Timestamp now = new Timestamp(date.getTime());
        long timestampAsLong = now.getTime();

        JSONArray fields = new JSONArray();
        fields.put(new JSONObject().put("type", "int32").put("optional", false).put("field", "enterid"));
        fields.put(new JSONObject().put("type", "int64").put("optional", false).put("name", "org.apache.kafka.connect.data.Timestamp").put("version", 1).put("field", "senddate"));
        fields.put(new JSONObject().put("type", "string").put("optional", false).put("field", "message"));
        fields.put(new JSONObject().put("type", "string").put("optional", false).put("field", "messagetype"));
        fields.put(new JSONObject().put("type", "string").put("optional", false).put("field", "fid"));

        JSONObject valueSchema = new JSONObject();
        valueSchema.put("type", "struct");
        valueSchema.put("name", "chatting");
        valueSchema.put("fields", fields);
        valueSchema.put("optional", false);

        JSONObject payload = new JSONObject();
        payload.put("enterid", enterId);
        payload.put("senddate", timestampAsLong);
        payload.put("message", message);
        payload.put("messagetype", messageType);
        payload.put("fid", fid);

        JSONObject value = new JSONObject();
        value.put("schema", valueSchema);
        value.put("payload", payload);

        kafkaTemplate.send(topic, String.valueOf(value));

    }

}