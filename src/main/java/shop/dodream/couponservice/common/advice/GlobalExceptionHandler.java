package shop.dodream.couponservice.common.advice;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.dodream.couponservice.exception.*;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Not Found
    @ExceptionHandler({
            CouponNotFoundException.class,
            CouponPolicyNotFoundException.class,
            UserCouponNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Resource not found");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    // Bad Request
    @ExceptionHandler({
            DuplicatePolicyNameException.class,
            AlreadyUsedCouponException.class
    })
    public ProblemDetail handleBadRequest(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    //  인증 실패
    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorized(UnauthorizedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Unauthorized");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    // 권한 실패
    @ExceptionHandler(UnauthorizedUserCouponAccessException.class)
    public ProblemDetail handleForbidden(UnauthorizedUserCouponAccessException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Forbidden");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    //validationexception
    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleCustomValidation(ValidationException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation_Error");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    // @vaild
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation_Error");
        log.error(ex.getMessage(), ex);
        String detail = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        problem.setDetail(detail);
        return problem;
    }

    // 그 외
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther(Exception ex) {
        log.error(ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Internal Server Error");
        return problem;
    }
}

