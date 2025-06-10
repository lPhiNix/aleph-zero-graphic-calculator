package com.placeholder.placeholder.api.util.common.mapper;

import org.hibernate.proxy.HibernateProxy;
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
                Class<?> targetClass;

                // if the value is a Hibernate proxy, obtain the persistent class
                if (HibernateProxy.class.isAssignableFrom(value.getClass())) {
                    targetClass = ((HibernateProxy) value).getHibernateLazyInitializer().getPersistentClass();
                } else {
                    // otherwise, use the actual class of the value
                    targetClass = AopUtils.getTargetClass(value);
                }

                contextData.put(targetClass, value);

                // Save the value for all interfaces implemented by the target class
                for (Class<?> iface : targetClass.getInterfaces()) {
                    contextData.put(iface, value);
                }
            }
            return this;
        }


        public final Builder withAny(Object... values) {
            if (values != null) {
                for (Object value : values) {
                    with(value);
                }
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
            return new MappingContext(contextData, contextIdData);
        }
    }
}
