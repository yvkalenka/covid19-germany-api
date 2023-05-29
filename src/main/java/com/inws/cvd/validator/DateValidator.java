package com.inws.cvd.validator;

import jakarta.validation.ValidationException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateValidator {

    private static final LocalDate DAY_COVID_BEGAN = LocalDate.of(2020, 1, 1);

    @SneakyThrows
    public void validateDates(LocalDate fromDate, LocalDate toDate) throws ValidationException {
        if (fromDate.isAfter(toDate)) {
            throw new ValidationException("Param 'fromDate' must have value less than 'toDate' param.");
        }

        var tomorrow = LocalDate.now().plusDays(1);
        if (fromDate.isAfter(tomorrow) || toDate.isAfter(tomorrow)) {
            throw new ValidationException("Api does not have available statistics for time range in future.");
        }

        // Initially remote api does not provide the info about recent 2 days.
        // Validate the case to distinguish cases when there is no actual data.
        var maximumDate = LocalDate.now().minusDays(2);
        if (toDate.isAfter(maximumDate)) {
            throw new ValidationException("Api does not have available statistics for recent 48 hours. Set 'toDate' param to earlier date.");
        }

        if (fromDate.isBefore(DAY_COVID_BEGAN)) {
            throw new ValidationException(String.format("Param 'fromDate' must be equal or after covid19 began on %s.", DAY_COVID_BEGAN));
        }
    }
}
