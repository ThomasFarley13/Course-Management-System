package com.COMP3004.CMS;



import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;


@Controller
public class ErrorMessage implements ErrorController {
    @Getter @Setter private String errorHTML;

    //create the errormessage instance
    private static ErrorMessage errorMessages;

    //constructor if needed
    private ErrorMessage(){
        errorHTML = defaultError();
    };


    //handling for the error pathing
    @RequestMapping(value = "/error")
    public String getInstance(HttpServletRequest request){
        //below is the incoming error message
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            errorMessages = new ErrorMessage();
            //populates the singleton only if needed

            int errorMessage = Integer.parseInt(status.toString());

            if(errorMessage == HttpStatus.BAD_REQUEST.value()){
                errorMessages.setErrorHTML(showError400());
            }
            else if(errorMessage == HttpStatus.UNAUTHORIZED.value()){
                errorMessages.setErrorHTML(showError401());
            }
            else if(errorMessage == HttpStatus.FORBIDDEN.value()){
                errorMessages.setErrorHTML(showError403());
            }
            else if(errorMessage  == HttpStatus.NOT_FOUND.value()) {
                errorMessages.setErrorHTML(showError404());
            }
            else if(errorMessage  == HttpStatus.REQUEST_TIMEOUT.value()) {
                errorMessages.setErrorHTML(showError408());
            }
            else if(errorMessage  == HttpStatus.GONE.value()) {
                errorMessages.setErrorHTML(showError410());
            }
            else if(errorMessage  == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessages.setErrorHTML(showError500());
            }
            else if(errorMessage  == HttpStatus.BAD_GATEWAY.value()) {
                errorMessages.setErrorHTML(showError502());
            }
            else {
                return errorMessages.getErrorHTML();
            }
        }
        return defaultError();

    }

    private String defaultError(){
        return "errors/default-error";
    }

    private String showError400(){
        return "errors/400";
    }

    private String showError401(){
        return "errors/401";
    }

    private String showError403(){
        return "errors/403";
    }

    private String showError404(){
        return "errors/404";
    }

    private String showError408(){
        return "errors/408";
    }

    private String showError410(){
        return "errors/410";
    }

    private String showError500(){
        return "errors/500";
    }

    private String showError502(){
        return "errors/502";
    }

    @Override
    public String getErrorPath() {
        return null;
    }


}
