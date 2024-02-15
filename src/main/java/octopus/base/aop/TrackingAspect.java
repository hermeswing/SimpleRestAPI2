package octopus.base.aop;


import lombok.extern.slf4j.Slf4j;
import octopus.base.WebConst;
import octopus.base.utils.MyThreadLocal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * MDC(Mapped Diagnostic Context)는 Logging에서 사용되는 패턴으로, 로그 메시지에 특정 데이터를 매핑하여 출력하는데 사용
 * Spring AOP와 MDC를 함께 사용하여 각 호출되는 메서드에 대한 정보를 로그에 추가할 수 있습니다.
 *
 * 사용의 예)
 * 로그 출력 시 MDC의 데이터를 사용하도록 logback.xml 또는 log4j2.xml 설정 파일을 구성
 * {@code MDC.put( "ThreadId", threadId ); }
 *
 * &lt;property name="LOG_PATTERN" value="%d{yy-MM-dd HH:mm:SSS} [%thread] %-5level %30logger.%method [ %line line ] [%X{ThreadId}] - %msg%n" /&gt;
 *
 * 다음은 각 부분의 의미입니다:
 * - %d{yy-MM-dd HH:mm:SSS}: 날짜 및 시간 포맷
 * - [%thread]: 스레드 이름
 * - %-5level: 로그 레벨
 * - %30logger.%method: 로거 이름과 메소드 이름
 * - [ %line line ]: 라인 번호
 * - [%X{ThreadId}]: 스레드 ID
 * - %msg: 로그 메시지
 * - %n: 줄 바꿈
 *
 * 위 설정에서 %X{ThreadId}는 MDC에서 className 키의 값을 로그 메시지에 추가하도록 합니다.
 * 따라서 각 메서드 호출 시 클래스 이름이 로그에 출력됩니다.
 * </pre>
 */
@Slf4j
@Aspect
@Component
public class TrackingAspect {

    @Before( "execution(* octopus.*.controller.*.*(..))" )
    public void loggingBefore( JoinPoint joinPoint ) {
        //String className = joinPoint.getTarget().getClass().getSimpleName();

        // 호출된 메서드의 클래스와 메서드 이름을 가져오기
        //String className = joinPoint.getSignature().getDeclaringTypeName();
        //String methodName = joinPoint.getSignature().getName();

        String threadId = "ThreadId-" + Thread.currentThread().getId();
        MDC.put( WebConst.THREAD_ID, threadId );
        // ThreadLocal 을 초기화 한다.
        MyThreadLocal.clearContext();
        MyThreadLocal.setContext( WebConst.THREAD_ID, threadId );

        MyThreadLocal.setContext( WebConst.START_TIME, System.currentTimeMillis() );

        //MyThreadLocal.setTrackingLog( joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() );
        MyThreadLocal.setTrackingLog( "[Controller Call] " + joinPoint.getSignature().toLongString() );

        //log.debug( "Transaction started for method >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " );
        //log.debug( "Target().getClass(): {}", joinPoint.getTarget().getClass() );
        //log.debug( "Target().toString(): {}", joinPoint.getTarget().toString() );
        //log.debug( "Signature().toShortString() :: {}", joinPoint.getSignature().toShortString() );
        //log.debug( "Signature().toLongString() :: {}", joinPoint.getSignature().toLongString() );
        //log.debug( "Signature().getName() :: {}", joinPoint.getSignature().getName() );
        //log.debug( "Signature().getClass() :: {}", joinPoint.getSignature().getClass() );
        //log.debug( "Signature().getDeclaringType() :: {}", joinPoint.getSignature().getDeclaringType() );
        //log.debug( "Signature().getDeclaringTypeName() :: {}", joinPoint.getSignature().getDeclaringTypeName() );
        //log.debug( "Signature().getModifiers() :: {}", joinPoint.getSignature().getModifiers() );
        //log.debug( "Transaction started for method <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< " );
    }

    @Around( "execution(* octopus.*.controller.*.*(..))" )
    public Object loggingControllerAround( ProceedingJoinPoint joinPoint ) throws Throwable {
        try {
            return joinPoint.proceed();
        } finally {
            MyThreadLocal.setTrackingLog( "[Controller Release] " + joinPoint.getSignature().toLongString() );

            long duration = System.currentTimeMillis() - (Long) MyThreadLocal.getContext( WebConst.START_TIME );
            MyThreadLocal.setTrackingLog( "실행시간 :: " + duration + " ms" );
            //log.info( "{}.{}({}) 실행 시간 : {} ms", joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), Arrays.toString( joinPoint.getArgs() ), duration );

            MyThreadLocal.printStackLog();

            //log.info( "Signature().getName() :: {}", joinPoint.getSignature().getName() );
            //log.debug( "Transaction completed for method: {}", joinPoint.getSignature().toLongString() + " >> Duration: " + duration + " ms" );

            //MyThreadLocal.clearContext();
            MDC.remove( WebConst.THREAD_ID );
        }
    }

    @Before( "execution(* octopus.*.service.*.*(..))" )
    public void loggingServiceBefore( JoinPoint joinPoint ) {
        MyThreadLocal.setTrackingLog( "[Service Call] " + joinPoint.getSignature().toLongString() );
    }

    @After( "execution(* octopus.*.service.*.*(..))" )
    public void loggingServiceAfter( JoinPoint joinPoint ) {
        //MyThreadLocal.setTrackingLog( joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() );
        MyThreadLocal.setTrackingLog( "[Service Release] " + joinPoint.getSignature().toLongString() );
    }

    @Before( "execution(* octopus.*.repository.*.*(..))" )
    public void loggingRepositoryBefore( JoinPoint joinPoint ) {
        MyThreadLocal.setTrackingLog( "[Repository Call] " + joinPoint.getSignature().toLongString() );
    }

    @After( "execution(* octopus.*.repository.*.*(..))" )
    public void loggingRepositoryAfter( JoinPoint joinPoint ) {
        //MyThreadLocal.setTrackingLog( joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() );
        MyThreadLocal.setTrackingLog( "[Repository Release] " + joinPoint.getSignature().toLongString() );
    }
}
