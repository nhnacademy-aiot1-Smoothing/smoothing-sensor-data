package live.smoothing.sensordata.interceptor;

import live.smoothing.common.exception.CommonException;
import live.smoothing.sensordata.dto.ThreadLocalUserId;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class UserIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        String userId = request.getHeader("X-USER-ID");

        if (Objects.isNull(userId)) {
            throw new CommonException(HttpStatus.UNAUTHORIZED ,"X-USER-ID 헤더가 존재하지 않습니다.");
        }

        ThreadLocalUserId.setUserId(userId);
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request,
                           @NotNull HttpServletResponse response,
                           @NotNull Object handler,
                           ModelAndView modelAndView) {

        ThreadLocalUserId.clear();
    }
}
