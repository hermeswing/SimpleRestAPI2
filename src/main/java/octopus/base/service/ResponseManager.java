package octopus.base.service;

import octopus.base.enumeration.ResultCode;
import octopus.base.model.CommonResult;
import octopus.base.model.ListResult;
import octopus.base.model.SingleResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 *     MessageConfig 가 추가되어야 한다.
 * </pre>
 */
@Component
public class ResponseManager {

    /**
     * 단건 결과를 처리하는 메소드
     *
     * @return
     */
    public <T> ResponseEntity getSingleResult( T data ) {
        SingleResult<T> result = new SingleResult<>();
        result.setData( data );
        setSuccessResult( result );

        return ResponseEntity.ok().body( result );
    }

    // 다중건 결과를 처리하는 메소드
    public <T> ResponseEntity getListResult( List<T> list ) {
        ListResult<T> result = new ListResult<>();
        result.setList( list );
        setSuccessResult( result );

        return ResponseEntity.ok().body( result );
    }

    // 성공 결과만 처리하는 메소드
    public CommonResult getSuccessResult() {
        CommonResult result = new CommonResult();
        result.setSuccess( true );
        result.setCode( ResultCode.SUCCESS.getCode() );
        result.setMsg( "정상처리되었습니다." );

        return result;
    }

    /**
     * 성공 결과를 생성
     */
    public void setSuccessResult( CommonResult result ) {
        result.setSuccess( true );
        result.setCode( ResultCode.SUCCESS.getCode() );
        result.setMsg( "정상처리되었습니다." );
    }

    // 성공 결과만 처리하는 메소드
    public ResponseEntity getSuccessResult( String message ) {
        CommonResult result = new CommonResult();
        result.setSuccess( true );
        result.setCode( ResultCode.SUCCESS.getCode() );
        result.setMsg( message );

        return ResponseEntity.ok().body( result );
    }

    public ResponseEntity getSuccessResult( HttpStatus status, String message ) {
        CommonResult result = new CommonResult();
        result.setSuccess( true );
        result.setCode( ResultCode.SUCCESS.getCode() );
        result.setMsg( message );
        result.setStatus( status );

        return ResponseEntity.ok().body( result );
    }

    /**
     * 실패 결과만 처리하는 메소드
     *
     * @return
     */
    public ResponseEntity getErrorResult( HttpStatus status, String msg ) {
        CommonResult result = new CommonResult();
        result.setSuccess( false );
        result.setMsg( msg );
        result.setCode( ResultCode.ERROR.getCode() );
        result.setStatus( status );

        return ResponseEntity.badRequest().body( result );
    }

    // 단일건 결과를 처리하는 메소드
    //public <T> SingleResult<T> getSingleResult( T data ) {
    //    SingleResult<T> result = new SingleResult<>();
    //    result.setData( data );
    //    setSuccessResult( result );
    //
    //    return result;
    //}

    // 다중건 결과를 처리하는 메소드
    //public <T> ListResult<T> getListResult( List<T> list ) {
    //    ListResult<T> result = new ListResult<>();
    //    result.setList( list );
    //    setSuccessResult( result );
    //
    //    return result;
    //}

    // HATEOAS를 적용한 다중건 결과를 처리하는 메소드
    // public <T> ListResult<T> getListResult(CollectionModel<T> collection) {
    // ListResult<T> result = new ListResult<>();
    // result.setCollection(collection);
    // setSuccessResult(result);
    // return result;


    // 실패 결과만 처리하는 메소드
    // public ResponseEntity getErrorResult( int code, String msg ) {
    //     CommonResult result = new CommonResult();
    //     result.setSuccess( false );
    //     result.setCode( code );
    //     result.setMsg( msg );
    //
    //     return ResponseEntity.internalServerError().body( result );
    // }
}