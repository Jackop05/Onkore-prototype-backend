package com.onkore_backend.onkore.Controller;

import com.onkore_backend.onkore.Service.Data.PostServices;
import com.onkore_backend.onkore.Util.JsonFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discount-code")
public class DiscountCodeController {

    @Autowired
    PostServices postServices;

    @PostMapping("/post-discount-code")
    public String postDiscountCode(@RequestBody Map<String, String> body) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date beginsAt = dateFormat.parse(body.get("beginsAt"));
            Date expiresAt = dateFormat.parse(body.get("endsAt"));

            String emailsString = body.get("emails");
            String subjectsString = body.get("subjects");
            List<String> emails = JsonFormatter.convertStringToList(emailsString, String::trim);
            List<String> subjects = JsonFormatter.convertStringToList(subjectsString, String::trim);

            postServices.postDiscountCode(body.get("codeName"), beginsAt, expiresAt, Integer.parseInt(body.get("discountPercentage")), Integer.parseInt(body.get("discountAnount")), subjects, emails, body.get("givenCodePassword"), "Haslo_nie_do_zlamania");
            return "Discount code posted successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
