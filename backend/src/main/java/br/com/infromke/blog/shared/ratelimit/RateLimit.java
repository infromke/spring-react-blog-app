package br.com.infromke.blog.shared.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // indica que a anotação vai em cima de métodos
@Retention(RetentionPolicy.RUNTIME) // e ela precisa estar disponível em tempo de execução
public @interface RateLimit {
    RateLimitType type() default RateLimitType.GLOBAL;
}
