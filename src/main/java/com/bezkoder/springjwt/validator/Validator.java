package com.bezkoder.springjwt.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class Validator {

    private Pattern namePatter = Pattern.compile("^[a-zA-Z[:punct:]a-zA-Z[:punct:]]*$");
    private Pattern jobPatter = Pattern.compile("^[a-zA-Z]*$");

    public boolean isValidName(String name) {
        return null == name ? false : namePatter.matcher(name).find();
    }
    public boolean isValidJobTittle(String name) {
        return null == name ? false : namePatter.matcher(name).find();
    }
}
