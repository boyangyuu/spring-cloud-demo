package com.mobileenerlytics.filter.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class SimpleFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 1;
  }

  @Override
  public boolean shouldFilter() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
    log.info(String.format("shouldFilter %s request to %s, query string is %s",
            request.getMethod(),
            request.getRequestURL().toString(),
            request.getQueryString()));
    boolean isAuth = request.getRequestURL().toString().contains("auth");
    return !isAuth;
  }

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();

    log.info(String.format("%s request to %s, query string is %s",
            request.getMethod(),
            request.getRequestURL().toString(),
            request.getQueryString()));


    //todo add
    // parse the user name
    boolean isPro = false;
    if (isPro) return null;

    // get the token
    final String authzHeader = ctx.getZuulRequestHeaders().get(HttpHeaders.AUTHORIZATION);
    String token = null;
    // change the frontend, using header (set token)
    if(authzHeader != null) { // when we do the login , or jinkin uploading
      String[] userPass = authzHeader.split(" ");
      token = userPass[1];
    }

    //https://stackoverflow.com/questions/14413169/which-java-library-provides-base64-encoding-decoding
    // encoded, return it after login(username, pswd => token)
//    Date now = new Date();
//    Date exp = new Date(System.currentTimeMillis() + (1000 * 30)); //  todo 30 seconds
//    long expiredDate = exp.getTime();
//    String strCombined = "username:" + expiredDate + ":mobileenergy";
//    byte[] message = strCombined.getBytes(StandardCharsets.UTF_8);
//    String encoded = Base64.getEncoder().encodeToString(message);
//    System.out.println(encoded);
// => aGVsbG8gd29ybGQ=

    // decoded
    byte[] decoded = Base64.getDecoder().decode(token);
    String strDecoded = new String(decoded, StandardCharsets.UTF_8);
    System.out.println("strDecoded: " + strDecoded);
    String[] splits = strDecoded.split(":");
    String msg = "";

    // check decode
    if (splits.length <= 2 || !splits[2].equals("mobileenergy")) {
        msg = "decode failed, auth failed";
        log.info(msg);
        ctx.unset();
        ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        ctx.setResponseBody(msg);
        return null;
    }

    // check expired
    String userName = splits[0];
    long expiredDate = Long.parseLong(splits[1]);
    if (new Date().after(new Date(expiredDate))) {
        msg = "expired, auth failed";
        log.info(msg);
        ctx.unset();
        ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        ctx.setResponseBody(msg);
        return null;
    }

    //
    log.info("auth successfully");
    return null;
  }

}
