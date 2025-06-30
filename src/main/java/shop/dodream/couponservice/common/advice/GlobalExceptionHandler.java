package shop.dodream.couponservice.common.advice;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.dodream.couponservice.exception.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Not Found
    @ExceptionHandler({
            CouponNotFoundException.class,
            CouponPolicyNotFoundException.class,
            UserCouponNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("리소스를 찾을 수 없습니다.");
        problem.setDetail(ex.getMessage());
        problem.setProperty("errorCode", "NOT_FOUND");
        return problem;
    }

    // Bad Request
    @ExceptionHandler({
            DuplicatePolicyNameException.class,
            AlreadyUsedCouponException.class
    })
    public ProblemDetail handleBadRequest(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("잘못된 요청입니다.");
        problem.setDetail(ex.getMessage());
        problem.setProperty("errorCode", "BAD_REQUEST");
        return problem;
    }

    //  인증 실패
    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorized(UnauthorizedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("인증이 필요합니다.");
        problem.setDetail(ex.getMessage());
        problem.setProperty("errorCode", "UNAUTHORIZED");
        return problem;
    }

    // 권한 실패
    @ExceptionHandler(UnauthorizedUserCouponAccessException.class)
    public ProblemDetail handleForbidden(UnauthorizedUserCouponAccessException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("권한이 없습니다.");
        problem.setDetail(ex.getMessage());
        problem.setProperty("errorCode", "FORBIDDEN");
        return problem;
    }

    //validation exception
    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleCustomValidation(ValidationException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("비즈니스 유효성 검사 실패");
        problem.setDetail(ex.getMessage());
        problem.setProperty("errorCode", "VALIDATION_ERROR");
        return problem;
    }

    // request body vaild
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("검증에 실패했습니다.");
        String detail = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        problem.setDetail(detail);
        problem.setProperty("errorCode", "VALIDATION_ERROR");
        return problem;
    }

    // 등등
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("서버 내부 오류");
        problem.setDetail("알 수 없는 오류가 발생했습니다.");
        return problem;
    }
}

