package octopus.base.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import octopus.base.enumeration.ResultCode;
import octopus.base.service.ResponseManager;
import octopus.base.utils.MyThreadLocal;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NonUniqueResultException;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    private final ResponseManager responseService;
    private final MessageSourceAccessor messageSourceAccessor;

    /**
     * 권한 Exception 메시지 처리
     *
     * @param e 권한 Exception
     * @return ResponseEntity<?>
     */
    @ExceptionHandler( InternalAuthenticationServiceException.class )
    public ResponseEntity<?> internalAuthServiceException( InternalAuthenticationServiceException e ) {
        log.debug( "e.getBindingResult() :: {}", e.getMessage() );
        return responseService.getErrorResult( HttpStatus.BAD_REQUEST, getMessage( "권한 없음." ) );
    }

    /**
     * <pre>
     *     @Valid 에 의해 발생되는 Exception 메시지 처리
     * </pre>
     *
     * @param e Method Argument 오류
     * @return ResponseEntity<?>
     */
    @ExceptionHandler( MethodArgumentNotValidException.class )
    public ResponseEntity<?> methodArgumentNotValidException( MethodArgumentNotValidException e ) {
        log.debug( "e.getBindingResult() :: {}", e.getBindingResult() );

        Map<String, String> errMsg = makeErrorResponse( e.getBindingResult() );
        return responseService.getErrorResult( HttpStatus.BAD_REQUEST, getMessage( "argumentException" ) + " :: [" + errMsg.get( "errorField" ) + "] :: " + errMsg.get( "description" ) );

    }

    /**
     * <pre>
     * null이 아닌 인자의 값이 잘못되었을 때
     * </pre>
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler( IllegalArgumentException.class )
    protected ResponseEntity<?> argumentException( HttpServletRequest request, IllegalArgumentException e ) {

        log.debug( "[ExceptionAdvice.argumentException] :: {}", e.toString() );

        return responseService.getErrorResult( HttpStatus.BAD_REQUEST, getMessage( "argumentException" ) );
    }

    /**
     * <pre>
     * 객체 상태가 메서드 호출을 처리하기에 적절치 않을 때
     * </pre>
     *
     * @param e
     * @return
     */
    @ExceptionHandler( IllegalStateException.class )
    protected ResponseEntity<?> illegalStateException( IllegalStateException e ) {

        log.debug( "[ExceptionAdvice.illegalStateException] :: {}", e.toString() );

        return responseService.getErrorResult( HttpStatus.BAD_REQUEST, getMessage( "argumentException" ) );
    }

    /**
     * <pre>
     *    사용자 정보 조회 시 오류 발생.
     *    String Security Authentication 오류
     * </pre>
     *
     * @param bce String Security Authentication 오류
     * @return ResponseEntity
     */
    @ExceptionHandler( BadCredentialsException.class )
    protected ResponseEntity<?> badCredentialsException( BadCredentialsException bce ) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        log.info( "[badCredentialsException] result :: {}", bce.getMessage() );
        return responseService.getErrorResult( HttpStatus.BAD_REQUEST, getMessage( "userNotFound" ) );
    }

    @ExceptionHandler( ExUserNotFoundException.class )
    protected ResponseEntity<?> userNotFoundException( HttpServletRequest request, ExUserNotFoundException e ) {
        String message = "[ExUserNotFoundException] " + e.getMessage();
        printStackTrace(message);

        return responseService.getErrorResult( HttpStatus.BAD_REQUEST, e.getMessage() );
    }

    @ExceptionHandler( ExDuplicatedException.class )
    protected ResponseEntity<?> duplicatedException( HttpServletRequest request, ExDuplicatedException e ) {
        String message = "[ExDuplicatedException] " + e.getMessage();
        printStackTrace(message);

        return responseService.getErrorResult( HttpStatus.BAD_REQUEST, e.getMessage() );
    }

    @ExceptionHandler( NonUniqueResultException.class )
    protected ResponseEntity<?> nonUniqueResultException( HttpServletRequest request, NonUniqueResultException nure ) {

        return responseService.getErrorResult( HttpStatus.BAD_REQUEST, getMessage( "emailSignupFailed" ) );
    }

    @ExceptionHandler( DataAccessException.class )
    protected ResponseEntity<?> dataAccessException( HttpServletRequest request, DataAccessException dae ) {

        log.info( "[ExceptionAdvice >> dataAccessException] result :: {}", dae.getMessage() );

        return getResponseEntity( dae );
    }

    /*
        @ExceptionHandler( DataAccessException.class )
        @ResponseStatus( HttpStatus.INTERNAL_SERVER_ERROR )
        protected CommonResult dataAccessException( HttpServletRequest request, DataAccessException dae ) {
            Map<String, Object> result = getErrMsg( dae );

            log.info( "[ExceptionAdvice >> dataAccessException] result :: {}", result );

            return responseService.getFailResult( Integer.parseInt( String.valueOf( result.get( "errCode" ) ) ), result.get( "errMsg" ).toString() );
        }
    */
    @ExceptionHandler( Exception.class )
    protected ResponseEntity<?> defaultException( HttpServletRequest request, Exception e ) {
        log.info( "[ExceptionAdvice >> defaultException] getMessage :: {}", e.getMessage() );
        if( e.getMessage() == null ) {
            e.printStackTrace();
        }
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getErrorResult( HttpStatus.BAD_REQUEST, getMessage( "unKnown" ) );
    }

    // code정보에 해당하는 메시지를 조회합니다.
    private String getMessage( String code ) {
        return getMessage( code, null );
    }

    // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
    private String getMessage( String code, Object[] args ) {
        return messageSourceAccessor.getMessage( code, args, LocaleContextHolder.getLocale() );
    }

    /**
     * Error Message 처리
     *
     * @param ex DataAccessException
     * @return ResponseEntity<?>
     */
    private ResponseEntity<?> getResponseEntity( DataAccessException ex ) {
        log.info( "[getResponseEntity >> getErrMsg] getMessage :: {}", ex.getMessage() );

        if( ex instanceof DataIntegrityViolationException ) {
            // 고유성 제한 위반과 같은 데이터 삽입 또는 업데이트시 무결성 위반
            // "등록된 데이터가 컬럼의 속성과 다릅니다. (길이, 속성, 필수입력항목 등..)"

            return responseService.getErrorResult( HttpStatus.BAD_REQUEST, getMessage( "dataIntegrityViolationException" ) );
        } else {

            return responseService.getErrorResult( HttpStatus.BAD_REQUEST, ex.getMessage() );
        }
    }

    /**
     * Error Message 처리
     *
     * @param ex DataAccessException
     * @return Map<String, Object>
     */
    private Map<String, Object> getErrMsg( DataAccessException ex ) {
        int errCode = 0;
        String errMsg = "";

        // log.info("[DataAccessException >> getErrMsg] getMessage :: {}", ex.get);
        log.info( "[DataAccessException >> getErrMsg] getMessage :: {}", ex.getMessage() );

        if( ex instanceof BadSqlGrammarException ) {
            SQLException se = ( (BadSqlGrammarException) ex ).getSQLException();

            errCode = se.getErrorCode();
            errMsg = se.getMessage();
        } else if( ex instanceof InvalidResultSetAccessException ) {
            SQLException se = ( (InvalidResultSetAccessException) ex ).getSQLException();

            errCode = se.getErrorCode();
            errMsg = se.getMessage();
        } else if( ex instanceof DuplicateKeyException ) {
            // 고유성 제한 위반과 같은 데이터 삽입 또는 업데이트시 무결성 위반
            errCode = ResultCode.ERROR.getCode();
            errMsg = getMessage( "duplicateKeyException" ); // "중복된 데이터가 존재합니다.";
        } else if( ex instanceof DataIntegrityViolationException ) {
            // 고유성 제한 위반과 같은 데이터 삽입 또는 업데이트시 무결성 위반
            // "등록된 데이터가 컬럼의 속성과 다릅니다. (길이, 속성, 필수입력항목 등..)";
            errCode = ResultCode.ERROR.getCode();
            //errMsg  = getMessage("dataIntegrityViolationException"); // "등록된 데이터가 컬럼의 속성과 다릅니다. (길이, 속성, 필수입력항목 등..)";
            errMsg = getMessage( "duplicateKeyException" ); // "중복된 데이터가 존재합니다.";
        } else if( ex instanceof DataAccessResourceFailureException ) {
            // 데이터 액세스 리소스가 완전히 실패했습니다 (예 : 데이터베이스에 연결할 수 없음)
            errCode = ResultCode.ERROR.getCode();
            errMsg = getMessage( "dataAccessResourceFailureException" ); // "데이터베이스 연결오류";
        } else if( ex instanceof CannotAcquireLockException ) {

        } else if( ex instanceof DeadlockLoserDataAccessException ) {
            // 교착 상태로 인해 현재 작업이 실패했습니다.
            errCode = ResultCode.ERROR.getCode();
            errMsg = getMessage( "deadlockLoserDataAccessException" ); // "교착 상태로 인한 현재 작업 실패";
        } else if( ex instanceof CannotSerializeTransactionException ) {
            errCode = ResultCode.ERROR.getCode();
            errMsg = "직렬화 모드에서 트랜잭션을 완료 할 수 없음";
        } else {
            errCode = ResultCode.ERROR.getCode();
            errMsg = ex.getMessage();
        }

        Map<String, Object> map = new HashMap<>();
        map.put( "errCode", errCode );
        map.put( "errMsg", errMsg );

        return map;
    }

    private Map<String, String> makeErrorResponse( BindingResult bindingResult ) {
        String description = "";
        String defaultMsg = "";
        String errorField = "";

        Map<String, String> errMap = new HashMap<>();

        // 에러가 있다면
        if( bindingResult.hasErrors() ) {
            // DTO에 설정한 meaasge값을 가져온다
            defaultMsg = bindingResult.getFieldError().getDefaultMessage();
            errorField = bindingResult.getFieldError().getField();
            // DTO에 유효성체크를 걸어놓은 어노테이션명을 가져온다.
            String bindResultCode = bindingResult.getFieldError().getCode();

            log.debug( "defaultMsg :: {}", defaultMsg );
            log.debug( "errorField :: {}", errorField );
            log.debug( "bindResultCode :: {}", bindResultCode );

            errMap.put( "errorField", errorField );


            switch( bindResultCode ) {
                case "NotNull":
                    description = getMessage( "valid.notnull" );
                    break;
                case "Min":
                    description = getMessage( "valid.min" );
                    break;
                case "Max":
                    description = getMessage( "valid.max" );
                    break;
                case "Size":
                    description = defaultMsg;
                    break;
                case "Email":
                    description = getMessage( "valid.email" );
                    break;
                case "Pattern":
                    description = defaultMsg;
                    break;
            }
        }

        errMap.put( "description", description );

        return errMap;
    }

    /**
     * <pre>
     *     ThreadLocal 의 Stacking 로그를 출력한다.
     * </pre>
     * @param message 정재된 오류메시지
     */
    private void printStackTrace(String message) {
        MyThreadLocal.setTrackingLog( "[Exception] " + this.getClass().getName() + " >> " + message);
        MyThreadLocal.printStackLog();
    }
}