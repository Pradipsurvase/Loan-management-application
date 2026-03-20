package com.bank.loan.eligibility_service.Validator;

import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.enums.Nationality;
import com.bank.loan.eligibility_service.exception.BusinessException;
import com.bank.loan.eligibility_service.exception.InvalidAgeException;
import com.bank.loan.eligibility_service.exception.InvalidPhoneNumber;
import com.bank.loan.eligibility_service.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StudentValidator {
    private static final String PAN_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";
    private static final String EMALI_REGEX ="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public void validate(StudentDetails student) {

        Optional.ofNullable(student)
                .orElseThrow(() -> new BusinessException("Student details cannot be null"));

        validateNationality(student);
        validateAge(student);
        validatePan(student);
        validateAadhaar(student);
        validateMobile(student.getStudentMobile(),student.getNationality());
        validateName(student);
        validateEmail(student);
    }

    private void validateName(StudentDetails student){

        Optional.ofNullable(student.getStudentName())
                .filter(name ->!name.isBlank())
                .filter(name ->name.length() >= 3)
                .orElseThrow(()->new ValidationException("valid student name is required"));

    }
    private void validateAge(StudentDetails student) {

        Optional.ofNullable(student.getAge())
                .filter(age -> age >= 18 && age <= 35)
                .orElseThrow(() ->
                        new InvalidAgeException("Student age must be between 18 and 35"));
    }

    private void validatePan(StudentDetails student) {

        Optional.ofNullable(student.getPanNumber())
                .filter(pan -> !pan.isBlank())
                .filter(pan -> pan.matches(PAN_REGEX))
                .orElseThrow(() ->
                        new ValidationException("Invalid PAN number format"));
    }

    private void validateAadhaar(StudentDetails student) {

        if(student.getNationality() == Nationality.INDIAN){
            Optional.ofNullable(student.getAadhaarNumber())
                    .filter(aadhar ->!aadhar.isBlank())
                    .filter(aadhar ->aadhar.matches("^\\d{12}$"))
                    .orElseThrow(() ->new ValidationException("valid aadhar is required for indian students"));
        }
    }

    private void validateEmail(StudentDetails student){

        Optional.ofNullable(student.getStudentEmail())
                .filter(email ->!email.isBlank())
                .filter(email->email.matches(EMALI_REGEX))
                .orElseThrow(()->new ValidationException("invalid email format"));
    }

    private void validateNationality(StudentDetails student) {

        Optional.ofNullable(student.getNationality())
                .orElseThrow(() ->
                        new ValidationException("Nationality is required"));
    }

    private void validateMobile(String mobile,Nationality nationality) {


        if (mobile == null || mobile.isBlank()) {
            throw new InvalidPhoneNumber("Mobile number is required");
        }

        if (nationality == Nationality.INDIAN) {

            if (!mobile.matches("^[6-9][0-9]{9}$")) {
                throw new InvalidPhoneNumber("Invalid Indian mobile number");
            }

        } else if(nationality == Nationality.NRI){

            if (!mobile.matches("^\\+[1-9][0-9]{7,14}$")) {
                throw new InvalidPhoneNumber("Invalid international mobile number");
            }
        }
    }
}