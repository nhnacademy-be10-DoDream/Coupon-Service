package shop.dodream.couponservice.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import shop.dodream.couponservice.common.annotation.CurrentUser;
import shop.dodream.couponservice.common.resolver.CurrentUserArgumentResolver;
import shop.dodream.couponservice.exception.UnauthorizedException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserArgumentResolverTest {

    private final CurrentUserArgumentResolver resolver = new CurrentUserArgumentResolver();

    private MethodParameter parameterWithAnnotation() throws NoSuchMethodException {
        Method method = TestController.class.getMethod("withCurrentUser", String.class);
        MethodParameter param = new MethodParameter(method, 0);
        param.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        return param;
    }

    private MethodParameter parameterWithoutAnnotation() throws NoSuchMethodException {
        Method method = TestController.class.getMethod("withoutCurrentUser", String.class);
        return new MethodParameter(method, 0);
    }

    static class TestController {
        public void withCurrentUser(@CurrentUser String id) {}
        public void withoutCurrentUser(String id) {}
    }

    @Test
    @DisplayName("supportsParameter returns true when annotation present")
    void supportsParameterTrue() throws Exception {
        assertThat(resolver.supportsParameter(parameterWithAnnotation())).isTrue();
    }

    @Test
    @DisplayName("supportsParameter returns false when annotation missing")
    void supportsParameterFalse() throws Exception {
        assertThat(resolver.supportsParameter(parameterWithoutAnnotation())).isFalse();
    }

    @Test
    @DisplayName("resolveArgument returns header value")
    void resolveArgument() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-USER-ID", "u");
        ServletWebRequest webRequest = new ServletWebRequest(req);
        Object result = resolver.resolveArgument(
                parameterWithAnnotation(),
                new ModelAndViewContainer(),
                webRequest,
                Mockito.mock(WebDataBinderFactory.class));
        assertThat(result).isEqualTo("u");
    }

    @Test
    @DisplayName("resolveArgument throws when header missing")
    void resolveArgumentFail() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletWebRequest webRequest = new ServletWebRequest(req);
        assertThatThrownBy(() -> resolver.resolveArgument(parameterWithAnnotation(), new ModelAndViewContainer(), webRequest, null))
                .isInstanceOf(UnauthorizedException.class);
    }
}
