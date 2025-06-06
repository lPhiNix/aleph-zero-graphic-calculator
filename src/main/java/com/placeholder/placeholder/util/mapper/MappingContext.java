package com.placeholder.placeholder.util.mapper;

import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record MappingContext(Map<Class<?>, Object> contextData, Map<String, Integer> contextIdData) {
    public MappingContext {
        contextData = Map.copyOf(contextData);
        contextIdData = Map.copyOf(contextIdData);
    }

    public <T> Optional<T> getContextData(Class<T> key) {
        return Optional.ofNullable(contextData.get(key)).map(key::cast);
    }

    public Optional<Integer> getContextData(String key) {
        return Optional.ofNullable(contextIdData.get(key));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(MappingContext context) {
        return new Builder(context.contextData(), context.contextIdData());
    }

    public static class Builder {
        private final Map<Class<?>, Object> contextData;
        private final Map<String, Integer> contextIdData;

        public Builder(Map<Class<?>, Object> contextData, Map<String, Integer> contextIdData) {

            this.contextData = contextData;
            this.contextIdData = contextIdData;
        }

        public Builder() {
            this.contextData = new HashMap<>();
            this.contextIdData = new HashMap<>();
        }

        public <T> Builder with(T value) {
            if (value != null) {
                Class<?> targetClass = HibernateProxy.class.isAssignableFrom(value.getClass())
                        ? ((HibernateProxy) value).getHibernateLazyInitializer().getPersistentClass()
                        : AopUtils.getTargetClass(value);

                contextData.put(targetClass, value);
            }
            return this;
        }

        public Builder with(String key, Integer value) {
            if (key != null && value != null) {
                contextIdData.put(key, value);
            }
            return this;
        }

        public MappingContext build() {
            return new MappingContext(new HashMap<>(contextData), new HashMap<>(contextIdData));
        }
    }
}
