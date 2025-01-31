package com.yxt.ucache.core.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.yxt.ucache.common.SerialGenericPolicy;
import com.yxt.ucache.core.utils.UDateParser;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Function;
import org.apache.commons.lang3.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JacksonSerializer
 *
 * @author qinjiaxing on 2025/1/30
 * @author <others>
 */
public class JacksonSerializer implements SerialGenericPolicy {

    private static final String SDF_YEAR2DAY = "yyyy-MM-dd";
    private static final String SDF_HOUR2SECOND = "HH:mm:ss.SSS";
    private static final String SDF_YEAR2SECOND = "yyyy-MM-dd HH:mm:ss.SSS";
    protected static Logger logger = LoggerFactory.getLogger(JacksonSerializer.class);
    protected final ObjectMapper mapper;
    protected final ObjectMapper noClassMapper;

    public JacksonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        configBase(mapper);
        configTime(mapper);

        // 配置方式参考自 2.3.2 {@link org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer}
        // 包含 @class
        this.mapper = mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        ObjectMapper noClassMapper = new ObjectMapper();
        configBase(noClassMapper);
        configTime(noClassMapper);
        // 忽略缺少 @class 字段类型，反序列化的jsonstring中没有@class，可使用此类型，不过范围有限，
        // 注意：并不能和fastjson更好匹配，比如date类型fastjson会自适应，jackson只能匹配某种类型
        this.noClassMapper = noClassMapper.deactivateDefaultTyping();
    }


    @Override
    public <T> Function<byte[], T> decoder(Class<T> clazz) {
        return e -> {
            try {
                T val = doDecoder(e, clazz, mapper);
                if (e != null && val == null) {
                    // 如果序列化失败，则使用noClassMapper重试，示例这里date类型可能会出现序列化失败
                    val = doDecoder(e, clazz, noClassMapper);
                }
                return val;
            } catch (Exception ex) {
                try {
                    return doDecoder(e, clazz, noClassMapper);
                } catch (Exception ex2) {
                    // NOSONAR
                }
                throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
            }
        };
    }

    @Override
    public String name() {
        return Jackson;
    }

    @Override
    public Function<Object, byte[]> encoder() {
        return e -> {
            try {
                return mapper.writeValueAsBytes(e);
            } catch (JsonProcessingException ex) {
                throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
            }
        };
    }

    @Override
    public Function<byte[], Object> decoder() {
        return e -> {
            try {
                Object val = doDecoder(e, Object.class, mapper);
                if (e != null && val == null) {
                    // 如果序列化失败，则使用noClassMapper重试，示例这里date类型可能会出现序列化失败
                    val = doDecoder(e, Object.class, noClassMapper);
                }
                return val;
            } catch (Exception ex) {
                try {
                    return doDecoder(e, Object.class, noClassMapper);
                } catch (Exception exception) {
                    // NOSONAR
                }
                throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
            }
        };
    }

    protected <T> T doDecoder(byte[] src, Class<T> tClass, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(src, tClass);
        } catch (IOException ex) {
            throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    private void configTime(ObjectMapper mapper) {
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(SDF_YEAR2DAY)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(SDF_HOUR2SECOND)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(SDF_YEAR2SECOND)));
        javaTimeModule.addSerializer(Date.class, DateSerializer.instance);

        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(SDF_YEAR2DAY)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(SDF_HOUR2SECOND)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(SDF_YEAR2SECOND)));
        // javaTimeModule.addDeserializer(Date.class, new DateDeserializers.DateDeserializer());
        // 做了简单优化
        javaTimeModule.addDeserializer(Date.class, new UDateDeserializer());
        mapper.registerModule(javaTimeModule);

    }

    private void configBase(ObjectMapper mapper) {
        // 排除为null的字段
        mapper.setSerializationInclusion(Include.NON_ABSENT);
        // 强制所有Field 和 Method 公开
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        // 此处指定为EVERYTHING是为了支持final修饰的Class和Field
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, DefaultTyping.EVERYTHING);
        // 忽略非序列化对象
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 忽略未知属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略未知类
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }

    /**
     * 在默认的date反序列化上，做了简单的拓展
     */
    protected static class UDateDeserializer extends DateDeserializers.DateDeserializer {

        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Exception ex;
            try {
                return _parseDate(p, ctxt);
            } catch (Exception e) {
                ex = e;
            }
            // 只有默认的反序列化失败后才会使用拓展的反序列化，为了适配非标准数据（旧数据）
            try {
                return UDateParser.parseDateWithLeniency(p.getText(), null);
            } catch (ParseException e) {
                logger.warn("UDateDeserializer deserialize error1 ", ex);
                logger.warn("UDateDeserializer deserialize error2 ", e);
                throw new RuntimeException(ex);
            }
        }
    }
}
