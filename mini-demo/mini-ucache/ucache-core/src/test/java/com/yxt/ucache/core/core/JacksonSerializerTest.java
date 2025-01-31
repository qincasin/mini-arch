package com.yxt.ucache.core.core;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JacksonSerializerTest {

    private JacksonSerializer serializer;

    @BeforeEach
    public void setUp() {
        serializer = new JacksonSerializer();
    }

    @Test
    public void testSerializeAndDeserialize() throws Exception {
        // 创建一个测试对象，包含日期
        TestObject testObject = new TestObject("test", 123, LocalDate.now(),LocalDateTime.now());

        // 序列化
        byte[] serializedBytes = serializer.encoder().apply(testObject);

        // 反序列化
        TestObject deserializedObject = serializer.decoder(TestObject.class).apply(serializedBytes);
        // 将deserializedObject 转为 TestObject 类型
        // 验证反序列化后的对象是否与原始对象相同
        assertEquals(testObject.getName(), deserializedObject.getName());
        assertEquals(testObject.getValue(), deserializedObject.getValue());
        assertEquals(testObject.getDate(), deserializedObject.getDate()); // 验证日期字段
        // 输出
        System.out.println(serializer.mapper.writeValueAsString(deserializedObject));
    }

    // 辅助测试类
    static class TestObject {
        private String name;
        private int value;
        private LocalDate date; // 添加日期字段
        private LocalDateTime dateTime;

        public TestObject() {
        }

        public TestObject(String name, int value, LocalDate date) {
            this.name = name;
            this.value = value;
            this.date = date;
        }

        public TestObject(String name, int value, LocalDate date, LocalDateTime dateTime) {
            this.name = name;
            this.value = value;
            this.date = date;
            this.dateTime = dateTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public LocalDate getDate() { // 添加getter方法
            return date;
        }

        public void setDate(LocalDate date) { // 添加setter方法
            this.date = date;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }

        @Override
        public String toString() {
            return "TestObject{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    ", date=" + date +
                    ", dateTime=" + dateTime +
                    '}';
        }
    }
}