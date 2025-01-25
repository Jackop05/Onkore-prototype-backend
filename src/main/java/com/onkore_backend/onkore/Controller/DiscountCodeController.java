package com.onkore_backend.onkore.Controller;

import com.onkore_backend.onkore.Service.Data.DeleteServices;
import com.onkore_backend.onkore.Service.Data.GetServices;
import com.onkore_backend.onkore.Service.Data.PostServices;
import com.onkore_backend.onkore.Util.JsonFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discount-code")
public class DiscountCodeController {

    @Autowired
    PostServices postServices;

    @Autowired
    DeleteServices deleteServices;

    @Autowired
    GetServices getServices;

    @GetMapping("/check-promo-code")
    public ResponseEntity<String> checkPromoCode(
            @RequestParam("email") String email,
            @RequestParam("promoCode") String promoCode,
            @RequestParam("subjectId") String subjectId) {
        try {
            if(getServices.checkPromoCode(promoCode, email, subjectId)) {
                return ResponseEntity.ok("Promo code is valid");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Promo code is invalid");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

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

    @DeleteMapping("/delete-discount-code")
    public String deleteDiscountCode(@RequestBody Map<String, String> body) {
        try {
            return deleteServices.deleteDiscountCode(body.get("codeName"), body.get("givenCodePassword"), "Haslo_nie_do_zlamania");
        } catch (Exception e) {
            return  e.getMessage();
        }
    }
}
